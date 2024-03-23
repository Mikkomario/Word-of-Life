package vf.word.database.access.single.bible.translation

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.TranslationDbFactory
import vf.word.database.storable.bible.TranslationModel
import vf.word.model.stored.bible.Translation

import java.time.Instant

object UniqueTranslationAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueTranslationAccess = new _UniqueTranslationAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueTranslationAccess(condition: Condition) extends UniqueTranslationAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return individual and distinct translations.
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait UniqueTranslationAccess 
	extends SingleRowModelAccess[Translation] with FilterableView[UniqueTranslationAccess] 
		with DistinctModelAccess[Translation, Option[Translation], Value] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Name of this translation. None if no translation (or value) was found.
	  */
	def name(implicit connection: Connection) = pullColumn(model.name.column).getString
	
	/**
	  * A shortened version of this translation's name. 
	  * Empty if there is no abbreviation.. None if no translation (or value) was found.
	  */
	def abbreviation(implicit connection: Connection) = pullColumn(model.abbreviation.column).getString
	
	/**
	  * Time when this translation was added to this database. None if no translation (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.created.column).instant
	
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Factory used for constructing database the interaction models
	  */
	protected def model = TranslationModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = TranslationDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueTranslationAccess = 
		new UniqueTranslationAccess._UniqueTranslationAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the abbreviations of the targeted translations
	  * @param newAbbreviation A new abbreviation to assign
	  * @return Whether any translation was affected
	  */
	def abbreviation_=(newAbbreviation: String)(implicit connection: Connection) = 
		putColumn(model.abbreviation.column, newAbbreviation)
	
	/**
	  * Updates the creation times of the targeted translations
	  * @param newCreated A new created to assign
	  * @return Whether any translation was affected
	  */
	def created_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.created.column, newCreated)
	
	/**
	  * Updates the names of the targeted translations
	  * @param newName A new name to assign
	  * @return Whether any translation was affected
	  */
	def name_=(newName: String)(implicit connection: Connection) = putColumn(model.name.column, newName)
}

