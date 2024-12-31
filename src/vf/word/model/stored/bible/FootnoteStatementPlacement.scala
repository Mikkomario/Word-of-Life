package vf.word.model.stored.bible

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.logos.model.partial.text.StatementPlacementData
import utopia.logos.model.stored.text.StoredStatementPlacementLike
import utopia.vault.model.template.StoredFromModelFactory
import vf.word.database.access.single.bible.footnote.statement.DbSingleFootnoteStatementPlacement
import vf.word.model.factory.bible.FootnoteStatementPlacementFactoryWrapper
import vf.word.model.partial.bible.FootnoteStatementPlacementData

object FootnoteStatementPlacement 
	extends StoredFromModelFactory[FootnoteStatementPlacementData, FootnoteStatementPlacement]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = FootnoteStatementPlacementData
	
	override protected def complete(model: AnyModel, data: FootnoteStatementPlacementData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a footnote statement placement that has already been stored in the database
  * @param id   id of this footnote statement placement in the database
  * @param data Wrapped footnote statement placement data
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class FootnoteStatementPlacement(id: Int, data: FootnoteStatementPlacementData) 
	extends FootnoteStatementPlacementFactoryWrapper[FootnoteStatementPlacementData, FootnoteStatementPlacement] 
		with StatementPlacementData 
		with StoredStatementPlacementLike[FootnoteStatementPlacementData, FootnoteStatementPlacement]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this footnote statement placement in the database
	  */
	def access = DbSingleFootnoteStatementPlacement(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: FootnoteStatementPlacementData) = copy(data = data)
}

