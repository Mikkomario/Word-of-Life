package vf.word.database.access.single.bible.book_statement_link

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.word.model.stored.bible.BookStatementLink

/**
  * An access point to individual book statement links, based on their id
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class DbSingleBookStatementLink(id: Int) 
	extends UniqueBookStatementLinkAccess with SingleIntIdModelAccess[BookStatementLink]

