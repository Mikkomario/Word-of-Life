package vf.word.database.access.single.bible.translation.book.statement

import utopia.logos.database.access.single.text.statement.placement.UniqueStatementPlacementAccessLike
import utopia.vault.database.Connection
import vf.word.database.storable.bible.BookStatementPlacementDbModel

/**
  * A common trait for access points which target individual book statement placements or similar 
  * items at a time
  * @tparam A Type of read (book statement placements -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait UniqueBookStatementPlacementAccessLike[+A, +Repr] extends UniqueStatementPlacementAccessLike[A, Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the book where the statement appears. 
	  * None if no book statement placement (or value) was found.
	  */
	def bookId(implicit connection: Connection) = parentId
	
	
	// IMPLEMENTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	override protected def model = BookStatementPlacementDbModel
}

