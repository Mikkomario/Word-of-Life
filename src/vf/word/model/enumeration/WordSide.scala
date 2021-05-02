package vf.word.model.enumeration

/**
 * An enumeration for left or right side
 * @author Mikko Hilpinen
 * @since 30.4.2021, v0.2
 */
sealed trait WordSide
{
	/**
	 * DB id of this side
	 */
	val id: Int
	
	/**
	 * @return The side opposite to this one
	 */
	def opposite: WordSide
}

object WordSide
{
	/**
	 * Word appears on the left side
	 */
	case object Left extends WordSide
	{
		val id = 0
		override def opposite = Right
	}
	/**
	 * Word appears on the right side
	 */
	case object Right extends WordSide
	{
		val id = 1
		override def opposite = Left
	}
}
