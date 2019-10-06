package util.iterator;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * An iterator over an array.
 * 
 * @author Peter Van Weert
 */
public class ArrayIterator<T> implements ListIterator<T> {
    
    protected T[] array;
    
    protected int index;
    
    public ArrayIterator(T... array) {
        if (array == null)
            throw new NullPointerException();
        
        this.array = array;
    }
    
    public boolean hasNext() {
        return (index < array.length);
    }

    public T next() {
        try {
            return array[index++];
        } catch (IndexOutOfBoundsException iobe) {
            index = array.length;
            throw new NoSuchElementException();
        }
    }

    /**
     * This operation is not supported by this iterator.
     * 
     * @throws UnsupportedOperationException
     */
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    public boolean hasPrevious() {
        return (index > 0);
    }

    public T previous() {
        try {
            return array[--index];
        } catch (IndexOutOfBoundsException iobe) {
            index = 0;
            throw new NoSuchElementException();
        }
    }

    public int nextIndex() {
        return index;
    }

    public int previousIndex() {
        return index-1;
    }

    public void set(T o) {
        array[index] = o;
    }

    /**
     * This operation is not supported by this iterator.
     * 
     * @throws UnsupportedOperationException
     */
    public void add(T o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}