package vf.word.model.stored.text

/**
 * Represents a word that has been registered in the DB
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 * @param id DB id of this word
 * @param value This word as a string
 * @param alwaysCapitalized Whether this word is always presented as capitalized
 */
case class Word(id: Int, value: String, alwaysCapitalized: Boolean = false)
{
	// IMPLEMENTED  -----------------------
	
	override def toString = value
}
