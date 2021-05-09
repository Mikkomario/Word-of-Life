package vf.word.model.enumeration

import utopia.flow.parse.Regex

/**
 * An enumeration for different ways to capitalize words
 * @author Mikko Hilpinen
 * @since 25.4.2021, v0.1
 */
sealed trait Capitalization
{
	/**
	 * Database value used for this capitalization level
	 */
	val id: Int
}

object Capitalization
{
	// ATTRIBUTES   ------------------------
	
	/**
	 * All values of this enumeration
	 */
	val values = Vector[Capitalization](Normal, AlwaysCapitalize, AllCaps)
	
	
	// OTHER    ----------------------------
	
	/**
	 * @param word A word
	 * @return Capitalization used in that word
	 */
	def of(word: String): Capitalization =
	{
		if (word.isEmpty)
			Normal
		else if (word.takeWhile { c => Regex.alpha(c.toString) }.forall { _.isUpper })
		{
			if (word.length == 1) AlwaysCapitalize else AllCaps
		}
		else if (word.head.isUpper)
			AlwaysCapitalize
		else
			Normal
	}
	
	/**
	 * @param id A capitalization id
	 * @return Capitalization that matches that id
	 */
	def forId(id: Int) = values.find { _.id == id }
	
	
	// NESTED   ----------------------------
	
	/**
	 * Use normal capitalization rules (Word starts with an upper case letter when it's at the beginning of a
	 * sentence but not otherwise). E.g. "and".
	 */
	case object Normal extends Capitalization
	{
		override val id = 0
	}
	
	/**
	 * Always capitalize a word. E.g. "Daniel"
	 */
	case object AlwaysCapitalize extends Capitalization
	{
		override val id = 1
	}
	
	/**
	 * The word is spelled with all upper-case letters. E.g. "LORD"
	 */
	case object AllCaps extends Capitalization
	{
		override val id = 2
	}
}
