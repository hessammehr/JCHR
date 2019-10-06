package runtime;

import runtime.Handler.RehashableKey;
import runtime.hash.HashObservable;
import runtime.hash.RehashableKeySet;
import annotations.JCHR_Asks;
import annotations.JCHR_Declare;

/**
 * Represents a untyped logical variable, which can never be bound to a value.
 * Only aliassing of free logical variables is possible. 
 * 
 * @author Peter Van Weert
 */
public class FreeLogical implements BuiltInConstraintObservable, HashObservable {
    
    protected FreeLogical parent = null;

    protected int rank;

    protected DoublyLinkedConstraintList<Constraint> variableObservers;
    
    protected RehashableKeySet hashObservers; 
    
    private String name;

    private static int counter;

    @JCHR_Declare
    public FreeLogical() {
        // NOP
    }

    @JCHR_Declare
    public FreeLogical(String name) {
        this.name = name;
    }

    public void addBuiltInConstraintObserver(Constraint constraint) {
        FreeLogical found = find();
    	DoublyLinkedConstraintList<Constraint> observers = found.variableObservers;
        if (observers == null)
            found.variableObservers = new DoublyLinkedConstraintList<Constraint>(constraint);
        else
            observers.addFirst(constraint);
    }
    
    public void addHashObserver(RehashableKey observer) {
        final FreeLogical found = find(); 
        RehashableKeySet observers = found.hashObservers;
        if (observers == null)
            found.hashObservers = new RehashableKeySet(observer);
        else
            observers.add(observer);
    }
    
    public void rehashAll() {
        if (hashObservers != null) hashObservers.rehashAll();
    }
    
    public void rehashAllAndDispose() {
    	if (hashObservers != null) {
    		hashObservers.justRehashAll();
    		hashObservers = null;
    	}
    }
    
    /**
     * {@inheritDoc}
     * <br/>
     * As the result will be stored in this <code>Logical</code>,
     * it is clearly the case that this <code>Logical</code> will
     * be the <em>new</em> representative. Also: this was already
     * a representative of course, so these hashes will remain 
     * the same, unless we have received a value we didn't have
     * before (in this case rehashing has to be done elsewhere, as
     * noted before).
     */
    public void mergeHashObservers(RehashableKeySet others) {
        // the trivial case where we do not have observers yet:
        if (hashObservers == null) {
            hashObservers = others;
            // these hashes could have just changed now of course!
            others.justRehashAll();
        } 
        // else we have the non-trivial case: 
        else hashObservers.mergeWith(others);
    }
    
    public RehashableKeySet getHashObservers() {
        return hashObservers;
    }
    
    public void reset() {
        hashObservers = null;
        if (variableObservers != null) 
            variableObservers.reset();
        rank = 0;
        parent = null;
    }
    
    @JCHR_Asks("var")
    public final boolean isVar() {
        return true;
    }

    @JCHR_Asks("ground")
    public final boolean isGround() {
        return false;
    }
    
    @JCHR_Asks("nonvar")
    public final boolean isNonVar() {
        return false;
    }

    public final FreeLogical find() {
    	if (parent == null) return this;
    	FreeLogical root = parent, temp;
    	while ((temp = root.parent) != null) root = temp;
        FreeLogical current = this;
        do {
        	temp = current.parent;
        	current.parent = root;
        } while ((current = temp) != root);
        return root;
    }
    
    @Override
    public int hashCode() {
    	if (parent == null)
    		return super.hashCode();
		else
			return find().hashCode();
    }
    
    public String getName() {
    	if (parent == null) {
    		if (name == null) name = "$" + counter++;
    		return name;
    	}
    	FreeLogical root = parent, temp;
    	while ((temp = root.parent) != null) root = temp;
    	if (root.name == null) root.name = "$" + counter++;
        return root.name;
    }

    @Override
    public String toString() {
        return getName();
    }
}