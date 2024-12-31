package vf.word.model.partial.bible

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.logos.model.partial.text.{StatementPlacementData, StatementPlacementDataLike}
import vf.word.model.factory.bible.FootnoteStatementPlacementFactory

object FootnoteStatementPlacementData extends FromModelFactoryWithSchema[FootnoteStatementPlacementData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("parentId", IntType, Single("parent_id")), 
			PropertyDeclaration("statementId", IntType, Single("statement_id")), 
			PropertyDeclaration("orderIndex", IntType, Single("order_index"), 0)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		FootnoteStatementPlacementData(valid("parentId").getInt, valid("statementId").getInt, 
			valid("orderIndex").getInt)
}

/**
  * Links a footnote to a statement made within it
  * @param parentId    Id of the footnote where the statement is made
  * @param statementId Id of the statement which appears within the linked text
  * @param orderIndex  0-based index that indicates the specific location of the placed text
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class FootnoteStatementPlacementData(parentId: Int, statementId: Int, orderIndex: Int = 0) 
	extends FootnoteStatementPlacementFactory[FootnoteStatementPlacementData] with StatementPlacementData 
		with StatementPlacementDataLike[FootnoteStatementPlacementData]
{
	// IMPLEMENTED	--------------------
	
	override def copyStatementPlacement(parentId: Int, statementId: Int, orderIndex: Int) = 
		copy(parentId = parentId, statementId = statementId, orderIndex = orderIndex)
}

