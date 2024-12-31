package vf.word.database.access.single.bible.footnote.statement

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.word.model.stored.bible.FootnoteStatementPlacement

/**
  * An access point to individual footnote statement placements, based on their id
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class DbSingleFootnoteStatementPlacement(id: Int) 
	extends UniqueFootnoteStatementPlacementAccess with SingleIntIdModelAccess[FootnoteStatementPlacement]

