package vf.word.database.access.single.bible.book_statement_link

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.word.model.combined.bible.VerseBeginningStatementLink

/**
  * An access point to individual verse beginning statement links, based on their book statement link id
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class DbSingleVerseBeginningStatementLink(id: Int) 
	extends UniqueVerseBeginningStatementLinkAccess with SingleIntIdModelAccess[VerseBeginningStatementLink]

