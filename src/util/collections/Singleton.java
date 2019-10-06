package util.collections;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

import util.Cloneable;
import util.exceptions.IndexOutOfBoundsException;
import util.iterator.SingletonIterator;

/*
 * version 1.3.4    (Peter Van Weert)
 *   renamed to singleton: no longer only a set...
 * version 1.0.3    (Peter Van Weert)
 *   reimplemented
 */
/**
 * A class representing a singleton. This is a trivial implementation
 * of both <code>Set</code> and <code>List</code> containing
 * exactly one element. It appears <code>java.util.Collections</code>
 * offers similar functionality, but hey, ours is better: it is both
 * a <code>Set</code> and a <code>List</code> (not sure why one would
 * need this, but still).
 * 
 * @author Peter Van Weert
 */
public final class Singleton<T>
    implements SortedSet<T>, RandomAccess, List<T>, Serializable, Cloneable<Singleton<T>> {
    
    private static final long serialVersionUID = 1L;
    
    private T value;
    
    public Singleton(T value) {
        setValue(value);
    }

    public int size() {
        return 1;
    }

    public Iterator<T> iterator() {
        return listIterator();
    }

    public Spliterator<T> spliterator() {
        return List.super.spliterator();
    }
    /**
     * Returns the value contained in this singleton.
     * 
     * @return The value contained in this singleton.
     */
    public T getValue() {
        return value;
    }

    protected void setValue(T singletonValue) {
        this.value = singletonValue;
    }

    /**
     * This method is not supported (singleton).
     * 
     * @throws UnsupportedOperationException
     */
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    public T get(int index) throws IndexOutOfBoundsException {
        if (index == 0) return getValue();
        throw new IndexOutOfBoundsException(index, 0);
    }

    public T set(int index, T element) {
        if (index != 0)
            throw new IndexOutOfBoundsException(index, 0);
        T old = getValue();
        setValue(element);
        return old;
    }

    /**
     * This method is not supported (singleton).
     * 
     * @throws UnsupportedOperationException
     */
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported (singleton).
     * 
     * @throws UnsupportedOperationException
     */
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    public int indexOf(Object o) {
        if (o == getValue()) return 0;
        if (o == null) return -1;
        if (o.equals(getValue())) return 0;
        return -1;
    }

    public int lastIndexOf(Object o) {
        return indexOf(o);
    }

    public ListIterator<T> listIterator() {
        return new SingletonIterator<T>(getValue());
    }

    public ListIterator<T> listIterator(int index) {
        if (index != 0)
            throw new IndexOutOfBoundsException(index, 0);
        return listIterator();
    }

    public List<T> subList(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) return Empty.getInstance();
        if (fromIndex == 0 && toIndex == 1) return this;
        throw new java.lang.IndexOutOfBoundsException();
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean contains(Object o) {
        return (o == getValue() || (o != null && o.equals(getValue())));
    }

    public Object[] toArray() {
        return new Object[] { getValue() };
    }

    @SuppressWarnings({"unchecked", "hiding"})
    public <T> T[] toArray(T[] a) {
        if (a.length == 0)            
            a = (T[])Array.newInstance(a.getClass().getComponentType(), 1);
        Object[] result = a;
        result[0] = getValue(); 
        if (a.length > 1) a[1] = null;
        return a;
    }

    /**
     * This method is not supported (singleton).
     * 
     * @throws UnsupportedOperationException
     */
    public boolean add(T o) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported (singleton).
     * 
     * @throws UnsupportedOperationException
     */
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> c) {
        for (Object o : c) if (! contains(o)) return false;
        return true;
    }

    /**
     * This method is not supported (singleton).
     * 
     * @throws UnsupportedOperationException
     */
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported (singleton).
     * 
     * @throws UnsupportedOperationException
     */
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported (singleton).
     * 
     * @throws UnsupportedOperationException
     */
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported (singleton).
     * 
     * @throws UnsupportedOperationException
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    @SuppressWarnings({"unchecked", "hiding"})
    public Singleton<T> clone() {
        try {
            return (Singleton<T>)super.clone();
        } catch (CloneNotSupportedException cnse) {
            throw new InternalError();
        }
    }
    
    @Override
    public int hashCode() {
        // breaks specification of List !!!
        return ((value == null)? 0 : value.hashCode());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Collection) || ((Collection<?>)obj).size() != 1) 
            return false;
        Object otherValue = ((Collection<?>)obj).iterator().next();
        return this.value == otherValue 
            || ((otherValue != null) && otherValue.equals(this.value));
    }
    
    public Comparator<? super T> comparator() {
        return null;
    }
    
    public T first() {
        return get(0);
    }
    public T last() {
        return get(0);
    }
    
    public SortedSet<T> headSet(T toElement) {
        return Empty.getInstance();
    }
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return Empty.getInstance();
    }
    public SortedSet<T> tailSet(T fromElement) {
        if (!fromElement.equals(getValue()))
            throw new IllegalArgumentException();
        return this;
    }
}