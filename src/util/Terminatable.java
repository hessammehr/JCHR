package util;

/**
 * A <tt>Terminatable</tt> is an object that can be terminated. 
 * The <code>terminate</code> method is invoked to, e.g., release resources 
 * that the object is holding (such as open files). After termination
 * the object can no longer be used: all methods contain an implicit
 * precondition that the object they are called upon is not terminated,
 * i.e. should a method be called on a terminated object the behavior is
 * unspecified (the contract is broken). Of course, it might be better
 * to end clean by throwing an <code>IllegalStateException</code> for
 * instance, but implementors are not obliged to do so.
 * 
 * @author Peter Van Weert
 */
public interface Terminatable {

    /**
     * Inspector that returns <code>true</code> if and only if this object
     * is terminated (i.e. its <code>terminate</code> has been called).  
     * 
     * @return <code>true</code> if and only if this object
     *  is terminated (i.e. its <code>terminate</code> has been called);
     *  <code>false</code> otherwise.
     */
    boolean isTerminated();

    /**
     * Terminates this object. Henceforth the <code>isTerminated</code> method
     * will return <code>true</code>. A terminated object should never be used
     * again. Terminating an object more then once has no effect.
     */
    void terminate();
}