package vf.word.model.stored.text

import utopia.vault.model.template.Stored
import vf.word.model.partial.text.SentencePartData

/**
 * Represents a single section of a sentence that has been registered to the DB
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 * @param id DB id of this sentence part
 */
case class SentencePart(id: Int, data: SentencePartData) extends Stored[SentencePartData, Int]
