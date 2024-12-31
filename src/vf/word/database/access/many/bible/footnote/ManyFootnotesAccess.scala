package vf.word.database.access.many.bible.footnote

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteDbFactory
import vf.word.model.stored.bible.Footnote

object ManyFootnotesAccess extends ViewFactory[ManyFootnotesAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyFootnotesAccess = _ManyFootnotesAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private class ManyFootnotesSubView(condition: Condition) extends ManyFootnotesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
	
	private case class _ManyFootnotesAccess(override val accessCondition: Option[Condition]) 
		extends ManyFootnotesAccess
}

/**
  * A common trait for access points which target multiple footnotes at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait ManyFootnotesAccess 
	extends ManyFootnotesAccessLike[Footnote, ManyFootnotesAccess] with ManyRowModelAccess[Footnote]
{
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyFootnotesAccess = ManyFootnotesAccess(condition)
	
	override def filter(filterCondition: Condition): ManyFootnotesAccess = 
		new ManyFootnotesAccess.ManyFootnotesSubView(mergeCondition(filterCondition))
}

