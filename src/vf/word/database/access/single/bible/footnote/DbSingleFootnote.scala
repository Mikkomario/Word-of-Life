package vf.word.database.access.single.bible.footnote

import utopia.vault.nosql.access.single.model.distinct.SingleIntIdModelAccess
import vf.word.model.stored.bible.Footnote

/**
  * An access point to individual footnotes, based on their id
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class DbSingleFootnote(id: Int) extends UniqueFootnoteAccess with SingleIntIdModelAccess[Footnote]

