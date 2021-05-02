package vf.word.model.cached

import vf.word.model.enumeration.WordSide

object Location
{
	/**
	 * @param targetId A location id (E.g. sentence segment id)
	 * @return The first location in that entity
	 */
	def headOf(targetId: Int) = apply(targetId, 0)
}

/**
 * Represents a location within a sentence, sentence part, sentence segment, a word combination etc.
 * @author Mikko Hilpinen
 * @since 30.4.2021, v0.2
 * @param targetId Id of the sentence segment, sentence part etc. instance that provides the locations
 * @param orderIndex Index of the item's location in that instance where 0 is the first location.
 */
case class Location(targetId: Int, orderIndex: Int)
{
	// COMPUTED --------------------------
	
	/**
	 * @return The location after this one
	 */
	def next = copy(orderIndex = orderIndex + 1)
	/**
	 * @return The location previous to this one
	 */
	def previous = copy(orderIndex = orderIndex - 1)
	
	/**
	 * @return Whether this is the first location in the target entity
	 */
	def isHead = orderIndex == 0
	/**
	 * @return Whether this location is the second or greater in the target entity
	 */
	def isTail = orderIndex > 0
	/**
	 * @return Whether this location is positive (not on the left side of starting location)
	 */
	def isPositive = orderIndex >= 0
	
	
	// OTHER    ----------------------------
	
	/**
	 * @param direction Advancing direction
	 * @return A location towards that direction
	 */
	def towards(direction: WordSide) = direction match
	{
		case WordSide.Left => previous
		case WordSide.Right => next
	}
}
