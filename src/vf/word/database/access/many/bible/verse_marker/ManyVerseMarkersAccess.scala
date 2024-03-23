package vf.word.database.access.many.bible.verse_marker

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.VerseMarkerDbFactory
import vf.word.database.storable.bible.VerseMarkerModel
import vf.word.model.stored.bible.VerseMarker

object ManyVerseMarkersAccess
{
	// NESTED	--------------------
	
	private class ManyVerseMarkersSubView(condition: Condition) extends ManyVerseMarkersAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points which target multiple verse markers at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait ManyVerseMarkersAccess 
	extends ManyRowModelAccess[VerseMarker] with FilterableView[ManyVerseMarkersAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * chapter indexs of the accessible verse markers
	  */
	def chapterIndices(implicit connection: Connection) =
		pullColumn(model.chapterIndex.column).map { v => v.getInt }
	
	/**
	  * verse indexs of the accessible verse markers
	  */
	def verseIndices(implicit connection: Connection) = pullColumn(model.verseIndex.column)
		.map { v => v.getInt }
	
	/**
	  * first statement ids of the accessible verse markers
	  */
	def firstStatementIds(implicit connection: Connection) = 
		pullColumn(model.firstStatementId.column).map { v => v.getInt }
	
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = VerseMarkerModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = VerseMarkerDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyVerseMarkersAccess = 
		new ManyVerseMarkersAccess.ManyVerseMarkersSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the chapter indexs of the targeted verse markers
	  * @param newChapterIndex A new chapter index to assign
	  * @return Whether any verse marker was affected
	  */
	def chapterIndices_=(newChapterIndex: Int)(implicit connection: Connection) =
		putColumn(model.chapterIndex.column, newChapterIndex)
	
	/**
	  * Updates the first statement ids of the targeted verse markers
	  * @param newFirstStatementId A new first statement id to assign
	  * @return Whether any verse marker was affected
	  */
	def firstStatementIds_=(newFirstStatementId: Int)(implicit connection: Connection) = 
		putColumn(model.firstStatementId.column, newFirstStatementId)
	
	/**
	  * @param chapterIndex chapter index to target
	  * @return Copy of this access point that only includes verse markers with the specified chapter index
	  */
	def inChapter(chapterIndex: Int) = filter(model.chapterIndex.column <=> chapterIndex)
	
	/**
	  * @param chapterIndices Targeted chapter indexs
	  * @return Copy of this access point that only includes verse markers where chapter index is within the
	  *  specified value set
	  */
	def inChapters(chapterIndices: Iterable[Int]) =
		filter(model.chapterIndex.column.in(chapterIndices))
	
	/**
	  * Updates the verse indexs of the targeted verse markers
	  * @param newVerseIndex A new verse index to assign
	  * @return Whether any verse marker was affected
	  */
	def verseIndices_=(newVerseIndex: Int)(implicit connection: Connection) =
		putColumn(model.verseIndex.column, newVerseIndex)
	
	/**
	  * @param verseIndex verse index to target
	  * @return Copy of this access point that only includes verse markers with the specified verse index
	  */
	def withIndex(verseIndex: Int) = filter(model.verseIndex.column <=> verseIndex)
	
	/**
	  * @param verseIndices Targeted verse indexs
	  * @return Copy of this access point that only includes verse markers where verse index is within the
	  *  specified value set
	  */
	def withIndices(verseIndices: Iterable[Int]) = filter(model.verseIndex.column.in(verseIndices))
}

