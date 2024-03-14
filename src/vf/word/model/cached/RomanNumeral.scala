package vf.word.model.cached

import utopia.flow.parse.string.Regex

import scala.language.implicitConversions

object RomanNumeral
{
	// ATTRIBUTES   --------------------------
	
	private val acceptedCharRegex = Regex.anyOf("IVXLCDM")
	/**
	 * A regular expression for identifying Roman numerals.
	 */
	val regex = acceptedCharRegex.oneOrMoreTimes
	
	private val letterValues = Map('I' -> 1, 'V' -> 5, 'X' -> 10, 'L' -> 50, 'C' -> 100, 'D' -> 500, 'M' -> 1000)
	
	
	// IMPLICIT ------------------------------
	
	implicit def unwrap(n: RomanNumeral): Int = n.value
	
	
	// OTHER    ------------------------------
	
	/**
	 * Converts a string into a roman numeral
	 *
	 * @param string String to convert into a numeral
	 * @throws IllegalArgumentException If the specified string contains a character any other than I, V, X, L, C, D or M
	 * @return A Roman numeral interpreting the specified string
	 */
	@throws[IllegalArgumentException]("If the specified string contains a character any other than I, V, X, L, C, D or M")
	def apply(string: String): RomanNumeral = {
		val symbols = string.toVector.map(letterValues.apply)
		var total = 0
		// Adds the listed symbol values together in order to form the actual numeric value
		symbols.indices.foreach { index =>
			val value = symbols(index)
			// Case: A smaller value appears before a larger value
			// => Interprets this as a reduction (e.g. IV = -1 + 5 = 4)
			if (symbols.lift(index + 1).exists { _ > value })
				total -= value
			// Case: This value is larger or equal to the next one => Adds it to the total
			else
				total += value
		}
		apply(string, total)
	}
}

/**
 * Represents a number written in Roman numeric symbols.
 * E.g. "XIV" would be 14.
 * @author Mikko Hilpinen
 * @since 11/03/2024, v0.2
 */
case class RomanNumeral(string: String, value: Int)
{
	override def toString = string
}
