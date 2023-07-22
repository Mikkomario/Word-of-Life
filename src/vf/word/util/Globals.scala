package vf.word.util

import utopia.flow.async.context.ThreadPool
import utopia.flow.util.logging.{Logger, SysErrLogger}

import scala.concurrent.ExecutionContext

/**
 * Contains globally used constants for this project
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 */
// TODO: Rename to Common
object Globals
{
	// ATTRIBUTES   ------------------------------
	
	/**
	 * Standard logging implementation used in this project
	 */
	implicit val log: Logger = SysErrLogger
	/**
	 * The thread pool used in this project
	 */
	// TODO: No need for separate ThreadPool and ExecutionContext values
	val threadPool = new ThreadPool("word")
	
	
	// IMPLICIT ----------------------------------
	
	/**
	 * @return Implicit execution context used in this project
	 */
	implicit def executionContext: ExecutionContext = threadPool
}
