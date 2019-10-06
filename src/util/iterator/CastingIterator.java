package util.iterator;

import java.util.Iterator;

/**
 * An iterator that decorates another iterator, type casting
 * the latters elements before returning them. 
 * 
 * @author Peter Van Weert
 */
public class CastingIterator<From, To> extends ConvertingIterator<From, To> {
    public CastingIterator(Iterator<? extends From> wrapped) {
        super(wrapped, new Convertor<From, To>() {
            @SuppressWarnings("unchecked")
            public To convert(From o) {
                return (To)o;
            }
        });
    }
}