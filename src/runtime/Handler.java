package runtime;

import java.util.Iterator;

import runtime.debug.Tracer;
import util.Resettable;
import util.collections.AbstractUnmodifiableCollection;
import util.iterator.Filtered;
import util.iterator.FilteredIterable;
import util.iterator.Filtered.Filter;
import annotations.JCHR_Constraint;
import annotations.JCHR_Constraints;

/**
 * <p>
 * The K.U.Leuven JCHR compiler generates for each declared JCHR
 * handler a subclass of this class, {@link Handler}. 
 * These subclasses contain methods to tell the user-defined 
 * constraints they handle, and to inspect their constraint store.
 * </p>
 * <p><a id="accessible"></a>
 * Each handler is a (unmodifiable) collection of constraints, but
 * since constraints can have access modifiers, care must be taken not
 * to brake encapsulation: the constraints included in the collection's 
 * methods (like {@link #iterator()}, {@link #size()}, ...)  are only 
 * those that are <em>accessible</em>:
 * <ul> 
 * <li>
 *  For a public handler class, this will only be the <code>public</code> 
 *  (and <code>local</code>) constraints.
 *  If you have declared other constraints to have <code>protected</code>
 *  or default access, the generated code will also include
 *  <code>includeProtected()</code> and/or <code>includePackage()</code>
 *  methods to get a less publicly accessible view of the handler,
 *  which will also include the less accessible constraints in its
 *  collection.
 * </li>
 * <li>
 *  For a handler with default access, the collection includes the 
 *  constraints with default and <code>protected</code> access as well.
 * </li>
 * </ul>
 * Note that <code>private</code> constraints are never exposed in this
 * encapsulation scheme. 
 * <!-- 
 * Depending on debugging settings we do expose them sometimes
 * through a method called <code>includePrivate()</code>, but this will
 * not be done by standard compilation. 
 * --> 
 * </p>
 *  
 * @author Peter Van Weert
 */
public abstract class Handler extends AbstractUnmodifiableCollection<IConstraint> implements Resettable {
    
	protected Handler() {
		this(ConstraintSystem.get());
	}
	protected Handler(ConstraintSystem constraintSystem) {
		$$constraintSystem = constraintSystem;
		$$continuationQueue = constraintSystem.QUEUE;
	}
	
	protected final ConstraintSystem $$constraintSystem;
	protected final ContinuationQueue $$continuationQueue;
	public final ConstraintSystem getConstraintSystem() {
		return $$constraintSystem;
	}
	protected final ContinuationStack getContinuationStack() {
		return $$constraintSystem.STACK; 
	}
	
	protected Continuation dequeue() { return $$constraintSystem.dequeue(); }
	protected Continuation dequeue(Continuation continuation) { return $$constraintSystem.dequeue(continuation); }
	protected void enterHostLanguageMode() { $$constraintSystem.hostLanguageMode = true; }
	protected void exitHostLanguageMode() { $$constraintSystem.hostLanguageMode = false; }
	
	public abstract String getIdentifier();
    
    /**
     * <p>
     * Returns an iterator over all <em>accessible</em> constraints currently in 
     * the store, with accessible as defined <a href="#accessible">here</a>. 
     * The <code>Iterator.remove()</code> method is not supported by 
     * the iterator that is returned. The guarantees (or lack thereof) about its 
     * behavior are completely analogous to those of iterators returning 
     * iterators over individual constraints:
     * </p>
     * <ul>
     *  <li>
     *      There are no guarantees concerning the order in which the constraints 
     *      are returned.
     *  </li>
     *  <li>
     *      The iterators <em>might</em> fail if the constraint store is structurally modified
     *      at any time after the <code>Iterator</code> is created. In the face of concurrent modification
     *      it cannot recover from, the <code>Iterator</code> fails quickly and cleanly (throwing a
     *      <code>ConcurrentModificationException</code>), rather than risking arbitrary, 
     *      non-deterministic behavior at an undetermined time in the future.
     *      <br/>
     *      The <i>fail-fast</i> behavior of the <code>Iterator</code> is not guaranteed,
     *      even for single-threaded applications (this constraint is inherited from the 
     *      <a href="http://java.sun.com/j2se/1.5.0/docs/guide/collections/">Java Collections Framework</a>).
     *      and should only be used to detect bugs. 
     *      <br/>
     *      Important is that, while <code>Iterator</code>s returned by collections of the 
     *      Java Collections Framework generally &quot;fail fast on a best-effort basis&quot;, 
     *      this is not the case with our <code>Iterator</code>s. On the contrary: our
     *      iterators try to recover from structural changes &quot;on a best-effort basis&quot;,
     *      and fail cleanly when this is not possible (or possibly to expensive). So,
     *      in general you can get away with many updates on the constraint store during
     *      iterations (there is no way of telling which will fail though...)
     *  </li>
     *  <li>
     *      As a general note: structural changes between calls of hasNext() and next()
     *      are a bad idea: this easily leads to <code>ConcurrentModificationException</code>s.
     *  </li>
     *  <li>
     *      The failure of the <code>Iterator</code> might only occur some time after
     *      the structural modification was done: this is again because many parts
     *      of the constraint store are iterable in the presence of modification.
     *  </li>
     *  <li>
     *      When a constraint is added to the constraint store after the creation of the
     *      iterator it is possible it appears somewhere later in the iteration, but
     *      it is equally possible it does not.
     *  </li>
     *  <li>
     *      Removal of constraints on the other hand does mean the iterator will never return
     *      this constraint.
     *      Note that it still remains possible that the iterator fails somewhere after
     *      (and because of) this removal.
     *  </li>
     * </ul>
     * <p>
     * This lack of guarantees is intentional. Some <i>Iterator</i>s might behave perfectly 
     * in the presence of constraint store updates, whilst others do not. Some might return
     * constraints in order of their creation (and only iterate over constraints that existed
     * at the time of their creation), others do not. In fact: it is perfectly possible that 
     * their behavior changes between two compilations (certainly when moving to a new version
     * of the compiler). This is the price (and at the same time the bless) of declarative 
     * programming: it is the compiler that chooses the data structures that seem optimal 
     * to him at the time!
     * </p>
     *
     * @return An iterator over <em>all</em> constraints currently in the store.
     */
    public abstract Iterator<IConstraint> lookup();
    
    /**
     * This method is the equivalent of the <code>lookup</code>-method,
     * allowing a handler to be the target of the "foreach" statement.
     * We refer to the former method for more information about the
     * behavior of the <code>Iterator</code> that is returned.
     * 
     * @return An iterator over all <em>accessible</em> constraints 
     *  currently in the store. 
     *  The term ``accessible'' is defined <a href="#accessible">here</a>.  
     * 
     * @see #lookup()
     */
    @Override
    public abstract Iterator<IConstraint> iterator();
    
    /**
     * Returns a filtered iterable view over the <em>accessible</em>
     * constraints currently in the constraint store.
     * The term ``accessible'' is defined <a href="#accessible">here</a>. 
     * The same limitations apply as with other iterators. 
     * Also, if the filter tests on a condition that is altered during 
     * an iteration, the behavior of the iterator is not defined.
     * 
     * @return A filtered iterable view over the <em>accessible</em>
     *  constraints currently in the constraint store.
     *  The term ``accessible'' is defined <a href="#accessible">here</a>. 
     *  
     * @see #lookup()
     */
    public Iterable<IConstraint> filter(Filter<? super IConstraint> filter) {
        return new FilteredIterable<IConstraint>(this, filter);
    }
    
    /*                                             *\
       Some simple key types used for hash indices 
    \*                                             */
    
    public static interface Key {/* empty interface */}
    
    public static interface RehashableKey extends Key {
        /**
         * Rehashing means recalculating the keys hash-value and making sure
         * it remains in a correct hash-bucket. Should only be called if 
         * the hash of one of the components that make up the hash code
         * has changed.
         * 
         * @return true if and only if the key is still used after rehashing.
         *  A key will no longer be used if:
         *  <ul>
         *      <li>There are currently no constraints behind it anymore.</li>
         *      <li>
         *          After rehashing (and reinsertion probably) an equal key
         *          (not identical of course, only when we're very unlucky...)
         *          was already present and the current one is no longer used.
         *      </li>
         *  </ul>
         */
        public boolean rehash();
        
        public boolean isSuperfluous();
        
        public int getRehashableKeyId();
    }
    
    protected interface LookupKey extends Key {/* again, an empty interface */}
    
    public JCHR_Constraint[] getConstraintInfo() {
        return getConstraintInfo(getClass());
    }
    public static JCHR_Constraint[] getConstraintInfo(Handler handler) {
        return getConstraintInfo(handler.getClass());
    }
    public static JCHR_Constraint[] getConstraintInfo(Class<? extends Handler> handlerClass) {
        return handlerClass.getAnnotation(JCHR_Constraints.class).value();
    }
    
    /**
     * Checks whether constraints of the given class are stored by this
     * handler. Note that if the result is <code>true</code>, this does
     * not necessarily mean constraints of that particular class are
     * ever actually stored, it just means they <em>might</em> be
     * (whether they will or not depends on the concrete constraints 
     * told to the handler). If the result is <code>false</code> however,
     * it is certain that constraints of the given class are <em>never</em>
     * stored.
     * 
     * @param constraintClass
     *  The class of constraints under consideration.
     * @return <code>true</code> if constraints of the given class <em>might</em>
     *  be stored by this handler; <code>false</code> if they are can 
     *  <em>never</em> be stored in this handler.
     */
    public abstract boolean isStored(Class<? extends IConstraint> constraintClass);
    
    protected static void terminateAll(Iterable<? extends Constraint> constraints) {
    	terminateAll(constraints.iterator());
    }
    protected static void terminateAll(Iterator<? extends Constraint> constraints) {
    	while (constraints.hasNext()) constraints.next().terminate();
    }
    
    @SuppressWarnings("unchecked")
    public static Class<? extends Constraint> getConstraintClass(
        Class<? extends Handler> handlerClass, String constraintIdentifier
    ) throws IllegalArgumentException {
        try {
            return (Class<? extends Constraint>)Class.forName(
                handlerClass.getName()
                + '$'
                + Constraint.getClassSimpleName(constraintIdentifier)
            );
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(constraintIdentifier);
        }
    }
    
    /**
     * Returns an array of constraint classes of all <em>accessible</em>
     * constraints. For the definition of <em>accessible</em>, cf. 
     * <a href="#accessible">here</a>.
     */
    public abstract Class<? extends Constraint>[] getConstraintClasses(); 
    
    /**
     * Returns <code>true</code> if this handler was generated with the necessary
     * debug code to support tracing; <code>false</code> otherwise.
     * 
     * @return <code>true</code> if this handler was generated with the 
     * 	necessary debug code to support tracing; <code>false</code> otherwise.
     * 
     * @see #setTracer(Tracer)
     * @see #getTracer()
     * @see #getTracerView()
     */
    public boolean canBeTraced() {
    	return false;
    }
    
    /**
     * Sets a tracer for this handler.
     * 
     * @param tracer
     *  The new tracer for this handler (this will overwrite the existing 
     *  tracer if present)
     * @throws UnsupportedOperationException
     * 	if attaching the tracer cannot be done. 
     *  Whether or not attaching succeeds, 
     *  depends on the settings with which the handler was generated: 
     *  if no debug code was inserted in the handler,
     *  this operation will throw an exception.
     *  
     * @see #canBeTraced()
     */
    public void setTracer(Tracer tracer) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns the tracer this handler has set if one is present,
     * <code>null</code> if none was set.
     * 
     * @return The tracer this handler has set if one is present,
     * 	<code>null</code> if none was set.
     * @throws UnsupportedOperationException
     * 	If the handler was not generated with debug code.
     * 
     * @see #canBeTraced()
     */
    public Tracer getTracer() throws UnsupportedOperationException {
    	throw new UnsupportedOperationException();
    }
    
    /**
     * Returns a view of this handler used by tracers. Only
     * possible if the handler was generated with full debug
     * code option (should only be used when debugging as it may
     * break encapsulation). 
     * 
     * @return A view of this handler that can be used by tracers.
     * @throws UnsupportedOperationException
     * 	If the handler was not generated with full debug code.
     * 
     * @see #canBeTraced()
     */
    public Handler getTracerView() {
    	throw new UnsupportedOperationException();
    }
    
	protected abstract static class Continuation {
		protected abstract Continuation call();
	}
	
	
	@SuppressWarnings("all")
	protected final void call(Continuation continuation) {
		$$constraintSystem.hostLanguageMode = false;
		$$constraintSystem.STACK.pushDrop();
		while ((continuation = continuation.call()) != null);
		$$constraintSystem.hostLanguageMode = true;
	}
	
	/**
	 * Reactivates all constraints.
	 */
	public abstract void reactivateAll();
	
	/**
	 * Reactivates all constraints that are not excluded by the provided filter.
	 *
	 * @param filter
	 *   A filter on the constraints to reactivate.
	 */
	public abstract void reactivateAll(Filtered.Filter<? super Constraint> filter);
}