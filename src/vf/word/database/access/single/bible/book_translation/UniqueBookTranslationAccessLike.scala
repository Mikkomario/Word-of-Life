package vf.word.database.access.single.bible.book_translation

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import vf.word.database.storable.bible.BookTranslationModel
import vf.word.model.enumeration.Book

/**
  * A common trait for access points which target individual book translations or similar items at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait UniqueBookTranslationAccessLike[+A] 
	extends SingleModelAccess[A] with DistinctModelAccess[A, Option[A], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * The translated book. None if no book translation (or value) was found.
	  */
	def book(implicit connection: Connection) = pullColumn(model.book.column).int.flatMap(Book.findForId)
	
	/**
	  * Id of the translation this book is part of. None if no book translation (or value) was found.
	  */
	def translationId(implicit connection: Connection) = pullColumn(model.translationId.column).int
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = BookTranslationModel
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the books of the targeted book translations
	  * @param newBook A new book to assign
	  * @return Whether any book translation was affected
	  */
	def book_=(newBook: Book)(implicit connection: Connection) = putColumn(model.book.column, newBook.id)
	
	/**
	  * Updates the translation ids of the targeted book translations
	  * @param newTranslationId A new translation id to assign
	  * @return Whether any book translation was affected
	  */
	def translationId_=(newTranslationId: Int)(implicit connection: Connection) = 
		putColumn(model.translationId.column, newTranslationId)
}

