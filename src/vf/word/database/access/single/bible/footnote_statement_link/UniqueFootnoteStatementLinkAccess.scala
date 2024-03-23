package vf.word.database.access.single.bible.footnote_statement_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteStatementLinkDbFactory
import vf.word.database.storable.bible.FootnoteStatementLinkModel
import vf.word.model.stored.bible.FootnoteStatementLink

object UniqueFootnoteStatementLinkAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueFootnoteStatementLinkAccess = 
		new _UniqueFootnoteStatementLinkAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueFootnoteStatementLinkAccess(condition: Condition) 
		extends UniqueFootnoteStatementLinkAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct footnote statement links.
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait UniqueFootnoteStatementLinkAccess 
	extends SingleRowModelAccess[FootnoteStatementLink] 
		with FilterableView[UniqueFootnoteStatementLinkAccess] 
		with DistinctModelAccess[FootnoteStatementLink, Option[FootnoteStatementLink], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * 
		Id of the footnote where the statement is made. None if no footnote statement link (or value) was found.
	  */
	def footnoteId(implicit connection: Connection) = pullColumn(model.footnoteId.column).int
	
	/**
	  * Id of the statement made within the footnote. None if no footnote statement link (or value) was found.
	  */
	def statementId(implicit connection: Connection) = pullColumn(model.statementId.column).int
	
	/**
	  * A 0-based index that determines where the statement appears within the footnote. None if no footnote
	  *  statement link (or value) was found.
	  */
	def orderIndex(implicit connection: Connection) = pullColumn(model.orderIndex.column).int
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = FootnoteStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteStatementLinkDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueFootnoteStatementLinkAccess = 
		new UniqueFootnoteStatementLinkAccess._UniqueFootnoteStatementLinkAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the footnote ids of the targeted footnote statement links
	  * @param newFootnoteId A new footnote id to assign
	  * @return Whether any footnote statement link was affected
	  */
	def footnoteId_=(newFootnoteId: Int)(implicit connection: Connection) = 
		putColumn(model.footnoteId.column, newFootnoteId)
	
	/**
	  * Updates the order indexs of the targeted footnote statement links
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any footnote statement link was affected
	  */
	def orderIndex_=(newOrderIndex: Int)(implicit connection: Connection) = 
		putColumn(model.orderIndex.column, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted footnote statement links
	  * @param newStatementId A new statement id to assign
	  * @return Whether any footnote statement link was affected
	  */
	def statementId_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(model.statementId.column, newStatementId)
}

