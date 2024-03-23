package vf.word.database.access.single.bible.book_statement_link

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.BookStatementLinkDbFactory
import vf.word.database.storable.bible.BookStatementLinkModel
import vf.word.model.stored.bible.BookStatementLink

/**
  * Used for accessing individual book statement links
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbBookStatementLink extends SingleRowModelAccess[BookStatementLink] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = BookStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = BookStatementLinkDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted book statement link
	  * @return An access point to that book statement link
	  */
	def apply(id: Int) = DbSingleBookStatementLink(id)
	
	/**
	  * 
		@param condition Filter condition to apply in addition to this root view's condition. Should yield unique book
	  *  statement links.
	  * @return An access point to the book statement link that satisfies the specified condition
	  */
	protected
		 def filterDistinct(condition: Condition) = UniqueBookStatementLinkAccess(mergeCondition(condition))
}

