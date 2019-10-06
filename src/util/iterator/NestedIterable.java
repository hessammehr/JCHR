package util.iterator;

import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * Objects of this class are iterable over a collection of collections of elements.
 * Henceforth we will call this collection of collections <i>outer</i>
 * and its elements <i>inner</i> collections. The returned iterators will traverse
 * the inner collections in the order they are presented by the outer
 * iterator.
 * </p>
 * <p>
 * This iterators will inherit the <i>fail-fast</i> behaviour of the nested
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
 * @see NestedIterable
 * @author Peter Van Weert
 */
public class NestedIterable<T> implements Iterable<T> {

    /*
     * I know I should be using getters and setters here, but I
     * cannot see why the representation would ever change. Therefore
     * I have opted for performance here...
     */
    protected Iterable<? extends Iterable<? extends T>> iterable;
    
    /**
     * Creates a new iterable over the values of the given <code>Map</code>.
     * 
     * @param map The <code>Map</code> over whose elements elements the
     *  returned iterators will iterate.
     *  
     * @throws NullPointerException
     *  If <code>map</code> is a null pointer.
     */
    public NestedIterable(Map<?, ? extends Iterable<? extends T>> map) {
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
    public NestedIterable(Iterable<? extends Iterable<? extends T>> iterable) {
        this.iterable = iterable;
    }
    
    public Iterator<T> iterator() {
        return new NestedIterator<T>(iterable);
    }
}