package vf.word.database.access.single.bible.footnote

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteDbFactory
import vf.word.model.stored.bible.Footnote

object UniqueFootnoteAccess extends ViewFactory[UniqueFootnoteAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueFootnoteAccess = _UniqueFootnoteAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueFootnoteAccess(override val accessCondition: Option[Condition]) 
		extends UniqueFootnoteAccess
}

/**
  * A common trait for access points that return individual and distinct footnotes.
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait UniqueFootnoteAccess 
	extends UniqueFootnoteAccessLike[Footnote, UniqueFootnoteAccess] with SingleRowModelAccess[Footnote]
{
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteDbFactory
	override protected def self = this
	
	override def apply(condition: Condition): UniqueFootnoteAccess = UniqueFootnoteAccess(condition)
}

