package vf.word.database.model.text

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.Storable
import utopia.vault.model.template.DataInserter
import vf.word.database.WordTables
import vf.word.model.partial.text.WordCombinationWordData
import vf.word.model.stored.text.WordCombinationWord

object WordCombinationWordModel
	extends DataInserter[WordCombinationWordModel, WordCombinationWord, WordCombinationWordData]
{
	// IMPLEMENTED ----------------------------
	
	override def table = WordTables.wordCombinationWord
	
	override def apply(data: WordCombinationWordData): WordCombinationWordModel = apply(None, Some(data.wordId),
		Some(data.combinationId), Some(data.orderIndex))
	
	override protected def complete(id: Value, data: WordCombinationWordData) = WordCombinationWord(id.getInt, data)
}

/**
 * Used for interacting with word combination word assignments in the DB
 * @author Mikko Hilpinen
 * @since 30.4.2021, v0.2
 */
case class WordCombinationWordModel(id: Option[Int] = None, wordId: Option[Int] = None,
                                    combinationId: Option[Int] = None, orderIndex: Option[Int] = None)
	extends Storable
{
	override def table = WordCombinationWordModel.table
	
	override def valueProperties = Vector("id" -> id, "wordId" -> wordId, "combinationId" -> combinationId,
		"orderIndex" -> orderIndex)
}
