package vf.word.controller.parse

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.{PDFTextStripper, TextPosition}
import utopia.flow.collection.immutable.Pair
import utopia.flow.collection.mutable.builder.ZipBuilder
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.parse.AutoClose._
import utopia.flow.parse.string.Regex
import utopia.logos.database.access.many.word.statement.DbStatements
import utopia.logos.model.cached.StatementText
import utopia.vault.database.{Connection, ConnectionPool}
import vf.word.database.storable.bible.{BookStatementLinkModel, BookTranslationModel, FootnoteModel, FootnoteStatementLinkModel, VerseMarkerModel}
import vf.word.model.cached.{ChapterText, RomanNumeral}
import vf.word.model.enumeration.Book
import vf.word.model.partial.bible.{BookStatementLinkData, BookTranslationData, FootnoteData, FootnoteStatementLinkData, VerseMarkerData}

import java.nio.file.Path
import java.util
import scala.collection.immutable.VectorBuilder
import scala.concurrent.ExecutionContext

/**
 * Extracts Bible text information from a pdf file
 * @author Mikko Hilpinen
 * @since 22.7.2023, v0.2
 */
object BiblePdfParser
{
	// ATTRIBUTES   -----------------------
	
	private val referenceChar = '*'
	
	
	// OTHER    ---------------------------
	
	/**
	 * Parses and stores a Bible book
	 * @param translationId Id of the translation associated with the specified file
	 * @param book The linked / translated Bible book
	 * @param path Path to the pdf file that contains book data
	 * @param exc Implicit execution context
	 * @param cPool Implicit connection pool (a database connection is kept open during text processing)
	 * @return Id of the inserted book translation.
	 *         Failure if the parsing process failed.
	 */
	def apply(translationId: Int, book: Book, path: Path)(implicit exc: ExecutionContext, cPool: ConnectionPool) = {
		PDDocument.load(path.toFile).tryConsume { doc =>
			// TODO: Probably has to first determine the default font size by reading one page or something
			cPool { implicit c =>
				// Creates the extractor
				val extractor = new CustomTextStripper(10.959f, 24.787f)
				extractor.setSortByPosition(true)
				
				// Processes the book text
				extractor.getText(doc)
				
				// Inserts the book and finalizes the process
				val bookId = BookTranslationModel.insert(BookTranslationData(book, translationId)).id
				extractor.finishBook(bookId)
				
				bookId
				/*
				doc.getDocumentCatalog.getPages.iterator().asScala.slice(5, 10).foreach { page =>
					println("\nProcessing page")
					extractor.processPage(page)
					
					extractor.setStartPage(1)
					extractor.setEndPage(1)
				}*/
			}
		}
	}
	
	
	// NESTED   -----------------------------
	
	private object Expressions
	{
		private val period = Regex.escape('.')
		
		val textReference = Regex.escape(referenceChar)
		// A reference may start with a chapter marker. e.g. 1. or a verse marker, e.g. v.7
		val referenceStart = {
			val verseMarker = (Regex("v") + period + Regex.digit.oneOrMoreTimes).withinParenthesis
			(Regex.digit.oneOrMoreTimes + period + verseMarker.noneOrOnce).withinParenthesis || verseMarker
		}
		
		// TODO: Implement prioritized verse-splitting
		// val highPriorityVerseSplitter = Regex.anyOf(".;!?")
	}
	
	// private case class Line()
	
	private class CustomTextStripper(defaultFontSize: Float, headerFontSize: Float)
	                                (implicit connection: Connection)
		extends PDFTextStripper
	{
		// ATTRIBUTES   ---------------------
		
		private var lastLineY = 10000f
		// Assumes that every page starts with a header
		private var headerFlag = true
		private var referencesFlag = false
		
		private val builder = new ChapterBuilder(1)
		private val referenceTextBuilder = new VectorBuilder[String]()
		// Collects foot note data (reference + text)
		private val footnotesBuilder = ZipBuilder.zip[FootnoteData, String]()
		
		// Collects all inserted statement ids
		private val statementIdsBuilder = new VectorBuilder[Int]()
		
		
		// IMPLEMENTED  -----------------------
		
		/*
		override def processTextPosition(text: TextPosition) = {
			val fontDesc = text.getFont.getFontDescriptor
			if (fontDesc == null)
				println(s"Text: ${ text.getUnicode }")
			else
				println(s"Text: ${text.getUnicode} (Italic: ${fontDesc.isItalic}, Bold: ${fontDesc.isForceBold} / ${text.getFontSize})")
			super.processTextPosition(text)
		}*/
		
		override def writeString(text: String, textPositions: util.List[TextPosition]) = {
			val pos = textPositions.get(0)
			// Tracks text position to recognize line changes
			val y = pos.getY
			// Uses font size to determine text role
			lazy val fontSize = pos.getFontSize
			lazy val baseText = Regex.letterOrDigit.filter(text)
			// Checks whether the specified text represents or includes a number.
			// Some numbers may be expressed as Roman literals
			lazy val number = {
				if (RomanNumeral.regex(baseText))
					Some(RomanNumeral(baseText).value)
				else
					baseText.int
			}
			
			// Case: Same line
			if (y == lastLineY) {
				if (!headerFlag)
					processNormally(text, number, fontSize)
			}
			// Case: New line
			else {
				builder.newLine()
				
				// Case: New page => Processes the references from the last page and ignores the first line
				if (y < lastLineY) {
					processReferences()
					headerFlag = true
				}
				// Case: Next line
				else {
					// Checks whether a header continues
					if (headerFlag && fontSize < headerFontSize)
						headerFlag = false
					
					if (!headerFlag) {
						// Case: Reference section starts => Remembers it and continues processing normally
						if (Expressions.referenceStart(text)) {
							referencesFlag = true
							builder += text
						}
						// Case: Normal new line => Processes normally
						else
							processNormally(text, number, fontSize)
					}
				}
				
				lastLineY = y
			}
			
			// println(s"$text: ${pos.getX}, ${pos.getY} ${pos.getFontSize}")
			// super.writeString(text, textPositions)
		}
		
		
		// OTHER    ----------------------
		
		// Finalizes the book.
		// Intended to be called once all text data has been read / processed
		def finishBook(bookId: Int) = {
			// Processes the final chapter
			processReferences()
			storeChapter(builder.finishChapter())
			
			// Inserts the collected statements to the book
			BookStatementLinkModel.insert(statementIdsBuilder.result().zipWithIndex
				.map { case (statementId, orderIndex) => BookStatementLinkData(bookId, statementId, orderIndex) })
		}
		
		// Processes an individual word / text element
		// Checks for verse markers, chapter markers, etc.
		private def processNormally(text: String, number: => Option[Int], fontSize: => Float) = {
			// Case: Writing the reference section => Collects reference text separately
			// (processes once section completes)
			if (referencesFlag)
				referenceTextBuilder += text
			// Case: Normal font element => Appends the text for later processing
			else if (fontSize == defaultFontSize)
				builder += text
			// Case: Small font element => Checks for a verse marker
			else if (fontSize < defaultFontSize)
				number match {
					// Case: Verse marker => Remembers its position
					case Some(number) => builder.addVerseMarker(number)
					// Case: Not a verse marker but other lower size element => Treats as text
					case None => builder += text
				}
			// Case: Larger font element => Expects a chapter marker. Ignores others.
			else
				number.foreach { n => storeChapter(builder.finishChapter(n)) }
		}
		
		private def processReferences() = {
			val referencesText = referenceTextBuilder.result().mkString(" ")
			referenceTextBuilder.clear()
			val references = Expressions.referenceStart.split(referencesText).map { _.trim }.filter { _.nonEmpty }
			footnotesBuilder.right ++= references
		}
		
		private def storeChapter(chapter: ChapterText) = {
			// Stores the collected statements. Extracts * marks and notes the locations where they appeared.
			val rawStatementData = chapter.verses.flatMap { _.statements }
			// The first values are statement indices,
			// The second values are word indices within those statements
			val referenceIndices = rawStatementData.zipWithIndex.flatMap { case (statement, statementIndex) =>
				statement.words.zipWithIndex.flatMap { case (word, wordIndex) =>
					if (word.text.contains(referenceChar)) {
						val referredWordIndex = if (word.text.length == 1) None else Some(wordIndex)
						Some(statementIndex -> referredWordIndex)
					}
					else
						None
				}
			}
			val cleanedStatementData = rawStatementData
				.map { _.mapWordText { t => Expressions.textReference.filterNot(t) } }
			val insertedStatementIds = DbStatements.store(cleanedStatementData).map { _.either.id }
			
			// Attaches the statements to the book translation
			// (the actual attachment is performed once the book is being finalized)
			statementIdsBuilder ++= insertedStatementIds
			
			// Stores the verse markers
			VerseMarkerModel.insert(chapter.verseMarkerIndices.map { case Pair(verseNumber, statementIndex) =>
				VerseMarkerData(chapter.index, verseNumber, insertedStatementIds(statementIndex))
			})
			
			// Queues and processes the reference marker data
			footnotesBuilder.left ++= referenceIndices.map { case (statementIndex, wordIndex) =>
				FootnoteData(insertedStatementIds(statementIndex), wordIndex)
			}
			storeCollectedFootnotes()
		}
		
		private def storeCollectedFootnotes() = {
			// Stores the footnotes
			// First value is footnote id. Second value is text to insert.
			val footnoteTextData = FootnoteModel
				.insertFrom(footnotesBuilder.popResult()) { _._1 } { case (footnote, (_, text)) => footnote.id -> text }
			
			// Stores the footnote text
			// OrderedFootnoteIds contains both the footnote id and the statement's order index
			val (statementData, orderedFootnoteIds) = footnoteTextData
				.flatMap { case (footnoteId, text) =>
					StatementText.allFrom(text).zipWithIndex.map { case (statementText, orderIndex) =>
						statementText -> (footnoteId -> orderIndex)
					}
				}
				.split
			val insertedStatementIds = DbStatements.store(statementData).map { _.either.id }
			
			// Attaches the text to the inserted footnotes
			FootnoteStatementLinkModel.insert(insertedStatementIds.indices.map { i =>
				val (footnoteId, orderIndex) = orderedFootnoteIds(i)
				FootnoteStatementLinkData(footnoteId, insertedStatementIds(i), orderIndex)
			})
		}
	}
}
