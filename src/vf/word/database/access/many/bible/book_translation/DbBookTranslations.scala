package vf.word.database.access.many.bible.book_translation

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple book translations at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbBookTranslations extends ManyBookTranslationsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted book translations
	  * @return An access point to book translations with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbBookTranslationsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbBookTranslationsSubset(targetIds: Set[Int]) extends ManyBookTranslationsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

