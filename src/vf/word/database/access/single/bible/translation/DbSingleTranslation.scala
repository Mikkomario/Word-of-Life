package vf.word.database.access.single.bible.translation

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.word.model.stored.bible.Translation

/**
  * An access point to individual translations, based on their id
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class DbSingleTranslation(id: Int) 
	extends UniqueTranslationAccess with SingleIntIdModelAccess[Translation]

