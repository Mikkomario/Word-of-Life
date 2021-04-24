package vf.word.database.access.many.text

import utopia.flow.datastructure.immutable.Model
import utopia.vault.database.Connection
import utopia.vault.sql.Insert
import vf.word.database.WordTables

/**
 * Used for accessing multiple writings from the database at a time
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 */
object DbWritings
{
	// COMPUTED --------------------------------
	
	private def table = WordTables.writing
	
	
	// OTHER    --------------------------------
	
	/**
	 * Inserts a new writing to the DB
	 * @param connection Database connection (implicit)
	 * @return Id of the newly inserted writing
	 */
	def insert()(implicit connection: Connection) = Insert(table, Model.empty).generatedIntKeys.head
}
