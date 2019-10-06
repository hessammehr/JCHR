package util.iterator;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import util.Resettable;

public final class EmptyIterator<T> 
    implements Resettable, ListIterator<T>{
    
    private EmptyIterator() {/* SINGLETON */}
    
    // Since this is a singleton that is used *a lot* in many cases, we
    // instantiate it eagerly (why else would you load this class unless
    // you were inteding to use the getInstance() method anyway...)
    @SuppressWarnings("unchecked")
    private static EmptyIterator instance = new EmptyIterator();
    
    /*
     * Since this is a singleton, it cannot be type safe!
     */
    @SuppressWarnings("unchecked")
    public final static <T> EmptyIterator<T> getInstance() {
        return instance;
    }
    
    public boolean hasNext() {
        return false;
    }
    
    public T next() {
        throw new NoSuchElementException();
    }
    
    /**
     * This operation is not supported by this iterator.
     * 
     * @throws UnsupportedOperationException
     */
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * This operation is not supported by this iterator.
     * 
     * @throws UnsupportedOperationException
     */
    public void add(T t) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    public boolean hasPrevious() {
        return false;
    }
    
    public int nextIndex() {
        return 0;
    }
    
    public T previous() {
        throw new NoSuchElementException();
    }
    
    public int previousIndex() {
        return -1;
    }
    
    /**
     * This operation is not supported by this iterator.
     * 
     * @throws UnsupportedOperationException
     */
    public void set(T t) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    public void reset() {
        // NOP
    }
    
    /**
     * Throws CloneNotSupportedException.  This guarantees that 
     * the "singleton" status is preserved.
     *
     * @return (never returns)
     * @throws CloneNotSupportedException
     *  Cloning of a singleton is not allowed!
     */
    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
