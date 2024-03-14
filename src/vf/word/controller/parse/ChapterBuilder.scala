package vf.word.controller.parse

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.parse.string.Regex
import utopia.flow.util.StringExtensions._
import utopia.flow.view.mutable.eventful.ResettableFlag
import utopia.logos.model.cached.StatementText
import vf.word.controller.parse.ChapterBuilder.wordSplitAtEndOfLineRegex
import vf.word.model.cached.{ChapterText, VerseText}

import scala.collection.immutable.VectorBuilder
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object ChapterBuilder
{
	// ATTRIBUTES   -----------------------
	
	private val wordSplitAtEndOfLineRegex = Regex.any + Regex.letter + Regex.escape('-')
}

/**
 * Used for building up the text and the verses that form a single chapter in a Bible book
 * @author Mikko Hilpinen
 * @since 14/03/2024, v0.2
 */
class ChapterBuilder(initialChapterIndex: Int) extends mutable.Builder[String, ChapterText]
{
	// ATTRIBUTES   ----------------------
	
	private var chapterIndex = initialChapterIndex
	
	// Collects completed verses
	private val versesBuilder = new VectorBuilder[VerseText]()
	// Collects completed statements within the currently open verse
	private val verseBuilder = new VectorBuilder[StatementText]()
	// Corrects words within the currently open line
	private val lineBuilder = new VectorBuilder[String]()
	
	// Verse being currently written
	private var openVerseMarker = -1
	// If the previous line didn't finish the last statement, it is stored here
	// Includes the separator before the next word/part, if applicable
	private var openStatement = ""
	// Collects the verse markers found within the currently open line
	private val verseMarkerQueue = new ListBuffer[Int]()
	// Contains whether the previous line ended with a delimiter,
	// meaning that if the next line introduces a new verse, it may very well start from the beginning of that line.
	private val verseMayStopBeforeNewLineFlag = ResettableFlag()
	
	
	// IMPLEMENTED  ----------------------
	
	//noinspection ScalaUnusedExpression
	override def clear() = {
		versesBuilder.clear()
		verseBuilder.clear()
		lineBuilder.clear()
		openVerseMarker = -1
		openStatement = ""
		verseMarkerQueue.clear()
		verseMayStopBeforeNewLineFlag.set()
	}
	
	override def result() = {
		// Finishes the current line and verse
		newLine()
		openStatement.notEmpty.foreach { t => verseBuilder ++= StatementText.allFrom(t) }
		completeVerse()
		
		ChapterText(chapterIndex, versesBuilder.result())
	}
	
	override def addOne(elem: String) = {
		lineBuilder += elem
		this
	}
	
	
	// OTHER    --------------------------
	
	/**
	 * Adds a verse marker to the text, indicating that this verse should start on the currently open line.
	 * @param verseIndex Index of the verse that starts on this line
	 */
	def addVerseMarker(verseIndex: Int): Unit = verseMarkerQueue += verseIndex
	
	/**
	 * Starts a new line of text
	 */
	//noinspection ScalaUnusedExpression
	def newLine(): Unit = {
		val line = openStatement + lineBuilder.result().mkString(" ")
		lineBuilder.clear()
		val lineStatements = StatementText.allFrom(line)
		// Will not consider the last started statement completed unless it ends with a delimiter
		// (since the statement may otherwise continue on the next line)
		val (completedStatements, nextOpenStatement) = {
			val lastStatement = lineStatements.lastOption
			if (lastStatement.exists { _.delimiter.isEmpty })
				lineStatements.drop(1) -> lastStatement
			else
				lineStatements -> None
		}
		
		// In case new verses start on this line, finishes the currently open verse as soon as
		// the currently open statement finishes.
		// In case there are multiple verse markers, adds one-statement -verses from parsed statements to fill in
		// At the end of this section, the 'openVerseMarker' verse may continue to the next line.
		var completedStatementsUsed = 0
		verseMarkerQueue.foreach { nextVerseMarker =>
			// Finishes the currently open statement
			if (!verseMayStopBeforeNewLineFlag.reset() && completedStatements.hasSize > completedStatementsUsed) {
				verseBuilder += completedStatements(completedStatementsUsed)
				completedStatementsUsed += 1
			}
			// Moves to build the next verse
			completeVerse(nextVerseMarker)
		}
		verseMarkerQueue.clear()
		
		// Next continues adding statements the currently open verse
		// (being the last newly introduced verse in case this line had verse markers)
		// Will return the incomplete statement (if present) to the buffer,
		// so that it will form the beginning of the next statement
		nextOpenStatement match {
			// Case: The last started statement didn't finish
			case Some(statement) =>
				val text = statement.toString
				// Case: Line ends in the middle of a word, indicated by a - at the end =>
				// Removes the dash and joins the word back together.
				if (wordSplitAtEndOfLineRegex(text))
					openStatement = text.dropRight(1)
				// Case: Line ends between words => Adds a whitespace before next line contents
				else
					openStatement = text + " "
				
			// Case: No statement was left open =>
			// Makes a note of this, so that the next line has the option of immediately starting with a new verse.
			case None =>
				openStatement = ""
				verseMayStopBeforeNewLineFlag.set()
		}
	}
	
	/**
	 * Finishes writing the currently open chapter and starts the next chapter
	 * @param nextChapterIndex The next chapter index. Default = The index that follows the current chapter index.
	 * @return Currently built chapter.
	 */
	def finishChapter(nextChapterIndex: Int = chapterIndex + 1) = {
		val res = result()
		clear()
		chapterIndex = nextChapterIndex
		res
	}
	
	//noinspection ScalaUnusedExpression
	private def completeVerse(newVerseIndex: Int = openVerseMarker + 1) = {
		versesBuilder += VerseText(openVerseMarker, verseBuilder.result())
		verseBuilder.clear()
		openVerseMarker = newVerseIndex
	}
}
