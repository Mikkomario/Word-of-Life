package vf.word.database.access.single.bible.footnote

import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteWithStatementPlacementsDbFactory
import vf.word.database.storable.bible.FootnoteStatementPlacementDbModel
import vf.word.model.combined.bible.FootnoteWithStatementPlacements

object UniqueFootnoteWithStatementPlacementsAccess 
	extends ViewFactory[UniqueFootnoteWithStatementPlacementsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueFootnoteWithStatementPlacementsAccess = 
		_UniqueFootnoteWithStatementPlacementsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private
		 case class _UniqueFootnoteWithStatementPlacementsAccess(override val accessCondition: Option[Condition]) 
		extends UniqueFootnoteWithStatementPlacementsAccess
}

/**
  * A common trait for access points that return distinct footnotes with statement placements
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait UniqueFootnoteWithStatementPlacementsAccess 
	extends UniqueFootnoteAccessLike[FootnoteWithStatementPlacements, 
		UniqueFootnoteWithStatementPlacementsAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with the linked placements
	  */
	protected def placementModel = FootnoteStatementPlacementDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteWithStatementPlacementsDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): UniqueFootnoteWithStatementPlacementsAccess = 
		UniqueFootnoteWithStatementPlacementsAccess(condition)
}

