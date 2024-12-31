package vf.word.database.access.many.bible.translation.book.statement

import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.VerseBeginningStatementPlacementDbFactory
import vf.word.database.storable.bible.VerseMarkerDbModel
import vf.word.model.combined.bible.VerseBeginningStatementPlacement

object ManyVerseBeginningStatementPlacementsAccess 
	extends ViewFactory[ManyVerseBeginningStatementPlacementsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyVerseBeginningStatementPlacementsAccess = 
		_ManyVerseBeginningStatementPlacementsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private
		 case class _ManyVerseBeginningStatementPlacementsAccess(override val accessCondition: Option[Condition]) 
		extends ManyVerseBeginningStatementPlacementsAccess
}

/**
  * A common trait for access points that return multiple verse beginning statement placements at 
  * a time
  * @author Mikko Hilpinen
  * @since 30.12.2024
  */
trait ManyVerseBeginningStatementPlacementsAccess 
	extends ManyBookStatementPlacementsAccessLike[VerseBeginningStatementPlacement, ManyVerseBeginningStatementPlacementsAccess] 
		with ManyRowModelAccess[VerseBeginningStatementPlacement]
{
	// COMPUTED	--------------------
	
	/**
	  * chapter indices of the accessible verse markers
	  */
	def verseMarkerChapterIndices(implicit connection: Connection) = 
		pullColumn(verseMarkerModel.chapterIndex.column).map { v => v.getInt }
	
	/**
	  * verse indices of the accessible verse markers
	  */
	def verseMarkerVerseIndices(implicit connection: Connection) = 
		pullColumn(verseMarkerModel.verseIndex.column).map { v => v.getInt }
	
	/**
	  * first statement ids of the accessible verse markers
	  */
	def verseMarkerFirstStatementIds(implicit connection: Connection) = 
		pullColumn(verseMarkerModel.firstStatementId.column).map { v => v.getInt }
	
	/**
	  * Model (factory) used for interacting the verse markers associated with this verse beginning 
	  * statement placement
	  */
	protected def verseMarkerModel = VerseMarkerDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = VerseBeginningStatementPlacementDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyVerseBeginningStatementPlacementsAccess = 
		ManyVerseBeginningStatementPlacementsAccess(condition)
}

