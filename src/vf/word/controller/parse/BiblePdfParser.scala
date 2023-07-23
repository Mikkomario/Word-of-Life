package vf.word.controller.parse

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.{PDFTextStripper, TextPosition}
import utopia.flow.parse.AutoClose._

import java.nio.file.Path
import java.util

/**
 * Extracts Bible text information from a pdf file
 * @author Mikko Hilpinen
 * @since 22.7.2023, v0.2
 */
object BiblePdfParser
{
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
			val extractor = new CustomTextStripper()
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
	
	private class CustomTextStripper extends PDFTextStripper
	{
		/*
		override def processTextPosition(text: TextPosition) = {
			val fontDesc = text.getFont.getFontDescriptor
			println(s"Text: ${text.getUnicode} (Italic: ${fontDesc.isItalic}, Bold: ${fontDesc.isForceBold} / ${fontDesc.getFontWeight})")
			super.processTextPosition(text)
		}*/
		
		override def writeString(text: String, textPositions: util.List[TextPosition]) = {
			val pos = textPositions.get(0)
			println(s"$text: ${pos.getX}, ${pos.getY} ${pos.getFontSize}")
			super.writeString(text, textPositions)
		}
	}
}
