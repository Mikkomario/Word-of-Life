package vf.word.model.stored.bible

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.model.template.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.word.database.access.single.bible.verse.DbSingleVerseMarker
import vf.word.model.factory.bible.{VerseMarkerFactory, VerseMarkerFactoryWrapper}
import vf.word.model.partial.bible.VerseMarkerData

object VerseMarker extends StoredFromModelFactory[VerseMarkerData, VerseMarker]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = VerseMarkerData
	
	override protected def complete(model: AnyModel, data: VerseMarkerData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a verse marker that has already been stored in the database
  * @param id   id of this verse marker in the database
  * @param data Wrapped verse marker data
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class VerseMarker(id: Int, data: VerseMarkerData) 
	extends StoredModelConvertible[VerseMarkerData] with FromIdFactory[Int, VerseMarker]
		with VerseMarkerFactoryWrapper[VerseMarkerData, VerseMarker]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this verse marker in the database
	  */
	def access = DbSingleVerseMarker(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int): VerseMarker = copy(id = id)
	
	override protected def wrap(data: VerseMarkerData) = copy(data = data)
}

