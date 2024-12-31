package vf.word.database.factory.bible

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.logos.database.factory.text.StatementPlacementDbFactoryLike
import utopia.vault.sql.OrderBy
import vf.word.database.storable.bible.BookStatementPlacementDbModel
import vf.word.model.partial.bible.BookStatementPlacementData
import vf.word.model.stored.bible.BookStatementPlacement

/**
  * Used for reading book statement placement data from the DB
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object BookStatementPlacementDbFactory extends StatementPlacementDbFactoryLike[BookStatementPlacement]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	override val dbProps = BookStatementPlacementDbModel
	
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
		BookStatementPlacement(id, BookStatementPlacementData(parentId, statementId, orderIndex))
}

