package vf.word.database.access.many.bible.verse

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple verse markers at a time
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object DbVerseMarkers 
	extends ManyVerseMarkersAccess with UnconditionalView with ViewManyByIntIds[ManyVerseMarkersAccess]

