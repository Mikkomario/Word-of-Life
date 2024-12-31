package vf.word.database.storable.bible

import utopia.flow.generic.model.immutable.Value
import utopia.logos.database.props.text.StatementPlacementDbProps
import utopia.logos.database.storable.text.{StatementPlacementDbModel, StatementPlacementDbModelFactoryLike, StatementPlacementDbModelLike}
import utopia.vault.model.immutable.DbPropertyDeclaration
import vf.word.database.WordOfLifeTables
import vf.word.model.factory.bible.BookStatementPlacementFactory
import vf.word.model.partial.bible.BookStatementPlacementData
import vf.word.model.stored.bible.BookStatementPlacement

/**
  * Used for constructing BookStatementPlacementDbModel instances and for inserting book 
  * statement placements to the database
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object BookStatementPlacementDbModel 
	extends StatementPlacementDbModelFactoryLike[BookStatementPlacementDbModel, BookStatementPlacement, BookStatementPlacementData] 
		with BookStatementPlacementFactory[BookStatementPlacementDbModel] with StatementPlacementDbProps
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with book ids
	  */
	lazy val bookId = property("bookId")
	
	/**
	  * Database property used for interacting with statement ids
	  */
	override lazy val statementId = property("statementId")
	
	/**
	  * Database property used for interacting with order indices
	  */
	override lazy val orderIndex = property("orderIndex")
	
	
	// IMPLEMENTED	--------------------
	
	override def parentId = bookId
	
	override def table = WordOfLifeTables.bookStatementPlacement
	
	override def apply(data: BookStatementPlacementData): BookStatementPlacementDbModel = 
		apply(None, Some(data.bookId), Some(data.statementId), Some(data.orderIndex))
	
	/**
	  * @param bookId Id of the book where the statement appears
	  * @return A model containing only the specified book id
	  */
	override def withBookId(bookId: Int) = apply(bookId = Some(bookId))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param orderIndex 0-based index that indicates the specific location of the placed text
	  * @return A model containing only the specified order index
	  */
	override def withOrderIndex(orderIndex: Int) = apply(orderIndex = Some(orderIndex))
	
	/**
	  * @param statementId Id of the statement which appears within the linked text
	  * @return A model containing only the specified statement id
	  */
	override def withStatementId(statementId: Int) = apply(statementId = Some(statementId))
	
	override protected def complete(id: Value, data: BookStatementPlacementData) = 
		BookStatementPlacement(id.getInt, data)
}

/**
  * Used for interacting with BookStatementPlacements in the database
  * @param id book statement placement database id
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class BookStatementPlacementDbModel(id: Option[Int] = None, bookId: Option[Int] = None, 
	statementId: Option[Int] = None, orderIndex: Option[Int] = None) 
	extends StatementPlacementDbModel with StatementPlacementDbModelLike[BookStatementPlacementDbModel] 
		with BookStatementPlacementFactory[BookStatementPlacementDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def dbProps = BookStatementPlacementDbModel
	
	override def parentId = bookId
	
	override def table = BookStatementPlacementDbModel.table
	
	/**
	  * @param id          Id to assign to the new model (default = currently assigned id)
	  * @param parentId    parent id to assign to the new model (default = currently assigned value)
	  * @param statementId statement id to assign to the new model (default = currently assigned 
	  *                    value)
	  * @param orderIndex  order index to assign to the new model (default = currently assigned value)
	  */
	override def copyStatementPlacement(id: Option[Int] = id, parentId: Option[Int] = parentId, 
		statementId: Option[Int] = statementId, orderIndex: Option[Int] = orderIndex) = 
		copy(id = id, bookId = parentId, statementId = statementId, orderIndex = orderIndex)
	
	/**
	  * @param bookId Id of the book where the statement appears
	  * @return A new copy of this model with the specified book id
	  */
	override def withBookId(bookId: Int) = copy(bookId = Some(bookId))
}

