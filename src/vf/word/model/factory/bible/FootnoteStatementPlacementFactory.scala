package vf.word.model.factory.bible

import utopia.logos.model.factory.text.StatementPlacementFactory

/**
  * Common trait for footnote statement placement-related factories which allow construction with 
  * individual properties
  * @tparam A Type of constructed instances
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait FootnoteStatementPlacementFactory[+A] extends StatementPlacementFactory[A]

