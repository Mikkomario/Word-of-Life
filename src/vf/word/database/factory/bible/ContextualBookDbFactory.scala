package vf.word.database.factory.bible

import utopia.vault.nosql.factory.row.linked.CombiningFactory
import vf.word.model.combined.bible.ContextualBook
import vf.word.model.stored.bible.{BookTranslation, Translation}

/**
  * Used for reading contextual books from the database
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object ContextualBookDbFactory extends CombiningFactory[ContextualBook, BookTranslation, Translation]
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = TranslationDbFactory
	
	override def parentFactory = BookTranslationDbFactory
	
	override def apply(bookTranslation: BookTranslation, translation: Translation) = 
		ContextualBook(bookTranslation, translation)
}

