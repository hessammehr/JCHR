package runtime.hash;

import runtime.Handler.RehashableKey;
import util.Terminatable;

/*
 * idee was: dit moet sneller zijn dan een identityhashmap,
 * maar jammer genoeg valt dit tegen
 * (maar is natuurlijk niet trager)
 * 
 * nog een poging zou zijn om IdentityHashMap aan te passen
 */
public final class RehashableKeySet implements Terminatable {

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
    Entry[] table;

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
    public RehashableKeySet(int initialCapacity, float loadFactor) {
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
    public RehashableKeySet(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>HashIndex</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    public RehashableKeySet() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        table = new Entry[DEFAULT_INITIAL_CAPACITY];
    }
    
    /**
     * Constructs an empty <tt>HashIndex</tt> with the default initial capacity
     * (16) and the default load factor (0.75), and a single initial value.
     */
    public RehashableKeySet(RehashableKey key) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        table = new Entry[DEFAULT_INITIAL_CAPACITY];
        
        int hash = key.getRehashableKeyId();
        hash ^= (hash >>> 20) ^ (hash >>> 12);
        hash = hash ^ (hash >>> 7) ^ (hash >>> 4);
        table[hash & (DEFAULT_INITIAL_CAPACITY-1)] = new Entry(hash, key, null);
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
     * Adds the given entry to the index, does nothing if allready
     * in the set.
     * 
     * @param key
     *  The key to add to the store.
     */
    public void add(RehashableKey key) {
        int hash = key.getRehashableKeyId();
        hash ^= (hash >>> 20) ^ (hash >>> 12);
        hash = hash ^ (hash >>> 7) ^ (hash >>> 4);
        int i = hash & (table.length-1);
        
        for (Entry e = table[i]; e != null; e = e.next)
            if (key == e.key) return; 

        table[i] = new Entry(hash, key, table[i]);
        if (size++ >= threshold) {
            int capacity = table.length;
            if (capacity == MAXIMUM_CAPACITY) {
            	if (size == MAXIMUM_CAPACITY)
            		throw new IllegalStateException("Maximumum capacity exhausted.");
                threshold = MAXIMUM_CAPACITY-1;
            } else
            	grow(capacity << 1);
        }
    }
    
    /**
     * Rehashes the contents of this map into a new array with a
     * larger capacity.
     */
    @SuppressWarnings("unchecked")
    private final void grow(int newCapacity) {
    	Entry[] oldTable = table;
        Entry[] newTable = new Entry[newCapacity];
    	
        for (int j = 0; j < oldTable.length; j++) {
            Entry current = oldTable[j];
            if (current != null) {
                oldTable[j] = null;
                do {
                	Entry next = current.next;
                	int i = current.hash & (newCapacity-1);
                	current.next = newTable[i];
                	newTable[i] = current;
                	current = next;
                } while (current != null);
            }
        }

        table = newTable;
        threshold = (int)(newCapacity * loadFactor);
    }

    /**
     * Removes all entries from this index.
     * The index will be empty after this call returns.
     */
    public void clear() {
        Entry[] tab = table;
        for (int i = 0; i < tab.length; i++)
            tab[i] = null;
        size = 0;
    }
    
    public float trim() {
    	int sizeBefore = size;
    	
    	Entry[] table = this.table;
    	Entry current, previous;
    	
		for (int i = 0; i < table.length; i++) {
			if ((current = table[i]) != null) {
				if (current.key.isSuperfluous()) {
					do {
						current = current.next;
						size--;
					} while (current != null && current.key.isSuperfluous());
					table[i] = current;
				}
				
				if (current != null) {
    				previous = current;
    				while ((current = current.next) != null) {
    					if (current.key.isSuperfluous()) {
    						do {
        						current = current.next;
        						size--;
        					} while (current != null && current.key.isSuperfluous());
    						
    						if ((previous.next = current) != null)
    							previous = current;
    						else
    							break;
    					}
    				}
				}
			}
		}
		
		return ((sizeBefore-size) / sizeBefore);
    }
    
    public void terminate() {
    	table = null;
    }
    public boolean isTerminated() {
    	return (table == null);
    }

    private final static class Entry {
        final RehashableKey key;
        Entry next;
        final int hash;

        /**
         * Creates new entry.
         */
        Entry(int hash, RehashableKey entry, Entry next) {
            this.key = entry;
            this.next = next;
            this.hash = hash;
        }

        @Override
        public final String toString() {
            return key.toString();
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
    	Entry[] entries = table;
    	
    	result.append('{');
    	
    	boolean first = true;
    	for (int i = 0; i < entries.length; i++) {
    		if (entries[i] != null) {
    			if (first)
    				first = false;
    			else
    				result.append(", ");
    			
				result.append('@').append(Integer.toHexString(entries[i].hash)).append(" = ").append(entries[i].key);
    		}
    	}
    	
    	result.append('}');
    	
    	return result.toString();
    }
    
    /**
     * Rehashes all {@link RehashableKey}s in this set.
     * After the call to this method, all keys that have become
     * superfluous are removed from the set.
     * 
     * @see #justRehashAll()
     */
    public void rehashAll() {
    	Entry[] table = this.table;
    	outer: for (int i = 0; i < table.length; i++) {
    		Entry current = table[i];
    		if (current != null) {
    			if (!current.key.rehash()) {
    				do {
    					size--;
    					if ((current = current.next) == null) {
    						table[i] = null;	// niets behouden
    						continue outer;
    					}
    				} while (!current.key.rehash());
					table[i] = current;	// eerste behouden gevonden
    			}
    			Entry last = current;	// laatste behouden
	    		while ((current = current.next) != null) 
    				if (current.key.rehash())
    					last = last.next = current;	// nieuwe behouden
    				else
    					size--;
    		}
    	}
    }
    
    /**
     * Rehashes all {@link RehashableKey}s in this set.
     * After the call to this method, the keys that have become
     * superfluous are still present in the set.
     * This is perfectly acceptable if it is for instance known 
     * the set will be disposed of after the call to this method. 
     * 
     * @see #rehashAll()
     */
    public void justRehashAll() {
    	Entry[] table = this.table;
    	for (int i = 0; i < table.length; i++) {
    		Entry current = table[i];
    		while (current != null) {
    			current.key.rehash();
    			current = current.next;
    		}
    	}
    }
    
    /**
     * Mengt deze verzameling met de sleutels uit <code>other</code>. 
     * De sleutels waarvan de hash gewijzigd is moeten worden geherpositioneerd 
     * in hun hash-map. Hierbij kan het gebeuren dat de oude sleutel en 
     * dus de oude observers overbodig geworden zijn.
     * Merk op dat gegeven de volgende invariant:
     * <div>
     * <pre>
     *  eq(var1, var2) 
     *      ==> hash(var1) == hash(var2) 
     *      ==> hashobserverlist(var1) == hashobserverlist(var2)
     * </pre>
     * </div>
     * geldt dat dit de enige observers zijn die overbodig zijn nu. Dit is
     * een sterkere veronderstelling dan de eerste implicatie (die uiteraard
     * noodzakelijk is), maar is best aanvaardbaar: het tegendeel zou
     * ineffici&euml;nt zijn.
     * 
     * @pre The hash-values of the keys in <em>this</em> set have <em>not</em>
     *  been changed: i.e. no rehashing has to be done here!
     * @post The other key-set is terminated.
     */
    @SuppressWarnings("unchecked")
    public void mergeWith(RehashableKeySet other) {
    	Entry[] thisTable = this.table,
    		otherTable = other.table;
    	other.table = null;	// terminate other table (its entries may be reused)
    	
    	int expectedSize = this.size + other.size;
    	int capacity = thisTable.length;
    	if (expectedSize >= threshold) do {
			if (capacity == MAXIMUM_CAPACITY) break;
			if (expectedSize < (int)((capacity <<= 2) * loadFactor)) {
				// avoid growing too much (risks an extra resize, but at most one)
		    	capacity >>= 2;
				break;
			}
		} while (true);
    	
		if (thisTable.length < capacity) grow(capacity);
    	
    	for (int j,i = 0; i < otherTable.length; i++) {
    		Entry otherCurrent = otherTable[i];
    		others: while (otherCurrent != null) {
    			if (otherCurrent.key.rehash()) {
    		        Entry first = table[j = otherCurrent.hash & (capacity-1)];
    		        for (Entry thisCurrent = first; thisCurrent != null; thisCurrent = thisCurrent.next)
    		            if (otherCurrent.key == thisCurrent.key) {
    		            	otherCurrent = otherCurrent.next;
    		            	continue others;
    		            }
    		        if (size ++>= threshold) {	// happens at most once
    		        	if (size == MAXIMUM_CAPACITY)
    		        		throw new IllegalStateException("Maximal capacity exhausted");
    		        	grow(capacity <<= 2);
    		        	first = table[j = otherCurrent.hash & (capacity-1)];
    		        }
    		        table[j] = otherCurrent;
    		        Entry otherNext = otherCurrent.next; 
    		        otherCurrent.next = first;
    		        otherCurrent = otherNext;
    			} else {
    				otherCurrent = otherCurrent.next;
    			}
    		}
    	}
    }
}