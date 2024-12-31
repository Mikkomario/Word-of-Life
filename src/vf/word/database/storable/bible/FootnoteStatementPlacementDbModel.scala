package vf.word.database.storable.bible

import utopia.flow.generic.model.immutable.Value
import utopia.logos.database.props.text.StatementPlacementDbProps
import utopia.logos.database.storable.text.{StatementPlacementDbModel, StatementPlacementDbModelFactoryLike, StatementPlacementDbModelLike}
import utopia.vault.model.immutable.DbPropertyDeclaration
import vf.word.database.WordOfLifeTables
import vf.word.model.factory.bible.FootnoteStatementPlacementFactory
import vf.word.model.partial.bible.FootnoteStatementPlacementData
import vf.word.model.stored.bible.FootnoteStatementPlacement

/**
  * Used for constructing FootnoteStatementPlacementDbModel instances and for inserting footnote 
  * statement placements to the database
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object FootnoteStatementPlacementDbModel 
	extends StatementPlacementDbModelFactoryLike[FootnoteStatementPlacementDbModel, FootnoteStatementPlacement, FootnoteStatementPlacementData] 
		with FootnoteStatementPlacementFactory[FootnoteStatementPlacementDbModel] 
		with StatementPlacementDbProps
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with parent ids
	  */
	override lazy val parentId = property("parentId")
	
	/**
	  * Database property used for interacting with statement ids
	  */
	override lazy val statementId = property("statementId")
	
	/**
	  * Database property used for interacting with order indices
	  */
	override lazy val orderIndex = property("orderIndex")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = WordOfLifeTables.footnoteStatementPlacement
	
	override def apply(data: FootnoteStatementPlacementData): FootnoteStatementPlacementDbModel = 
		apply(None, Some(data.parentId), Some(data.statementId), Some(data.orderIndex))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param orderIndex 0-based index that indicates the specific location of the placed text
	  * @return A model containing only the specified order index
	  */
	override def withOrderIndex(orderIndex: Int) = apply(orderIndex = Some(orderIndex))
	
	/**
	  * @param parentId Id of the footnote where the statement is made
	  * @return A model containing only the specified parent id
	  */
	override def withParentId(parentId: Int) = apply(parentId = Some(parentId))
	
	/**
	  * @param statementId Id of the statement which appears within the linked text
	  * @return A model containing only the specified statement id
	  */
	override def withStatementId(statementId: Int) = apply(statementId = Some(statementId))
	
	override protected def complete(id: Value, data: FootnoteStatementPlacementData) = 
		FootnoteStatementPlacement(id.getInt, data)
}

/**
  * Used for interacting with FootnoteStatementPlacements in the database
  * @param id footnote statement placement database id
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class FootnoteStatementPlacementDbModel(id: Option[Int] = None, parentId: Option[Int] = None, 
	statementId: Option[Int] = None, orderIndex: Option[Int] = None) 
	extends StatementPlacementDbModel with StatementPlacementDbModelLike[FootnoteStatementPlacementDbModel] 
		with FootnoteStatementPlacementFactory[FootnoteStatementPlacementDbModel]
{
	// IMPLEMENTED	--------------------
	
	override def dbProps = FootnoteStatementPlacementDbModel
	
	override def table = FootnoteStatementPlacementDbModel.table
	
	/**
	  * @param id          Id to assign to the new model (default = currently assigned id)
	  * @param parentId    parent id to assign to the new model (default = currently assigned value)
	  * @param statementId statement id to assign to the new model (default = currently assigned 
	  *                    value)
	  * @param orderIndex  order index to assign to the new model (default = currently assigned value)
	  */
	override def copyStatementPlacement(id: Option[Int] = id, parentId: Option[Int] = parentId, 
		statementId: Option[Int] = statementId, orderIndex: Option[Int] = orderIndex) = 
		copy(id = id, parentId = parentId, statementId = statementId, orderIndex = orderIndex)
}

