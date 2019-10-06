package util.collections;

import java.util.ArrayList;
import java.util.EmptyStackException;

import util.exceptions.IllegalArgumentException;

/**
 * <p> 
 * The <code>Stack</code> class represents a last-in-first-out 
 * (LIFO) stack of objects. It extends class <tt>ArrayList</tt> with five 
 * operations that allow a list to be treated as a stack. The usual 
 * <tt>push</tt> and <tt>pop</tt> operations are provided, as well as a
 * method to <tt>peek</tt> at the top item on the stack, a method to test 
 * for whether the stack is <tt>empty</tt>, and a method to <tt>search</tt> 
 * the stack for an item and discover how far it is from the top.
 * </p>
 * <p>
 * <em>
 *  Unlike {@link java.util.Stack} (which extends <tt>Vector</tt>),
 *  the methods in this class are <b>not</b> synchronized. Other
 *  then that we have opted to adher to the specification of the
 *  former class where applicable.
 * </em>
 * </p>
 * <p>
 * The <tt>empty</tt>, <tt>peek</tt>, <tt>pop</tt> and <tt>push</tt>
 * operations run in constant time. All of the other operations
 * have the time complexities declared in the specification of <code>ArrayList</code>.
 * Also for more information on the other operations as well as the
 * <i>capacity</i> of a stack, we refer to the specification of
 * the super-class.
 * </p>
 *
 * @author Peter Van Weert
 * 
 * @see java.util.Stack
 * @see java.util.ArrayList
 */
public class Stack<T> extends ArrayList<T> implements IStack<T> {
    /**
     * Creates an empty <code>Stack</code>.
     */
    public Stack() {
        super();
    }
    
    /**
     * Creates an empty <code>Stack</code>, with given initial capacity.
     * 
     * @param   initialCapacity   the initial capacity of the list.
     * @exception IllegalArgumentException if the specified initial capacity
     *            is negative
     */
    public Stack(int initialCapacity) throws IllegalArgumentException {
        super(initialCapacity);
    }

    public void push(T item) {
        add(item);
    }

    public T pop() {
        try {
            return remove(size() - 1);
        } catch (IndexOutOfBoundsException iobe) {
            throw new EmptyStackException();
        }
    }

    public T peek() throws EmptyStackException {
        return peek(1);
    }
    
    /**
     * Looks at the object at the given 1-based position in this stack 
     * without removing it from the stack. To look at the top element
     * use a <code>depth</code> of 1. 
     *
     * @param      depth The 1-based position from the top of the stack where
     *             to look for an object. 
     * 
     * @return     the object at the given 1-based position in this stack
     * @exception  EmptyStackException  if this stack is empty.
     * @exception  IndexOutOfBoundsException  if the given index is out of bounds.
     * @see        #peek()
     */
    public T peek(int depth) throws EmptyStackException, IndexOutOfBoundsException {
        try {
            return get(size() - depth);
        } catch (IndexOutOfBoundsException iobe) {
            if (isEmpty()) throw new EmptyStackException();
            throw iobe;
        }
    }
    
    public boolean empty() {
        return isEmpty();
    }

    /**
     * Returns the 1-based position where an object is on this stack. 
     * If the object <tt>o</tt> occurs as an item in this stack, this 
     * method returns the distance from the top of the stack of the 
     * occurrence nearest the top of the stack; the topmost item on the 
     * stack is considered to be at distance <tt>1</tt>. The <tt>equals</tt> 
     * method is used to compare <tt>o</tt> to the 
     * items in this stack.
     *
     * @param   o   the desired object.
     * @return  the 1-based position from the top of the stack where 
     *          the object is located; the return value <code>-1</code>
     *          indicates that the object is not on the stack.
     * @see     java.util.Stack#search(java.lang.Object)
     */
    public int search(Object o) {
        int i = lastIndexOf(o);
        return (i >= 0)? size() - i : -1;
    }
    
    public void reset() {
        clear();
    }

    private static final long serialVersionUID = 1;
}
