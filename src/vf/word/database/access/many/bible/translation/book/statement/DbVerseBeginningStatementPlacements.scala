package vf.word.database.access.many.bible.translation.book.statement

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple verse beginning statement placements at a time
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object DbVerseBeginningStatementPlacements 
	extends ManyVerseBeginningStatementPlacementsAccess with UnconditionalView 
		with ViewManyByIntIds[ManyVerseBeginningStatementPlacementsAccess]

