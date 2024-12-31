package vf.word.database.access.single.bible.translation.book.statement

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.word.model.stored.bible.BookStatementPlacement

/**
  * An access point to individual book statement placements, based on their id
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class DbSingleBookStatementPlacement(id: Int) 
	extends UniqueBookStatementPlacementAccess with SingleIntIdModelAccess[BookStatementPlacement]

