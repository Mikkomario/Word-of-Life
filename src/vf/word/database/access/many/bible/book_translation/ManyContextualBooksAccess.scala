package vf.word.database.access.many.bible.book_translation

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.many.model.ManyRowModelAccess
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.ContextualBookDbFactory
import vf.word.database.storable.bible.TranslationModel
import vf.word.model.combined.bible.ContextualBook

import java.time.Instant

object ManyContextualBooksAccess
{
	// NESTED	--------------------
	
	private class SubAccess(condition: Condition) extends ManyContextualBooksAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(condition)
	}
}

/**
  * A common trait for access points that return multiple contextual books at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024
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
	protected def translationModel = TranslationModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = ContextualBookDbFactory
	
	override protected def self = this
	
	override def filter(filterCondition: Condition): ManyContextualBooksAccess = 
		new ManyContextualBooksAccess.SubAccess(mergeCondition(filterCondition))
	
	
	// OTHER	--------------------
	
	/**
	  * Updates the abbreviations of the targeted translations
	  * @param newAbbreviation A new abbreviation to assign
	  * @return Whether any translation was affected
	  */
	def translationAbbreviations_=(newAbbreviation: String)(implicit connection: Connection) = 
		putColumn(translationModel.abbreviation.column, newAbbreviation)
	
	/**
	  * Updates the creation times of the targeted translations
	  * @param newCreated A new created to assign
	  * @return Whether any translation was affected
	  */
	def translationCreationTimes_=(newCreated: Instant)(implicit connection: Connection) = 
		putColumn(translationModel.created.column, newCreated)
	
	/**
	  * Updates the names of the targeted translations
	  * @param newName A new name to assign
	  * @return Whether any translation was affected
	  */
	def translationNames_=(newName: String)(implicit connection: Connection) = 
		putColumn(translationModel.name.column, newName)
}

