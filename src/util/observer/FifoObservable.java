package util.observer;

public class FifoObservable extends ListObservable {

    @Override
    protected void notifyObservers(Observer[] observers, Object[] args) {
        for (int i = 0; i < observers.length; i++)
            observers[i].update(this, args);
    }
}
