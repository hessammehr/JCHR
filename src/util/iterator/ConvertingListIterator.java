package util.iterator;

import java.util.ListIterator;

/**
 * A list iterator that decorates another list iterator, returning its elements 
 * in the same order but converted using a user-defined convertor.
 * 
 * @author Peter Van Weert
 */
public class ConvertingListIterator<From, To> 
    extends ConvertingIterator<From, To>
    implements ListIterator<To> {

    /**
     * Creates a new <code>ConvertingListIterator</code> that decorates a given 
     * list iterator.
     * It returns the elements in the order of this decorated iterator, but it
     * converts them first using the given convertor. 
     * 
     * @param wrapped
     *  The decorated iterator.
     * @param convertor
     *  The convertor that has to be used to convert between the decorated iterators 
     *  elements to the ones returned by this iterator.
     */
    public ConvertingListIterator(
        ListIterator<? extends From> wrapped, 
        Convertor<? super From, ? extends To> convertor
    ) {
        super(wrapped, convertor);
    }
    
    /**
     * Creates a new <code>ConvertingListIterator</code> that decorates the 
     * list iterator from the given convertor argument.
     * It returns the elements in the order of this decorated iterator, 
     * but it converts them first using the given convertor. 
     * 
     * @param convertor
     *  The convertor that has to be used to convert between the decorated iterators 
     *  elements to the ones returned by this iterator.
     *  
     * @see ListIteratorConvertor#getListIterator()
     */
    public ConvertingListIterator(
        ListIteratorConvertor<From, ? extends To> convertor
    ) {
        this(convertor.getListIterator(), convertor);
    }
    
    /**
     * An abstract convertor that can be used for converting
     * list iterators. It has two extra convenience methods: 
     * {@link #nextIndex()} and {@link #previousIndex()}
     * 
     * @author Peter Van Weert
     */
    @SuppressWarnings("hiding")
    public static abstract class ListIteratorConvertor<From, To> 
        implements Convertor<From, To> {
        
        private ListIterator<From> listIterator;
        
        public ListIteratorConvertor(ListIterator<From> iterator) {
            setListIterator(iterator);
        }

        public int nextIndex() {
            return getListIterator().nextIndex();
        }
        
        public int previousIndex() {
            return getListIterator().previousIndex();
        }
        
        public ListIterator<From> getListIterator() {
            return listIterator;
        }
        protected void setListIterator(ListIterator<From> listIterator) {
            this.listIterator = listIterator;
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected ListIterator<From> getDecorated() {
        return (ListIterator<From>)super.getDecorated();
    }

    public boolean hasPrevious() {
        return getDecorated().hasPrevious();
    }
    public To previous() {
        return getConvertor().convert(getDecorated().previous());
    }

    public int nextIndex() {
        return getDecorated().nextIndex();
    }
    public int previousIndex() {
        return getDecorated().previousIndex();
    }

    /**
     * This method cannot be supported, since we cannot set <code>To</code>
     * objects through the dedorated list iterator (which iterates over
     * <code>From</code> objects). The <code>setUnconverted</code> method
     * does allow the setting of <code>From</code> objects, provided
     * the decorated iterator supports setting.
     *
     * @throws UnsupportedOperationException
     *  This operation is not supported.
     *  
     * @see #setUnconverted(From)
     */
    public void set(To o) {
        throw new UnsupportedOperationException();
    }
    /**
     * Calls the decorated <code>set</code> method with the given argument.
     * The behavior is therefor completely specified by the implementation
     * of this method. 
     * 
     * @param o
     *  The object to set.
     *  
     * @see ListIterator#set(E)
     */
    public void setUnconverted(From o) {
        getDecorated().set(o);
    }
    
    /**
     * This method cannot be supported, since we cannot add <code>To</code>
     * objects through the dedorated list iterator (which iterates over
     * <code>From</code> objects). The <code>setUnconverted</code> method
     * does allow the setting of <code>From</code> objects, provided
     * the decorated iterator supports setting.
     *
     * @throws UnsupportedOperationException
     *  This operation is not supported.
     *  
     * @see #addUnconverted(From)
     */
    public void add(To o) {
        throw new UnsupportedOperationException();
    }
    /**
     * Calls the decorated <code>add</code> method with the given argument.
     * The behavior is therefor completely specified by the implementation
     * of this method. 
     * 
     * @param o
     *  The object to add.
     *  
     * @see ListIterator#add(E)
     */
    public void addUnconverted(From o) {
        getDecorated().add(o);
    }
}
