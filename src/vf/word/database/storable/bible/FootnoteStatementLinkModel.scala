package vf.word.database.storable.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.FromIdFactory
import utopia.vault.nosql.storable.StorableFactory
import vf.word.database.factory.bible.FootnoteStatementLinkDbFactory
import vf.word.model.factory.bible.FootnoteStatementLinkFactory
import vf.word.model.partial.bible.FootnoteStatementLinkData
import vf.word.model.stored.bible.FootnoteStatementLink

/**
  * Used for constructing FootnoteStatementLinkModel instances and for inserting footnote statement links
  *  to the database
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object FootnoteStatementLinkModel 
	extends StorableFactory[FootnoteStatementLinkModel, FootnoteStatementLink, FootnoteStatementLinkData] 
		with FootnoteStatementLinkFactory[FootnoteStatementLinkModel] with FromIdFactory[Int, FootnoteStatementLinkModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Property that contains footnote statement link footnote id
	  */
	lazy val footnoteId = property("footnoteId")
	
	/**
	  * Property that contains footnote statement link statement id
	  */
	lazy val statementId = property("statementId")
	
	/**
	  * Property that contains footnote statement link order index
	  */
	lazy val orderIndex = property("orderIndex")
	
	
	// COMPUTED	--------------------
	
	/**
	  * The factory object used by this model type
	  */
	def factory = FootnoteStatementLinkDbFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: FootnoteStatementLinkData) = 
		apply(None, Some(data.footnoteId), Some(data.statementId), Some(data.orderIndex))
	
	override def withId(id: Int) = apply(Some(id))
	
	override protected def complete(id: Value, data: FootnoteStatementLinkData) = 
		FootnoteStatementLink(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param footnoteId Id of the footnote where the statement is made
	  * @return A model containing only the specified footnote id
	  */
	def withFootnoteId(footnoteId: Int) = apply(footnoteId = Some(footnoteId))
	
	/**
	  * @param orderIndex A 0-based index that determines where the statement appears within the footnote
	  * @return A model containing only the specified order index
	  */
	def withOrderIndex(orderIndex: Int) = apply(orderIndex = Some(orderIndex))
	
	/**
	  * @param statementId Id of the statement made within the footnote
	  * @return A model containing only the specified statement id
	  */
	def withStatementId(statementId: Int) = apply(statementId = Some(statementId))
}

/**
  * Used for interacting with FootnoteStatementLinks in the database
  * @param id footnote statement link database id
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class FootnoteStatementLinkModel(id: Option[Int] = None, footnoteId: Option[Int] = None, 
	statementId: Option[Int] = None, orderIndex: Option[Int] = None) 
	extends StorableWithFactory[FootnoteStatementLink] 
		with FootnoteStatementLinkFactory[FootnoteStatementLinkModel] 
		with FromIdFactory[Int, FootnoteStatementLinkModel]
{
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteStatementLinkModel.factory
	
	override def valueProperties = 
		Vector("id" -> id, FootnoteStatementLinkModel.footnoteId.name -> footnoteId, 
			FootnoteStatementLinkModel.statementId.name -> statementId, 
			FootnoteStatementLinkModel.orderIndex.name -> orderIndex)
	
	override def withId(id: Int) = copy(id = Some(id))
	
	
	// OTHER	--------------------
	
	/**
	  * @param footnoteId Id of the footnote where the statement is made
	  * @return A model containing only the specified footnote id
	  */
	def withFootnoteId(footnoteId: Int) = copy(footnoteId = Some(footnoteId))
	
	/**
	  * @param orderIndex A 0-based index that determines where the statement appears within the footnote
	  * @return A model containing only the specified order index
	  */
	def withOrderIndex(orderIndex: Int) = copy(orderIndex = Some(orderIndex))
	
	/**
	  * @param statementId Id of the statement made within the footnote
	  * @return A model containing only the specified statement id
	  */
	def withStatementId(statementId: Int) = copy(statementId = Some(statementId))
}

