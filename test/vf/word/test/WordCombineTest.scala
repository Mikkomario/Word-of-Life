package vf.word.test

import utopia.flow.util.logging.TimeLogger
import utopia.vault.sql.Delete
import utopia.vault.util.ErrorHandling
import utopia.vault.util.ErrorHandlingPrinciple.Throw
import vf.word.controller.process.CombineWords
import vf.word.database.{ConnectionPool, WordTables}
import vf.word.util.Globals.executionContext

/**
 * Tests word combination
 * @author Mikko Hilpinen
 * @since 3.5.2021, v10.9
 */
object WordCombineTest extends App
{
	ErrorHandling.defaultPrinciple = Throw
	
	val logger = new TimeLogger()
	// TODO: Add print calls for the logger
	
	ConnectionPool { implicit connection =>
		logger.checkPoint("Deleting existing word combination assignments")
		Delete.inParts(WordTables.wordCombinationAssignment, 5000)
		logger.checkPoint("Deleting existing word combination words")
		Delete.inParts(WordTables.wordCombinationWord, 5000)
		logger.checkPoint("Deleting existing word combinations")
		Delete.inParts(WordTables.wordCombination, 5000)
		
		logger.checkPoint("Starting word combination algorithm")
		CombineWords()
		logger.checkPoint("Done!")
	}
}
