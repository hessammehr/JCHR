package runtime.history;

import java.util.Arrays;
import runtime.history.TuplePropagationHistory.Tuple;

import util.Resettable;

/**
 * <p>
 * Hash based propagation history, based on an open-addressing hash table 
 * with linear probing collision resolution.
 * Empirical experiments have not yet shown improvements when compared to
 * the chaining-based implementation {@link TuplePropagationHistory}.
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
 * @author Peter Van Weert
 */
public class ProbingTuplePropagationHistory implements Resettable {
    /**
     * The default initial capacity - MUST be a power of two.
     */
    private final static int DEFAULT_INITIAL_CAPACITY = 16;
    /**
     * The default initial capacity, equal to DEFAULT_INITIAL_CAPACITY * 3/4.
     */
    protected final static int DEFAULT_INITIAL_THRESHOLD = 12;

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    protected final static int MAXIMUM_CAPACITY = 1 << 30;
    
    /**
     * The table, resized as necessary. Length MUST Always be a power of two.
     */
    protected Tuple[] table;

    /**
     * The number of key-value mappings contained in this map.
     */
    protected int size;

    /**
     * The next size value at which to resize (capacity * load factor).
     */
    protected int threshold;

    /**
     * Constructs an empty <tt>IdPropagationHistory</tt> with the specified initial
     * capacity and load factor.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is non-positive
     */
    @SuppressWarnings("unchecked")
    public ProbingTuplePropagationHistory(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        
        int capacity;
        if (initialCapacity > MAXIMUM_CAPACITY)
            capacity = MAXIMUM_CAPACITY;
        else
        	// Find a power of 2 >= initialCapacity
        	for (capacity = 1; capacity < initialCapacity; capacity <<= 1);

        threshold = (3 * capacity) >> 2;
        table = new Tuple[capacity];
    }

    /**
     * Constructs an empty <tt>IdPropagationHistory</tt> with the default initial capacity
     * (16) and load factor (0.75).
     */
    @SuppressWarnings("unchecked")
    public ProbingTuplePropagationHistory() {
        threshold = DEFAULT_INITIAL_THRESHOLD;
        table = new Tuple[DEFAULT_INITIAL_CAPACITY];
    }

    /**
     * Returns the number of entries in this index.
     *
     * @return the number of entries in this index.
     */
    public int size() {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this history contains no identifiers.
     *
     * @return <tt>true</tt> if this history contains no identifiers
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Returns whether or not this history contains the specified identifier.
     * 
     * @param ID
     *  The identifier to look up.
     *  An identifier has to be a number different from zero.
     *  
     * @return <code>true</code> iff this history contains the specified identifier;
     *  <code>false</code> otherwise.
     *
     * @see #putFirstTime(int)
     * @see #insert(int)
     */
    public boolean contains(Tuple t) {
        Tuple[] table = this.table;
        int l = table.length - 1;
        int hash = t.hash & l;

        for (int j = hash; j >= 0; j--) {
        	if (table[j] == null) return false;
        	if (table[j].equals(t)) return true;
        }
        for (int j = l; j > hash; j--) {
        	if (table[j] == null) return false;
        	if (table[j].equals(t)) return true;
        }
        return false;
    }
    
    /**
     * Inserts the specified identifier in the history. 
     * Returns whether or not it was already present before.
     * 
     * @param ID
     *  The identifier to insert.
     *  An identifier has to be a number different from zero.
     * @return If the inserted identifier was already present, 
     * 	<code>false</code> is returned; 
     * else the result will be <code>true</code>.
     */
    public boolean insert(Tuple t) {
        Tuple[] table = this.table;
        int l = table.length - 1;
        int hash = t.hash & l;
        
        for (int j = hash; j >= 0; j--) {
        	if (table[j] == null) {
        		table[j] = t;
        		if (size++ >= threshold) resize();
        		return true;
        	}
        	if (table[j].equals(t)) return false;
        	
        }
        for (int j = l; j > hash; j--) {
        	if (table[j] == null) {
        		table[j] = t;
        		if (size++ >= threshold) resize();
        		return true;
        	}
        	if (table[j].equals(t)) return false;
        }

        throw new IllegalStateException();
    }

    /**
     * Adds the given identifier to the index. No equal identifier is already
     * stored in the index: this is an <em>essential precondition</em>
     * to this method.
     *
     * @param ID
     *  The new identifier to put in the history.
     *  An identifier has to be a number different from zero.
     */
    public void add(Tuple t) {
        Tuple[] table = this.table;
        int l = table.length - 1;
        int hash = t.hash & l;
        
        for (int j = hash; j >= 0; j--) {
        	if (table[j] == null) {
        		table[j] = t;
        		if (size++ >= threshold) resize();
        		return;
        	}
        }
        for (int j = l; j > hash; j--) {
        	if (table[j] == null) {
        		table[j] = t;
        		if (size++ >= threshold) resize();
        		return;
        	}
        }
    }
    
    /**
     * Rehashes the contents of this map into a new array with a
     * larger capacity.
     *
     * If current capacity is MAXIMUM_CAPACITY, this method does not
     * resize the map, but sets threshold to MAXIMUM_CAPACITY-1.
     * This has the effect of preventing future calls.
     */
    protected void resize() {
        Tuple[] oldTable = table;
        int oldCapacity = oldTable.length;
        
        if (oldCapacity == MAXIMUM_CAPACITY) {
        	if (size == MAXIMUM_CAPACITY)
        		throw new IllegalStateException("Maximum capacity exhausted.");
            threshold = MAXIMUM_CAPACITY-1;
            return;
        }
        
        int newCapacity = oldCapacity << 1;
        Tuple[] newTable = new Tuple[newCapacity];
        int l = newCapacity - 1;
        
        f0r: for (int k = 0; k < oldCapacity; k++) {
            Tuple t = oldTable[k];
            int hash = t == null? 0 : t.hash & l;
            
            for (int j = hash; j >= 0; j--) {
            	if (newTable[j] == null) {
            		newTable[j] = t;
            		continue f0r;
            	}
            }
            for (int j = l; j > hash; j--) {
            	if (newTable[j] == null) {
            		newTable[j] = t;
            		continue f0r;
            	}
            }
        }
        
        table = newTable;
        threshold = (3 * newCapacity) >> 2;
    }

    /**
     * Removes the specified identifier from this history.
     *
     * @param  ID 
     *  the identifier that is to be removed from the index.
     *  An identifier has to be a number different from zero.
     *  Note: this implementation is optimised for the case
     *  where the identifier is effectively present in the
     *  history. The other case will work, but the complexity
     *  will not be optimal (best-case linear in the size of the
     *  history, instead of worst-case linear / average case constant).
     */
    public void remove(Tuple t) {
        Tuple[] table = this.table;
        int l = table.length - 1;
        int hash = t.hash & l;

        for (int j = hash; j >= 0; j--) {
        	if (table[j].equals(t)) {
        		doRemove(j);
        		return;
        	}
        }
        for (int j = l; j > hash; j--) {
        	if (table[j].equals(t)) {
        		doRemove(j);
        		return;
        	}
        }
    }
    
    protected void doRemove(int r) {
    	size--;
    	Tuple[] table = this.table;
    	int m = r, l = table.length-1;
    	
    	/* assumption: at least one 0 occurs in table 
    	 		(otherwise outer clearly is an infinite loop) */
    	outer: do {
	    	while (--m >= 0) {	/* case 1: m < r */
	    		Tuple t = table[m];
	    		if (t == null) {
	    			table[r] = null;
	    	    	return;
	    		}
	    		int hash = t.hash & l;
	    		if (hash >= r || hash < m) {
	    			table[r] = t;
	    			r = m;
	    		}
	    	}
	    	m = l;
	    	while (true) {	/* case 2: m > r */
	    		Tuple t = table[m];
	    		if (t == null) {
	    			table[r] = null;
	    	    	return;
	    		}
	    		int hash = t.hash & l;
	    		if (hash >= r && hash < m) {
	    			table[r] = t;
	    			r = m;
	    			continue outer;
	    		}
	    		m--;
	    	}
    	} while (true);
    }

    /**
     * Removes all identifiers from this index.
     * The index will be empty after this call returns.
     */
    public void reset() {
        table = new Tuple[table.length];
        size = 0;
    }

    /**
     * Returns a string representation of this propagation history.  
     *
     * @return a string representation of this propagation history.
     */
    @Override
    public String toString() {
    	Tuple[] table = new TuplePropagationHistory.Tuple[size];
    	for (int i = 0, j = 0; j < table.length; i++)
    		if ((table[j] = this.table[i]) != null) j++;
    	return Arrays.toString(table);
    }
}