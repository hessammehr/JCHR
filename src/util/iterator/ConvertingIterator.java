package util.iterator;

import java.util.Iterator;

/**
 * An iterator that decorates another iterator, returning its elements 
 * in the same order but converted using a user-defined convertor.
 * 
 * @author Peter Van Weert
 */
public class ConvertingIterator<From, To> implements Iterator<To> {
    
    /**
     * A simple interface declaring a method that can be used to convert one
     * object from type <code>From</code> to an object from type <code>To</code>. 
     *  
     * @author Peter Van Weert
     */
    @SuppressWarnings("hiding")
    public interface Convertor<From, To> {
        /**
         * Receives an element, converts it and returns the converted result.
         * 
         * @param elem
         *  The element that has to be converted.
         * @return The converted element. 
         */
        To convert(From elem);
    }
    
    @SuppressWarnings("hiding")
    public static abstract class IndexedConvertor<From, To> implements Convertor<From, To> {
        private int index;
        
        public final To convert(From elem) {
            return convert(elem, index++);
        }
        
        public abstract To convert(From elem, int index);
    }
    
    /**
     * Creates a new <code>ConvertingIterator</code> that decorates a new iterator
     * over the given <code>Iterable</code>.
     * It returns the elements in the order of this decorated iterator, but it
     * converts them first using the given convertor. 
     * 
     * @param iterable
     *  The iterable we will take an iterator of and wrap it.
     * @param convertor
     *  The convertor that has to be used to convert between the decorated iterators 
     *  elements to the ones returned by this iterator.
     *  
     * @see #ConvertingIterator(Iterator, Convertor)
     */
    public ConvertingIterator(Iterable<? extends From> iterable, Convertor<? super From, ? extends To> convertor) {
        this(iterable.iterator(), convertor);
    }
    
    /**
     * Creates a new <code>ConvertingIterator</code> that decorates a given iterator.
     * It returns the elements in the order of this decorated iterator, but it
     * converts them first using the given convertor. 
     * 
     * @param wrapped
     *  The decorated iterator.
     * @param convertor
     *  The convertor that has to be used to convert between the decorated iterators 
     *  elements to the ones returned by this iterator.
     */
    public ConvertingIterator(Iterator<? extends From> wrapped, Convertor<? super From, ? extends To> convertor) {
        setDecorated(wrapped);
        setConvertor(convertor);
    }
    
    private Convertor<? super From, ? extends To> convertor;
    
    private Iterator<? extends From> decorated;
    
    public boolean hasNext() {
        return getDecorated().hasNext();
    }

    public To next() {
        return getConvertor().convert(getDecorated().next());
    }
    
    /**
     * {@inheritDoc}
     * 
     * @throws UnsupportedOperationException
     *  If the decorated iterator does not suppert the <code>remove</code>
     *  operation.
     */
    public void remove() throws UnsupportedOperationException {
        getDecorated().remove();
    }

    protected Iterator<? extends From> getDecorated() {
        return decorated;
    }
    protected void setDecorated(Iterator<? extends From> decorated) {
        this.decorated = decorated;
    }
    
    public Convertor<? super From, ? extends To> getConvertor() {
        return convertor;
    }
    protected void setConvertor(Convertor<? super From, ? extends To> convertor) {
        this.convertor = convertor;
    }
}
