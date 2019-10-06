package util.collections;

import java.io.Serializable;
import java.util.*;

import util.exceptions.IndexOutOfBoundsException;
import util.iterator.EmptyIterator;

/**
 * <p>
 * Represents an empty collection, set or (random access) list.
 * Objects of this class are serializable. 
 * </p>
 * <p>
 * This class is mostly implemented using the singleton pattern, 
 * with the single exception of the case where you need an 
 * empty <code>SortedSet</code> that does not use the natural 
 * order of its elements (even though these elements will never 
 * be there, this might be important e.g. in constructors like 
 * {@link java.util.TreeSet#TreeSet(java.util.SortedSet)}).
 * <br/>
 * When created using the <code>getInstance</code> method,
 * the (singleton!) result will also be a sorted set, but one
 * that uses the natural order of its (absent) elements. 
 * </p> 
 * 
 * @author Peter Van Weert
 */
public abstract class Empty<T>
    extends AbstractUnmodifiableCollection<T>
    implements SortedSet<T>, RandomAccess, List<T>, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    Empty() { /* SEMI-SINGLETON */ }
    @SuppressWarnings("unchecked")
    private static Empty instance;
    @SuppressWarnings("unchecked")
    public static <T> Empty<T> getInstance() {
        if (instance == null)
            instance = new SingletonEmpty();
        return instance;
    }
    @SuppressWarnings("hiding")
    private static class SingletonEmpty<T> extends Empty<T> {
        private static final long serialVersionUID = 1L;
        
        SingletonEmpty() { /*NOP*/ }
        
        // Preserves singleton property
        private Object readResolve() {
            return getInstance();
        }
    }
    
    public static <T> Empty<T> getInstance(Comparator<T> comparator) {
        if (comparator == null) 
            return getInstance();
        else
            return new ComparatorEmpty<T>(comparator);
    }
    @SuppressWarnings("hiding")
    private static class ComparatorEmpty<T> extends Empty<T> {
        private static final long serialVersionUID = 1L;
        
        private Comparator<T> comparator;
        
        public ComparatorEmpty(Comparator<T> comparator) {
            this.comparator = comparator;
        }
        
        @Override
        public Comparator<T> comparator() {
            return comparator;
        }
        
        @Override
        public int hashCode() {
            return comparator.hashCode();
        }
        
        @Override
        public boolean equals(Object other) {
            return (this == other) 
                || ((other instanceof ComparatorEmpty) && 
                comparator.equals(((ComparatorEmpty<?>)other).comparator));
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return EmptyIterator.getInstance();
    }
    
    public Comparator<? super T> comparator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    @SuppressWarnings("hiding")
    public <T> T[] toArray(T[] a) {
        if (a == null) throw new NullPointerException();
        if (a.length > 0) a[0] = null;
        return a;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.isEmpty();
    }
    
    public boolean addAll(int index, Collection<? extends T> c) {
        if (index == 0)
            throw new UnsupportedOperationException();
        else
            throw new IndexOutOfBoundsException(index, 0, 0);
    }

    public T get(int index) {
        throw new IndexOutOfBoundsException(index, 0, 0);
    }
    
    public T first() {
        throw new NoSuchElementException();
    }
    
    public T last() {
        throw new NoSuchElementException();
    }

    public T set(int index, T element) {
        throw new IndexOutOfBoundsException(index, 0, 0);
    }

    public void add(int index, T element) {
        if (index == 0)
            throw new UnsupportedOperationException();
        else    
            throw new IndexOutOfBoundsException(index, 0, 0);
    }

    public T remove(int index) {
        throw new IndexOutOfBoundsException(index, 0, 0);
    }

    public int indexOf(Object o) {
        return -1;
    }

    public int lastIndexOf(Object o) {
        return -1;
    }

    public ListIterator<T> listIterator() {
        return EmptyIterator.getInstance();
    }

    public ListIterator<T> listIterator(int index) {
        return EmptyIterator.getInstance();
    }

    @Override
    public Spliterator<T> spliterator() {
        return List.super.spliterator();
    }

    public List<T> subList(int fromIndex, int toIndex) {
        if (fromIndex != 0 || toIndex != 0)
            throw new java.lang.IndexOutOfBoundsException();
        return this;
    }
    
    public SortedSet<T> tailSet(T fromElement) {
        return this;
    }
    
    public SortedSet<T> headSet(T toElement) {
        return this;
    }
    
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return this;
    }
    
    /**
     * Throws CloneNotSupportedException.  This guarantees that 
     * the "singleton" status is preserved.
     *
     * @return (never returns)
     * @throws CloneNotSupportedException
     *  Cloning of a singleton is not allowed!
     */
    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    @Override
    public int hashCode() {
        return 1;
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Set)
            && (obj instanceof List)
            && ((Collection<?>)obj).isEmpty();
    }
}