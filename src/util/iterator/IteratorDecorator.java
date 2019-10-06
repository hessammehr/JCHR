package util.iterator;

import java.util.Iterator;

public abstract class IteratorDecorator<T> implements Iterator<T> {

    public IteratorDecorator(Iterator<T> decorated) {
        setDecorated(decorated);
    }
    
    private Iterator<T> decorated;
    
    public Iterator<T> getDecorated() {
        return decorated;
    }
    protected void setDecorated(Iterator<T> decorated) {
        this.decorated = decorated;
    }
    
    public boolean hasNext() {
        return getDecorated().hasNext();
    }
    
    public T next() {
        return getDecorated().next();
    }
    
    /**
     * {@inheritDoc}
     * 
     * @throws UnsupportedOperationException
     *  If the decorated iterator does not suppert the <code>remove</code>
     *  operation.
     */
    public void remove() {
        getDecorated().remove();
    }
}
