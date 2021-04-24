package vf.word.model.stored.text

import utopia.vault.model.template.Stored
import vf.word.model.partial.text.SentenceSegmentData

/**
 * Represents and individual sentence segment (smallest part of a sentence that's not a word) that has been registered
 * to the DB
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 * @param id DB id of this sentence segment
 */
case class SentenceSegment(id: Int, data: SentenceSegmentData) extends Stored[SentenceSegmentData, Int]
