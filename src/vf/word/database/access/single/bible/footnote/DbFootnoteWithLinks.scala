package vf.word.database.access.single.bible.footnote

import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteWithLinksDbFactory
import vf.word.database.storable.bible.{FootnoteModel, FootnoteStatementLinkModel}
import vf.word.model.combined.bible.FootnoteWithLinks

/**
  * Used for accessing individual footnote with linkses
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbFootnoteWithLinks extends SingleModelAccess[FootnoteWithLinks] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with linked footnotes
	  */
	protected def model = FootnoteModel
	
	/**
	  * A database model (factory) used for interacting with the linked links
	  */
	protected def linkModel = FootnoteStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteWithLinksDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted footnote with links
	  * @return An access point to that footnote with links
	  */
	def apply(id: Int) = DbSingleFootnoteWithLinks(id)
	
	/**
	  * 
		@param condition Filter condition to apply in addition to this root view's condition. Should yield unique footnote 
	  * with linkses.
	  * @return An access point to the footnote with links that satisfies the specified condition
	  */
	protected
		 def filterDistinct(condition: Condition) = UniqueFootnoteWithLinksAccess(mergeCondition(condition))
}

