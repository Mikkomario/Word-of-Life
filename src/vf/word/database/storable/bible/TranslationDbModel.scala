package vf.word.database.storable.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.model.immutable.{DbPropertyDeclaration, Storable}
import utopia.vault.model.template.{FromIdFactory, HasId, HasIdProperty}
import utopia.vault.nosql.storable.StorableFactory
import vf.word.database.WordOfLifeTables
import vf.word.model.factory.bible.TranslationFactory
import vf.word.model.partial.bible.TranslationData
import vf.word.model.stored.bible.Translation

import java.time.Instant

/**
  * Used for constructing TranslationDbModel instances and for inserting translations to the 
  * database
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object TranslationDbModel 
	extends StorableFactory[TranslationDbModel, Translation, TranslationData] 
		with FromIdFactory[Int, TranslationDbModel] with HasIdProperty 
		with TranslationFactory[TranslationDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val id = DbPropertyDeclaration("id", index)
	
	/**
	  * Database property used for interacting with names
	  */
	lazy val name = property("name")
	
	/**
	  * Database property used for interacting with abbreviations
	  */
	lazy val abbreviation = property("abbreviation")
	
	/**
	  * Database property used for interacting with creation times
	  */
	lazy val created = property("created")
	
	
	// IMPLEMENTED	--------------------
	
	override def table = WordOfLifeTables.translation
	
	override def apply(data: TranslationData): TranslationDbModel = 
		apply(None, data.name, data.abbreviation, Some(data.created))
	
	/**
	  * @param abbreviation A shortened version of this translation's name. 
	  *                     Empty if there is no abbreviation.
	  * @return A model containing only the specified abbreviation
	  */
	override def withAbbreviation(abbreviation: String) = apply(abbreviation = abbreviation)
	
	/**
	  * @param created Time when this translation was added to this database
	  * @return A model containing only the specified created
	  */
	override def withCreated(created: Instant) = apply(created = Some(created))
	
	override def withId(id: Int) = apply(id = Some(id))
	
	/**
	  * @param name Name of this translation
	  * @return A model containing only the specified name
	  */
	override def withName(name: String) = apply(name = name)
	
	override protected def complete(id: Value, data: TranslationData) = Translation(id.getInt, data)
}

/**
  * Used for interacting with Translations in the database
  * @param id translation database id
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class TranslationDbModel(id: Option[Int] = None, name: String = "", abbreviation: String = "", 
	created: Option[Instant] = None) 
	extends Storable with HasId[Option[Int]] with FromIdFactory[Int, TranslationDbModel] 
		with TranslationFactory[TranslationDbModel]
{
	// ATTRIBUTES	--------------------
	
	override lazy val valueProperties = 
		Vector(TranslationDbModel.id.name -> id, TranslationDbModel.name.name -> name, 
			TranslationDbModel.abbreviation.name -> abbreviation, TranslationDbModel.created.name -> created)
	
	
	// IMPLEMENTED	--------------------
	
	override def table = TranslationDbModel.table
	
	/**
	  * @param abbreviation A shortened version of this translation's name. 
	  *                     Empty if there is no abbreviation.
	  * @return A new copy of this model with the specified abbreviation
	  */
	override def withAbbreviation(abbreviation: String) = copy(abbreviation = abbreviation)
	
	/**
	  * @param created Time when this translation was added to this database
	  * @return A new copy of this model with the specified created
	  */
	override def withCreated(created: Instant) = copy(created = Some(created))
	
	override def withId(id: Int) = copy(id = Some(id))
	
	/**
	  * @param name Name of this translation
	  * @return A new copy of this model with the specified name
	  */
	override def withName(name: String) = copy(name = name)
}

