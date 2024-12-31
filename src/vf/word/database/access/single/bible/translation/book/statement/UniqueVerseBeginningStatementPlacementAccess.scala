package vf.word.database.access.single.bible.translation.book.statement

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.VerseBeginningStatementPlacementDbFactory
import vf.word.database.storable.bible.VerseMarkerDbModel
import vf.word.model.combined.bible.VerseBeginningStatementPlacement

object UniqueVerseBeginningStatementPlacementAccess 
	extends ViewFactory[UniqueVerseBeginningStatementPlacementAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueVerseBeginningStatementPlacementAccess = 
		_UniqueVerseBeginningStatementPlacementAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private
		 case class _UniqueVerseBeginningStatementPlacementAccess(override val accessCondition: Option[Condition]) 
		extends UniqueVerseBeginningStatementPlacementAccess
}

/**
  * A common trait for access points that return distinct verse beginning statement placements
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait UniqueVerseBeginningStatementPlacementAccess 
	extends UniqueBookStatementPlacementAccessLike[VerseBeginningStatementPlacement, UniqueVerseBeginningStatementPlacementAccess] 
		with SingleRowModelAccess[VerseBeginningStatementPlacement]
{
	// COMPUTED	--------------------
	
	/**
	  * A 1-based index that indicates which chapter this verse belongs to. 
	  * None if no verse marker (or value) was found.
	  */
	def verseMarkerChapterIndex(implicit connection: Connection) = 
		pullColumn(verseMarkerModel.chapterIndex.column).int
	
	/**
	  * A 1-based index that indicates which verse this is. 
	  * None if no verse marker (or value) was found.
	  */
	def verseMarkerVerseIndex(implicit connection: Connection) = 
		pullColumn(verseMarkerModel.verseIndex.column).int
	
	/**
	  * Id of the book statement that starts this verse. 
	  * None if no verse marker (or value) was found.
	  */
	def verseMarkerFirstStatementId(implicit connection: Connection) = 
		pullColumn(verseMarkerModel.firstStatementId.column).int
	
	/**
	  * A database model (factory) used for interacting with the linked verse marker
	  */
	protected def verseMarkerModel = VerseMarkerDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = VerseBeginningStatementPlacementDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): UniqueVerseBeginningStatementPlacementAccess = 
		UniqueVerseBeginningStatementPlacementAccess(condition)
}

