package vf.word.database.access.single.bible.translation.book.statement

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.VerseBeginningStatementPlacementDbFactory
import vf.word.database.storable.bible.{BookStatementPlacementDbModel, VerseMarkerDbModel}
import vf.word.model.combined.bible.VerseBeginningStatementPlacement

/**
  * Used for accessing individual verse beginning statement placements
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object DbVerseBeginningStatementPlacement 
	extends SingleRowModelAccess[VerseBeginningStatementPlacement] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with the linked verse marker
	  */
	protected def verseMarkerModel = VerseMarkerDbModel
	
	/**
	  * A database model (factory) used for interacting with linked placements
	  */
	private def model = BookStatementPlacementDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = VerseBeginningStatementPlacementDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted verse beginning statement placement
	  * @return An access point to that verse beginning statement placement
	  */
	def apply(id: Int) = DbSingleVerseBeginningStatementPlacement(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should 
	  *                  yield unique verse beginning statement placements.
	  * @return An access point to the verse beginning statement placement that satisfies the specified 
	  * condition
	  */
	private def distinct(condition: Condition) = UniqueVerseBeginningStatementPlacementAccess(condition)
}

