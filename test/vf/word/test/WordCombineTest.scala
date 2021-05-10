package vf.word.test

import utopia.flow.generic.DataType
import utopia.flow.util.TimeLogger
import utopia.vault.sql.{Delete, Limit}
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
	DataType.setup()
	ErrorHandling.defaultPrinciple = Throw
	
	val logger = new TimeLogger()
	
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
