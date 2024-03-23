package vf.word.database.factory.bible

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.word.database.WordOfLifeTables
import vf.word.model.partial.bible.FootnoteData
import vf.word.model.stored.bible.Footnote

/**
  * Used for reading footnote data from the DB
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object FootnoteDbFactory extends FromValidatedRowModelFactory[Footnote]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = WordOfLifeTables.footnote
	
	override protected def fromValidatedModel(valid: Model) = 
		Footnote(valid("id").getInt, FootnoteData(valid("commentedStatementId").getInt, 
			valid("targetedWordIndex").int))
}

