package vf.word.database.model.text

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.model.immutable.Storable
import utopia.vault.sql.Insert
import vf.word.database.WordTables
import vf.word.model.partial.text.SentenceSegmentData
import vf.word.model.stored.text.SentenceSegment

object SentenceSegmentModel
{
	// COMPUTED -------------------------
	
	/**
	 * @return The table used by this model
	 */
	def table = WordTables.sentenceSegment
	
	
	// OTHER    -------------------------
	
	/**
	 * @param data Sentence segment data
	 * @return A model matching that data
	 */
	def apply(data: SentenceSegmentData): SentenceSegmentModel = apply(None, Some(data.sentencePartId),
		Some(data.orderIndex), Some(data.terminator), Some(data.parenthesis))
	
	/**
	 * Inserts multiple new sentence segments to the DB
	 * @param data Data to insert
	 * @param connection DB Connection (implicit)
	 * @return Newly inserted segments
	 */
	def insert(data: Seq[SentenceSegmentData])(implicit connection: Connection) =
	{
		val ids = Insert(table, data.map { apply(_).toModel }).generatedIntKeys
		ids.zip(data).map { case (id, data) => SentenceSegment(id, data) }
	}
}

/**
 * Used for interacting with sentence segments in the DB
 * @author Mikko Hilpinen
 * @since 24.4.2021, v0.1
 */
case class SentenceSegmentModel(id: Option[Int] = None, sentencePartId: Option[Int] = None,
                                orderIndex: Option[Int] = None, terminator: Option[Char] = None,
                                parenthesis: Option[Boolean] = None) extends Storable
{
	override def table = SentenceSegmentModel.table
	
	override def valueProperties = Vector("id" -> id, "sentencePartId" -> sentencePartId,
		"orderIndex" -> orderIndex, "terminatorChar" -> terminator.map { _.toString },
		"parenthesis" -> parenthesis)
}
