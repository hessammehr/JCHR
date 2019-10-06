package runtime;

import runtime.Handler.RehashableKey;
import runtime.hash.HashObservable;
import runtime.hash.RehashableKeySet;
import annotations.JCHR_Asks;
import annotations.JCHR_Coerce;
import annotations.JCHR_Declare;
import annotations.JCHR_Init;

/**
 * Represents a typed logical variable. 
 * 
 * @param <T>
 *  The type of the typed logical variables: logical variables of type <code>T</code>
 *  can be told to be equal to a <i>value</i> of type <code>T</code>. 
 *  It is important that the specified type is a value. For more information
 *  on value types, we refer to the JCHR user's manual. In short this means: 
 *  <ul>
 *  <li>
 *  The state of the object can never change in a way that a guard depending 
 *  on it could become true or false (generally this means that it cannot change
 *  in such a way that it affects public inspectors).
 *  </li>
 *  <li>
 *  Once two objects of the type are equal, they remain equal. In other words
 *  its {@link T#equals(Object)} method is <em>monotonic</em>. This is a slightly
 *  less strong condition then the <em>consistency</em> property demanded by
 *  the specification of {@link Object#equals(Object)}.
 *  </li>
 *  <li>
 *  Its hash-value cannot change.
 *  </li>
 *  </ul>
 *  Note that the latter two conditions generally are a consequence of the first
 *  one. If these conditions are not met, i.e. if <code>T</code> is not a value type,
 *  this implementation will not be correct. This is a consequence of the 
 *  transitive modified problem (cf. manual).  
 * 
 * @author Peter Van Weert
 */
//@JCHR_Constraints({
//        @JCHR_Constraint(identifier = "ground", arity = 1),
//        @JCHR_Constraint(identifier = "var"   , arity = 1),
//        @JCHR_Constraint(identifier = "nonvar", arity = 1),
//})
public class Logical<T> implements BuiltInConstraintObservable, HashObservable {
    
    protected T value;

    protected Logical<T> parent = null;

    protected int rank;

    protected DoublyLinkedConstraintList<Constraint> variableObservers;
    
    protected RehashableKeySet hashObservers; 
    
    private String name;

    private static int counter;

    @JCHR_Declare
    public Logical() {
        // NOP
    }

    @JCHR_Declare
    public Logical(String name) {
        this.name = name;
    }
    
    @JCHR_Init
    public Logical(T value) {
        this.value = value;
    }

    @JCHR_Init( identifier = 0 )
    public Logical(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public void addBuiltInConstraintObserver(Constraint constraint) {
        Logical<T> found = find();
        if (found.value == null) {
        	DoublyLinkedConstraintList<Constraint> observers = found.variableObservers;
            if (observers == null)
                found.variableObservers = new DoublyLinkedConstraintList<Constraint>(constraint);
            else
                observers.addFirst(constraint);
        }
    }
    
    public void addHashObserver(RehashableKey observer) {
        final Logical<T> found = find(); 
        if (found.value == null) {
            RehashableKeySet observers = found.hashObservers;
            if (observers == null)
                found.hashObservers = new RehashableKeySet(observer);
            else
                observers.add(observer);
        }
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
        value = null;
        rank = 0;
        parent = null;
    }
    
    @JCHR_Asks("var")
    public final boolean isVar() {
        return (find().value == null);
    }

    @JCHR_Asks("ground")
    public final boolean isGround() {
        return (find().value != null);
    }
    
    @JCHR_Asks("nonvar")
    public final boolean isNonVar() {
        return (find().value != null);
    }

    @JCHR_Coerce
    public final T getValue() {
        T result = find().value;
        if (result == null) 
        	throw new InstantiationException(getName());
        return result;
    }

    public final Logical<T> find() {
    	if (parent == null) return this;
    	Logical<T> root = parent, temp;
    	while ((temp = root.parent) != null) root = temp;
        Logical<T> current = this;
        do {
        	temp = current.parent;
        	current.parent = root;
        } while ((current = temp) != root);
        return root;
    }
    
    @Override
    public int hashCode() {
    	if (parent == null)
    		return value == null? super.hashCode() : value.hashCode();
		else
			return find().hashCode();
    }
    
    public String getName() {
        if (name == null) name = "$" + counter++;
        return name;
    }

    @Override
    public String toString() {
        final T value = find().value;
        if (value == null)
            return getName();
        else
            return value.toString();
    }
}