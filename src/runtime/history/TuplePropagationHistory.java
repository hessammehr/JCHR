package runtime.history;

import util.Resettable;

/**
 * <p>
 * Hash based propagation history, very loosely based on the general purpose 
 * {@link java.util.HashMap} implementation (version 1.72) by Doug Lea, Josh Bloch, 
 * Arthur van Hoff and Neal Gafter. 
 * For more information cf. also {@link runtime.hash.HashIndex}.
 * <p>
 * This implementation provides constant-time performance for the basic
 * operations ({@link #add(Tuple)} and {@link #contains(Tuple)}), 
 * assuming the hash function disperses the elements properly among the buckets.
 * </p>
 * <p>
 * An instance of <tt>TuplePropagationHistory</tt> has two parameters that affect its
 * performance: <i>initial capacity</i> and <i>load factor</i>.  The
 * <i>capacity</i> is the number of buckets in the hash table, and the initial
 * capacity is simply the capacity at the time the hash table is created.  The
 * <i>load factor</i> is a measure of how full the hash table is allowed to
 * get before its capacity is automatically increased.  When the number of
 * entries in the hash table exceeds the product of the load factor and the
 * current capacity, the hash table is <i>rehashed</i> (that is, internal data
 * structures are rebuilt) so that the hash table has approximately twice the
 * number of buckets.
 * </p>
 * <p>
 * As a general rule, the default load factor (.75) offers a good tradeoff
 * between time and space costs.  Higher values decrease the space overhead
 * but increase the lookup cost (reflected in most of the operations, 
 * including ({@link #add(Tuple)} and {@link #contains(Tuple)})).  
 * The expected number of entries in the map and its load factor should be taken
 * into account when setting its initial capacity, so as to minimize the
 * number of rehash operations.  If the initial capacity is greater
 * than the maximum number of entries divided by the load factor, no
 * rehash operations will ever occur.
 * </p>
 * <p>
 * If many tuples are to be stored in a <tt>TuplePropagationHistory</tt> instance,
 * creating it with a sufficiently large capacity will allow the mappings to
 * be stored more efficiently than letting it perform automatic rehashing as
 * needed to grow the table.
 * </p>
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a hash map concurrently, and at least one of
 * the threads modifies the map structurally, it <i>must</i> be
 * synchronized externally.  (A structural modification is any operation
 * that adds or deletes one or more mappings; merely changing the value
 * associated with a key that an instance already contains is not a
 * structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the map.
 *
 * @author  ( Doug Lea )
 * @author  ( Josh Bloch )
 * @author  ( Arthur van Hoff )
 * @author  ( Neal Gafter )
 * @see     java.util.HashMap
 * 
 * @author Peter Van Weert
 */
public class TuplePropagationHistory implements Resettable {
    /**
     * The default initial capacity - MUST be a power of two.
     */
    final static int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    final static int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     */
    final static float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The table, resized as necessary. Length MUST Always be a power of two.
     */
    Tuple[] table;

    /**
     * The number of key-value mappings contained in this map.
     */
    int size;

    /**
     * The next size value at which to resize (capacity * load factor).
     */
    int threshold;

    /**
     * The load factor for the hash table.
     */
    final float loadFactor;

    /**
     * Constructs an empty <tt>IdPropagationHistory</tt> with the specified initial
     * capacity and load factor.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive
     */
    @SuppressWarnings("unchecked")
    public TuplePropagationHistory(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException(
                "Illegal initial capacity: " + initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException(
                    "Illegal load factor: " + loadFactor);
        
        // Find a power of 2 >= initialCapacity
        int capacity = 1;
        while (capacity < initialCapacity)
            capacity <<= 1;

        this.loadFactor = loadFactor;
        threshold = (int)(capacity * loadFactor);
        table = new Tuple[capacity];
    }

    /**
     * Constructs an empty <tt>IdPropagationHistory</tt> with the specified initial
     * capacity and the default load factor (0.75).
     *
     * @param  initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    public TuplePropagationHistory(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>IdPropagationHistory</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    @SuppressWarnings("unchecked")
    public TuplePropagationHistory() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        table = new Tuple[DEFAULT_INITIAL_CAPACITY];
    }

    /**
     * Returns the number of entries in this index
     * (note that this number may overflow, so in theory
     * the result is the number of entries in this index
     * modulo {@link Integer#MAX_VALUE}).
     *
     * @return the number of entries in this index.
     */
    public int size() {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this history contains no tuples.
     *
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Returns whether or not this history contains the specified tuple.
     * 
     * @param tuple
     *  The tuple to look up.
     *  
     * @return <code>true</code> iff this history contains the specified tuple;
     *  <code>false</code> otherwise.
     *
     * @see #putFirstTime(Tuple)
     * @see #insert(Tuple)
     */
    public boolean contains(Tuple tuple) {
        int hash = tuple.hash;
        for (Tuple t = table[(hash & (table.length-1))]; t != null; t = t.next)
            if (t.hash == hash && t.equals(tuple)) return true;
        return false;
    }
    
    /**
     * Inserts the specified tuple in the history. Returns whether or
     * not it was already present before.
     * 
     * @param tuple
     *  The tuple to insert.
     * @return If the inserted tuple was already present, <code>false</code>
     *  is returned; else the result will be <code>true</code>.
     */
    public boolean insert(Tuple tuple) {
        int hash = tuple.hash;
        int i = hash & (table.length-1);
        
        for (Tuple t = table[i]; t != null; t = t.next)
            if (t.hash == hash && t.equals(tuple)) return false;
        
        // before returning, we do a put!
        tuple.next = table[i]; 
        table[i] = tuple;
        if (size++ > threshold) resize();
        
        return true;
    }

    /**
     * Adds the given tuple to the index. No equal tuple is already
     * stored in the index: this is an <em>essential precondition</em>
     * to this method.
     *
     * @param tuple
     *  The new tuple to put in the history.
     */
    public void add(Tuple tuple) {
        int i = tuple.hash & (table.length-1);
        tuple.next = table[i]; 
        table[i] = tuple;
        if (size++ > threshold) resize();
    }
    
    /**
     * Rehashes the contents of this map into a new array with a
     * larger capacity.
     *
     * If current capacity is MAXIMUM_CAPACITY, this method does not
     * resize the map, but sets threshold to Integer.MAX_VALUE.
     * This has the effect of preventing future calls.
     */
    private final void resize() {
        Tuple[] oldTable = table;
        int oldCapacity = oldTable.length;
        
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }
        
        int newCapacity = table.length << 1;

        Tuple[] newTable = new Tuple[newCapacity];
        
        for (int j = 0; j < oldTable.length; j++) {
            Tuple t = oldTable[j];
            if (t != null) {
                oldTable[j] = null;
                do {
                    Tuple next = t.next;
                    int i = t.hash & (newCapacity-1);
                    t.next = newTable[i];
                    newTable[i] = t;
                    t = next;
                } while (t != null);
            }
        }
        table = newTable;
        threshold = (int)(newCapacity * loadFactor);
    }

    /**
     * Removes the specified tuple from this history. 
     *
     * @param  tuple 
     *  the tuple that is to be removed from the index
     */
    public void remove(Tuple tuple) {
    	int hash = tuple.hash;
        hash ^= (hash >>> 20) ^ (hash >>> 12);
        hash = hash ^ (hash >>> 7) ^ (hash >>> 4);
        int i = hash & (table.length-1);

        size--;
        
        Tuple prev = table[i];
        if (prev.hash == hash && tuple.equals(prev)) {
        	table[i] = prev.next;
        } else {
        	Tuple current = prev.next;
    		while (current.hash != hash || !tuple.equals(current)) {
    			prev = current;
    			current = current.next;
    		}
            prev.next = current.next;
        }
    }

    /**
     * Removes all tuples from this index.
     * The index will be empty after this call returns.
     */
    public void reset() {
        Tuple[] tab = table;
        for (int i = 0; i < tab.length; i++)
            tab[i] = null;
        size = 0;
    }

    protected static class Tuple {
        Tuple next;
        int hash;
        
        @Override
    	public final int hashCode() {
        	return hash;
    	}
    }
    
    /**
     * Returns a string representation of this propagation history.  
     *
     * @return a string representation of this propagation history.
     */
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder(size >> 3);
    	Tuple[] entries = table;
    	
    	result.append('[');
    	
    	boolean first = true;
    	for (int i = 0; i < entries.length; i++) {
    		if (entries[i] != null) {
    			if (first)
    				first = false;
    			else
    				result.append(", ");
    			
				result.append(entries[i]);
    		}
    	}
    	
    	result.append(']');
    	
    	return result.toString();
    }
}