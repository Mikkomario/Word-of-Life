package vf.word.model.combined.bible

import utopia.flow.view.template.Extender
import utopia.vault.model.template.HasId
import vf.word.model.factory.bible.FootnoteFactoryWrapper
import vf.word.model.partial.bible.FootnoteData
import vf.word.model.stored.bible.{Footnote, FootnoteStatementPlacement}

object FootnoteWithStatementPlacements
{
	// OTHER	--------------------
	
	/**
	  * @param footnote   footnote to wrap
	  * @param placements placements to attach to this footnote
	  * @return Combination of the specified footnote and placement
	  */
	def apply(footnote: Footnote, 
		placements: Seq[FootnoteStatementPlacement]): FootnoteWithStatementPlacements = 
		_FootnoteWithStatementPlacements(footnote, placements)
	
	
	// NESTED	--------------------
	
	/**
	  * @param footnote   footnote to wrap
	  * @param placements placements to attach to this footnote
	  */
	private case class _FootnoteWithStatementPlacements(footnote: Footnote, 
		placements: Seq[FootnoteStatementPlacement]) 
		extends FootnoteWithStatementPlacements
	{
		// IMPLEMENTED	--------------------
		
		override protected def wrap(factory: Footnote) = copy(footnote = factory)
	}
}

/**
  * A footnote which includes links to the statements made
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait FootnoteWithStatementPlacements 
	extends Extender[FootnoteData] with HasId[Int] 
		with FootnoteFactoryWrapper[Footnote, FootnoteWithStatementPlacements]
{
	// ABSTRACT	--------------------
	
	/**
	  * Wrapped footnote
	  */
	def footnote: Footnote
	
	/**
	  * Placements that are attached to this footnote
	  */
	def placements: Seq[FootnoteStatementPlacement]
	
	
	// IMPLEMENTED	--------------------
	
	/**
	  * Id of this footnote in the database
	  */
	override def id = footnote.id
	
	override def wrapped = footnote.data
	
	override protected def wrappedFactory = footnote
}

