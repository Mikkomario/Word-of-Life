package vf.word.database.access.many.bible.translation.book.statement

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple book statement placements at a time
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object DbBookStatementPlacements 
	extends ManyBookStatementPlacementsAccess with UnconditionalView 
		with ViewManyByIntIds[ManyBookStatementPlacementsAccess]

