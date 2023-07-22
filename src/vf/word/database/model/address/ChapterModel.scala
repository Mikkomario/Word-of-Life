package vf.word.database.model.address

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.model.immutable.Storable
import utopia.vault.sql.Insert
import vf.word.database.WordTables
import vf.word.model.stored.address.Chapter

object ChapterModel
{
	// COMPUTED ---------------------------
	
	/**
	 * @return The table used by this model
	 */
	def table = WordTables.chapter
	
	
	// OTHER    ---------------------------
	
	/**
	 * Inserts multiple new chapters to the database
	 * @param data Data to insert (each containing book id + chapter number)
	 * @param connection DB Connection (implicit)
	 * @return Newly inserted chapters
	 */
	def insert(data: Seq[(Int, Int)])(implicit connection: Connection) =
	{
		val ids = Insert(table, data.map { case (bookId, number) => apply(None, Some(bookId), Some(number)).toModel })
			.generatedIntKeys
		ids.zip(data).map { case (id, (bookId, number)) => Chapter(id, bookId, number) }
	}
}

/**
 * Used for interacting with Bible chapter data in the DB
 * @author Mikko Hilpinen
 * @since 24.4.2021, v0.1
 */
case class ChapterModel(id: Option[Int] = None, bookId: Option[Int] = None, number: Option[Int] = None)
	extends Storable
{
	override def table = ChapterModel.table
	
	override def valueProperties = Vector("id" -> id, "bookId" -> bookId, "number" -> number)
}
