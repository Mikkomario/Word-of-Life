package vf.word.database.factory.bible

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
import vf.word.database.storable.bible.FootnoteDbModel
import vf.word.model.partial.bible.FootnoteData
import vf.word.model.stored.bible.Footnote

/**
  * Used for reading footnote data from the DB
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object FootnoteDbFactory extends FromValidatedRowModelFactory[Footnote]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = FootnoteDbModel
	
	override lazy val defaultOrdering: Option[OrderBy] = None
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override protected def fromValidatedModel(valid: Model) = 
		Footnote(valid(this.model.id.name).getInt, 
			FootnoteData(valid(this.model.commentedStatementId.name).getInt, 
			valid(this.model.targetedWordIndex.name).int))
}

