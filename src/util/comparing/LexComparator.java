package util.comparing;

import java.util.Comparator;
import java.lang.Comparable;

/**
 * If two arrays are different, 
 * then either they have different elements at some index, 
 * or their lengths are different, or both. 
 * If they have different elements at one or more index positions, 
 * let k be the smallest such index; 
 * then the array whose character at position k has the smaller value, 
 * as determined by using the {@link Comparable#compareTo(Object)} method, 
 * lexicographically precedes the other array. 
 * In this case, {@link #compare(Comparable[], Comparable[])} 
 * returns the result of the {@link Comparable#compareTo(Object)} method.
 * If there is no index position at which they differ, 
 * then the shorter array lexicographically precedes the longer array. 
 * In this case, {@link #compare(Comparable[], Comparable[])} returns 
 * the difference of the lengths of the arrays. 
 * 
 * @author Peter Van Weert
 */
public class LexComparator<T extends Comparable<? super T>> implements Comparator<T[]> {
    private LexComparator() { /* SINGLETON */ }
    @SuppressWarnings("unchecked")
    private static LexComparator instance;
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<? super T>> LexComparator<T> getInstance() {
        if (instance == null)
            instance = new LexComparator();
        return instance;
    }
    
    public int compare(T[] one, T[] other) {
        for (int i = 0; i < one.length && i < other.length; i++) {
            if (one[i] == null) {
                if (other[i] == null) continue;
                return -1;
            }
            if (other[i] == null) return +1;
            
            int comp = one[i].compareTo(other[i]);
            if (comp != 0) return comp;
        }
        return one.length - other.length;
    }
}