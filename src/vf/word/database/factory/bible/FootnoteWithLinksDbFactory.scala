package vf.word.database.factory.bible

import utopia.vault.nosql.factory.multi.MultiCombiningFactory
import vf.word.model.combined.bible.FootnoteWithLinks
import vf.word.model.stored.bible.{Footnote, FootnoteStatementLink}

/**
  * Used for reading footnote with linkses from the database
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object FootnoteWithLinksDbFactory 
	extends MultiCombiningFactory[FootnoteWithLinks, Footnote, FootnoteStatementLink]
{
	// IMPLEMENTED	--------------------
	
	override def childFactory = FootnoteStatementLinkDbFactory
	
	override def isAlwaysLinked = true
	
	override def parentFactory = FootnoteDbFactory
	
	override def apply(footnote: Footnote, links: Vector[FootnoteStatementLink]) = 
		FootnoteWithLinks(footnote, links)
}

