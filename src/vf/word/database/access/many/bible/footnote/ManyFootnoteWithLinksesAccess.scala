package vf.word.database.access.many.bible.footnote

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.FootnoteWithLinksDbFactory
import vf.word.database.storable.bible.FootnoteStatementLinkModel
import vf.word.model.combined.bible.FootnoteWithLinks

object ManyFootnoteWithLinksesAccess
{
	// NESTED	--------------------
	
	private class SubAccess(condition: Condition) extends ManyFootnoteWithLinksesAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return multiple footnote with linkses at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024
  */
trait ManyFootnoteWithLinksesAccess 
	extends ManyFootnotesAccessLike[FootnoteWithLinks, ManyFootnoteWithLinksesAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * footnote ids of the accessible footnote statement links
	  */
	def linkFootnoteIds(implicit connection: Connection) = 
		pullColumn(linkModel.footnoteId.column).map { v => v.getInt }
	
	/**
	  * statement ids of the accessible footnote statement links
	  */
	def linkStatementIds(implicit connection: Connection) = 
		pullColumn(linkModel.statementId.column).map { v => v.getInt }
	
	/**
	  * order indexs of the accessible footnote statement links
	  */
	def linkOrderIndexs(implicit connection: Connection) = 
		pullColumn(linkModel.orderIndex.column).map { v => v.getInt }
	
	/**
	  * Model (factory) used for interacting the footnote statement links associated with this footnote 
		with links
	  */
	protected def linkModel = FootnoteStatementLinkModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteWithLinksDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyFootnoteWithLinksesAccess = 
		new ManyFootnoteWithLinksesAccess.SubAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the footnote ids of the targeted footnote statement links
	  * @param newFootnoteId A new footnote id to assign
	  * @return Whether any footnote statement link was affected
	  */
	def linkFootnoteIds_=(newFootnoteId: Int)(implicit connection: Connection) = 
		putColumn(linkModel.footnoteId.column, newFootnoteId)
	
	/**
	  * Updates the order indexs of the targeted footnote statement links
	  * @param newOrderIndex A new order index to assign
	  * @return Whether any footnote statement link was affected
	  */
	def linkOrderIndexs_=(newOrderIndex: Int)(implicit connection: Connection) = 
		putColumn(linkModel.orderIndex.column, newOrderIndex)
	
	/**
	  * Updates the statement ids of the targeted footnote statement links
	  * @param newStatementId A new statement id to assign
	  * @return Whether any footnote statement link was affected
	  */
	def linkStatementIds_=(newStatementId: Int)(implicit connection: Connection) = 
		putColumn(linkModel.statementId.column, newStatementId)
}

