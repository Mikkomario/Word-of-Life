package vf.word.database.access.many.bible.footnote_statement_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple footnote statement links at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbFootnoteStatementLinks extends ManyFootnoteStatementLinksAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted footnote statement links
	  * @return An access point to footnote statement links with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbFootnoteStatementLinksSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbFootnoteStatementLinksSubset(targetIds: Set[Int]) extends ManyFootnoteStatementLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

