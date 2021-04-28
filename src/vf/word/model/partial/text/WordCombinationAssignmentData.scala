package vf.word.model.partial.text

/**
 * Contains information about a word combination's occurrence in the text
 * @author Mikko Hilpinen
 * @since 28.4.2021, v0.2
 * @param wordCombinationId Id of the word combination to assign
 * @param headAssignmentId Id of the assignment of the first word in this combination
 * @param primary Whether this is the primary (longest) word combination at that location (default = false)
 */
case class WordCombinationAssignmentData(wordCombinationId: Int, headAssignmentId: Int, primary: Boolean = false)
