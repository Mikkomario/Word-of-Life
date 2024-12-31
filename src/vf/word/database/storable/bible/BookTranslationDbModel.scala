package vf.word.database.storable.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{FromIdFactory, HasId, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import vf.word.database.WordOfLifeTables
import vf.word.model.enumeration.Book
import vf.word.model.factory.bible.BookTranslationFactory
import vf.word.model.partial.bible.BookTranslationData
import vf.word.model.stored.bible.BookTranslation

/**
  * Used for constructing BookTranslationDbModel instances and for inserting book translations to 
  * the database
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object BookTranslationDbModel 
	extends StorableFactory[BookTranslationDbModel, BookTranslation, BookTranslationData] 
		with FromIdFactory[Int, BookTranslationDbModel] with HasIdProperty 
		with BookTranslationFactory[BookTranslationDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with books
	  */
	lazy val book = property("bookId")
	
	/**
	  * Database property used for interacting with translation ids
	  */
	lazy val translationId = property("translationId")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = WordOfLifeTables.bookTranslation
	
	override def apply(data: BookTranslationData): BookTranslationDbModel = 
		apply(None, Some(data.book), Some(data.translationId))
	
	/**
	  * @param book The translated book
	  * @return A model containing only the specified book
	  */
	override def withBook(book: Book) = apply(book = Some(book))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param translationId Id of the translation this book is part of
	  * @return A model containing only the specified translation id
	  */
	override def withTranslationId(translationId: Int) = apply(translationId = Some(translationId))
	
	override protected def complete(id: Value, data: BookTranslationData) = BookTranslation(id.getInt, data)
}

/**
  * Used for interacting with BookTranslations in the database
  * @param id book translation database id
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class BookTranslationDbModel(id: Option[Int] = None, book: Option[Book] = None, 
	translationId: Option[Int] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, BookTranslationDbModel] 
		with BookTranslationFactory[BookTranslationDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties = 
		Vector(BookTranslationDbModel.id.name -> id, 
			BookTranslationDbModel.book.name -> book.map[Value] { e => e.id }.getOrElse(Value.empty), 
			BookTranslationDbModel.translationId.name -> translationId)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = BookTranslationDbModel.table
	
	/**
	  * @param book The translated book
	  * @return A new copy of this model with the specified book
	  */
	override def withBook(book: Book) = copy(book = Some(book))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	/**
	  * @param translationId Id of the translation this book is part of
	  * @return A new copy of this model with the specified translation id
	  */
	override def withTranslationId(translationId: Int) = copy(translationId = Some(translationId))
}

