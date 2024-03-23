package vf.word.model.partial.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.InstantType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ModelConvertible
import utopia.flow.time.Now
import vf.word.model.factory.bible.TranslationFactory

import java.time.Instant

object TranslationData extends FromModelFactoryWithSchema[TranslationData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("name", StringType), PropertyDeclaration("abbreviation", 
			StringType, isOptional = true), PropertyDeclaration("created", InstantType, isOptional = true)))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		TranslationData(valid("name").getString, valid("abbreviation").getString, valid("created").getInstant)
}

/**
  * Represents a Bible translation
  * @param name Name of this translation
  * @param abbreviation A shortened version of this translation's name. 
  * Empty if there is no abbreviation.
  * @param created Time when this translation was added to this database
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class TranslationData(name: String, abbreviation: String = "", created: Instant = Now) 
	extends TranslationFactory[TranslationData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = Model(Vector("name" -> name, "abbreviation" -> abbreviation, "created" -> created))
	
	override def withAbbreviation(abbreviation: String) = copy(abbreviation = abbreviation)
	override def withCreated(created: Instant) = copy(created = created)
	override def withName(name: String) = copy(name = name)
}

