package vf.word.model.partial.bible

import utopia.flow.collection.immutable.Single
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.logos.model.partial.text.{StatementPlacementData, StatementPlacementDataLike}
import vf.word.model.factory.bible.BookStatementPlacementFactory

object BookStatementPlacementData extends FromModelFactoryWithSchema[BookStatementPlacementData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("bookId", IntType, Vector("book_id", "parentId", 
			"parent_id")), PropertyDeclaration("statementId", IntType, Single("statement_id")), 
			PropertyDeclaration("orderIndex", IntType, Single("order_index"), 0)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		BookStatementPlacementData(valid("bookId").getInt, valid("statementId").getInt, 
			valid("orderIndex").getInt)
}

/**
  * Links a statement to a book in which it is made
  * @param bookId      Id of the book where the statement appears
  * @param statementId Id of the statement which appears within the linked text
  * @param orderIndex  0-based index that indicates the specific location of the placed text
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class BookStatementPlacementData(bookId: Int, statementId: Int, orderIndex: Int = 0) 
	extends BookStatementPlacementFactory[BookStatementPlacementData] with StatementPlacementData 
		with StatementPlacementDataLike[BookStatementPlacementData]
{
	// IMPLEMENTED	--------------------
	
	override def parentId = bookId
	
	override def copyStatementPlacement(parentId: Int, statementId: Int, orderIndex: Int) = 
		copy(bookId = parentId, statementId = statementId, orderIndex = orderIndex)
	
	override def withBookId(bookId: Int) = copy(bookId = bookId)
}

