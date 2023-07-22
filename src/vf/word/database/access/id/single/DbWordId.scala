package vf.word.database.access.id.single

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.column.SingleIntIdAccess
import vf.word.database.WordTables
import vf.word.database.model.text.WordModel
import vf.word.model.enumeration.Capitalization

/**
 * Used for accessing individual word ids
 * @author Mikko Hilpinen
 * @since 3.5.2021, v0.2
 */
object DbWordId extends SingleIntIdAccess
{
	// COMPUTED ----------------------------
	
	private def model = WordModel
	
	
	// IMPLEMENTED  ------------------------
	
	override def table = WordTables.word
	
	override def target = table
	
	override def globalCondition = None
	
	
	// OTHER    ----------------------------
	
	/**
	 * Finds a word id for a word
	 * @param word The word as it is spelled
	 * @param connection DB Connection
	 * @return Id of that word. None if that word is not registered to the database
	 */
	def apply(word: String)(implicit connection: Connection) =
	{
		val baseModel = model.withValue(word)
		find(baseModel.withCapitalization(Capitalization.of(word)).toCondition)
			.orElse { find(baseModel.toCondition) }
	}
}
