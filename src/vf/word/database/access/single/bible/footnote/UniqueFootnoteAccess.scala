package vf.word.database.access.single.bible.footnote

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteDbFactory
import vf.word.model.stored.bible.Footnote

object UniqueFootnoteAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueFootnoteAccess = new _UniqueFootnoteAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueFootnoteAccess(condition: Condition) extends UniqueFootnoteAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct footnotes.
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait UniqueFootnoteAccess 
	extends UniqueFootnoteAccessLike[Footnote] with SingleRowModelAccess[Footnote] 
		with FilterableView[UniqueFootnoteAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueFootnoteAccess = 
		new UniqueFootnoteAccess._UniqueFootnoteAccess(mergeCondition(filterCondition))
}

