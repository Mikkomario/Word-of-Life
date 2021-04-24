package vf.word.database.model.text

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.model.immutable.Storable
import utopia.vault.sql.Insert
import vf.word.database.WordTables
import vf.word.model.partial.text.SentencePartData
import vf.word.model.stored.text.SentencePart

object SentencePartModel
{
	// COMPUTED ------------------------------
	
	/**
	 * @return The table used by this model
	 */
	def table = WordTables.sentencePart
	
	
	// OTHER    ------------------------------
	
	/**
	 * @param data Sentence part data
	 * @return A model matching that data
	 */
	def apply(data: SentencePartData): SentencePartModel = apply(None, Some(data.sentenceId), Some(data.orderIndex))
	
	/**
	 * Inserts multiple new sentence parts to the DB
	 * @param data Sentence part data
	 * @param connection DB Connection (implicit)
	 * @return Newly inserted sentence parts
	 */
	def insert(data: Seq[SentencePartData])(implicit connection: Connection) =
	{
		val ids = Insert(table, data.map { apply(_).toModel }).generatedIntKeys
		ids.zip(data).map { case (id, data) => SentencePart(id, data) }
	}
}

/**
 * Used for interacting with sentence part data in the DB
 * @author Mikko Hilpinen
 * @since 24.4.2021, v0.1
 */
case class SentencePartModel(id: Option[Int] = None, sentenceId: Option[Int] = None, orderIndex: Option[Int] = None)
	extends Storable
{
	override def table = SentencePartModel.table
	
	override def valueProperties = Vector("id" -> id, "sentenceId" -> sentenceId, "orderIndex" -> orderIndex)
}
