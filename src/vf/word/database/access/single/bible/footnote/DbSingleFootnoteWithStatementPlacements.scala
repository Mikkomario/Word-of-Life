package vf.word.database.access.single.bible.footnote

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.word.model.combined.bible.FootnoteWithStatementPlacements

/**
  * An access point to individual footnotes with statement placements, based on their footnote id
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class DbSingleFootnoteWithStatementPlacements(id: Int) 
	extends UniqueFootnoteWithStatementPlacementsAccess 
		with SingleIntIdModelAccess[FootnoteWithStatementPlacements]

