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
		// println(connection(Delete(WordTables.wordCombinationAssignment) + Limit(3)))
		
		logger.checkPoint("Deleting existing word combination assignments")
		connection(Delete(WordTables.wordCombinationAssignment))
		logger.checkPoint("Deleting existing word combination words")
		connection(Delete(WordTables.wordCombinationWord))
		logger.checkPoint("Deleting existing word combinations")
		connection(Delete(WordTables.wordCombination))
		
		logger.checkPoint("Starting word combination algorithm")
		CombineWords()
		logger.checkPoint("Done!")
	}
}
