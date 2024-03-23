package vf.word.database.access.many.bible.footnote

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple footnote with linkses at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbFootnoteWithLinkses extends ManyFootnoteWithLinksesAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted footnote with linkses
	  * @return An access point to footnote with linkses with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbFootnoteWithLinksesSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbFootnoteWithLinksesSubset(targetIds: Set[Int]) extends ManyFootnoteWithLinksesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

