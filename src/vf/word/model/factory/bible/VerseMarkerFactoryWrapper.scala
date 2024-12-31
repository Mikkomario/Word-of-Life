package vf.word.model.factory.bible

import utopia.flow.util.Mutate

/**
  * Common trait for classes that implement VerseMarkerFactory by wrapping a VerseMarkerFactory 
  * instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait VerseMarkerFactoryWrapper[A <: VerseMarkerFactory[A], +Repr] extends VerseMarkerFactory[Repr]
{
	// ABSTRACT	--------------------
	
	/**
	  * The factory wrapped by this instance
	  */
	protected def wrappedFactory: A
	
	/**
	  * Mutates this item by wrapping a mutated instance
	  * @param factory The new factory instance to wrap
	  * @return Copy of this item with the specified wrapped factory
	  */
	protected def wrap(factory: A): Repr
	
	
	// IMPLEMENTED	--------------------
	
	override def withChapterIndex(chapterIndex: Int) = mapWrapped { _.withChapterIndex(chapterIndex) }
	
	override def withFirstStatementId(firstStatementId: Int) = 
		mapWrapped { _.withFirstStatementId(firstStatementId) }
	
	override def withVerseIndex(verseIndex: Int) = mapWrapped { _.withVerseIndex(verseIndex) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

