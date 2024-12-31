package vf.word.database.access.many.bible.footnote.statement

import utopia.vault.nosql.view.{UnconditionalView, ViewManyByIntIds}

/**
  * The root access point when targeting multiple footnote statement placements at a time
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
object DbFootnoteStatementPlacements 
	extends ManyFootnoteStatementPlacementsAccess with UnconditionalView 
		with ViewManyByIntIds[ManyFootnoteStatementPlacementsAccess]

