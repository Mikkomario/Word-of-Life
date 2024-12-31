package vf.word.database.access.single.bible.footnote.statement

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteStatementPlacementDbFactory
import vf.word.database.storable.bible.FootnoteStatementPlacementDbModel
import vf.word.model.stored.bible.FootnoteStatementPlacement

/**
  * Used for accessing individual footnote statement placements
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object DbFootnoteStatementPlacement 
	extends SingleRowModelAccess[FootnoteStatementPlacement] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = FootnoteStatementPlacementDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteStatementPlacementDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted footnote statement placement
	  * @return An access point to that footnote statement placement
	  */
	def apply(id: Int) = DbSingleFootnoteStatementPlacement(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should 
	  *                  yield unique footnote statement placements.
	  * @return An access point to the footnote statement placement that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueFootnoteStatementPlacementAccess(condition)
}

