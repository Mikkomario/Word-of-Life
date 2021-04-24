package vf.word.model.stored.address

/**
 * Represents a Bible book chapter reference that has been recorded to the DB
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 * @param id DB id of this chapter
 * @param bookId Id of the writing that contains this chapter
 * @param number Number of this chapter (starting from 1)
 */
case class Chapter(id: Int, bookId: Int, number: Int)
