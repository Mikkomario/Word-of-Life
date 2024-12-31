package vf.word.database.access.many.bible.translation.book

import utopia.flow.collection.immutable.IntSet
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.word.database.storable.bible.BookTranslationDbModel
import vf.word.model.enumeration.Book

/**
  * A common trait for access points which target multiple book translations or similar instances 
  * at a time
  * @tparam A Type of read (book translations -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
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
	
	/**
	  * Unique ids of the accessible book translations
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = BookTranslationDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param book book to target
	  * @return Copy of this access point that only includes book translations with the specified book
	  */
	def apply(book: Book) = filter(model.book.column <=> book.id)
	
	/**
	  * @param books Targeted books
	  * @return Copy of this access point that only includes book translations where book is within the 
	  * specified value set
	  */
	def in(books: Iterable[Book]) = filter(model.book.column.in(books.map { book => book.id }))
	
	/**
	  * @param translationId translation id to target
	  * @return Copy of this access point that only includes book translations with the specified translation 
	  * id
	  */
	def partOfTranslation(translationId: Int) = filter(model.translationId.column <=> translationId)
	
	/**
	  * @param translationIds Targeted translation ids
	  * @return Copy of this access point that only includes book translations where translation id is within 
	  * the specified value set
	  */
	def partOfTranslations(translationIds: IterableOnce[Int]) = 
		filter(model.translationId.column.in(IntSet.from(translationIds)))
}

