package vf.word.model.stored.bible

import utopia.vault.model.template.{FromIdFactory, StoredModelConvertible}
import vf.word.database.access.single.bible.translation.DbSingleTranslation
import vf.word.model.factory.bible.TranslationFactory
import vf.word.model.partial.bible.TranslationData

import java.time.Instant

/**
  * Represents a translation that has already been stored in the database
  * @param id id of this translation in the database
  * @param data Wrapped translation data
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class Translation(id: Int, data: TranslationData) 
	extends StoredModelConvertible[TranslationData] with TranslationFactory[Translation] 
		with FromIdFactory[Int, Translation]
{
	// COMPUTED	--------------------
	
	/**
	  * An access point to this translation in the database
	  */
	def access = DbSingleTranslation(id)
	
	
	// IMPLEMENTED	--------------------
	
	override def withAbbreviation(abbreviation: String) = copy(data = data.withAbbreviation(abbreviation))
	
	override def withCreated(created: Instant) = copy(data = data.withCreated(created))
	
	override def withId(id: Int) = copy(id = id)
	
	override def withName(name: String) = copy(data = data.withName(name))
}

