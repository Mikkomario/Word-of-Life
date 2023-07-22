package vf.word.database.access.id.many

import utopia.vault.nosql.access.many.column.ManyIntIdAccess
import vf.word.database.WordTables

/**
 * Used for accessing multiple word ids at a time
 * @author Mikko Hilpinen
 * @since 28.4.2021, v0.2
 */
object DbWordIds extends ManyIntIdAccess
{
	// IMPLEMENTED  -----------------------------
	
	override def table = WordTables.word
	override def target = table
	
	override def globalCondition = None
}
