package vf.word.database.model.text

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.model.immutable.Storable
import utopia.vault.sql.Insert
import vf.word.database.WordTables
import vf.word.model.partial.text.WordCombinationAssignmentData
import vf.word.model.stored.text.WordCombinationAssignment

object WordCombinationAssignmentModel
{
	// COMPUTED ---------------------------------
	
	/**
	 * @return The table used by this model
	 */
	def table = WordTables.wordCombinationAssignment
	
	
	// OTHER    ---------------------------------
	
	/**
	 * @param data Word combination assignment data
	 * @return A model matching that data
	 */
	def apply(data: WordCombinationAssignmentData): WordCombinationAssignmentModel =
		apply(None, Some(data.wordCombinationId), Some(data.headAssignmentId), Some(data.primary))
	
	/**
	 * Inserts multiple new word combination assignments to the DB
	 * @param data Data to insert
	 * @param connection DB Connection (implicit)
	 * @return Newly inserted data
	 */
	def insert(data: Seq[WordCombinationAssignmentData])(implicit connection: Connection) =
	{
		val ids = Insert(table, data.map { apply(_).toModel }).generatedIntKeys
		ids.zip(data).map { case (id, data) => WordCombinationAssignment(id, data) }
	}
}

/**
 * Used for interacting with word combination assignments in the DB
 * @author Mikko Hilpinen
 * @since 28.4.2021, v0.2
 */
case class WordCombinationAssignmentModel(id: Option[Int] = None, wordCombinationId: Option[Int] = None,
                                          headAssignmentId: Option[Int] = None, primary: Option[Boolean] = None)
	extends Storable
{
	override def table = WordCombinationAssignmentModel.table
	
	override def valueProperties = Vector("id" -> id, "wordCombinationId" -> wordCombinationId,
		"headAssignmentId" -> headAssignmentId, "isPrimary" -> primary)
}