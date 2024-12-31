package vf.word.database.access.single.bible.translation.book

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.BookTranslationDbFactory
import vf.word.database.storable.bible.BookTranslationDbModel
import vf.word.model.enumeration.Book
import vf.word.model.partial.bible.BookTranslationData
import vf.word.model.stored.bible.BookTranslation

/**
  * Used for accessing individual book translations
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object DbBookTranslation extends SingleRowModelAccess[BookTranslation] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = BookTranslationDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = BookTranslationDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted book translation
	  * @return An access point to that book translation
	  */
	def apply(id: Int) = DbSingleBookTranslation(id)
	/**
	 * @param book Targeted book
	 * @param translationId Targeted translation's id
	 * @return Access to that book's specific translation
	 */
	def apply(book: Book, translationId: Int) =
		distinct(model.book <=> book && model.translationId <=> translationId)
	
	/**
	 * @param book Targeted book
	 * @param translationId Id of the targeted translation
	 * @param connection Implicit DB connection
	 * @return Either:
	 *              - Right: Book translation that already existed in the DB
	 *              - Left: Newly inserted translation
	 */
	def store(book: Book, translationId: Int)(implicit connection: Connection) =
		apply(book, translationId).pull.toRight { model.insert(BookTranslationData(book, translationId)) }
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should 
	  *                  yield unique book translations.
	  * @return An access point to the book translation that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueBookTranslationAccess(condition)
}

