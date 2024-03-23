package vf.word.database.storable.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.template.FromIdFactory
import utopia.vault.nosql.storable.StorableFactory
import vf.word.database.factory.bible.TranslationDbFactory
import vf.word.model.factory.bible.TranslationFactory
import vf.word.model.partial.bible.TranslationData
import vf.word.model.stored.bible.Translation

import java.time.Instant

/**
  * Used for constructing TranslationModel instances and for inserting translations to the database
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object TranslationModel 
	extends StorableFactory[TranslationModel, Translation, TranslationData] 
		with TranslationFactory[TranslationModel] with FromIdFactory[Int, TranslationModel]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Property that contains translation name
	  */
	lazy val name = property("name")
	
	/**
	  * Property that contains translation abbreviation
	  */
	lazy val abbreviation = property("abbreviation")
	
	/**
	  * Property that contains translation created
	  */
	lazy val created = property("created")
	
	
	// COMPUTED	--------------------
	
	/**
	  * The factory object used by this model type
	  */
	def factory = TranslationDbFactory
	
	
	// IMPLEMENTED	--------------------
	
	override def table = factory.table
	
	override def apply(data: TranslationData) = apply(None, data.name, data.abbreviation, Some(data.created))
	
	override def withId(id: Int) = apply(Some(id))
	
	override protected def complete(id: Value, data: TranslationData) = Translation(id.getInt, data)
	
	
	// OTHER	--------------------
	
	/**
	  * @param abbreviation A shortened version of this translation's name. 
	  * Empty if there is no abbreviation.
	  * @return A model containing only the specified abbreviation
	  */
	def withAbbreviation(abbreviation: String) = apply(abbreviation = abbreviation)
	
	/**
	  * @param created Time when this translation was added to this database
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = apply(created = Some(created))
	
	/**
	  * @param name Name of this translation
	  * @return A model containing only the specified name
	  */
	def withName(name: String) = apply(name = name)
}

/**
  * Used for interacting with Translations in the database
  * @param id translation database id
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class TranslationModel(id: Option[Int] = None, name: String = "", abbreviation: String = "", 
	created: Option[Instant] = None) 
	extends StorableWithFactory[Translation] with TranslationFactory[TranslationModel] 
		with FromIdFactory[Int, TranslationModel]
{
	// IMPLEMENTED	--------------------
	
	override def factory = TranslationModel.factory
	
	override def valueProperties = 
		Vector("id" -> id, TranslationModel.name.name -> name, 
			TranslationModel.abbreviation.name -> abbreviation, TranslationModel.created.name -> created)
	
	override def withId(id: Int) = copy(id = Some(id))
	
	
	// OTHER	--------------------
	
	/**
	  * @param abbreviation A shortened version of this translation's name. 
	  * Empty if there is no abbreviation.
	  * @return A model containing only the specified abbreviation
	  */
	def withAbbreviation(abbreviation: String) = copy(abbreviation = abbreviation)
	
	/**
	  * @param created Time when this translation was added to this database
	  * @return A model containing only the specified created
	  */
	def withCreated(created: Instant) = copy(created = Some(created))
	
	/**
	  * @param name Name of this translation
	  * @return A model containing only the specified name
	  */
	def withName(name: String) = copy(name = name)
}

