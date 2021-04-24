package vf.word.model.partial.address

/**
 * Represents a 3 letter code used for a book of the Bible
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 * @param code The 3 letter code
 * @param writingId Id of the writing this code refers to
 */
case class BookCodeData(code: String, writingId: Int)
