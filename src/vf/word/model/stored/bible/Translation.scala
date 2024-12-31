package vf.word.model.stored.bible

import utopia.flow.generic.model.template.ModelLike.AnyModel
import utopia.vault.model.template.{FromIdFactory, StoredFromModelFactory, StoredModelConvertible}
import vf.word.database.access.single.bible.translation.DbSingleTranslation
import vf.word.model.factory.bible.{TranslationFactory, TranslationFactoryWrapper}
import vf.word.model.partial.bible.TranslationData

import java.time.Instant

object Translation extends StoredFromModelFactory[TranslationData, Translation]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = TranslationData
	
	override protected def complete(model: AnyModel, data: TranslationData) = 
		model("id").tryInt.map { apply(_, data) }
}

/**
  * Represents a translation that has already been stored in the database
  * @param id   id of this translation in the database
  * @param data Wrapped translation data
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class Translation(id: Int, data: TranslationData) 
	extends StoredModelConvertible[TranslationData] with FromIdFactory[Int, Translation]
		with TranslationFactoryWrapper[TranslationData, Translation]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this translation in the database
	  */
	def access = DbSingleTranslation(id)
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrappedFactory = data
	
	override def withId(id: Int): Translation = copy(id = id)
	
	override protected def wrap(data: TranslationData) = copy(data = data)
}

