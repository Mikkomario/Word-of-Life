package vf.word.model.stored.text

import utopia.vault.model.template.Stored
import vf.word.model.partial.text.WordCombinationAssignmentData

/**
 * Represents a recorded occurrence of a word combination in the text
 * @author Mikko Hilpinen
 * @since 28.4.2021, v0.2
 */
case class WordCombinationAssignment(id: Int, data: WordCombinationAssignmentData)
	extends Stored[WordCombinationAssignmentData, Int]
