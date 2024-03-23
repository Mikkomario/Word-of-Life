package vf.word.database.access.many.bible.book_statement_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple verse beginning statement links at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbVerseBeginningStatementLinks extends ManyVerseBeginningStatementLinksAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted verse beginning statement links
	  * @return An access point to verse beginning statement links with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbVerseBeginningStatementLinksSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbVerseBeginningStatementLinksSubset(targetIds: Set[Int])
		 extends ManyVerseBeginningStatementLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

