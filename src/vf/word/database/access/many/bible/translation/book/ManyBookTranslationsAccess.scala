package vf.word.database.access.many.bible.translation.book

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.BookTranslationDbFactory
import vf.word.model.stored.bible.BookTranslation

object ManyBookTranslationsAccess extends ViewFactory[ManyBookTranslationsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyBookTranslationsAccess = 
		_ManyBookTranslationsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyBookTranslationsAccess(override val accessCondition: Option[Condition]) 
		extends ManyBookTranslationsAccess
}

/**
  * A common trait for access points which target multiple book translations at a time
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait ManyBookTranslationsAccess 
	extends ManyBookTranslationsAccessLike[BookTranslation, ManyBookTranslationsAccess] 
		with ManyRowModelAccess[BookTranslation]
{
	// IMPLEMENTED	--------------------
	
	override def factory = BookTranslationDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyBookTranslationsAccess = 
		ManyBookTranslationsAccess(condition)
}

