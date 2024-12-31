package vf.word.model.factory.bible

import utopia.flow.util.Mutate
import vf.word.model.enumeration.Book

/**
  * Common trait for classes that implement BookTranslationFactory by wrapping a 
  * BookTranslationFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait BookTranslationFactoryWrapper[A <: BookTranslationFactory[A], +Repr] 
	extends BookTranslationFactory[Repr]
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
	
	override def withBook(book: Book) = mapWrapped { _.withBook(book) }
	
	override def withTranslationId(translationId: Int) = mapWrapped { _.withTranslationId(translationId) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

