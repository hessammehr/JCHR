package compiler.CHRIntermediateForm.constraints.ud.schedule;

public abstract class AbstractVariableInfo implements IVariableInfo {
    public int compareTo(IVariableInfo other) {
        return this.getDeclarationIndex() - other.getDeclarationIndex();
    }
    
    @Override
    public String toString() {
        return getActualVariable().toString()
            + " = " + getDeclaringOccurrence()
            + '#' + getFormalVariable()
            + " @ " + getDeclarationIndex();
    }
}