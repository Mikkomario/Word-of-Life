package vf.word.model.stored.text

/**
 * Represents a sentence that has been stored to the DB (with no associated data included)
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 * @param id             DB id of this sentence
 * @param contextId Id of the writing this sentence is part of
 * @param orderIndex Index of the position of this sentence in it's writing (starting from 0)
 */
case class Sentence(id: Int, contextId: Int, orderIndex: Int)
