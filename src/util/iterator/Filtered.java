package util.iterator;

import java.util.Iterator;

/**
 * A <code>Filtered</code> object is an object that decorates another
 * object, equipping it with a filter on its elements. 
 * 
 * @param <E>
 *  The element type.
 * @param <D>
 *  The type of the decorated object.
 * 
 * @author Peter Van Weert
 */
public abstract class Filtered<E, D> {
	
	public static <T> FilteredIterable<T> filter(Iterable<? extends T> iterable, Filter<? super T> filter) {
		return new FilteredIterable<T>(iterable, filter);
	}
	public static <T> FilteredIterator<T> filter(Iterator<? extends T> iterable, Filter<? super T> filter) {
		return new FilteredIterator<T>(iterable, filter);
	}
	
    /**
     * <p>
     * A simple interface used to define the filter used by 
     * <code>FilteredIterator</code>. It declares a method that will be used
     * to decide whether or not to exclude an element from the iteration.
     * </p><p>
     * <em>
     * Either {@link #include(Object)} or {@link #exclude(Object)}
     * has to be overridden.
     * </em>
     * If both are overridden, the behavior of both methods should be consistent. 
     * </p>
     * 
     * @author Peter Van Weert
     */
    public abstract static class Filter<S> {
        /**
         * Checks whether a given element should be excluded or not.
         * 
         * @param elem
         *  The element that possibly has to be excluded.
         * @return True if and only if the given element has to be excluded.
         */
        public boolean exclude(S elem) {
        	return !include(elem);
        }
        
        /**
         * Checks whether a given element should be included or not.
         * 
         * @param elem
         *  The element that possibly has to be included.
         * @return True if and only if the given element has to be included.
         */
        public boolean include(S elem) {
        	return !exclude(elem);        	
        }
    }
    
    /**
     * A simple utility class used to define a filter that uses indices 
     * to filter the elements. It declares a method that will be used
     * to decide whether or not to exclude an element with a given
     * index from the iteration.
     * 
     * @author Peter Van Weert
     */
    public abstract class IndexExclusionFilter<S> extends Filter<S> {
    	private int index = 0;
    	
    	@Override
    	public final boolean exclude(S elem) {
    		return hasToExclude(index++);
    	}
    	@Override
    	public final boolean include(S elem) {
    		return !hasToExclude(index++);
    	}
    	
        protected abstract boolean hasToExclude(int index);
    }
    
    /**
     * A simple utility class used to define a filter that uses indices 
     * to filter the elements. It declares a method that will be used
     * to decide whether or not to include an element with a given
     * index in the iteration.
     * 
     * @author Peter Van Weert
     */
    public abstract class IndexInclusionFilter<S> extends Filter<S> {
    	private int index = 0;
    	
    	@Override
    	public final boolean exclude(S elem) {
    		return !hasToInclude(index++);
    	}
    	@Override
    	public final boolean include(S elem) {
    		return !hasToInclude(index++);
    	}
    	
    	/**
         * Checks whether the element with the given index has to be
         * included in the iteration.
         * 
         * @param index
         *  The index of the current element under consideration for
         *  inclusion or not.
         * @return True if and only if the element with the given index
         *  has to be included.
         */
        protected abstract boolean hasToInclude(int index);
    }
    
    /**
     * Creates a new <code>Filtered</code>.
     * @param decorated
     *  The decorated object.
     * @param filter
     *  The user-defined filter used to filter the elements of the 
     *  decorated object.
     */
    Filtered(D decorated, Filter<? super E> filter) {
        setFilter(filter);
        setDecorated(decorated);
    }
    
    
    
    private Filter<? super E> filter;
    
    public Filter<? super E> getFilter() {
        return filter;
    }
    protected void setFilter(Filter<? super E> filter) {
        this.filter = filter;
    }
    
    private D decorated;
    
    public D getDecorated() {
        return decorated;
    }
    protected void setDecorated(D decorated) {
        this.decorated = decorated;
    }
}
