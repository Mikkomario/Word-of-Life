package vf.word.database.access.single.bible.verse_marker

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.VerseMarkerDbFactory
import vf.word.database.storable.bible.VerseMarkerModel
import vf.word.model.stored.bible.VerseMarker

object UniqueVerseMarkerAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueVerseMarkerAccess = new _UniqueVerseMarkerAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueVerseMarkerAccess(condition: Condition) extends UniqueVerseMarkerAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct verse markers.
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait UniqueVerseMarkerAccess 
	extends SingleRowModelAccess[VerseMarker] with FilterableView[UniqueVerseMarkerAccess] 
		with DistinctModelAccess[VerseMarker, Option[VerseMarker], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A 1-based index that indicates which chapter this verse belongs to. None if no verse marker (or value)
	  *  was found.
	  */
	def chapterIndex(implicit connection: Connection) = pullColumn(model.chapterIndex.column).int
	
	/**
	  * A 1-based index that indicates which verse this is. None if no verse marker (or value) was found.
	  */
	def verseIndex(implicit connection: Connection) = pullColumn(model.verseIndex.column).int
	
	/**
	  * Id of the book statement that starts this verse. None if no verse marker (or value) was found.
	  */
	def firstStatementId(implicit connection: Connection) = pullColumn(model.firstStatementId.column).int
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = VerseMarkerModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = VerseMarkerDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueVerseMarkerAccess = 
		new UniqueVerseMarkerAccess._UniqueVerseMarkerAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the chapter indexs of the targeted verse markers
	  * @param newChapterIndex A new chapter index to assign
	  * @return Whether any verse marker was affected
	  */
	def chapterIndex_=(newChapterIndex: Int)(implicit connection: Connection) = 
		putColumn(model.chapterIndex.column, newChapterIndex)
	
	/**
	  * Updates the first statement ids of the targeted verse markers
	  * @param newFirstStatementId A new first statement id to assign
	  * @return Whether any verse marker was affected
	  */
	def firstStatementId_=(newFirstStatementId: Int)(implicit connection: Connection) = 
		putColumn(model.firstStatementId.column, newFirstStatementId)
	
	/**
	  * Updates the verse indexs of the targeted verse markers
	  * @param newVerseIndex A new verse index to assign
	  * @return Whether any verse marker was affected
	  */
	def verseIndex_=(newVerseIndex: Int)(implicit connection: Connection) = 
		putColumn(model.verseIndex.column, newVerseIndex)
}

