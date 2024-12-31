package vf.word.database.access.many.bible.translation.book.statement

import utopia.flow.collection.immutable.IntSet
import utopia.flow.generic.casting.ValueConversions._
import utopia.logos.database.access.many.text.statement.placement.ManyStatementPlacementsAccessLike
import utopia.vault.database.Connection
import vf.word.database.storable.bible.BookStatementPlacementDbModel

/**
  * A common trait for access points which target multiple book statement placements or similar 
  * instances at a time
  * @tparam A Type of read (book statement placements -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait ManyBookStatementPlacementsAccessLike[+A, +Repr] extends ManyStatementPlacementsAccessLike[A, Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * book ids of the accessible book statement placements
	  */
	def bookIds(implicit connection: Connection) = parentIds
	
	
	// IMPLEMENTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	override protected def model = BookStatementPlacementDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * @param bookId book id to target
	  * @return Copy of this access point that only includes book statement placements with the specified 
	  * book id
	  */
	def inBook(bookId: Int) = filter(model.bookId.column <=> bookId)
	/**
	  * @param bookIds Targeted book ids
	  * @return Copy of this access point that only includes book statement placements where book id is 
	  * within the specified value set
	  */
	def inBooks(bookIds: IterableOnce[Int]) = filter(model.bookId.column.in(IntSet.from(bookIds)))
}

