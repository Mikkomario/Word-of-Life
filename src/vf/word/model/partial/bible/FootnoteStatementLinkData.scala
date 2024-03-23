package vf.word.model.partial.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import vf.word.model.factory.bible.FootnoteStatementLinkFactory

object FootnoteStatementLinkData extends FromModelFactoryWithSchema[FootnoteStatementLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("footnoteId", IntType, Vector("footnote_id")), 
			PropertyDeclaration("statementId", IntType, Vector("statement_id")), 
			PropertyDeclaration("orderIndex", IntType, Vector("order_index"))))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		FootnoteStatementLinkData(valid("footnoteId").getInt, valid("statementId").getInt, 
			valid("orderIndex").getInt)
}

/**
  * Links a footnote to a statement made within it
  * @param footnoteId Id of the footnote where the statement is made
  * @param statementId Id of the statement made within the footnote
  * @param orderIndex A 0-based index that determines where the statement appears within the footnote
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class FootnoteStatementLinkData(footnoteId: Int, statementId: Int, orderIndex: Int) 
	extends FootnoteStatementLinkFactory[FootnoteStatementLinkData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("footnoteId" -> footnoteId, "statementId" -> statementId, "orderIndex" -> orderIndex))
	
	override def withFootnoteId(footnoteId: Int) = copy(footnoteId = footnoteId)
	
	override def withOrderIndex(orderIndex: Int) = copy(orderIndex = orderIndex)
	
	override def withStatementId(statementId: Int) = copy(statementId = statementId)
}

