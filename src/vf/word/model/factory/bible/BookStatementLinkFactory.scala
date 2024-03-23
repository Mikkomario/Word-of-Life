package vf.word.model.factory.bible

/**
  * Common trait for book statement link-related factories which allow construction with individual properties
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait BookStatementLinkFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param bookId New book id to assign
	  * @return Copy of this item with the specified book id
	  */
	def withBookId(bookId: Int): A
	
	/**
	  * @param orderIndex New order index to assign
	  * @return Copy of this item with the specified order index
	  */
	def withOrderIndex(orderIndex: Int): A
	
	/**
	  * @param statementId New statement id to assign
	  * @return Copy of this item with the specified statement id
	  */
	def withStatementId(statementId: Int): A
}

