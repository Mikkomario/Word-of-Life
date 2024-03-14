package vf.word.model.cached

/**
 * Contains the text of a single Bible chapter, with verse markers included
 * @author Mikko Hilpinen
 * @since 14/03/2024, v0.2
 */
case class ChapterText(index: Int, verses: Vector[VerseText])
