package vf.word.database.model.text

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.model.immutable.Storable
import utopia.vault.sql.Insert
import vf.word.database.WordTables
import vf.word.model.enumeration.WordSide
import vf.word.model.partial.text.WordCombinationData
import vf.word.model.stored.text.WordCombination

object WordCombinationModel
{
	// COMPUTED -------------------------------
	
	/**
	 * @return The table used by this model
	 */
	def table = WordTables.wordCombination
	
	
	// OTHER    ------------------------------
	
	/**
	 * @param data Word combination data
	 * @return A model matching that data
	 */
	def apply(data: WordCombinationData): WordCombinationModel = apply(None, Some(data.wordCount),
		data.baseCombinationId, Some(data.baseCombinationSide))
	
	/**
	 * Inserts multiple new word combinations to the DB
	 * @param data Data to insert
	 * @param connection DB Connection (implicit)
	 * @return Newly inserted combinations
	 */
	def insert(data: Seq[WordCombinationData])(implicit connection: Connection) =
	{
		val ids = Insert(table, data.map { apply(_).toModel }).generatedIntKeys
		ids.zip(data).map { case (id, data) => WordCombination(id, data) }
	}
}

/**
 * Used for interacting with word combinations in the DB
 * @author Mikko Hilpinen
 * @since 28.4.2021, v0.2
 */
case class WordCombinationModel(id: Option[Int] = None, wordCount: Option[Int] = None,
                                baseCombinationId: Option[Int] = None, baseCombinationSide: Option[WordSide] = None)
	extends Storable
{
	override def table = WordCombinationModel.table
	
	override def valueProperties = Vector("id" -> id, "baseCombinationId" -> baseCombinationId,
		"wordCount" -> wordCount, "baseCombinationSide" -> baseCombinationSide.map { _.id })
}