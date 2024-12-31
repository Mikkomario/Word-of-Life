package vf.word.database.access.many.bible.verse

import utopia.flow.collection.immutable.IntSet
import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.VerseMarkerDbFactory
import vf.word.database.storable.bible.VerseMarkerDbModel
import vf.word.model.stored.bible.VerseMarker

object ManyVerseMarkersAccess extends ViewFactory[ManyVerseMarkersAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyVerseMarkersAccess = 
		_ManyVerseMarkersAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyVerseMarkersAccess(override val accessCondition: Option[Condition]) 
		extends ManyVerseMarkersAccess
}

/**
  * A common trait for access points which target multiple verse markers at a time
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait ManyVerseMarkersAccess 
	extends ManyRowModelAccess[VerseMarker] with FilterableView[ManyVerseMarkersAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * chapter indices of the accessible verse markers
	  */
	def chapterIndices(implicit connection: Connection) = 
		pullColumn(model.chapterIndex.column).map { v => v.getInt }
	
	/**
	  * verse indices of the accessible verse markers
	  */
	def verseIndices(implicit connection: Connection) = 
		pullColumn(model.verseIndex.column).map { v => v.getInt }
	
	/**
	  * first statement ids of the accessible verse markers
	  */
	def firstStatementIds(implicit connection: Connection) = 
		pullColumn(model.firstStatementId.column).map { v => v.getInt }
	
	/**
	  * Unique ids of the accessible verse markers
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = VerseMarkerDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = VerseMarkerDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyVerseMarkersAccess = ManyVerseMarkersAccess(condition)
	
	
	// OTHER	--------------------
	
	/**
	  * @param chapterIndex chapter index to target
	  * @return Copy of this access point that only includes verse markers with the specified chapter index
	  */
	def inChapter(chapterIndex: Int) = filter(model.chapterIndex.column <=> chapterIndex)
	
	/**
	  * @param chapterIndices Targeted chapter indices
	  * @return Copy of this access point that only includes verse markers where chapter index is within the 
	  * specified value set
	  */
	def inChapters(chapterIndices: IterableOnce[Int]) = 
		filter(model.chapterIndex.column.in(IntSet.from(chapterIndices)))
	
	/**
	  * @param verseIndex verse index to target
	  * @return Copy of this access point that only includes verse markers with the specified verse index
	  */
	def withIndex(verseIndex: Int) = filter(model.verseIndex.column <=> verseIndex)
	
	/**
	  * @param verseIndices Targeted verse indices
	  * @return Copy of this access point that only includes verse markers where verse index is within the 
	  * specified value set
	  */
	def withIndices(verseIndices: IterableOnce[Int]) = 
		filter(model.verseIndex.column.in(IntSet.from(verseIndices)))
}

