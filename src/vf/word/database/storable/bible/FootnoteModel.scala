package vf.word.database.storable.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.FromIdFactory
import utopia.vault.nosql.storable.StorableFactory
import vf.word.database.factory.bible.FootnoteDbFactory
import vf.word.model.factory.bible.FootnoteFactory
import vf.word.model.partial.bible.FootnoteData
import vf.word.model.stored.bible.Footnote

/**
  * Used for constructing FootnoteModel instances and for inserting footnotes to the database
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object FootnoteModel 
	extends StorableFactory[FootnoteModel, Footnote, FootnoteData] with FootnoteFactory[FootnoteModel]
		with FromIdFactory[Int, FootnoteModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Property that contains footnote commented statement id
	  */
	lazy val commentedStatementId = property("commentedStatementId")
	
	/**
	  * Property that contains footnote targeted word index
	  */
	lazy val targetedWordIndex = property("targetedWordIndex")
	
	
	// COMPUTED	--------------------
	
	/**
	  * The factory object used by this model type
	  */
	def factory = FootnoteDbFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: FootnoteData) = apply(None, Some(data.commentedStatementId), 
		data.targetedWordIndex)
	
	override def withId(id: Int) = apply(Some(id))
	
	override protected def complete(id: Value, data: FootnoteData) = Footnote(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param commentedStatementId Id of the specific statement this footnote comments on
	  * @return A model containing only the specified commented statement id
	  */
	def withCommentedStatementId(commentedStatementId: Int) = 
		apply(commentedStatementId = Some(commentedStatementId))
	
	/**
	  * @param targetedWordIndex A 0-based index that specifies the word targeted within this statement. None if
	  *  no specific word was targeted.
	  * @return A model containing only the specified targeted word index
	  */
	def withTargetedWordIndex(targetedWordIndex: Int) = apply(targetedWordIndex = Some(targetedWordIndex))
}

/**
  * Used for interacting with Footnotes in the database
  * @param id footnote database id
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class FootnoteModel(id: Option[Int] = None, commentedStatementId: Option[Int] = None, 
	targetedWordIndex: Option[Int] = None) 
	extends StorableWithFactory[Footnote] with FootnoteFactory[FootnoteModel] 
		with FromIdFactory[Int, FootnoteModel]
{
	// IMPLEMENTED	--------------------
	
	override def factory = FootnoteModel.factory
	
	override def valueProperties = 
		Vector("id" -> id, FootnoteModel.commentedStatementId.name -> commentedStatementId, 
			FootnoteModel.targetedWordIndex.name -> targetedWordIndex)
	
	override def withId(id: Int) = copy(id = Some(id))
	
	
	// OTHER	--------------------
	
	/**
	  * @param commentedStatementId Id of the specific statement this footnote comments on
	  * @return A model containing only the specified commented statement id
	  */
	def withCommentedStatementId(commentedStatementId: Int) = 
		copy(commentedStatementId = Some(commentedStatementId))
	
	/**
	  * 
		@param targetedWordIndex A 0-based index that specifies the word targeted within this statement. None if
	  *  no specific word was targeted.
	  * @return A model containing only the specified targeted word index
	  */
	def withTargetedWordIndex(targetedWordIndex: Int) = copy(targetedWordIndex = Some(targetedWordIndex))
}

