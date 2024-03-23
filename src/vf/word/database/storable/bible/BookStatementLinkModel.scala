package vf.word.database.storable.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.FromIdFactory
import utopia.vault.nosql.storable.StorableFactory
import vf.word.database.factory.bible.BookStatementLinkDbFactory
import vf.word.model.factory.bible.BookStatementLinkFactory
import vf.word.model.partial.bible.BookStatementLinkData
import vf.word.model.stored.bible.BookStatementLink

/**
  * Used for constructing BookStatementLinkModel instances and for inserting book statement links to the database
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object BookStatementLinkModel 
	extends StorableFactory[BookStatementLinkModel, BookStatementLink, BookStatementLinkData] 
		with BookStatementLinkFactory[BookStatementLinkModel] with FromIdFactory[Int, BookStatementLinkModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Property that contains book statement link book id
	  */
	lazy val bookId = property("bookId")
	/**
	  * Property that contains book statement link statement id
	  */
	lazy val statementId = property("statementId")
	/**
	  * Property that contains book statement link order index
	  */
	lazy val orderIndex = property("orderIndex")
	
	
	// COMPUTED	--------------------
	
	/**
	  * The factory object used by this model type
	  */
	def factory = BookStatementLinkDbFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: BookStatementLinkData) = 
		apply(None, Some(data.bookId), Some(data.statementId), Some(data.orderIndex))
	
	override def withId(id: Int) = apply(Some(id))
	
	override protected def complete(id: Value, data: BookStatementLinkData) = BookStatementLink(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param bookId Id of the book where the statement appears
	  * @return A model containing only the specified book id
	  */
	def withBookId(bookId: Int) = apply(bookId = Some(bookId))
	
	/**
	  * @param orderIndex Index that indicates, where in the book the linked statement appears
	  * @return A model containing only the specified order index
	  */
	def withOrderIndex(orderIndex: Int) = apply(orderIndex = Some(orderIndex))
	
	/**
	  * @param statementId Id of the statement made
	  * @return A model containing only the specified statement id
	  */
	def withStatementId(statementId: Int) = apply(statementId = Some(statementId))
}

/**
  * Used for interacting with BookStatementLinks in the database
  * @param id book statement link database id
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class BookStatementLinkModel(id: Option[Int] = None, bookId: Option[Int] = None, 
	statementId: Option[Int] = None, orderIndex: Option[Int] = None) 
	extends StorableWithFactory[BookStatementLink] with BookStatementLinkFactory[BookStatementLinkModel] 
		with FromIdFactory[Int, BookStatementLinkModel]
{
	// IMPLEMENTED	--------------------
	
	override def factory = BookStatementLinkModel.factory
	
	override def valueProperties = 
		Vector("id" -> id, BookStatementLinkModel.bookId.name -> bookId, 
			BookStatementLinkModel.statementId.name -> statementId, 
			BookStatementLinkModel.orderIndex.name -> orderIndex)
	
	override def withId(id: Int) = copy(id = Some(id))
	
	
	// OTHER	--------------------
	
	/**
	  * @param bookId Id of the book where the statement appears
	  * @return A model containing only the specified book id
	  */
	def withBookId(bookId: Int) = copy(bookId = Some(bookId))
	
	/**
	  * @param orderIndex Index that indicates, where in the book the linked statement appears
	  * @return A model containing only the specified order index
	  */
	def withOrderIndex(orderIndex: Int) = copy(orderIndex = Some(orderIndex))
	
	/**
	  * @param statementId Id of the statement made
	  * @return A model containing only the specified statement id
	  */
	def withStatementId(statementId: Int) = copy(statementId = Some(statementId))
}

