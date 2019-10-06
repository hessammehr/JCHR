package compiler.CHRIntermediateForm.conjuncts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import util.collections.CollectionPrinter;

import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;

public class Conjunction<T extends IConjunct> implements IConjunction<T> {

    private List<T> conjuncts;
    
    protected Conjunction() {
        setConjuncts(new ArrayList<T>(4));
    }
    protected Conjunction(Conjunction<T> conjunction) {
        setConjuncts(conjunction.getConjunctsRef());
    }
    protected Conjunction(T... initial) {
        setConjuncts(new ArrayList<T>(Arrays.asList(initial)));
    }
    protected Conjunction(List<T> initial) {
        setConjuncts(initial);
    }

    public List<T> getConjuncts() {
        return Collections.unmodifiableList(getConjunctsRef());
    }
    public T[] getConjunctsArray(T[] result) {
        return getConjunctsRef().toArray(result);
    }
    protected List<T> getConjunctsRef() {
        return conjuncts;
    }
    
    protected void setConjuncts(List<T> conjuncts) {
        this.conjuncts = conjuncts;
    }
    
    /**
     * @param fromIndex
     * 	low endpoint (inclusive) of the subconjunction
     */
    public Conjunction<T> getSubConjunction(int fromIndex) {
        return getSubConjunction(fromIndex, getLength());
    }
    /**
     * @param fromIndex
     * 	low endpoint (inclusive) of the subconjunction
     * @param toIndex
     * 	upper endpoint (exclusive) of the subconjunction
     */
    public Conjunction<T> getSubConjunction(int fromIndex, int toIndex) {
        return new Conjunction<T>(getConjunctsRef().subList(fromIndex, toIndex));
    }
    
    public Iterator<T> iterator() {
        return getConjunctsRef().iterator();
    }
    public ListIterator<T> listIterator() {
        return getConjunctsRef().listIterator();
    }
    public ListIterator<T> listIterator(int index) throws IndexOutOfBoundsException {
        return getConjunctsRef().listIterator(index);
    }

    public void addConjunct(T conjunct) {
        getConjunctsRef().add(conjunct);
    }
    
    public boolean isEmpty() {
    	return getLength() == 0;
    }

    public boolean hasConjuncts() {
        return getLength() > 0;
    }

    public int getLength() {
        return getConjunctsRef().size();
    }

    public T getConjunctAt(int index) {
        return getConjunctsRef().get(index);
    }
    
    public int indexOf(T conjunct) throws NoSuchElementException {
        int result = getConjunctsRef().indexOf(conjunct);
        if (result < 0) throw new NoSuchElementException();
        return result;
    }

    @Override
    public String toString() {
        return hasConjuncts()
            ? CollectionPrinter.getCommaSeperatedInstance()
                                    .toString(getConjunctsRef())
            : "true";
    }

    public void accept(IArgumentVisitor visitor) throws Exception {
        for (T conjunct : this) {
            visitor.resetVisiting();
            conjunct.accept(visitor);
        }
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        for (T conjunct : this) conjunct.accept(visitor);
    }
    
    public void accept(IConjunctVisitor visitor) throws Exception {
        for (T conjunct : this) conjunct.accept(visitor);
    }
    
    public static <T extends IConjunct> void visit(
        IConjunctVisitor visitor, T... conjuncts
    ) throws Exception{        
        for (T conjunct : conjuncts) conjunct.accept(visitor);
    }
    public static <T extends IConjunct> void visit(
        IConjunctVisitor visitor, Collection<T> conjuncts
    ) throws Exception{    
        for (T conjunct : conjuncts) conjunct.accept(visitor);
    }
    
    public static <T extends IGuardConjunct> void visit(
        IGuardConjunctVisitor visitor, T... conjuncts
    ) throws Exception{        
        for (T conjunct : conjuncts) conjunct.accept(visitor);
    }
    public static <T extends IGuardConjunct> void visit(
        IGuardConjunctVisitor visitor, Collection<T> conjuncts
    ) throws Exception{    
        for (T conjunct : conjuncts) conjunct.accept(visitor);
    }
    
    /**
     * Returns <code>true</code> if, after a first execution,
     * executing this conjuncion a second time has no effect;
     * returns <code>false</code> if this not the case, 
     * or if it is not know to be the case.
     * 
     * @return <code>true</code> iff this conjunction is idempotent.
     */
    public boolean isIdempotent() {
    	return IdempotenceVisitor.isIdempotent(this);
    }
}