package vf.word.database.access.many.bible.translation

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.{FilterableView, ViewFactory}
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.TranslationDbFactory
import vf.word.database.storable.bible.TranslationDbModel
import vf.word.model.stored.bible.Translation

import java.time.Instant

object ManyTranslationsAccess extends ViewFactory[ManyTranslationsAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyTranslationsAccess = 
		_ManyTranslationsAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private class ManyTranslationsSubView(condition: Condition) extends ManyTranslationsAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
	
	private case class _ManyTranslationsAccess(override val accessCondition: Option[Condition]) 
		extends ManyTranslationsAccess
}

/**
  * A common trait for access points which target multiple translations at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait ManyTranslationsAccess 
	extends ManyRowModelAccess[Translation] with FilterableView[ManyTranslationsAccess] with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * names of the accessible translations
	  */
	def names(implicit connection: Connection) = pullColumn(model.name.column).flatMap { _.string }
	
	/**
	  * abbreviations of the accessible translations
	  */
	def abbreviations(implicit connection: Connection) = 
		pullColumn(model.abbreviation.column).flatMap { _.string }
	
	/**
	  * creation times of the accessible translations
	  */
	def creationTimes(implicit connection: Connection) = 
		pullColumn(model.created.column).map { v => v.getInstant }
	
	/**
	  * Unique ids of the accessible translations
	  */
	def ids(implicit connection: Connection) = pullColumn(index).map { v => v.getInt }
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	protected def model = TranslationDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = TranslationDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyTranslationsAccess = ManyTranslationsAccess(condition)
	
	override def filter(filterCondition: Condition): ManyTranslationsAccess = 
		new ManyTranslationsAccess.ManyTranslationsSubView(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the abbreviations of the targeted translations
	  * @param newAbbreviation A new abbreviation to assign
	  * @return Whether any translation was affected
	  */
	def abbreviations_=(newAbbreviation: String)(implicit connection: Connection) = 
		putColumn(model.abbreviation.column, newAbbreviation)
	
	/**
	  * Updates the creation times of the targeted translations
	  * @param newCreated A new created to assign
	  * @return Whether any translation was affected
	  */
	def creationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(model.created.column, newCreated)
	
	/**
	  * Updates the names of the targeted translations
	  * @param newName A new name to assign
	  * @return Whether any translation was affected
	  */
	def names_=(newName: String)(implicit connection: Connection) = putColumn(model.name.column, newName)
	
	/**
	  * @param abbreviation abbreviation to target
	  * @return Copy of this access point that only includes translations with the specified abbreviation
	  */
	def withAbbreviation(abbreviation: String) = filter(model.abbreviation.column <=> abbreviation)
	
	/**
	  * @param abbreviations Targeted abbreviations
	  * @return Copy of this access point that only includes translations where abbreviation is within the
	  * specified value set
	  */
	def withAbbreviations(abbreviations: Iterable[String]) = 
		filter(model.abbreviation.column.in(abbreviations))
	
	/**
	  * @param name name to target
	  * @return Copy of this access point that only includes translations with the specified name
	  */
	def withName(name: String) = filter(model.name.column <=> name)
	
	/**
	  * @param names Targeted names
	  * @return Copy of this access point that only includes translations where name is within the specified 
	  * value set
	  */
	def withNames(names: Iterable[String]) = filter(model.name.column.in(names))
}

