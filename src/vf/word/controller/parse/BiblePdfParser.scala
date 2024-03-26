package vf.word.controller.parse

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.{PDFTextStripper, TextPosition}
import utopia.flow.collection.mutable.builder.ZipBuilder
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.parse.AutoClose._
import utopia.flow.parse.string.Regex
import vf.word.model.cached.{ChapterText, RomanNumeral}
import vf.word.model.partial.bible.FootnoteData

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
		val referenceStart = {
			val verseMarker = (Regex("v") + period + Regex.digit.oneOrMoreTimes).withinParenthesis
			(Regex.digit.oneOrMoreTimes + period + verseMarker.noneOrOnce).withinParenthesis || verseMarker
		}
		
		// TODO: Implement prioritized verse-splitting
		// val highPriorityVerseSplitter = Regex.anyOf(".;!?")
	}
	
	// private case class Line()
	
	private class CustomTextStripper(defaultFontSize: Float, headerFontSize: Float) extends PDFTextStripper
	{
		// ATTRIBUTES   ---------------------
		
		private var lastLineY = 10000f
		// Assumes that every page starts with a header
		private var headerFlag = true
		private var referencesFlag = false
		
		private val builder = new ChapterBuilder(1)
		private val referenceTextBuilder = new VectorBuilder[String]()
		// Collects foot note data (reference + text)
		// Intended to be processed once the book completes
		private val footnotesBuilder = ZipBuilder.zip[FootnoteData, String]()
		
		
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
			// Case: New line
			else {
				builder.newLine()
				
				// Case: New page => Processes the references from the last page and ignores the first line
				if (y < lastLineY) {
					processReferences()
					headerFlag = true
				}
				// Case: Next line
				else {
					// Checks whether a header continues
					if (headerFlag && fontSize < headerFontSize)
						headerFlag = false
					
					if (!headerFlag) {
						// Case: Reference section starts => Remembers it and continues processing normally
						if (Expressions.referenceStart(text)) {
							referencesFlag = true
							builder += text
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
		
		// TODO: Add a function for finalizing the last chapter before closing the document
		
		// Processes an individual word / text element
		// Checks for verse markers, chapter markers, etc.
		private def processNormally(text: String, number: => Option[Int], fontSize: => Float) = {
			// Case: Writing the reference section => Collects reference text separately
			// (processes once section completes)
			if (referencesFlag)
				referenceTextBuilder += text
			// Case: Normal font element => Appends the text for later processing
			else if (fontSize == defaultFontSize)
				builder += text
			// Case: Small font element => Checks for a verse marker
			else if (fontSize < defaultFontSize)
				number match {
					// Case: Verse marker => Remembers its position
					case Some(number) => builder.addVerseMarker(number)
					// Case: Not a verse marker but other lower size element => Treats as text
					case None => builder += text
				}
			// Case: Larger font element => Expects a chapter marker. Ignores others.
			else
				number.foreach { n => storeChapter(builder.finishChapter(n)) }
		}
		
		// TODO: Implement and call this from new page start (& finalization)
		private def processReferences() = {
			val referencesText = referenceTextBuilder.result().mkString(" ")
			referenceTextBuilder.clear()
			val references = Expressions.referenceStart.split(referencesText).map { _.trim }.filter { _.nonEmpty }
		}
		
		private def storeChapter(chapter: ChapterText) = {
			// TODO: Implement
			// TODO: Process the * marks
		}
	}
}
