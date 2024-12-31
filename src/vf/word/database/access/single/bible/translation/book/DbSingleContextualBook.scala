package vf.word.database.access.single.bible.translation.book

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.word.model.combined.bible.ContextualBook

/**
  * An access point to individual contextual books, based on their book translation id
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class DbSingleContextualBook(id: Int) 
	extends UniqueContextualBookAccess with SingleIntIdModelAccess[ContextualBook]

