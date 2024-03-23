package vf.word.model.factory.bible

import vf.word.model.enumeration.Book

/**
  * Common trait for book translation-related factories which allow construction with individual properties
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait BookTranslationFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param book New book to assign
	  * @return Copy of this item with the specified book
	  */
	def withBook(book: Book): A
	
	/**
	  * @param translationId New translation id to assign
	  * @return Copy of this item with the specified translation id
	  */
	def withTranslationId(translationId: Int): A
}

