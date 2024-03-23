package vf.word.database.access.many.bible.footnote_statement_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteStatementLinkDbFactory
import vf.word.database.storable.bible.FootnoteStatementLinkModel
import vf.word.model.stored.bible.FootnoteStatementLink

object ManyFootnoteStatementLinksAccess
{
	// NESTED	--------------------
	
	private class ManyFootnoteStatementLinksSubView(condition: Condition)
		 extends ManyFootnoteStatementLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple footnote statement links at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait ManyFootnoteStatementLinksAccess 
	extends ManyRowModelAccess[FootnoteStatementLink] with FilterableView[ManyFootnoteStatementLinksAccess] 
		with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * footnote ids of the accessible footnote statement links
	  */
	def footnoteIds(implicit connection: Connection) = pullColumn(model.footnoteId.column)
		.map { v => v.getInt }
	
	/**
	  * statement ids of the accessible footnote statement links
	  */
	def statementIds(implicit connection: Connection) = pullColumn(model.statementId.column)
		.map { v => v.getInt }
	
	/**
	  * order indices of the accessible footnote statement links
	  */
	def orderIndices(implicit connection: Connection) = pullColumn(model.orderIndex.column)
		.map { v => v.getInt }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = FootnoteStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteStatementLinkDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyFootnoteStatementLinksAccess = 
		new ManyFootnoteStatementLinksAccess
			.ManyFootnoteStatementLinksSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * @param orderIndex order index to target
	  * @return Copy of this access point that only includes footnote statement links 
		with the specified order index
	  */
	def at(orderIndex: Int) = filter(model.orderIndex.column <=> orderIndex)
	
	/**
	  * @param orderIndices Targeted order indices
	  * @return Copy of this access point that only includes footnote statement links where order index is within
	  *  the specified value set
	  */
	def atIndices(orderIndices: Iterable[Int]) =
		filter(model.orderIndex.column.in(orderIndices))
	
	/**
	  * Updates the footnote ids of the targeted footnote statement links
	  * @param newFootnoteId A new footnote id to assign
	  * @return Whether any footnote statement link was affected
	  */
	def footnoteIds_=(newFootnoteId: Int)(implicit connection: Connection) = 
		putColumn(model.footnoteId.column, newFootnoteId)
	
	/**
	  * @param footnoteId footnote id to target
	  * @return Copy of this access point that only includes footnote statement links 
		with the specified footnote id
	  */
	def inFootnote(footnoteId: Int) = filter(model.footnoteId.column <=> footnoteId)
	
	/**
	  * @param footnoteIds Targeted footnote ids
	  * @return Copy of this access point that only includes footnote statement links where footnote id is within
	  *  the specified value set
	  */
	def inFootnotes(footnoteIds: Iterable[Int]) =
		filter(model.footnoteId.column.in(footnoteIds))
	
	/**
	  * Updates the order indices of the targeted footnote statement links
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any footnote statement link was affected
	  */
	def orderIndices_=(newOrderIndex: Int)(implicit connection: Connection) = 
		putColumn(model.orderIndex.column, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted footnote statement links
	  * @param newStatementId A new statement id to assign
	  * @return Whether any footnote statement link was affected
	  */
	def statementIds_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(model.statementId.column, newStatementId)
	
	/**
	  * @param statementId statement id to target
	  * @return Copy of this access point that only includes footnote statement links 
		with the specified statement id
	  */
	def toStatement(statementId: Int) = filter(model.statementId.column <=> statementId)
	
	/**
	  * @param statementIds Targeted statement ids
	  * @return Copy of this access point that only includes footnote statement links where statement id is within
	  *  the specified value set
	  */
	def toStatements(statementIds: Iterable[Int]) =
		filter(model.statementId.column.in(statementIds))
}

