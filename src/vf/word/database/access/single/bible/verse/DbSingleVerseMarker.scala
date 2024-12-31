package vf.word.database.access.single.bible.verse

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.word.model.stored.bible.VerseMarker

/**
  * An access point to individual verse markers, based on their id
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
case class DbSingleVerseMarker(id: Int) 
	extends UniqueVerseMarkerAccess with SingleIntIdModelAccess[VerseMarker]

