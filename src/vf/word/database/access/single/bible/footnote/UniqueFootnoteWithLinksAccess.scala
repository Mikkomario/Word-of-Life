package vf.word.database.access.single.bible.footnote

import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteWithLinksDbFactory
import vf.word.database.storable.bible.FootnoteStatementLinkModel
import vf.word.model.combined.bible.FootnoteWithLinks

object UniqueFootnoteWithLinksAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueFootnoteWithLinksAccess =
		 new _UniqueFootnoteWithLinksAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueFootnoteWithLinksAccess(condition: Condition) extends UniqueFootnoteWithLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return distinct footnote with linkses
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait UniqueFootnoteWithLinksAccess 
	extends UniqueFootnoteAccessLike[FootnoteWithLinks] with FilterableView[UniqueFootnoteWithLinksAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with the linked links
	  */
	protected def linkModel = FootnoteStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteWithLinksDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueFootnoteWithLinksAccess = 
		new UniqueFootnoteWithLinksAccess._UniqueFootnoteWithLinksAccess(mergeCondition(filterCondition))
}

