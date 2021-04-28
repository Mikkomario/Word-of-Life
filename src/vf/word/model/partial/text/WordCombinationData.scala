package vf.word.model.partial.text

/**
 * Contains information about a word combination (when two or more words are used together multiple times)
 * @author Mikko Hilpinen
 * @since 28.4.2021, v0.2
 * @param headWordId Id of the word that begins this word combination
 * @param combinedWordId Id of the word that is added by this combination (the last word)
 * @param wordCount Number of words in this combination in total (default = 2)
 * @param parentId Id of the word combination that acts as the parent of this combination.
 *                 None if this is a two-word combination (default)
 */
case class WordCombinationData(headWordId: Int, combinedWordId: Int, wordCount: Int = 2, parentId: Option[Int] = None)
