package vf.word.model.partial.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import vf.word.model.factory.bible.BookStatementLinkFactory

object BookStatementLinkData extends FromModelFactoryWithSchema[BookStatementLinkData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("bookId", IntType, Vector("book_id")), 
			PropertyDeclaration("statementId", IntType, Vector("statement_id")), 
			PropertyDeclaration("orderIndex", IntType, Vector("order_index"))))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		BookStatementLinkData(valid("bookId").getInt, valid("statementId").getInt, valid("orderIndex").getInt)
}

/**
  * Lists the statements made within a book
  * @param bookId Id of the book where the statement appears
  * @param statementId Id of the statement made
  * @param orderIndex Index that indicates, where in the book the linked statement appears
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class BookStatementLinkData(bookId: Int, statementId: Int, orderIndex: Int) 
	extends BookStatementLinkFactory[BookStatementLinkData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("bookId" -> bookId, "statementId" -> statementId, "orderIndex" -> orderIndex))
	
	override def withBookId(bookId: Int) = copy(bookId = bookId)
	
	override def withOrderIndex(orderIndex: Int) = copy(orderIndex = orderIndex)
	
	override def withStatementId(statementId: Int) = copy(statementId = statementId)
}

