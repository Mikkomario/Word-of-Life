package vf.word.database.access.single.bible.translation.book

import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.ContextualBookDbFactory
import vf.word.database.storable.bible.TranslationDbModel
import vf.word.model.combined.bible.ContextualBook

object UniqueContextualBookAccess extends ViewFactory[UniqueContextualBookAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): UniqueContextualBookAccess = 
		_UniqueContextualBookAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _UniqueContextualBookAccess(override val accessCondition: Option[Condition]) 
		extends UniqueContextualBookAccess
}

/**
  * A common trait for access points that return distinct contextual books
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait UniqueContextualBookAccess 
	extends UniqueBookTranslationAccessLike[ContextualBook, UniqueContextualBookAccess] 
		with SingleRowModelAccess[ContextualBook]
{
	// COMPUTED	--------------------
	
	/**
	  * Name of this translation. 
	  * None if no translation (or value) was found.
	  */
	def translationName(implicit connection: Connection) = pullColumn(translationModel.name.column).getString
	
	/**
	  * A shortened version of this translation's name. 
	  * Empty if there is no abbreviation. 
	  * None if no translation (or value) was found.
	  */
	def translationAbbreviation(implicit connection: Connection) = 
		pullColumn(translationModel.abbreviation.column).getString
	
	/**
	  * Time when this translation was added to this database. 
	  * None if no translation (or value) was found.
	  */
	def translationCreated(implicit connection: Connection) = 
		pullColumn(translationModel.created.column).instant
	
	/**
	  * A database model (factory) used for interacting with the linked translation
	  */
	protected def translationModel = TranslationDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = ContextualBookDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): UniqueContextualBookAccess = 
		UniqueContextualBookAccess(condition)
}

