package vf.word.model.combined.bible

import utopia.flow.view.template.Extender
import utopia.vault.model.template.HasId
import vf.word.model.factory.bible.BookStatementPlacementFactoryWrapper
import vf.word.model.partial.bible.BookStatementPlacementData
import vf.word.model.stored.bible.{BookStatementPlacement, VerseMarker}

object VerseBeginningStatementPlacement
{
	// OTHER	--------------------
	
	/**
	  * @param placement   placement to wrap
	  * @param verseMarker verse marker to attach to this placement
	  * @return Combination of the specified placement and verse marker
	  */
	def apply(placement: BookStatementPlacement, 
		verseMarker: VerseMarker): VerseBeginningStatementPlacement = 
		_VerseBeginningStatementPlacement(placement, verseMarker)
	
	
	// NESTED	--------------------
	
	/**
	  * @param placement   placement to wrap
	  * @param verseMarker verse marker to attach to this placement
	  */
	private case class _VerseBeginningStatementPlacement(placement: BookStatementPlacement, 
		verseMarker: VerseMarker) 
		extends VerseBeginningStatementPlacement
	{
		// IMPLEMENTED	--------------------
		
		override protected def wrap(factory: BookStatementPlacement) = copy(placement = factory)
	}
}

/**
  * Represents a statement link that begins a verse. Includes verse marker information.
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait VerseBeginningStatementPlacement 
	extends Extender[BookStatementPlacementData] with HasId[Int] 
		with BookStatementPlacementFactoryWrapper[BookStatementPlacement, VerseBeginningStatementPlacement]
{
	// ABSTRACT	--------------------
	
	/**
	  * Wrapped placement
	  */
	def placement: BookStatementPlacement
	
	/**
	  * The verse marker that is attached to this placement
	  */
	def verseMarker: VerseMarker
	
	
	// IMPLEMENTED	--------------------
	
	/**
	  * Id of this placement in the database
	  */
	override def id = placement.id
	
	override def wrapped = placement.data
	
	override protected def wrappedFactory = placement
}

