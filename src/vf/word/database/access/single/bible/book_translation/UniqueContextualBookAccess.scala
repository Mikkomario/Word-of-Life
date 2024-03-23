package vf.word.database.access.single.bible.book_translation

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.FilterableView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.ContextualBookDbFactory
import vf.word.database.storable.bible.TranslationModel
import vf.word.model.combined.bible.ContextualBook

import java.time.Instant

object UniqueContextualBookAccess
{
	// OTHER	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	def apply(condition: Condition): UniqueContextualBookAccess = new _UniqueContextualBookAccess(condition)
	
	
	// NESTED	--------------------
	
	private class _UniqueContextualBookAccess(condition: Condition) extends UniqueContextualBookAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return distinct contextual books
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
trait UniqueContextualBookAccess 
	extends UniqueBookTranslationAccessLike[ContextualBook] with SingleRowModelAccess[ContextualBook] 
		with FilterableView[UniqueContextualBookAccess]
{
	// COMPUTED	--------------------
	
	/**
	  * Name of this translation. None if no translation (or value) was found.
	  */
	def translationName(implicit connection: Connection) = pullColumn(translationModel.name.column).getString
	
	/**
	  * A shortened version of this translation's name. 
	  * Empty if there is no abbreviation.. None if no translation (or value) was found.
	  */
	def translationAbbreviation(implicit connection: Connection) = 
		pullColumn(translationModel.abbreviation.column).getString
	
	/**
	  * Time when this translation was added to this database. None if no translation (or value) was found.
	  */
	def translationCreated(implicit connection: Connection) = pullColumn(translationModel
		.created.column).instant
	
	/**
	  * A database model (factory) used for interacting with the linked translation
	  */
	protected def translationModel = TranslationModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = ContextualBookDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): UniqueContextualBookAccess = 
		new UniqueContextualBookAccess._UniqueContextualBookAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the abbreviations of the targeted translations
	  * @param newAbbreviation A new abbreviation to assign
	  * @return Whether any translation was affected
	  */
	def translationAbbreviation_=(newAbbreviation: String)(implicit connection: Connection) = 
		putColumn(translationModel.abbreviation.column, newAbbreviation)
	
	/**
	  * Updates the creation times of the targeted translations
	  * @param newCreated A new created to assign
	  * @return Whether any translation was affected
	  */
	def translationCreated_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(translationModel.created.column, newCreated)
	
	/**
	  * Updates the names of the targeted translations
	  * @param newName A new name to assign
	  * @return Whether any translation was affected
	  */
	def translationName_=(newName: String)(implicit connection: Connection) = 
		putColumn(translationModel.name.column, newName)
}

