package vf.word.database.storable.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{FromIdFactory, HasId, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import vf.word.database.WordOfLifeTables
import vf.word.model.factory.bible.VerseMarkerFactory
import vf.word.model.partial.bible.VerseMarkerData
import vf.word.model.stored.bible.VerseMarker

/**
  * Used for constructing VerseMarkerDbModel instances and for inserting verse markers to the 
  * database
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object VerseMarkerDbModel 
	extends StorableFactory[VerseMarkerDbModel, VerseMarker, VerseMarkerData] 
		with FromIdFactory[Int, VerseMarkerDbModel] with HasIdProperty 
		with VerseMarkerFactory[VerseMarkerDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with chapter indices
	  */
	lazy val chapterIndex = property("chapterIndex")
	
	/**
	  * Database property used for interacting with verse indices
	  */
	lazy val verseIndex = property("verseIndex")
	
	/**
	  * Database property used for interacting with first statement ids
	  */
	lazy val firstStatementId = property("firstStatementId")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = WordOfLifeTables.verseMarker
	
	override def apply(data: VerseMarkerData): VerseMarkerDbModel = 
		apply(None, Some(data.chapterIndex), Some(data.verseIndex), Some(data.firstStatementId))
	
	/**
	  * @param chapterIndex A 1-based index that indicates which chapter this verse belongs to
	  * @return A model containing only the specified chapter index
	  */
	override def withChapterIndex(chapterIndex: Int) = apply(chapterIndex = Some(chapterIndex))
	
	/**
	  * @param firstStatementId Id of the book statement that starts this verse
	  * @return A model containing only the specified first statement id
	  */
	override def withFirstStatementId(firstStatementId: Int) = apply(firstStatementId = 
		Some(firstStatementId))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param verseIndex A 1-based index that indicates which verse this is
	  * @return A model containing only the specified verse index
	  */
	override def withVerseIndex(verseIndex: Int) = apply(verseIndex = Some(verseIndex))
	
	override protected def complete(id: Value, data: VerseMarkerData) = VerseMarker(id.getInt, data)
}

/**
  * Used for interacting with VerseMarkers in the database
  * @param id verse marker database id
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class VerseMarkerDbModel(id: Option[Int] = None, chapterIndex: Option[Int] = None, 
	verseIndex: Option[Int] = None, firstStatementId: Option[Int] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, VerseMarkerDbModel] 
		with VerseMarkerFactory[VerseMarkerDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties = 
		Vector(VerseMarkerDbModel.id.name -> id, VerseMarkerDbModel.chapterIndex.name -> chapterIndex, 
			VerseMarkerDbModel.verseIndex.name -> verseIndex, 
			VerseMarkerDbModel.firstStatementId.name -> firstStatementId)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = VerseMarkerDbModel.table
	
	/**
	  * @param chapterIndex A 1-based index that indicates which chapter this verse belongs to
	  * @return A new copy of this model with the specified chapter index
	  */
	override def withChapterIndex(chapterIndex: Int) = copy(chapterIndex = Some(chapterIndex))
	
	/**
	  * @param firstStatementId Id of the book statement that starts this verse
	  * @return A new copy of this model with the specified first statement id
	  */
	override def withFirstStatementId(firstStatementId: Int) = copy(firstStatementId = Some(firstStatementId))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	/**
	  * @param verseIndex A 1-based index that indicates which verse this is
	  * @return A new copy of this model with the specified verse index
	  */
	override def withVerseIndex(verseIndex: Int) = copy(verseIndex = Some(verseIndex))
}

