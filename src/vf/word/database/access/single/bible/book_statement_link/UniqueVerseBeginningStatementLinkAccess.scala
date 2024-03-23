package vf.word.database.access.single.bible.book_statement_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.VerseBeginningStatementLinkDbFactory
import vf.word.database.storable.bible.VerseMarkerModel
import vf.word.model.combined.bible.VerseBeginningStatementLink

object UniqueVerseBeginningStatementLinkAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueVerseBeginningStatementLinkAccess = 
		new _UniqueVerseBeginningStatementLinkAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueVerseBeginningStatementLinkAccess(condition: Condition) 
		extends UniqueVerseBeginningStatementLinkAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return distinct verse beginning statement links
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait UniqueVerseBeginningStatementLinkAccess 
	extends UniqueBookStatementLinkAccessLike[VerseBeginningStatementLink] 
		with SingleRowModelAccess[VerseBeginningStatementLink] 
		with FilterableView[UniqueVerseBeginningStatementLinkAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * A 1-based index that indicates which chapter this verse belongs to. None if no verse marker (or value)
	  *  was found.
	  */
	def verseMarkerChapterIndex(implicit connection: Connection) = 
		pullColumn(verseMarkerModel.chapterIndex.column).int
	
	/**
	  * A 1-based index that indicates which verse this is. None if no verse marker (or value) was found.
	  */
	def verseMarkerVerseIndex(implicit connection: Connection) = 
		pullColumn(verseMarkerModel.verseIndex.column).int
	
	/**
	  * Id of the book statement that starts this verse. None if no verse marker (or value) was found.
	  */
	def verseMarkerFirstStatementId(implicit connection: Connection) = 
		pullColumn(verseMarkerModel.firstStatementId.column).int
	
	/**
	  * A database model (factory) used for interacting with the linked verse marker
	  */
	protected def verseMarkerModel = VerseMarkerModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = VerseBeginningStatementLinkDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueVerseBeginningStatementLinkAccess = 
		new UniqueVerseBeginningStatementLinkAccess._UniqueVerseBeginningStatementLinkAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the chapter indexs of the targeted verse markers
	  * @param newChapterIndex A new chapter index to assign
	  * @return Whether any verse marker was affected
	  */
	def verseMarkerChapterIndex_=(newChapterIndex: Int)(implicit connection: Connection) = 
		putColumn(verseMarkerModel.chapterIndex.column, newChapterIndex)
	
	/**
	  * Updates the first statement ids of the targeted verse markers
	  * @param newFirstStatementId A new first statement id to assign
	  * @return Whether any verse marker was affected
	  */
	def verseMarkerFirstStatementId_=(newFirstStatementId: Int)(implicit connection: Connection) = 
		putColumn(verseMarkerModel.firstStatementId.column, newFirstStatementId)
	
	/**
	  * Updates the verse indexs of the targeted verse markers
	  * @param newVerseIndex A new verse index to assign
	  * @return Whether any verse marker was affected
	  */
	def verseMarkerVerseIndex_=(newVerseIndex: Int)(implicit connection: Connection) = 
		putColumn(verseMarkerModel.verseIndex.column, newVerseIndex)
}

