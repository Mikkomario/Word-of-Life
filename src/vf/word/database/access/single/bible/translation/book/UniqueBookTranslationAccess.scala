package vf.word.database.access.single.bible.translation.book

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.BookTranslationDbFactory
import vf.word.model.stored.bible.BookTranslation

object UniqueBookTranslationAccess extends ViewFactory[UniqueBookTranslationAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueBookTranslationAccess = 
		_UniqueBookTranslationAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueBookTranslationAccess(override val accessCondition: Option[Condition]) 
		extends UniqueBookTranslationAccess
}

/**
  * A common trait for access points that return individual and distinct book translations.
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait UniqueBookTranslationAccess 
	extends UniqueBookTranslationAccessLike[BookTranslation, UniqueBookTranslationAccess] 
		with SingleRowModelAccess[BookTranslation]
{
	// IMPLEMENTED	--------------------
	
	override def factory = BookTranslationDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): UniqueBookTranslationAccess = 
		UniqueBookTranslationAccess(condition)
}

