package util.collections;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import util.Resettable;

/**
 * <p> 
 * The <code>Stack</code> class represents a last-in-first-out 
 * (LIFO) stack of objects. The usual {@link #push(Object)} and 
 * {@link #pop()} operations are provided, 
 * as well as a method to {@link #peek()} at the top item on the stack, 
 * and a method to test for whether the stack {@link #isEmpty()}. 
 * </p>
 * <p>
 * <em>
 *  Unlike {@link java.util.Stack} (which extends {@link Vector}),
 *  the methods in this class are <b>not</b> synchronized. Other
 *  then that we have opted to adher to the specification of the
 *  former class where applicable.
 * </em>
 * </p>
 * <p>
 * 	All operations run in constant time.
 * </p>
 *
 * @author Peter Van Weert
 * 
 * @see java.util.Stack
 * @see util.Stack
 */
public class LinkedStack<T> implements Iterable<T>, Resettable {
	protected Node<T> top;
	protected int size;
	
	protected final static class Node<V> {
		final Node<V> previous;
		final V value;
		public Node(final Node<V> previous, final V value) {
			super();
			this.previous = previous;
			this.value = value;
		}
	}
	
    /**
     * Pushes an item onto the top of this stack.
     *
     * @param   item   the item to be pushed onto this stack.
     * @return  the <code>item</code> argument.
     * @see     java.util.Stack#push(Object)
     */
    public void push(T item) {
        top = new Node<T>(top, item);
        size++;
    }

    /**
     * Removes the object at the top of this stack and returns that 
     * object as the value of this function. 
     *
     * @return     The object at the top of this stack (the last item 
     *             of the <tt>ArrayList</tt> object).
     * @exception  EmptyStackException  if this stack is empty.
     * @see        java.util.Stack#pop()
     */
    public T pop() {
    	if (size-- == 0) {
    		size = 0;
    		throw new EmptyStackException();
    	}
        T result = top.value;
        top = top.previous;
        return result;
    }

    /**
     * Looks at the object at the top of this stack without removing it 
     * from the stack. 
     *
     * @return     the object at the top of this stack (the last item 
     *             of the <tt>ArrayList</tt> object). 
     * @exception  EmptyStackException  if this stack is empty.
     * @see        java.util.Stack#peek()
     */
    public T peek() throws EmptyStackException {
    	if (size == 0)
    		throw new EmptyStackException();
        return top.value;
    }
    
    /**
     * Tests if this stack is empty.
     *
     * @return  <code>true</code> if and only if this stack contains 
     *          no items; <code>false</code> otherwise.
     * @see     java.util.Stack#empty()
     */
    public boolean isEmpty() {
        return size == 0;
    }

    public void reset() {
        top = null;
        size = 0;
    }
    
    protected final class NodeIterator implements Iterator<T> {
    	private Node<T> current = top;
    	public boolean hasNext() {
    		return current != null;
    	}
    	public T next() {
    		if (current == null) throw new NoSuchElementException();
    		T result = current.value;
    		current = current.previous;
    		return result;
    	}
    	public void remove() {
    		throw new UnsupportedOperationException();
    	}
    }
    public Iterator<T> iterator() {
    	return new NodeIterator();
    }
}