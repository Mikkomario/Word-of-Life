package vf.word.database.access.id.many

import utopia.logos.database.LogosTables
import utopia.vault.nosql.access.many.column.ManyIntIdAccess

/**
 * Used for accessing multiple word ids at a time
 * @author Mikko Hilpinen
 * @since 28.4.2021, v0.2
 */
object DbWordIds extends ManyIntIdAccess
{
	// IMPLEMENTED  -----------------------------
	
	override def table = LogosTables.word
	override def target = table
	
	override def accessCondition = None
}
