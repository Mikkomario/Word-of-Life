package vf.word.model.factory.bible

/**
  * Common trait for footnote statement link-related factories which allow construction 
	with individual properties
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait FootnoteStatementLinkFactory[+A]
{
	// ABSTRACT	--------------------
	
	/**
	  * @param footnoteId New footnote id to assign
	  * @return Copy of this item with the specified footnote id
	  */
	def withFootnoteId(footnoteId: Int): A
	
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

