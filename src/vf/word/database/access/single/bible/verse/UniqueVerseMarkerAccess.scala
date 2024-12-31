package vf.word.database.access.single.bible.verse

import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.VerseMarkerDbFactory
import vf.word.database.storable.bible.VerseMarkerDbModel
import vf.word.model.stored.bible.VerseMarker

object UniqueVerseMarkerAccess extends ViewFactory[UniqueVerseMarkerAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueVerseMarkerAccess = 
		_UniqueVerseMarkerAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueVerseMarkerAccess(override val accessCondition: Option[Condition]) 
		extends UniqueVerseMarkerAccess
}

/**
  * A common trait for access points that return individual and distinct verse markers.
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait UniqueVerseMarkerAccess 
	extends SingleRowModelAccess[VerseMarker] 
		with DistinctModelAccess[VerseMarker, Option[VerseMarker], Value] 
		with FilterableView[UniqueVerseMarkerAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * A 1-based index that indicates which chapter this verse belongs to. 
	  * None if no verse marker (or value) was found.
	  */
	def chapterIndex(implicit connection: Connection) = pullColumn(model.chapterIndex.column).int
	
	/**
	  * A 1-based index that indicates which verse this is. 
	  * None if no verse marker (or value) was found.
	  */
	def verseIndex(implicit connection: Connection) = pullColumn(model.verseIndex.column).int
	
	/**
	  * Id of the book statement that starts this verse. 
	  * None if no verse marker (or value) was found.
	  */
	def firstStatementId(implicit connection: Connection) = pullColumn(model.firstStatementId.column).int
	
	/**
	  * Unique id of the accessible verse marker. None if no verse marker was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = VerseMarkerDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = VerseMarkerDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): UniqueVerseMarkerAccess = UniqueVerseMarkerAccess(condition)
}

