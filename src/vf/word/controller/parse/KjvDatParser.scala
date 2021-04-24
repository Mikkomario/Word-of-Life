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
	private val allSeparators = sentenceSeparators ++ sentencePartSeparators + ','
	
	private val sentenceSeparatorRegex = Regex.anyOf(sentenceSeparators.mkString(""))
	private val partSeparatorRegex = Regex.anyOf(sentencePartSeparators.mkString(""))
	private val segmentSeparatorRegex = Regex(",")
	
	
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
		var lastChapter = Chapter(-1, -1, -1)
		var wordIds = Map[String, Int]()
		
		// Sentences are inserted in bulks
		val sentenceBuffer = ActionBuffer[PreparedSentence](100) { sentences =>
			println(s"Inserting ${sentences.size} sentences")
			// Starts by inserting the sentences (top level) first
			val insertedSentences = SentenceModel.insert(sentences.map { s => s.writingId -> s.orderIndex })
			
			// Next inserts the sentence parts
			// Sentence + part index + part data (= segments + possible address)
			val sentencePartData = insertedSentences.zip(sentences).flatMap { case (sentence, data) =>
				data.parts.zipWithIndex.map { case (partData, partIndex) => (sentence, partIndex, partData) }
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
			// Remembers the verse links as well
			// Writing id + Part id + segment index + text + parenthesis + address (if assigned)
			val segmentData = insertedSentenceParts.zip(sentencePartData)
				.flatMap { case (sentencePart, (sentence, _, (segments, address))) =>
					val segmentsWithIndex = segments.zipWithIndex
					// If there is an address listed, it is considered to refer to the first segment
					val (firstSegmentText, firstSegmentParenthesis) = segments.head
					(sentence.contextId, sentencePart.id, 0, firstSegmentText, firstSegmentParenthesis, address) +:
						segmentsWithIndex.tail.map { case ((text, parenthesis), index) =>
							(sentence.contextId, sentencePart.id, index, text, parenthesis, None)
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
			var incompleteSentenceStart = ""
			
			def nextSentenceIndex = sentenceIndexIteratorPointer.value.next()
			
			while (linesIter.hasNext)
			{
				// Checks whether book needs to be changed
				val nextBookCode = linesIter.poll.address.bookCode
				if (nextBookCode != currentBookCode)
				{
					// Adds the last sentence of the book, if one was left partial from the previous line
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
					}
					
					println(s"Starting book: $nextBookCode")
					
					// Starts a new book
					currentWritingId = DbWritings.insert()
					currentBookCode = nextBookCode
					BookCodeModel.insert(BookCodeData(nextBookCode, currentWritingId))
					// Resets sentence order index counter
					sentenceIndexIteratorPointer.reset()
				}
				
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
			}
			
			// Adds the last partial sentence if there was one and inserts the rest of the sentences
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
			}
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
			segmentId -> Regex.whiteSpace.split(targetText).toVector.map { _.trim }
		}
		// Needs to handle the word casing. If there doesn't exist a capitalized version of the word outside
		// of segment starts, considers that word to be lower-case
		val (firstWordsRaw, nextWordsRaw) = segmentWords.splitMap { case (_, words) => words.head -> words.tail }
		val nextWords = nextWordsRaw.flatten.toSet
		val uniqueFirstWords = (firstWordsRaw.toSet -- nextWords) -- existingWords.keySet
		
		// Inserts the new unambiguous words to acquire word ids
		val insertedWords = WordModel.insert((nextWords -- existingWords.keySet)
			.map { word => word -> word.head.isUpper }.toVector)
		val currentWords = existingWords ++ insertedWords.map { word => word.value -> word.id }
		
		// Handles the possibly ambiguous words
		val actuallyUniqueFirstWords = uniqueFirstWords.filterNot { word => currentWords.contains(word.toLowerCase) }
		val newWordMap = if (actuallyUniqueFirstWords.nonEmpty)
		{
			val ambiguousWords = actuallyUniqueFirstWords.filter { _.head.isUpper }
			if (ambiguousWords.nonEmpty)
				println(s"Unsure whether the following words should be capitalized or not: ${
					ambiguousWords.mkString(", ")}")
			val insertedFirstWords = WordModel.insert(
				actuallyUniqueFirstWords.map { word => word -> word.head.isUpper }.toVector)
			currentWords ++ insertedFirstWords.map { word => word.value -> word.id }
		}
		else
			currentWords
		
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
	private def split(line: String) =
	{
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
	}
	
	private def separateParenthesisFrom(str: String) =
	{
		val matcher = Regex.parenthesis.pattern.matcher(str)
		val builder = new VectorBuilder[(String, Boolean)]()
		var lastEnd = 0
		while (matcher.find())
		{
			val start = matcher.start()
			val end = matcher.end()
			if (start > lastEnd)
				builder += (str.substring(lastEnd, start) -> false)
			builder += (str.substring(start, end) -> true)
			lastEnd = end
		}
		if (str.length > lastEnd)
			builder += (str.substring(lastEnd) -> false)
		
		builder.result()
	}
	
	
	// NESTED   ------------------------------
	
	// Each part may or may not contain a verse address
	// Parenthesis flag is included in each segment (String, Boolean)
	private case class PreparedSentence(writingId: Int, orderIndex: Int,
	                                    parts: Vector[(Vector[(String, Boolean)], Option[Address])])
	
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
		def sentences =
		{
			val parts = sentenceSeparatorRegex.divide(text).map { _.trim }.filterNot { _.isEmpty }
			// Case: Last sentence is complete / ends on this line
			if (sentenceSeparators.contains(text.last))
				(copy(text = parts.head), parts.tail, None)
			// Case: Sentence is left incomplete
			else
				(copy(text = parts.head), parts.tail.dropRight(1), if (parts.size > 1) Some(parts.last) else None)
		}
	}
	
	private case class Address(bookCode: String, chapterNumber: Int, verseNumber: Int)
	{
		override def toString = s"$bookCode:$chapterNumber:$verseNumber"
	}
}
