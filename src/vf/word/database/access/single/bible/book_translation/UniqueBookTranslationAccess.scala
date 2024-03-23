package vf.word.database.access.single.bible.book_translation

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.BookTranslationDbFactory
import vf.word.model.stored.bible.BookTranslation

object UniqueBookTranslationAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueBookTranslationAccess = new _UniqueBookTranslationAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueBookTranslationAccess(condition: Condition) extends UniqueBookTranslationAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct book translations.
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait UniqueBookTranslationAccess 
	extends UniqueBookTranslationAccessLike[BookTranslation] with SingleRowModelAccess[BookTranslation] 
		with FilterableView[UniqueBookTranslationAccess]
{
	// IMPLEMENTED	--------------------
	
	override def factory = BookTranslationDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueBookTranslationAccess = 
		new UniqueBookTranslationAccess._UniqueBookTranslationAccess(mergeCondition(filterCondition))
}

