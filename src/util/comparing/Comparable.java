package util.comparing;


/**
 * @author Peter Van Weert
 */
public interface Comparable<T> {
    
    /**
     * Compares with another given element, and returns a comparison.
     * Note that we are not comparing elements <em>to</em> eachother, as the  
     * Java interface {@link java.lang.Comparable} wrongly does,
     * but <em>with</em> eachother.
     * 
     * @param other
     *  The element to compare with.
     * @return A comparison.
     * 
     * @see Comparison
     */
    public Comparison compareWith(T other);
}
