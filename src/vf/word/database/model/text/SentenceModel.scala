package vf.word.database.model.text

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.model.immutable.Storable
import utopia.vault.sql.Insert
import vf.word.database.WordTables
import vf.word.model.stored.text.Sentence

object SentenceModel
{
	// COMPUTED -------------------------------
	
	/**
	 * @return The table used by this model
	 */
	def table = WordTables.sentence
	
	
	// OTHER    -------------------------------
	
	/**
	 * Inserts multiple new sentences to the DB
	 * @param data Data to insert (each containing context id + order index)
	 * @param connection DB Connection (implicit)
	 * @return Newly inserted sentences
	 */
	def insert(data: Seq[(Int, Int)])(implicit connection: Connection) = {
		val ids = Insert(table, data.map { case (contextId, orderIndex) =>
			apply(None, Some(contextId), Some(orderIndex)).toModel }).generatedIntKeys
		ids.zip(data).map { case (id, (contextId, orderIndex)) => Sentence(id, contextId, orderIndex) }
	}
}

/**
 * Used for interacting with sentence data in the DB
 * @author Mikko Hilpinen
 * @since 24.4.2021, v0.1
 */
case class SentenceModel(id: Option[Int] = None, contextId: Option[Int] = None, orderIndex: Option[Int] = None)
	extends Storable
{
	override def table = SentenceModel.table
	
	override def valueProperties = Vector("id" -> id, "contextId" -> contextId, "orderIndex" -> orderIndex)
}
