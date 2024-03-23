package vf.word.database.access.many.bible.book_statement_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple book statement links at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbBookStatementLinks extends ManyBookStatementLinksAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted book statement links
	  * @return An access point to book statement links with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbBookStatementLinksSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbBookStatementLinksSubset(targetIds: Set[Int]) extends ManyBookStatementLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

