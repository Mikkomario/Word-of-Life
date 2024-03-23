package vf.word.database

import utopia.vault.database.Tables
import utopia.vault.model.immutable.Table
import vf.word.util.Common._

/**
  * Used for accessing the database tables introduced in this project
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object WordOfLifeTables extends Tables(cPool)
{
	// COMPUTED	--------------------
	
	/**
	  * Table that contains book statement links (Lists the statements made within a book)
	  */
	def bookStatementLink = apply("book_statement_link")
	
	/**
	  * Table that contains book translations (Represents a translated book of the Bible)
	  */
	def bookTranslation = apply("book_translation")
	
	/**
	  * Table that contains footnotes (Represents a foot note made within the original text, 
	  * concerning a specific word or a statement within the test)
	  */
	def footnote = apply("footnote")
	
	/**
	  * Table that contains footnote statement links (Links a footnote to a statement made within it)
	  */
	def footnoteStatementLink = apply("footnote_statement_link")
	
	/**
	  * Table that contains translations (Represents a Bible translation)
	  */
	def translation = apply("translation")
	
	/**
	  * Table that contains verse markers (Locates a verse marker within a text)
	  */
	def verseMarker = apply("verse_marker")
	
	
	// OTHER	--------------------
	
	/**
	 * @param tableName Name of the targeted table
	 * @return Table with that name
	 */
	def apply(tableName: String): Table = apply(dbName, tableName)
}

