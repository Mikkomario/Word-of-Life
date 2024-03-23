package vf.word.database.access.many.bible.book_translation

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.BookTranslationDbFactory
import vf.word.model.stored.bible.BookTranslation

object ManyBookTranslationsAccess
{
	// NESTED	--------------------
	
	private class ManyBookTranslationsSubView(condition: Condition) extends ManyBookTranslationsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple book translations at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait ManyBookTranslationsAccess 
	extends ManyBookTranslationsAccessLike[BookTranslation, ManyBookTranslationsAccess] 
		with ManyRowModelAccess[BookTranslation]
{
	// IMPLEMENTED	--------------------
	
	override def factory = BookTranslationDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyBookTranslationsAccess = 
		new ManyBookTranslationsAccess.ManyBookTranslationsSubView(mergeCondition(filterCondition))
}

