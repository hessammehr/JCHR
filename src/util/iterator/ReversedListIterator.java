package util.iterator;

import java.util.List;
import java.util.ListIterator;

public class ReversedListIterator<T> extends ListIteratorDecorator<T> {

    public static <T> ListIterator<T> getReversedListIterator(List<T> list) {
        return new ReversedListIterator<T>(list.listIterator(list.size()));
    }
    
    public static <T> ListIterator<T> getReversedListIterator(ListIterable<T> iterable, int size) {
        return new ReversedListIterator<T>(iterable.listIterator(size));
    }
    
    protected ReversedListIterator(ListIterator<T> decorated) {
        super(decorated);
    }
    
    @Override
    public boolean hasNext() {
        return super.hasPrevious();
    }
    @Override
    public T next() {
        return super.previous();
    }
    
    @Override
    public boolean hasPrevious() {
        return super.hasNext();
    }
    @Override
    public T previous() {
        return super.next();
    }
    
    @Override
    public int nextIndex() {
        return super.previousIndex();
    }
    @Override
    public int previousIndex() {
        return super.nextIndex();
    }
}
