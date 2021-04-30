package vf.word.model.partial.text

import vf.word.model.cached.Location

/**
 * Represents a word assignment within a word combination
 * @author Mikko Hilpinen
 * @since 30.4.2021, v0.2
 * @param wordId Id of the word to assign
 * @param location The location to which the word is assigned in that combination
 */
case class WordCombinationWordData(wordId: Int, location: Location)
{
	/**
	 * @return Id of the word combination to which the word is assigned to
	 */
	def combinationId: Int = location.targetId
	/**
	 * @return Index of the word's location in the word combination
	 */
	def orderIndex = location.orderIndex
}
