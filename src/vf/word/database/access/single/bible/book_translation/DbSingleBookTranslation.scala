package vf.word.database.access.single.bible.book_translation

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.word.model.stored.bible.BookTranslation

/**
  * An access point to individual book translations, based on their id
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class DbSingleBookTranslation(id: Int) 
	extends UniqueBookTranslationAccess with SingleIntIdModelAccess[BookTranslation]

