package vf.word.database.access.many.bible.translation.book

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple contextual books at a time
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object DbContextualBooks 
	extends ManyContextualBooksAccess with UnconditionalView with ViewManyByIntIds[ManyContextualBooksAccess]

