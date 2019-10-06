package compiler.CHRIntermediateForm.conjuncts;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import util.collections.Singleton;
import util.exceptions.IndexOutOfBoundsException;
import util.iterator.SingletonIterator;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;

public class ConjunctConjunction<T extends IConjunct> implements IConjunction<T> {

    private T conjunct;
    
    public ConjunctConjunction(T conjunct) {
        setConjunct(conjunct);
    }
    
    public T getConjunct() {
        return conjunct;
    }
    protected void setConjunct(T conjunct) {
        this.conjunct = conjunct;
    }
    
    public T getConjunctAt(int index) {
        if (index != 0) throw new IndexOutOfBoundsException(index);
        return getConjunct();
    }
    
    public List<T> getConjuncts() {
        return new Singleton<T>(getConjunct());
    }
    @SuppressWarnings("unchecked")
    public T[] getConjunctsArray(T[] result) {
        if (result == null || result.length == 0)
            return (T[]) new Object[] { getConjunct() };
        result[0] = getConjunct();
        if (result.length > 1) result[1] = null;
        return result;
    }
    
    public int getLength() {
        return 1;
    }
    public boolean hasConjuncts() {
        return true;
    }
    
    public int indexOf(T conjunct) throws NoSuchElementException {
        if (getConjunct().equals(conjunct)) 
            return 0;
        else
            throw new NoSuchElementException(conjunct.toString());
    }
    
    public Iterator<T> iterator() {
        return listIterator();
    }
    public ListIterator<T> listIterator() {
        return new SingletonIterator<T>(getConjunct());
    }
    public ListIterator<T> listIterator(int index) throws IndexOutOfBoundsException {
        if (index != 0) throw new IndexOutOfBoundsException(index);
        return listIterator();
    }
    
    public void accept(IArgumentVisitor visitor) throws Exception {
        visitor.resetVisiting();
        getConjunct().accept(visitor);
    }
    public void accept(IConjunctVisitor visitor) throws Exception {
        getConjunct().accept(visitor);
    }
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        getConjunct().accept(visitor);
    }
    
    @Override
    public String toString() {
    	return getConjunct().toString();
    }
}
