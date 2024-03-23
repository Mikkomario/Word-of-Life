package vf.word.database.access.single.bible.footnote

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteDbFactory
import vf.word.database.storable.bible.FootnoteModel
import vf.word.model.stored.bible.Footnote

/**
  * Used for accessing individual footnotes
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbFootnote extends SingleRowModelAccess[Footnote] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = FootnoteModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted footnote
	  * @return An access point to that footnote
	  */
	def apply(id: Int) = DbSingleFootnote(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique footnotes.
	  * @return An access point to the footnote that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueFootnoteAccess(mergeCondition(condition))
}

