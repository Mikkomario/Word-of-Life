package vf.word.model.combined.bible

import utopia.flow.view.template.Extender
import vf.word.model.partial.bible.FootnoteData
import vf.word.model.stored.bible.{Footnote, FootnoteStatementLink}

/**
  * A footnote which includes links to the statements made
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class FootnoteWithLinks(footnote: Footnote, links: Vector[FootnoteStatementLink]) 
	extends Extender[FootnoteData]
{
	// COMPUTED	--------------------
	
	/**
	  * Id of this footnote in the database
	  */
	def id = footnote.id
	
	
	// IMPLEMENTED	--------------------
	
	override def wrapped = footnote.data
}

