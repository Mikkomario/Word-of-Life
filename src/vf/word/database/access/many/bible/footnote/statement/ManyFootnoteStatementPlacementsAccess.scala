package vf.word.database.access.many.bible.footnote.statement

import utopia.flow.collection.immutable.IntSet
import utopia.flow.generic.casting.ValueConversions._
import utopia.logos.database.access.many.text.statement.placement.ManyStatementPlacementsAccessLike
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteStatementPlacementDbFactory
import vf.word.database.storable.bible.FootnoteStatementPlacementDbModel
import vf.word.model.stored.bible.FootnoteStatementPlacement

object ManyFootnoteStatementPlacementsAccess extends ViewFactory[ManyFootnoteStatementPlacementsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyFootnoteStatementPlacementsAccess = 
		_ManyFootnoteStatementPlacementsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyFootnoteStatementPlacementsAccess(override val accessCondition: Option[Condition])
		extends ManyFootnoteStatementPlacementsAccess
}

/**
  * A common trait for access points which target multiple footnote statement placements at a time
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait ManyFootnoteStatementPlacementsAccess 
	extends ManyStatementPlacementsAccessLike[FootnoteStatementPlacement, ManyFootnoteStatementPlacementsAccess]
		with ManyRowModelAccess[FootnoteStatementPlacement]
{
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteStatementPlacementDbFactory
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	override protected def model = FootnoteStatementPlacementDbModel
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyFootnoteStatementPlacementsAccess = 
		ManyFootnoteStatementPlacementsAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * @param parentId parent id to target
	  * @return Copy of this access point that only includes footnote statement placements with the specified 
	  * parent id
	  */
	def inFootnote(parentId: Int) = filter(model.parentId.column <=> parentId)
	/**
	  * @param parentIds Targeted parent ids
	  * @return Copy of this access point that only includes footnote statement placements where parent id is 
	  * within the specified value set
	  */
	def inFootnotes(parentIds: IterableOnce[Int]) =
		filter(model.parentId.column.in(IntSet.from(parentIds)))
}

