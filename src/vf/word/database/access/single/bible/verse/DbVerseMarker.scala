package vf.word.database.access.single.bible.verse

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.VerseMarkerDbFactory
import vf.word.database.storable.bible.VerseMarkerDbModel
import vf.word.model.stored.bible.VerseMarker

/**
  * Used for accessing individual verse markers
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object DbVerseMarker extends SingleRowModelAccess[VerseMarker] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = VerseMarkerDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = VerseMarkerDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted verse marker
	  * @return An access point to that verse marker
	  */
	def apply(id: Int) = DbSingleVerseMarker(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should 
	  *                  yield unique verse markers.
	  * @return An access point to the verse marker that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueVerseMarkerAccess(condition)
}

