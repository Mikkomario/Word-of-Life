package vf.word.database.access.many.bible.book_statement_link

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.VerseBeginningStatementLinkDbFactory
import vf.word.database.storable.bible.VerseMarkerModel
import vf.word.model.combined.bible.VerseBeginningStatementLink

object ManyVerseBeginningStatementLinksAccess
{
	// NESTED	--------------------
	
	private class SubAccess(condition: Condition) extends ManyVerseBeginningStatementLinksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return multiple verse beginning statement links at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024
  */
trait ManyVerseBeginningStatementLinksAccess 
	extends ManyBookStatementLinksAccessLike[VerseBeginningStatementLink, ManyVerseBeginningStatementLinksAccess] 
		with ManyRowModelAccess[VerseBeginningStatementLink]
{
	// COMPUTED	--------------------
	
	/**
	  * chapter indexs of the accessible verse markers
	  */
	def verseMarkerChapterIndexs(implicit connection: Connection) = 
		pullColumn(verseMarkerModel.chapterIndex.column).map { v => v.getInt }
	
	/**
	  * verse indexs of the accessible verse markers
	  */
	def verseMarkerVerseIndexs(implicit connection: Connection) = 
		pullColumn(verseMarkerModel.verseIndex.column).map { v => v.getInt }
	
	/**
	  * first statement ids of the accessible verse markers
	  */
	def verseMarkerFirstStatementIds(implicit connection: Connection) = 
		pullColumn(verseMarkerModel.firstStatementId.column).map { v => v.getInt }
	
	/**
	  * Model (factory) used for interacting the verse markers associated 
		with this verse beginning statement link
	  */
	protected def verseMarkerModel = VerseMarkerModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = VerseBeginningStatementLinkDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyVerseBeginningStatementLinksAccess = 
		new ManyVerseBeginningStatementLinksAccess.SubAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the chapter indexs of the targeted verse markers
	  * @param newChapterIndex A new chapter index to assign
	  * @return Whether any verse marker was affected
	  */
	def verseMarkerChapterIndexs_=(newChapterIndex: Int)(implicit connection: Connection) = 
		putColumn(verseMarkerModel.chapterIndex.column, newChapterIndex)
	
	/**
	  * Updates the first statement ids of the targeted verse markers
	  * @param newFirstStatementId A new first statement id to assign
	  * @return Whether any verse marker was affected
	  */
	def verseMarkerFirstStatementIds_=(newFirstStatementId: Int)(implicit connection: Connection) = 
		putColumn(verseMarkerModel.firstStatementId.column, newFirstStatementId)
	
	/**
	  * Updates the verse indexs of the targeted verse markers
	  * @param newVerseIndex A new verse index to assign
	  * @return Whether any verse marker was affected
	  */
	def verseMarkerVerseIndexs_=(newVerseIndex: Int)(implicit connection: Connection) = 
		putColumn(verseMarkerModel.verseIndex.column, newVerseIndex)
}

