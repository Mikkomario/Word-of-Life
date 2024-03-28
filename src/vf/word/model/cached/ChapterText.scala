package vf.word.model.cached

import utopia.flow.collection.immutable.Pair

import scala.collection.immutable.VectorBuilder

/**
 * Contains the text of a single Bible chapter, with verse markers included
 *
 * @author Mikko Hilpinen
 * @since 14/03/2024, v0.2
 */
case class ChapterText(index: Int, verses: Vector[VerseText])
{
	/**
	 * @return Indices of the statements (0-based, containing all statements within this chapter)
	 *         where a new verse starts.
	 *         The first value is the starting verse number and the second value is the 0-based statement index.
	 */
	def verseMarkerIndices = {
		val builder = new VectorBuilder[Pair[Int]]()
		var nextIndex = 0
		verses.foreach { verse =>
			builder += Pair(verse.index, nextIndex)
			nextIndex += verse.statements.size
		}
		builder.result()
	}
}