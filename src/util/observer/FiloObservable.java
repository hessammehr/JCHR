package util.observer;

public class FiloObservable extends ListObservable {

    @Override
    protected void notifyObservers(Observer[] observers, Object[] args) {
        for (int i = observers.length; i >= 0; i--)
            observers[i].update(this, args);
    }

}
