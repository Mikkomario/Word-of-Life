package vf.word.database.access.single.bible.book_translation

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.BookTranslationDbFactory
import vf.word.database.storable.bible.BookTranslationModel
import vf.word.model.stored.bible.BookTranslation

/**
  * Used for accessing individual book translations
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbBookTranslation extends SingleRowModelAccess[BookTranslation] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = BookTranslationModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = BookTranslationDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted book translation
	  * @return An access point to that book translation
	  */
	def apply(id: Int) = DbSingleBookTranslation(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique book translations.
	  * @return An access point to the book translation that satisfies the specified condition
	  */
	protected
		 def filterDistinct(condition: Condition) = UniqueBookTranslationAccess(mergeCondition(condition))
}

