package vf.word.model.enumeration

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.model.mutable.DataType.{IntType, StringType}
import utopia.flow.generic.model.template.ValueConvertible
import utopia.flow.operator.equality.EqualsExtensions._
import utopia.flow.util.StringExtensions._

import java.util.NoSuchElementException

/**
  * An enumeration for different books that appear within the Bible
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
sealed trait Book extends ValueConvertible
{
	// ABSTRACT	--------------------
	
	/**
	  * id used to represent this book in database and json
	  */
	def id: Int
	/**
	 * @return A code representing this book. Empty if this book doesn't have a code.
	 */
	def code: String
	
	
	// IMPLEMENTED	--------------------
	
	override def toValue = id
}

object Book
{
	// ATTRIBUTES	--------------------
	
	/**
	  * All available book values
	  */
	val values: Vector[Book] = Vector(Genesis, Exodus, Leviticus, Numbers, Deuteronomy, Joshua, Judges, Ruth,
		Samuel1, Samuel2, Kings1, Kings2, Chronicles1, Chronicles2, Ezra, Nehemiah, Esther, Job, Psalms, Proverbs,
		Ecclesiastes, SongOfSolomon, Isaiah, Jeremiah, Lamentations, Ezekiel, Daniel, Hosea, Joel, Amos,
		Obadiah, Jonah, Micah, Nahum, Habakkuk, Zephaniah, Haggai, Zechariah, Malachi, Matthew, Mark,
		Luke, John, Acts, Romans, Corinthians1, Corinthians2, Galatians, Ephesians, Philippians,
		Colossians, Thessalonians1, Thessalonians2, Timothy1, Timothy2, Titus, Philemon, Hebrews, James,
		Peter1, Peter2, John1, John2, John3, Jude, Revelation)
	
	
	// OTHER	--------------------
	
	/**
	  * @param id id representing a book
	  * @return book matching the specified id. None if the id didn't match any book
	  */
	def findForId(id: Int) = values.find { _.id == id }
	/**
	 * @param code A book code
	 * @return Book which matches that code. None if no such book was found.
	 */
	def findForCode(code: String) = code.ifNotEmpty.flatMap { code => values.find { _.code ~== code } }
	/**
	  * @param value A value representing an book id
	  * @return book matching the specified value. None if the value didn't match any book
	  */
	def findForValue(value: Value) = value.castTo(IntType, StringType) match {
		 case Left(idVal) => findForId(idVal.getInt)
		 case Right(stringVal) =>
			 val str = stringVal.getString
			 findForCode(str).orElse { values.find { _.toString ~== str } }
	 }
	/**
	  * @param id id matching a book
	  * @return book matching that id. Failure if no matching value was found.
	  */
	def forId(id: Int) =
		findForId(id).toTry { new NoSuchElementException(s"No value of Book matches id '$id'") }
	/**
	 * @param code A book code
	 * @return Book matching that code. Failure if no matching value was found.
	 */
	def forCode(code: String) =
		findForCode(code).toTry { new NoSuchElementException(s"No value of Book matches '$code'") }
	/**
	  * @param value A value representing an book id
	  * @return book matching the specified value, when the value is interpreted as an book id. Failure if no 
	  * matching value was found.
	  */
	def fromValue(value: Value) = 
		findForValue(value).toTry { new NoSuchElementException(s"No value of Book matches id '$value'") }
	
	
	// NESTED	--------------------
	
	case object Acts extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 44
		override val code: String = "Act"
	}
	
	case object Amos extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 30
		override val code: String = "Amo"
	}
	
	case object Chronicles1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 13
		override val code: String = "Ch1"
	}
	
	case object Chronicles2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 14
		override val code: String = "Ch2"
	}
	
	case object Colossians extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 51
		override val code: String = "Col"
	}
	
	case object Corinthians1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 46
		override val code: String = "Co1"
	}
	
	case object Corinthians2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 47
		override val code: String = "Co2"
	}
	
	case object Daniel extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 27
		override val code: String = "Dan"
	}
	
	case object Deuteronomy extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 5
		override val code: String = "Deu"
	}
	
	case object Ecclesiastes extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 21
		override val code: String = "Ecc"
	}
	
	case object Ephesians extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 49
		override val code: String = "Eph"
	}
	
	case object Esther extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 17
		override val code: String = "Est"
	}
	
	case object Exodus extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 2
		override val code: String = "Exo"
	}
	
	case object Ezekiel extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 26
		override val code: String = "Eze"
	}
	
	case object Ezra extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 15
		override val code: String = "Ezr"
	}
	
	case object Galatians extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 48
		override val code: String = "Gal"
	}
	
	case object Genesis extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 1
		override val code: String = "Gen"
	}
	
	case object Habakkuk extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 35
		override val code: String = "Hab"
	}
	
	case object Haggai extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 37
		override val code: String = "Hag"
	}
	
	case object Hebrews extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 58
		override val code: String = "Heb"
	}
	
	case object Hosea extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 28
		override val code: String = "Hos"
	}
	
	case object Isaiah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 23
		override val code: String = "Isa"
	}
	
	case object James extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 59
		override val code: String = "Jam"
	}
	
	case object Jeremiah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 24
		override val code: String = "Jer"
	}
	
	case object Job extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 18
		override val code: String = "Job"
	}
	
	case object Joel extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 29
		override val code: String = "Joe"
	}
	
	case object John extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 43
		override val code: String = "Joh"
	}
	
	case object John1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 62
		override val code: String = "Jo1"
	}
	
	case object John2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 63
		override val code: String = "Jo2"
	}
	
	case object John3 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 64
		override val code: String = "Jo3"
	}
	
	case object Jonah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 32
		override val code: String = "Jon"
	}
	
	case object Joshua extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 6
		override val code: String = "Jos"
	}
	
	case object Jude extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 65
		override val code: String = "Jde"
	}
	
	case object Judges extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 7
		override val code: String = "Jdg"
	}
	
	case object Kings1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 11
		override val code: String = "Kg1"
	}
	
	case object Kings2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 12
		override val code: String = "Kg2"
	}
	
	case object Lamentations extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 25
		override val code: String = "Lam"
	}
	
	case object Leviticus extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 3
		override val code: String = "Lev"
	}
	
	case object Luke extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 42
		override val code: String = "Luk"
	}
	
	case object Malachi extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 39
		override val code: String = "Mal"
	}
	
	case object Mark extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 41
		override val code: String = "Mar"
	}
	
	case object Matthew extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 40
		override val code: String = "Mat"
	}
	
	case object Micah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 33
		override val code: String = "Mic"
	}
	
	case object Nahum extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 34
		override val code: String = "Nah"
	}
	
	case object Nehemiah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 16
		override val code: String = "Neh"
	}
	
	case object Numbers extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 4
		override val code: String = "Num"
	}
	
	case object Obadiah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 31
		override val code: String = "Oba"
	}
	
	case object Peter1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 60
		override val code: String = "Pe1"
	}
	
	case object Peter2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 61
		override val code: String = "Pe2"
	}
	
	case object Philemon extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 57
		override val code: String = "Plm"
	}
	
	case object Philippians extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 50
		override val code: String = "Phi"
	}
	
	case object Proverbs extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 20
		override val code: String = "Pro"
	}
	
	case object Psalms extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 19
		override val code: String = "Psa"
	}
	
	case object Revelation extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 66
		override val code: String = "Rev"
	}
	
	case object Romans extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 45
		override val code: String = "Rom"
	}
	
	case object Ruth extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 8
		override val code: String = "Rut"
	}
	
	case object Samuel1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 9
		override val code: String = "Sa1"
	}
	
	case object Samuel2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 10
		override val code: String = "Sa2"
	}
	
	case object SongOfSolomon extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 22
		override val code: String = "Sol"
	}
	
	case object Thessalonians1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 52
		override val code: String = "Th1"
	}
	
	case object Thessalonians2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 53
		override val code: String = "Th2"
	}
	
	case object Timothy1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 54
		override val code: String = "Ti1"
	}
	
	case object Timothy2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 55
		override val code: String = "Ti2"
	}
	
	case object Titus extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 56
		override val code: String = "Tit"
	}
	
	case object Zechariah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 38
		override val code: String = "Zac"
	}
	
	case object Zephaniah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 36
		override val code: String = "Zep"
	}
}

