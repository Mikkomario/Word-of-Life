package vf.word.model.stored.bible

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.model.template.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.word.database.access.single.bible.footnote.DbSingleFootnote
import vf.word.model.factory.bible.{FootnoteFactory, FootnoteFactoryWrapper}
import vf.word.model.partial.bible.FootnoteData

object Footnote extends StoredFromModelFactory[FootnoteData, Footnote]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = FootnoteData
	
	override protected def complete(model: AnyModel, data: FootnoteData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a footnote that has already been stored in the database
  * @param id   id of this footnote in the database
  * @param data Wrapped footnote data
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class Footnote(id: Int, data: FootnoteData) 
	extends StoredModelConvertible[FootnoteData] with FromIdFactory[Int, Footnote]
		with FootnoteFactoryWrapper[FootnoteData, Footnote]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this footnote in the database
	  */
	def access = DbSingleFootnote(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int): Footnote = copy(id = id)
	
	override protected def wrap(data: FootnoteData) = copy(data = data)
}

