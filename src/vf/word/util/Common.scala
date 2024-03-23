package vf.word.util

import utopia.flow.async.context.ThreadPool
import utopia.flow.util.logging.{Logger, SysErrLogger}
import utopia.vault.database.ConnectionPool
import vf.word.database.ConnectionPool

import scala.concurrent.ExecutionContext

/**
 * Contains globally used constants for this project
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 */
object Common
{
	// ATTRIBUTES   ------------------------------
	
	/**
	 * Standard logging implementation used in this project
	 */
	implicit val log: Logger = SysErrLogger
	/**
	 * The thread pool used in this project
	 */
	implicit val threadPool: ThreadPool = new ThreadPool("word")
	/**
	 * Connection pool used for constructing database-connections
	 */
	implicit val cPool: ConnectionPool = new ConnectionPool()
	
	/**
	 * Name of the database used
	 */
	val dbName = "word"
}
