package vf.word.test

import utopia.flow.generic.DataType
import utopia.flow.util.FileExtensions._
import utopia.vault.sql.Delete
import utopia.vault.util.ErrorHandling
import utopia.vault.util.ErrorHandlingPrinciple.Throw
import vf.word.controller.parse.KjvDatParser
import vf.word.database.{ConnectionPool, WordTables}
import vf.word.util.Globals._

import scala.util.{Failure, Success}

/**
 * Tests importing Bible data
 * @author Mikko Hilpinen
 * @since 24.4.2021, v0.1
 */
object ImportDataTest extends App
{
	DataType.setup()
	ErrorHandling.defaultPrinciple = Throw
	
	ConnectionPool { implicit connection =>
		println("Deleting existing data")
		connection(Delete(WordTables.word))
		connection(Delete(WordTables.writing))
		
		println("Starting file processing")
		KjvDatParser("data/kjvdat.txt") match
		{
			case Success(_) => println("Done!")
			case Failure(error) => error.printStackTrace()
		}
	}
}
