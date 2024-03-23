package vf.word.database.access.many.bible.book_statement_link

import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.BookStatementLinkDbFactory
import vf.word.model.stored.bible.BookStatementLink

object ManyBookStatementLinksAccess
{
	// NESTED	--------------------
	
	private class ManyBookStatementLinksSubView(condition: Condition) extends ManyBookStatementLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple book statement links at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait ManyBookStatementLinksAccess 
	extends ManyBookStatementLinksAccessLike[BookStatementLink, ManyBookStatementLinksAccess] 
		with ManyRowModelAccess[BookStatementLink]
{
	// IMPLEMENTED	--------------------
	
	override def factory = BookStatementLinkDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyBookStatementLinksAccess = 
		new ManyBookStatementLinksAccess.ManyBookStatementLinksSubView(mergeCondition(filterCondition))
}

