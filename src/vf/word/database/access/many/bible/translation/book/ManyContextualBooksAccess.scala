package vf.word.database.access.many.bible.translation.book

import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.nosql.view.ViewFactory
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.ContextualBookDbFactory
import vf.word.database.storable.bible.TranslationDbModel
import vf.word.model.combined.bible.ContextualBook

object ManyContextualBooksAccess extends ViewFactory[ManyContextualBooksAccess]
{
	// IMPLEMENTED	--------------------
	
	/**
	  * @param condition Condition to apply to all requests
	  * @return An access point that applies the specified filter condition (only)
	  */
	override def apply(condition: Condition): ManyContextualBooksAccess = 
		_ManyContextualBooksAccess(Some(condition))
	
	
	// NESTED	--------------------
	
	private case class _ManyContextualBooksAccess(override val accessCondition: Option[Condition]) 
		extends ManyContextualBooksAccess
}

/**
  * A common trait for access points that return multiple contextual books at a time
  * @author Mikko Hilpinen
  * @since 30.12.2024
  */
trait ManyContextualBooksAccess 
	extends ManyBookTranslationsAccessLike[ContextualBook, ManyContextualBooksAccess] 
		with ManyRowModelAccess[ContextualBook]
{
	// COMPUTED	--------------------
	
	/**
	  * names of the accessible translations
	  */
	def translationNames(implicit connection: Connection) = 
		pullColumn(translationModel.name.column).flatMap { _.string }
	
	/**
	  * abbreviations of the accessible translations
	  */
	def translationAbbreviations(implicit connection: Connection) = 
		pullColumn(translationModel.abbreviation.column).flatMap { _.string }
	
	/**
	  * creation times of the accessible translations
	  */
	def translationCreationTimes(implicit connection: Connection) = 
		pullColumn(translationModel.created.column).map { v => v.getInstant }
	
	/**
	  * Model (factory) used for interacting the translations associated with this contextual book
	  */
	protected def translationModel = TranslationDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = ContextualBookDbFactory
	
	override protected def self = this
	
	override def apply(condition: Condition): ManyContextualBooksAccess = ManyContextualBooksAccess(condition)
}

