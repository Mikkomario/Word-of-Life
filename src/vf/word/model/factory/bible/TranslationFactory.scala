package vf.word.model.factory.bible

import java.time.Instant

/**
  * Common trait for translation-related factories which allow construction with individual properties
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait TranslationFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param abbreviation New abbreviation to assign
	  * @return Copy of this item with the specified abbreviation
	  */
	def withAbbreviation(abbreviation: String): A
	
	/**
	  * @param created New created to assign
	  * @return Copy of this item with the specified created
	  */
	def withCreated(created: Instant): A
	
	/**
	  * @param name New name to assign
	  * @return Copy of this item with the specified name
	  */
	def withName(name: String): A
}

