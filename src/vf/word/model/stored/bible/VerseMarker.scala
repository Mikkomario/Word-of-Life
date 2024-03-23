package vf.word.model.stored.bible

import utopia.vault.model.template.{FromIdFactory, StoredModelConvertible}
import vf.word.database.access.single.bible.verse_marker.DbSingleVerseMarker
import vf.word.model.factory.bible.VerseMarkerFactory
import vf.word.model.partial.bible.VerseMarkerData

/**
  * Represents a verse marker that has already been stored in the database
  * @param id id of this verse marker in the database
  * @param data Wrapped verse marker data
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class VerseMarker(id: Int, data: VerseMarkerData) 
	extends StoredModelConvertible[VerseMarkerData] with VerseMarkerFactory[VerseMarker] 
		with FromIdFactory[Int, VerseMarker]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this verse marker in the database
	  */
	def access = DbSingleVerseMarker(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def withChapterIndex(chapterIndex: Int) = copy(data = data.withChapterIndex(chapterIndex))
	
	override def withFirstStatementId(firstStatementId: Int) = 
		copy(data = data.withFirstStatementId(firstStatementId))
	
	override def withId(id: Int) = copy(id = id)
	
	override def withVerseIndex(verseIndex: Int) = copy(data = data.withVerseIndex(verseIndex))
}

