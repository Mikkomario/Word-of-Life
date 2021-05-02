package vf.word.controller.process

import utopia.vault.database.Connection
import utopia.vault.sql.Condition
import vf.word.database.access.id.many.WordIds
import vf.word.database.access.single.text.DbWord
import vf.word.database.factory.text.WordAssignmentFactory
import vf.word.database.model.text.WordAssignmentModel
import vf.word.model.cached.Location
import vf.word.model.enumeration.WordSide

/**
 * Searches for word combinations in the database data and records them
 * @author Mikko Hilpinen
 * @since 30.4.2021, v0.2
 */
object CombineWords
{
	private val minimumCombinationOccurrence = 2
	
	def apply()(implicit connection: Connection) =
	{
		// Handles each word at a time
		WordIds.iterator.foreach { wordId =>
			val headAssignments = DbWord(wordId).assignments
			val baseLocations = headAssignments.map { _.location }
			// Searches the words that appear after this word in the text
			val rightwardCombinations = combinationsFrom(baseLocations)
				.map { case (wordIds, locations) => (wordId +: wordIds) -> locations.map { _.previous } }
			// Checks which of the combinations extend to the left and by how much
			// The resulting word id lists won't contain the starting word id, because it is already included in the
			// rightward combinations
			val leftwardCombinations = combinationsFrom(
				rightwardCombinations.valuesIterator.flatten.distinct.toVector, WordSide.Left)
				.map { case (wordIds, locations) => wordIds -> locations.map { _.next } }
			// Combines and saves the combinations
			rightwardCombinations.foreach { case (words, locations) =>
				// Checks if there are some applicable leftward combinations
				// TODO: Continue
			}
		}
	}
	
	private def combinationsFrom(baseLocations: Seq[Location], direction: WordSide = WordSide.Right)
	                            (implicit connection: Connection): Map[Vector[Int], Vector[Location]] =
	{
		if (baseLocations.nonEmpty)
		{
			// Finds the words listed next to the base locations
			WordAssignmentFactory.getMany(Condition.or(baseLocations.map { _.towards(direction) }
				.filter { _.isPositive }.map { WordAssignmentModel.withLocation(_).toCondition }))
				// Only includes the words of which there are multiple instances
				.groupBy { _.wordId }.filter { _._2.size >= minimumCombinationOccurrence }
				// Continues to check whether the combinations continue
				.flatMap { case (wordId, nextAssignments) =>
					val nextLocations = nextAssignments.map { _.location }
					val continuingCombinations = combinationsFrom(nextAssignments.map { _.location }, direction)
					// Adjusts the locations of results to start from the correct baseLocation
					// Combines this iteration's results with the recursive results
					val continuingLocations = continuingCombinations.valuesIterator.flatten.toSet
					val stoppingLocations = nextLocations.filterNot(continuingLocations.contains)
					
					val continuingResults = continuingCombinations
						.map { case (wordIds, locations) =>
							val allWordIds = direction match
							{
								case WordSide.Left => wordIds :+ wordId
								case WordSide.Right => wordId +: wordIds
							}
							allWordIds -> locations.map { _.towards(direction.opposite) }
						}
					if (stoppingLocations.isEmpty)
						continuingResults
					else
						continuingResults + (Vector(wordId) -> stoppingLocations)
				}
		}
		else
			Map()
	}
}
