package vf.word.model.stored.bible

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.logos.model.partial.text.StatementPlacementData
import utopia.logos.model.stored.text.StoredStatementPlacementLike
import utopia.vault.model.template.StoredFromModelFactory
import vf.word.database.access.single.bible.translation.book.statement.DbSingleBookStatementPlacement
import vf.word.model.factory.bible.BookStatementPlacementFactoryWrapper
import vf.word.model.partial.bible.BookStatementPlacementData

object BookStatementPlacement 
	extends StoredFromModelFactory[BookStatementPlacementData, BookStatementPlacement]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = BookStatementPlacementData
	
	override protected def complete(model: AnyModel, data: BookStatementPlacementData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a book statement placement that has already been stored in the database
  * @param id   id of this book statement placement in the database
  * @param data Wrapped book statement placement data
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class BookStatementPlacement(id: Int, data: BookStatementPlacementData) 
	extends BookStatementPlacementFactoryWrapper[BookStatementPlacementData, BookStatementPlacement] 
		with StatementPlacementData 
		with StoredStatementPlacementLike[BookStatementPlacementData, BookStatementPlacement]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this book statement placement in the database
	  */
	def access = DbSingleBookStatementPlacement(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def withId(id: Int) = copy(id = id)
	
	override protected def wrap(data: BookStatementPlacementData) = copy(data = data)
}

