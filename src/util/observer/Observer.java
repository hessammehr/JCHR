package util.observer;

public interface Observer {

    /**
     * This method is called whenever the observed object is changed. 
     * An application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param   o     
     *  The observable object.
     * @param   arguments   
     *  Arguments passed to the <code>notifyObservers</code> method.
     */
    void update(Observable o, Object... arguments);
}
