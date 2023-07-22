package vf.word.database.access.single.text

import utopia.vault.database.Connection
import vf.word.database.factory.text.WordAssignmentFactory
import vf.word.database.model.text.WordAssignmentModel

/**
 * Used for accessing individual words in the database
 * @author Mikko Hilpinen
 * @since 30.4.2021, v0.2
 */
object DbWord
{
	// COMPUTED ---------------------------
	
	private def assignmentFactory = WordAssignmentFactory
	private def assignmentModel = WordAssignmentModel
	
	
	// OTHER    ---------------------------
	
	/**
	 * @param wordId A word id
	 * @return An access point to that word's data
	 */
	def apply(wordId: Int) = DbSingleWord(wordId)
	
	
	// NESTED   ---------------------------
	
	case class DbSingleWord(wordId: Int)
	{
		/**
		 * @param connection Implicit DB Connection
		 * @return Assignments / locations of this word in all the texts
		 */
		def assignments(implicit connection: Connection) =
			assignmentFactory.findMany(assignmentModel.withWordId(wordId).toCondition)
	}
}
