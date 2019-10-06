package util.iterator;

import java.util.ListIterator;

/**
 * An iterator that decorates another iterator, type casting
 * the latters elements before returning them. 
 * 
 * @author Peter Van Weert
 */
public class CastingListIterator<From, To> extends ConvertingListIterator<From, To> {
    public CastingListIterator(ListIterator<? extends From> wrapped) {
        super(wrapped, new Convertor<From, To>() {
            @SuppressWarnings("unchecked")
            public To convert(From o) {
                return (To)o;
            }
        });
    }
}