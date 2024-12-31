package vf.word.model.factory.bible

import utopia.logos.model.factory.text.StatementPlacementFactoryWrapper

/**
  * Common trait for classes that implement BookStatementPlacementFactory by wrapping a 
  * BookStatementPlacementFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait BookStatementPlacementFactoryWrapper[A <: BookStatementPlacementFactory[A], +Repr] 
	extends BookStatementPlacementFactory[Repr] with StatementPlacementFactoryWrapper[A, Repr]
{
	// IMPLEMENTED	--------------------
	
	override def withBookId(bookId: Int) = withParentId(bookId)
}

