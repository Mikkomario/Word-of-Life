package vf.word.database.factory.bible

import utopia.flow.generic.model.template.{ModelLike, Property}
import utopia.vault.nosql.factory.row.model.FromRowModelFactory
import utopia.vault.sql.OrderBy
import vf.word.database.storable.bible.BookTranslationDbModel
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
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = BookTranslationDbModel
	
	override lazy val defaultOrdering: Option[OrderBy] = None
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override def apply(model: ModelLike[Property]) = table.validate(model).flatMap { valid =>
		Book.fromValue(valid(this.model.book.name)).map { book =>
			BookTranslation(valid(this.model.id.name).getInt, BookTranslationData(book,
				valid(this.model.translationId.name).getInt))
		}
	}
}

