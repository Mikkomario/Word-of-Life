package vf.word.model.partial.text

import vf.word.model.cached.Location

/**
 * Represents a single assignment of a word in a sentence segment
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 * @param wordId     Id of the word being assigned
 * @param location Sentence segment location assigned for the word
 */
case class WordAssignmentData(wordId: Int, location: Location)
{
	/**
	 * @return Id of the sentence segment to which the word is assigned
	 */
	def segmentId = location.targetId
	/**
	 * @return Index to which the word is assigned in the targeted segment
	 */
	def orderIndex = location.orderIndex
}
