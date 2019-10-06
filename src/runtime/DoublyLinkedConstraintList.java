package runtime;

import java.util.Iterator;
import java.util.NoSuchElementException;

import util.Terminatable;
import util.exceptions.IllegalArgumentException;

/**
 * A <code>DoublyLinkedConstraintList</code> is a linked list of constraints 
 * (what a surprise!) in which constraints are sorted on their age 
 * (i.e. ID). 
 * Note that this is not enforced by the {@link #addFirst(T)} method, 
 * as it is part of its precondition.
 * The {@link #checkedAddFirst(Constraint)} can be used 
 * to only add an element if it is allowed. 
 * This is a (minimal) linked list, not intended for general purpose use.
 * Methods for insertion other than on the first place are not provided,
 * no random access is possible,
 * methods such as {@link #equals(Object)} or {@link #hashCode()} are not implemented,
 * and so on.
 *   
 * @author Peter Van Weert
 */
public class DoublyLinkedConstraintList<T extends Constraint> 
	implements Iterable<T>, ConstraintIterable<T> {
	
	/*
     *     ______       ______       ______       ______
     *   ||      ||-->||      ||-->||      ||-->||      ||
     * --||______||<--||______||<--||______||<--||______||--
     *                   ^^^
     *                 iterator
     *              
     *               __________________  
     *     ______   /   ______       __v___       ______
     *   ||      ||/  || dead ||-->||      ||-->||      ||
     * --||______||<--||______||   ||______||<--||______||--
     *       ^           ^^^      /
     *       |         iterator  /
     *       |__________________/
     *
     *               _______________________________ 
     *     ______   /   ______       ______       __v___
     *   ||      ||/  || dead ||-->|| dead ||-->||      ||
     * --||______||<--||______||   ||______||   ||______||--
     *       ^           ^^^      /            /
     *       |        iterator   /            /
     *       |__________________/____________/
     *     
     *               _______________________________ 
     *     ______   /   ______       ______       __v___
     *   ||      ||/  || dead ||-->|| dead ||-->||      ||
     * --||______||<--||______||   ||______||   ||______||--
     *       ^                    /   ^^^      /
     *       |                   /  iterator  /
     *       |__________________/____________/
     *       
     *
     * Note that for the head, the 'next' pointer is reliable nonetheless!
     */
	
    /**
	 * De head van deze lijst.
	 * @invar head != null
	 */
	protected Node<T> head = new Node<T>();

	public DoublyLinkedConstraintList() {
		// NOP
    }
    
    public DoublyLinkedConstraintList(T initialConstraint) {
    	try {
    		final Node<T> head = this.head;
            head.value = initialConstraint;
            initialConstraint.addStorageBackPointer(head);
            this.head = new Node<T>(head);
        } catch (NullPointerException npe) {
            throw new IllegalArgumentException(initialConstraint);
        }
    }
    
    /**
	 * Adds an element <code>x</code> in front of the list. 
	 * 
	 * @param x
	 *  The element you want to add in front of this list.
	 */
    public void addFirst(T x) {
        try {
        	Node<T> head = this.head;
            head.value = x;
            x.addStorageBackPointer(head);
            this.head = new Node<T>(head);
        } catch (NullPointerException npe) {
            throw new IllegalArgumentException(x);
        }
    }
    
    /**
     * Merges two list: this one and the given argument (<code>other</code>).
     * Both lists of constraints are sorted in descending ID, as will the result.
     * The result will be stored in this list. None of the iterators over
     * either list will be disturbed, nor will they start iterating over
     * constraints that weren't in <code>this</code> or <code>other</code>
     * respectively at the time the iterators were created. 
     * Note that in order to accomplish this, when both lists are non-empty,
     * we <em>have</em> to duplicate the entire lists! 
     * The constraints will be notified that they are added to the new
     * list (node). After the merge a constraint can be in the new list as well 
     * as in both old lists. Only when one of the lists is empty we can
     * reuse the other list.
     * 
     * @param other
     *  The list of constraints to merge with.
     */
	public void mergeWith(DoublyLinkedConstraintList<T> other) {
        Node<T> thisCurrent = this.head.next, 
               otherCurrent = other.head.next;
        
        if (otherCurrent == null) return; // nothing to merge
        if (thisCurrent != null) {
            // Release the old list (iterators must not be disturbed)
            head = new Node<T>();
            
            int thisID = thisCurrent.value.ID,
               otherID = otherCurrent.value.ID;
            
            Node<T> trail = head;
            
            while (true) {
                while (thisID > otherID) {
                    trail = trail.next = new Node<T>(thisCurrent.value, trail);
                    thisCurrent = thisCurrent.next;
                    if (thisCurrent == null) {
                        do {
                        	trail = trail.next = new Node<T>(otherCurrent.value, trail);
                            otherCurrent = otherCurrent.next;
                        } while (otherCurrent != null);

                        return;
                    }
                    thisID = thisCurrent.value.ID;
                }
                /* thisID <= otherID */
                if (thisID == otherID) {
                    thisCurrent = thisCurrent.next;       // otherCurrent will be added next!
                    if (thisCurrent == null) {
                        do {
                        	trail = trail.next = new Node<T>(otherCurrent.value, trail);
                            otherCurrent = otherCurrent.next;
                        } while (otherCurrent != null);

                        return;
                    }
                    thisID = thisCurrent.value.ID;
                }
                /* thisID < otherID */
                do {
                	trail = trail.next = new Node<T>(otherCurrent.value, trail);
                    otherCurrent = otherCurrent.next;
                    if (otherCurrent == null) {
                        do {
                        	trail = trail.next = new Node<T>(thisCurrent.value, trail);
                            thisCurrent = thisCurrent.next;
                        } while (thisCurrent != null);

                        return;
                    }
                    otherID = otherCurrent.value.ID;
                } while (thisID < otherID);
                /* thisID >= otherID */
                if (thisID == otherID) {
                    otherCurrent = otherCurrent.next;     // thisCurrent will be added next!
                    if (otherCurrent == null) {
                        do {
                        	trail = trail.next = new Node<T>(thisCurrent.value, trail);
                            thisCurrent = thisCurrent.next;
                        } while (thisCurrent != null);

                        return;
                    }
                    otherID = otherCurrent.value.ID;
                }
                /* thisID > otherID */
            }            
        } else { /* thisCurrent == null && otherCurrent != null */
            this.head = other.head;
        }
    }
    
    public Iterator<T> existentialIterator() {
    	return new SemiUniversalIterator<T>(head);
    }
    public Iterator<T> semiUniversalIterator() {
    	return new SemiUniversalIterator<T>(head);
    }
    public Iterator<T> universalIterator() {
    	return new UniversalIterator<T>(head);
    }
    
    /**
     * An {@link Iterator} that does not fail if structural changes to the list 
     * occur during its iteration 
     * (in controst to the {@link Iterator} implementations provided
     * by the Java Collections Framework, which are <em>fail-fast</em>)
     * 
     * @author Peter Van Weert
     */
    @SuppressWarnings("hiding")
    private final static class UniversalIterator<T extends Constraint> implements Iterator<T> {
        private Node<T> next;
        
        UniversalIterator(Node<T> head) {
            next = head.next;
        }

        @SuppressWarnings("all")
        public boolean hasNext() {
        	Node<T> next = this.next;
        	if (next == null) return false;
        	if (next.value != null) return true;
        	
        	// path compression:
        	Node<T> trueNext = next.next;
        	while (trueNext != null) {
        		if (trueNext.value != null) {
        			do {
        				Node<T> temp = next.next;
        				next.next = trueNext;
        				next = temp;
        			} while (next != trueNext);
        			
        			this.next = trueNext;
        			return true;
        		}
        		trueNext = trueNext.next;
        	}
        	
        	do {
				Node<T> temp = next.next;
				next.next = null;
				next = temp;
			} while (next != null);
        	
        	this.next = null;
        	return false;
        }

        public T next() throws NoSuchElementException {
        	Node<T> next = this.next;
        	if (next == null) throw new NoSuchElementException();
        	T result = next.value;
        	if (result != null) {
        		this.next = next.next;
        		return result;
    		}
        	
        	// path compression:        	
        	Node<T> trueNext = next.next;
        	while (trueNext != null) {
        		if (trueNext.value != null) {
        			do {
        				Node<T> temp = next.next;
        				next.next = trueNext;
        				next = temp;
        			} while (next != trueNext);
        			
        			this.next = trueNext.next;
        			return trueNext.value;
        		}
        		trueNext = trueNext.next;
        	}
        	
        	do {
				Node<T> temp = next.next;
				next.next = null;
				next = temp;
			} while (next != null);
        	
        	this.next = null;
        	throw new NoSuchElementException();
        }

        /**
         * This operation is not supported by this iterator.
         * 
         * @throws UnsupportedOperationException
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String toString() {
        	if (hasNext())
        		return next.toString();
        	else
        		return "[]";
        }
    }
    
    /**
     * An {@link Iterator} that does not fail if items are added to the list 
     * during its iteration 
     * (in controst to the {@link Iterator} implementations provided
     * by the Java Collections Framework, which are <em>fail-fast</em>).
     * If items are removed from during iterationm,
     * behavior is undetermined.
     * 
     * @author Peter Van Weert
     */
    @SuppressWarnings("hiding")
    private final static class SemiUniversalIterator<T extends Constraint> implements Iterator<T> {
        /*
         * @invariant (next == head) || (hasNext ^ (next == null)) 
         */
        private Node<T> next;
        
        SemiUniversalIterator(Node<T> head) {
            next = head.next;
        }

        @SuppressWarnings("all")
        public boolean hasNext() {
			return next != null;
        }

        public T next() throws NoSuchElementException {
        	try {
        		T result = next.value;
    			next = next.next;
				return result;
        	} catch (NullPointerException npe) {
        		throw new NoSuchElementException();
        	}
        }

        /**
         * This operation is not supported by this iterator.
         * 
         * @throws UnsupportedOperationException
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String toString() {
        	if (hasNext())
        		return next.toString();
        	else
        		return "[]";
        }
    }
    
	/**
	 * Returns the first element of the list.
	 * 
	 * @return The first element of the list.
	 * @throws NoSuchElementException 
	 *   If the list is empty.
	 */
	public T getFirst() throws NoSuchElementException {
	    try {
	        return head.next.value;
	    } catch (NullPointerException npe) {
	        throw new NoSuchElementException();
	    }
	}

	/**
	 * Geeft een iterator terug over deze lijst. Deze iterator is <b>NIET</b>
	 * fail-fast zoals dat gebruikelijk is bij de Java-Collections implementaties.
	 * 
	 * @return Een iterator terug over deze lijst. Deze iterator is <b>NIET</b>
	 *  fail-fast zoals dat gebruikelijk is bij de Java-Collections implementaties.
	 */
	public Iterator<T> iterator() {
	    return new UniversalIterator<T>(head);
	}

	/**
	 * Maakt de lijst van elementen los van dit object. Merk op dat de lijst
	 * wel blijft verderbestaan, hij wordt niet vernietigd. Bestaande
	 * iteratoren kunnen dus gerust verder itereren.
	 * 
	 * @post isEmpty()
	 */
	public void reset() {
	    head.next = null;
	}

	public void terminate() {
	    head = null;
	}

	public boolean isTerminated() {
	    return (head == null);
	}

	/**
	 * Geeft <code>true</code> terug als deze lijst geen elementen bevat.
	 *  
	 * @return getSize() == 0 
	 */
	public boolean isEmpty() {
		// the head.next pointer is always consistent /* XXX: is this correct? */		
	    return head.next == null;
	}

	/**
	 * Returns the number of constraints in this list.
	 */
	public int size() {
	    int result = 0;
	    for (Node<?> n = head.next; n != null; n = n.next)
	    	if (n.value != null) result++;
	    return result;
	}

	@Override
	public String toString() {
	    return head.toString();
	}
	
	public final static class Node<T extends Constraint> implements Terminatable {
		T value;
		Node<T> previous, next;
		
		Node() {
			// protected constructor
		}
		Node(Node<T> next) {
			this.next = next;
			next.previous = this;
		}
		Node(T value, Node<T> previous) {
			this.previous = previous;
			this.value = value;
			value.addStorageBackPointer(this);
		}
		
		public void terminate() {
			value = null;
			if ((previous.next = next) != null)
				next.previous = previous;
		}
		
		public boolean isTerminated() {
			return value == null;
		}
		
		@Override
		public String toString() {
			if (value == null)
				return (next == null)? "[]" : next.toString();

			StringBuilder result = new StringBuilder();
			result.append('[').append(value);
			Node<T> next = this.next;
			while (next != null) {
				result.append(", ").append(next.value);
				next = next.next;
			}
			return result.append(']').toString();
		}
	}
}