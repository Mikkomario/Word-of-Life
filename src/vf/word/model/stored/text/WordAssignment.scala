package vf.word.model.stored.text

import utopia.vault.model.template.Stored
import vf.word.model.partial.text.WordAssignmentData

/**
 * Represents a word location assignment that has been registered to DB
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 */
case class WordAssignment(id: Int, data: WordAssignmentData) extends Stored[WordAssignmentData, Int]
