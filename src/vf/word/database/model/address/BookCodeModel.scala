package vf.word.database.model.address

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.model.immutable.Storable
import vf.word.database.WordTables
import vf.word.model.partial.address.BookCodeData
import vf.word.model.stored.address.BookCode

object BookCodeModel
{
	// COMPUTED ------------------------
	
	/**
	 * @return The table used by this model
	 */
	def table = WordTables.bookCode
	
	
	// OTHER    ------------------------
	
	/**
	 * @param data Book code data
	 * @return A model matching that data
	 */
	def apply(data: BookCodeData): BookCodeModel = apply(None, Some(data.code), Some(data.writingId))
	
	/**
	 * Inserts a new book code to the DB
	 * @param data book code data to insert
	 * @param connection DB Connection (implicit)
	 * @return Newly inserted code
	 */
	def insert(data: BookCodeData)(implicit connection: Connection) = BookCode(apply(data).insert().getInt, data)
}

/**
 * Used for interacting with book codes in the DB
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 */
case class BookCodeModel(id: Option[Int] = None, code: Option[String] = None, bookId: Option[Int] = None)
	extends Storable
{
	override def table = BookCodeModel.table
	
	override def valueProperties = Vector("id" -> id, "code" -> code, "bookId" -> bookId)
}