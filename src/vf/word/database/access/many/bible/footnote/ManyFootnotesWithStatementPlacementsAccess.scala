package vf.word.database.access.many.bible.footnote

import utopia.vault.database.Connection
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteWithStatementPlacementsDbFactory
import vf.word.database.storable.bible.FootnoteStatementPlacementDbModel
import vf.word.model.combined.bible.FootnoteWithStatementPlacements

object ManyFootnotesWithStatementPlacementsAccess 
	extends ViewFactory[ManyFootnotesWithStatementPlacementsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyFootnotesWithStatementPlacementsAccess = 
		_ManyFootnotesWithStatementPlacementsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private
		 case class _ManyFootnotesWithStatementPlacementsAccess(override val accessCondition: Option[Condition]) 
		extends ManyFootnotesWithStatementPlacementsAccess
}

/**
  * A common trait for access points that return multiple footnotes with statement placements at 
  * a time
  * @author Mikko Hilpinen
  * @since 30.12.2024
  */
trait ManyFootnotesWithStatementPlacementsAccess 
	extends ManyFootnotesAccessLike[FootnoteWithStatementPlacements, 
		ManyFootnotesWithStatementPlacementsAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * parent ids of the accessible footnote statement placements
	  */
	def placementParentIds(implicit connection: Connection) = 
		pullColumn(placementModel.parentId.column).map { v => v.getInt }
	
	/**
	  * statement ids of the accessible footnote statement placements
	  */
	def placementStatementIds(implicit connection: Connection) = 
		pullColumn(placementModel.statementId.column).map { v => v.getInt }
	
	/**
	  * order indices of the accessible footnote statement placements
	  */
	def placementOrderIndices(implicit connection: Connection) = 
		pullColumn(placementModel.orderIndex.column).map { v => v.getInt }
	
	/**
	  * Model (factory) used for interacting the footnote statement placements associated with this 
	  * footnote with statement placements
	  */
	protected def placementModel = FootnoteStatementPlacementDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteWithStatementPlacementsDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyFootnotesWithStatementPlacementsAccess = 
		ManyFootnotesWithStatementPlacementsAccess(condition)
}

