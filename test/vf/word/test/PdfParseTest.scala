package vf.word.test

import utopia.flow.parse.file.FileExtensions._
import vf.word.controller.parse.BiblePdfParser

/**
 * Tests Bible pdf parsing
 * @author Mikko Hilpinen
 * @since 22.7.2023, v0.2
 */
object PdfParseTest extends App
{
	BiblePdfParser("data/matthews/45-Romans.pdf").get
	
	println("\nDone")
}
