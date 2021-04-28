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
	Ch1|7|14| The sons of Manasseh; Ashriel, whom she bare:
		(but his concubine the Aramitess bare Machir the father of Gilead:~
	Ch1|7|15| And Machir took to wife the sister of Huppim and Shuppim, whose sister's name was Maachah;)
		and the name of the second was Zelophehad: and Zelophehad had daughters.~
	
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
	/*
	private def separateParenthesisFrom(str: String, regex: Regex = Regex.parenthesis) =
	{
		val matcher = regex.pattern.matcher(str)
		val builder = new VectorBuilder[(String, Boolean)]()
		var lastEnd = 0
		while (matcher.find())
		{
			val start = matcher.start()
			val end = matcher.end()
			if (start > lastEnd)
				builder += (str.substring(lastEnd, start).trim -> false)
			// Doesn't include the parenthesis themselves in the strings
			println(s"$start to $end in $str")
			builder += (str.substring(start + 1, end - 1).trim -> true)
			lastEnd = end
		}
		if (str.length > lastEnd)
			builder += (str.substring(lastEnd).trim -> false)
		
		builder.result()
	}
	assert(separateParenthesisFrom(
		"And Pathrusim, and Casluhim, (out of whom came Philistim,) and Caphtorim.") == Vector(
		"And Pathrusim, and Casluhim," -> false, "out of whom came Philistim," -> true, "and Caphtorim." -> false))
	
	private val sentenceSeparators = Set('.', '?', '!')
	private val sentencePartSeparators = Set(';', ':')
	
	private val sentenceSeparatorRegex = Regex.anyOf(sentenceSeparators.mkString(""))
	private val partSeparatorRegex = Regex.anyOf(sentencePartSeparators.mkString(""))
	
	private val sentenceEndingParenthesisRegex =
		Regex.escape('(') + Regex.any + sentenceSeparatorRegex + (!Regex.escape(')')).zeroOrMoreTimes + Regex.escape(')')
	private val partEndingParenthesisRegex =
		Regex.escape('(') + Regex.any + partSeparatorRegex + (!Regex.escape(')')).zeroOrMoreTimes + Regex.escape(')')
	
	println(separateParenthesisFrom("Some text. (More text. And more) here (and here);",
		sentenceEndingParenthesisRegex))
	assert(separateParenthesisFrom("Some text. (More text. And more) here (and here);",
		sentenceEndingParenthesisRegex) == Vector(
		"Some text." -> false, "More text. And more" -> true, "here (and here);" -> false))
	*/
	// KjvDatParser.test()
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
	/*
	assert(Capitalization.of("Lord's") == AlwaysCapitalize)
	assert(Capitalization.of("LORD's") == AllCaps)
	assert(Capitalization.of("lords") == Normal)
	assert(Capitalization.of("lord's") == Normal)*/
}
