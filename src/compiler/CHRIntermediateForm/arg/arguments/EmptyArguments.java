package compiler.CHRIntermediateForm.arg.arguments;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import util.collections.Empty;
import util.iterator.EmptyIterator;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argument.IImplicitArgument;
import compiler.CHRIntermediateForm.matching.MatchingInfos;
import compiler.CHRIntermediateForm.types.IType;

public final class EmptyArguments implements IArguments {
    
    private EmptyArguments() { /* SINGLETON */ }    
    private static EmptyArguments instance;
    public static EmptyArguments getInstance() {
        if (instance == null)
            instance = new EmptyArguments();
        return instance;
    }
    
    public boolean isMutable() {
        return false;
    }
    
    public List<IArgument> asList() {
        return Empty.getInstance();
    }
    public Iterator<IArgument> iterator() {
        return EmptyIterator.getInstance();
    }
    public ListIterator<IArgument> listIterator() {
        return EmptyIterator.getInstance();
    }
    public ListIterator<IArgument> listIterator(int index) throws IndexOutOfBoundsException {
        if (index == 0)
            return listIterator();
        else
            throw new util.exceptions.IndexOutOfBoundsException(index);
    }

    public int getArity() {
        return 0;
    }

    public IArgument getArgumentAt(int index) {
        throw new IndexOutOfBoundsException();
    }
    
    public int getIndexOf(IArgument argument) {
        return -1;
    }

    public void replaceArgumentAt(int index, IArgument arguement) {
        throw new IndexOutOfBoundsException();
    }

    public IType[] getTypes() {
        return new IType[0];
    }

    public IType getTypeAt(int index) {
        throw new IndexOutOfBoundsException();
    }

    public void addArgument(IArgument argument) {
        throw new IndexOutOfBoundsException();
    }

    public void addArgumentAt(int index, IArgument argument) {
        throw new IndexOutOfBoundsException();            
    }

    public void addImplicitArgument(IImplicitArgument implicitArgument) {
        throw new IndexOutOfBoundsException();
    }

    public void removeImplicitArgument() {
        throw new UnsupportedOperationException();            
    }

    public void markFirstAsImplicitArgument() {
        throw new UnsupportedOperationException();            
    }

    public void removeImplicitArgumentMark() {
        throw new UnsupportedOperationException();            
    }

    public boolean hasImplicitArgument() {
        return false;
    }

    public void incorporate(MatchingInfos assignmentInfos, boolean ignoreImplicitArgument) {
        // NOP
    }

    public boolean allFixed() {
        return true;
    }
    public boolean allConstant() {
        return true;
    }
    
    /**
     * Throws CloneNotSupportedException.  This guarantees that 
     * the "singleton" status is preserved.
     *
     * @return (never returns)
     * @throws CloneNotSupportedException
     *  Cloning of a singleton is not allowed!
     */
    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IArguments)
            && (((IArguments)obj).getArity() == 0);
    }
    
    @Override
    public String toString() {
        return "()";
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
}