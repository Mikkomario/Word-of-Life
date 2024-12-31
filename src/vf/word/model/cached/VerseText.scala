package vf.word.model.cached

import utopia.logos.model.cached.Statement

/**
 * Represents the text contents of a single Bible verse
 *
 * @author Mikko Hilpinen
 * @since 14/03/2024, v0.2
 */
// TODO: Rename to Verse once Verse is named StoredVerse
case class VerseText(index: Int, statements: Seq[Statement])
{
	// ATTRIBUTES   -------------------
	
	override lazy val toString = statements.mkString
}
