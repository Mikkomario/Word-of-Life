package vf.word.database.access.many.bible.translation

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple translations at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbTranslations extends ManyTranslationsAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted translations
	  * @return An access point to translations with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbTranslationsSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbTranslationsSubset(targetIds: Set[Int]) extends ManyTranslationsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

