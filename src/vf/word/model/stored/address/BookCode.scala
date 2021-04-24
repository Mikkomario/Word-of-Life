package vf.word.model.stored.address

import utopia.vault.model.template.Stored
import vf.word.model.partial.address.BookCodeData

/**
 * Represents a Bible book code that has been registered to DB
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 */
case class BookCode(id: Int, data: BookCodeData) extends Stored[BookCodeData, Int]
