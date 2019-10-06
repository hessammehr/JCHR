package util.iterator;

import java.util.ListIterator;

public interface ListIterable<T> extends Iterable<T> {
    
    /**
     * Returns a list iterator of the elements in a list (in proper
     * sequence).
     *
     * @return a list iterator of the elements in a list (in proper
     *  sequence).
     */
    ListIterator<T> listIterator();
    
    /**
     * Returns a list iterator of the elements in this list (in proper
     * sequence), starting at the specified position in this list.  The
     * specified index indicates the first element that would be returned by
     * an initial call to the <tt>next</tt> method.  An initial call to
     * the <tt>previous</tt> method would return the element with the
     * specified index minus one.
     *
     * @param index 
     *  Index of first element to be returned from the
     *  list iterator (by a call to the <tt>next</tt> method).
     * @return a list iterator of the elements in this list (in proper
     *  sequence), starting at the specified position in this list.
     * @throws IndexOutOfBoundsException 
     *  If the index is out of range (index &lt; 0 || index &gt; size())
     */
    ListIterator<T> listIterator(int index) throws IndexOutOfBoundsException;
}
