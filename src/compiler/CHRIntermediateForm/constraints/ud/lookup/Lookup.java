package compiler.CHRIntermediateForm.constraints.ud.lookup;

import java.util.Iterator;

import util.Arrays;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumented.IBasicArgumented;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleElement;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleVisitor;
import compiler.CHRIntermediateForm.rulez.Head;
import compiler.CHRIntermediateForm.variables.IActualVariable;
import compiler.CHRIntermediateForm.variables.Variable;

public abstract class Lookup implements IScheduleElement, IBasicArgumented {

    private IActualVariable[] variables;
    
    public Lookup(IActualVariable[] variables) {
        setVariables(variables);
    }
    
    public abstract Occurrence getOccurrence();
    
    public UserDefinedConstraint getConstraint() {
        return getOccurrence().getArgumentable();
    }
    
    public boolean canChangeLookupInformation() {
        return getLookupCategory() == null;
    }

    public abstract ILookupType getLookupType();
    
    public abstract ILookupCategory getLookupCategory();
    
    /**
     * @throws NullPointerException
     *  If the given argument is <code>null</code>
     * @throws IllegalStateException
     *  If the lookup information can no longer be changed.
     */
    public abstract void setLookupCategory(ILookupCategory lookupCategory)
        throws NullPointerException, IllegalStateException;
    
    
    protected void setVariables(IActualVariable[] variables) {
        this.variables = variables;
    }
    public IActualVariable[] getVariables() {
        return variables;
    }
    
    public int getVariableIndexOf(Variable variable) {
        return Arrays.identityIndexOf(getVariables(), variable);
    }
    
    public void accept(IArgumentVisitor visitor) throws Exception {
        visitor.isVisiting();
        for  (IArgument argument : this) argument.accept(visitor);
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        for  (IArgument argument : this) argument.accept(visitor);
    }
    
    public int getArity() {
        return getArguments().getArity();
    }
    
    public int getIndexOf(IArgument argument) {
        return getArguments().getIndexOf(argument);
    }
    
    public Head getHead() {
        return getOccurrence().getHead();
    }
    public boolean isPositive() {
        return getOccurrence().isPositive();
    }
    public boolean isNegative() {
        return getOccurrence().isNegative();
    }
    public int getHeadNbr() {
        return getHead().getNbr();
    }

    public boolean isSingleton() {
        return getLookupCategory().isSingleton(getOccurrence());
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append(getOccurrence().getIdentifier()).append('(');
        
        IActualVariable[] variables = getVariables();
        if (variables.length > 0)
            builder.append(variables[0]);
        for (int i = 1; i < variables.length; i++)
            builder.append(", ").append(variables[i]);
        
        return builder.append(')').toString();
    }
    
    public void accept(IScheduleVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    public Iterator<IArgument> iterator() {
        return getArguments().iterator();
    }
    
    public boolean isSeededBy(Occurrence other) {
    	return getLookupType().isSeededBy(other);
    }
}
