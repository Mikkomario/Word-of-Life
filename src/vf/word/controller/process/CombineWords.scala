package vf.word.controller.process

import utopia.flow.datastructure.immutable.Tree
import utopia.flow.util.ActionBuffer
import utopia.flow.util.CollectionExtensions._
import utopia.vault.database.Connection
import utopia.vault.sql.Condition
import vf.word.database.access.id.many.WordIds
import vf.word.database.access.single.text.DbWord
import vf.word.database.factory.text.WordAssignmentFactory
import vf.word.database.model.text.{WordAssignmentModel, WordCombinationModel, WordCombinationWordModel}
import vf.word.model.cached.Location
import vf.word.model.enumeration.WordSide
import vf.word.model.partial.text.{WordCombinationData, WordCombinationWordData}
import vf.word.model.stored.text.WordAssignment

import scala.collection.mutable

/**
 * Searches for word combinations in the database data and records them
 * @author Mikko Hilpinen
 * @since 30.4.2021, v0.2
 */
object CombineWords
{
	// ATTRIBUTES   -----------------------------------
	
	private val minimumCombinationOccurrence = 2
	private val bufferSize = 300
	
	
	// OTHER    ---------------------------------------
	
	/**
	 * Searches and records word combinations throughout all the texts
	 * @param connection DB Connection (implicit)
	 */
	def apply()(implicit connection: Connection) =
	{
		// The inserts are bundled using a buffer
		val insertBuffer = ActionBuffer[Tree[PreparedCombination]](bufferSize) { roots =>
			insertCombinations(roots, None)
		}
		val duplicateChecker = new DuplicateChecker()
		
		// Handles each word at a time
		WordIds.iterator.foreach { wordId =>
			val headAssignments = DbWord(wordId).assignments
			val baseLocations = headAssignments.map { _.location }
			// Searches the words that appear after this word in the text
			// May filter the results based on already inserted combinations
			val rightwardCombinations = duplicateChecker.filter(wordId, combinationsFrom(baseLocations))
			// Checks which of the combinations extend to the left and by how much
			val leftwardCombinations = combinationsFrom(
				rightwardCombinations.flatMap { _.allContentIterator.flatMap { _._2 } }, WordSide.Left)
			// Combines and saves the combinations
			rightwardCombinations.foreach { root =>
				insertBuffer ++= combineCombinations(Vector(), root, headAssignments, leftwardCombinations,
					duplicateChecker)
			}
		}
		
		// Inserts the remaining combinations
		insertBuffer.flush()
	}
	
	private def insertCombinations(remaining: Vector[Tree[PreparedCombination]], baseCombinationId: Option[Int])
	                              (implicit connection: Connection): Unit =
	{
		if (remaining.nonEmpty)
		{
			// Performs the inserts in layers in order to acquire the base combination ids which are required in the
			// following inserts
			// Starts by inserting the base combinations
			val insertedCombinations = WordCombinationModel.insert(remaining.map { _.content }
				.map { prepared => WordCombinationData(prepared.wordIds.size, baseCombinationId, prepared.baseSide) })
			val insertedRemaining = insertedCombinations.zip(remaining)
			// Next assigns the words into those combinations
			WordCombinationWordModel.insert(insertedCombinations.zip(remaining)
				.flatMap { case (combination, node) => node.content.wordIds.zipWithIndex
					.map { case (wordId, index) => WordCombinationWordData(wordId, Location(combination.id, index)) } })
			// Finally moves to the underlying layers
			// (each is handled separately due to the different base combination id)
			insertedRemaining.foreach { case (combination, node) =>
				insertCombinations(node.children, Some(combination.id))
			}
		}
	}
	
	private def combineCombinations(baseWordIds: Vector[Int], node: Tree[(Int, Vector[Location])],
	                                baseAssignments: Vector[WordAssignment],
	                                leftCombinations: Vector[Tree[(Int, Vector[Location])]],
	                                duplicateChecker: DuplicateChecker): Vector[Tree[PreparedCombination]] =
	{
		val (wordId, rootLocations) = node.content
		val wordIds = baseWordIds :+ wordId
		
		// Collects the branches first
		val branchResults = node.children.flatMap { childNode =>
			combineCombinations(wordIds, childNode, baseAssignments, leftCombinations, duplicateChecker) }
		// Also collects the leftward results
		// (which need to be reversed because the word ids are listed from right to left)
		val leftTrees = leftCombinations.flatMap { node =>
			// Case: The left root is associated with this node
			if (node.content._2.existsCount(minimumCombinationOccurrence)(rootLocations.contains))
				Vector(node)
			// Case: Some of the left child nodes may be associated with this node
			else
				node.findBranches { _.content._2.existsCount(minimumCombinationOccurrence)(rootLocations.contains) }
		}
		// Records the left side branches to the duplicate checker also
		duplicateChecker.recordLeftBranches(wordIds, leftTrees)
		val leftResults = leftTrees.flatMap { leftNode =>
			combineCombinations(wordIds, leftNode, baseAssignments, Vector(), duplicateChecker) }
			.map { _.map { _.reverse } }
		
		// Checks whether this node ought to be saved
		// Case: Yes => Uses this node as the base for other associated nodes
		if (rootLocations.size >= minimumCombinationOccurrence)
		{
			val baseCombination = PreparedCombination(wordIds,
				rootLocations.flatMap { location => baseAssignments.find { _.location == location }.map { _.id } })
			Vector(Tree(baseCombination, branchResults ++ leftResults))
		}
		// Case: No => Only save branches / left side results
		else
			branchResults ++ leftResults
	}
	
	// Each tree node contains the singular word id + the start locations of the word combination ending with that id
	private def combinationsFrom(baseLocations: Seq[Location], direction: WordSide = WordSide.Right)
	                            (implicit connection: Connection): Vector[Tree[(Int, Vector[Location])]] =
	{
		if (baseLocations.nonEmpty)
		{
			val backDirection = direction.opposite
			
			// Finds the words listed next to the base locations
			WordAssignmentFactory.getMany(Condition.or(baseLocations.map { _.towards(direction) }
				.filter { _.isPositive }.map { WordAssignmentModel.withLocation(_).toCondition }))
				// Only includes the words of which there are multiple instances
				.groupBy { _.wordId }.filter { _._2.size >= minimumCombinationOccurrence }
				// Continues to check whether the combinations continue
				.map { case (wordId, nextAssignments) =>
					val nextLocations = nextAssignments.map { _.location }
					val continuingCombinations = combinationsFrom(nextAssignments.map { _.location }, direction)
					// Adjusts the locations of results to start from the correct baseLocation
					val continuingLocations = continuingCombinations.flatMap { _.content._2 }.toSet
					val stoppingLocations = nextLocations.filterNot(continuingLocations.contains)
					
					val continuingResults = continuingCombinations.map { _.map { case (wordId, locations) =>
						wordId -> locations.map { _.towards(backDirection) }
					} }
					// Combines this iteration's results with the recursive results to form a tree
					Tree(wordId -> stoppingLocations.map { _.towards(backDirection) }, continuingResults)
				}.toVector
		}
		else
			Vector()
	}
	
	
	// NESTED   ----------------------------------------
	
	private case class PreparedCombination(wordIds: Vector[Int], headAssignmentIds: Vector[Int],
	                                       baseSide: WordSide = WordSide.Left)
	{
		// Reverses both word id vector and the base side
		def reverse = copy(wordIds = wordIds.reverse, baseSide = baseSide.opposite)
	}
	
	// Mutable. Makes sure no duplicates are recorded in the DB
	private class DuplicateChecker
	{
		// ATTRIBUTES   ----------------------------
		
		// Leftmost word id -> continuing word ids
		// Inserts that would start with these ids will be skipped
		private val skips = mutable.Map[Int, Vector[Vector[Int]]]()
		
		
		// OTHER    --------------------------------
		
		// Filters the specified word combinations to exclude duplicates
		// Used for right side word combinations
		def filter(baseWordId: Int, rightCombinations: Vector[Tree[(Int, Vector[Location])]]) =
		{
			skips.get(baseWordId) match
			{
				case Some(skips) =>
					rightCombinations.map { root =>
						root.copy(children = root.children.flatMap { filterTree(_, skips) })
					}
				case None => rightCombinations
			}
		}
		
		// This method should be called whenever left side branches are included in word combinations
		def recordLeftBranches(rightWordIds: Vector[Int], leftBranches: Vector[Tree[(Int, Vector[Location])]]) =
		{
			if (leftBranches.nonEmpty)
			{
				// 3rd word and the following words are ignored on the right side
				val rightChain = rightWordIds.take(2)
				// Updates the skips based on the left side word id "chains", including the right chain also
				val leftWordChains = leftBranches.flatMap { root =>
					root.allBranches.map { chain => chain.map { _._1 }.reverse :+ root.content._1 } }
				skips ++= leftWordChains.groupMap { _.head } { _.tail }.map { case (head, tails) =>
					val existingTails = skips.getOrElse(head, Vector())
					head -> (existingTails ++ tails.map { _ ++ rightChain }).distinct
				}
			}
		}
		
		// Skips all paths that match the 'remainingSkips'
		// Returns either a) the full tree, b) a partial tree or c) no tree at all
		private def filterTree(tree: Tree[(Int, Vector[Location])], remainingSkips: Vector[Vector[Int]]): Option[Tree[(Int, Vector[Location])]] =
		{
			val applicableSkips = remainingSkips.filter { _.head == tree.content._1 }
			
			// Case: There may be skips
			if (applicableSkips.nonEmpty)
			{
				// Case: All skip-triggering word ids found => skips this tree
				if (applicableSkips.exists { _.size == 1 })
					None
				// Case: Needs to check for remaining word ids
				else
				{
					// Checks which children to skip (if any)
					val remainingChildren = tree.children.flatMap { filterTree(_, remainingSkips.map { _.tail }) }
					// Case: No children left => Keeps this node still (because it didn't match all the skip ids)
					if (remainingChildren.isEmpty)
						Some(tree.withoutChildren)
					// Case: Some of the branches may have been skipped while some remained
					else
						Some(tree.copy(children = remainingChildren))
				}
			}
			// Case: There won't be skips
			else
				Some(tree)
		}
	}
}
