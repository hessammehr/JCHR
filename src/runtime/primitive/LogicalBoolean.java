package runtime.primitive;

import runtime.BuiltInConstraintObservable;
import runtime.Constraint;
import runtime.DoublyLinkedConstraintList;
import runtime.InstantiationException;
import runtime.Handler.RehashableKey;
import runtime.hash.HashObservable;
import runtime.hash.RehashableKeySet;
import annotations.JCHR_Coerce;
import annotations.JCHR_Declare;
import annotations.JCHR_Init;

/**
 * @author Peter Van Weert
 */
public class LogicalBoolean implements BuiltInConstraintObservable, HashObservable {
    
    protected boolean value;
    protected boolean hasValue;

    protected LogicalBoolean parent = this;

    protected int rank;

    protected DoublyLinkedConstraintList<Constraint> variableObservers;
    
    protected RehashableKeySet hashObservers; 
    
    private String name;

    private static int counter;

    @JCHR_Declare
    public LogicalBoolean() {
        // NOP
    }

    @JCHR_Declare
    public LogicalBoolean(String name) {
        this.name = name;
    }
    
    @JCHR_Init
    public LogicalBoolean(boolean value) {
        this.value = value;
        this.hasValue = true;
    }

    @JCHR_Init( identifier = 0 )
    public LogicalBoolean(String name, boolean value) {
        this.name = name;
        this.value = value;
        this.hasValue = true;
    }

    public final void addBuiltInConstraintObserver(Constraint constraint) {
        LogicalBoolean found = find();
        if (! found.hasValue) {
            DoublyLinkedConstraintList<Constraint> observers = found.variableObservers;
            if (observers == null)
                found.variableObservers = new DoublyLinkedConstraintList<Constraint>(constraint);
            else
                observers.addFirst(constraint);
        }
    }
    
    public final void addHashObserver(RehashableKey observer) {
        final LogicalBoolean found = find(); 
        if (! found.hasValue) {
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
        hasValue = false;
        rank = 0;
        parent = this;
    }
    
    public final boolean isVar() {
        return !find().hasValue;
    }

    public final boolean isGround() {
        return find().hasValue;
    }
    
    public final boolean isNonVar() {
        return find().hasValue;
    }

    @JCHR_Coerce
    public final boolean getValue() {
    	LogicalBoolean found = find();
        if (!found.hasValue)
        	throw new InstantiationException(found.getName());
    	return found.value;
    }

    public final LogicalBoolean find() {
        if (parent != this)
            return (parent = parent.find());
        else
            return this;
    }
    
    @Override
    public int hashCode() {
        return parent == this
            ? ( hasValue
                ? ( value? 1 : 0 )
                : super.hashCode() )
            : parent.hashCode();
    }
    
    public String getName() {
        if (name == null) name = "$" + counter++;
        return name;
    }

    @Override
    public String toString() {
        final LogicalBoolean found = find(); 
        if (!found.hasValue)
            return getName();
        else
            return found.value? "true" : "false";
    }
}