package vf.word.model.combined.bible

import utopia.flow.view.template.Extender
import vf.word.model.partial.bible.BookStatementLinkData
import vf.word.model.stored.bible.{BookStatementLink, VerseMarker}

/**
  * Represents a statement link that begins a verse. Includes verse marker information.
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class VerseBeginningStatementLink(bookStatementLink: BookStatementLink, verseMarker: VerseMarker) 
	extends Extender[BookStatementLinkData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this book statement link in the database
	  */
	def id = bookStatementLink.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = bookStatementLink.data
}

