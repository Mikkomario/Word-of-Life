package vf.word.model.factory.bible

import utopia.logos.model.factory.text.StatementPlacementFactory

/**
  * Common trait for book statement placement-related factories which allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait BookStatementPlacementFactory[+A] extends StatementPlacementFactory[A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param bookId New book id to assign
	  * @return Copy of this item with the specified book id
	  */
	def withBookId(bookId: Int): A
	
	
	// IMPLEMENTED	--------------------
	
	override def withParentId(parentId: Int) = withBookId(parentId)
}

