package vf.word.database.access.single.bible.translation.book.statement

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.word.model.combined.bible.VerseBeginningStatementPlacement

/**
  * An access point to individual verse beginning statement placements, based on their placement 
  * id
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class DbSingleVerseBeginningStatementPlacement(id: Int) 
	extends UniqueVerseBeginningStatementPlacementAccess 
		with SingleIntIdModelAccess[VerseBeginningStatementPlacement]

