package vf.word.model.partial.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import vf.word.model.factory.bible.FootnoteFactory

object FootnoteData extends FromModelFactoryWithSchema[FootnoteData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("commentedStatementId", IntType, 
			Vector("commented_statement_id")), PropertyDeclaration("targetedWordIndex", IntType, 
			Vector("targeted_word_index"), isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		FootnoteData(valid("commentedStatementId").getInt, valid("targetedWordIndex").int)
}

/**
  * Represents a foot note made within the original text, 
  * concerning a specific word or a statement within the test
  * @param commentedStatementId Id of the specific statement this footnote comments on
  * @param targetedWordIndex A 0-based index that specifies the word targeted within this statement. None if
  *  no specific word was targeted.
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class FootnoteData(commentedStatementId: Int, targetedWordIndex: Option[Int] = None) 
	extends FootnoteFactory[FootnoteData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("commentedStatementId" -> commentedStatementId, 
			"targetedWordIndex" -> targetedWordIndex))
	
	override def withCommentedStatementId(commentedStatementId: Int) = 
		copy(commentedStatementId = commentedStatementId)
	
	override
		 def withTargetedWordIndex(targetedWordIndex: Int) = copy(targetedWordIndex = Some(targetedWordIndex))
}

