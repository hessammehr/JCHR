package runtime.debug.graphical;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JList;

import runtime.Constraint;
import runtime.ConstraintComparator;
import util.Resettable;
import util.comparing.ReversedComparator;
import util.exceptions.IndexOutOfBoundsException;

/**
 * Implements a list model for a list of constraints.
 * <br/>
 * At the moment the methods of this class are never synchronized, and 
 * so far, so good. 
 *  
 * @author Peter Van Weert
 */
public class ConstraintListModel extends AbstractListModel implements Resettable {
    private static final long serialVersionUID = 1L;
    
    private int size;
    private Constraint[] array;
    
    private Comparator<Constraint> comparator;
    
    private final static int INITIAL_CAPACITY = 16;
    
    public ConstraintListModel() {
        array = new Constraint[INITIAL_CAPACITY];
        setComparator(ConstraintComparator.getOldFirstComparator());
    }
    
    public ConstraintListModel(Iterator<? extends Constraint> iter) {
        this();
        init(iter);
    }
    
    public void init(Iterator<? extends Constraint> iter) {
        if (size != 0) throw new IllegalStateException();
            
        boolean justReverse = true, sorted = true;
    
        while (iter.hasNext()) {
            Constraint next = iter.next();
            if (size != 0 && (justReverse || sorted)) {
                int c = getComparator().compare(array[size-1], next);
                sorted &=c<= 0;
                justReverse &=c>= 0;
            }
            add(size, next);
        }

        if (!sorted) if (justReverse) doReverse(); else doSort();
        
        fireIntervalAdded(this, 0, size-1);
    }
    
    public void reset() {
        Arrays.fill(array, 0, size-1, null);
        size = 0;
    }
    
    /**
     * Returns the number of constraints in this list.
     *
     * @return  the number of constraints in this list
     * @see Vector#size()
     */
    public int size() {
        return size;
    }
    
    public int getSize() {
        return size;
    }
    
    /**
     * Returns the constraint at the specified index (the index here
     * is the logical index, as seen by the {@link JList}: the actual 
     * index will be equal to <code>size-1-index</code>.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred
     *    method to use is <code>get(int)</code>, which implements the 
     *    <code>List</code> interface defined in the 1.2 
     *    Collections framework.
     * </blockquote>
     * @param      index   a <em>logical</em> index into this list
     * @return     the constraint at the specified index
     * @exception  ArrayIndexOutOfBoundsException  if the <code>index</code> 
     *             is negative or greater than the current size of this 
     *             list
     * @see #get(int)
     * @see #toActualIndex(int)
     */
    public Constraint getElementAt(int index) {
        return get(index);
    }
    
    protected final static boolean REVERSE = false;
    
    protected int toActualIndex(int index) {
        return REVERSE? size-1-index : index;
    }
    protected int toLogicalIndex(int index) {
        return REVERSE? size-1-index : index;
    }
    
    /**
     * Returns the constraint at the specified position in this list.
     * 
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code>
     * if the index is out of range
     * (<code>index &lt; 0 || index &gt;= size()</code>).
     *
     * @param index index of constraint to return
     */
    public Constraint get(int index) {
        return array[toActualIndex(index)];
    }
    
    /**
     * Deletes the constraint at the specified index.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index 
     * is invalid.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred
     *    method to use is <code>remove(int)</code>, which implements the 
     *    <code>List</code> interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @param      index   the index of the constraint to remove
     * @see #remove(int)
     */
    protected void removeElementAt(int index) {
        int actual = toActualIndex(index);
        
        int numMoved = size - actual - 1;
        if (numMoved < 0)
            throw new IndexOutOfBoundsException(actual, size);
        
        if (numMoved > 0)
            System.arraycopy(array, actual+1, array, actual, numMoved);
        array[--size] = null; // Let gc do its work
        
        fireIntervalRemoved(this, index, index);
    }
    
    /**
     * Removes the element at the specified position in this list.
     * Returns the element that was removed from the list.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code>
     * if the index is out of range
     * (<code>index &lt; 0 || index &gt;= size()</code>).
     *
     * @param index the index of the element to removed
     */
    protected Constraint remove(int index) {
        Constraint result = array[toActualIndex(index)];
        removeElementAt(index);
        return result;
    }

    public int insert(Constraint constraint) {
        int index;
        
        if (size == 0) { 
            add(0, constraint);
            index = 0;
        } else if (getComparator().compare(constraint, array[size-1]) > 0) {
            add(size, constraint);
            index = toLogicalIndex(size-1);
        } else {
            index = indexOf(constraint);
            add(index, constraint);
            index = toLogicalIndex(index);
        }
        
        fireIntervalAdded(this, index, index);
        
        return index;
    }
    
    public int indexOf(Constraint constraint) {
        return toLogicalIndex(actualIndexOf(constraint));
    }
    
    /**
     * Returns the actual index (not the logical index as seen
     * by the {@link JList}) of the given constraint in this list,
     * or the index at which it should be inserted.
     * 
     * @param constraint
     *  The constraint to search.
     * @return the actual index (not the logical index as seen
     *  by the {@link JList}) of the given constraint in this list,
     *  or the index at which it should be inserted
     */
    protected int actualIndexOf(Constraint constraint) {
        int low = 0;
        int high = size-1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            Constraint midVal = array[mid];

            if (constraint == midVal)
                return mid;
            if (getComparator().compare(constraint, midVal) > 0)
                low = mid + 1;
            else
                high = mid - 1;
        }
        
        return low;  // not found
    }
    
    public void refresh() {
        fireContentsChanged(this, 0, size-1);
    }
    public void contentsChanged(int index) {
        fireContentsChanged(this, index, index);
    }
    
    public void remove(Constraint constraint) {
        int index = indexOf(constraint);
        removeElementAt(index);
        fireIntervalRemoved(this, index, index);
    }
    
    /**
     * Inserts the specified constraint at the specified position in this list.
     * <p>
     * Throws an <code>ArrayIndexOutOfBoundsException</code> if the
     * index is out of range
     * (<code>index &lt; 0 || index &gt; size()</code>).
     *
     * @param index index at which the specified constraint is to be inserted
     * @param constraint constraint to be inserted
     */
    protected void add(int index, Constraint constraint) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(index, size);

        ensureCapacity();
        System.arraycopy(array, index, array, index+1, size++-index);
        array[index] = constraint;
    }
    
    protected void ensureCapacity() {
        int capacity = array.length;
        if (size == capacity) {
            Constraint[] newArray = new Constraint[(capacity * 3)/2 + 1];
            System.arraycopy(array, 0, newArray, 0, size);
            array = newArray;
        }
    }

    
    @Override
    public String toString() {
        return Arrays.toString(array);
    }
    
    public void reverse() {
        setComparator(ReversedComparator.reverse(getComparator()));
        doReverse();
        refresh();
    }
    protected void doReverse() {
        util.Arrays.reverse(array, 0, size);
    }
    public void sortBy(Comparator<Constraint> comparator) {
        setComparator(comparator);
        doSort();
        refresh();
    }
    protected void doSort() {
        Arrays.sort(array, 0, size, comparator);
    }
    
    protected void setComparator(Comparator<Constraint> comparator) {
        this.comparator = comparator;
    }
    protected Comparator<Constraint> getComparator() {
        return comparator;
    }
}
