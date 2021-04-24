package vf.word.database

import utopia.vault.database.Tables
import vf.word.util.Globals.executionContext

/**
 * An access point to tables that are used in this project
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 */
object WordTables
{
	// ATTRIBUTES   ---------------------------
	
	private val tables = new Tables(ConnectionPool)
	private val databaseName = "living_word_database"
	
	
	// COMPUTED -------------------------------
	
	/**
	 * @return Table that contains all words
	 */
	def word = apply("word")
	/**
	 * @return Table that contains characters that separate sentence segments from each other
	 */
	def separatorCharacter = apply("separator_character")
	
	/**
	 * @return Table that contains books, letters and other whole writings
	 */
	def writing = apply("writing")
	/**
	 * @return Table that contains complete sentences
	 */
	def sentence = apply("sentence")
	/**
	 * @return Table that contains parts of complete sentences
	 */
	def sentencePart = apply("sentence_part")
	/**
	 * @return Table that contains smaller sentence segment parts
	 */
	def sentenceSegment = apply("sentence_segment")
	/**
	 * @return Table that shows where each word is placed within a sentence segment
	 */
	def wordAssignment = apply("word_assignment")
	
	/**
	 * @return Table that lists codes used for books
	 */
	def bookCode = apply("book_code")
	/**
	 * @return Table that lists Bible chapters
	 */
	def chapter = apply("chapter")
	/**
	 * @return Table that lists Bible verses
	 */
	def verse = apply("verse")
	
	// OTHER    -------------------------------
	
	private def apply(tableName: String) = tables(databaseName, tableName)
}
