package vf.word.database.access.single.bible.book_statement_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.word.database.storable.bible.BookStatementLinkModel

/**
  * A common trait for access points which target individual book statement links or similar items at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait UniqueBookStatementLinkAccessLike[+A] 
	extends SingleModelAccess[A] with DistinctModelAccess[A, Option[A], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the book where the statement appears. None if no book statement link (or value) was found.
	  */
	def bookId(implicit connection: Connection) = pullColumn(model.bookId.column).int
	
	/**
	  * Id of the statement made. None if no book statement link (or value) was found.
	  */
	def statementId(implicit connection: Connection) = pullColumn(model.statementId.column).int
	
	/**
	  * Index that indicates, 
	  * where in the book the linked statement appears. None if no book statement link (or value) was found.
	  */
	def orderIndex(implicit connection: Connection) = pullColumn(model.orderIndex.column).int
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = BookStatementLinkModel
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the book ids of the targeted book statement links
	  * @param newBookId A new book id to assign
	  * @return Whether any book statement link was affected
	  */
	def bookId_=(newBookId: Int)(implicit connection: Connection) = putColumn(model.bookId.column, newBookId)
	
	/**
	  * Updates the order indexs of the targeted book statement links
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any book statement link was affected
	  */
	def orderIndex_=(newOrderIndex: Int)(implicit connection: Connection) = 
		putColumn(model.orderIndex.column, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted book statement links
	  * @param newStatementId A new statement id to assign
	  * @return Whether any book statement link was affected
	  */
	def statementId_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(model.statementId.column, newStatementId)
}

