package util.iterator;

import java.io.IOException;
import java.util.Iterator;

public final class IteratorUtilities {

    private IteratorUtilities() {/* non-instantiatable utility class */}
    
    public static <T> StringBuilder deepAppendTo(StringBuilder appendable, T... elements) {
        return deepAppendTo(appendable, new ArrayIterator<T>(elements));
    }
    public static <T> StringBuilder deepAppendTo(StringBuilder appendable, Iterable<T> iterable) {
        return deepAppendTo(appendable, iterable.iterator());
    }
    public static <T> StringBuilder deepAppendTo(StringBuilder appendable, Iterator<T> iterator) {
        return deepAppendTo(appendable, iterator, "[", "]", ",");
    }
    
    public static <T> StringBuffer deepAppendTo(StringBuffer appendable, T... elements) {
        return deepAppendTo(appendable, new ArrayIterator<T>(elements));
    }
    public static <T> StringBuffer deepAppendTo(StringBuffer appendable, Iterable<T> iterable) {
        return deepAppendTo(appendable, iterable.iterator());
    }
    public static <T> StringBuffer deepAppendTo(StringBuffer appendable, Iterator<T> iterator) {
        return deepAppendTo(appendable, iterator, "[", "]", ",");
    }
    
    public static <T> Appendable deepAppendTo(Appendable appendable, T... elements) throws IOException {
        return deepAppendTo(appendable, new ArrayIterator<T>(elements));
    }
    public static <T> Appendable deepAppendTo(Appendable appendable, Iterable<T> iterable) throws IOException  {
        return deepAppendTo(appendable, iterable.iterator());
    }
    public static <T> Appendable deepAppendTo(Appendable appendable, Iterator<T> iterator) throws IOException {
        return deepAppendTo(appendable, iterator, "[", "]", ",");
    }

    public static <T> String deepToString(T... elements) {
        return deepToString(new ArrayIterator<T>(elements));
    }
    public static <T> String deepToString(Iterable<T> iterable) {
        return deepToString(iterable.iterator());
    }
    public static <T> String deepToString(Iterator<T> iterator) {
        return deepToString(iterator, "[", "]", ",");
    }
    
    public static <T> String deepToString(Iterator<T> iterator, String prefix, String postfix, String infix) {
        return deepAppendTo(new StringBuilder(), iterator, prefix, postfix, infix).toString();
    }
    
    public static <T> StringBuilder deepAppendTo(StringBuilder appendable, Iterator<T> iterator, String prefix, String postfix, String infix) {
        try { deepAppendTo((Appendable)appendable, iterator, prefix, postfix, infix); }
        catch (IOException ioe) { /* cannot happen */ }
        return appendable;
    }
    
    public static <T> StringBuffer deepAppendTo(StringBuffer appendable, Iterator<T> iterator, String prefix, String postfix, String infix) {
    	try { deepAppendTo((Appendable)appendable, iterator, prefix, postfix, infix); }
        catch (IOException ioe) { /* cannot happen */ }
        return appendable;
    }
    
    public static <T> Appendable deepAppendTo(Appendable appendable, Iterator<T> iterator, String prefix, String postfix, String infix) throws IOException {
        appendable.append(prefix);
        if (iterator.hasNext()) {
            appendable.append(iterator.next().toString());
            
            while (iterator.hasNext())
                appendable.append(infix).append(iterator.next().toString());
        }
        return appendable.append(postfix);
    }
    
    public static <T> int size(Iterable<T> iterable) {
        return size(iterable.iterator());
    }
    
    /**
     * Returns the number of elements the given iterator returns. 
     * If it returns more than Integer.MAX_VALUE elements, 
     * returns Integer.MAX_VALUE. 
     *
     * @param iterator
     * 	The {@link Iterator} whose elements have to be counted.
     * 	Note that the iterator will probably have become useless
     * 	after counting...
     * @return The number of elements the given iterator returns. 
     * 	If it returns more than Integer.MAX_VALUE elements, 
     * 	returns Integer.MAX_VALUE.
     */
    public static <T> int size(Iterator<T> iterator) {
        int result = 0;
        while (iterator.hasNext()) {
            iterator.next();
            result++;
            if (result < 0) return Integer.MAX_VALUE;
        }
        return result;
    }
    
    public static <T> boolean isEmpty(Iterable<T> iterable) {
        return isEmpty(iterable.iterator());
    }
    
    public static <T> boolean isEmpty(Iterator<T> iterator) {
        return !iterator.hasNext();
    }
    
    public static <T> boolean contains(Iterator<T> iterator, Object o) {
        if (o == null) 
            return identityContains(iterator, null);
        while (iterator.hasNext())
            if (o.equals(iterator.next())) return true;
        return false;
    }
    
    public static <T> boolean identityContains(Iterable<T> iterable, Object o) {
        return identityContains(iterable.iterator(), o);
    }
    public static <T> boolean identityContains(Iterator<T> iterator, Object o) {
        while (iterator.hasNext())
            if (iterator.next() == o) return true;
        return false;
    }
    
    public static <T> int indexOf(Iterable<T> iterable, Object o) {
        return indexOf(iterable.iterator(), o);
    }
    public static <T> int indexOf(Iterator<T> iterator, Object o) {
        if (o == null) 
            return identityIndexOf(iterator, null);
        int result = 0;
        while (iterator.hasNext())
            if (o.equals(iterator.next())) return result;
            else result++;
        return -1;
    }
    
    public static <T> int identityIndexOf(Iterable<T> iterable, Object o) {
        return identityIndexOf(iterable.iterator(), o);
    }
    public static <T> int identityIndexOf(Iterator<T> iterator, Object o) {
        int result = 0;
        while (iterator.hasNext())
            if (iterator.next() == o) return result;
            else result++;
        return -1;
    }    
}