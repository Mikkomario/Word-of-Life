package vf.word.controller.parse

import utopia.flow.datastructure.mutable.{PointerWithEvents, ResettableLazy}
import utopia.flow.parse.{CsvReader, Regex}
import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.ActionBuffer
import utopia.flow.util.StringExtensions._
import utopia.vault.database.Connection
import vf.word.database.access.many.text.DbWritings
import vf.word.database.model.address.{BookCodeModel, ChapterModel, VerseModel}
import vf.word.database.model.text.{SentenceModel, SentencePartModel, SentenceSegmentModel, WordAssignmentModel, WordModel}
import vf.word.model.cached.Location
import vf.word.model.enumeration.Capitalization
import vf.word.model.enumeration.Capitalization.AlwaysCapitalize
import vf.word.model.partial.address.{BookCodeData, VerseData}
import vf.word.model.partial.text.{SentencePartData, SentenceSegmentData, WordAssignmentData}
import vf.word.model.stored.address.Chapter
import vf.word.model.stored.text.Word

import java.nio.file.Path
import scala.collection.immutable.VectorBuilder

/**
 * A parser that reads kjvdat.txt (King James Version data) file and inserts its contents to the DB
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 */
object KjvDatParser
{
	// ATTRIBUTES   ---------------------------
	
	private val sentenceSeparators = Set('.', '?', '!')
	private val sentencePartSeparators = Set(';', ':')
	// private val sentenceSegmentSeparators = Set(',')
	private val sentenceAndPartSeparators = sentenceSeparators ++ sentencePartSeparators
	private val allSeparators = sentenceAndPartSeparators + ','
	
	private val sentenceSeparatorRegex = Regex.anyOf(sentenceSeparators.mkString(""))
	private val partSeparatorRegex = Regex.anyOf(sentencePartSeparators.mkString(""))
	private val segmentSeparatorRegex = Regex(",")
	
	private val parenthesisFillerRegex = (!Regex.escape(')')).zeroOrMoreTimes
	private val sentenceEndingParenthesisRegex = Regex.escape('(') + parenthesisFillerRegex +
		sentenceSeparatorRegex + parenthesisFillerRegex + Regex.escape(')')
	private val partEndingParenthesisRegex = Regex.escape('(') + parenthesisFillerRegex + partSeparatorRegex +
		parenthesisFillerRegex + Regex.escape(')')
	
	
	// OTHER    -------------------------------
	
	/**
	 * Reads the King James Bible data from a text file
	 * @param path Path to the text file
	 * @param connection DB connection (implicit)
	 * @return Success or failure
	 */
	def apply(path: Path)(implicit connection: Connection) =
	{
		// Needs to save chapter data between insert iterations in order to not insert a chapter twice
		val words = new WordCollector()
		var lastChapter = Chapter(-1, -1, -1)
		
		// Sentences are inserted in bulks
		val sentenceBuffer = ActionBuffer[PreparedSentence](100) { sentences =>
			// Starts by inserting the sentences (top level) first
			val insertedSentences = SentenceModel.insert(sentences.map { s => s.writingId -> s.orderIndex })
			
			// Next inserts the sentence parts
			// Sentence + part index + segments
			val sentencePartData = insertedSentences.zip(sentences).flatMap { case (sentence, data) =>
				data.parts.zipWithIndex.map { case (segments, partIndex) => (sentence, partIndex, segments) }
			}
			val insertedSentenceParts = SentencePartModel.insert(
				sentencePartData.map { case (sentence, partIndex, _) => SentencePartData(sentence.id, partIndex) })
			
			// Next moves to the individual sentence segments
			// Writing id + Part id + segment index + text + parenthesis + address (if assigned)
			val segmentData = insertedSentenceParts.zip(sentencePartData)
				.flatMap { case (sentencePart, (sentence, _, segments)) =>
					segments.zipWithIndex.map { case (segment, index) =>
						(sentence.contextId, sentencePart.id, index, segment.text, segment.parenthesis, segment.address)
					}
				}
			val insertedSegments = SentenceSegmentModel.insert(
				segmentData.map { case (_, partId, orderIndex, text, parenthesis, _) =>
					// In some cases, has to add a separator character to the parenthesis
					// TODO: Try removing this part, whether all the separators have already been added
					val lastChar =
					{
						val default = text.last
						if (parenthesis)
						{
							if (allSeparators.contains(default)) default else ','
						}
						else
							default
					}
					SentenceSegmentData(partId, orderIndex, lastChar, parenthesis)
				})
			
			// Inserts chapter and verse markers
			// Writing id + verse address + segment id
			val verseData = insertedSegments.zip(segmentData)
				.flatMap { case (segment, (writingId, _, _, _, _, address)) =>
					address.map { address => (writingId, address, segment.id) }
				}
			// Doesn't insert the last chapter again
			val listedChapters = verseData.map { case (writingId, address, _) =>
				writingId -> address.chapterNumber }.toSet
			val insertedChapters = ChapterModel.insert(
				(listedChapters - (lastChapter.bookId -> lastChapter.number)).toVector)
			// Writing id -> chapter number -> chapter id
			val chapterIds = (insertedChapters :+ lastChapter).groupBy { _.bookId }.view.mapValues { chapters =>
				chapters.map { chapter => chapter.number -> chapter.id }.toMap
			}.toMap
			VerseModel.insert(verseData.map { case (writingId, address, segmentId) =>
				VerseData(chapterIds(writingId)(address.chapterNumber), address.verseNumber, segmentId)
			})
			
			// Next inserts the new words
			words.record(insertedSegments.zip(segmentData)
				.map { case (segment, (_, _, _, text, _, _)) => segment.id -> text })
			// Records the last chapter for the next iteration
			if (insertedChapters.nonEmpty)
				lastChapter = insertedChapters.maxBy { c => c.bookId * 1000 + c.number }
		}
		
		CsvReader.iterateRawRowsIn(path, "\\|") { rawLinesIter =>
			val linesIter = rawLinesIter.map(VerseLine.fromLine)
			
			var currentWritingId = -1
			var currentBookCode = ""
			val sentenceIndexIteratorPointer = ResettableLazy { Iterator.from(0) }
			val sentenceBuilder = new SentenceBuilder()
			
			def nextSentenceIndex() = sentenceIndexIteratorPointer.value.next()
			
			while (linesIter.hasNext)
			{
				val nextLine = linesIter.next()
				
				// Checks whether book needs to be changed
				val nextBookCode = nextLine.address.bookCode
				if (nextBookCode != currentBookCode)
				{
					// Adds the last sentence of the book, if one was left partial from the previous line
					sentenceBuffer ++= sentenceBuilder.closeAndTake()
						.map { parts => PreparedSentence(currentWritingId, nextSentenceIndex(), parts) }
					// TODO: Remove test print?
					println(s"Starting book: $nextBookCode")
					
					// Starts a new book
					currentWritingId = DbWritings.insert()
					currentBookCode = nextBookCode
					BookCodeModel.insert(BookCodeData(nextBookCode, currentWritingId))
					// Resets sentence order index counter
					sentenceIndexIteratorPointer.reset()
				}
				
				// Adds the line to the builder and updates the buffer
				sentenceBuilder += nextLine
				sentenceBuffer ++= sentenceBuilder.takeSentences()
					.map { parts => PreparedSentence(currentWritingId, nextSentenceIndex(), parts) }
			}
			
			// Adds the last partial sentence if there was one and inserts the rest of the sentences
			sentenceBuffer ++= sentenceBuilder.closeAndTake().map { parts =>
				PreparedSentence(currentWritingId, nextSentenceIndex(), parts) }
			sentenceBuffer.flush()
			// Shows which words were left ambiguous
			// TODO: Remove test print?
			println(s"Following words were left ambiguous: [${
				words.ambiguousWords.toVector.map { _.value }.sorted.mkString(", ")}]")
		}
	}
	
	private def separateParenthesisFrom(str: String, regex: Regex = Regex.parenthesis) =
	{
		val matcher = regex.pattern.matcher(str)
		val builder = new VectorBuilder[(String, Boolean)]()
		var lastEnd = 0
		while (matcher.find())
		{
			val start = matcher.start()
			val end = matcher.end()
			if (start > lastEnd)
				builder += (str.substring(lastEnd, start).trim -> false)
			// Doesn't include the parenthesis themselves in the strings
			builder += (str.substring(start + 1, end - 1).trim -> true)
			lastEnd = end
		}
		if (str.length > lastEnd)
			builder += (str.substring(lastEnd).trim -> false)
		
		builder.result()
	}
	
	
	// NESTED   ------------------------------
	
	private case class PreparedSentence(writingId: Int, orderIndex: Int, parts: Vector[Vector[PreparedSegment]])
	
	private case class PreparedSegment(text: String, address: Option[Address] = None, parenthesis: Boolean = false)
	{
		/**
		 * Appends a character to this segment
		 * @param char Character to append
		 * @return An appended copy of this segment
		 */
		def +(char: Char) = copy(text = s"$text$char")
	}
	
	private object VerseLine
	{
		// Each line ends with ~
		def fromLine(line: Vector[String]) = VerseLine(Address(line.head, line(1).toInt, line(2).toInt),
			line(3).untilLast("~").stripControlCharacters.trim)
	}
	
	private case class VerseLine(address: Address, text: String)
	
	private case class Address(bookCode: String, chapterNumber: Int, verseNumber: Int)
	{
		override def toString = s"$bookCode:$chapterNumber:$verseNumber"
	}
	
	private class SentenceBuilder
	{
		// ATTRIBUTES   ------------------------
		
		private var completeSentences = Vector[Vector[Vector[PreparedSegment]]]()
		private var completeParts = Vector[Vector[PreparedSegment]]()
		private var completeSegments = Vector[PreparedSegment]()
		private var incompleteSegment: Option[PreparedSegment] = None
		
		// Left as true when the previous line opened but didn't close a parenthesis
		private var parenthesisOpenFlag = false
		
		
		// OTHER    ----------------------------
		
		def +=(line: VerseLine) =
		{
			// Separates the parenthesis parts of the line
			val parenthesisParts = separateParenthesisFrom(line.text, sentenceEndingParenthesisRegex)
			// Adds the parts individually
			val (firstPart, firstParenthesis) = parenthesisParts.head
			// Checks whether an existing parenthesis segment ends within the first part
			if (parenthesisOpenFlag && !firstParenthesis && firstPart.contains(')'))
			{
				parenthesisOpenFlag = false
				
				// If so, handles the first part in two parts
				val (parenthesisPart, outsideParenthesisPart) = firstPart.splitAtFirst(")")
				val trimmedParenthesisPart = parenthesisPart.trim
				val trimmedOutsideParenthesisPart = outsideParenthesisPart.trim
				addSentenceParenthesisPart(trimmedParenthesisPart, Some(line.address), parenthesis = true)
				if (trimmedOutsideParenthesisPart.nonEmpty)
				{
					// In case this is the last part, checks whether a parenthesis section starts but doesn't end
					// there
					if (parenthesisParts.size == 1)
						addLastSentenceParenthesisPart(trimmedOutsideParenthesisPart, None, parenthesis = false)
					else
						addSentenceParenthesisPart(trimmedOutsideParenthesisPart, None, parenthesis = false)
				}
			}
			else
			{
				val actualFirstParenthesis = firstParenthesis || parenthesisOpenFlag
				if (parenthesisParts.size == 1)
					addLastSentenceParenthesisPart(firstPart, Some(line.address), actualFirstParenthesis)
				else
					addSentenceParenthesisPart(firstPart, Some(line.address), firstParenthesis || parenthesisOpenFlag)
			}
			if (parenthesisParts.size > 1)
			{
				// Handles the last part separately, because it may start a parenthesis section and not end it
				parenthesisParts.tail.dropRight(1).foreach { case (part, parenthesis) =>
					addSentenceParenthesisPart(part, None, parenthesis)
				}
				val (lastPart, lastParenthesis) = parenthesisParts.last
				addLastSentenceParenthesisPart(lastPart, None, lastParenthesis)
			}
		}
		
		// Retrieves completed sentences and prepares for the next ones
		def takeSentences() =
		{
			val result = completeSentences
			completeSentences = Vector()
			result
		}
		
		// Ends the book etc. and returns the last sentences
		def closeAndTake() =
		{
			closeSentence()
			takeSentences()
		}
		
		// Closes currently open sentence so that a new one can be started
		private def closeSentence() =
		{
			// Creates a new sentence from the incomplete parts + segments
			val lastSegments = incompleteSegment match
			{
				case Some(incomplete) => completeSegments :+ (incomplete + '.')
				case None => completeSegments
			}
			val lastParts =
			{
				if (lastSegments.isEmpty)
					completeParts
				else
					completeParts :+ lastSegments
			}
			if (lastParts.nonEmpty)
				completeSentences :+= lastParts
			
			completeParts = Vector()
			completeSegments = Vector()
			incompleteSegment = None
		}
		
		private def addLastSentenceParenthesisPart(text: String, address: Option[Address], parenthesis: Boolean) =
		{
			// Checks whether the part will leave a parenthesis open
			text.optionLastIndexOf("(") match
			{
				case Some(lastParenthesisStart) =>
					val remaining = text.substring(lastParenthesisStart)
					// Case: Parenthesis is not left open => Default treatment
					if (remaining.contains(')'))
						addSentenceParenthesisPart(text, None, parenthesis)
					// Case: Parenthesis is left open => treats the parenthesis part as a separate whole
					else
					{
						val before = text.substring(0, lastParenthesisStart).trim
						val after = remaining.drop(1).trim
						if (before.nonEmpty)
							addSentenceParenthesisPart(before, address, parenthesis = false)
						addSentenceParenthesisPart(after, if (before.isEmpty) address else None, parenthesis = true)
						parenthesisOpenFlag = true
					}
				// Case: No parenthesis => default handling
				case None => addSentenceParenthesisPart(text, None, parenthesis)
			}
		}
		
		private def addSentenceParenthesisPart(text: String, address: Option[Address], parenthesis: Boolean) =
		{
			// Checks how many sentences the text contains
			val sentences = sentenceSeparatorRegex.divide(text).map { _.trim }.filterNot { _.isEmpty }
			// Processes the sentences. However, the last sentence may not always complete.
			addSentence(sentences.head, address, parenthesis,
				sentences.size > 1 || sentenceSeparators.contains(sentences.head.last))
			if (sentences.size > 1)
			{
				sentences.tail.dropRight(1).foreach { addSentence(_, None, parenthesis, complete = true) }
				addSentence(sentences.last, None, parenthesis, sentenceSeparators.contains(sentences.last.last))
			}
		}
		
		private def addSentence(sentence: String, address: Option[Address], parenthesis: Boolean, complete: Boolean) =
		{
			// If the sentence is within parenthesis, can't combine it with the previously open data
			if (parenthesis)
				closeSentence()
			
			// Checks whether some parts are wrapped in parenthesis and separates those
			// Text + parenthesis + completes
			val parenthesisParts =
			{
				if (parenthesis)
					Vector((sentence, parenthesis, complete))
				else
					separateParenthesisFrom(sentence, partEndingParenthesisRegex).map { case (part, parenthesis) =>
						(part, parenthesis, sentenceAndPartSeparators.contains(part.last)) }
			}
			// Separates the parts based on the normal regex
			// Text + parenthesis + completes
			val parts = parenthesisParts.flatMap { case (text, parenthesis, completes) =>
				val parts = partSeparatorRegex.divide(text).map { _.trim }
				parts.dropRight(1).map { part => (part, parenthesis, true) } :+ (parts.last, parenthesis, completes)
			}
			
			// The address and previous data may affect the first part but not the rest
			val (firstPart, firstParenthesis, firstCompletes) = parts.head
			addPart(firstPart, address, firstParenthesis, firstCompletes)
			// Also, the last part may be left incomplete
			if (parts.size > 1)
				parts.tail.foreach { case (part, parenthesis, completes) => addPart(part, None, parenthesis, completes) }
			
			// If this sentence is now completed, closes it
			if (complete)
			{
				completeSentences :+= completeParts
				completeParts = Vector()
			}
		}
		
		private def addPart(part: String, address: Option[Address], parenthesis: Boolean, complete: Boolean) =
		{
			// Checks whether there are some segments in parenthesis
			val parenthesisSegments =
			{
				if (parenthesis)
					Vector(part -> parenthesis)
				else
					separateParenthesisFrom(part)
			}
			// Nex splits based on segment separator (removes ")," -cases)
			// All segments
			val segments = parenthesisSegments.flatMap { case (text, parenthesis) =>
				segmentSeparatorRegex.divide(text).map { _.trim }
					.filterNot { t => t.isEmpty || (t.length == 1 && allSeparators.contains(t.head)) }
					.map { _ -> parenthesis }
			}
			// May need to add separators before parenthesis.
			// All but the last segment (modified)
			val beginningSegments = segments.dropRight(1).map { case (segment, parenthesis) =>
				if (allSeparators.contains(segment.last))
					segment -> parenthesis
				else
					(segment + ",") -> parenthesis
			}
			// Saves the results
			// If there is an address to assign, connects it with the first segment
			// Also appends possible incomplete segment start if there is one (if possible)
			val (firstSegmentRaw, firstParenthesis) = beginningSegments.headOption.getOrElse(segments.head)
			val firstSegment = incompleteSegment match
			{
				case Some(incomplete) =>
					// "Pops" the incomplete segment
					incompleteSegment = None
					// Case: Parenthesis or verse conflict between the incomplete and complete portions =>
					// closes the incomplete portion and starts a new one
					if ((incomplete.parenthesis != firstParenthesis) ||
						(incomplete.address.isDefined && address.isDefined))
					{
						completeSegments :+= incomplete + ','
						PreparedSegment(firstSegmentRaw, address, firstParenthesis)
					}
					// Case: The incomplete segment portion may be added to the beginning of this segment
					else
						PreparedSegment(s"${incomplete.text} $firstSegmentRaw", incomplete.address.orElse(address),
							firstParenthesis)
				// Case: No incomplete segment start present
				case None => PreparedSegment(firstSegmentRaw, address, firstParenthesis)
			}
			
			val lastSegmentCompletes = complete || allSeparators.contains(segments.last._1.last)
			// Case: The first segment is the last segment => May add it or leave it incomplete
			if (segments.size == 1)
			{
				if (lastSegmentCompletes)
					completeSegments :+= firstSegment
				else
					incompleteSegment = Some(firstSegment)
			}
			// Case: There are multiple segments => Checks whether the last one should be added or left
			else
			{
				completeSegments :+= firstSegment
				completeSegments ++= beginningSegments.tail.map { case (text, parenthesis) =>
					PreparedSegment(text, None, parenthesis) }
				val (lastSegmentText, lastParenthesis) = segments.last
				val lastSegment = PreparedSegment(lastSegmentText, None, lastParenthesis)
				if (lastSegmentCompletes) completeSegments :+= lastSegment else incompleteSegment = Some(lastSegment)
			}
			
			// If the part completes, closes it
			if (complete)
			{
				completeParts :+= completeSegments
				completeSegments = Vector()
			}
		}
	}
	
	private class WordCollector
	{
		// ATTRIBUTES   -----------------------------
		
		private val wordIdsPointer = new PointerWithEvents(Map[String, Int]())
		private var _ambiguousWords = Set[Word]()
		
		private val savedWordsPointer = wordIdsPointer.map { _.keySet }
		
		
		// COMPUTED ---------------------------------
		
		def ambiguousWords = _ambiguousWords
		
		private def savedWords = savedWordsPointer.value
		
		
		// OTHER    ---------------------------------
		
		def record(segments: Vector[(Int, String)])(implicit connection: Connection): Unit =
		{
			// Starts by splitting the segments to individual words
			val segmentWords = segments.map { case (segmentId, text) =>
				val targetText = if (allSeparators.contains(text.last)) text.dropRight(1) else text
				val words = Regex.whiteSpace.split(targetText).toVector.map { _.trim }.filterNot { _.isEmpty }
				if (words.isEmpty)
				{
					println(s"Problem in segment $segmentId: '$text' because it contains no words")
					throw new IllegalArgumentException("No words in segment")
				}
				segmentId -> words
			}
			// Needs to handle the word casing. If there doesn't exist a capitalized version of the word outside
			// of segment starts, considers that word to be lower-case
			val (firstWordsRaw, nextWordsRaw) = segmentWords.splitMap { case (_, words) => words.head -> words.tail }
			val nextWords = nextWordsRaw.flatten.toSet
			
			// Checks whether some of the ambiguous words got resolved
			attemptToResolveWith(nextWords)
			
			val uniqueNextWords = nextWords -- savedWords
			val uniqueFirstWords = (firstWordsRaw.toSet -- uniqueNextWords) -- savedWords
			
			// Handles the first words. AlwaysCapitalize -words are ambiguous at first because it's unsure whether
			// the casing is due to location in the sentence
			val firstWordsByCasing = uniqueFirstWords.groupBy(Capitalization.of)
			val unambiguousFirstWords = firstWordsByCasing - AlwaysCapitalize
			val ambiguousFirstWords = firstWordsByCasing.getOrElse(AlwaysCapitalize, Set())
				.filterNot { word => uniqueNextWords.contains(word.toLowerCase) ||
					unambiguousFirstWords.exists { _._2.contains(word.toLowerCase) } ||
					savedWords.contains(word.toLowerCase) }
			
			// Checks whether unambiguous first words can be used to resolve ambiguous ones
			val firstWordResolves = attemptToResolveWith(unambiguousFirstWords.flatMap { _._2 }.toSet).map { _._2 }
			
			// Inserts the new words to the DB
			val insertedWords = WordModel.insert(
				uniqueNextWords.map { word => word -> Capitalization.of(word) }.toVector ++
				unambiguousFirstWords.flatMap { case (capitalization, words) => words.map { _ -> capitalization } }
					.filterNot { case (word, _) => firstWordResolves.contains(word) }
			)
			val insertedAmbiguousWords = WordModel.insert(ambiguousFirstWords.toVector.map { _ -> AlwaysCapitalize })
			if (insertedWords.nonEmpty || insertedAmbiguousWords.nonEmpty)
				wordIdsPointer.update { _ ++ (insertedWords ++ insertedAmbiguousWords)
					.map { word => word.value -> word.id } }
			
			// Remembers the ambiguous words so that they can be resolved later
			if (insertedAmbiguousWords.nonEmpty)
				_ambiguousWords ++= insertedAmbiguousWords
			
			// Assigns the words to the segments
			WordAssignmentModel.insert(segmentWords.flatMap { case (segmentId, words) =>
				words.zipWithIndex.map { case (word, index) =>
					WordAssignmentData(idForWord(word), Location(segmentId, index)) }
			})
		}
		
		private def idForWord(word: String) =
			wordIdsPointer.value.getOrElse(word, wordIdsPointer.value(word.toLowerCase))
		
		private def attemptToResolveWith(words: Set[String])(implicit connection: Connection) =
		{
			// Checks whether some of the ambiguous words got resolved
			val resolves = words.flatMap { word => _ambiguousWords.find { _.value ~== word }.map { _ -> word } }
			// Updates the resolved word capitalization where necessary
			resolves.foreach { case (original, newVersion) =>
				val correctCapitalization = Capitalization.of(newVersion)
				if (correctCapitalization != AlwaysCapitalize)
					WordModel.withId(original.id).withValue(newVersion).withCapitalization(correctCapitalization)
						.update()
			}
			if (resolves.nonEmpty)
			{
				// Also updates the casing in the word id map
				wordIdsPointer.update { _ -- resolves.map { _._1.value } ++
					resolves.map { case (word, spelling) => spelling -> word.id } }
				// Updates the ambiguous words set
				val resolvedWordIds = resolves.map { _._1.id }
				_ambiguousWords = _ambiguousWords.filterNot { word => resolvedWordIds.contains(word.id) }
			}
			
			// Returns the resolving words
			resolves
		}
	}
}
