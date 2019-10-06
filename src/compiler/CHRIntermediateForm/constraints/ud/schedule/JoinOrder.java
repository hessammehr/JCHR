package compiler.CHRIntermediateForm.constraints.ud.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import util.Cloneable;
import util.Resettable;

public class JoinOrder extends AbstractJoinOrder 
        implements Resettable, Cloneable<JoinOrder> {
    
    private Elements elements;
    
    public JoinOrder() {
        setElements(new Elements());
    }
    
    public JoinOrder(int capacity) {
        setElements(new Elements(capacity));
    }
    
    public Iterator<IJoinOrderElement> iterator() {
        return listIterator();
    }
    public ListIterator<IJoinOrderElement> listIterator() {
        return getElements().listIterator();
    }

    public Elements getElements() {
        return elements;
    }
    public IJoinOrderElement getElementAt(int index) {
        return elements.get(index);
    }
    public void setElements(Elements components) {
        this.elements = components;
    }
    
    public void addElement(IJoinOrderElement element) {
        getElements().add(element);
    }
    public <T extends IJoinOrderElement> void addElements(Collection<T> elements) {
        getElements().addAll(elements);
    }
    public void addElementAt(IJoinOrderElement element, int index) {
        getElements().add(index, element);
    }
    
    public int size() {
        return getElements().size();
    }
    
    @Override
    public String toString() {
        return getElements().toString();
    }
    
    public void reset() {
        getElements().clear();
    }
    
    public void reset(int keep) {
        getElements().removeFrom(keep);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public JoinOrder clone() {
        try {
            JoinOrder result = (JoinOrder)super.clone();
            result.setElements(getElements().clone());
            return result;
            
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
    
    public final static class Elements 
        extends ArrayList<IJoinOrderElement> 
        implements Cloneable<Elements> {
        
        Elements() {
            super();
        }
        Elements(int capacity) {
            super(capacity);
        }
        <T extends IJoinOrderElement> Elements(T... elements) {
            this(Arrays.asList(elements));
        }
        Elements(Collection<? extends IJoinOrderElement> c) {
            super(c);
        }


        private static final long serialVersionUID = 1L;

        public void removeFrom(int fromIndex) {
            super.removeRange(fromIndex, size());
        }
        
        @Override
        public void removeRange(int fromIndex, int toIndex) {
            super.removeRange(fromIndex, toIndex);
        }
        
        @Override
        public Elements clone() {
            return (Elements)super.clone();
        }
    }
}
