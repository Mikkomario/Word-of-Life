package vf.word.controller.process

import utopia.flow.collection.immutable.Tree
import utopia.flow.util.ActionBuffer
import utopia.flow.collection.CollectionExtensions._
import utopia.flow.operator.equality.EqualsFunction
import utopia.vault.database.Connection
import utopia.vault.sql.Condition
import vf.word.database.access.many.text.DbWords
import vf.word.database.access.single.text.DbWord
import vf.word.database.factory.text.WordAssignmentFactory
import vf.word.database.model.text.{WordAssignmentModel, WordCombinationAssignmentModel, WordCombinationModel, WordCombinationWordModel}
import vf.word.model.cached.Location
import vf.word.model.enumeration.Capitalization.{AllCaps, AlwaysCapitalize, Normal}
import vf.word.model.enumeration.WordSide
import vf.word.model.partial.text.{WordCombinationAssignmentData, WordCombinationData, WordCombinationWordData}
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
	
	private val minimumCombinationOccurrence = 7
	private val bufferSize = 350
	
	
	// OTHER    ---------------------------------------
	
	/**
	 * Searches and records word combinations throughout all the texts
	 * @param connection DB Connection (implicit)
	 */
	def apply()(implicit connection: Connection) =
	{
		// The inserts are bundled using a set of buffers
		val insertBuffer = new InsertBuffer()
		val duplicateChecker = new DuplicateChecker()
		var processedWordIds = Set[Int]()
		
		// Selects base words within groups based on capitalization
		// Handles each word at a time
		Vector(AllCaps, AlwaysCapitalize, Normal).view.flatMap { DbWords.withCapitalization(_).ids }.foreach { wordId =>
			val headAssignments = DbWord(wordId).assignments
			val numberOfOrigins = headAssignments.size
			// Skips words that appear too few or too many times
			if (numberOfOrigins >= minimumCombinationOccurrence && numberOfOrigins < 20000)
			{
				println(s"Processing word $wordId (${headAssignments.size} locations)")
				// Searches the words that appear after this word in the text
				// May filter the results based on already inserted combinations
				val rightwardCombinations = duplicateChecker.filter(wordId,
					combinationsFrom(headAssignments, excludeWordIds = processedWordIds))
				// Checks which of the combinations extend to the left and by how much
				val leftwardCombinations = combinationsFrom(
					rightwardCombinations.flatMap { _.allNavsIterator.flatMap { _.headAssignments } }, WordSide.Left)
				// Combines and saves the combinations
				rightwardCombinations.foreach { root =>
					insertBuffer ++= combineCombinations(Vector(wordId), root, leftwardCombinations, duplicateChecker)
				}
				
				processedWordIds += wordId
			}
		}
		
		// Inserts the remaining combinations
		println("Inserting the last buffered data")
		insertBuffer.flush()
	}
	
	private def combineCombinations(baseWordIds: Vector[Int], node: Tree[CombinationPiece],
	                                leftCombinations: Vector[Tree[CombinationPiece]],
	                                duplicateChecker: DuplicateChecker): Vector[Tree[PreparedCombination]] =
	{
		val rootHeadAssignments = node.nav.headAssignments
		val wordIds = baseWordIds :+ node.nav.wordId
		
		// Collects the branches first
		val branchResults = node.children.flatMap { childNode =>
			combineCombinations(wordIds, childNode, leftCombinations, duplicateChecker) }
		// Also collects the leftward results
		// (which need to be reversed because the word ids are listed from right to left)
		val leftTrees = leftCombinations.flatMap { node =>
			// Checks whether the specified node is associated with this root node
			// FIXME: This is likely wrong. findBranches was removed from Flow
			//  and I can't determine what was the original intent here. Hopefully this fix will work.
			node.filterWithPaths { _.nav.headAssignments
					.existsCount(minimumCombinationOccurrence)(rootHeadAssignments.contains) }
				.map { _.last }
			
			/*
			// Case: The left root is associated with this node
			if (node.nav.headAssignments.existsCount(minimumCombinationOccurrence)(rootHeadAssignments.contains))
				Vector(node)
			// Case: Some of the left child nodes may be associated with this node
			else
				node.filterWithPaths { _.nav.headAssignments
					.existsCount(minimumCombinationOccurrence)(rootHeadAssignments.contains) }
					.map { _.last }
			 */
		}
		// Records the left side branches to the duplicate checker also
		duplicateChecker.recordLeftBranches(wordIds, leftTrees)
		val leftResults = leftTrees.flatMap { leftNode =>
			combineCombinations(wordIds.reverse, leftNode, Vector(), duplicateChecker) }
			.map { _.map { _.reverse } }
		
		// Checks whether this node ought to be saved
		// Case: Yes => Uses this node as the base for other associated nodes
		if (rootHeadAssignments.size >= minimumCombinationOccurrence) {
			val baseCombination = PreparedCombination(wordIds, node.nav.assignments)
			Vector(Tree(baseCombination, branchResults ++ leftResults))
		}
		// Case: No => Only save branches / left side results
		else
			branchResults ++ leftResults
	}
	
	// Each tree node contains the singular word id + the start locations of the word combination ending with that id
	private def combinationsFrom(baseAssignments: Vector[WordAssignment], direction: WordSide = WordSide.Right,
	                             excludeWordIds: Set[Int] = Set())
	                            (implicit connection: Connection): Vector[Tree[CombinationPiece]] =
	{
		if (baseAssignments.nonEmpty)
		{
			val backDirection = direction.opposite
			
			// Finds the words listed next to the base locations
			assignmentsNextTo(baseAssignments, direction).toVector
				// Only includes the words of which there are multiple instances
				.groupBy { _.wordId }
				.filter { case (_, assignments) => assignments.size >= minimumCombinationOccurrence }
				// Continues to check whether the combinations continue
				.map { case (wordId, nextAssignments) =>
					val baseAssignmentForNextAssignmentId = nextAssignments.map { next =>
						val baseLocation = next.location.towards(backDirection)
						next.id -> baseAssignments.find { _.location == baseLocation }.get
					}.toMap
					
					// If this word was in the list of excluded words, no continuing combinations are searched
					if (excludeWordIds.contains(wordId))
						Tree(CombinationPiece(wordId, nextAssignments.map { assignment =>
							Vector(baseAssignmentForNextAssignmentId(assignment.id), assignment) }))
					else
					{
						val continuingCombinations = combinationsFrom(nextAssignments, direction, excludeWordIds)
						// Adjusts the locations of results to start from the correct baseLocation
						val continuingAssignments = continuingCombinations
							.flatMap { _.nav.headAssignments }.toSet
						val stoppingAssignments = nextAssignments.filterNot(continuingAssignments.contains)
						
						val continuingResults = continuingCombinations.map { _.map { combination =>
							combination.copy(assignments = combination.assignments.map { range =>
								baseAssignmentForNextAssignmentId(range.head.id) +: range
							})
						} }
						// Combines this iteration's results with the recursive results to form a tree
						Tree(CombinationPiece(wordId, stoppingAssignments
							.map { assignment => Vector(baseAssignmentForNextAssignmentId(assignment.id), assignment) }),
							continuingResults)
					}
				}.toVector
		}
		else
			Vector()
	}
	
	private def assignmentsNextTo(original: Vector[WordAssignment], direction: WordSide)
	                             (implicit connection: Connection) =
	{
		// Performs the queries in smaller pieces to avoid overburdening the database and results parsing
		val numberOfOrigins = original.size
		Iterator.from(0, 250).takeWhile { _ < numberOfOrigins }
			.map { startIndex => original.slice(startIndex, startIndex + 250)
				.map { _.location.towards(direction) }.filter { _.isPositive }
				.map { WordAssignmentModel.withLocation(_).toCondition } }
			.map { Condition.or(_) }
			.flatMap { WordAssignmentFactory.findMany(_) }
	}
	
	
	// NESTED   ----------------------------------------
	
	// Assignments contain both the first word and the last word assignment
	private case class CombinationPiece(wordId: Int, assignments: Vector[Vector[WordAssignment]])
	{
		def headAssignments = assignments.map { _.head }
	}
	
	// Assignment ids contain the first and the last word assignments
	private case class PreparedCombination(wordIds: Vector[Int], assignments: Vector[Vector[WordAssignment]],
	                                       baseSide: WordSide = WordSide.Left)
	{
		def headAssignmentIds = assignments.map { _.head.id }
		
		// Reverses the word id vector, assignment meaning and the base side
		def reverse = PreparedCombination(wordIds.reverse, assignments.map { _.reverse },
			baseSide.opposite)
	}
	
	private class InsertBuffer(implicit connection: Connection)
	{
		// ATTRIBUTES   -----------------------------
		
		private val wordInsertBuffer = ActionBuffer[WordCombinationWordData](1000) { wordData =>
			println(s"Inserting ${wordData.size} combination words")
			WordCombinationWordModel.insert(wordData)
		}
		private val assignmentInsertBuffer = ActionBuffer[WordCombinationAssignmentData](1500) { assignmentData =>
			println(s"Inserting ${assignmentData.size} combination assignments")
			WordCombinationAssignmentModel.insert(assignmentData)
		}
		private val primaryInsertBuffer = ActionBuffer[(Tree[PreparedCombination], Option[Int])](200) { trees =>
			insertCombinations(trees)
		}
		
		
		// OTHER    ------------------------------
		
		def ++=(roots: Vector[Tree[PreparedCombination]]) = primaryInsertBuffer ++= roots.map { _ -> None }
		
		def flush() =
		{
			primaryInsertBuffer.flush()
			wordInsertBuffer.flush()
			assignmentInsertBuffer.flush()
		}
		
		private def insertCombinations(remaining: Vector[(Tree[PreparedCombination], Option[Int])])
		                               (implicit connection: Connection): Unit =
		{
			if (remaining.nonEmpty)
			{
				println(s"Inserting ${remaining.size} new word combinations")
				
				// Performs the inserts in layers in order to acquire the base combination ids which are required in the
				// following inserts
				// Starts by inserting the base combinations
				val insertedCombinations = WordCombinationModel.insert(
					remaining.map { case (combinationTree, baseId) =>
						val combination = combinationTree.nav
						WordCombinationData(combination.wordIds.size, baseId, combination.baseSide)
					})
				val insertedRemaining = insertedCombinations.zip(remaining.map { _._1 })
				// Next assigns the words into those combinations (inserts in bulks)
				wordInsertBuffer ++= insertedRemaining
					.flatMap { case (combination, node) => node.nav.wordIds.zipWithIndex
						.map { case (wordId, index) => WordCombinationWordData(wordId, Location(combination.id, index)) } }
				// Next assigns the combinations to correct places (inserts in bulks)
				assignmentInsertBuffer ++= insertedRemaining.flatMap { case (combination, node) =>
					val headWordId = node.nav.wordIds.head
					val primaryLocations = node.nav.headAssignmentIds
					val secondaryLocations = node.navsBelowIterator.flatMap { combination =>
						val headAdjust = combination.wordIds.indexOf(headWordId)
						combination.assignments.map { _(headAdjust).id }
					}.toVector
					
					primaryLocations.map { assignmentId =>
						WordCombinationAssignmentData(combination.id, assignmentId, primary = true) } ++
						secondaryLocations.map { assignmentId => WordCombinationAssignmentData(combination.id, assignmentId) }
				}
				// Finally moves to the underlying layers (which are handled in bulks)
				insertedRemaining.foreach { case (combination, node) =>
					primaryInsertBuffer ++= node.children.map { _ -> Some(combination.id) }
				}
			}
		}
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
		def filter(baseWordId: Int, rightCombinations: Vector[Tree[CombinationPiece]]) = {
			skips.get(baseWordId) match {
				case Some(skips) =>
					rightCombinations.map { root =>
						root.copy(children = root.children.flatMap { filterTree(_, skips) })(EqualsFunction.default)
					}
				case None => rightCombinations
			}
		}
		
		// This method should be called whenever left side branches are included in word combinations
		def recordLeftBranches(rightWordIds: Vector[Int], leftBranches: Vector[Tree[CombinationPiece]]) = {
			if (leftBranches.nonEmpty) {
				// 3rd word and the following words are ignored on the right side
				val rightChain = rightWordIds.take(2)
				// Updates the skips based on the left side word id "chains", including the right chain also
				val leftWordChains = leftBranches.flatMap { root =>
					root.branchesBelowIterator.map { chain => chain.map { _.nav.wordId }.reverse :+ root.nav.wordId } }
				skips ++= leftWordChains.groupMap { _.head } { _.tail }.map { case (head, tails) =>
					val existingTails = skips.getOrElse(head, Vector())
					head -> (existingTails ++ tails.map { _ ++ rightChain }).distinct
				}
			}
		}
		
		// Skips all paths that match the 'remainingSkips'
		// Returns either a) the full tree, b) a partial tree or c) no tree at all
		private def filterTree(tree: Tree[CombinationPiece],
		                        remainingSkips: Vector[Vector[Int]]): Option[Tree[CombinationPiece]] =
		{
			val applicableSkips = remainingSkips.filter { _.head == tree.nav.wordId }
			
			// Case: There may be skips
			if (applicableSkips.nonEmpty) {
				// Case: All skip-triggering word ids found => skips this tree
				if (applicableSkips.exists { _.size == 1 })
					None
				// Case: Needs to check for remaining word ids
				else {
					// Checks which children to skip (if any)
					val remainingChildren = tree.children.flatMap { filterTree(_, applicableSkips.map { _.tail }) }
					// Case: No children left => Keeps this node still (because it didn't match all the skip ids)
					if (remainingChildren.isEmpty)
						Some(tree.withoutChildren)
					// Case: Some of the branches may have been skipped while some remained
					else
						Some(tree.copy(children = remainingChildren)(EqualsFunction.default))
				}
			}
			// Case: There won't be skips
			else
				Some(tree)
		}
	}
}
