package vf.word.database.access.single.bible.translation.book

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.ContextualBookDbFactory
import vf.word.database.storable.bible.{BookTranslationDbModel, TranslationDbModel}
import vf.word.model.combined.bible.ContextualBook

/**
  * Used for accessing individual contextual books
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object DbContextualBook extends SingleRowModelAccess[ContextualBook] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with the linked translation
	  */
	protected def translationModel = TranslationDbModel
	
	/**
	  * A database model (factory) used for interacting with linked book translations
	  */
	private def model = BookTranslationDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = ContextualBookDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted contextual book
	  * @return An access point to that contextual book
	  */
	def apply(id: Int) = DbSingleContextualBook(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should 
	  *                  yield unique contextual books.
	  * @return An access point to the contextual book that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueContextualBookAccess(condition)
}

