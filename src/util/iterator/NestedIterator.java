package util.iterator;

import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * Provides an iterator over a collection of collections of elements.
 * Henceforth we will call this collection of collections <i>outer</i>
 * and its elements <i>inner</i> collections. The iterator will traverse
 * the inner collections in the order they are presented by the outer
 * iterator.
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
public class NestedIterator<T> implements Iterator<T> {

    /*
     * I know I should be using getters and setters here, but I
     * cannot see why the representation would ever change. Therefore
     * I have opted for performance here...
     */
    protected Iterator<? extends Iterable<? extends T>> outer;
    
    protected Iterator<? extends T> inner;

    /**
     * Creates a new iterator over the values of the given <code>Map</code>.
     * 
     * @param map The <code>Map</code> over whose elements elements this
     *  iterator will iterate.
     *  
     * @throws NullPointerException
     *  If <code>map</code> is a null pointer.
     */
    public NestedIterator(Map<?, ? extends Iterable<? extends T>> map) {
        this(map.values());
    }
 
    /**
     * Creates a new iterator over the values of the given collection.
     * 
     * @param iterable The collection over whose elements elements this
     *  iterator will iterate.
     *  
     * @throws NullPointerException
     *  If <code>coll</code> is a null pointer.
     */
    public NestedIterator(Iterable<? extends Iterable<? extends T>> iterable) {
        this(iterable.iterator());
    }
    
    public NestedIterator(Iterator<? extends Iterable<? extends T>> iterator) {
        outer = iterator;
        if (outer.hasNext())
            inner = outer.next().iterator();
        else
            inner = EmptyIterator.getInstance();
    }
    
    public boolean hasNext() {
        if (inner.hasNext()) return true;

        // This is NOT correct if the remove operation
        // is also implemented:
        while (outer.hasNext())
            if ((inner = outer.next().iterator()).hasNext())
                return true;
        return false;
    }

    public T next() {
        do {
            if (inner.hasNext()) return inner.next();
            inner = outer.next().iterator();
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