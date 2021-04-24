package vf.word.model.partial.text

/**
 * Represents and individual sentence segment (smallest part of a sentence that's not a word)
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 * @param sentencePartId Id of the sentence part this segment belongs to
 * @param orderIndex     Index of this segment's position within that sentence part (starting from 0)
 * @param terminator     The character that terminates this segment
 * @param parenthesis Whether this sentence segment should be wrapped in parenthesis
 */
case class SentenceSegmentData(sentencePartId: Int, orderIndex: Int, terminator: Char, parenthesis: Boolean = false)
