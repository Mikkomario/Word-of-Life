package vf.word.model.stored.text

import vf.word.model.enumeration.Capitalization
import vf.word.model.enumeration.Capitalization.Normal

/**
 * Represents a word that has been registered in the DB
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 * @param id DB id of this word
 * @param value This word as a string
 * @param capitalization Capitalization rule to use for this word (default = normal word rules)
 */
case class Word(id: Int, value: String, capitalization: Capitalization = Normal)
{
	// IMPLEMENTED  -----------------------
	
	override def toString = value
}
