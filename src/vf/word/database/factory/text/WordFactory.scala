package vf.word.database.factory.text

import utopia.flow.generic.casting.ValueUnwraps._
import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
import vf.word.database.WordTables
import vf.word.model.enumeration.Capitalization
import vf.word.model.enumeration.Capitalization.Normal
import vf.word.model.stored.text.Word

/**
 * Used for reading word data from the DB
 * @author Mikko Hilpinen
 * @since 8.5.2021, v0.2
 */
object WordFactory extends FromValidatedRowModelFactory[Word]
{
	override def table = WordTables.word
	
	override def defaultOrdering: Option[OrderBy] = None
	
	override protected def fromValidatedModel(model: Model) = Word(model("id"), model("value"),
		Capitalization.forId(model("capitalization")).getOrElse(Normal))
}
