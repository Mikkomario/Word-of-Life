package vf.word.database.factory.bible

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.logos.database.factory.text.StatementPlacementDbFactoryLike
import utopia.vault.sql.OrderBy
import vf.word.database.storable.bible.FootnoteStatementPlacementDbModel
import vf.word.model.partial.bible.FootnoteStatementPlacementData
import vf.word.model.stored.bible.FootnoteStatementPlacement

/**
  * Used for reading footnote statement placement data from the DB
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object FootnoteStatementPlacementDbFactory extends StatementPlacementDbFactoryLike[FootnoteStatementPlacement]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = FootnoteStatementPlacementDbModel
	
	override lazy val defaultOrdering: Option[OrderBy] = None
	
	
	// IMPLEMENTED	--------------------
	
	override def table = dbProps.table
	
	/**
	  * @param model       Model from which additional data may be read
	  * @param id          Id to assign to the read/parsed statement placement
	  * @param parentId    parent id to assign to the new statement placement
	  * @param statementId statement id to assign to the new statement placement
	  * @param orderIndex  order index to assign to the new statement placement
	  */
	override protected def apply(model: AnyModel, id: Int, parentId: Int, statementId: Int, orderIndex: Int) =
		FootnoteStatementPlacement(id, FootnoteStatementPlacementData(parentId, statementId, orderIndex))
}

