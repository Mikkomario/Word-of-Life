package vf.word.model.factory.bible

import utopia.flow.util.Mutate

/**
  * Common trait for classes that implement FootnoteFactory by wrapping a FootnoteFactory instance
  * @tparam A Type of constructed instances
  * @tparam Repr Implementing type of this factory
  * @author Mikko Hilpinen
  * @since 30.12.2024, v0.2
  */
trait FootnoteFactoryWrapper[A <: FootnoteFactory[A], +Repr] extends FootnoteFactory[Repr]
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
	
	override def withCommentedStatementId(commentedStatementId: Int) = 
		mapWrapped { _.withCommentedStatementId(commentedStatementId) }
	
	override def withTargetedWordIndex(targetedWordIndex: Int) = 
		mapWrapped { _.withTargetedWordIndex(targetedWordIndex) }
	
	
	// OTHER	--------------------
	
	/**
	  * Modifies this item by mutating the wrapped factory instance
	  * @param f A function for mutating the wrapped factory instance
	  * @return Copy of this item with a mutated wrapped factory
	  */
	protected def mapWrapped(f: Mutate[A]) = wrap(f(wrappedFactory))
}

