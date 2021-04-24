package vf.word.model.partial.address

/**
 * Represents a Bible verse mark
 * @author Mikko Hilpinen
 * @since 24.4.2021, v0.1
 * @param chapterId Id of the chapter this verse belongs to
 * @param number The number of this verse (starting from 1)
 * @param startSegmentId Id of the sentence segment this verse starts at
 */
case class VerseData(chapterId: Int, number: Int, startSegmentId: Int)
