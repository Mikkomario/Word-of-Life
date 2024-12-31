package vf.word.database.access.single.bible.translation.book.statement

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.BookStatementPlacementDbFactory
import vf.word.database.storable.bible.BookStatementPlacementDbModel
import vf.word.model.stored.bible.BookStatementPlacement

/**
  * Used for accessing individual book statement placements
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object DbBookStatementPlacement 
	extends SingleRowModelAccess[BookStatementPlacement] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = BookStatementPlacementDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = BookStatementPlacementDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted book statement placement
	  * @return An access point to that book statement placement
	  */
	def apply(id: Int) = DbSingleBookStatementPlacement(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should 
	  *                  yield unique book statement placements.
	  * @return An access point to the book statement placement that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueBookStatementPlacementAccess(condition)
}

