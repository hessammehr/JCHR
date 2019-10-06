package util.collections;

import java.util.AbstractCollection;
import java.util.Collection;

/**
 * This class provides a skeletal implementation of the <tt>Collection</tt>
 * interface, to minimize the effort required to implement this interface.
 * <p>
 * 
 * To implement an unmodifiable collection, the programmer needs only to extend
 * this class and provide implementations for the <tt>iterator</tt> and
 * <tt>size</tt> methods. (The iterator returned by the <tt>iterator</tt>
 * method must only implement <tt>hasNext</tt> and <tt>next</tt>.)
 * <p>
 * 
 * The programmer should generally provide a void (no argument) and
 * <tt>Collection</tt> constructor, as per the recommendation in the
 * <tt>Collection</tt> interface specification.
 * <p>
 * 
 * The documentation for each non-abstract methods in this class describes its
 * implementation in detail. Each of these methods may be overridden if the
 * collection being implemented admits a more efficient implementation.
 * <p>
 * 
 * @author Peter Van Weert
 * @see java.util.AbstractCollection
 */
public abstract class AbstractUnmodifiableCollection<T> extends AbstractCollection<T> {

    /**
     * This method is not supported (unmodifiable collection).
     * 
     * @throws UnsupportedOperationException
     */
	@Override
    public final boolean add(T o) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported (unmodifiable collection).
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public final boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }
    
    
    /**
     * This method is not supported, unless the collection is
     * allready empty.
     * 
     * @throws UnsupportedOperationException
     *  If the collection is not empty.
     */
    @Override
    public final void clear() {
        if (!isEmpty())
            throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported (unmodifiable collection).
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public final boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported (unmodifiable collection).
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public final boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported (unmodifiable collection).
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public final boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }    
}