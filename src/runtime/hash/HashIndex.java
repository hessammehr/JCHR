package runtime.hash;

import java.util.Iterator;
import java.util.NoSuchElementException;

import util.Resettable;

/*
 * IDEAS
 *  - maybe we could use double linked entry lists and have entries 
 *      remove themselves (i.e. no more lookup needed)?
 *  - move the second level hash function to the elements themselves:  
 *      . no more need for extra field in entry (downside: extra indirection)
 *      . no more recalculation of second level hash if not necessary (DONE)
 *  - merge Entry and actual entry classes in some cases
 */

/**
 * <p>
 * Special purpose hash index, loosely based on the general purpose {@link java.util.HashMap} 
 * implementation (version 1.72) by Doug Lea, Josh Bloch, Arthur van Hoff 
 * and Neal Gafter. Notable changes include:
 * </p>
 * <ul>
 * <li>All non-essential code has been stripped and/or inlined.</li>
 * <li>
 * Domain-specific precondintions have been taken into account: keys
 * can never be <code>null</code>, keys generally never have been 
 * inserted before, ... This allowed us to avoid several tests
 * </li>
 * <li>
 * Keys and values are merged into entries (turning it more into a set
 * than a map really). You can iterate over all entries in the index.
 * </li>
 * <li>
 * The {@link Iterator}s returned by {@link #iterator()} are not fail-safe. 
 * (i.e.: take care when using this class, see also {@link #safeIterator()}!), 
 * </li>
 * <li>
 * Extra, more specific methods have been added
 * </li>
 * </ul>
 * <p>
 * In fact, not much is left from the original code. Or, to paraphrase Peter Lin:
 * <br/>
 * <q>If java.util.HashMap was a women, 
 * this custom hashtable would be naked. 
 * no bikini, no g-string.</q>
 * <br/>
 * </p>
 * <p>
 * This implementation provides constant-time performance for the basic
 * operations (<tt>get</tt> and <tt>put</tt>), assuming the hash function
 * disperses the elements properly among the buckets.  Iteration over
 * the entire index requires time proportional to the "capacity" of the
 * <tt>HashIndex</tt> instance (the number of buckets) plus its size (the number
 * of entries). Thus, it's important not to set the initial
 * capacity too high (or the load factor too low) 
 * if iteration performance is important.
 * </p>
 * <p>
 * An instance of <tt>HashIndex</tt> has two parameters that affect its
 * performance: <i>initial capacity</i> and <i>load factor</i>.  The
 * <i>capacity</i> is the number of buckets in the hash table, and the initial
 * capacity is simply the capacity at the time the hash table is created.  
 * The <i>load factor</i> is a measure of how full the hash table is allowed to
 * get before its capacity is automatically increased.  When the number of
 * entries in the hash table exceeds the product of the load factor and the
 * current capacity, the hash table is <i>rehashed</i> (that is, internal data
 * structures are rebuilt) so that the hash table has approximately twice the
 * number of buckets.
 * </p>
 * <p>
 * As a general rule, the default load factor (.75) offers a good tradeoff
 * between time and space costs.  Higher values decrease the space overhead
 * but increase the lookup cost (reflected in most of the operations of the
 * <tt>HashIndex</tt> class, including <tt>get</tt> and <tt>put</tt>).  The
 * expected number of entries in the index and its load factor should be taken
 * into account when setting its initial capacity, so as to minimize the
 * number of rehash operations.  If the initial capacity is greater
 * than the maximum number of entries divided by the load factor, no
 * rehash operations will ever occur.
 * </p>
 * <p>
 * If many entries are to be stored in a <tt>HashIndex</tt> instance,
 * creating it with a sufficiently large capacity will allow the entries to
 * be stored more efficiently than letting it perform automatic rehashing as
 * needed to grow the table.
 * </p>
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a hash index concurrently, and at least one of
 * the threads modifies the index structurally, it <i>must</i> be
 * synchronized externally.  (A structural modification is any operation
 * that adds or deletes one or more entries.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the index.
 *
 * @param <E> the type of entries
 *
 * @author  ( Doug Lea )
 * @author  ( Josh Bloch )
 * @author  ( Arthur van Hoff )
 * @author  ( Neal Gafter )
 * @see     java.util.HashMap
 * 
 * @author Peter Van Weert
 */
public final class HashIndex<E> implements Iterable<E>, Resettable {

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
    Entry<E>[] table;

    /**
     * The number of key-value entries contained in this index.
     */
    int size;

    /**
     * The next size value at which to resize (capacity * load factor).
     */
    int threshold;

    /**
     * The load factor for the hash index.
     */
    final float loadFactor;

    /**
     * Constructs an empty <tt>HashIndex</tt> with the specified initial
     * capacity and load factor.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive
     */
    @SuppressWarnings("unchecked")
    public HashIndex(int initialCapacity, float loadFactor) {
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
        table = new Entry[capacity];
    }

    /**
     * Constructs an empty <tt>HashIndex</tt> with the specified initial
     * capacity and the default load factor (0.75).
     *
     * @param  initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    public HashIndex(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>HashIndex</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    @SuppressWarnings("unchecked")
    public HashIndex() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        table = new Entry[DEFAULT_INITIAL_CAPACITY];
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
     * Returns <tt>true</tt> if this index contains no entries.
     *
     * @return <tt>true</tt> if this index contains no entries
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * {@inheritDoc}
     * <br/>
     * <em>The returned iterator is not fail-safe under structural modifications!</em>
     * (A structural modification is any operation that adds or deletes one or more entries).
     * 
     * @see #safeIterator()
     */
    public Iterator<E> iterator() {
        return new HashIterator<E>(table);
    }
    
    /**
     * Returns the entry to which the specified &quot;key&quot; is 
     * &quot;mapped&quot;, or, in other words, an entry that is equal 
     * to the given key and contained in the index, 
     * or {@code null} if this is not the case.
     * 
     * <p>More formally, if this index contains an entry {@code e} 
     * such that {@code key.equals(e))}, then this method returns 
     * {@code e}; otherwise it returns {@code null}.  
     * (There can be at most one such entry.)
     * 
     * @param key
     *  The key to look up.
     *
     * @see #putFirstTime(Object)
     * @see #insert(Object)
     */
    public E get(Object key) {
        int hash = key.hashCode();
        
        for (Entry<E> e = table[(hash & (table.length-1))];
             e != null;
             e = e.next
         ) {
            E k;
            if (e.hash == hash && key.equals(k = e.entry))
                return k;
        }
        return null;
    }
    
    /**
     * If an equal entry was already present, the latter entry is returned,
     * and the method behaves as a {@link #get(Object)} operation.
     * If no equal entry is present, the given <code>entry</code> is
     * inserted (i.e. the method behaves as a {@link #putFirstTime(Object)}), 
     * and <code>null</code> is returned. 
     * 
     * @param entry
     *  The entry to insert if no equal entry is already present.
     * @return If an equal entry was already present, the latter entry 
     *  is returned; else the result will be <code>null</code>.
     */
    public E insertOrGet(E entry) {
    	int hash = entry.hashCode();
        int i = hash & (table.length-1);

        E old;
        for (Entry<E> e = table[i]; e != null; e = e.next)
            if (e.hash == hash && entry.equals(old = e.entry))
                return old;

        table[i] = new Entry<E>(hash, entry, table[i]);
        if (size++ >= threshold) grow();
        
        return null;
    }
    
    /**
     * If no equal entry is present, the given <code>entry</code> is
     * inserted (i.e. the method behaves as a {@link #putFirstTime(Object)}), 
     * and <code>true</code> is returned.
     * If an equal entry was already present, the latter is retained 
     * and <code>false</code> is simply returned without any changes
     * to the index structure. 
     * 
     * @param entry
     *  The entry to insert if no equal entry is already present.
     * @return <code>true</code> if the entry was successfully inserted
     * 	in the index; <code>false</code> if an equal entry was already present 
     */
    public boolean insert(E entry) {
    	int hash = entry.hashCode();
        int i = hash & (table.length-1);

        for (Entry<E> e = table[i]; e != null; e = e.next)
            if (e.hash == hash && entry.equals(e.entry))
                return false;

        table[i] = new Entry<E>(hash, entry, table[i]);
        if (size++ >= threshold) grow();
        
        return true;
    }
    
    /**
     * Checks whether an equal entry is already present in this index.
     * 
     * @param entry
     * 	The entry to check.
     * @return If an equal entry is already present in this index,
     * 	the result is <code>true</code>, otherwise, the result is <code>false</code>. 
     */
    public boolean contains(E entry) {
    	int hash = entry.hashCode();

        for (Entry<E> e = table[hash & (table.length-1)]; e != null; e = e.next)
            if (e.hash == hash && entry.equals(e.entry))
                return true;

        return false;
    }
    
    /**
     * Adds the given entry to the index. No equal entry is already
     * stored in the index: this is an <em>essential precondition</em>
     * to this method. If this is not guaranteed, maybe {@link #putAndGet(Object)}
     * is what you need.   
     *
     * @param entry
     *  The new entry put in the store.
     *  
     * @see #putAndGet(Object)
     */
    public void putFirstTime(E entry) {
        int hash = entry.hashCode();
        int i = hash & (table.length-1);
        
        table[i] = new Entry<E>(hash, entry, table[i]);
        if (size++ >= threshold) grow();
    }
    
    /**
     * Adds the given entry to the index, or replaces an equal entry 
     * already stored in the index. If you are sure no equal entry is
     * already stored, using the {@link #putFirstTime(Object)} method is more 
     * efficient.
     * 
     * @param entry
     *  The entry to add to the store.
     * @return The replaced, equal entry if such an entry was present,
     *  or <code>null</code> otherwise.
     */
    public E putAndGet(E entry) {
        int hash = entry.hashCode();
        int i = hash & (table.length-1);
        
        E old;
        for (Entry<E> e = table[i]; e != null; e = e.next)
            if (e.hash == hash && entry.equals(old = e.entry)) {
                e.entry = entry;
                return old;
            }

        table[i] = new Entry<E>(hash, entry, table[i]);
        if (size++ >= threshold) grow();
        
        return null;
    }
    
    /**
     * Rehashes the contents of this map into a new array with a
     * larger capacity.
     *
     * If current capacity is MAXIMUM_CAPACITY, this method does not
     * resize the map, but sets threshold to Integer.MAX_VALUE.
     */
    @SuppressWarnings("unchecked")
    private final void grow() {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }
            
        int newCapacity = 2 * oldCapacity;
        
        Entry<E>[] newTable = new Entry[newCapacity];
            
        for (int j = 0; j < oldTable.length; j++) {
            Entry<E> e = oldTable[j];
            if (e != null) {
                oldTable[j] = null;
                do {
                    Entry<E> next = e.next;
                    int i = e.hash & (newCapacity-1);
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
        table = newTable;
        threshold = (int)(newCapacity * loadFactor);
    }

    /**
     * Removes the specified entry from this index. A <em>specific
     * precondition</em> to this method is that the provided
     * entry is in fact contained by the index (entries are
     * only compared using reference comparison 
     * (i.e. <code>==</code>) by this method).
     *
     * @param  entry 
     *  the entry that is to be removed from the index
     * @throws NullPointerException
     * 	if the entry was not contained by the index at all
     */
    public void remove(E entry) {
        int i = entry.hashCode() & (table.length-1);
        size--;
        
        Entry<E> prev = table[i];
        if (prev.entry == entry) {
        	table[i] = prev.next;
        } else {
	        Entry<E> current = prev.next;
	        while (current.entry != entry) {
	            prev = current;
	            current = current.next;
	        }
	        prev.next = current.next;
        }
    }

    /**
     * Removes all entries from this index.
     * The index will be empty after this call returns.
     */
    public void reset() {
        Entry<E>[] tab = table;
        for (int i = 0; i < tab.length; i++) tab[i] = null;
        size = 0;
    }

    private final static class Entry<E> {
        E entry;
        Entry<E> next;
        final int hash;

        /**
         * Creates new entry.
         */
        Entry(int h, E entry, Entry<E> next) {
            this.entry = entry;
            this.next = next;
            hash = h;
        }

        @Override
        public final String toString() {
            return entry.toString();
        }
    }

    private final static class HashIterator<E> implements Iterator<E> {
        Entry<E> next;    // next entry to return
        int index;        // current slot
        final Entry<E>[] table;

        HashIterator(Entry<E>[] table) {
            this.table = table;
            /* we know table.length is not 0 since 0 is not a power of two */
            while ((next = table[index]) == null && ++index < table.length);
        }

        public final boolean hasNext() {
            return next != null;
        }

        public E next() {
            Entry<E> e = next, next;
            if (e == null)
                throw new NoSuchElementException();
            if ((next = e.next) == null)
                while (++index < table.length && (next = table[index]) == null);
            this.next = next;
            return e.entry;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    
    /**
     * Returns a string representation of this index.  
     *
     * @return a string representation of this index
     */
    @Override
    public String toString() {
    	StringBuilder result = new StringBuilder();
    	Entry<E>[] entries = table;
    	
    	result.append('{');
    	
    	boolean first = true;
    	for (int i = 0; i < entries.length; i++) {
    		if (entries[i] != null) {
    			if (first)
    				first = false;
    			else
    				result.append(", ");
    			
				result.append('@').append(Integer.toHexString(entries[i].hash)).append(" = ").append(entries[i].entry);
    		}
    	}
    	
    	result.append('}');
    	
    	return result.toString();
    }
}