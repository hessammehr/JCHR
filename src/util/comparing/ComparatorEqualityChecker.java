package util.comparing;

import java.util.Comparator;

public abstract class ComparatorEqualityChecker<T> implements Comparator<T>, EqualityChecker<T> {

    /**
     * Returns an instance comparing objects using the given <code>Comparator</code>. 
     * 
     * @param comparator
     *  The comparator that has to be used to compare objects. 
     * @return An instance of <code>ComparatorEqualityChecker</code>
     *  using the using the given <code>Comparator</code> for comparing
     *  objects.
     */
    public static <S> ComparatorEqualityChecker<S> getInstance(
        final Comparator<S> comparator
    ) {
        return new ComparatorEqualityChecker<S>() {
            public int compare(S o1, S o2) {
                return comparator.compare(o1, o2);
            }
        };
    }
    
    /**
     * Two objects are the same if the <code>compare</code> method
     * returns 0.
     * 
     * @return <code>compare(o1, o2) == 0</code>
     */
    public final boolean equals(T o1, T o2) {
        return compare(o1, o2) == 0;
    }
}