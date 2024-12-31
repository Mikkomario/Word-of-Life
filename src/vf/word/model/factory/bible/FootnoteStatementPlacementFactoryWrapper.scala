package vf.word.model.factory.bible

import utopia.logos.model.factory.text.StatementPlacementFactoryWrapper

/**
  * Common trait for classes that implement FootnoteStatementPlacementFactory by wrapping a 
  * FootnoteStatementPlacementFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait FootnoteStatementPlacementFactoryWrapper[A <: FootnoteStatementPlacementFactory[A], +Repr] 
	extends FootnoteStatementPlacementFactory[Repr] with StatementPlacementFactoryWrapper[A, Repr]

