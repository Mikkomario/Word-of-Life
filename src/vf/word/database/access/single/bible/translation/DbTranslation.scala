package vf.word.database.access.single.bible.translation

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.util.StringExtensions._
import utopia.vault.database.Connection
import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.TranslationDbFactory
import vf.word.database.storable.bible.TranslationDbModel
import vf.word.model.partial.bible.TranslationData
import vf.word.model.stored.bible.Translation

/**
  * Used for accessing individual translations
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbTranslation extends SingleRowModelAccess[Translation] with UnconditionalView with Indexed
{
	// COMPUTED	--------------------
	
	/**
	  * Model which contains the primary database properties interacted with in this access point
	  */
	private def model = TranslationDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = TranslationDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted translation
	  * @return An access point to that translation
	  */
	def apply(id: Int) = DbSingleTranslation(id)
	
	/**
	 * Stores a Bible translation to the DB. Checks for an existing entry before inserting one.
	 * @param name Name of this translation
	 * @param abbreviation Abbreviation of this translation's name (used as the primary search key).
	 *                     Default = empty = no abbreviation available.
	 * @param connection Implicit DB connection
	 * @return Either:
	 *              - Right: A matching translation that already existed in the DB
	 *              - Left: A newly inserted translation
	 */
	def store(name: String, abbreviation: String = "")(implicit connection: Connection) =
		abbreviation.ifNotEmpty.flatMap { abbr => distinct(model.abbreviation <=> abbr).pull }
			.orElse { distinct(model.name <=> name).pull }
			.toRight { model.insert(TranslationData(name, abbreviation)) }
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should 
	  *                  yield unique translations.
	  * @return An access point to the translation that satisfies the specified condition
	  */
	private def distinct(condition: Condition) = UniqueTranslationAccess(condition)
}

