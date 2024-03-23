package vf.word.database.factory.bible

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import vf.word.database.WordOfLifeTables
import vf.word.model.partial.bible.BookStatementLinkData
import vf.word.model.stored.bible.BookStatementLink

/**
  * Used for reading book statement link data from the DB
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object BookStatementLinkDbFactory extends FromValidatedRowModelFactory[BookStatementLink]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = WordOfLifeTables.bookStatementLink
	
	override protected def fromValidatedModel(valid: Model) = 
		BookStatementLink(valid("id").getInt, BookStatementLinkData(valid("bookId").getInt, 
			valid("statementId").getInt, valid("orderIndex").getInt))
}

