package vf.word.database.factory.bible

import utopia.vault.nosql.factory.multi.MultiCombiningFactory
import vf.word.model.combined.bible.FootnoteWithStatementPlacements
import vf.word.model.stored.bible.{Footnote, FootnoteStatementPlacement}

/**
  * Used for reading footnotes with statement placements from the database
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object FootnoteWithStatementPlacementsDbFactory 
	extends MultiCombiningFactory[FootnoteWithStatementPlacements, Footnote, FootnoteStatementPlacement]
{
	// ATTRIBUTES	--------------------
	
	override val parentFactory = FootnoteDbFactory
	
	override val childFactory = FootnoteStatementPlacementDbFactory
	
	override val isAlwaysLinked = true
	
	
	// IMPLEMENTED	--------------------
	
	/**
	  * @param footnote   footnote to wrap
	  * @param placements placements to attach to this footnote
	  */
	override def apply(footnote: Footnote, placements: Seq[FootnoteStatementPlacement]) = 
		FootnoteWithStatementPlacements(footnote, placements)
}

