package util.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.IdentityHashMap;
import java.util.Iterator;

import util.Resettable;


/**
 * This class implements the <tt>Set</tt> interface, backed by a hash table
 * (actually a <tt>IdentityHashMap</tt> instance).  It makes no guarantees as to the
 * iteration order of the set; in particular, it does not guarantee that the
 * order will remain constant over time.  This class permits the <tt>null</tt>
 * element.<p>
 *
 * This class offers constant time performance for the basic operations
 * (<tt>add</tt>, <tt>remove</tt>, <tt>contains</tt> and <tt>size</tt>),
 * assuming the hash function disperses the elements properly among the
 * buckets.  Iterating over this set requires time proportional to the sum of
 * the <tt>IdentityHashSet</tt> instance's size (the number of elements) plus the
 * "capacity" of the backing <tt>IdentityHashMap</tt> instance (the number of
 * buckets).  Thus, it's very important not to set the initial capacity too
 * high if iteration performance is important. A final assumption that is
 * made is that the system identity hash function 
 * ({@link System#identityHashCode(Object)}) disperses elements properly 
 * among the buckets. <p>
 *
 * <p><b>This class is <i>not</i> a general-purpose <tt>Set</tt>
 * implementation!  While this class implements the <tt>Set</tt> interface, it
 * intentionally violates <tt>Set's</tt> general contract, which mandates the
 * use of the <tt>equals</tt> method when comparing objects.  This class is
 * designed for use only in the rare cases wherein reference-equality
 * semantics are required.</b>
 *
 * <b>Note that this implementation is not synchronized.</b> If multiple
 * threads access a set concurrently, and at least one of the threads modifies
 * the set, it <i>must</i> be synchronized externally.  This is typically
 * accomplished by synchronizing on some object that naturally encapsulates
 * the set.  If no such object exists, the set should be "wrapped" using the
 * <tt>Collections.synchronizedSet</tt> method.  This is best done at creation
 * time, to prevent accidental unsynchronized access to the <tt>IdentityHashSet</tt>
 * instance:
 * 
 * <pre>
 *     Set s = Collections.synchronizedSet(new IdentityHashSet(...));
 * </pre><p>
 *
 * The iterators returned by this class's <tt>iterator</tt> method are
 * <i>fail-fast</i>: if the set is modified at any time after the iterator is
 * created, in any way except through the iterator's own <tt>remove</tt>
 * method, the Iterator throws a <tt>ConcurrentModificationException</tt>.
 * Thus, in the face of concurrent modification, the iterator fails quickly
 * and cleanly, rather than risking arbitrary, non-deterministic behavior at
 * an undetermined time in the future.
 * 
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis. 
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i><p>
 *
 * Maybe this class <em>should</em> be a member of the 
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>, but it is <em>not</em>. Its implementation
 * is based upon that of <code>HashSet</code> (which <em>is</em> part of 
 * the framework).
 *
 * @author  Peter Van Weert
 * @see     java.util.Collection
 * @see     java.util.Set
 * @see     java.util.HashSet
 * @see     java.util.Collections#synchronizedSet(java.util.Set)
 * @see     java.util.IdentityHashMap
 */
public class IdentityHashSet<E> 
    extends AbstractSet<E>
    implements Cloneable, Serializable, Resettable {

    private static final long serialVersionUID = 1L;

    // Making the following member fields protected for subclassing. I know
    // it's not best practice, but it is the most efficient way (which is an
    // important factor with implementation of hash sets...)
    
    protected transient IdentityHashMap<E,Object> map;

    // Dummy value to associate with an Object in the backing Map
    protected static final Object PRESENT = new Object();

    /**
     * Constructs a new, empty set; the backing <tt>IdentityHashMap</tt> instance has
     * a default expected maximum size (21).
     */
    public IdentityHashSet() {
        map = new IdentityHashMap<E,Object>();
    }

    /**
     * Constructs a new set containing the elements in the specified
     * collection.  The <tt>IdentityHashMap</tt> is created with an initial 
     * expected maximum size sufficient to contain the elements in
     * the specified collection.
     *
     * @param c the collection whose elements are to be placed into this set.
     * @throws NullPointerException   if the specified collection is null.
     */
    public IdentityHashSet(Collection<? extends E> c) {
        map = new IdentityHashMap<E,Object>(Math.max((int) (c.size()/.75f) + 1, 16));
        addAll(c);
    }

    /**
     * Constructs a new, empty set; the backing <tt>IdentityHashMap</tt> instance has
     * the specified expected maximum size.
     *
     * @param      expectedMaxSize   the initial expected maximum size.
     * @throws     IllegalArgumentException if the initial capacity is less
     *             than zero.
     */
    public IdentityHashSet(int expectedMaxSize) {
        map = new IdentityHashMap<E,Object>(expectedMaxSize);
    }

    /**
     * Returns an iterator over the elements in this set.  The elements
     * are returned in no particular order. The <code>Iterator.remove</code>
     * method is implemented (calling it will remove the current element
     * from this set). 
     *
     * @return an Iterator over the elements in this set.
     * @see ConcurrentModificationException
     */
    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    /**
     * Returns the number of elements in this set (its cardinality).
     *
     * @return the number of elements in this set (its cardinality).
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * Returns <tt>true</tt> if this set contains no elements.
     *
     * @return <tt>true</tt> if this set contains no elements.
     */
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this set contains the specified element.
     *
     * @param o element whose presence in this set is to be tested.
     * @return <tt>true</tt> if this set contains the specified element.
     */
    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    /**
     * Adds the specified element to this set if it is not already
     * present.
     *
     * @param o element to be added to this set.
     * @return <tt>true</tt> if the set did not already contain the specified
     *  element.
     */
    @Override
    public boolean add(E o) {
        return map.put(o, PRESENT) == null;
    }

    /**
     * Removes the specified element from this set if it is present.
     *
     * @param o object to be removed from this set, if present.
     * @return <tt>true</tt> if the set contained the specified element.
     */
    @Override
    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }

    /**
     * Removes all of the elements from this set.
     */
    @Override
    public void clear() {
        map.clear();
    }
    
    /**
     * Removes all of the elements from this set.
     */
    public void reset() {
        map.clear();
    }

    /**
     * Returns a shallow copy of this <tt>IdentityHashSet</tt> instance: the elements
     * themselves are not cloned.
     *
     * @return a shallow copy of this set.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object clone() {
        try { 
            IdentityHashSet<E> newSet = (IdentityHashSet<E>) super.clone();            
            newSet.map = (IdentityHashMap<E, Object>) map.clone();
            return newSet;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * Save the state of this <tt>IdentityHashSet</tt> instance to a stream 
     * (that is, serialize this set).
     *
     * @serialData The size of the set (the number of elements it contains)
     *    (int), followed by all of its elements (each an Object) in
     *    no particular order.
     */
    private void writeObject(ObjectOutputStream s)
        throws IOException {
    
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out size
        s.writeInt(map.size());

        // Write out all elements in the proper order.
        for (E e : map.keySet()) s.writeObject(e);
    }

    /**
     * Reconstitute the <tt>IdentityHashSet</tt> instance from a stream (that is,
     * deserialize it).
     */
    private void readObject(ObjectInputStream s)
        throws IOException, ClassNotFoundException {
        
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read in size
        int size = s.readInt();
        
        // Create backing IdentityHashMap (allow for 33% growth)
        map = new IdentityHashMap<E,Object>((size*4)/3);

        // Read in all elements in the proper order.
        for (int i=0; i<size; i++) {
            @SuppressWarnings("unchecked")
            E e = (E) s.readObject();
            map.put(e, PRESENT);
        }
    }
    
    /**
     * Returns the hash code value for this set.  The hash code of a set is
     * defined to be the sum of the hash codes of the elements in the set.
     * This ensures that <tt>s1.equals(s2)</tt> implies that
     * <tt>s1.hashCode()==s2.hashCode()</tt> for any two sets <tt>s1</tt>
     * and <tt>s2</tt>, as required by the general contract of
     * Object.hashCode.<p>
     *
     * This implementation enumerates over the set, using the system
     * identity hash function ({@link System#identityHashCode(Object)})
     * on each element in the collection, and adding up the results.
     *
     * @return the hash code value for this set.
     */
    @Override
    public int hashCode() {
        int h = 0;
        for (E e : this) h += System.identityHashCode(e);
        return h;
    }
}
