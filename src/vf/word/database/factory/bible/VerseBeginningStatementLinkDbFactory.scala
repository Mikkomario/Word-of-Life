package vf.word.database.factory.bible

import utopia.vault.nosql.factory.row.linked.CombiningFactory
import vf.word.model.combined.bible.VerseBeginningStatementLink
import vf.word.model.stored.bible.{BookStatementLink, VerseMarker}

/**
  * Used for reading verse beginning statement links from the database
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object VerseBeginningStatementLinkDbFactory 
	extends CombiningFactory[VerseBeginningStatementLink, BookStatementLink, VerseMarker]
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = VerseMarkerDbFactory
	
	override def parentFactory = BookStatementLinkDbFactory
	
	override def apply(bookStatementLink: BookStatementLink, verseMarker: VerseMarker) = 
		VerseBeginningStatementLink(bookStatementLink, verseMarker)
}

