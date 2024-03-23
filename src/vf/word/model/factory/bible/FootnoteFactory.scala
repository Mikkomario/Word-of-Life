package vf.word.model.factory.bible

/**
  * Common trait for footnote-related factories which allow construction with individual properties
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait FootnoteFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param commentedStatementId New commented statement id to assign
	  * @return Copy of this item with the specified commented statement id
	  */
	def withCommentedStatementId(commentedStatementId: Int): A
	
	/**
	  * @param targetedWordIndex New targeted word index to assign
	  * @return Copy of this item with the specified targeted word index
	  */
	def withTargetedWordIndex(targetedWordIndex: Int): A
}

