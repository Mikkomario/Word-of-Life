package vf.word.database.factory.text

import utopia.flow.generic.casting.ValueUnwraps._
import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
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
	
	// TODO: Change to use orderIndex -property (?)
	override def defaultOrdering: Option[OrderBy] = None
	
	override protected def fromValidatedModel(model: Model) = WordAssignment(model("id"),
		WordAssignmentData(model("wordId"), Location(model("locationId"), model("orderIndex"))))
}
