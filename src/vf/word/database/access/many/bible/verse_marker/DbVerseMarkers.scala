package vf.word.database.access.many.bible.verse_marker

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.nosql.view.UnconditionalView

/**
  * The root access point when targeting multiple verse markers at a time
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object DbVerseMarkers extends ManyVerseMarkersAccess with UnconditionalView
{
	// OTHER	--------------------
	
	/**
	  * @param ids Ids of the targeted verse markers
	  * @return An access point to verse markers with the specified ids
	  */
	def apply(ids: Set[Int]) = new DbVerseMarkersSubset(ids)
	
	
	// NESTED	--------------------
	
	class DbVerseMarkersSubset(targetIds: Set[Int]) extends ManyVerseMarkersAccess
	{
		// IMPLEMENTED	--------------------
		
		override def accessCondition = Some(index in targetIds)
	}
}

