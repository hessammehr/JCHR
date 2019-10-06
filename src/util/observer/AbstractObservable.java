package util.observer;

public abstract class AbstractObservable implements Observable {
    private boolean changed = false;
    
    /**
     * Marks this <tt>Observable</tt> object as having been changed; the
     * <tt>hasChanged</tt> method will now return <tt>true</tt>.
     */
    protected synchronized void setChanged() {
        changed = true;
    }

    /**
     * Indicates that this object has no longer changed, or that it has already
     * notified all of its observers of its most recent change, so that the
     * <tt>hasChanged</tt> method will now return <tt>false</tt>. This
     * method is called automatically by the <code>notifyObservers</code>
     * methods.
     * 
     * @see Observable#notifyObservers()
     * @see Observable#notifyObservers(Object[])
     */
    protected synchronized void clearChanged() {
        changed = false;
    }

    public synchronized boolean hasChanged() {
        return changed;
    }
    
    /**
     * (unsynchronized)
     * 
     *  @see #hasChanged()
     */
    protected boolean changed() {
        return changed;
    }
}
