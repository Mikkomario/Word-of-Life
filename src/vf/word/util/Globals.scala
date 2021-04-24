package vf.word.util

import utopia.flow.async.ThreadPool

import scala.concurrent.ExecutionContext

/**
 * Contains globally used constants for this project
 * @author Mikko Hilpinen
 * @since 23.4.2021, v0.1
 */
object Globals
{
	// ATTRIBUTES   ------------------------------
	
	/**
	 * The thread pool used in this project
	 */
	val threadPool = new ThreadPool("word")
	
	
	// IMPLICIT ----------------------------------
	
	/**
	 * @return Implicit execution context used in this project
	 */
	implicit def executionContext: ExecutionContext = threadPool.executionContext
}
