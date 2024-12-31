package vf.word.database.access.many.bible.translation.book.statement

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.BookStatementPlacementDbFactory
import vf.word.model.stored.bible.BookStatementPlacement

object ManyBookStatementPlacementsAccess extends ViewFactory[ManyBookStatementPlacementsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyBookStatementPlacementsAccess = 
		_ManyBookStatementPlacementsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyBookStatementPlacementsAccess(override val accessCondition: Option[Condition]) 
		extends ManyBookStatementPlacementsAccess
}

/**
  * A common trait for access points which target multiple book statement placements at a time
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait ManyBookStatementPlacementsAccess 
	extends ManyBookStatementPlacementsAccessLike[BookStatementPlacement, ManyBookStatementPlacementsAccess] 
		with ManyRowModelAccess[BookStatementPlacement]
{
	// IMPLEMENTED	--------------------
	
	override def factory = BookStatementPlacementDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyBookStatementPlacementsAccess = 
		ManyBookStatementPlacementsAccess(condition)
}

