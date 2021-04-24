package vf.word.model.partial.text

/**
 * Represents a single assignment of a word in a sentence segment
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 * @param wordId     Id of the word being assigned
 * @param locationId Id of the sentence segment this word belongs to
 * @param orderIndex Index of the position of that word within that sentence segment (starting from 0)
 */
case class WordAssignmentData(wordId: Int, locationId: Int, orderIndex: Int)
