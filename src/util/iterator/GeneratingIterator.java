package util.iterator;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * An iterator that generates its elements on the fly, 
 * possibly based on their index.
 * 
 * @author Peter Van Weert
 */
public abstract class GeneratingIterator<T> implements ListIterator<T> {
    int nextIndex, nbElements;
 
    public GeneratingIterator() {
        this(Integer.MAX_VALUE);
    }
    public GeneratingIterator(int nbElements) throws IllegalArgumentException {
        this(nbElements, 0);
    }
    public GeneratingIterator(int nbElements, int initialIndex)
    throws IllegalArgumentException, IndexOutOfBoundsException {
    	if (nbElements < 0)
    		throw new IllegalArgumentException(nbElements + " < 0");
    	if (initialIndex < 0 || initialIndex >= nbElements)
    		throw new util.exceptions.IndexOutOfBoundsException(initialIndex, nbElements);
    	
        setNbElements(nbElements);
        setNextIndex(initialIndex);
    }
    
    public void setNextIndex(int index) {
		this.nextIndex = index;
	}
    
    protected void setNbElements(int nbElements) {
        this.nbElements = nbElements;
    }
    public int getNbElements() {
        return nbElements;
    }
    
    public int nextIndex() {
        return nextIndex;
    }
    public int previousIndex() {
        return nextIndex() - 1;
    }
    
    public void add(T e) {
        throw new UnsupportedOperationException();
    }
    public void remove() {
        throw new UnsupportedOperationException();
    }
    public void set(T e) {
        throw new UnsupportedOperationException();
    }
    
    public boolean hasPrevious() {
        return previousIndex() >= 0;
    }
    public boolean hasNext() {
        return nextIndex() < getNbElements();
    }
    
    public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        final int nextIndex = nextIndex();
        setNextIndex(nextIndex + 1);
        return generate(nextIndex);
    }
    public T previous() {
        if (!hasPrevious()) throw new NoSuchElementException();
        final int previousIndex = previousIndex();
        setNextIndex(previousIndex);
        return generate(previousIndex);
    }
    
    protected abstract T generate(int index);
}
