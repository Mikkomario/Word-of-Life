package vf.word.database.factory.bible

import utopia.vault.nosql.factory.row.linked.CombiningFactory
import vf.word.model.combined.bible.VerseBeginningStatementPlacement
import vf.word.model.stored.bible.{BookStatementPlacement, VerseMarker}

/**
  * Used for reading verse beginning statement placements from the database
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object VerseBeginningStatementPlacementDbFactory 
	extends CombiningFactory[VerseBeginningStatementPlacement, BookStatementPlacement, VerseMarker]
{
	// ATTRIBUTES	--------------------
	
	override val parentFactory = BookStatementPlacementDbFactory
	
	override val childFactory = VerseMarkerDbFactory
	
	
	// IMPLEMENTED	--------------------
	
	/**
	  * @param placement   placement to wrap
	  * @param verseMarker verse marker to attach to this placement
	  */
	override def apply(placement: BookStatementPlacement, verseMarker: VerseMarker) = 
		VerseBeginningStatementPlacement(placement, verseMarker)
}

