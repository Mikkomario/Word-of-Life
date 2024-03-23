package vf.word.database.storable.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.FromIdFactory
import utopia.vault.nosql.storable.StorableFactory
import vf.word.database.factory.bible.VerseMarkerDbFactory
import vf.word.model.factory.bible.VerseMarkerFactory
import vf.word.model.partial.bible.VerseMarkerData
import vf.word.model.stored.bible.VerseMarker

/**
  * Used for constructing VerseMarkerModel instances and for inserting verse markers to the database
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object VerseMarkerModel 
	extends StorableFactory[VerseMarkerModel, VerseMarker, VerseMarkerData] 
		with VerseMarkerFactory[VerseMarkerModel] with FromIdFactory[Int, VerseMarkerModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Property that contains verse marker chapter index
	  */
	lazy val chapterIndex = property("chapterIndex")
	
	/**
	  * Property that contains verse marker verse index
	  */
	lazy val verseIndex = property("verseIndex")
	
	/**
	  * Property that contains verse marker first statement id
	  */
	lazy val firstStatementId = property("firstStatementId")
	
	
	// COMPUTED	--------------------
	
	/**
	  * The factory object used by this model type
	  */
	def factory = VerseMarkerDbFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: VerseMarkerData) = 
		apply(None, Some(data.chapterIndex), Some(data.verseIndex), Some(data.firstStatementId))
	
	override def withId(id: Int) = apply(Some(id))
	
	override protected def complete(id: Value, data: VerseMarkerData) = VerseMarker(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param chapterIndex A 1-based index that indicates which chapter this verse belongs to
	  * @return A model containing only the specified chapter index
	  */
	def withChapterIndex(chapterIndex: Int) = apply(chapterIndex = Some(chapterIndex))
	
	/**
	  * @param firstStatementId Id of the book statement that starts this verse
	  * @return A model containing only the specified first statement id
	  */
	def withFirstStatementId(firstStatementId: Int) = apply(firstStatementId = Some(firstStatementId))
	
	/**
	  * @param verseIndex A 1-based index that indicates which verse this is
	  * @return A model containing only the specified verse index
	  */
	def withVerseIndex(verseIndex: Int) = apply(verseIndex = Some(verseIndex))
}

/**
  * Used for interacting with VerseMarkers in the database
  * @param id verse marker database id
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class VerseMarkerModel(id: Option[Int] = None, chapterIndex: Option[Int] = None, 
	verseIndex: Option[Int] = None, firstStatementId: Option[Int] = None) 
	extends StorableWithFactory[VerseMarker] with VerseMarkerFactory[VerseMarkerModel] 
		with FromIdFactory[Int, VerseMarkerModel]
{
	// IMPLEMENTED	--------------------
	
	override def factory = VerseMarkerModel.factory
	
	override def valueProperties = 
		Vector("id" -> id, VerseMarkerModel.chapterIndex.name -> chapterIndex, 
			VerseMarkerModel.verseIndex.name -> verseIndex, 
			VerseMarkerModel.firstStatementId.name -> firstStatementId)
	
	override def withId(id: Int) = copy(id = Some(id))
	
	
	// OTHER	--------------------
	
	/**
	  * @param chapterIndex A 1-based index that indicates which chapter this verse belongs to
	  * @return A model containing only the specified chapter index
	  */
	def withChapterIndex(chapterIndex: Int) = copy(chapterIndex = Some(chapterIndex))
	
	/**
	  * @param firstStatementId Id of the book statement that starts this verse
	  * @return A model containing only the specified first statement id
	  */
	def withFirstStatementId(firstStatementId: Int) = copy(firstStatementId = Some(firstStatementId))
	
	/**
	  * @param verseIndex A 1-based index that indicates which verse this is
	  * @return A model containing only the specified verse index
	  */
	def withVerseIndex(verseIndex: Int) = copy(verseIndex = Some(verseIndex))
}

