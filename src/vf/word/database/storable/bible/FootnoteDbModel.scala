package vf.word.database.storable.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{FromIdFactory, HasId, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import vf.word.database.WordOfLifeTables
import vf.word.model.factory.bible.FootnoteFactory
import vf.word.model.partial.bible.FootnoteData
import vf.word.model.stored.bible.Footnote

/**
  * Used for constructing FootnoteDbModel instances and for inserting footnotes to the database
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object FootnoteDbModel 
	extends StorableFactory[FootnoteDbModel, Footnote, FootnoteData] with FromIdFactory[Int, FootnoteDbModel] 
		with HasIdProperty with FootnoteFactory[FootnoteDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with commented statement ids
	  */
	lazy val commentedStatementId = property("commentedStatementId")
	
	/**
	  * Database property used for interacting with targeted word indices
	  */
	lazy val targetedWordIndex = property("targetedWordIndex")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = WordOfLifeTables.footnote
	
	override def apply(data: FootnoteData): FootnoteDbModel = 
		apply(None, Some(data.commentedStatementId), data.targetedWordIndex)
	
	/**
	  * @param commentedStatementId Id of the specific statement this footnote comments on
	  * @return A model containing only the specified commented statement id
	  */
	override def withCommentedStatementId(commentedStatementId: Int) = 
		apply(commentedStatementId = Some(commentedStatementId))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param targetedWordIndex A 0-based index that specifies the word targeted within this 
	  *                          statement. 
	  *                          None if no specific word was targeted.
	  * @return A model containing only the specified targeted word index
	  */
	override def withTargetedWordIndex(targetedWordIndex: Int) = 
		apply(targetedWordIndex = Some(targetedWordIndex))
	
	override protected def complete(id: Value, data: FootnoteData) = Footnote(id.getInt, data)
}

/**
  * Used for interacting with Footnotes in the database
  * @param id footnote database id
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class FootnoteDbModel(id: Option[Int] = None, commentedStatementId: Option[Int] = None, 
	targetedWordIndex: Option[Int] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, FootnoteDbModel] 
		with FootnoteFactory[FootnoteDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties = 
		Vector(FootnoteDbModel.id.name -> id, 
			FootnoteDbModel.commentedStatementId.name -> commentedStatementId, 
			FootnoteDbModel.targetedWordIndex.name -> targetedWordIndex)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = FootnoteDbModel.table
	
	/**
	  * @param commentedStatementId Id of the specific statement this footnote comments on
	  * @return A new copy of this model with the specified commented statement id
	  */
	override def withCommentedStatementId(commentedStatementId: Int) = 
		copy(commentedStatementId = Some(commentedStatementId))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	/**
	  * @param targetedWordIndex A 0-based index that specifies the word targeted within this 
	  *                          statement. 
	  *                          None if no specific word was targeted.
	  * @return A new copy of this model with the specified targeted word index
	  */
	override def withTargetedWordIndex(targetedWordIndex: Int) = copy(targetedWordIndex = 
		Some(targetedWordIndex))
}

