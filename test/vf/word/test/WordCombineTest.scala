package vf.word.test

import utopia.flow.generic.DataType
import utopia.vault.sql.Delete
import utopia.vault.util.ErrorHandling
import utopia.vault.util.ErrorHandlingPrinciple.Throw
import vf.word.controller.process.CombineWords
import vf.word.database.{ConnectionPool, WordTables}
import vf.word.util.Globals.executionContext

import scala.util.{Failure, Success}

/**
 * Tests word combination
 * @author Mikko Hilpinen
 * @since 3.5.2021, v10.9
 */
object WordCombineTest extends App
{
	DataType.setup()
	ErrorHandling.defaultPrinciple = Throw
	
	ConnectionPool { implicit connection =>
		println("Deleting existing data")
		connection(Delete(WordTables.wordCombination))
		
		println("Starting file processing")
		CombineWords.test("Daniel")
		println("Done!")
	}
}
