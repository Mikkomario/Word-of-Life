package vf.word.model.stored.address

import utopia.vault.model.template.Stored
import vf.word.model.partial.address.VerseData

/**
 * Represents a verse reference that has been stored to the DB
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 * @param id DB id of this verse
 */
case class Verse(id: Int, data: VerseData) extends Stored[VerseData, Int]
