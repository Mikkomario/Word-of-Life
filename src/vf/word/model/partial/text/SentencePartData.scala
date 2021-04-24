package vf.word.model.partial.text

/**
 * Represents a single section of a sentence
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 * @param sentenceId Id of the sentence this part belongs to
 * @param orderIndex Index of this part within that sentence (starting from 0)
 */
case class SentencePartData(sentenceId: Int, orderIndex: Int)
