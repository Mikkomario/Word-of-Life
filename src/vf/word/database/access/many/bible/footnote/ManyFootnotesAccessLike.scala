package vf.word.database.access.many.bible.footnote

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import vf.word.database.storable.bible.FootnoteModel

/**
  * A common trait for access points which target multiple footnotes or similar instances at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait ManyFootnotesAccessLike[+A, +Repr] extends ManyModelAccess[A] with Indexed with FilterableView[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * commented statement ids of the accessible footnotes
	  */
	def commentedStatementIds(implicit connection: Connection) = 
		pullColumn(model.commentedStatementId.column).map { v => v.getInt }
	
	/**
	  * targeted word indexs of the accessible footnotes
	  */
	def targetedWordIndexs(implicit connection: Connection) = 
		pullColumn(model.targetedWordIndex.column).flatMap { v => v.int }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = FootnoteModel
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the commented statement ids of the targeted footnotes
	  * @param newCommentedStatementId A new commented statement id to assign
	  * @return Whether any footnote was affected
	  */
	def commentedStatementIds_=(newCommentedStatementId: Int)(implicit connection: Connection) = 
		putColumn(model.commentedStatementId.column, newCommentedStatementId)
	
	/**
	  * @param commentedStatementId commented statement id to target
	  * @return Copy of this access point that only includes footnotes 
		with the specified commented statement id
	  */
	def forStatement(commentedStatementId: Int) = 
		filter(model.commentedStatementId.column <=> commentedStatementId)
	
	/**
	  * @param commentedStatementIds Targeted commented statement ids
	  * 
		@return Copy of this access point that only includes footnotes where commented statement id is within the
	  *  specified value set
	  */
	def forStatements(commentedStatementIds: Iterable[Int]) =
		filter(model.commentedStatementId.column.in(commentedStatementIds))
	
	/**
	  * Updates the targeted word indexs of the targeted footnotes
	  * @param newTargetedWordIndex A new targeted word index to assign
	  * @return Whether any footnote was affected
	  */
	def targetedWordIndexs_=(newTargetedWordIndex: Int)(implicit connection: Connection) = 
		putColumn(model.targetedWordIndex.column, newTargetedWordIndex)
}

