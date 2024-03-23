package vf.word.model.partial.bible

import utopia.flow.generic.casting.ValueConversions._
import utopia.flow.generic.factory.FromModelFactoryWithSchema
import utopia.flow.generic.model.immutable.{Model, ModelDeclaration, PropertyDeclaration}
import utopia.flow.generic.model.mutable.DataType.IntType
import utopia.flow.generic.model.template.ModelConvertible
import vf.word.model.factory.bible.VerseMarkerFactory

object VerseMarkerData extends FromModelFactoryWithSchema[VerseMarkerData]
{
	// ATTRIBUTES	--------------------
	
	override lazy val schema = 
		ModelDeclaration(Vector(PropertyDeclaration("chapterIndex", IntType, Vector("chapter_index")), 
			PropertyDeclaration("verseIndex", IntType, Vector("verse_index")), 
			PropertyDeclaration("firstStatementId", IntType, Vector("first_statement_id"))))
	
	
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(valid: Model) = 
		VerseMarkerData(valid("chapterIndex").getInt, valid("verseIndex").getInt, 
			valid("firstStatementId").getInt)
}

/**
  * Locates a verse marker within a text
  * @param chapterIndex A 1-based index that indicates which chapter this verse belongs to
  * @param verseIndex A 1-based index that indicates which verse this is
  * @param firstStatementId Id of the book statement that starts this verse
  * @author Mikko Hilpinen
  * @since 21.03.2024, v0.2
  */
case class VerseMarkerData(chapterIndex: Int, verseIndex: Int, firstStatementId: Int) 
	extends VerseMarkerFactory[VerseMarkerData] with ModelConvertible
{
	// IMPLEMENTED	--------------------
	
	override def toModel = 
		Model(Vector("chapterIndex" -> chapterIndex, "verseIndex" -> verseIndex, 
			"firstStatementId" -> firstStatementId))
	
	override def withChapterIndex(chapterIndex: Int) = copy(chapterIndex = chapterIndex)
	
	override def withFirstStatementId(firstStatementId: Int) = copy(firstStatementId = firstStatementId)
	
	override def withVerseIndex(verseIndex: Int) = copy(verseIndex = verseIndex)
}

