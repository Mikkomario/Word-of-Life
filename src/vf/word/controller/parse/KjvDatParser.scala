package vf.word.controller.parse

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.parse.file.CsvReader
import utopia.flow.util.EitherExtensions._
import utopia.flow.util.StringExtensions._
import utopia.flow.util.TryExtensions._
import utopia.logos.database.access.many.text.statement.DbStatements
import utopia.logos.model.cached.Statement
import utopia.vault.database.Connection
import vf.word.database.access.single.bible.translation.DbTranslation
import vf.word.database.access.single.bible.translation.book.DbBookTranslation
import vf.word.database.storable.bible.BookStatementPlacementDbModel
import vf.word.model.enumeration.Book
import vf.word.model.partial.bible.{BookStatementPlacementData, VerseMarkerData}

import java.nio.file.Path
import scala.collection.immutable.VectorBuilder
import scala.util.{Failure, Success}

/**
 * A parser that reads kjvdat.txt (King James Version data) file and inserts its contents to the DB
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 */
object KjvDatParser
{
	// OTHER    -------------------------------
	
	/**
	 * Reads the King James Bible data from a text file
	 * @param path Path to the text file
	 * @param connection DB connection (implicit)
	 * @return Success or failure. May contain secondary failures if some book codes were not recognized.
	 */
	def apply(path: Path)(implicit connection: Connection) = {
		val failuresBuilder = new VectorBuilder[Throwable]()
		CsvReader
			.iterateRawRowsIn(path, "\\|") { rawLinesIter =>
				lazy val translationId = DbTranslation.store("King James", "KJV").either.id
				var lastBookCode = ""
				var lastBookId = -1
				
				// Handles one chapter at a time
				rawLinesIter.map(VerseLine.fromLine).groupBy { _.chapter }.foreach { case (chapter, verses) =>
					// Inserts a new book, if necessary
					val bookId = {
						if (chapter.bookCode == lastBookCode)
							lastBookId
						else {
							lastBookCode = chapter.bookCode
							val id = Book.forCode(chapter.bookCode) match {
								case Success(book) => DbBookTranslation.store(book, translationId).either.id
								case Failure(error) =>
									failuresBuilder += error
									-1
							}
							lastBookId = id
							id
						}
					}
					if (bookId >= 0) {
						// Parses verse text data
						val statementData = verses.flatMap { verse =>
							val verseStatements = Statement.allFrom(verse.text)
							(verseStatements.head -> Some(verse.verse)) +: verseStatements.tail.map { _ -> None }
						}
						
						// Inserts the statements
						val insertedStatementIds = DbStatements.store(statementData.map { _._1 }).map { _.either.id }
						val placementIds = BookStatementPlacementDbModel
							.insert(insertedStatementIds.map { BookStatementPlacementData(bookId, _) })
							.map { _.id }
						
						// Inserts the verse markers
						placementIds.zip(statementData.map { _._2 })
							.flatMap { case (placementId, verseNumber) =>
								verseNumber.map { VerseMarkerData(chapter.number, _, placementId) }
							}
					}
				}
			}
			.catching(failuresBuilder.result())
	}
	
	
	// NESTED   ------------------------------
	
	private object VerseLine
	{
		// Each line ends with ~
		def fromLine(line: Seq[String]) =
			apply(Chapter(line.head, line(1).toInt), line(2).toInt, line(3).untilLast("~").stripControlCharacters.trim)
	}
	private case class VerseLine(chapter: Chapter, verse: Int, text: String)
	
	private case class Chapter(bookCode: String, number: Int)
}
