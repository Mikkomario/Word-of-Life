package vf.word.database.access.many.bible.footnote

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple footnotes with statement placements at a time
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object DbFootnotesWithStatementPlacements 
	extends ManyFootnotesWithStatementPlacementsAccess with UnconditionalView 
		with ViewManyByIntIds[ManyFootnotesWithStatementPlacementsAccess]

