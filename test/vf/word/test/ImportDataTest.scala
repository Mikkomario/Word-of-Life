package vf.word.test

import utopia.flow.parse.file.FileExtensions._
import utopia.flow.util.logging.TimeLogger
import utopia.vault.sql.Delete
import utopia.vault.util.ErrorHandling
import utopia.vault.util.ErrorHandlingPrinciple.Throw
import vf.word.controller.parse.KjvDatParser
import vf.word.database.{ConnectionPool, WordTables}
import vf.word.util.Common._

import scala.util.{Failure, Success}

/**
 * Tests importing Bible data
 * @author Mikko Hilpinen
 * @since 24.4.2021, v0.1
 */
object ImportDataTest extends App
{
	ErrorHandling.defaultPrinciple = Throw
	val logger = new TimeLogger()
	// TODO: Add print calls for the logger
	
	logger.checkPoint("Opening DB Connection")
	ConnectionPool { implicit connection =>
		logger.checkPoint("Deleting word assignments")
		connection(Delete(WordTables.wordAssignment))
		logger.checkPoint("Deleting verse markers")
		connection(Delete(WordTables.verse))
		logger.checkPoint("Deleting chapters")
		connection(Delete(WordTables.chapter))
		logger.checkPoint("Deleting book codes")
		connection(Delete(WordTables.bookCode))
		logger.checkPoint("Deleting sentence segments")
		connection(Delete(WordTables.sentenceSegment))
		logger.checkPoint("Deleting sentence parts")
		connection(Delete(WordTables.sentencePart))
		logger.checkPoint("Deleting sentences")
		connection(Delete(WordTables.sentence))
		logger.checkPoint("Deleting writings")
		connection(Delete(WordTables.writing))
		logger.checkPoint("Deleting words")
		connection(Delete(WordTables.word))
		
		logger.checkPoint("Starting file processing")
		KjvDatParser("data/kjvdat.txt") match
		{
			case Success(_) => logger.checkPoint("File processing completed")
			case Failure(error) => error.printStackTrace()
		}
	}
}
