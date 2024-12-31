package vf.word.database.access.single.bible.translation.book.statement

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.BookStatementPlacementDbFactory
import vf.word.model.stored.bible.BookStatementPlacement

object UniqueBookStatementPlacementAccess extends ViewFactory[UniqueBookStatementPlacementAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueBookStatementPlacementAccess = 
		_UniqueBookStatementPlacementAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueBookStatementPlacementAccess(override val accessCondition: Option[Condition]) 
		extends UniqueBookStatementPlacementAccess
}

/**
  * A common trait for access points that return individual and distinct book statement 
  * placements.
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait UniqueBookStatementPlacementAccess 
	extends UniqueBookStatementPlacementAccessLike[BookStatementPlacement, UniqueBookStatementPlacementAccess] 
		with SingleRowModelAccess[BookStatementPlacement]
{
	// IMPLEMENTED	--------------------
	
	override def factory = BookStatementPlacementDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): UniqueBookStatementPlacementAccess = 
		UniqueBookStatementPlacementAccess(condition)
}

