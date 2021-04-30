package vf.word.controller.process

import utopia.vault.database.Connection
import utopia.vault.sql.Condition
import vf.word.database.access.id.many.WordIds
import vf.word.database.access.single.text.DbWord
import vf.word.database.factory.text.WordAssignmentFactory
import vf.word.database.model.text.WordAssignmentModel
import vf.word.model.cached.Location
import vf.word.model.stored.text.WordAssignment

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
			// Searches the words that appear after this word in the text
			val rightWardCombinations = combinationsFrom(DbWord(wordId).assignments.map { _.location })
				.map { case (wordIds, locations) => (wordId +: wordIds) -> locations }
			// TODO: Attempt to extend the combinations leftward as well, then save each
		}
	}
	
	// TODO: Add a parameter or a variation that allows for leftward scan
	private def combinationsFrom(leftLocations: Seq[Location])
	                            (implicit connection: Connection): Map[Vector[Int], Vector[Location]] =
	{
		// Finds the words listed next to the left locations
		WordAssignmentFactory.getMany(Condition.or(leftLocations.map { location =>
			WordAssignmentModel.withLocation(location.next).toCondition }))
			// Only includes the words of which there are multiple instances
			.groupBy { _.wordId }.filter { _._2.size >= minimumCombinationOccurrence }
			// Continues to check whether the combinations continue
			.flatMap { case (wordId, rightAssignments) =>
				val rightLocations = rightAssignments.map { _.location }
				val continuingCombinations = combinationsFrom(rightAssignments.map { _.location })
				// Adjusts the locations of results to start from the correct leftLocation
				// Combines this iteration's results with the recursive results
				val continuingLocations = continuingCombinations.valuesIterator.flatten.toSet
				val stoppingLocations = rightLocations.filterNot(continuingLocations.contains)
				
				val continuingResults = continuingCombinations
					.map { case (wordIds, locations) => (wordId +: wordIds) -> locations.map { _.previous } }
				if (stoppingLocations.isEmpty)
					continuingResults
				else
					continuingResults + (Vector(wordId) -> stoppingLocations)
			}
	}
}
