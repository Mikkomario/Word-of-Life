package vf.word.model.stored.text

import utopia.vault.model.template.Stored
import vf.word.model.partial.text.WordCombinationData

/**
 * Represents a recurring word combination that has been stored to the DB
 * @author Mikko Hilpinen
 * @since 28.4.2021, v0.2
 */
case class WordCombination(id: Int, data: WordCombinationData) extends Stored[WordCombinationData, Int]
