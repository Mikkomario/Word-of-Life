package vf.word.database.model.text

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.model.immutable.Storable
import utopia.vault.sql.Insert
import vf.word.database.WordTables
import vf.word.model.enumeration.Capitalization
import vf.word.model.stored.text.Word

object WordModel
{
	// COMPUTED ------------------------------
	
	/**
	 * @return The table used by this model
	 */
	def table = WordTables.word
	
	
	// OTHER    -----------------------------
	
	/**
	 * @param id A word id
	 * @return A word with that id
	 */
	def withId(id: Int) = apply(Some(id))
	
	/**
	 * Inserts multiple new words to the database
	 * @param data Data to insert
	 * @param connection DB Connection (implicit)
	 * @return Newly inserted words
	 */
	def insert(data: Seq[(String, Capitalization)])(implicit connection: Connection) =
	{
		val ids = Insert(table, data.map { case (value, capitalize) =>
			apply(None, Some(value), Some(capitalize)).toModel }).generatedIntKeys
		ids.zip(data).map { case (id, (value, capitalize)) => Word(id, value, capitalize) }
	}
}

/**
 * Used for interacting with word data in the DB
 * @author Mikko Hilpinen
 * @since 24.4.2021, v0.1
 */
case class WordModel(id: Option[Int] = None, value: Option[String] = None, capitalize: Option[Capitalization] = None)
	extends Storable
{
	// IMPLEMENTED  -------------------------
	
	override def table = WordModel.table
	
	override def valueProperties = Vector("id" -> id, "value" -> value, "capitalization" -> capitalize.map { _.id })
	
	
	// OTHER    -----------------------------
	
	/**
	 * @param value New word value / text
	 * @return A copy of this model with that value
	 */
	def withValue(value: String) = copy(value = Some(value))
	
	/**
	 * @param capitalization A new capitalization
	 * @return A copy of this model with that capitalization
	 */
	def withCapitalization(capitalization: Capitalization) = copy(capitalize = Some(capitalization))
}