package vf.word.database.access.many.text

import utopia.vault.database.Connection
import utopia.vault.nosql.access.{Indexed, ManyRowModelAccess}
import vf.word.database.factory.text.WordFactory
import vf.word.database.model.text.WordModel
import vf.word.model.enumeration.Capitalization
import vf.word.model.stored.text.Word

/**
 * Used for accessing multiple words at a time
 * @author Mikko Hilpinen
 * @since 8.5.2021, v0.2
 */
object DbWords extends ManyRowModelAccess[Word] with Indexed
{
	// COMPUTED ---------------------------------
	
	private def model = WordModel
	
	
	// IMPLEMENTED  -----------------------------
	
	override def factory = WordFactory
	
	override def globalCondition = None
	
	
	// OTHER    ---------------------------------
	
	/**
	 * @param capitalization A capitalization style
	 * @return An access point to words with that capitalization
	 */
	def withCapitalization(capitalization: Capitalization) =
		DbWordsWithCapitalization(capitalization)
	
	
	// NESTED   ---------------------------------
	
	case class DbWordsWithCapitalization(capitalization: Capitalization) extends ManyRowModelAccess[Word]
	{
		// COMPUTED -----------------------------
		
		/**
		 * @param connection DB Connection (implicit)
		 * @return Ids of all the words accessible
		 */
		def ids(implicit connection: Connection) = pullColumn(index).flatMap { _.int }
		
		
		// IMPLEMENTED  -------------------------
		
		override def factory = DbWords.factory
		
		override def globalCondition = Some(model.withCapitalization(capitalization).toCondition)
	}
}
