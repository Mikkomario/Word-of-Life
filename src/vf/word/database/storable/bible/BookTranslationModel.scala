package vf.word.database.storable.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.FromIdFactory
import utopia.vault.nosql.storable.StorableFactory
import vf.word.database.factory.bible.BookTranslationDbFactory
import vf.word.model.enumeration.Book
import vf.word.model.factory.bible.BookTranslationFactory
import vf.word.model.partial.bible.BookTranslationData
import vf.word.model.stored.bible.BookTranslation

/**
  * Used for constructing BookTranslationModel instances and for inserting book translations to the database
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object BookTranslationModel 
	extends StorableFactory[BookTranslationModel, BookTranslation, BookTranslationData] 
		with BookTranslationFactory[BookTranslationModel] with FromIdFactory[Int, BookTranslationModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Property that contains book translation book
	  */
	lazy val book = property("bookId")
	
	/**
	  * Property that contains book translation translation id
	  */
	lazy val translationId = property("translationId")
	
	
	// COMPUTED	--------------------
	
	/**
	  * The factory object used by this model type
	  */
	def factory = BookTranslationDbFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: BookTranslationData) = apply(None, Some(data.book.id), Some(data.translationId))
	
	override def withId(id: Int) = apply(Some(id))
	
	override protected def complete(id: Value, data: BookTranslationData) = BookTranslation(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param book The translated book
	  * @return A model containing only the specified book
	  */
	def withBook(book: Book) = apply(book = Some(book.id))
	
	/**
	  * @param translationId Id of the translation this book is part of
	  * @return A model containing only the specified translation id
	  */
	def withTranslationId(translationId: Int) = apply(translationId = Some(translationId))
}

/**
  * Used for interacting with BookTranslations in the database
  * @param id book translation database id
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class BookTranslationModel(id: Option[Int] = None, book: Option[Int] = None, 
	translationId: Option[Int] = None) 
	extends StorableWithFactory[BookTranslation] with BookTranslationFactory[BookTranslationModel] 
		with FromIdFactory[Int, BookTranslationModel]
{
	// IMPLEMENTED	--------------------
	
	override def factory = BookTranslationModel.factory
	
	override def valueProperties = 
		Vector("id" -> id, BookTranslationModel.book.name -> book, 
			BookTranslationModel.translationId.name -> translationId)
	
	override def withId(id: Int) = copy(id = Some(id))
	
	
	// OTHER	--------------------
	
	/**
	  * @param book The translated book
	  * @return A model containing only the specified book
	  */
	def withBook(book: Book) = copy(book = Some(book.id))
	
	/**
	  * @param translationId Id of the translation this book is part of
	  * @return A model containing only the specified translation id
	  */
	def withTranslationId(translationId: Int) = copy(translationId = Some(translationId))
}

