package vf.word.model.stored.bible

import utopia.vault.model.template.{FromIdFactory, StoredModelConvertible}
import vf.word.database.access.single.bible.footnote.DbSingleFootnote
import vf.word.model.factory.bible.FootnoteFactory
import vf.word.model.partial.bible.FootnoteData

/**
  * Represents a footnote that has already been stored in the database
  * @param id id of this footnote in the database
  * @param data Wrapped footnote data
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class Footnote(id: Int, data: FootnoteData) 
	extends StoredModelConvertible[FootnoteData] with FootnoteFactory[Footnote] 
		with FromIdFactory[Int, Footnote]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this footnote in the database
	  */
	def access = DbSingleFootnote(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def withCommentedStatementId(commentedStatementId: Int) = 
		copy(data = data.withCommentedStatementId(commentedStatementId))
	
	override def withId(id: Int) = copy(id = id)
	
	override def withTargetedWordIndex(targetedWordIndex: Int) = 
		copy(data = data.withTargetedWordIndex(targetedWordIndex))
}

