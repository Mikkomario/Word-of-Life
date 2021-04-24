package vf.word.database.model.text

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.model.immutable.Storable
import utopia.vault.sql.Insert
import vf.word.database.WordTables
import vf.word.model.partial.text.WordAssignmentData
import vf.word.model.stored.text.WordAssignment

object WordAssignmentModel
{
	// COMPUTED -------------------------
	
	/**
	 * @return The table used by this model
	 */
	def table = WordTables.wordAssignment
	
	
	// OTHER    -------------------------
	
	/**
	 * @param data Word assignment data
	 * @return A model matching that data
	 */
	def apply(data: WordAssignmentData): WordAssignmentModel = apply(None, Some(data.wordId), Some(data.locationId),
		Some(data.orderIndex))
	
	/**
	 * Inserts multiple new word assignments to the DB
	 * @param data Data to insert
	 * @param connection DB Connection (implicit)
	 * @return Newly inserted word assignments
	 */
	def insert(data: Seq[WordAssignmentData])(implicit connection: Connection) =
	{
		val ids = Insert(table, data.map { apply(_).toModel }).generatedIntKeys
		ids.zip(data).map { case (id, data) => WordAssignment(id, data) }
	}
}

/**
 * Used for interacting with the word assignments in the DB
 * @author Mikko Hilpinen
 * @since 24.4.2021, v0.1
 */
case class WordAssignmentModel(id: Option[Int] = None, wordId: Option[Int] = None, locationId: Option[Int] = None,
                               orderIndex: Option[Int] = None) extends Storable
{
	override def table = WordAssignmentModel.table
	
	override def valueProperties = Vector("id" -> id, "wordId" -> wordId, "locationId" -> locationId,
		"orderIndex" -> orderIndex)
}
