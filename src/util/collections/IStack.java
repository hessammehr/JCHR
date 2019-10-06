package util.collections;

import java.util.EmptyStackException;

import util.Resettable;

public interface IStack<T> extends Resettable {
	/**
     * Pushes an item onto the top of this stack. This has exactly 
     * the same effect as:
     * <blockquote><pre>add(item)</pre></blockquote>
     *
     * @param   item   the item to be pushed onto this stack.
     * @see     java.util.Stack#push
     */
    public void push(T item);

    /**
     * Removes the object at the top of this stack and returns that 
     * object as the value of this function. 
     *
     * @return     The object at the top of this stack (the last item 
     *             of the <tt>ArrayList</tt> object).
     * @exception  EmptyStackException  if this stack is empty.
     * @see        java.util.Stack#pop()
     */
    public T pop();

    /**
     * Looks at the object at the top of this stack without removing it 
     * from the stack. 
     *
     * @return     the object at the top of this stack (the last item 
     *             of the <tt>ArrayList</tt> object). 
     * @exception  EmptyStackException  if this stack is empty.
     * @see        java.util.Stack#peek()
     */
    public T peek() throws EmptyStackException;
    
    /**
     * Tests if this stack is empty.
     *
     * @return  <code>true</code> if and only if this stack contains 
     *          no items; <code>false</code> otherwise.
     * @see     java.util.Stack#empty()
     */
    public boolean empty();
}
