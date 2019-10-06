package util.iterator;

import java.util.Iterator;
import java.util.ListIterator;

public abstract class AbstractListIterable<T> implements ListIterable<T> {
    public Iterator<T> iterator() {
        return listIterator();
    }
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }
}
