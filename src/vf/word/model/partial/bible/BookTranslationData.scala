package vf.word.model.partial.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactory
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.{ModelConvertible, ModelLike, Property}
import vf.word.model.enumeration.Book
import vf.word.model.factory.bible.BookTranslationFactory

object BookTranslationData extends FromModelFactory[BookTranslationData]
{
	// ATTRIBUTES	--------------------
	
	lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("book", IntType), PropertyDeclaration("translationId", 
			IntType, Vector("translation_id"))))
	
	
	// IMPLEMENTED	--------------------
	
	override def apply(model: ModelLike[Property]) = {
		schema.validate(model).flatMap { valid =>
			Book.fromValue(valid("book")).map { book => 
				BookTranslationData(book, valid("translationId").getInt)
			}
		}
	}
}

/**
  * Represents a translated book of the Bible
  * @param book The translated book
  * @param translationId Id of the translation this book is part of
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class BookTranslationData(book: Book, translationId: Int) 
	extends BookTranslationFactory[BookTranslationData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("book" -> book.id, "translationId" -> translationId))
	
	override def withBook(book: Book) = copy(book = book)
	
	override def withTranslationId(translationId: Int) = copy(translationId = translationId)
}

