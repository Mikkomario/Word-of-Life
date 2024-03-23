package vf.word.database.access.single.bible.translation

import utopia.vault.nosql.access.single.model.SingleRowModelAccess
import utopia.vault.nosql.template.Indexed
import utopia.vault.nosql.view.UnconditionalView
import utopia.vault.sql.Condition
import vf.word.database.factory.bible.TranslationDbFactory
import vf.word.database.storable.bible.TranslationModel
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
	  * Factory used for constructing database the interaction models
	  */
	protected def model = TranslationModel
	
	
	// IMPLEMENTED	--------------------
	
	override def factory = TranslationDbFactory
	
	
	// OTHER	--------------------
	
	/**
	  * @param id Database id of the targeted translation
	  * @return An access point to that translation
	  */
	def apply(id: Int) = DbSingleTranslation(id)
	
	/**
	  * @param condition Filter condition to apply in addition to this root view's condition. Should yield
	  *  unique translations.
	  * @return An access point to the translation that satisfies the specified condition
	  */
	protected def filterDistinct(condition: Condition) = UniqueTranslationAccess(mergeCondition(condition))
}

