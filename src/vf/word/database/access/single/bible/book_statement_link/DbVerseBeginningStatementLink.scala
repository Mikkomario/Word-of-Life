package vf.word.database.access.single.bible.book_statement_link

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.VerseBeginningStatementLinkDbFactory
import vf.word.database.storable.bible.{BookStatementLinkModel, VerseMarkerModel}
import vf.word.model.combined.bible.VerseBeginningStatementLink

/**
  * Used for accessing individual verse beginning statement links
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbVerseBeginningStatementLink 
	extends SingleRowModelAccess[VerseBeginningStatementLink] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A database model (factory) used for interacting with linked book statement links
	  */
	protected def model = BookStatementLinkModel
	
	/**
	  * A database model (factory) used for interacting with the linked verse marker
	  */
	protected def verseMarkerModel = VerseMarkerModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = VerseBeginningStatementLinkDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted verse beginning statement link
	  * @return An access point to that verse beginning statement link
	  */
	def apply(id: Int) = DbSingleVerseBeginningStatementLink(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique verse beginning statement links.
	  * @return An access point to the verse beginning statement link that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = 
		UniqueVerseBeginningStatementLinkAccess(mergeCondition(condition))
}

