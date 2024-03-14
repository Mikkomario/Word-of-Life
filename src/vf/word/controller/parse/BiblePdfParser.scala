package vf.word.controller.parse

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.{PDFTextStripper, TextPosition}
import utopia.flow.collection.mutable.builder.CompoundingVectorBuilder
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.parse.AutoClose._
import utopia.flow.parse.string.Regex
import vf.word.model.cached.RomanNumeral

import java.nio.file.Path
import java.util
import scala.collection.immutable.VectorBuilder

/**
 * Extracts Bible text information from a pdf file
 * @author Mikko Hilpinen
 * @since 22.7.2023, v0.2
 */
object BiblePdfParser
{
	// ATTRIBUTES   -----------------------
	
	private val periodRegex = Regex.escape('.')
	private val referenceStartRegex = Regex.digit.oneOrMoreTimes + periodRegex
	
	
	// TODO: Implement a proper version of this function once the models are up-to-date. This is merely for testing pdf-reading.
	/*
	package arspdfbox;

	import java.io.*;
	import org.apache.pdfbox.exceptions.InvalidPasswordException;

	import org.apache.pdfbox.pdmodel.PDDocument;
	import org.apache.pdfbox.pdmodel.PDPage;
	import org.apache.pdfbox.pdmodel.common.PDStream;
	import org.apache.pdfbox.util.PDFTextStripper;
	import org.apache.pdfbox.util.TextPosition;
	import java.io.IOException;
	import java.util.List;

	public class PrintTextLocations extends PDFTextStripper {

		public PrintTextLocations() throws IOException {
			super.setSortByPosition(true);
		}

		public static void main(String[] args) throws Exception {

			PDDocument document = null;
			try {
				File input = new File("Stedman_Medical_Dictionary.pdf");
				//File input = new File("results/FontExample5.pdf");
				document = PDDocument.load(input);
				if (document.isEncrypted()) {
					try {
						document.decrypt("");
					} catch (InvalidPasswordException e) {
						System.err.println("Error: Document is encrypted with a password.");
						System.exit(1);
					}
				}
				PrintTextLocations printer = new PrintTextLocations();
				List allPages = document.getDocumentCatalog().getAllPages();
				//for (int i = 0; i < allPages.size(); i++) {
				for (int i = 99; i < 100; i++) {
					PDPage page = (PDPage) allPages.get(i);
					System.out.println("Processing page: " + i);
					PDStream contents = page.getContents();
					if (contents != null) {
						printer.processStream(page, page.findResources(), page.getContents().getStream());
					}
				}
			} finally {
				if (document != null) {
					document.close();
				}
			}
		}

		/**
			* @param text The text to be processed
			*/
		@Override /* this is questionable, not sure if needed... */
		protected void processTextPosition(TextPosition text)  {
			System.out.println("String[" + text.getXDirAdj() + ","
					+ text.getYDirAdj() + " fs=" + text.getFontSize() + " xscale="
					+ text.getXScale() + " height=" + text.getHeightDir() + " space="
					+ text.getWidthOfSpace() + " width="
					+ text.getWidthDirAdj() + "]" + text.getCharacter());
			System.out.append(text.getCharacter()+" <--------------------------------");
			// System.out.println("String[" + text.getXDirAdj() + "," + text.getYDirAdj() + " fs=" + text.getFontSize() + " xscale=" + text.getXScale() + " height=" + text.getHeightDir() + " space=" + text.getWidthOfSpace() + " width=" + text.getWidthDirAdj() + "]" + text.getCharacter());
			System.out.println(text.getFont().getBaseFont()); System.out.println(" Italic="+text.getFont().getFontDescriptor().isItalic());
			System.out.println(" Bold="+text.getFont().getFontDescriptor().getFontWeight());
			System.out.println(" ItalicAngle="+text.getFont().getFontDescriptor().getItalicAngle());
			//try{
			System.out.println(" xxxx="+text.getFont().getFontDescriptor().isFixedPitch());
			//} catch (IOException ioex){}

		}

	}
	 */
	def apply(path: Path) = {
		PDDocument.load(path.toFile).tryConsume { doc =>
			// TODO: Probably has to first determine the default font size by reading one page or something
			
			val extractor = new CustomTextStripper(10.959f, 24.787f)
			/*
			doc.getDocumentCatalog.getPages.iterator().asScala.slice(5, 10).foreach { page =>
				println("\nProcessing page")
				extractor.processPage(page)
			}*/
			extractor.setSortByPosition(true)
			extractor.setStartPage(1)
			extractor.setEndPage(1)
			println(extractor.getText(doc))
		}
	}
	
	
	// NESTED   -----------------------------
	
	private object Expressions
	{
		val period = Regex.escape('.')
		
		// A reference may start with a chapter marker. e.g. 1. or a verse marker, e.g. v.7
		val referenceStart = (Regex.digit.oneOrMoreTimes + period).withinParenthesis ||
			(Regex("v") + period + Regex.digit.oneOrMoreTimes).withinParenthesis
		
		val highPriorityVerseSplitter = Regex.anyOf(".;!?")
	}
	
	// private case class Line()
	
	private class CustomTextStripper(defaultFontSize: Float, headerFontSize: Float) extends PDFTextStripper
	{
		// ATTRIBUTES   ---------------------
		
		private var lastLineY = 10000f
		private var chapterMarker: Option[Int] = None
		// Assumes that every page starts with a header
		private var headerFlag = true
		private var referencesFlag = false
		
		private val lineBuilder = new StringBuilder()
		// First value is the word index BEFORE which the verse marker appears (e.g. 0 would be at line beginning)
		// Second value is the verse number itself
		private val verseMarkersBuilder = new VectorBuilder[Int]()
		
		private var queuedVerseMarker: Option[Int] = None
		
		// TODO: Add chapter and verse-building
		
		
		// IMPLEMENTED  -----------------------
		
		/*
		override def processTextPosition(text: TextPosition) = {
			val fontDesc = text.getFont.getFontDescriptor
			if (fontDesc == null)
				println(s"Text: ${ text.getUnicode }")
			else
				println(s"Text: ${text.getUnicode} (Italic: ${fontDesc.isItalic}, Bold: ${fontDesc.isForceBold} / ${text.getFontSize})")
			super.processTextPosition(text)
		}*/
		
		override def writeString(text: String, textPositions: util.List[TextPosition]) = {
			val pos = textPositions.get(0)
			// Tracks text position to recognize line changes
			val y = pos.getY
			// Uses font size to determine text role
			lazy val fontSize = pos.getFontSize
			lazy val baseText = Regex.letterOrDigit.filter(text)
			// Checks whether the specified text represents or includes a number.
			// Some numbers may be expressed as Roman literals
			lazy val number = {
				if (RomanNumeral.regex(baseText))
					Some(RomanNumeral(baseText).value)
				else
					baseText.int
			}
			
			// Case: Same line
			if (y == lastLineY) {
				if (!headerFlag)
					processNormally(text, number, fontSize)
			}
			else {
				// TODO: Process the collected line
				
				// Case: New page => Ignores the first line
				if (y < lastLineY)
					headerFlag = true
				// Case: New line
				else {
					// Checks whether a header continues
					if (headerFlag && fontSize < headerFontSize)
						headerFlag = false
					
					if (!headerFlag) {
						// Case: Reference section starts => Remembers it and continues processing normally
						if (Expressions.referenceStart(text)) {
							referencesFlag = true
							lineBuilder += text
						}
						// Case: Normal new line => Processes normally
						else
							processNormally(text, number, fontSize)
					}
				}
				
				lastLineY = y
			}
			
			
			println(s"$text: ${pos.getX}, ${pos.getY} ${pos.getFontSize}")
			
			// TODO: By removing this line, one may perhaps improve memory use somewhat
			super.writeString(text, textPositions)
		}
		
		
		// OTHER    ----------------------
		
		private def buildLine() = {
			// TODO: Implement
		}
		
		// Processes an individual word / text element
		// Checks for verse markers, chapter markers, etc.
		private def processNormally(text: String, number: => Option[Int], fontSize: => Float) = {
			// Case: Reference section or a normal font element => Appends the text for later processing
			if (referencesFlag || fontSize == defaultFontSize)
				lineBuilder += text
			// Case: Small font element => Checks for a verse marker
			else if (fontSize < defaultFontSize)
				number match {
					// Case: Verse marker => Remembers its position
					case Some(number) => verseMarkersBuilder += (lineBuilder.size -> number)
					// Case: Not a verse marker but other lower size element => Treats as text
					case None => lineBuilder += text
				}
			// Case: Larger font element => Expects a chapter marker. Ignores others.
			else
				number.foreach { n => chapterMarker = Some(n) }
		}
	}
}
