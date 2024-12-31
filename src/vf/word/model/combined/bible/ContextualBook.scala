package vf.word.model.combined.bible

import utopia.flow.view.template.Extender
import utopia.vault.model.template.HasId
import vf.word.model.factory.bible.BookTranslationFactoryWrapper
import vf.word.model.partial.bible.BookTranslationData
import vf.word.model.stored.bible.{BookTranslation, Translation}

object ContextualBook
{
	// OTHER	--------------------
	
	/**
	  * @param bookTranslation book translation to wrap
	  * @param translation     translation to attach to this book translation
	  * @return Combination of the specified book translation and translation
	  */
	def apply(bookTranslation: BookTranslation, translation: Translation): ContextualBook = 
		_ContextualBook(bookTranslation, translation)
	
	
	// NESTED	--------------------
	
	/**
	  * @param bookTranslation book translation to wrap
	  * @param translation     translation to attach to this book translation
	  */
	private case class _ContextualBook(bookTranslation: BookTranslation, translation: Translation) 
		extends ContextualBook
	{
		// IMPLEMENTED	--------------------
		
		override protected def wrap(factory: BookTranslation) = copy(bookTranslation = factory)
	}
}

/**
  * Includes information about this book's translation
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait ContextualBook 
	extends Extender[BookTranslationData] with HasId[Int] 
		with BookTranslationFactoryWrapper[BookTranslation, ContextualBook]
{
	// ABSTRACT	--------------------
	
	/**
	  * Wrapped book translation
	  */
	def bookTranslation: BookTranslation
	
	/**
	  * The translation that is attached to this book translation
	  */
	def translation: Translation
	
	
	// IMPLEMENTED	--------------------
	
	/**
	  * Id of this book translation in the database
	  */
	override def id = bookTranslation.id
	
	override def wrapped = bookTranslation.data
	
	override protected def wrappedFactory = bookTranslation
}

