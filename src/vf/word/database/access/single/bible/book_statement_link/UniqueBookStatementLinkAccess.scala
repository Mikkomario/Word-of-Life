package vf.word.database.access.single.bible.book_statement_link

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.BookStatementLinkDbFactory
import vf.word.model.stored.bible.BookStatementLink

object UniqueBookStatementLinkAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueBookStatementLinkAccess =
		 new _UniqueBookStatementLinkAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueBookStatementLinkAccess(condition: Condition) extends UniqueBookStatementLinkAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct book statement links.
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait UniqueBookStatementLinkAccess 
	extends UniqueBookStatementLinkAccessLike[BookStatementLink] with SingleRowModelAccess[BookStatementLink] 
		with FilterableView[UniqueBookStatementLinkAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = BookStatementLinkDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueBookStatementLinkAccess = 
		new UniqueBookStatementLinkAccess._UniqueBookStatementLinkAccess(mergeCondition(filterCondition))
}

