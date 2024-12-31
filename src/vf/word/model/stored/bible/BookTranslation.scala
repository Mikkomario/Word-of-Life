package vf.word.model.stored.bible

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.model.template.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.word.database.access.single.bible.translation.book.DbSingleBookTranslation
import vf.word.model.enumeration.Book
import vf.word.model.factory.bible.{BookTranslationFactory, BookTranslationFactoryWrapper}
import vf.word.model.partial.bible.BookTranslationData

object BookTranslation extends StoredFromModelFactory[BookTranslationData, BookTranslation]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = BookTranslationData
	
	override protected def complete(model: AnyModel, data: BookTranslationData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a book translation that has already been stored in the database
  * @param id   id of this book translation in the database
  * @param data Wrapped book translation data
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class BookTranslation(id: Int, data: BookTranslationData) 
	extends StoredModelConvertible[BookTranslationData] with FromIdFactory[Int, BookTranslation]
		with BookTranslationFactoryWrapper[BookTranslationData, BookTranslation]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this book translation in the database
	  */
	def access = DbSingleBookTranslation(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int): BookTranslation = copy(id = id)
	
	override protected def wrap(data: BookTranslationData) = copy(data = data)
}

