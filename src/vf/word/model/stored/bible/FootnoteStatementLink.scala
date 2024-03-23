package vf.word.model.stored.bible

import utopia.vault.model.template.{FromIdFactory, StoredModelConvertible}
import vf.word.database.access.single.bible.footnote_statement_link.DbSingleFootnoteStatementLink
import vf.word.model.factory.bible.FootnoteStatementLinkFactory
import vf.word.model.partial.bible.FootnoteStatementLinkData

/**
  * Represents a footnote statement link that has already been stored in the database
  * @param id id of this footnote statement link in the database
  * @param data Wrapped footnote statement link data
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class FootnoteStatementLink(id: Int, data: FootnoteStatementLinkData) 
	extends StoredModelConvertible[FootnoteStatementLinkData] 
		with FootnoteStatementLinkFactory[FootnoteStatementLink] 
		with FromIdFactory[Int, FootnoteStatementLink]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this footnote statement link in the database
	  */
	def access = DbSingleFootnoteStatementLink(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def withFootnoteId(footnoteId: Int) = copy(data = data.withFootnoteId(footnoteId))
	
	override def withId(id: Int) = copy(id = id)
	
	override def withOrderIndex(orderIndex: Int) = copy(data = data.withOrderIndex(orderIndex))
	
	override def withStatementId(statementId: Int) = copy(data = data.withStatementId(statementId))
}

