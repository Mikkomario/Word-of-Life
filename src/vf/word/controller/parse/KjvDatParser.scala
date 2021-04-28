package vf.word.controller.parse

import utopia.flow.datastructure.mutable.ResettableLazy
import utopia.flow.parse.{CsvReader, Regex}
import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.ActionBuffer
import utopia.flow.util.StringExtensions._
import utopia.vault.database.Connection
import vf.word.database.access.many.text.DbWritings
import vf.word.database.model.address.{BookCodeModel, ChapterModel, VerseModel}
import vf.word.database.model.text.{SentenceModel, SentencePartModel, SentenceSegmentModel, WordAssignmentModel, WordModel}
import vf.word.model.enumeration.Capitalization
import vf.word.model.partial.address.{BookCodeData, VerseData}
import vf.word.model.partial.text.{SentencePartData, SentenceSegmentData, WordAssignmentData}
import vf.word.model.stored.address.Chapter

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
	
	def test() =
	{
		val builder = new SentenceBuilder()
		// Sa1|9|27| And as they were going down to the end of the city, Samuel said to Saul, Bid the servant pass on before us, (and he passed on), but stand thou still a while, that I may shew thee the word of God.~
		builder += VerseLine(Address("Sa1", 9, 27),
			"And as they were going down to the end of the city, Samuel said to Saul, Bid the servant pass on before us, (and he passed on), but stand thou still a while, that I may shew thee the word of God.")
		val sentences = builder.takeSentences()
		println(s"${sentences.size} sentences:")
		sentences.foreach { sentence =>
			println(s"\tSentence with ${sentence.size} parts:")
			sentence.foreach { part =>
				println(s"\t\tPart with ${part.size} segments:")
				part.foreach { segment =>
					println(s"\t\t\t${segment.text} (Parenthesis: ${segment.parenthesis}, Addr: ${segment.address})")
				}
			}
		}
	}
	
	/**
	 * Reads the King James Bible data from a text file
	 * @param path Path to the text file
	 * @param connection DB connection (implicit)
	 * @return Success or failure
	 */
	def apply(path: Path)(implicit connection: Connection) =
	{
		// Needs to save chapter data between insert iterations in order to not insert a chapter twice
		var lastChapter = Chapter(-1, -1, -1)
		var wordIds = Map[String, Int]()
		
		// Sentences are inserted in bulks
		val sentenceBuffer = ActionBuffer[PreparedSentence2](100) { sentences =>
			println(s"Inserting ${sentences.size} sentences")
			sentences.find { _.parts.exists { _.exists { segment =>
				segment.text.contains('(') || segment.text.contains(')') } } }.foreach { sentence =>
				println("Parenthesis error in following sentence:")
				sentence.parts.foreach { part =>
					println(s"\tPart with ${part.size} segments:")
					part.foreach { segment => println(
						s"\t\t${segment.text} (Parenthesis: ${segment.parenthesis}, Address: ${segment.address})") }
				}
				throw new IllegalArgumentException("Broken sentence")
			}
			// Starts by inserting the sentences (top level) first
			val insertedSentences = SentenceModel.insert(sentences.map { s => s.writingId -> s.orderIndex })
			
			// Next inserts the sentence parts
			// Sentence + part index + segments
			val sentencePartData = insertedSentences.zip(sentences).flatMap { case (sentence, data) =>
				data.parts.zipWithIndex.map { case (segments, partIndex) => (sentence, partIndex, segments) }
			}
			// println(s"Listed addresses: ${sentences.flatMap { s => s.parts.map { _._2 } }.mkString(", ")}")
			/*
			println(s"Sentence parts:\n${sentencePartData.map { case (sentence, partIndex, (segments, address)) =>
				s"${sentence.id}:$partIndex: ${segments.size} segments${address match
				{
					case Some(addr) => s" (${addr.bookCode}:${addr.chapterNumber}:${addr.verseNumber})"
					case None => ""
				}}"
			}.mkString("\n")}")*/
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
			segmentData.find { case (_, _, _, text, parenthesis, _) => !parenthesis &&
				!sentenceSeparators.contains(text.last) && !sentencePartSeparators.contains(text.last) &&
				text.last != ','
			}.foreach { case (writingId, partId, segmentIndex, text, parenthesis, address) =>
				println(s"Error in following segment: '$text' ($writingId:$partId:$segmentIndex)")
				println(s"Parenthesis: $parenthesis")
				println(s"Address: $address")
				println(s"Last character: '${text.last}'")
			}
			val insertedSegments = SentenceSegmentModel.insert(
				segmentData.map { case (_, partId, orderIndex, text, parenthesis, _) =>
					// In some cases, has to add a separator character to the parenthesis
					val lastChar =
					{
						val default = text.last
						if (parenthesis)
						{
							if (allSeparators.contains(default)) default else ','
						}
						else
							default // TODO: Likely you will have to add a separator here as well, when necessary
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
			/*
			println(s"Listed chapters: ${listedChapters.toVector.sorted
				.map { case (bookId, chNumber) => s"$bookId:$chNumber" }.mkString(", ")}")
			println(s"Previous chapter: ${lastChapter.bookId}:${lastChapter.number}")*/
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
			wordIds = insertWords(insertedSegments.zip(segmentData)
				.map { case (segment, (_, _, _, text, _, _)) => segment.id -> text }, wordIds)
			// Records the last chapter for the next iteration
			if (insertedChapters.nonEmpty)
				lastChapter = insertedChapters.maxBy { c => c.bookId * 1000 + c.number }
		}
		
		CsvReader.iterateRawRowsIn(path, "\\|") { rawLinesIter =>
			val linesIter = rawLinesIter.map(VerseLine.fromLine).pollable
			
			var currentWritingId = -1
			var currentBookCode = ""
			val sentenceIndexIteratorPointer = ResettableLazy { Iterator.from(0) }
			val sentenceBuilder = new SentenceBuilder()
			// var incompleteSentenceStart = ""
			
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
						.map { parts => PreparedSentence2(currentWritingId, nextSentenceIndex(), parts) }
					/*
					if (incompleteSentenceStart.nonEmpty)
					{
						val (incompleteParts, lastSegment) = split(incompleteSentenceStart)
						val parts = lastSegment match
						{
							case Some(lastSegment) =>
								incompleteParts.dropRight(1) :+ (incompleteParts.last :+ (lastSegment + "." -> false))
							case None => incompleteParts
						}
						sentenceBuffer += PreparedSentence(currentWritingId, nextSentenceIndex, parts.map { _ -> None})
						incompleteSentenceStart = ""
					}*/
					
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
					.map { parts => PreparedSentence2(currentWritingId, nextSentenceIndex(), parts) }
				/*
				// Collects the lines that belong to this sentence / sentence group
				// NB: This logic breaks if the book doesn't end its last sentence
				val lines = linesIter.takeNextTo { line => sentenceSeparatorRegex.existsIn(line.text) }
				// Separates possible additional & incomplete sentences
				val (lastLine, extraSentences, incompleteEnd) = lines.last.sentences
				// Handles the main sentence first
				var incompleteSegment: Option[String] = None
				val previousLineParts =
				{
					if (incompleteSentenceStart.nonEmpty)
					{
						val (parts, incomplete) = split(incompleteSentenceStart)
						incompleteSegment = incomplete
						parts
					}
					else
						Vector()
				}
				val mainSentenceParts = (lines.dropRight(1) :+ lastLine).flatMap { line =>
					val (lineParts, incomplete) = split(line.text)
					// May append the incomplete segment to the first line part
					val firstLinePart = incompleteSegment match
					{
						case Some(segmentPart) =>
							val part = lineParts.head
							val (defaultSegment, parenthesis) = part.head
							((segmentPart + defaultSegment.head.toLower + defaultSegment.tail) -> parenthesis) +:
								part.tail
						case None => lineParts.head
					}
					incompleteSegment = incomplete
					(firstLinePart -> Some(line.address)) +: lineParts.tail.map { _ -> None }
				}
				sentenceBuffer += PreparedSentence(currentWritingId, nextSentenceIndex,
					previousLineParts.map { _ -> None } ++ mainSentenceParts)
				
				// Next adds possible additional sentences in the last verse
				// Doesn't expect these to contain incomplete segments because these are complete sentences
				sentenceBuffer ++= extraSentences.map { sentence =>
					PreparedSentence(currentWritingId, nextSentenceIndex, split(sentence)._1.map { _ -> None }) }
				incompleteSentenceStart = incompleteSegment.getOrElse("") + incompleteEnd.getOrElse("")
				 */
			}
			
			// Adds the last partial sentence if there was one and inserts the rest of the sentences
			sentenceBuffer ++= sentenceBuilder.closeAndTake().map { parts =>
				PreparedSentence2(currentWritingId, nextSentenceIndex(), parts) }
			/*
			if (incompleteSentenceStart.nonEmpty)
			{
				val (sentenceParts, lastSegment) = split(incompleteSentenceStart)
				val parts = lastSegment match
				{
					case Some(lastSegment) =>
						sentenceParts.dropRight(1) :+ (sentenceParts.last :+ (lastSegment + "." -> false) )
					case None => sentenceParts
				}
				sentenceBuffer += PreparedSentence(currentWritingId, nextSentenceIndex, parts.map { _ -> None })
			}*/
			sentenceBuffer.flush()
		}
	}
	
	// Returns the new words map
	private def insertWords(segments: Vector[(Int, String)], existingWords: Map[String, Int])
	                       (implicit connection: Connection) =
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
		val uniqueFirstWords = (firstWordsRaw.toSet -- nextWords) -- existingWords.keySet
		
		// Inserts the new unambiguous words to acquire word ids
		val insertedWords = WordModel.insert((nextWords -- existingWords.keySet)
			.map { word => word -> Capitalization.of(word) }.toVector)
		val currentWords = existingWords ++ insertedWords.map { word => word.value -> word.id }
		
		// Handles the possibly ambiguous words
		val actuallyUniqueFirstWords = uniqueFirstWords.filterNot { word => currentWords.contains(word.toLowerCase) }
		val newWordMap =
		{
			if (actuallyUniqueFirstWords.nonEmpty)
			{
				val ambiguousWords = actuallyUniqueFirstWords.filter { _.head.isUpper }
				if (ambiguousWords.nonEmpty)
					println(s"Unsure whether the following words should be capitalized or not: ${
						ambiguousWords.mkString(", ")}")
				// TODO: Remember and resolve the ambiguous words when more data is received
				val insertedFirstWords = WordModel.insert(
					actuallyUniqueFirstWords.map { word => word -> Capitalization.of(word) }.toVector)
				currentWords ++ insertedFirstWords.map { word => word.value -> word.id }
			}
			else
				currentWords
		}
		
		// Assigns the words to the segments
		WordAssignmentModel.insert(segmentWords.flatMap { case (segmentId, words) =>
			words.zipWithIndex.map { case (word, index) =>
				WordAssignmentData(newWordMap.getOrElse(word, newWordMap(word.toLowerCase)), segmentId, index)
			}
		})
		
		newWordMap
	}
	
	// Divides to parts and segments (where segments have a parenthesis flag)
	// Includes optional incomplete segment start text
	/*
	private def split(line: String) =
	{
		// Splits the line first to parts
		val defaultResult = partSeparatorRegex.divide(line).map { _.trim }.filterNot { _.isEmpty }.map { part =>
			// Parenthesis segments separately, if there are any
			// TODO: Removed parenthesis in the separateParenthesisFrom method
			separateParenthesisFrom(part).flatMap { case (part, parenthesis) =>
				// Then splits each part to segments
				val wholeText = if (parenthesis) part.afterFirst("(").untilLast(")") else part.trim
				segmentSeparatorRegex.divide(wholeText).map { _.trim -> parenthesis }
			}
		}
		// Sometimes the line may end in a middle of a sentence segment, in which case separates it
		val (lastSegmentText, lastSegmentParenthesis) = defaultResult.last.last
		if (lastSegmentParenthesis || allSeparators.contains(lastSegmentText.last))
			defaultResult -> None
		else
		{
			val lastPart = defaultResult.last
			if (lastPart.size > 1)
				(defaultResult.dropRight(1) :+ lastPart.dropRight(1)) -> Some(lastSegmentText)
			else
				defaultResult.dropRight(1) -> Some(lastSegmentText)
		}
	}*/
	// private def splitToSegments(text: String)
	/*
	private def splitVerseLines(lines: Vector[(String, Option[Address])], previousStart: Option[String] = None) =
	{
		// Handles complete parts of the previous sentence
		val (previousParts, startSegment) = previousStart match
		{
			case Some(start) =>
				val parts = partSeparatorRegex.divide(start).map { _.trim }.filterNot { _.isEmpty }
				if (sentencePartSeparators.contains(parts.last.last))
					parts -> None
				else
					parts.dropRight(1) -> Some(parts.last)
			case None => Vector() -> None
		}
		// Next handles the verse lines. Appends incomplete end of a line to the next line.
		var incomplete = previousStart
		val partsPerVerse =
		{
			lines.map { case (line, address) =>
				val parts = partSeparatorRegex.divide(line).map { _.trim }.filterNot { _.isEmpty }
			}
		}
		
		// Splits the line first to parts
		val defaultResult = partSeparatorRegex.divide(line).map { _.trim }.filterNot { _.isEmpty }.map { part =>
			// Parenthesis segments separately, if there are any
			separateParenthesisFrom(part).flatMap { case (part, parenthesis) =>
				// Then splits each part to segments
				val wholeText = if (parenthesis) part.afterFirst("(").untilLast(")") else part.trim
				segmentSeparatorRegex.divide(wholeText).map { _.trim -> parenthesis }
			}
		}
		// Sometimes the line may end in a middle of a sentence segment, in which case separates it
		val (lastSegmentText, lastSegmentParenthesis) = defaultResult.last.last
		if (lastSegmentParenthesis || allSeparators.contains(lastSegmentText.last))
			defaultResult -> None
		else
		{
			val lastPart = defaultResult.last
			if (lastPart.size > 1)
				(defaultResult.dropRight(1) :+ lastPart.dropRight(1)) -> Some(lastSegmentText)
			else
				defaultResult.dropRight(1) -> Some(lastSegmentText)
		}
	}*/
	
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
	
	// Each part may or may not contain a verse address
	// Parenthesis flag is included in each segment (String, Boolean)
	private case class PreparedSentence(writingId: Int, orderIndex: Int,
	                                    parts: Vector[(Vector[(String, Boolean)], Option[Address])])
	
	private case class PreparedSentence2(writingId: Int, orderIndex: Int, parts: Vector[Vector[PreparedSegment]])
	
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
		// TODO: Add parsing failure handling?
		// Each line ends with ~
		def fromLine(line: Vector[String]) = VerseLine(Address(line.head, line(1).toInt, line(2).toInt),
			line(3).untilLast("~").stripControlCharacters.trim)
	}
	
	private case class VerseLine(address: Address, text: String)
	{
		// Single sentence part of this line + additional sentences + incomplete sentence
		/*
		def sentences =
		{
			val parts = sentenceSeparatorRegex.divide(text).map { _.trim }.filterNot { _.isEmpty }
			// Case: Last sentence is complete / ends on this line
			if (sentenceSeparators.contains(text.last))
				(copy(text = parts.head), parts.tail, None)
			// Case: Sentence is left incomplete
			else
				(copy(text = parts.head), parts.tail.dropRight(1), if (parts.size > 1) Some(parts.last) else None)
		}*/
	}
	
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
			/*
			{
				if (incompleteSegmentText.nonEmpty)
					incompleteSegmentText + " " + segments.head._1
				else
					segments.head._1
			}*/
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
}
