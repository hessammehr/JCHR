package util.iterator;

import java.util.Iterator;

/**
 * A <code>FilteredIteratable</code> decorates another {@link Iterable},
 * filtering the elements returned by its iterator using a user-defined
 * filter.
 * <br/>
 * The <code>remove</code> operation cannot be supported since we have to
 * look ahead in the <code>hasNext</code> method.
 * <br/>
 * The <code>hasToExclude</code> method will be called once
 * for each element in the original iterations (of course for as far 
 * the decorating iterator iterates), so you could keep an index
 * counter in your filter!
 * 
 * @author Peter Van Weert
 */
public class FilteredIterable<T>
    extends Filtered<T, Iterable<? extends T>>
    implements Iterable<T> {

    public FilteredIterable(Iterable<? extends T> decorated, Filter<? super T> filter) {
        super(decorated, filter);
    }

    public Iterator<T> iterator() {
        return new FilteredIterator<T>(getDecorated(), getFilter());
    }
}