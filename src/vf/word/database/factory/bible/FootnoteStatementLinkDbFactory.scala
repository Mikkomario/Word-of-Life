package vf.word.database.factory.bible

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.word.database.WordOfLifeTables
import vf.word.model.partial.bible.FootnoteStatementLinkData
import vf.word.model.stored.bible.FootnoteStatementLink

/**
  * Used for reading footnote statement link data from the DB
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object FootnoteStatementLinkDbFactory extends FromValidatedRowModelFactory[FootnoteStatementLink]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = WordOfLifeTables.footnoteStatementLink
	
	override protected def fromValidatedModel(valid: Model) = 
		FootnoteStatementLink(valid("id").getInt, FootnoteStatementLinkData(valid("footnoteId").getInt, 
			valid("statementId").getInt, valid("orderIndex").getInt))
}

