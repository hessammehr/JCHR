package util.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A <code>FilteredIterator</code> decorates another iterator, filtering
 * the elements returned by this decorated iterator using a user-defined
 * filter.
 * <br/>
 * The <code>remove</code> operation cannot be supported since we have to
 * look ahead in the <code>hasNext</code> method.
 * <br/>
 * The <code>hasToExclude</code> method will be called once
 * for each element in the original iteration (of course for as far 
 * the decorating iterator iterates), so you could keep an index
 * counter in your filter!
 * 
 * @author Peter Van Weert
 */
public class FilteredIterator<T> 
    extends Filtered<T, Iterator<? extends T>> 
    implements Iterator<T> {
    
    /**
     * Creates a new <code>FilteredIterator</code> decorates an iterator over the
     * given iterable, filtering the elements returned by this decorated iterator using the user-defined
     * filter.<br/>
     * The <code>remove</code> operation cannot be supported since we have to
     * look ahead in the <code>hasNext</code> method.
     * <br/>
     * The <code>hasToExclude</code> method will be called once
     * for each element in the original iteration (of course for as far 
     * the decorating iterator iterates), so you could keep an index
     * counter in your filter!
     * 
     * @param iterable
     *  The iterable we will be decorating an iterator of.
     * @param filter
     *  The user-defined filter used to filter the elements returned by the decorated
     *  iterator.
     *  
     * @see #FilteredIterator(Iterator, Filter)
     */
    public FilteredIterator(Iterable<? extends T> iterable, Filter<? super T> filter) {
        this(iterable.iterator(), filter);
    }
    
    /**
     * Creates a new <code>FilteredIterator</code> decorates the given iterator, 
     * filtering the elements returned by this decorated iterator using the user-defined
     * filter.<br/>
     * The <code>remove</code> operation cannot be supported since we have to
     * look ahead in the <code>hasNext</code> method.
     * <br/>
     * The <code>hasToExclude</code> method will be called once
     * for each element in the original iteration (of course for as far 
     * the decorating iterator iterates), so you could keep an index
     * counter in your filter!
     * 
     * @param decorated
     *  The decorated iterator.
     * @param filter
     *  The user-defined filter used to filter the elements returned by the decorated
     *  iterator.
     */
    public FilteredIterator(Iterator<? extends T> decorated, Filter<? super T> filter) {
        super(decorated, filter);
    }
    
    private boolean hasNext;
    private T next;
    
    public boolean hasNext() {
        // Needed when some ogre asks hasNext() several times in a row
        // (that is: before calling the next() method)
        if (hasNext) return true;
        // Isn't this nice Java code!!!
        while (
            (hasNext = getDecorated().hasNext()) 
            && 
            getFilter().exclude(
                next = getDecorated().next()
            )
        ){ /* NOP */ }
        return hasNext;
    }

    public T next() {
        if (hasNext || hasNext()) {
            hasNext = false;
            return next;
        }
        throw new NoSuchElementException();
    }
    
    /**
     * {@inheritDoc) 
     * 
     * @throws IllegalStateException 
     *  If the next method has not yet been called, or the remove 
     *  method has already been called after the last call to the
     *  next method, or the hasNext method has already been called
     *  after the last call to the next method.
     */
    public void remove() throws IllegalStateException {
        if (hasNext) throw new IllegalStateException(
            "The hasNext method has been called after the last" +
            "call to the next method."
        );
        getDecorated().remove();
    }
}
