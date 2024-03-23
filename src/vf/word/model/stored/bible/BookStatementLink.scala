package vf.word.model.stored.bible

import utopia.vault.model.template.{FromIdFactory, StoredModelConvertible}
import vf.word.database.access.single.bible.book_statement_link.DbSingleBookStatementLink
import vf.word.model.factory.bible.BookStatementLinkFactory
import vf.word.model.partial.bible.BookStatementLinkData

/**
  * Represents a book statement link that has already been stored in the database
  * @param id id of this book statement link in the database
  * @param data Wrapped book statement link data
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class BookStatementLink(id: Int, data: BookStatementLinkData) 
	extends StoredModelConvertible[BookStatementLinkData] with BookStatementLinkFactory[BookStatementLink] 
		with FromIdFactory[Int, BookStatementLink]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this book statement link in the database
	  */
	def access = DbSingleBookStatementLink(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def withBookId(bookId: Int) = copy(data = data.withBookId(bookId))
	
	override def withId(id: Int) = copy(id = id)
	
	override def withOrderIndex(orderIndex: Int) = copy(data = data.withOrderIndex(orderIndex))
	
	override def withStatementId(statementId: Int) = copy(data = data.withStatementId(statementId))
}

