package util.iterator;

import java.util.ListIterator;

/**
 * An iterable whose elements are generated on the fly, 
 * possibly based on their index.
 * 
 * @author Peter Van Weert
 */
public abstract class GeneratingIterable<T> extends AbstractListIterable<T> {
    int nbElements;
 
    public GeneratingIterable() {
        this(Integer.MAX_VALUE);
    }
    public GeneratingIterable(int nbElements) {
    	if (nbElements < 0)
    		throw new IllegalArgumentException(nbElements + " < 0");
        setNbElements(nbElements);
    }
    
    protected void setNbElements(int nbElements) {
        this.nbElements = nbElements;
    }
    public int getNbElements() {
        return nbElements;
    }
    
    public ListIterator<T> listIterator(int index) throws IndexOutOfBoundsException {
        return new GeneratingIterator<T>(getNbElements(), index) {
            @Override
            protected T generate(int index) {
                return GeneratingIterable.this.generate(index);
            }
        };
    }
    
    protected abstract T generate(int index);
}
