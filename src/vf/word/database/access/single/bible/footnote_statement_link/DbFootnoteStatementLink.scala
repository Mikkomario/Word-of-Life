package vf.word.database.access.single.bible.footnote_statement_link

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteStatementLinkDbFactory
import vf.word.database.storable.bible.FootnoteStatementLinkModel
import vf.word.model.stored.bible.FootnoteStatementLink

/**
  * Used for accessing individual footnote statement links
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbFootnoteStatementLink 
	extends SingleRowModelAccess[FootnoteStatementLink] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = FootnoteStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteStatementLinkDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted footnote statement link
	  * @return An access point to that footnote statement link
	  */
	def apply(id: Int) = DbSingleFootnoteStatementLink(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique footnote statement links.
	  * @return An access point to the footnote statement link that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = 
		UniqueFootnoteStatementLinkAccess(mergeCondition(condition))
}

