package vf.word.database.factory.bible

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
import vf.word.database.storable.bible.VerseMarkerDbModel
import vf.word.model.partial.bible.VerseMarkerData
import vf.word.model.stored.bible.VerseMarker

/**
  * Used for reading verse marker data from the DB
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object VerseMarkerDbFactory extends FromValidatedRowModelFactory[VerseMarker]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = VerseMarkerDbModel
	
	override lazy val defaultOrdering: Option[OrderBy] = None
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override protected def fromValidatedModel(valid: Model) = 
		VerseMarker(valid(this.model.id.name).getInt, 
			VerseMarkerData(valid(this.model.chapterIndex.name).getInt, 
			valid(this.model.verseIndex.name).getInt, valid(this.model.firstStatementId.name).getInt))
}

