package compiler.CHRIntermediateForm.constraints.ud.lookup.category;

import java.util.Iterator;
import java.util.NoSuchElementException;

import util.iterator.SingletonIterator;

import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;

public abstract class SingletonLookupCategories implements ILookupCategories {

    protected abstract SingletonLookupCategory getSingletonInstance();
    
    public Iterator<ILookupCategory> iterator() {
        return new SingletonIterator<ILookupCategory>(
            getSingletonInstance()
        );
    }
    
    public int getNbLookupCategories() {
        return 1;
    }

    public boolean contains(ILookupCategory category) {
        return category == getSingletonInstance();
    }
    public boolean contains(ILookupType lookupType) {
        return (lookupType == getSingletonInstance().getSingletonInstance());
    }
    
    public void addLookupCategory(ILookupCategory category) {
        throw new UnsupportedOperationException(this.toString());
    }
    
    public ILookupCategory getLookupCategory(ILookupType lookupType) {
        return contains(lookupType)
            ? getSingletonInstance()
            : null;
    }
    
    public ILookupCategory getMasterLookupCategory() {
        return getSingletonInstance();
    }
    
    public int getIndexOf(ILookupCategory lookupCategory) throws NoSuchElementException {
        if (lookupCategory == getSingletonInstance())
            return 0;
        else
            throw new NoSuchElementException();
    }
    
    public ILookupCategory[] toArray() {
        return new ILookupCategory[] { getSingletonInstance() };
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName();
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
    
    public boolean isTrivial() {
    	return true;
    }
}
