package vf.word.database.access.single.bible.footnote

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.word.database.storable.bible.FootnoteDbModel

/**
  * A common trait for access points which target individual footnotes or similar items at a time
  * @tparam A Type of read (footnotes -like) instances
  * @tparam Repr Type of this access point
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait UniqueFootnoteAccessLike[+A, +Repr] 
	extends SingleModelAccess[A] with DistinctModelAccess[A, Option[A], Value] with FilterableView[Repr] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Id of the specific statement this footnote comments on. 
	  * None if no footnote (or value) was found.
	  */
	def commentedStatementId(implicit connection: Connection) = 
		pullColumn(model.commentedStatementId.column).int
	
	/**
	  * A 0-based index that specifies the word targeted within this statement. 
	  * None if no specific word was targeted. 
	  * None if no footnote (or value) was found.
	  */
	def targetedWordIndex(implicit connection: Connection) = pullColumn(model.targetedWordIndex.column).int
	
	/**
	  * Unique id of the accessible footnote. None if no footnote was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = FootnoteDbModel
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the commented statement ids of the targeted footnotes
	  * @param newCommentedStatementId A new commented statement id to assign
	  * @return Whether any footnote was affected
	  */
	def commentedStatementId_=(newCommentedStatementId: Int)(implicit connection: Connection) = 
		putColumn(model.commentedStatementId.column, newCommentedStatementId)
	
	/**
	  * Updates the targeted word indexs of the targeted footnotes
	  * @param newTargetedWordIndex A new targeted word index to assign
	  * @return Whether any footnote was affected
	  */
	def targetedWordIndex_=(newTargetedWordIndex: Int)(implicit connection: Connection) = 
		putColumn(model.targetedWordIndex.column, newTargetedWordIndex)
}

