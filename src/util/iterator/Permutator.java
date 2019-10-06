package util.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import util.Arrays;
import util.Resettable;

/**
 * An iterator that generates all permutations of a given set of comparable 
 * elements, in no particular order. This iterator uses an incremental algorithm,
 * based upon an algorithm described in:<br/>
 * <pre>
 *  Kenneth H. Rosen, <i>Discrete Mathematics and Its Applications</i>, 
 *  2nd edition (NY: McGraw-Hill, 1991), pp. 282-284.
 * </pre>
 * The implementation itself is a polished version of the one made 
 * by Michael Gilleland 
 * (available at 
 * <a href="http://www.merriampark.com/perm.htm">http://www.merriampark.com/perm.htm</a>)
 * Note also the use of JCHR for the calculation of the factorial function ;-)
 * 
 * @author Peter Van Weert
 */
public class Permutator<T extends Comparable<? super T>> 
    implements Iterable<T[]>, Iterator<T[]>, Resettable {

    private T[] elements;

    private long numLeft;

    private long total;

    /**
     * <p>
     * Creates an iterator that incrementally generates all iteration orders 
     * of a given set of comparable elements, in no particular order. The given
     * elements have to be sorted from small to large according to the order
     * defined by their <code>compareTo</code>-methods. 
     * The number of elements is limited to 20 (inclusive) as 20! is the 
     * last factorial that can be represented in a Java long. 
     * We could use <code>BigInteger</code>s, but in practice this will 
     * never be useful...
     * </p>
     * <p>
     * If the argument given is an array, this array <em>will</em> be modified when
     * calling the <code>next()</code> method. No copies are ever made of the array.
     * This also means that the <code>next()</code> method will return an iterator over
     * this very array (after modifying it). Modifying this array externally is probably
     * <em>not</em> a good idea, but keeping a reference to it can be interesting
     * since it will contains the entire permutation after each call to <code>next()</code>.
     * Also: do not modify the elements of the array in any way that affects their
     * ordering, or the behavior of the permutator is no longer defined. 
     * </p>
     * 
     * @param elements
     *  Maximum 20 (inclusive) comparable elements, sorted from small to large.
     * @throws IllegalArgumentException
     *  If (strictly) more then 20 given elements are given, or the elements
     *  are not sorted from small to large (this test is quite cheap, so we 
     *  decided to incorporate it).
     * @throws NullPointerException
     *  If <code>elements</code> is a null-pointer.   
     */
    public Permutator(T... elements) 
    throws NullPointerException, IllegalArgumentException {
        if (elements.length > 20)
            throw new IllegalArgumentException(elements.length + " > 20");
        if (! Arrays.isSorted(elements))
            throw new IllegalArgumentException("unsorted element array");
            
        this.elements = elements;
        numLeft = total = getFactorial(elements.length);        
    }

    /**
     * Returns the number of permutations left.
     * 
     * @return The number of permutations left.
     */
    public long getNumberOfPermutationsLeft() {
        return numLeft;
    }

    /**
     * Returns the total number of permutations.
     * 
     * @return The total number of permutations.
     */
    public long getTotalNumberOfPermutations() {
        return total;
    }

    public boolean hasNext() {
        return numLeft > 0;
    }
    
    public Iterator<T[]> iterator() {
        return this;
    }

    private static long[] CACHE;
    private static long getFactorial(int n) {
    	if (n > 20) throw new IllegalArgumentException(n + " > 20");
    	if (CACHE == null) {
    		CACHE = new long[21];
    		long l = CACHE[0] = 1;
    		for (int i = 1; i <= 20; i++) CACHE[i] = l = l * i;
    	}
    	return CACHE[n];
    }

    /**
     * {@inheritDoc}
     *  
     * @throws ArrayIndexOutOfBoundsException
     *  When a reference to the initial array of elements is kept and 
     *  this array is modified, an <code>ArrayIndexOutOfBoundsException</code> 
     *  could occur. Also, when one of the arrays elements is changed
     *  in a way that affects the ordering relation such an exception
     *  could occur.
     */
    public T[] next() {
        if (numLeft == 0)
            throw new NoSuchElementException();
        
        if (numLeft != total) {
            // Find largest index i with elements[i] < elements[i+1]
    
            int i = elements.length - 2;
            while (elements[i].compareTo(elements[i+1]) > 0) i--;
    
            // Find index j such that elements[j] is smallest element
            // greater than elements[i] to the right of elements[i]
    
            int j = elements.length - 1;
            while (elements[i].compareTo(elements[j]) > 0) j--;
    
            // Swap elements[i] and elements[j]
            swap(i, j);
            
    
            // Put tail end of permutation after i'th position in increasing order
    
            i++;
            j = elements.length - 1;
            
            while (i < j) swap(i++, j--);
        }

        numLeft--;
        return elements;
    }
    
    /**
     * Resets the initial array to its original, sorted state. 
     * The complexity of this method is quite good: if the permutator 
     * has already delivered his final permutation, the order is O(n), 
     * which is optimal; if the permutator is still in the middle 
     * of permuting, the order is O(n log(n)), which might not be 
     * optimal (there might be a more clever algorithm then sorting 
     * here: you are all invited to find one!).
     */
    public void reset() {
        if (hasNext())
            java.util.Arrays.sort(elements);
        else
            Arrays.reverse(elements);
    }
    
    private void swap(int i, int j) {
        T temp = elements[i];
        elements[i] = elements[j];
        elements[j] = temp;
    }
    
    /**
     * This operation is not supported.
     * 
     * @throws UnsupportedOperationException
     */
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
