package vf.word.model.stored.text

import utopia.vault.model.template.Stored
import vf.word.model.partial.text.WordCombinationWordData

/**
 * Represents a word combination word assignment that has been registered to the DB
 * @author Mikko Hilpinen
 * @since 30.4.2021, v0.2
 */
case class WordCombinationWord(id: Int, data: WordCombinationWordData) extends Stored[WordCombinationWordData, Int]
