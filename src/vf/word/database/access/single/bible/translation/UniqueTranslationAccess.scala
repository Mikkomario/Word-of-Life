package vf.word.database.access.single.bible.translation

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.access.template.model.DistinctModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.TranslationDbFactory
import vf.word.database.storable.bible.TranslationDbModel
import vf.word.model.stored.bible.Translation

import java.time.Instant

object UniqueTranslationAccess extends ViewFactory[UniqueTranslationAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueTranslationAccess = 
		_UniqueTranslationAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueTranslationAccess(override val accessCondition: Option[Condition]) 
		extends UniqueTranslationAccess
}

/**
  * A common trait for access points that return individual and distinct translations.
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait UniqueTranslationAccess 
	extends SingleRowModelAccess[Translation] 
		with DistinctModelAccess[Translation, Option[Translation], Value] 
		with FilterableView[UniqueTranslationAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Name of this translation. 
	  * None if no translation (or value) was found.
	  */
	def name(implicit connection: Connection) = pullColumn(model.name.column).getString
	/**
	  * A shortened version of this translation's name. 
	  * Empty if there is no abbreviation. 
	  * None if no translation (or value) was found.
	  */
	def abbreviation(implicit connection: Connection) = pullColumn(model.abbreviation.column).getString
	/**
	  * Time when this translation was added to this database. 
	  * None if no translation (or value) was found.
	  */
	def created(implicit connection: Connection) = pullColumn(model.created.column).instant
	/**
	  * Unique id of the accessible translation. None if no translation was accessible.
	  */
	def id(implicit connection: Connection) = pullColumn(index).int
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = TranslationDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = TranslationDbFactory
	override protected def self = this
	
	override def apply(condition: Condition): UniqueTranslationAccess = UniqueTranslationAccess(condition)
	
	
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

