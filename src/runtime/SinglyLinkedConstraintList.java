package runtime;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * @author Peter Van Weert
 */
public class SinglyLinkedConstraintList<T extends Constraint> 
	implements ConstraintIterable<T> {
	
	/**
     * The head of this list.
     */
    protected Node<T> head;

	public SinglyLinkedConstraintList() {
		// NOP
    }
	
	public SinglyLinkedConstraintList(T initialConstraint) {
        head = new Node<T>(initialConstraint);
    }

    /**
     * Voegt een element <code>x</code> vooraan toe aan de lijst. Hierdoor
     * hebben iteratoren die over de lijst aan het itereren zijn er geen last
     * van.
     * 
     * @param constraint
     *  Het toe te voegen element.
     */
    public void addFirst(T constraint) {
        head = new Node<T>(constraint, head);
    }
    
    public Iterator<T> iterator() {
    	return new UniversalIterator<T>(head);
    }

    public Iterator<T> existentialIterator() {
    	return new ExistentialIterator<T>(head);
    }
    public Iterator<T> semiUniversalIterator() {
    	return new UniversalIterator<T>(head);
    }
    public Iterator<T> universalIterator() {
    	return new UniversalIterator<T>(head);
    }

    /**
     * Empties this list, without disturbing possible iterators.
     * 
     * @post isEmpty()
     */
    public void reset() {
        head = null;
    }
    
    /**
     * Geeft <code>true</code> terug als deze lijst geen elementen bevat.
     *  
     * @return size() == 0 
     */
    public boolean isEmpty() {
        return head == null;
    }
    
    public int size() {
        int result = 0;
        Node<T> current = head;
        while (current != null) {
        	result++;
        	current = current.next;
        }
        return result;
    }
    
    @Override
    public String toString() {
        return head == null? "[]" : head.toString();
    }
    
    @SuppressWarnings("hiding")
    protected final static class UniversalIterator<T extends Constraint> implements Iterator<T> {
        /*
         * @invariant current != null
         */
        private Node<T> next;
        
        public UniversalIterator(Node<T> head) {
            next = head;
        }

        public boolean hasNext() {
        	Node<T> next = this.next;
        	if (next == null) return false;
            if (next.value.isAlive()) return true;
            
            // path compression:
            Node<T> trueNext = next;
    		do {
    			if ((trueNext = trueNext.next) == null) {
    				this.next = null;
    				do {
    					Node<T> temp = next.next;
            			next.next = null;
            			next = temp;
            		} while (next != null);
        			return false;
    			}
    		} while (trueNext.value.isTerminated());
    		
    		this.next = trueNext;
    		do {
    			Node<T> temp = next.next;
    			next.next = trueNext;
    			next = temp;
    		} while (next.value.isTerminated());
        	return true;
        }

        public T next() throws NoSuchElementException {
        	Node<T> next = this.next;
        	if (next == null) throw new NoSuchElementException();
        	if (next.value.isAlive()) {
    			T result = next.value;
        		this.next = next.next;
        		return result;
    		}
        	
        	// path compression:        	
    		Node<T> trueNext = next;
    		do {
    			if ((trueNext = trueNext.next) == null) {
    				this.next = null;
    				do {
    					Node<T> temp = next.next;
            			next.next = null;
            			next = temp;
            		} while (next != null);
    				throw new NoSuchElementException();
    			}
    		} while (trueNext.value.isTerminated());
    		
    		do {
    			Node<T> temp = next.next;
    			next.next = trueNext;
    			next = temp;
    		} while (next != trueNext);

    		T result = trueNext.value;
    		this.next = trueNext.next;
    		return result;
        }

        /**
         * This operation is not supported by this <code>Iterator</code>.
         * 
         * @throws UnsupportedOperationException
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String toString() {
        	return (next == null)? "...]" : next.toString("..., ");
        }
    }
    
    @SuppressWarnings("hiding")
    protected final static class ExistentialIterator<T extends Constraint> implements Iterator<T> {
        /*
         * @invariant current != null
         */
        private Node<T> next;
        
        public ExistentialIterator(Node<T> head) {
            next = head;
        }

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
         * This operation is not supported by this <code>Iterator</code>.
         * 
         * @throws UnsupportedOperationException
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String toString() {
        	return (next == null)? "...]" : next.toString("..., ");
        }
    }
    
    @SuppressWarnings("hiding")
    protected final static class Node<T> {
    	protected Node(T value) {
    		this.value = value;
    	}
        
        protected Node(T value, Node<T> next) {
        	this.value = value;
            this.next = next;
        }
        
        /*
		  It is not a problem to make these fields public, as access is allready
		  limited by the fact that this is a protected class!
		  This way, subclasses of SingleLinkedList can use the fields
		  directly, without the need for getter-methods.
		*/
        public Node<T> next;
        
        public final T value;
        
        @Override
        public String toString() {
            return toString("[");
        }
        String toString(String init) {
        	StringBuilder result = new StringBuilder()
        		.append(init).append(value);
        	Node<T> next = this.next;
            while (next != null) {
            	result.append(", ").append(next.value);
            	next = next.next;
            }
            return result.append(']').toString();
        }
    }
    
    
    /**
     * Merges two list: this one and the given argument (<code>other</code>).
     * Both lists of constraints are sorted in descending ID, as will the result.
     * The result will be stored in this list. None of the iterators over
     * either list will be disturbed, nor will they start iterating over
     * constraints that weren't in <code>this</code> or <code>other</code>
     * respectively at the time the iterators were created. 
     * The constraints will be notified that they are added to the new
     * list (node). After the merge a constraint can be in the new list as well 
     * as in both old lists. Only when one of the lists is empty we can
     * reuse the other list.
     * 
     * @param other
     *  The list of constraints to merge with.
     */
    public void mergeWith(SinglyLinkedConstraintList<T> other) {
        Node<T> thisCurrent = this.head, 
               otherCurrent = other.head;
        
        if (otherCurrent == null) return; // nothing to merge
        if (thisCurrent != null) {
            int thisID = thisCurrent.value.ID,
            	otherID = otherCurrent.value.ID;
            
            Node<T> current;
            if (thisID > otherID) {
            	this.head = current = new Node<T>(thisCurrent.value);
            	thisCurrent = thisCurrent.next;
            	if (thisCurrent == null) {
        			current.next = otherCurrent;
        			return;
        		}
            	thisID = thisCurrent.value.ID;
            } else if (thisID < otherID) {
            	this.head = current = new Node<T>(otherCurrent.value);
            	otherCurrent = otherCurrent.next;
            	if (otherCurrent == null) {
        			current.next = thisCurrent;
        			return;
        		}
            	otherID = otherCurrent.value.ID;
            } else /* thisID == otherID */ {
            	this.head = current = new Node<T>(thisCurrent.value);
            	thisCurrent = thisCurrent.next;
            	otherCurrent = otherCurrent.next;
            	if (thisCurrent == null) {
        			current.next = otherCurrent;
        			return;
        		}
            	if (otherCurrent == null) {
        			current.next = thisCurrent;
        			return;
        		}
            	thisID = thisCurrent.value.ID;
            	otherID = otherCurrent.value.ID;
            }

            while (true) {
            	while (thisID > otherID) {
            		current.next = current = new Node<T>(thisCurrent.value);
            		thisCurrent = thisCurrent.next;
            		if (thisCurrent == null) {
            			current.next = otherCurrent;
            			return;
            		}
            		thisID = thisCurrent.value.ID;
            	}
            	/* thisID <= otherID */
            	if (thisID == otherID) {
            		thisCurrent = thisCurrent.next;       // otherCurrent will be added next!
            		if (thisCurrent == null) {
            			current.next = otherCurrent;
            			return;
            		}
            		thisID = thisCurrent.value.ID;
            	}
            	/* thisID < otherID */
            	do {
            		current = current.next = new Node<T>(otherCurrent.value);
            		otherCurrent = otherCurrent.next;
            		if (otherCurrent == null) {
            			current.next = thisCurrent;
            			return;
            		}
            		otherID = otherCurrent.value.ID;
            	} while (thisID < otherID);
            	/* thisID >= otherID */
            	if (thisID == otherID) {
                    otherCurrent = otherCurrent.next;     // thisCurrent will be added next!
                    if (otherCurrent == null) {
                    	current.next = thisCurrent;
            			return;
            		}
                    otherID = otherCurrent.value.ID;
            	}
            	/* thisID > otherID */
            }
        } else {/* thisCurrent == null && otherCurrent != null */
            this.head = other.head;
        }
    }
}