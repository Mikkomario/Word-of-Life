package vf.word.database.access.single.bible.footnote_statement_link

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.word.model.stored.bible.FootnoteStatementLink

/**
  * An access point to individual footnote statement links, based on their id
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class DbSingleFootnoteStatementLink(id: Int) 
	extends UniqueFootnoteStatementLinkAccess with SingleIntIdModelAccess[FootnoteStatementLink]

