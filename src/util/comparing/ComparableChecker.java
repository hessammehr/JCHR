package util.comparing;

import java.util.Comparator;

public final class ComparableChecker<T extends java.lang.Comparable<? super T>> 
    implements Comparator<T>, EqualityChecker<T> {
    
    private ComparableChecker() { /* SINGLETON */ }
    @SuppressWarnings("unchecked")
    private static ComparableChecker instance;
    @SuppressWarnings("unchecked")
    public static <T extends java.lang.Comparable<? super T>> ComparableChecker<T> getInstance() {
        if (instance == null)
            instance = new ComparableChecker();
        return instance;
    }
    
    /**
     * Two objects are compared using their <code>compareTo</code> methods.
     * 
     * @return If both objects are the same, return 0. Otherwise use
     *  the result returned by their 
     */
    public final int compare(T o1, T o2) {
        if (o1 == o2) return 0;
        if (o1 == null) return -o2.compareTo(o1);
        return o1.compareTo(o2);
    }
    
    /**
     * Two objects are the same if the <code>compare</code> method
     * returns 0.
     * 
     * @return <code>(o1 == o2) || (o1 != null && o1.compareTo(o2) == 0)</code>
     */
    public final boolean equals(T o1, T o2) {
        return (o1 == o2) || (o1 != null && o1.compareTo(o2) == 0);
    }
}