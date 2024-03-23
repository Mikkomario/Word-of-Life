package vf.word.database.factory.bible

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.word.database.WordOfLifeTables
import vf.word.model.partial.bible.VerseMarkerData
import vf.word.model.stored.bible.VerseMarker

/**
  * Used for reading verse marker data from the DB
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object VerseMarkerDbFactory extends FromValidatedRowModelFactory[VerseMarker]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = WordOfLifeTables.verseMarker
	
	override protected def fromValidatedModel(valid: Model) = 
		VerseMarker(valid("id").getInt, VerseMarkerData(valid("chapterIndex").getInt, 
			valid("verseIndex").getInt, valid("firstStatementId").getInt))
}

