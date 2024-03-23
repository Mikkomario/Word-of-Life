package vf.word.model.factory.bible

/**
  * Common trait for verse marker-related factories which allow construction with individual properties
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait VerseMarkerFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param chapterIndex New chapter index to assign
	  * @return Copy of this item with the specified chapter index
	  */
	def withChapterIndex(chapterIndex: Int): A
	
	/**
	  * @param firstStatementId New first statement id to assign
	  * @return Copy of this item with the specified first statement id
	  */
	def withFirstStatementId(firstStatementId: Int): A
	
	/**
	  * @param verseIndex New verse index to assign
	  * @return Copy of this item with the specified verse index
	  */
	def withVerseIndex(verseIndex: Int): A
}

