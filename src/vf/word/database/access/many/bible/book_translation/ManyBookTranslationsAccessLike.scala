package vf.word.database.access.many.bible.book_translation

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.word.database.storable.bible.BookTranslationModel
import vf.word.model.enumeration.Book

/**
  * A common trait for access points which target multiple book translations or similar instances at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait ManyBookTranslationsAccessLike[+A, +Repr] 
	extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * books of the accessible book translations
	  */
	def books(implicit connection: Connection) = 
		pullColumn(model.book.column).map { v => v.getInt }.flatMap(Book.findForId)
	
	/**
	  * translation ids of the accessible book translations
	  */
	def translationIds(implicit connection: Connection) = 
		pullColumn(model.translationId.column).map { v => v.getInt }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = BookTranslationModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param book book to target
	  * @return Copy of this access point that only includes book translations with the specified book
	  */
	def apply(book: Book) = filter(model.book.column <=> book.id)
	
	/**
	  * Updates the books of the targeted book translations
	  * @param newBook A new book to assign
	  * @return Whether any book translation was affected
	  */
	def books_=(newBook: Book)(implicit connection: Connection) = putColumn(model.book.column, newBook.id)
	
	/**
	  * @param books Targeted books
	  * 
		@return Copy of this access point that only includes book translations where book is within the specified
	  *  value set
	  */
	def in(books: Iterable[Book]) = filter(model.book.column.in(books.map { book => book.id }))
	
	/**
	  * Updates the translation ids of the targeted book translations
	  * @param newTranslationId A new translation id to assign
	  * @return Whether any book translation was affected
	  */
	def translationIds_=(newTranslationId: Int)(implicit connection: Connection) = 
		putColumn(model.translationId.column, newTranslationId)
}

