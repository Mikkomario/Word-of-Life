package vf.word.database.factory.bible

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.word.database.WordOfLifeTables
import vf.word.model.partial.bible.TranslationData
import vf.word.model.stored.bible.Translation

/**
  * Used for reading translation data from the DB
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object TranslationDbFactory extends FromValidatedRowModelFactory[Translation]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = WordOfLifeTables.translation
	
	override protected def fromValidatedModel(valid: Model) = 
		Translation(valid("id").getInt, TranslationData(valid("name").getString, 
			valid("abbreviation").getString, valid("created").getInstant))
}

