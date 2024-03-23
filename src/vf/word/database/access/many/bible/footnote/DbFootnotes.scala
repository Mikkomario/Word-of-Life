package vf.word.database.access.many.bible.footnote

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple footnotes at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbFootnotes extends ManyFootnotesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted footnotes
	  * @return An access point to footnotes with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbFootnotesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbFootnotesSubset(targetIds: Set[Int]) extends ManyFootnotesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

