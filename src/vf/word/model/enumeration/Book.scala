package vf.word.model.enumeration

import utopia.flow.collection.CollectionExtensions._
import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.model.immutable.Value
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.mutable.DataType.StringType
import utopia.flow.generic.model.template.ValueConvertible
import utopia.flow.operator.equality.EqualsExtensions._

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
	
	
	// IMPLEMENTED	--------------------
	
	override def toValue = id
}

object Book
{
	// ATTRIBUTES	--------------------
	
	/**
	  * All available book values
	  */
	val values: Vector[Book] = 
		Vector(Genesis, Exodus, Leviticus, Numbers, Deuteronomy, Joshua, Judges, Ruth, Samuel1, Samuel2, 
			Kings1, Kings2, Chronicles1, Chronicles2, Ezra, Nehemiah, Esther, Job, Psalms, Proverbs, 
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
	  * @param value A value representing an book id
	  * @return book matching the specified value. None if the value didn't match any book
	  */
	def findForValue(value: Value) = 
		{ value.castTo(IntType, 
			StringType) match { case Left(idVal) => findForId(idVal.getInt); case Right(stringVal) => val str = stringVal.getString; values.find { _.toString ~== str } } }
	
	/**
	  * @param id id matching a book
	  * @return book matching that id. Failure if no matching value was found.
	  */
	def forId(id: Int) = findForId(id).toTry { new NoSuchElementException(
		s"No value of Book matches id '$id'") }
	
	/**
	  * @param value A value representing an book id
	  * @return book matching the specified value, 
	  * when the value is interpreted as an book id. Failure if no matching value was found.
	  */
	def fromValue(value: Value) = 
		findForValue(value).toTry { new NoSuchElementException(s"No value of Book matches '$value'") }
	
	
	// NESTED	--------------------
	
	case object Acts extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 44
	}
	
	case object Amos extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 30
	}
	
	case object Chronicles1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 13
	}
	
	case object Chronicles2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 14
	}
	
	case object Colossians extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 51
	}
	
	case object Corinthians1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 46
	}
	
	case object Corinthians2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 47
	}
	
	case object Daniel extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 27
	}
	
	case object Deuteronomy extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 5
	}
	
	case object Ecclesiastes extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 21
	}
	
	case object Ephesians extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 49
	}
	
	case object Esther extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 17
	}
	
	case object Exodus extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 2
	}
	
	case object Ezekiel extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 26
	}
	
	case object Ezra extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 15
	}
	
	case object Galatians extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 48
	}
	
	case object Genesis extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 1
	}
	
	case object Habakkuk extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 35
	}
	
	case object Haggai extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 37
	}
	
	case object Hebrews extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 58
	}
	
	case object Hosea extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 28
	}
	
	case object Isaiah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 23
	}
	
	case object James extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 59
	}
	
	case object Jeremiah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 24
	}
	
	case object Job extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 18
	}
	
	case object Joel extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 29
	}
	
	case object John extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 43
	}
	
	case object John1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 62
	}
	
	case object John2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 63
	}
	
	case object John3 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 64
	}
	
	case object Jonah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 32
	}
	
	case object Joshua extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 6
	}
	
	case object Jude extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 65
	}
	
	case object Judges extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 7
	}
	
	case object Kings1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 11
	}
	
	case object Kings2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 12
	}
	
	case object Lamentations extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 25
	}
	
	case object Leviticus extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 3
	}
	
	case object Luke extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 42
	}
	
	case object Malachi extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 39
	}
	
	case object Mark extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 41
	}
	
	case object Matthew extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 40
	}
	
	case object Micah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 33
	}
	
	case object Nahum extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 34
	}
	
	case object Nehemiah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 16
	}
	
	case object Numbers extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 4
	}
	
	case object Obadiah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 31
	}
	
	case object Peter1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 60
	}
	
	case object Peter2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 61
	}
	
	case object Philemon extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 57
	}
	
	case object Philippians extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 50
	}
	
	case object Proverbs extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 20
	}
	
	case object Psalms extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 19
	}
	
	case object Revelation extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 66
	}
	
	case object Romans extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 45
	}
	
	case object Ruth extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 8
	}
	
	case object Samuel1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 9
	}
	
	case object Samuel2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 10
	}
	
	case object SongOfSolomon extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 22
	}
	
	case object Thessalonians1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 52
	}
	
	case object Thessalonians2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 53
	}
	
	case object Timothy1 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 54
	}
	
	case object Timothy2 extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 55
	}
	
	case object Titus extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 56
	}
	
	case object Zechariah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 38
	}
	
	case object Zephaniah extends Book
	{
		// ATTRIBUTES	--------------------
		
		override val id = 36
	}
}

