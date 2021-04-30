package vf.word.model.partial.text

import vf.word.model.enumeration.WordSide

/**
 * Contains information about a word combination (when two or more words are used together multiple times)
 * @author Mikko Hilpinen
 * @since 28.4.2021, v0.2
 * @param wordCount Number of words in this combination in total (default = 2)
 * @param baseCombinationId Id of the word combination that forms a section of this combination.
 *                          None if there is no such combination (default)
 * @param baseCombinationSide The side on which the base combination appears, if specified (default = left)
 */
case class WordCombinationData(wordCount: Int = 2, baseCombinationId: Option[Int] = None,
                               baseCombinationSide: WordSide = WordSide.Left)
