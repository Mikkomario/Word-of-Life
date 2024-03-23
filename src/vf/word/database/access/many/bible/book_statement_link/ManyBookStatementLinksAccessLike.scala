package vf.word.database.access.many.bible.book_statement_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.word.database.storable.bible.BookStatementLinkModel

/**
  * A common trait for access points which target multiple book statement links or similar instances at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait ManyBookStatementLinksAccessLike[+A, +Repr] 
	extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * book ids of the accessible book statement links
	  */
	def bookIds(implicit connection: Connection) = pullColumn(model.bookId.column).map { v => v.getInt }
	
	/**
	  * statement ids of the accessible book statement links
	  */
	def statementIds(implicit connection: Connection) = pullColumn(model.statementId.column)
		.map { v => v.getInt }
	
	/**
	  * order indices of the accessible book statement links
	  */
	def orderIndices(implicit connection: Connection) = pullColumn(model.orderIndex.column)
		.map { v => v.getInt }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = BookStatementLinkModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param orderIndex order index to target
	  * @return Copy of this access point that only includes book statement links 
		with the specified order index
	  */
	def at(orderIndex: Int) = filter(model.orderIndex.column <=> orderIndex)
	
	/**
	  * @param orderIndices Targeted order indices
	  * @return Copy of this access point that only includes book statement links where order index is within
	  *  the specified value set
	  */
	def atIndices(orderIndices: Iterable[Int]) = filter(model.orderIndex.column.in(orderIndices))
	
	/**
	  * Updates the book ids of the targeted book statement links
	  * @param newBookId A new book id to assign
	  * @return Whether any book statement link was affected
	  */
	def bookIds_=(newBookId: Int)(implicit connection: Connection) = putColumn(model.bookId.column, newBookId)
	
	/**
	  * @param bookId book id to target
	  * @return Copy of this access point that only includes book statement links with the specified book id
	  */
	def inBook(bookId: Int) = filter(model.bookId.column <=> bookId)
	
	/**
	  * @param bookIds Targeted book ids
	  * @return Copy of this access point that only includes book statement links where book id is within
	  *  the specified value set
	  */
	def inBooks(bookIds: Iterable[Int]) = filter(model.bookId.column.in(bookIds))
	
	/**
	  * Updates the order indices of the targeted book statement links
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any book statement link was affected
	  */
	def orderIndices_=(newOrderIndex: Int)(implicit connection: Connection) = 
		putColumn(model.orderIndex.column, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted book statement links
	  * @param newStatementId A new statement id to assign
	  * @return Whether any book statement link was affected
	  */
	def statementIds_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(model.statementId.column, newStatementId)
	
	/**
	  * @param statementId statement id to target
	  * @return Copy of this access point that only includes book statement links 
		with the specified statement id
	  */
	def toStatement(statementId: Int) = filter(model.statementId.column <=> statementId)
	
	/**
	  * @param statementIds Targeted statement ids
	  * @return Copy of this access point that only includes book statement links where statement id is within
	  *  the specified value set
	  */
	def toStatements(statementIds: Iterable[Int]) = filter(model.statementId.column.in(statementIds))
}

