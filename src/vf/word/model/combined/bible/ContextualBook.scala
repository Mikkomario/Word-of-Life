package vf.word.model.combined.bible

import utopia.flow.view.template.Extender
import vf.word.model.partial.bible.BookTranslationData
import vf.word.model.stored.bible.{BookTranslation, Translation}

/**
  * Includes information about this book's translation
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class ContextualBook(bookTranslation: BookTranslation, translation: Translation) 
	extends Extender[BookTranslationData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this book translation in the database
	  */
	def id = bookTranslation.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = bookTranslation.data
}

