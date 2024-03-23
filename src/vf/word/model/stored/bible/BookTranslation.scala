package vf.word.model.stored.bible

import utopia.vault.model.template.{FromIdFactory, StoredModelConvertible}
import vf.word.database.access.single.bible.book_translation.DbSingleBookTranslation
import vf.word.model.enumeration.Book
import vf.word.model.factory.bible.BookTranslationFactory
import vf.word.model.partial.bible.BookTranslationData

/**
  * Represents a book translation that has already been stored in the database
  * @param id id of this book translation in the database
  * @param data Wrapped book translation data
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class BookTranslation(id: Int, data: BookTranslationData) 
	extends StoredModelConvertible[BookTranslationData] with BookTranslationFactory[BookTranslation] 
		with FromIdFactory[Int, BookTranslation]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this book translation in the database
	  */
	def access = DbSingleBookTranslation(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def withBook(book: Book) = copy(data = data.withBook(book))
	
	override def withId(id: Int) = copy(id = id)
	
	override def withTranslationId(translationId: Int) = copy(data = data.withTranslationId(translationId))
}

