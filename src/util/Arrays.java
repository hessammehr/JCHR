package util;

import static java.lang.Math.abs;
import static java.lang.reflect.Array.newInstance;
import static java.util.Arrays.binarySearch;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import util.iterator.ArrayIterator;


/**
 * Some often (and less often) needed methods when working with arrays, 
 * which did not make it into <code>java.util.Arrays</code>. Of course
 * you can always come up with more of these. We will keep adding
 * them as soon as we need them along the road.
 *  
 * @author Peter Van Weert
 * 
 * @see java.util.Arrays
 */
public final class Arrays {
    private Arrays() {/* non-instantiatable utility class */}
    
    /**
     * <p>
     * Returns the first index of <code>elem</code> in <code>array</code>.
     * Elements are compared using their equals-method. 
     * Note that this method is intended to be used when you are sure
     * the element is in the array, or at least most of the time.
     * Otherwise the (a bit less efficient) method {@link #indexOf(T[], T) }
     * should probably be used.
     * </p>
     * <p>
     * Note that when you need to call this method many times, it
     * might be e.g. better to sort the array and use binary search
     * (see {@link java.util.Arrays}).
     * </p>
     * 
     * @param array
     *  The array that has to be scanned.
     * @param elem
     *  The element that has to be searched.
     * @return The first index of <code>elem</code> in <code>array</code>. 
     *   
     * @throws ArrayIndexOutOfBoundsException
     *  If the element is not contained in the array.
     * @throws NullPointerException
     *  If <code>array</code> is <code>null</code>.
     *  
     * @see java.lang.Object#equals(java.lang.Object)
     * @see #indexOf(T[], T)
     * @see #identityFirstIndexOf(T[], T)
     * @see java.util.Arrays
     */
    public final static <T> int firstIndexOf(T[] array, T elem) 
    throws NullPointerException, ArrayIndexOutOfBoundsException {
        if (elem == null) return identityFirstIndexOf(array, null);
        int i = -1; do { if (elem.equals(array[++i])) return i; } while (true);
    }
    
    /**
     * <p>
     * Returns the first index of <code>elem</code> in <code>array</code>.
     * Elements are compared by comparing their object identities (through <code>==</code>). 
     * Note that this method is intended to be used when you are sure
     * the element is in the array, or at least most of the time.
     * Otherwise the (a bit less efficient) method {@link #indexOf(T[], T) }
     * should probably be used.
     * </p>
     * <p>
     * Note that when you need to call this method many times, it
     * might be e.g. better to sort the array and use binary search
     * (see {@link java.util.Arrays }).
     * </p>
     * 
     * @param array
     *  The array that has to be scanned.
     * @param elem
     *  The element that has to be searched.
     * @return The first index of <code>elem</code> in <code>array</code>. 
     *   
     * @throws ArrayIndexOutOfBoundsException
     *  If the element is not contained in the array.
     * @throws NullPointerException
     *  If <code>array</code> is <code>null</code>.
     *  
     * @see #identityIndexOf(T[], T)
     * @see #firstIndexOf(T[], T)
     * @see java.util.Arrays
     */
    public final static <T> int identityFirstIndexOf(T[] array, T elem)
    throws NullPointerException, ArrayIndexOutOfBoundsException {
        int i = -1; do { if (array[++i] == elem) return i; } while (true);
    }
    
    /**
     * <p>
     * Returns the first index of <code>elem</code> in <code>array</code>.
     * Elements are compared using their equals-method. If the element
     * is not found, -1 is returned.
     * </p>
     * <p>
     * Note that when you need to call this method many times, it
     * might be e.g. better to sort the array and use binary search
     * (see {@link java.util.Arrays }).
     * </p>
     * 
     * @param array
     *  The array that has to be scanned.
     * @param elem
     *  The element that has to be searched.
     * @return The first index of <code>elem</code> in <code>array</code>,
     *  or -1 if not found.
     *  
     * @throws NullPointerException
     *  If <code>array</code> is <code>null</code>.
     *  
     * @see java.lang.Object#equals(java.lang.Object)
     * @see #firstIndexOf(T[], T)
     * @see #identityIndexOf(T[], T)
     * @see java.util.Arrays
     */
    public final static <T> int indexOf(T[] array, T elem)
    throws NullPointerException {
        if (elem == null) return identityIndexOf(array, elem);
        for (int i = 0; i < array.length; i++) 
            if (elem.equals(array[i])) return i;
        return -1;
    }
    
    /**
     * <p>
     * Returns the first index of <code>elem</code> in <code>array</code>.
     * Elements are compared by comparing their object identities (through <code>==</code>). 
     * </p>
     * <p>
     * Note that when you need to call this method many times, it
     * might be e.g. better to sort the array and use binary search
     * (see {@link java.util.Arrays }).
     * </p>
     * 
     * @param array
     *  The array that has to be scanned.
     * @param elem
     *  The element that has to be searched.
     * @return The first index of <code>elem</code> in <code>array</code>,
     *  or -1 if not found. 
     *   
     * @throws NullPointerException
     *  If <code>array</code> is <code>null</code>.
     *  
     * @see #identityIndexOf(T[], T)
     * @see #firstIndexOf(T[], T)
     * @see java.util.Arrays
     */
    public final static <T> int identityIndexOf(T[] array, T elem) 
    throws NullPointerException {
        for (int i = 0; i < array.length; i++)
            if (array[i] == elem) return i;
        return -1;
    }
    
    /**
     * <p>
     * Checks whether the element <code>elem</code> is in <code>array</code>.
     * Elements are compared using the <code>equals</code> method. 
     * </p>
     * <p>
     * Note that when you need to call this method many times, it
     * might be e.g. better to sort the array and use binary search
     * (see {@link java.util.Arrays }).
     * </p>
     * 
     * @param array
     *  The array that has to be scanned.
     * @param elem
     *  The element that has to be searched.
     * @return True if the element is found in the list, false otherwise.
     *   
     * @throws NullPointerException
     *  If <code>array</code> is <code>null</code>.
     *  
     * @see java.util.Arrays
     * @see Object#equals(java.lang.Object)
     */
    public final static <T> boolean contains(T[] array, T elem) {
        if (elem == null) return identityContains(array, null);
        for (T t : array) if (elem.equals(t)) return true;
        return false;
    }
    
    /**
     * <p>
     * Checks whether the given <code>array</code> contains any double
     * elements. Elements are compared with their <code>equals</code> method.
     * Two <code>null</code> are also counted as a double.
     * The amortized complexity of the algorithm is O(n), assuming the 
     * <code>hashCode</code> of the objects supplied is implemented properly
     * and in constant time (the worst case complexity is O(n^2), this is if the 
     * constant time hash funtion maps all objects to the same bucket).
     * </p>
     * <p> 
     * If computing the hash of an object is expensive (e.g. for a collection),
     * and comparing objects (e.g. comparing the first element of the list) is
     * cheap, the <code>containsDoublesComp</code> methods might be more
     * efficient.
     * </p>
     * 
     * @param array
     *  The array that to be scanned for doubles.
     * @return True if a double is found in the list, false otherwise.
     *   
     * @throws NullPointerException
     *  If <code>array</code> is <code>null</code>.
     *
     * @see #containsDoublesComp(Comparable[])
     * @see #containsDoublesComp(Object[], Comparator))
     */
    public final static <T> boolean containsDoublesHash(T... array) {
        return containsDoubles(new HashMap<T,Object>(array.length), array); 
    }
    
    /**
     * <p>
     * Checks whether the given <code>array</code> contains any double
     * elements. Elements are compared with <code>==</code>
     * (reference-equality).
     * The amortized complexity of the algorithm is O(n).
     * </p>

     * @param array
     *  The array that to be scanned for doubles.
     * @return True if a double is found in the list, false otherwise.
     *   
     * @throws NullPointerException
     *  If <code>array</code> is <code>null</code>.
     *
     * @see IdentityHashMap
     * @see System#identityHashCode(Object)
     */
    public final static <T> boolean identityContainsDoubles(T... array) {
        return containsDoubles(new IdentityHashMap<T,Object>(array.length), array); 
    }
    
    /**
     * <p>
     * Checks whether the given <code>array</code> contains any double
     * elements. Elements are compared with their {@link Comparable#compareTo(Object)} 
     * method. Two <code>null</code> are also counted as a double. 
     * The algorithm used has a complexity of O(n*log(n)), if comparing
     * of the objects takes constant time.
     * </p>
     * <p>
     * In many cases the algorithm implemented by {@link #containsDoublesHash(Object[])}
     * will be more efficient.
     * </p>
     * 
     * @param array
     *  The array that to be scanned for doubles.
     * @param algo
     *  The algorithm to use when looking for doubles.
     * @return True if a double is found in the list, false otherwise.
     *   
     * @throws NullPointerException
     *  If <code>array</code> is <code>null</code>.
     *  
     * @see #containsDoublesHash(Object[])
     */
    public final static <T extends Comparable<? super T>> boolean containsDoublesComp(T... array) {
        return containsDoubles(new TreeMap<T,Object>(), array);
    }
    
    /**
     * <p>
     * Checks whether the given <code>array</code> contains any double
     * elements. Elements are compared using the given <code>Comparator</code> 
     * object. Two <code>null</code> are also counted as a double.
     * The algorithm used has a complexity of O(n*log(n)), if comparing
     * of the objects takes constant time.
     * </p>
     * <p>
     * In many cases the algorithm implemented by {@link #containsDoublesHash(Object[])}
     * will be more efficient.
     * </p>
     * 
     * @param array
     *  The array that to be scanned for doubles.
     * @param comparator
     *  The {@link Comparator} to be used to compare the objects of the array.
     * @return True if a double is found in the list, false otherwise.
     *   
     * @throws NullPointerException
     *  If <code>array</code> or <code>comparator</code> is <code>null</code>.
     *  
     * @see #containsDoublesHash(Object[])
     */
    public final static <T> boolean containsDoublesComp(Comparator<? super T> comparator, T... array) {
        if (comparator == null) throw new NullPointerException();
        return containsDoubles(new TreeMap<T,Object>(comparator), array);
    }
    
    /**
     * <p>
     * Checks whether the given, sorted <code>array</code> contains any 
     * double elements. Elements are compared using the <code>equals</code>
     * method. The algorithm used has a complexity of O(n).
     * Note that the result will only be correct if the given array
     * is in fact sorted!
     * </p>
     * 
     * @param array
     *  The <em>sorted</em> array that to be scanned for doubles.
     * @return True if a double is found in the array, false otherwise.
     *   
     * @throws NullPointerException
     *  If <code>array</code> is <code>null</code>.
     */
    public final static <T extends Comparable<? super T>> boolean containsDoublesSorted(T... array) {
        for (int i = 0; i < array.length-1; i++) 
            if (array[i].equals(array[i+1])) return true;
        return false;
    }
    
    private final static <T> boolean containsDoubles(Map<T,Object> map, T... array) {
    	final Object PRESENT = new Object();
        for (T elem : array) if (map.put(elem, PRESENT) == PRESENT) return true;
        return false;
    }
    
    /**
     * <p>
     * Checks whether the element <code>elem</code> is in <code>array</code>.
     * Elements are compared by comparing their object identities (through <code>==</code>). 
     * </p>
     * <p>
     * Note that when you need to call this method many times, it
     * might be e.g. better to sort the array and use binary search
     * (see {@link java.util.Arrays }).
     * </p>
     * 
     * @param array
     *  The array that has to be scanned.
     * @param elem
     *  The element that has to be searched.
     * @return True if the element is found in the list, false otherwise.
     *   
     * @throws NullPointerException
     *  If <code>array</code> is <code>null</code>.
     *  
     * @see java.util.Arrays
     */
    public final static <T> boolean identityContains(T[] array, T elem) {
        for (T t : array) if (t == elem) return true;
        return false;
    }
    
    /**
     * Reverses the elements of an array.
     * 
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static <T> void reverse(T[] array) {
        reverse(array, 0, array.length);
    }
    
    /**
     * Reverses the elements of an array.
     * 
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(boolean[] array) {
        reverse(array, 0, array.length);
    }
    
    /**
     * Reverses the elements of an array.
     * 
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(char[] array) {
        reverse(array, 0, array.length);
    }
    
    /**
     * Reverses the elements of an array.
     * 
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(byte[] array) {
        reverse(array, 0, array.length);
    }
    
    /**
     * Reverses the elements of an array.
     * 
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(short[] array) {
        reverse(array, 0, array.length);
    }
    
    /**
     * Reverses the elements of an array.
     * 
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(int[] array) {
        reverse(array, 0, array.length);
    }
    
    /**
     * Reverses the elements of an array.
     * 
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(long[] array) {
        reverse(array, 0, array.length);
    }
    
    /**
     * Reverses the elements of an array.
     * 
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(float[] array) {
        reverse(array, 0, array.length);
    }
    
    /**
     * Reverses the elements of an array.
     * 
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(double[] array) {
        reverse(array, 0, array.length);
    }
    
    /**
     * Reverses a subset of the elements of an array.
     * 
     * @param from
     *  The array index indicating the start of the sub-array 
     *  to be reversed (inclusive).
     * @param to
     *  The array index indicating the end of the sub-array 
     *  to be reversed (exclusive).
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static <T> void reverse(T[] array, int from, int to) {
        int i = from, j = to-1;
        while (i < j) swap(array, i++, j--);
    }
    
    /**
     * Reverses a subset of the elements of an array.
     * 
     * @param from
     *  The array index indicating the start of the sub-array 
     *  to be reversed (inclusive).
     * @param to
     *  The array index indicating the end of the sub-array 
     *  to be reversed (exclusive).
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(boolean[] array, int from, int to) {
        int i = from, j = to-1;
        while (i < j) swap(array, i++, j--);
    }
    
    /**
     * Reverses a subset of the elements of an array.
     * 
     * @param from
     *  The array index indicating the start of the sub-array 
     *  to be reversed (inclusive).
     * @param to
     *  The array index indicating the end of the sub-array 
     *  to be reversed (exclusive).
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(char[] array, int from, int to) {
        int i = from, j = to-1;
        while (i < j) swap(array, i++, j--);
    }
    
    /**
     * Reverses a subset of the elements of an array.
     * 
     * @param from
     *  The array index indicating the start of the sub-array 
     *  to be reversed (inclusive).
     * @param to
     *  The array index indicating the end of the sub-array 
     *  to be reversed (exclusive).
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(byte[] array, int from, int to) {
        int i = from, j = to-1;
        while (i < j) swap(array, i++, j--);
    }
    
    /**
     * Reverses a subset of the elements of an array.
     * 
     * @param from
     *  The array index indicating the start of the sub-array 
     *  to be reversed (inclusive).
     * @param to
     *  The array index indicating the end of the sub-array 
     *  to be reversed (exclusive).
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(short[] array, int from, int to) {
        int i = from, j = to-1;
        while (i < j) swap(array, i++, j--);
    }
    
    /**
     * Reverses a subset of the elements of an array.
     * 
     * @param from
     *  The array index indicating the start of the sub-array 
     *  to be reversed (inclusive).
     * @param to
     *  The array index indicating the end of the sub-array 
     *  to be reversed (exclusive).
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(int[] array, int from, int to) {
        int i = from, j = to-1;
        while (i < j) swap(array, i++, j--);
    }
    
    /**
     * Reverses a subset of the elements of an array.
     * 
     * @param from
     *  The array index indicating the start of the sub-array 
     *  to be reversed (inclusive).
     * @param to
     *  The array index indicating the end of the sub-array 
     *  to be reversed (exclusive).
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(long[] array, int from, int to) {
        int i = from, j = to-1;
        while (i < j) swap(array, i++, j--);
    }
    
    /**
     * Reverses a subset of the elements of an array.
     * 
     * @param from
     *  The array index indicating the start of the sub-array 
     *  to be reversed (inclusive).
     * @param to
     *  The array index indicating the end of the sub-array 
     *  to be reversed (exclusive).
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(float[] array, int from, int to) {
        int i = from, j = to-1;
        while (i < j) swap(array, i++, j--);
    }
    
    /**
     * Reverses a subset of the elements of an array.
     * 
     * @param from
     *  The array index indicating the start of the sub-array 
     *  to be reversed (inclusive).
     * @param to
     *  The array index indicating the end of the sub-array 
     *  to be reversed (exclusive).
     * @param array
     *  The array whose elements are to be reversed.
     */
    public final static void reverse(double[] array, int from, int to) {
        int i = from, j = to-1;
        while (i < j) swap(array, i++, j--);
    }
    
    /**
     * Swaps two elements at given indices of a given array.
     * 
     * @param array
     *  The array of which we want to swap two elements.
     * @param i
     *  The index of the first element to be swapped.
     * @param j
     *  The index of the second element to be swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException
     *  If either <code>i</code> or <code>j</code> is out of bounds.
     * @throws NullPointerException
     *  If the given array is <code>null</code>.
     * 
     * @return The array whose elements have been swapped.
     */
    public final static <T> void swap(T[] array, int i, int j) 
    throws ArrayIndexOutOfBoundsException, NullPointerException {
        T t = array[i];
        array[i] = array[j];
        array[j] = t;
    }
    
    /**
     * Swaps two elements at given indices of a given array.
     * 
     * @param array
     *  The array of which we want to swap two elements.
     * @param i
     *  The index of the first element to be swapped.
     * @param j
     *  The index of the second element to be swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException
     *  If either <code>i</code> or <code>j</code> is out of bounds.
     * @throws NullPointerException
     *  If the given array is <code>null</code>.
     * 
     * @return The array whose elements have been swapped.
     */
    public final static void swap(boolean[] array, int i, int j) 
    throws ArrayIndexOutOfBoundsException, NullPointerException {
        boolean t = array[i];
        array[i] = array[j];
        array[j] = t;
    }
    
    /**
     * Swaps two elements at given indices of a given array.
     * 
     * @param array
     *  The array of which we want to swap two elements.
     * @param i
     *  The index of the first element to be swapped.
     * @param j
     *  The index of the second element to be swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException
     *  If either <code>i</code> or <code>j</code> is out of bounds.
     * @throws NullPointerException
     *  If the given array is <code>null</code>.
     * 
     * @return The array whose elements have been swapped.
     */
    public final static void swap(char[] array, int i, int j) 
    throws ArrayIndexOutOfBoundsException, NullPointerException {
        char t = array[i];
        array[i] = array[j];
        array[j] = t;
    }
    
    /**
     * Swaps two elements at given indices of a given array.
     * 
     * @param array
     *  The array of which we want to swap two elements.
     * @param i
     *  The index of the first element to be swapped.
     * @param j
     *  The index of the second element to be swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException
     *  If either <code>i</code> or <code>j</code> is out of bounds.
     * @throws NullPointerException
     *  If the given array is <code>null</code>.
     * 
     * @return The array whose elements have been swapped.
     */
    public final static void swap(byte[] array, int i, int j) 
    throws ArrayIndexOutOfBoundsException, NullPointerException {
        byte t = array[i];
        array[i] = array[j];
        array[j] = t;
    }
    
    /**
     * Swaps two elements at given indices of a given array.
     * 
     * @param array
     *  The array of which we want to swap two elements.
     * @param i
     *  The index of the first element to be swapped.
     * @param j
     *  The index of the second element to be swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException
     *  If either <code>i</code> or <code>j</code> is out of bounds.
     * @throws NullPointerException
     *  If the given array is <code>null</code>.
     * 
     * @return The array whose elements have been swapped.
     */
    public final static void swap(short[] array, int i, int j) 
    throws ArrayIndexOutOfBoundsException, NullPointerException {
        short t = array[i];
        array[i] = array[j];
        array[j] = t;
    }
    
    /**
     * Swaps two elements at given indices of a given array.
     * 
     * @param array
     *  The array of which we want to swap two elements.
     * @param i
     *  The index of the first element to be swapped.
     * @param j
     *  The index of the second element to be swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException
     *  If either <code>i</code> or <code>j</code> is out of bounds.
     * @throws NullPointerException
     *  If the given array is <code>null</code>.
     * 
     * @return The array whose elements have been swapped.
     */
    public final static void swap(int[] array, int i, int j) 
    throws ArrayIndexOutOfBoundsException, NullPointerException {
        int t = array[i];
        array[i] = array[j];
        array[j] = t;
    }
    
    /**
     * Swaps two elements at given indices of a given array.
     * 
     * @param array
     *  The array of which we want to swap two elements.
     * @param i
     *  The index of the first element to be swapped.
     * @param j
     *  The index of the second element to be swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException
     *  If either <code>i</code> or <code>j</code> is out of bounds.
     * @throws NullPointerException
     *  If the given array is <code>null</code>.
     * 
     * @return The array whose elements have been swapped.
     */
    public final static void swap(long[] array, int i, int j) 
    throws ArrayIndexOutOfBoundsException, NullPointerException {
        long t = array[i];
        array[i] = array[j];
        array[j] = t;
    }
    
    /**
     * Swaps two elements at given indices of a given array.
     * 
     * @param array
     *  The array of which we want to swap two elements.
     * @param i
     *  The index of the first element to be swapped.
     * @param j
     *  The index of the second element to be swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException
     *  If either <code>i</code> or <code>j</code> is out of bounds.
     * @throws NullPointerException
     *  If the given array is <code>null</code>.
     * 
     * @return The array whose elements have been swapped.
     */
    public final static void swap(float[] array, int i, int j) 
    throws ArrayIndexOutOfBoundsException, NullPointerException {
        float t = array[i];
        array[i] = array[j];
        array[j] = t;
    }
    
    /**
     * Swaps two elements at given indices of a given array.
     * 
     * @param array
     *  The array of which we want to swap two elements.
     * @param i
     *  The index of the first element to be swapped.
     * @param j
     *  The index of the second element to be swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException
     *  If either <code>i</code> or <code>j</code> is out of bounds.
     * @throws NullPointerException
     *  If the given array is <code>null</code>.
     * 
     * @return The array whose elements have been swapped.
     */
    public final static void swap(double[] array, int i, int j) 
    throws ArrayIndexOutOfBoundsException, NullPointerException {
        double t = array[i];
        array[i] = array[j];
        array[j] = t;
    }
    
    private final static Random RANDOM = new Random();
    
    /**
     * <p>
     * Randomly permute the specified array using a default source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If the given parameter is <code>null</code>.
     */
    public static boolean[] shuffle(boolean... array) {
        return shuffle(RANDOM, array);
    }
    
    /**
     * <p>
     * Randomly permute the specified array using the specified source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @param  rnd 
     *  The source of randomness to use to shuffle the list.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If either one of the parameters is <code>null</code>.
     */
    public static boolean[] shuffle(Random rnd, boolean... array) {
        for (int i = array.length; i>1; i--)
            swap(array, i-1, rnd.nextInt(i));
        return array;
    }
    
    /**
     * <p>
     * Randomly permute the specified array using a default source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If the given parameter is <code>null</code>.
     */
    public static byte[] shuffle(byte... array) {
        return shuffle(RANDOM, array);
    }
    
    /**
     * <p>
     * Randomly permute the specified array using the specified source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @param  rnd 
     *  The source of randomness to use to shuffle the list.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If either one of the parameters is <code>null</code>.
     */
    public static byte[] shuffle(Random rnd, byte... array) {
        for (int i = array.length; i>1; i--)
            swap(array, i-1, rnd.nextInt(i));
        return array;
    }
    
    /**
     * <p>
     * Randomly permute the specified array using a default source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If the given parameter is <code>null</code>.
     */
    public static short[] shuffle(short... array) {
        return shuffle(RANDOM, array);
    }
    
    /**
     * <p>
     * Randomly permute the specified array using the specified source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @param  rnd 
     *  The source of randomness to use to shuffle the list.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If either one of the parameters is <code>null</code>.
     */
    public static short[] shuffle(Random rnd, short... array) {
        for (int i = array.length; i>1; i--)
            swap(array, i-1, rnd.nextInt(i));
        return array;
    }
    
    /**
     * <p>
     * Randomly permute the specified array using a default source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If the given parameter is <code>null</code>.
     */
    public static char[] shuffle(char... array) {
        return shuffle(RANDOM, array);
    }
    
    /**
     * <p>
     * Randomly permute the specified array using the specified source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @param  rnd 
     *  The source of randomness to use to shuffle the list.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If either one of the parameters is <code>null</code>.
     */
    public static char[] shuffle(Random rnd, char... array) {
        for (int i = array.length; i>1; i--)
            swap(array, i-1, rnd.nextInt(i));
        return array;
    }
    
    /**
     * <p>
     * Randomly permute the specified array using a default source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If the given parameter is <code>null</code>.
     */
    public static int[] shuffle(int... array) {
        return shuffle(RANDOM, array);
    }
    
    /**
     * <p>
     * Randomly permute the specified array using the specified source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @param  rnd 
     *  The source of randomness to use to shuffle the list.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If either one of the parameters is <code>null</code>.
     */
    public static int[] shuffle(Random rnd, int... array) {
        for (int i = array.length; i>1; i--)
            swap(array, i-1, rnd.nextInt(i));
        return array;
    }
    
    /**
     * <p>
     * Randomly permute the specified array using a default source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If the given parameter is <code>null</code>.
     */
    public static long[] shuffle(long... array) {
        return shuffle(RANDOM, array);
    }
    
    /**
     * <p>
     * Randomly permute the specified array using the specified source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @param  rnd 
     *  The source of randomness to use to shuffle the list.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If either one of the parameters is <code>null</code>.
     */
    public static long[] shuffle(Random rnd, long... array) {
        for (int i = array.length; i>1; i--)
            swap(array, i-1, rnd.nextInt(i));
        return array;
    }
    
    /**
     * <p>
     * Randomly permute the specified array using a default source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If the given parameter is <code>null</code>.
     */
    public static float[] shuffle(float... array) {
        return shuffle(RANDOM, array);
    }
    
    /**
     * <p>
     * Randomly permute the specified array using the specified source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @param  rnd 
     *  The source of randomness to use to shuffle the list.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If either one of the parameters is <code>null</code>.
     */
    public static float[] shuffle(Random rnd, float... array) {
        for (int i = array.length; i>1; i--)
            swap(array, i-1, rnd.nextInt(i));
        return array;
    }
    
    /**
     * <p>
     * Randomly permute the specified array using a default source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If the given parameter is <code>null</code>.
     */
    public static double[] shuffle(double... array) {
        return shuffle(RANDOM, array);
    }
    
    /**
     * <p>
     * Randomly permute the specified array using the specified source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @param  rnd 
     *  The source of randomness to use to shuffle the list.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If either one of the parameters is <code>null</code>.
     */
    public static double[] shuffle(Random rnd, double... array) {
        for (int i = array.length; i>1; i--)
            swap(array, i-1, rnd.nextInt(i));
        return array;
    }
    
    /**
     * <p>
     * Randomly permute the specified array using a default source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If the given parameter is <code>null</code>.
     */
    public static <T> T[] shuffle(T... array) {
        return shuffle(RANDOM, array);
    }
    
    /**
     * <p>
     * Randomly permute the specified array using the specified source of
     * randomness.  All permutations occur with equal likelihood
     * assuming that the source of randomness is fair.
     * </p>
     * <p>
     * This implementation traverses the array backwards, from the last element
     * up to the second, repeatedly swapping a randomly selected element into
     * the "current position".  Elements are randomly selected from the
     * portion of the list that runs from the first element to the current
     * position, inclusive.
     * </p>
     * <p>
     * This method runs in linear time.
     * </p>
     * 
     * @param  array
     *  The array to be shuffled.
     * @param  rnd 
     *  The source of randomness to use to shuffle the list.
     * @return The <i>same</i> array, but shuffled.
     * @throws NullPointerException 
     *  If either one of the parameters is <code>null</code>.
     */
    public static <T> T[] shuffle(Random rnd, T... array) {
        for (int i = array.length; i>1; i--)
            swap(array, i-1, rnd.nextInt(i));
        return array;
    }
    
    /**
     * Returns the maximal element in the given array.
     * 
     * @param elements
     *  A number of of non-null <code>Comparable</code> elements.
     * @throws IndexOutOfBoundsException
     *  If the no elements are given.
     * @throws NullPointerException
     *  If the given elements contain a <code>null</code> reference,
     *  or is a <code>null</code> of type array of <code>T</code>.
     */
    public final static <T extends Comparable<? super T>> T max(T... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        T max = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (elements[i].compareTo(max) > 0) max = elements[i];
        return max;
    }
    
    /**
     * Returns the minimal element in the given array.
     * 
     * @param elements
     *  A number of of non-null <code>Comparable</code> elements.
     * @throws IndexOutOfBoundsException
     *  If the no elements are given (is an empty array).
     * @throws NullPointerException
     *  If the given elements contain a <code>null</code> reference,
     *  or is a <code>null</code> of type array of <code>T</code>.
     */
    public final static <T extends Comparable<? super T>> T min(T... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        T min = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (elements[i].compareTo(min) < 0) min = elements[i];
        return min;
    }
    
    /**
     * Returns the maximal element in the given array.
     * Elements are compared using the given comparator.
     * 
     * @param elements
     *  A number of non-null elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If no elements are given.
     * @throws NullPointerException
     *  If a <code>null</code> reference of type array 
     *  of <code>T</code>'s is presented, or the given
     *  comparator is <code>null</code>.
     */
    public final static <T> T max(Comparator<? super T> comparator, T... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        T max = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (comparator.compare(elements[i],max) > 0) 
                max = elements[i];
        return max;
    }
    
    /**
     * Returns the maximal element in the given array.
     * 
     * @param elements
     *  A number of elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If no elements are given.
     * @throws NullPointerException
     *  If a <code>null</code>-array of is presented.
     */
    public final static short max(short... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        short max = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (elements[i] > max) max = elements[i];
        return max;
    }
    
    /**
     * Returns the maximal element in the given array.
     * 
     * @param elements
     *  A number of elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If no elements are given.
     * @throws NullPointerException
     *  If a <code>null</code>-array of is presented.
     */
    public final static char max(char... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        char max = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (elements[i] > max) max = elements[i];
        return max;
    }
    
    /**
     * Returns the maximal element in the given array.
     * 
     * @param elements
     *  A number of elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If no elements are given.
     * @throws NullPointerException
     *  If a <code>null</code>-array of is presented.
     */
    public final static int max(int... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        int max = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (elements[i] > max) max = elements[i];
        return max;
    }
    
    
    /**
     * Returns the maximal element in the given array.
     * 
     * @param elements
     *  A number of elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If no elements are given.
     * @throws NullPointerException
     *  If a <code>null</code>-array of is presented.
     */
    public final static long max(long... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        long max = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (elements[i] > max) max = elements[i];
        return max;
    }
    
    /**
     * Returns the maximal element in the given array.
     * Elements are compared using <code>Float.compare</code>.
     * 
     * @param elements
     *  A number of elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If no elements are given.
     * @throws NullPointerException
     *  If a <code>null</code>-array of is presented.
     *  
     * @see Float#compare(float, float)
     */
    public final static float max(float... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        float max = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (Float.compare(elements[i], max) > 0) max = elements[i];
        return max;
    }
    
    /**
     * Returns the maximal element in the given array.
     * Elements are compared using <code>Double.compare</code>.
     * 
     * @param elements
     *  A number of elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If no elements are given.
     * @throws NullPointerException
     *  If a <code>null</code>-array of is presented.
     *  
     * @see Double#compare(double, double)
     */
    public final static double max(double... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        double max = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (Double.compare(elements[i], max) > 0) max = elements[i];
        return max;
    }
    
    /**
     * Returns the maximal element in the given array.
     * 
     * @param elements
     *  A number of elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If no elements are given.
     * @throws NullPointerException
     *  If a <code>null</code>-array of is presented.
     */
    public final static byte max(byte... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        byte max = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (elements[i] > max) max = elements[i];
        return max;
    }
    
    /**
     * Returns the minimal element in the given array.
     * 
     * @param elements
     *  A number of elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If no elements are given.
     * @throws NullPointerException
     *  If a <code>null</code>-array of is presented.
     */
    public final static short min(short... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        short min = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (elements[i] < min) min = elements[i];
        return min;
    }
    
    /**
     * Returns the minimal element in the given array.
     * 
     * @param elements
     *  A number of elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If no elements are given.
     * @throws NullPointerException
     *  If a <code>null</code>-array of is presented.
     */
    public final static char min(char... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        char min = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (elements[i] < min) min = elements[i];
        return min;
    }
    
    /**
     * Returns the minimal element in the given array.
     * 
     * @param elements
     *  A number of elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If no elements are given.
     * @throws NullPointerException
     *  If a <code>null</code>-array of is presented.
     */
    public final static int min(int... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        int min = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (elements[i] < min) min = elements[i];
        return min;
    }
    
    
    /**
     * Returns the minimal element in the given array.
     * 
     * @param elements
     *  A number of elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If no elements are given.
     * @throws NullPointerException
     *  If a <code>null</code>-array of is presented.
     */
    public final static long min(long... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        long min = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (elements[i] < min) min = elements[i];
        return min;
    }
    
    /**
     * Returns the minimal element in the given array.
     * Elements are compared using <code>Float.compare</code>.
     * 
     * @param elements
     *  A number of elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If no elements are given.
     * @throws NullPointerException
     *  If a <code>null</code>-array of is presented.
     *  
     * @see Float#compare(float, float)
     */
    public final static float min(float... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        float min = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (Float.compare(elements[i], min) < 0) min = elements[i];
        return min;
    }
    
    /**
     * Returns the minimal element in the given array.
     * Elements are compared using <code>Double.compare</code>.
     * 
     * @param elements
     *  A number of elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If no elements are given.
     * @throws NullPointerException
     *  If a <code>null</code>-array of is presented.
     *  
     * @see Double#compare(double, double)
     */
    public final static double min(double... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        double min = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (Double.compare(elements[i], min) < 0) min = elements[i];
        return min;
    }
    
    /**
     * Returns the minimal element in the given array.
     * 
     * @param elements
     *  A number of elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If no elements are given.
     * @throws NullPointerException
     *  If a <code>null</code>-array of is presented.
     */
    public final static byte min(byte... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        byte min = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (elements[i] < min) min = elements[i];
        return min;
    }
    
    /**
     * Returns the minimal element in the given array.
     * Elements are compared using the given comparator.
     * 
     * @param elements
     *  A number of non-null elements.
     * @param comparator
     *  The object responsible for comparing the elements
     *  in the given array.
     * @throws IndexOutOfBoundsException
     *  If the no elements are presented.
     * @throws NullPointerException
     *  If a <code>null</code> reference of type array 
     *  of <code>T</code>'s is presented, or the given
     *  comparator is <code>null</code>.
     */
    public final static <T> T min(Comparator<? super T> comparator, T... elements) 
    throws IndexOutOfBoundsException, NullPointerException {
        T min = elements[0];
        int i = 0;
        while (++i < elements.length)
            if (comparator.compare(elements[i],min) < 0) 
                min = elements[i];
        return min;
    }
    
    /**
     * Returns whether or not the elements in the given array are
     * sorted from small to large according to their natural order.
     * 
     * @param elements
     *  The array that has to be tested.
     * @return <code>true</code> if the elements in the given array are
     *  sorted from small to large according to their natural order;
     *  <code>false</code> otherwise.
     *  
     * @throws NullPointerException
     *  If <code>elements</code> is a null-pointer.
     */
    public final static boolean isSorted(int... elements) {
        for (int i = 1; i < elements.length; i++)
            if (elements[i] < elements[i-1]) return false;
        return true;
    }
    
    /**
     * Returns whether or not the elements in the given array are
     * sorted from small to large according to their natural order.
     * 
     * @param elements
     *  The array that has to be tested.
     * @return <code>true</code> if the elements in the given array are
     *  sorted from small to large according to their natural order;
     *  <code>false</code> otherwise.
     *  
     * @throws NullPointerException
     *  If <code>elements</code> is a null-pointer.
     */
    public final static boolean isSorted(byte... elements) {
        for (int i = 1; i < elements.length; i++)
            if (elements[i] < elements[i-1]) return false;
        return true;
    }
    
    /**
     * Returns whether or not the elements in the given array are
     * sorted from small to large according to their natural order.
     * 
     * @param elements
     *  The array that has to be tested.
     * @return <code>true</code> if the elements in the given array are
     *  sorted from small to large according to their natural order;
     *  <code>false</code> otherwise.
     *  
     * @throws NullPointerException
     *  If <code>elements</code> is a null-pointer.
     */
    public final static boolean isSorted(short... elements) {
        for (int i = 1; i < elements.length; i++)
            if (elements[i] < elements[i-1]) return false;
        return true;
    }
    
    /**
     * Returns whether or not the elements in the given array are
     * sorted from small to large according to their natural order.
     * 
     * @param elements
     *  The array that has to be tested.
     * @return <code>true</code> if the elements in the given array are
     *  sorted from small to large according to their natural order;
     *  <code>false</code> otherwise.
     * 
     * @throws NullPointerException
     *  If <code>elements</code> is a null-pointer.
     */
    public final static boolean isSorted(long... elements) {
        for (int i = 1; i < elements.length; i++)
            if (elements[i] < elements[i-1]) return false;
        return true;
    }
    
    /**
     * Returns whether or not the elements in the given array are
     * sorted from small to large according to their natural order.
     * 
     * @param elements
     *  The array that has to be tested.
     * @return <code>true</code> if the elements in the given array are
     *  sorted from small to large according to their natural order;
     *  <code>false</code> otherwise.
     *  
     * @throws NullPointerException
     *  If <code>elements</code> is a null-pointer.
     */
    public final static boolean isSorted(float... elements) {
        for (int i = 1; i < elements.length; i++)
            if (elements[i] < elements[i-1]) return false;
        return true;
    }
    
    /**
     * Returns whether or not the elements in the given array are
     * sorted from small to large according to their natural order.
     * 
     * @param elements
     *  The array that has to be tested.
     * @return <code>true</code> if the elements in the given array are
     *  sorted from small to large according to their natural order;
     *  <code>false</code> otherwise.
     *  
     * @throws NullPointerException
     *  If <code>elements</code> is a null-pointer.
     */
    public final static boolean isSorted(double... elements) {
        for (int i = 1; i < elements.length; i++)
            if (elements[i] < elements[i-1]) return false;
        return true;
    }
    
    /**
     * Returns whether or not the elements in the given array are
     * sorted from small to large according to their natural order.
     * 
     * @param elements
     *  The array that has to be tested.
     * @return <code>true</code> if the elements in the given array are
     *  sorted from small to large according to their natural order;
     *  <code>false</code> otherwise.
     *  
     * @throws NullPointerException
     *  If <code>elements</code> is a null-pointer.   
     */
    public final static boolean isSorted(char... elements) {
        for (int i = 1; i < elements.length; i++)
            if (elements[i] < elements[i-1]) return false;
        return true;
    }
    
    /**
     * Returns whether or not the elements in the given array are
     * sorted from small to large according to their natural order.
     * 
     * @param elements
     *  The array that has to be tested.
     * @return <code>true</code> if the elements in the given array are
     *  sorted from small to large according to their natural order;
     *  <code>false</code> otherwise.
     * 
     * @throws NullPointerException
     *  If <code>elements</code> is a null-pointer.   
     */
    public final static <T extends Comparable<? super T>> 
    boolean isSorted(T... elements) {
        for (int i = 1; i < elements.length; i++)
            if (elements[i].compareTo(elements[i-1]) < 0) return false;
        return true;
    }
    
    /**
     * Returns whether or not the elements in the given array are
     * sorted from small to large according to the order defined by
     * the given comparator.
     * 
     * @param elements
     *  The array that has to be tested.
     * @param comparator
     *  The comparator to be used to compare elements.
     *  
     * @return <code>true</code> if the elements in the given array are
     *  sorted from small to large according to the order defined by
     *  the given comparator; <code>false</code> otherwise.
     * 
     * @throws NullPointerException
     *  If <code>elements</code> or <code>comparator</code> 
     *  is a null-pointer.   
     */
    public final static <T> boolean 
        isSorted(Comparator<? super T> comparator, T... elements) {
        
        for (int i = 1; i < elements.length; i++)
            if (comparator.compare(elements[i], (elements[i-1])) < 0) 
                return false;
        return true;
    }
    
    /**
     * <p>
     * Returns an array containing all elements of <code>array</code>, in 
     * the same order, except the first occurrence of <code>elem</code>.
     * Elements are compared by comparing their object identities (through <code>==</code>). 
     * Note that this method is intended to be used when you are sure
     * the element is in the array, or at least most of the time.
     * </p>
     * <p>
     * Note that when you need to call this method many times, it
     * might be e.g. better to sort the array and use binary search
     * (see {@link java.util.Arrays }).
     * </p>
     * 
     * @param array
     *  The array that has to be scanned.
     * @param elem
     *  The element that has to be searched and excluded.
     * @param result
     *  The array the results will be stored in. This is also
     *  the array that is returned by this method. This array
     *  <i>cannot</i> be <code>null</code>. It should also
     *  be long enough to hold all resulting elements.
     * @return <code>result</code> (the third parameter)
     *   
     * @throws ArrayIndexOutOfBoundsException
     *  If the element is not contained in the given array,
     *  or if the array that has to hold the result is not
     *  long enough.
     * @throws NullPointerException
     *  If <code>array</code> is <code>null</code>, or
     *  if <code>result</code> is <code>null</code>.
     *  
     * @see java.util.Arrays
     */
    public final static <S, T extends S> T[] identityExcludeFirst(S[] array, T elem, T[] result)
    throws NullPointerException, ArrayIndexOutOfBoundsException {
        return remove(array, identityFirstIndexOf(array, elem), result);
    }
    
    /**
     *<p>
     * Returns an array containing all elements of <code>array</code>, in 
     * the same order, except the element at a given index.
     * 
     * @param array
     *  The input array
     * @param index
     *  The index of the element to be excluded.
     * @param result
     *  The array the results will be stored in. This is also
     *  the array that is returned by this method. This array
     *  <i>cannot</i> be <code>null</code>. It should also
     *  be long enough to hold all resulting elements.
     * @return <code>result</code> (the third parameter)
     *   
     * @throws ArrayIndexOutOfBoundsException
     *  If <code>index</code> is out of bounds, or if 
     *  <code>result</code> is not long enough to hold the result.
     * @throws NullPointerException
     *  If <code>array</code> is <code>null</code>, or
     *  if <code>result</code> is <code>null</code>.
     */
    public final static <S, T extends S> T[] remove(S[] array, int index, T[] result)
    throws NullPointerException, ArrayIndexOutOfBoundsException {
        System.arraycopy(array, 0, result, 0, index++);
        System.arraycopy(array, index, result, index-1, array.length - index);
        return result;
    }
    
    /**
     * <p>
     * Returns an unmodiable, fixed-size set backed by the specified 
     * array. The given array is not allowed to contain doubles
     * (this is a <i>precondition</i>), as this would result in
     * an illegal <code>Set</code>. 
     * <p>
     * </p>
     * This method acts as bridge between array-based and 
     * collection-based APIs, in combination with <tt>Set.toArray</tt>.
     * An example of an API that returns sets in the form of arrays
     * is the reflection API (e.g. {@link Class#getMethods()}
     * or {@link Class#getConstructors()}).
     * <br/>
     * This method also provides a convenient way to create a fixed-size
     * set initialized to contain several elements:
     * </p>
     * <pre>
     *     Set&lt;String&gt; stooges = Arrays.asSet("Larry", "Moe", "Curly");
     * </pre>
     * <p>
     * The returned set is serializable.
     * </p>
     *
     * @param A the array by which the set will be backed. 
     * 	This array is not allowed to contain doubles.
     * @return a set view of the specified array.
     * 
     * @see Set#toArray()
     * @see java.util.Arrays#asList(T[])
     */
    public final static <T> Set<T> asSet(T... elements) {
        return new ArraySet<T>(elements);
    }
    
    private static class ArraySet<T> extends AbstractSet<T> implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private T[] array;
        
        public ArraySet(T[] array) {
            if (array == null)
                throw new NullPointerException();
            this.array = array;
        }
        
        @Override
        public int size() {
            return array.length;
        }
    
        @Override
        public Iterator<T> iterator() {
            return new ArrayIterator<T>(array);
        }
        
        @Override
        public Object[] toArray() {
            return array.clone();
        }
        
        @Override
        @SuppressWarnings({"unchecked", "hiding"})
        public <T> T[] toArray(T[] a) {
            if (a.length < array.length)
                return (T[])array.clone();
            System.arraycopy(array, 0, a, 0, array.length);
            if (a.length > array.length)
                a[array.length] = null;
            return a;
        }
        
        @Override
        public boolean contains(Object elem) {
            return Arrays.contains(array, elem);
        }
        
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean removeAll(Collection<?> c) {
        	throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Converts an array of number elements to an array of primitive ints.
     * 
     * @param array
     *  The array that has to be converted.
     * @return An array containing the result of the {@link Number#intValue()}
     *  method applied to all members off the given array. If <code>array</code>
     *  is <code>null</code>, the result will be as well. 
     */
    public static int[] toIntArray(Number... array) {
        if (array == null) return null;
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = array[i].intValue();
        return result;
    }
    
    /**
     * Converts an array of number elements to an array of primitive bytes.
     * 
     * @param array
     *  The array that has to be converted.
     * @return An array containing the result of the {@link Number#byteValue()}
     *  method applied to all members off the given array. If <code>array</code>
     *  is <code>null</code>, the result will be as well. 
     */
    public static byte[] toByteArray(Number... array) {
        if (array == null) return null;
        byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = array[i].byteValue();
        return result;
    }
    
    /**
     * Converts an array of <code>Boolean</code> elements to an array 
     * of primitive booleans.
     * 
     * @param array
     *  The array that has to be converted.
     * @return An array containing the primitive versions of
     *  the given <code>Boolean</code>s. If <code>array</code>
     *  is <code>null</code>, the result will be as well. 
     */
    public static boolean[] toBooleanArray(Boolean... array) {
        if (array == null) return null;
        boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = array[i].booleanValue();
        return result;
    }
    
    /**
     * Converts an array of number elements to an array of primitive shorts.
     * 
     * @param array
     *  The array that has to be converted.
     * @return An array containing the result of the {@link Number#shortValue()}
     *  method applied to all members off the given array. If <code>array</code>
     *  is <code>null</code>, the result will be as well. 
     */
    public static short[] toShortArray(Number... array) {
        if (array == null) return null;
        short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = array[i].shortValue();
        return result;
    }
    
    /**
     * Converts an array of number elements to an array of primitive longs.
     * 
     * @param array
     *  The array that has to be converted.
     * @return An array containing the result of the {@link Number#longValue()}
     *  method applied to all members off the given array. If <code>array</code>
     *  is <code>null</code>, the result will be as well. 
     */
    public static long[] toLongArray(Number... array) {
        if (array == null) return null;
        long[] result = new long[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = array[i].longValue();
        return result;
    }
    
    /**
     * Converts an array of number elements to an array of primitive floats.
     * 
     * @param array
     *  The array that has to be converted.
     * @return An array containing the result of the {@link Number#floatValue()}
     *  method applied to all members off the given array. If <code>array</code>
     *  is <code>null</code>, the result will be as well. 
     */
    public static float[] toFloatArray(Number... array) {
        if (array == null) return null;
        float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = array[i].floatValue();
        return result;
    }
    
    /**
     * Converts an array of number elements to an array of primitive doubles.
     * 
     * @param array
     *  The array that has to be converted.
     * @return An array containing the result of the {@link Number#doubleValue()}
     *  method applied to all members off the given array. If <code>array</code>
     *  is <code>null</code>, the result will be as well. 
     */
    public static double[] toDoubleArray(Number... array) {
        if (array == null) return null;
        double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = array[i].doubleValue();
        return result;
    }
    
    /**
     * Appends the given elements to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The elements that have to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by all elements that had to be appended.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     */
    public static int[] append(int[] array, int... toAppend) {
        int[] result = new int[array.length + toAppend.length];
        System.arraycopy(array, 0, result, 0, array.length);
        System.arraycopy(toAppend, 0, result, array.length, toAppend.length);
        return result;   
    }
    
    /**
     * Appends the given elements to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The elements that have to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by all elements that had to be appended.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     */
    public static byte[] append(byte[] array, byte... toAppend) {
        byte[] result = new byte[array.length + toAppend.length];
        System.arraycopy(array, 0, result, 0, array.length);
        System.arraycopy(toAppend, 0, result, array.length, toAppend.length);
        return result;   
    }
    
    /**
     * Appends the given elements to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The elements that have to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by all elements that had to be appended.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     */
    public static boolean[] append(boolean[] array, boolean... toAppend) {
        boolean[] result = new boolean[array.length + toAppend.length];
        System.arraycopy(array, 0, result, 0, array.length);
        System.arraycopy(toAppend, 0, result, array.length, toAppend.length);
        return result;   
    }
    
    /**
     * Appends the given elements to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The elements that have to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by all elements that had to be appended.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     */
    public static short[] append(short[] array, short... toAppend) {
        short[] result = new short[array.length + toAppend.length];
        System.arraycopy(array, 0, result, 0, array.length);
        System.arraycopy(toAppend, 0, result, array.length, toAppend.length);
        return result;   
    }
    
    /**
     * Appends the given elements to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The elements that have to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by all elements that had to be appended.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     */
    public static char[] append(char[] array, char... toAppend) {
        char[] result = new char[array.length + toAppend.length];
        System.arraycopy(array, 0, result, 0, array.length);
        System.arraycopy(toAppend, 0, result, array.length, toAppend.length);
        return result;   
    }
    
    /**
     * Appends the given elements to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The elements that have to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by all elements that had to be appended.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     */
    public static long[] append(long[] array, long... toAppend) {
        long[] result = new long[array.length + toAppend.length];
        System.arraycopy(array, 0, result, 0, array.length);
        System.arraycopy(toAppend, 0, result, array.length, toAppend.length);
        return result;   
    }
    
    /**
     * Appends the given elements to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The elements that have to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by all elements that had to be appended.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     */
    public static float[] append(float[] array, float... toAppend) {
        float[] result = new float[array.length + toAppend.length];
        System.arraycopy(array, 0, result, 0, array.length);
        System.arraycopy(toAppend, 0, result, array.length, toAppend.length);
        return result;   
    }
    
    /**
     * Appends the given elements to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The elements that have to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by all elements that had to be appended.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     */
    public static double[] append(double[] array, double... toAppend) {
        double[] result = new double[array.length + toAppend.length];
        System.arraycopy(array, 0, result, 0, array.length);
        System.arraycopy(toAppend, 0, result, array.length, toAppend.length);
        return result;   
    }
    
    /**
     * Appends the given elements to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The elements that have to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by all elements that had to be appended.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     */
    public static <T> T[] append(T[] array, T... toAppend) {
        @SuppressWarnings("unchecked")
        T[] result = (T[])newInstance(array.getClass().getComponentType(), array.length + toAppend.length);
        System.arraycopy(array, 0, result, 0, array.length);
        System.arraycopy(toAppend, 0, result, array.length, toAppend.length);
        return result;   
    }
    
    /**
     * Appends the given element to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The element that has to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by the element that had to be appended.
     *  
     * @throws NullPointerException
     *  If the array is <code>null</code>.
     */
    public static int[] append(int[] array, int toAppend) {
        int[] result = new int[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = toAppend;
        return result;   
    }
    
    /**
     * Appends the given element to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The element that has to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by the element that had to be appended.
     *  
     * @throws NullPointerException
     *  If the array is <code>null</code>.
     */
    public static byte[] append(byte[] array, byte toAppend) {
        byte[] result = new byte[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = toAppend;
        return result;   
    }
    
    /**
     * Appends the given element to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The element that has to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by the element that had to be appended.
     *  
     * @throws NullPointerException
     *  If the array is <code>null</code>.
     */
    public static boolean[] append(boolean[] array, boolean toAppend) {
        boolean[] result = new boolean[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = toAppend;
        return result;   
    }
    
    /**
     * Appends the given element to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The element that has to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by the element that had to be appended.
     *  
     * @throws NullPointerException
     *  If the array is <code>null</code>.
     */
    public static short[] append(short[] array, short toAppend) {
        short[] result = new short[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = toAppend;
        return result;   
    }
    
    /**
     * Appends the given element to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The element that has to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by the element that had to be appended.
     *  
     * @throws NullPointerException
     *  If the array is <code>null</code>.
     */
    public static char[] append(char[] array, char toAppend) {
        char[] result = new char[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = toAppend;
        return result;   
    }
    
    /**
     * Appends the given element to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The element that has to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by the element that had to be appended.
     *  
     * @throws NullPointerException
     *  If the array is <code>null</code>.
     */
    public static long[] append(long[] array, long toAppend) {
        long[] result = new long[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = toAppend;
        return result;   
    }
    
    /**
     * Appends the given element to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The element that has to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by the element that had to be appended.
     *  
     * @throws NullPointerException
     *  If the array is <code>null</code>.
     */
    public static float[] append(float[] array, float toAppend) {
        float[] result = new float[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = toAppend;
        return result;
    }
    
    /**
     * Appends the given element to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The element that has to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by the element that had to be appended.
     *  
     * @throws NullPointerException
     *  If the array is <code>null</code>.
     */
    public static double[] append(double[] array, double toAppend) {
        double[] result = new double[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = toAppend;
        return result;   
    }
    
    /**
     * Appends the given element to an array. 
     * 
     * @param array
     *  The original array.
     * @param toAppend
     *  The element that has to be appended to the array.
     * @return An array containing all elements of the original
     *  array, followed by the element that had to be appended.
     *  
     * @throws NullPointerException
     *  If the array is <code>null</code>.
     */
    public static <T> T[] append(T[] array, T toAppend) {
        @SuppressWarnings("unchecked")
        T[] result = (T[])new Object[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = toAppend;
        return result;   
    }
    
    /**
     * Adds the given element into a sorted array. 
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the added element.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static int[] binaryAdd(int[] array, int toAdd) {
    	return add(array, abs(binarySearch(array, toAdd) + 1), toAdd);
    }
    
    /**
     * Adds the given element into a sorted array. 
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the added element.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static long[] binaryAdd(long[] array, long toAdd) {
    	return add(array, abs(binarySearch(array, toAdd) + 1), toAdd);
    }
    
    /**
     * Adds the given element into a sorted array. 
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the added element.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static short[] binaryAdd(short[] array, short toAdd) {
    	return add(array, abs(binarySearch(array, toAdd) + 1), toAdd);
    }
    
    /**
     * Adds the given element into a sorted array. 
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the added element.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static byte[] binaryAdd(byte[] array, byte toAdd) {
    	return add(array, abs(binarySearch(array, toAdd) + 1), toAdd);
    }
    
    /**
     * Adds the given element into a sorted array. 
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the added element.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static float[] binaryAdd(float[] array, float toAdd) {
    	return add(array, abs(binarySearch(array, toAdd) + 1), toAdd);
    }
    
    /**
     * Adds the given element into a sorted array. 
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the added element.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static double[] binaryAdd(double[] array, double toAdd) {
    	return add(array, abs(binarySearch(array, toAdd) + 1), toAdd);
    }
    
    /**
     * Adds the given element into a sorted array. 
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the added element.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static char[] binaryAdd(char[] array, char toAdd) {
    	return add(array, abs(binarySearch(array, toAdd) + 1), toAdd);
    }
    
    /**
     * Adds the given element into a sorted array. 
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the added element.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static <T extends Comparable<? super T>> T[] binaryAdd(T[] array, T toAdd) {
    	return add(array, abs(binarySearch(array, toAdd) + 1), toAdd);
    }
    
    /**
     * Adds the given element into a sorted array. 
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the added element.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static <T> T[] binaryAdd(T[] array, T toAdd, Comparator<? super T> comparator) {
    	return add(array, abs(binarySearch(array, toAdd, comparator) + 1), toAdd);
    }
    
    /**
     * Inserts the given element into a sorted array. 
     * If the element was already present, nothing happens.
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toInsert
     *  The element that has to be inserted into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the inserted element.
     *  If the element was already present, the original array is returned.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static int[] binaryInsert(int[] array, int toInsert) {
    	int index = binarySearch(array, toInsert);
    	return (index < 0)? add(array, -index-1, toInsert) : array;
    }
    
    /**
     * Inserts the given element into a sorted array. 
     * If the element was already present, nothing happens.
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toInsert
     *  The element that has to be inserted into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the inserted element.
     *  If the element was already present, the original array is returned.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static long[] binaryInsert(long[] array, long toInsert) {
    	int index = binarySearch(array, toInsert);
    	return (index < 0)? add(array, -index-1, toInsert) : array;
    }
    
    /**
     * Inserts the given element into a sorted array. 
     * If the element was already present, nothing happens.
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toInsert
     *  The element that has to be inserted into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the inserted element.
     *  If the element was already present, the original array is returned.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static short[] binaryInsert(short[] array, short toInsert) {
    	int index = binarySearch(array, toInsert);
    	return (index < 0)? add(array, -index-1, toInsert) : array;
    }
    
    /**
     * Inserts the given element into a sorted array. 
     * If the element was already present, nothing happens.
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toInsert
     *  The element that has to be inserted into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the inserted element.
     *  If the element was already present, the original array is returned.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static byte[] binaryInsert(byte[] array, byte toInsert) {
    	int index = binarySearch(array, toInsert);
    	return (index < 0)? add(array, -index-1, toInsert) : array;
    }
    
    /**
     * Inserts the given element into a sorted array. 
     * If the element was already present, nothing happens.
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toInsert
     *  The element that has to be inserted into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the inserted element.
     *  If the element was already present, the original array is returned.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static float[] binaryInsert(float[] array, float toInsert) {
    	int index = binarySearch(array, toInsert);
    	return (index < 0)? add(array, -index-1, toInsert) : array;
    }
    
    /**
     * Inserts the given element into a sorted array. 
     * If the element was already present, nothing happens.
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toInsert
     *  The element that has to be inserted into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the inserted element.
     *  If the element was already present, the original array is returned.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static double[] binaryInsert(double[] array, double toInsert) {
    	int index = binarySearch(array, toInsert);
    	return (index < 0)? add(array, -index-1, toInsert) : array;
    }
    
    /**
     * Inserts the given element into a sorted array.
     * If the element was already present, nothing happens.  
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toInsert
     *  The element that has to be inserted into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the inserted element.
     *  If the element was already present, the original array is returned.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static char[] binaryInsert(char[] array, char toInsert) {
    	int index = binarySearch(array, toInsert);
    	return (index < 0)? add(array, -index-1, toInsert) : array;
    }
    
    /**
     * Inserts the given element into a sorted array. 
     * If the element was already present, nothing happens.
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toInsert
     *  The element that has to be inserted into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the inserted element.
     *  If the element was already present, the original array is returned.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static <T extends Comparable<? super T>> T[] binaryInsert(T[] array, T toInsert) {
    	int index = binarySearch(array, toInsert);
    	return (index < 0)? add(array, -index-1, toInsert) : array;
    }
    
    /**
     * Inserts the given element into a sorted array. 
     * If the element was already present, nothing happens.
     *  
     * @param array
     *  The original array, sorted from small to large.
     * @param toInsert
     *  The element that has to be inserted into the array.
     * @return A sorted array containing all elements of the original
     *  array, as well as the inserted element.
     *  If the element was already present, the original array is returned.
     *  
     * @throws NullPointerException
     *  If either array is <code>null</code>.
     */
    public static <T> T[] binaryInsert(T[] array, T toInsert, Comparator<? super T> comparator) {
    	int index = binarySearch(array, toInsert, comparator);
    	return (index < 0)? add(array, -index-1, toInsert) : array;
    }
    
    /**
     * Adds the given elements into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added first element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the elements are to be added.
     * @param toAdd
     *  The elements that have to be added into the array.
     * @return An array containing elements of the original
     *  array, followed (at index <code>index</code> and further)
     *  by all elements that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static <T> T[] add(T[] array, int index, T... toAdd) {
        @SuppressWarnings("unchecked")
        T[] result = (T[])new Object[array.length + toAdd.length];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(toAdd, 0, result, index, toAdd.length);
        if (index != array.length)
            System.arraycopy(array, index, result, index+toAdd.length, array.length-index);
        return result;
    }
    
    /**
     * Adds the given elements into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added first element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the elements are to be added.
     * @param toAdd
     *  The elements that have to be added into the array.
     * @return An array containing elements of the original
     *  array, followed (at index <code>index</code> and further)
     *  by all elements that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static int[] add(int[] array, int index, int... toAdd) {
        int[] result = new int[array.length + toAdd.length];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(toAdd, 0, result, index, toAdd.length);
        if (index != array.length)
            System.arraycopy(array, index, result, index+toAdd.length, array.length-index);
        return result;
    }
    
    /**
     * Adds the given elements into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added first element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the elements are to be added.
     * @param toAdd
     *  The elements that have to be added into the array.
     * @return An array containing elements of the original
     *  array, followed (at index <code>index</code> and further)
     *  by all elements that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static byte[] add(byte[] array, int index, byte... toAdd) {
        byte[] result = new byte[array.length + toAdd.length];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(toAdd, 0, result, index, toAdd.length);
        if (index != array.length)
            System.arraycopy(array, index, result, index+toAdd.length, array.length-index);
        return result;
    }
    
    /**
     * Adds the given elements into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added first element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the elements are to be added.
     * @param toAdd
     *  The elements that have to be added into the array.
     * @return An array containing elements of the original
     *  array, followed (at index <code>index</code> and further)
     *  by all elements that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static boolean[] add(boolean[] array, int index, boolean... toAdd) {
        boolean[] result = new boolean[array.length + toAdd.length];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(toAdd, 0, result, index, toAdd.length);
        if (index != array.length)
            System.arraycopy(array, index, result, index+toAdd.length, array.length-index);
        return result;
    }
    
    /**
     * Adds the given elements into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added first element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the elements are to be added.
     * @param toAdd
     *  The elements that have to be added into the array.
     * @return An array containing elements of the original
     *  array, followed (at index <code>index</code> and further)
     *  by all elements that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static short[] add(short[] array, int index, short... toAdd) {
        short[] result = new short[array.length + toAdd.length];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(toAdd, 0, result, index, toAdd.length);
        if (index != array.length)
            System.arraycopy(array, index, result, index+toAdd.length, array.length-index);
        return result;
    }
    
    /**
     * Adds the given elements into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added first element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the elements are to be added.
     * @param toAdd
     *  The elements that have to be added into the array.
     * @return An array containing elements of the original
     *  array, followed (at index <code>index</code> and further)
     *  by all elements that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static char[] add(char[] array, int index, char... toAdd) {
        char[] result = new char[array.length + toAdd.length];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(toAdd, 0, result, index, toAdd.length);
        if (index != array.length)
            System.arraycopy(array, index, result, index+toAdd.length, array.length-index);
        return result;
    }
    
    /**
     * Adds the given elements into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added first element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the elements are to be added.
     * @param toAdd
     *  The elements that have to be added into the array.
     * @return An array containing elements of the original
     *  array, followed (at index <code>index</code> and further)
     *  by all elements that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static long[] add(long[] array, int index, long... toAdd) {
        long[] result = new long[array.length + toAdd.length];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(toAdd, 0, result, index, toAdd.length);
        if (index != array.length)
            System.arraycopy(array, index, result, index+toAdd.length, array.length-index);
        return result;
    }
    
    /**
     * Adds the given elements into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added first element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the elements are to be added.
     * @param toAdd
     *  The elements that have to be added into the array.
     * @return An array containing elements of the original
     *  array, followed (at index <code>index</code> and further)
     *  by all elements that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static float[] add(float[] array, int index, float... toAdd) {
        float[] result = new float[array.length + toAdd.length];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(toAdd, 0, result, index, toAdd.length);
        if (index != array.length)
            System.arraycopy(array, index, result, index+toAdd.length, array.length-index);
        return result;
    }
    
    /**
     * Adds the given elements into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added first element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the elements are to be added.
     * @param toAdd
     *  The elements that have to be added into the array.
     * @return An array containing elements of the original
     *  array, followed (at index <code>index</code> and further)
     *  by all elements that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static double[] add(double[] array, int index, double... toAdd) {
        double[] result = new double[array.length + toAdd.length];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        System.arraycopy(toAdd, 0, result, index, toAdd.length);
        if (index != array.length)
            System.arraycopy(array, index, result, index+toAdd.length, array.length-index);
        return result;
    }
    
    /**
     * Adds the given element into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the element has to be added.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return An array containing element of the original
     *  array, followed (at index <code>index</code> and further)
     *  by the element that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static <T> T[] add(T[] array, int index, T toAdd) {
        @SuppressWarnings("unchecked")
        T[] result = (T[])newInstance(array.getClass().getComponentType(), array.length + 1);
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        result[index] = toAdd;
        if (index != array.length)
            System.arraycopy(array, index, result, index+1, array.length-index);
        return result;
    }
    
    /**
     * Adds the given element into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the element has to be added.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return An array containing element of the original
     *  array, followed (at index <code>index</code> and further)
     *  by the element that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static int[] add(int[] array, int index, int toAdd) {
        int[] result = new int[array.length + 1];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        result[index] = toAdd;
        if (index != array.length)
            System.arraycopy(array, index, result, index+1, array.length-index);
        return result;
    }
    
    /**
     * Adds the given element into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the element has to be added.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return An array containing element of the original
     *  array, followed (at index <code>index</code> and further)
     *  by the element that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static byte[] add(byte[] array, int index, byte toAdd) {
        byte[] result = new byte[array.length + 1];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        result[index] = toAdd;
        if (index != array.length)
            System.arraycopy(array, index, result, index+1, array.length-index);
        return result;
    }
    
    /**
     * Adds the given element into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the element has to be added.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return An array containing element of the original
     *  array, followed (at index <code>index</code> and further)
     *  by the element that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static boolean[] add(boolean[] array, int index, boolean toAdd) {
        boolean[] result = new boolean[array.length + 1];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        result[index] = toAdd;
        if (index != array.length)
            System.arraycopy(array, index, result, index+1, array.length-index);
        return result;
    }
    
    /**
     * Adds the given element into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the element has to be added.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return An array containing element of the original
     *  array, followed (at index <code>index</code> and further)
     *  by the element that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static short[] add(short[] array, int index, short toAdd) {
        short[] result = new short[array.length + 1];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        result[index] = toAdd;
        if (index != array.length)
            System.arraycopy(array, index, result, index+1, array.length-index);
        return result;
    }
    
    /**
     * Adds the given element into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the element has to be added.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return An array containing element of the original
     *  array, followed (at index <code>index</code> and further)
     *  by the element that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static char[] add(char[] array, int index, char toAdd) {
        char[] result = new char[array.length + 1];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        result[index] = toAdd;
        if (index != array.length)
            System.arraycopy(array, index, result, index+1, array.length-index);
        return result;
    }
    
    /**
     * Adds the given element into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the element has to be added.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return An array containing element of the original
     *  array, followed (at index <code>index</code> and further)
     *  by the element that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static long[] add(long[] array, int index, long toAdd) {
        long[] result = new long[array.length + 1];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        result[index] = toAdd;
        if (index != array.length)
            System.arraycopy(array, index, result, index+1, array.length-index);
        return result;
    }
    
    /**
     * Adds the given element into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the element has to be added.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return An array containing element of the original
     *  array, followed (at index <code>index</code> and further)
     *  by the element that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static float[] add(float[] array, int index, float toAdd) {
        float[] result = new float[array.length + 1];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        result[index] = toAdd;
        if (index != array.length)
            System.arraycopy(array, index, result, index+1, array.length-index);
        return result;
    }
    
    /**
     * Adds the given element into an array at a given index
     * (i.e. in the resulting array the element at <code>index</code>
     * will be the added element).
     *  
     * @param array
     *  The original array.
     * @param index
     *  The index where the element has to be added.
     * @param toAdd
     *  The element that has to be added into the array.
     * @return An array containing element of the original
     *  array, followed (at index <code>index</code> and further)
     *  by the element that had to be added, followed by the
     *  remainder of the original array.
     *  
     * @throws NullPointerException
     *  If either one of the arguments is <code>null</code>.
     * @throws IndexOutOfBoundsException
     *  If <code>index</code> is not between 0 and <code>array.length</code>
     *  (inclusive: in this case the add works as an append).
     */
    public static double[] add(double[] array, int index, double toAdd) {
        double[] result = new double[array.length + 1];
        if (index != 0)
            System.arraycopy(array, 0, result, 0, index);
        result[index] = toAdd;
        if (index != array.length)
            System.arraycopy(array, index, result, index+1, array.length-index);
        return result;
    }
    
    /**
     * Checks whether one sorted array is a continuous sub-array of another sorted array.
     * No duplicates are allowed in either array. 
     * If duplicates are present, the result is unspecified.
     * 
     * @param one
     * 	A sorted, duplicate-free array.
     * @param other
     * 	A sorted, duplicate-free array.
     * @return <code>true</code> iff the first argument is a continuous sub-array
     * 	of the second; <code>false</code> otherwise.
     */
    public static boolean isContinuousSubArraySorted(int[] one, int[] other) {
    	if (one.length == 0) return true;
    	int i = binarySearch(other, one[0]);
    	if (i < 0 || other.length - i < one.length) return false;
    	for (int j = 1; j < one.length; j++)
    		if (one[j] != other[i+j]) return false;
    	return true;
    }
    
    /**
     * Checks whether one sorted array is a sub-array of another array.
     * No duplicates are allowed in either array. 
     * If duplicates are present, the result is unspecified.
     * 
     * @param one
     * 	A sorted, duplicate-free array.
     * @param other
     * 	A sorted, duplicate-free array.
     * @return <code>true</code> iff the first argument is a sub-array
     * 	of the second; <code>false</code> otherwise.
     */
    public static boolean isSubArraySorted(int[] one, int[] other) {
    	int i = 0, j = 0;
    	while (i < one.length) {
    		j = binarySearch(other, j, other.length, one[i++]);
    		if (j ++< 0) return false;
    	}
    	return true;
    }
    
    public static <T extends Comparable<? super T>> T[] mergeSorted(T[] one, T[] other) {
    	Class<?> type = one.getClass().getComponentType();
    	
    	int i = 0, j = 0, m = one.length, n = other.length;
    	
    	@SuppressWarnings("unchecked")
        T[] result = (T[])newInstance(type, m+n);

    	while (true) {
	    	while (i < m && one[i].compareTo(other[j]) <= 0)
	    		result[i+j] = one[i++];
	    	if (i == m) {
	    		System.arraycopy(other, j, result, i+j, n-j);
				return result;
	    	}
	    	while (j < n && other[j].compareTo(one[i]) <= 0)
	    		result[i+j] = other[j++];
	    	if (j == n) {
	    		System.arraycopy(one, i, result, i+j, m-i);
				return result;
	    	}
    	}
    }
    
    public static <T extends Comparable<? super T>> T[] unionSorted(T[] one, T[] other) {
    	Class<?> type = one.getClass().getComponentType();
    	
    	int i = 0, j = 0, k = 0, m = one.length, n = other.length, o = m+n, c = -1;
    	
    	@SuppressWarnings("unchecked")
        T[] temp = (T[])newInstance(type, o);

    	while (true) {
	    	while (i < m && (c = one[i].compareTo(other[j])) <= 0) {
	    		temp[k++] = one[i++];
	    		if (c == 0) { j++; break; }
	    	}
	    	if (i == m) {
	    		System.arraycopy(other, j, temp, k, n-j);
	    		k += n-j;
				break;
	    	}
	    	while (j < n && (c = other[j].compareTo(one[i])) <= 0) {
	    		temp[k++] = other[j++];
	    		if (c == 0) { i++; break; } 
	    	}
	    	if (j == n) {
	    		System.arraycopy(one, i, temp, k, m-i);
	    		k += m-i;
				break;
	    	}
    	}

    	if (k == o) return temp;
    	@SuppressWarnings("unchecked")
    	T[] result = (T[])newInstance(type, k);
    	System.arraycopy(temp, 0, result, 0, k);
    	return result;
    }
    
    public static <T extends Comparable<? super T>> T[] intersectSorted(T[] one, T[] other) {
    	Class<?> type = one.getClass().getComponentType();
    	
    	int i = 0, j = 0, k = 0, m = one.length, n = other.length, o = m+n, c = -1;
    	
    	if (n == 0) return one;
    	if (m == 0) return other;
    	
    	@SuppressWarnings("unchecked")
        T[] temp = (T[])newInstance(type, o);

    	while (true) {
	    	while (i < m && (c = one[i].compareTo(other[j])) <= 0) {
	    		if (c == 0) { temp[k++] = one[i++]; j++; break; }
	    		i++;
	    	}
	    	if (i == m) break;
	    	while (j < n && (c = other[j].compareTo(one[i])) <= 0) {
	    		if (c == 0) { temp[k++] = other[j++]; i++; break; }
	    		j++;
	    	}
	    	if (j == n) break;
    	}

    	if (k == o) return temp;

    	@SuppressWarnings("unchecked")
    	T[] result = (T[])newInstance(type, k);
    	System.arraycopy(temp, 0, result, 0, k);
    	return result;
    }
    
    /**
     * Returns an array of the indices where non-<code>null</code>
     * elements are present in the provided array. 
     * 
     * @param array
     * 	An array.
     * @return
     * 	An array of the indices where non-<code>null</code>
     * 	elements are present in the provided array.
     */
    public static int[] getNonNullIndices(Object[] array) {
    	int j = 0, n = array.length, temp[] = new int[n];
        for (int i = 0; i < array.length; i++)
        	if (array[i] != null) temp[j++] = i;
        if (j == n) return temp;
        int[] indices = new int[j];
        if (j > 0) System.arraycopy(temp, 0, indices, 0, j);
        return indices;
    }
}