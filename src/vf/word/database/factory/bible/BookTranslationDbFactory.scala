package vf.word.database.factory.bible

import utopia.flow.generic.model.template.{ModelLike, Property}
import utopia.vault.nosql.factory.row.model.FromRowModelFactory
import vf.word.database.WordOfLifeTables
import vf.word.model.enumeration.Book
import vf.word.model.partial.bible.BookTranslationData
import vf.word.model.stored.bible.BookTranslation

/**
  * Used for reading book translation data from the DB
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object BookTranslationDbFactory extends FromRowModelFactory[BookTranslation]
{
	// IMPLEMENTED	--------------------
	
	override def defaultOrdering = None
	
	override def table = WordOfLifeTables.bookTranslation
	
	override def apply(model: ModelLike[Property]) = {
		table.validate(model).flatMap { valid => 
			Book.fromValue(valid("bookId")).map { book => 
				BookTranslation(valid("id").getInt, BookTranslationData(book, valid("translationId").getInt))
			}
		}
	}
}

