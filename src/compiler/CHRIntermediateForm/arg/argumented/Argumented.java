package compiler.CHRIntermediateForm.arg.argumented;

import java.util.Iterator;
import java.util.SortedSet;

import util.comparing.Comparison;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumentable.IArgumentable;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.VariableCollector;
import compiler.CHRIntermediateForm.matching.MatchingInfos;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.variables.Variable;

/**
 * @author Peter Van Weert
 */
public abstract class Argumented<T extends IArgumentable<?>> 
                extends BasicArgumented implements IArgumented<T> {    
    
    private T type;
    
    public Argumented(T type, IArguments arguments) {
        super(arguments);
        setType(type);                
    }
    
    public Argumented(T type, IArgument... arguments) {
        super(arguments);
        setType(type);                
    }
    
    @Override
    public void incorporate(MatchingInfos assignmentInfos, boolean ignoreImplicitArgument) {
        throw new UnsupportedOperationException();
    }

    public T getArgumentable() {
        return type;
    }
    public boolean hasAsArgumentableType(T type) {
        return getArgumentable().equals(type);
    }
    protected void setType(T type) {
        this.type = type;
    }
    
    @Override
    public Iterator<IArgument> iterator() {
        return getArguments().iterator();
    }
    
    public IType[] getFormalParameterTypes() {
        return getArgumentable().getFormalParameterTypes();
    }
    
    
    public IArgumented<T> createInstance(MatchingInfos assignmentInfos, IArgument... arguments) {
        throw new UnsupportedOperationException();
    }
    public IArgumented<T> createInstance(IArgument... arguments) {
        throw new UnsupportedOperationException();
    }
    public IArgumented<T> createInstance(MatchingInfos assignmentInfos, IArguments arguments) {
        throw new UnsupportedOperationException();
    }
    public IArgumented<T> createInstance(IArguments arguments) {
        throw new UnsupportedOperationException();
    }
    
    public IType[] getExplicitFormalParameterTypes() {
        return getArgumentable().getExplicitFormalParameterTypes();
    }
    
    public IType getExplicitFormalParameterTypeAt(int index) {
        return getArgumentable().getExplicitFormalParameterTypeAt(index);
    }
    
    public IType getFormalParameterTypeAt(int index) {
        return getArgumentable().getFormalParameterTypeAt(index);
    }
    
    public boolean haveToIgnoreImplicitArgument() {
        return getArgumentable().haveToIgnoreImplicitArgument();
    }
    public IArgument getExplicitArgumentAt(int index) {
        return getArgumentAt(index + (haveToIgnoreImplicitArgument()? 1 : 0));
    }
    public IArguments getExplicitArguments() {
        if (haveToIgnoreImplicitArgument())
            return new Arguments(getArguments().asList().subList(1, getArity()));
        else
            return getArguments();
    }
    
    public Comparison compareWith(IArgumentable<?> other) {
        throw new UnsupportedOperationException();
    }
    
    public int getNbVariables() {
        return getVariables().size();
    }
    
    public SortedSet<Variable> getVariables() {
        return VariableCollector.collectVariables(this);
    }

    public boolean isValid() {
        if (getArity() != getArgumentable().getArity()) return false;
        int i = 0;
        for (IArgument argument : this)
            if (! isValidArgument(argument, i++))
                return false;
        return true;
    }
    
    protected boolean isValidArgument(IArgument argument, int index) {
        return argument.getType().isDirectlyAssignableTo(getFormalParameterTypeAt(index));
    }
    
    public int getExplicitArity() {
        return getArity() - (haveToIgnoreImplicitArgument()? 1 : 0);
    }
    
    @Override
    public void visitArguments(IArgumentVisitor visitor) throws Exception {
        if (! visitor.recurse()) return;
        if (visitor.explicitVariablesOnly() && haveToIgnoreImplicitArgument()) {
            for (int i = 1; i < getArity(); i++) 
                getArgumentAt(i).accept(visitor);
        } else {
            super.visitArguments(visitor);
        }
    }
    @Override
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        if (visitor.explicitVariablesOnly() && haveToIgnoreImplicitArgument()) {
            for (int i = 1; i < getArity(); i++) 
                getArgumentAt(i).accept(visitor);
        } else {
            super.accept(visitor);
        }
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ getArgumentable().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return super.equals(obj)
            && (obj instanceof Argumented)
            && ((Argumented<?>)obj).getArgumentable().equals(getArgumentable());
    }
    
}