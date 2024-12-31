package vf.word.database.factory.bible

import utopia.flow.generic.model.immutable.Model
import utopia.vault.nosql.factory.row.model.FromValidatedRowModelFactory
import utopia.vault.sql.OrderBy
import vf.word.database.storable.bible.TranslationDbModel
import vf.word.model.partial.bible.TranslationData
import vf.word.model.stored.bible.Translation

/**
  * Used for reading translation data from the DB
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
object TranslationDbFactory extends FromValidatedRowModelFactory[Translation]
{
	// ATTRIBUTES	--------------------
	
	/**
	  * Model that specifies how the data is read
	  */
	val model = TranslationDbModel
	
	override lazy val defaultOrdering: Option[OrderBy] = None
	
	
	// IMPLEMENTED	--------------------
	
	override def table = model.table
	
	override protected def fromValidatedModel(valid: Model) = 
		Translation(valid(this.model.id.name).getInt, TranslationData(valid(this.model.name.name).getString, 
			valid(this.model.abbreviation.name).getString, valid(this.model.created.name).getInstant))
}

