package vf.word.database.access.single.bible.translation.book

import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.word.database.storable.bible.BookTranslationDbModel
import vf.word.model.enumeration.Book

/**
  * A common trait for access points which target individual book translations or similar items 
  * at a time
  * @tparam A Type of read (book translations -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait UniqueBookTranslationAccessLike[+A, +Repr] 
	extends SingleModelAccess[A] with DistinctModelAccess[A, Option[A], Value] with FilterableView[Repr] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * The translated book. 
	  * None if no book translation (or value) was found.
	  */
	def book(implicit connection: Connection) = pullColumn(model.book.column).int.flatMap(Book.findForId)
	
	/**
	  * Id of the translation this book is part of. 
	  * None if no book translation (or value) was found.
	  */
	def translationId(implicit connection: Connection) = pullColumn(model.translationId.column).int
	
	/**
	  * Unique id of the accessible book translation. None if no book translation was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = BookTranslationDbModel
}

