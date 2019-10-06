package runtime.hash;

import java.util.Iterator;
import java.util.NoSuchElementException;

import runtime.Constraint;

import util.Resettable;

/**
 * Derived from {@link HashIndex}.
 * 
 * @author Peter Van Weert
 */
public abstract class FDSSHashIndex<E> implements Iterable<E>, Resettable {

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
    public FDSSHashIndex(int initialCapacity, float loadFactor) {
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
    public FDSSHashIndex(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>HashIndex</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    @SuppressWarnings("unchecked")
    public FDSSHashIndex() {
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
     * (A structural modification is any operation
     * that adds or deletes one or more entries).
     * 
     * @see #safeIterator()
     */
    public Iterator<E> iterator() {
        return new HashIterator<E>(table);
    }
    
    /**
     * Returns an iterator over all entries currently in the store. Structural
     * changes to the table are <em>not</em> reflected in the iteration.
     * (A structural modification is any operation
     * that adds or deletes one or more entries).
     * 
     * @return an iterator over all entries currently in the store. Structural
     *  changes to the table are <em>not</em> reflected in the iteration.
     *  
     * @see #iterator()
     */
    public Iterator<E> safeIterator() {
        return new HashIterator<E>(table.clone());
    }
    
    protected abstract int hash(E entry);
    
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
		@SuppressWarnings("unchecked")
        int hash = (key instanceof Constraint)
        	? hash((E)key)
			: key.hashCode();
        
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
    	int hash = hash(entry);
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
    	int hash = hash(entry);
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
    	int hash = hash(entry);

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
        int hash = hash(entry);
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
        int hash = hash(entry);
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
        int i = hash(entry) & (table.length-1);
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