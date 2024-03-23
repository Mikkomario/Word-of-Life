package vf.word.database.access.single.bible.footnote

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.word.model.combined.bible.FootnoteWithLinks

/**
  * An access point to individual footnote with linkses, based on their footnote id
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class DbSingleFootnoteWithLinks(id: Int) 
	extends UniqueFootnoteWithLinksAccess with SingleIntIdModelAccess[FootnoteWithLinks]

