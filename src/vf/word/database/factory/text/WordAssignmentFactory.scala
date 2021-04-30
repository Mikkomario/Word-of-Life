package vf.word.database.factory.text

import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.flow.generic.ValueUnwraps._
import utopia.vault.nosql.factory.FromValidatedRowModelFactory
import vf.word.database.WordTables
import vf.word.model.cached.Location
import vf.word.model.partial.text.WordAssignmentData
import vf.word.model.stored.text.WordAssignment

/**
 * Used for reading word assignment data from the DB
 * @author Mikko Hilpinen
 * @since 30.4.2021, v0.2
 */
object WordAssignmentFactory extends FromValidatedRowModelFactory[WordAssignment]
{
	override def table = WordTables.wordAssignment
	
	override protected def fromValidatedModel(model: Model[Constant]) = WordAssignment(model("id"),
		WordAssignmentData(model("wordId"), Location(model("locationId"), model("orderIndex"))))
}
