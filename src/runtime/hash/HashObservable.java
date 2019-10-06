package runtime.hash;

import runtime.Handler.RehashableKey;


public interface HashObservable {

    void addHashObserver(RehashableKey observer);
    
    RehashableKeySet getHashObservers();
    
    /**
     * @see RehashableKeySet#rehashAll()
     * @see RehashableKey#rehash()
     */
    void rehashAll();
    
    /**
     * @see #rehashAll()
     * @see RehashableKeySet#justRehashAll()
     * @see RehashableKey#rehash()
     */
    void rehashAllAndDispose();
    
    /**
     * Merges the hash-observers of the implementing object with those given
     * in <code>set</code>. 
     * During this merge rehashing of the keys in <code>set</code> gets done.
     * Rehashing of our own keys has to be done elsewhere if necessary.
     * 
     * @pre others != null
     *  
     * @param set
     *  The set of {@link RehashableKey}s to be merged into our 
     *  own set.
     *
     * @see RehashableKey#rehash()
     */
    void mergeHashObservers(RehashableKeySet set);
    
}