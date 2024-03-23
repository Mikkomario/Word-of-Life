package vf.word.database.access.many.bible.book_translation

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple contextual books at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbContextualBooks extends ManyContextualBooksAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted contextual books
	  * @return An access point to contextual books with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbContextualBooksSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbContextualBooksSubset(targetIds: Set[Int]) extends ManyContextualBooksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

