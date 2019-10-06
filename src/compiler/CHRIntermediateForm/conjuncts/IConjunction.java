package compiler.CHRIntermediateForm.conjuncts;

import java.util.List;
import java.util.NoSuchElementException;

import util.iterator.ListIterable;

import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitable;

public interface IConjunction<T extends IConjunct>
    extends ListIterable<T>, IArgumentVisitable, IConjunctVisitable {

    public List<T> getConjuncts();

    public T[] getConjunctsArray(T[] result);

    public boolean hasConjuncts();

    public int getLength();

    public T getConjunctAt(int index);

    public int indexOf(T conjunct) throws NoSuchElementException;

}