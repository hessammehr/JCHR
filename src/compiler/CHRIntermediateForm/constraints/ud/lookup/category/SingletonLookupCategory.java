package compiler.CHRIntermediateForm.constraints.ud.lookup.category;

import java.util.Iterator;

import util.exceptions.IndexOutOfBoundsException;
import util.iterator.SingletonIterator;

import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;

public abstract class SingletonLookupCategory implements ILookupCategory {
    public boolean isMasterCategory() {
        return true;
    }
    public void setMasterCategory() {
        // NOP
    }
    
    protected abstract ILookupType getSingletonInstance();
    
    public Iterator<ILookupType> iterator() {
        return new SingletonIterator<ILookupType>(
            getSingletonInstance()
        );
    }
    
    public int getNbLookupTypes() {
        return 1;
    }
    
    public void addLookupType(ILookupType lookupType) {
        if (lookupType != getSingletonInstance())
            throw new UnsupportedOperationException();
    }
    
    public int getNbVariables() {
        return 0;
    }
    
    public int[] getVariableIndices() {
        return new int[0];
    }
    
    public int getIndexOf(ILookupType lookupType) {
        return (lookupType == getSingletonInstance())? 0 : -1;
    }
    public boolean contains(ILookupType lookupType) {
        return (lookupType == getSingletonInstance());
    }
    public ILookupType getLookupTypeAt(int index) {
        if (index == 0)
            return getSingletonInstance();
        else
            throw new IndexOutOfBoundsException(index, 0);
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
    public String toString() {
        return getClass().getSimpleName();
    }
}
