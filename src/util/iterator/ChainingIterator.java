package util.iterator;

import java.util.Arrays;
import java.util.Iterator;
import util.iterator.ConvertingIterator.Convertor;

/**
 * <p>
 * Provides an iterator over a sequence of iterators, chaining them together
 * so to speak. Henceforth we will call this sequence of iterators <i>outer</i>
 * and its elements <i>inner</i> iterators. The iterator will traverse
 * the inner iterators in the order they are presented in the outer
 * sequence.
 * </p>
 * <p>
 * This iterator will inherit the <i>fail-fast</i> behaviour of the nested
 * iterators it decorates. If one of the underlying collections
 * has fail-fast iterators and is structurally modified at any time 
 * after the iterator is created the iterator will throw a
 * ConcurrentModificationException.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the
 * future.
 * </p>
 * <p>
 * Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis. 
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 * </p>
 * 
 * @see java.util.Iterator
 * @author Peter Van Weert
 */
public class ChainingIterator<T> implements Iterator<T> {

    /*
     * I know I should be using getters and setters here, but I
     * cannot see why the representation would ever change. Therefore
     * I have opted for performance here...
     */
    protected Iterator<Iterator<? extends T>> outer;
    
    protected Iterator<? extends T> inner;

    /**
     * Creates a new iterator over the iterators returned by the given iterables.
     * 
     * @param iterables 
     *  The <code>Iterable</code>s over whose iterator's elements this
     *  iterator will iterate.
     *  
     * @throws NullPointerException
     *  If <code>iterables</code> is a null pointer.
     */
    @SuppressWarnings("unchecked")
    public ChainingIterator(Iterable<? extends T>... iterables) 
    throws NullPointerException {
        this(new ConvertingIterator<Iterable<? extends T>, Iterator<? extends T>>(
            new ArrayIterator<Iterable<? extends T>>(iterables), 
            new Convertor<Iterable<? extends T>, Iterator<? extends T>>() {
                public Iterator<? extends T> convert(Iterable<? extends T> elem) { 
                    return elem.iterator();
                }
            })
        );
    }
    
    /**
     * Creates a new iterator over the given sequence of iterators.
     * 
     * @param iterators 
     *  The <code>Iterator</code>s over whose elements elements this
     *  iterator will iterate.
     *  
     * @throws NullPointerException
     *  If <code>iterators</code> is a null pointer.
     */
    @SuppressWarnings("unchecked")
    public ChainingIterator(Iterator<? extends T>... iterators) 
    throws NullPointerException{
        this(Arrays.asList(iterators));
    }
    
    /**
     * Creates a new iterator over the given sequence of iterators.
     * 
     * @param iteratables
     *  The  sequence of iterators over whose elements elements this
     *  iterator will iterate.
     * 
     * @throws NullPointerException
     *  If <code>iterables</code> is a null pointer.
     */
    public ChainingIterator(Iterable<Iterator<? extends T>> iteratables) {
        this(iteratables.iterator());
    }
    
    /**
     * Creates a new iterator over the given sequence of iterators.
     * 
     * @param iterators
     *  The sequence of iterators over whose elements elements this
     *  iterator will iterate.
     *  
     * @throws NullPointerException
     *  If <code>iterators</code> is a null pointer.
     */
    public ChainingIterator(Iterator<Iterator<? extends T>> iterators) {
        if (iterators.hasNext())
            inner = iterators.next();
        else
            inner = EmptyIterator.getInstance();
        outer = iterators;
    }
 
    public boolean hasNext() {
        if (inner.hasNext()) return true;

        // This is NOT correct if the remove operation
        // is also implemented:
        while (outer.hasNext())
            if ((inner = outer.next()).hasNext())
                return true;
        return false;
    }

    public T next() {
        do {
            if (inner.hasNext()) return inner.next();
            inner = outer.next();
        } while (true);
    }

    /**
     * This operation is not supported by this iterator. The reason
     * is that we will not need it and supporting it would complicate 
     * the implementation of the other operations. 
     * 
     * @throws UnsupportedOperationException 
     *  This operation is not supported by this iterator.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
}