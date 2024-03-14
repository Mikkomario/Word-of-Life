package vf.word.model.cached

import utopia.logos.model.cached.StatementText

/**
 * Represents the text contents of a single Bible verse
 *
 * @author Mikko Hilpinen
 * @since 14/03/2024, v0.2
 */
case class VerseText(index: Int, statements: Vector[StatementText])
{
	// ATTRIBUTES   -------------------
	
	override lazy val toString = statements.mkString
}
