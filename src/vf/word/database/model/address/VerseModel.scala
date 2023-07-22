package vf.word.database.model.address

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.model.immutable.Storable
import utopia.vault.sql.Insert
import vf.word.database.WordTables
import vf.word.model.partial.address.VerseData
import vf.word.model.stored.address.Verse

object VerseModel
{
	// COMPUTED --------------------------------
	
	/**
	 * @return The table used by this model
	 */
	def table = WordTables.verse
	
	
	// OTHER    --------------------------------
	
	/**
	 * @param data Verse data
	 * @return A model matching that data
	 */
	def apply(data: VerseData): VerseModel = apply(None, Some(data.chapterId), Some(data.number),
		Some(data.startSegmentId))
	
	/**
	 * Inserts multiple new verses to the database
	 * @param data Verse data
	 * @param connection DB Connection (implicit)
	 * @return Newly inserted verses
	 */
	def insert(data: Seq[VerseData])(implicit connection: Connection) =
	{
		val ids = Insert(table, data.map { apply(_).toModel }).generatedIntKeys
		ids.zip(data).map { case (id, data) => Verse(id, data) }
	}
}

/**
 * Used for interacting with verse data in the DB
 * @author Mikko Hilpinen
 * @since 24.4.2021, v0.1
 */
case class VerseModel(id: Option[Int] = None, chapterId: Option[Int] = None, number: Option[Int] = None,
                      startSegmentId: Option[Int] = None) extends Storable
{
	override def table = VerseModel.table
	
	override def valueProperties = Vector("id" -> id, "chapterId" -> chapterId, "number" -> number,
		"startSegmentId" -> startSegmentId)
}
