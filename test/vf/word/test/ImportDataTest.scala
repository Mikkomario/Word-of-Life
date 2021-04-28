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
	
	/*
	Fails on:
	Jdg|20|27| And the children of Israel enquired of the LORD,
		(for the ark of the covenant of God was there in those days,~
	Jdg|20|28| And Phinehas, the son of Eleazar, the son of Aaron, stood before it in those days,)
		saying, Shall I yet again go out to battle against the children of Benjamin my brother,
		or shall I cease? And the LORD said, Go up; for to morrow I will deliver them into thine hand.~

	Because the parenthesis is divided between two verses
	 */
	
	/*
	val builder = new SentenceBuilder()
	builder += VerseLine(Address("Exo", 9, 28),
		"Intreat the LORD (for it is enough) that there be no more mighty thunderings and hail; and I will let you go, and ye shall stay no longer.")
	println("Parsed sentences:")
	builder.takeSentences().foreach { parts =>
		println(s"Sentence with ${parts.size} parts:")
		parts.foreach { part =>
			println(s"\tPart with ${part.size} segments:")
			part.foreach { segment =>
				println(s"\t\t${segment.text} (Parenthesis: ${segment.parenthesis}, Address: ${segment.address})")
			}
		}
	}*/
	
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
