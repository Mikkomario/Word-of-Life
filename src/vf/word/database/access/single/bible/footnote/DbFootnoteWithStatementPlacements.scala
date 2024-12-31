package vf.word.database.access.single.bible.footnote

import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteWithStatementPlacementsDbFactory
import vf.word.database.storable.bible.{FootnoteDbModel, FootnoteStatementPlacementDbModel}
import vf.word.model.combined.bible.FootnoteWithStatementPlacements

/**
  * Used for accessing individual footnotes with statement placements
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object DbFootnoteWithStatementPlacements 
	extends SingleModelAccess[FootnoteWithStatementPlacements] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with the linked placements
	  */
	protected def placementModel = FootnoteStatementPlacementDbModel
	
	/**
	  * A database model (factory) used for interacting with linked footnotes
	  */
	private def model = FootnoteDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteWithStatementPlacementsDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted footnote with statement placements
	  * @return An access point to that footnote with statement placements
	  */
	def apply(id: Int) = DbSingleFootnoteWithStatementPlacements(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should 
	  *                  yield unique footnotes with statement placements.
	  * @return An access point to the footnote with statement placements that satisfies the specified 
	  * condition
	  */
	private def distinct(condition: Condition) = UniqueFootnoteWithStatementPlacementsAccess(condition)
}

