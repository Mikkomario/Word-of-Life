package vf.word.database.access.many.bible.footnote

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteDbFactory
import vf.word.model.stored.bible.Footnote

object ManyFootnotesAccess
{
	// NESTED	--------------------
	
	private class ManyFootnotesSubView(condition: Condition) extends ManyFootnotesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
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
	
	override def filter(filterCondition: Condition): ManyFootnotesAccess = 
		new ManyFootnotesAccess.ManyFootnotesSubView(mergeCondition(filterCondition))
}

