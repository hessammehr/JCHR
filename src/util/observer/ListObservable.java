package util.observer;

import java.util.ArrayList;
import java.util.List;

abstract class ListObservable extends AbstractObservable {
    private List<Observer> observers;

    /** 
     * Construct an observable with zero Observers. 
     */
    public ListObservable() {
        setObservers(new ArrayList<Observer>());
    }
    
    protected void setObservers(List<Observer> observers) {
        this.observers = observers;
    }
    protected List<Observer> getObservers() {
        return observers;
    }
    
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\
     * Implementing the remaining observer methods is easy ... *
    \* * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    
    public synchronized void addObserver(Observer o) {
        if (o == null)
            throw new NullPointerException();
        if (!getObservers().contains(o))
            getObservers().add(o);
    }

    public synchronized void deleteObserver(Observer o) {
        getObservers().remove(o);
    }

    public void notifyObservers(Object... arguments) {
        /*
         * a temporary array buffer, used as a snapshot of the state of current
         * Observers.
         */
        Observer[] temp;

        synchronized (this) {
            /*
             * We don't want the Observer doing callbacks into arbitrary code
             * while holding its own Monitor. The code where we extract each
             * Observable from the Vector and store the state of the Observer
             * needs synchronization, but notifying observers does not (should
             * not). The worst result of any potential race-condition here is
             * that: 1) a newly-added Observer will miss a notification in
             * progress 2) a recently unregistered Observer will be wrongly
             * notified when it doesn't care
             */
            if (!changed()) return;
            
            temp = new Observer[getNbObservers()];
            getObservers().toArray(temp);
            clearChanged();
        }

        notifyObservers(temp, arguments);
    }
    
    public synchronized int getNbObservers() {
        return getObservers().size();
    }
    
    public synchronized void clearObservers() {
        getObservers().clear();
    }
    
    /**
     * The only thing that is left to concrete subclasses is to decide
     * in which order the observers are to be notified.
     *  
     * @param observers
     *  The current observers (in the order they were added) 
     * @param args
     *  The arguments to pass to the <code>update</code> method.
     */
    protected abstract void notifyObservers(Observer[] observers, Object[] args);
}
