package compiler.CHRIntermediateForm.constraints.ud.lookup.type;

public abstract class SingletonLookupType implements ILookupType {

    public int[] getVariableIndices() {
        return new int[0];
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
}