package runtime;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

public interface ConstraintIterable<T extends Constraint> extends Iterable<T> {

	/**
	 * Returns a universal iterator (cf.\ {@link #universalIterator()})
	 * if possible. If no universal iterator implementation is present,
	 * an iterator object as one has come to expect from iterators
	 * in Java, that is: if a structural modification is made that
	 * could impede correct iteration, a {@link ConcurrentModificationException}
	 * is thrown. 
	 * 
	 * @see #universalIterator()
	 */
	public Iterator<T> iterator();
	
	/**
	 * Returns a <em>universal iterator</em>: 
	 * an iterator that does not fail under structural modifications
	 * (that is: constraint insertions or deletions) 
	 * of the underlying data structure during the iteration
	 * (in controst to the {@link Iterator} implementations provided
     * by the Java Collections Framework, which are <em>fail-fast</em>)
     * 
	 * @return A universal iterator.
	 * @throws UnsupportedOperationException
	 * 	If no universal iterator implementation is present.
	 */
	public Iterator<T> universalIterator() throws UnsupportedOperationException;
	
	/**
	 * Returns a <em>semi-universal iterator</em>: 
	 * an iterator that does not fail under constraint insertions,
	 * but which has no general guarantees 
	 * (that is: it may fail, or even produce wrong results)
	 * under constraint deletions.
	 * Removal though of the constraint that was last returned 
	 * by the {@link Iterator#next()} method may not cause the iterator to fail. 
     * 
	 * @return A semi-universal iterator.
	 * @throws UnsupportedOperationException
	 * 	If neither a semi-universal or universal iterator 
	 * 	implementation is present.
	 * 	If no semi-universal iterator implementation is present, 
	 * 	but a universal iterator implementation is,
	 * 	a universal iterator should be returned.
	 */
	public Iterator<T> semiUniversalIterator() throws UnsupportedOperationException;
	
	/**
	 * <p>
	 * Returns an <em>existential iterator</em>: an iterator that 
	 * is not guaranteed to work after structural modifications 
	 * of the underlying data structure during the iteration,
	 * nor is it required to fail by throwing an exeption
	 * (in controst to the {@link Iterator} implementations provided
     * by the Java Collections Framework, which throw 
     * a {@link ConcurrentModificationException} in case of structural
     * modifications during iteration).
     * If used after a structural modification, 
     * the behavior of the returned iterator is undetermined.
     * </p>
     * <p>
     * If no existential iterator implementation is present, 
	 * but a universal or semi-universal iterator implementation is,
	 * the latter kind should be returned.
	 * This operation is always supported,
	 * as at least one of the three kinds has to be supported
	 * (otherwise just do not implement the interface).
     * </p>
     * 
	 * @return An existential iterator.
	 */
	public Iterator<T> existentialIterator();
}