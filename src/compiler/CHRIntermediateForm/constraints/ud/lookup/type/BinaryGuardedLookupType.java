package compiler.CHRIntermediateForm.constraints.ud.lookup.type;

import static compiler.CHRIntermediateForm.arg.visitor.VariableCollector.collectVariables;
import static util.collections.CollectionUtils.disjoint;

import java.util.SortedSet;
import java.util.TreeSet;

import compiler.CHRIntermediateForm.arg.argument.FormalArgument;
import compiler.CHRIntermediateForm.arg.argumented.IArgumented;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.java.EnumEquality;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.matching.MatchingInfos;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.PrimitiveType;
import compiler.CHRIntermediateForm.variables.Variable;

public class BinaryGuardedLookupType extends LookupType {

    private SortedSet<BinaryGuardInfo> guards;
    
    public BinaryGuardedLookupType(IndexType indexType) {        
        super(indexType);
        setGuards(new TreeSet<BinaryGuardInfo>());
    }
    
    public void addGuard(int variableIndex, IGuardConjunct guard, int otherIndex) 
    throws ClassCastException, IllegalArgumentException {
        addGuard(variableIndex, (IArgumented<?>)guard, otherIndex);
    }
    
    protected void addGuard(int variableIndex, IArgumented<?> guard, int otherIndex)
    throws IllegalArgumentException {
        assert (guard.getExplicitArity() == 2);
        
        if (!getGuards().add(new BinaryGuardInfo(variableIndex, guard, otherIndex)))
            throw new IllegalArgumentException();
    }
    
    public SortedSet<BinaryGuardInfo> getGuards() {
        return guards;
    }
    public int getNbGuards() {
        return getGuards().size();
    }
    protected void setGuards(SortedSet<BinaryGuardInfo> guards) {
        this.guards = guards;
    }
    
    public boolean testArrayability() {
    	if (getNbGuards() == 1) {
    		BinaryGuardInfo info = getGuards().first();
    		if (info.getGuardConjunct().isEquality()) {
    			IType type = info.getOneType();
    			if (type == PrimitiveType.BOOLEAN
    					|| type.isDirectlyAssignableTo(EnumEquality.ENUM_TYPE))
					return true;
    		}
    	}
    	return false;
    }
    
    public int[] getVariableIndices() {
        int result[] = new int[getNbGuards()], i = 0;
        for (BinaryGuardInfo info : getGuards())
            result[i++] = info.getVariableIndex();
        return result;
    }
    
    @Override
    public boolean equals(Object other) {
        return (other instanceof BinaryGuardedLookupType)
            && this.equals((BinaryGuardedLookupType)other);
    }
    
    public boolean equals(BinaryGuardedLookupType other) {
        return super.equals(other)
            && this.getGuards().equals(other.getGuards());
    }
    
    public static class BinaryGuardInfo implements Comparable<BinaryGuardInfo> {
        private int variableIndex;
        
        private int otherIndex;
        
        private IArgumented<?> guard;
        
        public BinaryGuardInfo(int variableIndex, IArgumented<?> guard, int otherIndex) {
            setVariableIndex(variableIndex);
            setOtherIndex(otherIndex);

            // een lekker lange uitdrukking nu:
            setGuard(guard.getArgumentable().createInstance(
                MatchingInfos.DIRECT_MATCH, 
                new Arguments(
                    (otherIndex == 0)
                        ? FormalArgument.getOtherInstance(guard.getExplicitArgumentAt(0).getType())
                        : FormalArgument.getOneInstance(((Variable)guard.getExplicitArgumentAt(0)).getVariableType())
                ,
                	(otherIndex == 1)
                        ? FormalArgument.getOtherInstance(guard.getExplicitArgumentAt(1).getType())
                        : FormalArgument.getOneInstance(((Variable)guard.getExplicitArgumentAt(1)).getVariableType())
                )
            ));
        }
        
        public int getVariableIndex() {
            return variableIndex;
        }
        protected void setVariableIndex(int variableIndex) {
            this.variableIndex = variableIndex;
        }

        public IArgumented<?> getGuard() {
            return guard;
        }
        public IGuardConjunct getGuardConjunct() {
        	return (IGuardConjunct)getGuard();
        }
        protected void setGuard(IArgumented<?> guard) {
            this.guard = guard;
        }
        
        public IType getOneType() {
        	return getGuard().getExplicitArgumentAt(getOneIndex()).getType();
        }
        
        public IType getOtherType() {
            return getGuard().getExplicitArgumentAt(getOtherIndex()).getType();
        }
        public String getOtherTypeString() {
            return getOtherType().toTypeString();
        }
        
        protected int getOneIndex() {
        	int o = getOtherIndex();
        	return --o*o;
        }
        
        protected int getOtherIndex() {
            return otherIndex;
        }
        protected void setOtherIndex(int otherIndex) {
            this.otherIndex = otherIndex;
        }
        
        @Override
        public boolean equals(Object other) {
            return (other instanceof BinaryGuardInfo)
                && this.equals((BinaryGuardInfo)other);
        }
        
        public boolean equals(BinaryGuardInfo other) {
            return this.getOtherType().equals(other.getOtherType());
        }
        
        @Override
        public String toString() {
            return guard.toString() + ':' + getOtherIndex();
        }
        
        public int compareTo(BinaryGuardInfo other) {
            return this.getVariableIndex() - other.getVariableIndex();
        }
    }

    public boolean isSeededBy(Occurrence occurrence) {
    	SortedSet<Variable> one = occurrence.getVariables();
    	for (BinaryGuardInfo guard : getGuards())
    		if (!disjoint(one, collectVariables(guard.getGuard()))) return true;
    	return false;
    }
}