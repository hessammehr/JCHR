package util.observer;

/**
 * Instances of this interface represents observable objects, or "data" in the 
 * model-view paradigm.
 * <p>
 * An observable object can have one or more observers. An observer may be any
 * object that implements interface <tt>Observer</tt>. After an observable
 * instance changes, an application calling the <code>Observable</code>'s
 * <code>notifyObservers</code> method causes all of its observers to be
 * notified of the change by a call to their <code>update</code> method.
 * <p>
 * Note that this notification mechanism is has nothing to do with threads and
 * is completely separate from the <tt>wait</tt> and <tt>notify</tt>
 * mechanism of class <tt>Object</tt>.
 * <p>
 * When an observable object is newly created, its set of observers is empty.
 * Two observers are considered the same if and only if the <tt>equals</tt>
 * method returns true for them.
 * 
 * @author Peter Van Weert
 * 
 * @see java.util.Observable
 */
public interface Observable {
    /**
     * Adds an observer to the set of observers for this object, provided that
     * it is not the same as some observer already in the set. The order in
     * which notifications will be delivered to multiple observers is not
     * specified. See the class comment.
     * 
     * @param o
     *            An observer to be added.
     * @throws NullPointerException
     *             If the parameter o is null.
     */
    public void addObserver(Observer o);

    /**
     * Deletes an observer from the set of observers of this object. Passing
     * <code>null</code> to this method will have no effect.
     * 
     * @param o
     *  The observer to be deleted.
     */
    public void deleteObserver(Observer o);

    /**
     * If this object has changed, as indicated by the <code>hasChanged</code>
     * method, then notify all of its observers and then call the
     * <code>clearChanged</code> method to indicate that this object has no
     * longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with this
     * observable object as the first argument, followed by the other arguments
     * given in <code>arg</code>.
     * 
     * @param arguments
     *  A series of arbitrary objects as extra arguments.
     * @see ListObservable#clearChanged()
     * @see ListObservable#hasChanged()
     * @see java.util.Observer#update(ListObservable, java.lang.Object)
     */
    public void notifyObservers(Object... arguments);

    /**
     * Clears the observer list so that this object no longer has any observers.
     */
    public void clearObservers();

    /**
     * Tests if this object has changed.
     * 
     * @return <code>true</code> if and only if the <code>setChanged</code>
     *         method has been called more recently than the
     *         <code>clearChanged</code> method on this object;
     *         <code>false</code> otherwise.
     * @see ListObservable#clearChanged()
     * @see ListObservable#setChanged()
     */
    public boolean hasChanged();

    /**
     * Returns the number of observers of this <tt>Observable</tt> object.
     * 
     * @return the number of observers of this object.
     */
    public int getNbObservers();

}