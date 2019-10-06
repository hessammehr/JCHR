package util.iterator;

import java.util.ListIterator;

public abstract class ListIteratorDecorator<T>
    extends IteratorDecorator<T>
    implements ListIterator<T> {

    public ListIteratorDecorator(ListIterator<T> decorated) {
        super(decorated);
    }
    
    @Override
    public ListIterator<T> getDecorated() {
        return (ListIterator<T>)super.getDecorated();
    }

    /**
     * Adds an element to <em>the decorated list</em>.
     * 
     * {@inheritDoc}
     *
     * @exception UnsupportedOperationException 
     *  If the <tt>add</tt> method is not supported by the decorated 
     *  list iterator.
     * @exception ClassCastException 
     *  If the class of the specified element prevents it from 
     *  being added to the decorated list.
     * @exception IllegalArgumentException 
     *  If some aspect of this element prevents it from being added 
     *  to the decorated list.
     */
    public void add(T e) {
        getDecorated().add(e);
    }

    public boolean hasPrevious() {
        return getDecorated().hasPrevious();
    }

    public int nextIndex() {
        return getDecorated().nextIndex();
    }

    public T previous() {
        return getDecorated().previous();
    }

    public int previousIndex() {
        return getDecorated().previousIndex();
    }

    /**
     * {@inheritDoc}
     * <em>The replacement is propagated to the decorated iterator.</em> 
     *
     * @exception UnsupportedOperationException 
     *  If the <tt>set</tt> operation is not supported by the decorated
     *  list iterator.
     * @exception ClassCastException 
     *  If the class of the specified element prevents it from being 
     *  added to the decorated list.
     * @exception IllegalArgumentException 
     *  If some aspect of the specified element prevents it from 
     *  being added to the decorated list.
     */
    public void set(T e) {
        getDecorated().set(e);
    }
    
    
}
