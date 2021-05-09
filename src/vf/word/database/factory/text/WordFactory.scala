package vf.word.database.factory.text

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.flow.generic.ValueUnwraps._
import utopia.vault.nosql.factory.FromValidatedRowModelFactory
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
	
	override protected def fromValidatedModel(model: Model[Constant]) = Word(model("id"), model("value"),
		Capitalization.forId(model("capitalization")).getOrElse(Normal))
}
