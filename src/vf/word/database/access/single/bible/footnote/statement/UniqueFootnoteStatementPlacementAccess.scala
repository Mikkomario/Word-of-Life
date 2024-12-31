package vf.word.database.access.single.bible.footnote.statement

import utopia.logos.database.access.single.text.statement.placement.UniqueStatementPlacementAccessLike
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteStatementPlacementDbFactory
import vf.word.database.storable.bible.FootnoteStatementPlacementDbModel
import vf.word.model.stored.bible.FootnoteStatementPlacement

object UniqueFootnoteStatementPlacementAccess extends ViewFactory[UniqueFootnoteStatementPlacementAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueFootnoteStatementPlacementAccess = 
		_UniqueFootnoteStatementPlacementAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private
		 case class _UniqueFootnoteStatementPlacementAccess(override val accessCondition: Option[Condition]) 
		extends UniqueFootnoteStatementPlacementAccess
}

/**
  * A common trait for access points that return individual and distinct footnote statement 
  * placements.
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait UniqueFootnoteStatementPlacementAccess 
	extends UniqueStatementPlacementAccessLike[FootnoteStatementPlacement, 
		UniqueFootnoteStatementPlacementAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteStatementPlacementDbFactory
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	override protected def model = FootnoteStatementPlacementDbModel
	
	override protected def self = this
	
	override def apply(condition: Condition): UniqueFootnoteStatementPlacementAccess = 
		UniqueFootnoteStatementPlacementAccess(condition)
}

