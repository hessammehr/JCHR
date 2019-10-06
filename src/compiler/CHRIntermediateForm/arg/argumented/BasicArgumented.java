package compiler.CHRIntermediateForm.arg.argumented;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.arguments.ArgumentsDecorator;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.matching.MatchingInfos;

/**
 * @author Peter Van Weert
 */
public abstract class BasicArgumented
    extends ArgumentsDecorator
    implements IBasicArgumented {
    
    public BasicArgumented(IArguments arguments) {
        setArguments(arguments);       
    }
    
    public BasicArgumented(IArgument... arguments) {
        this(new Arguments(arguments));
    }
    
    public final MatchingInfos canHaveAsArguments(IArguments arguments) {
        if (arguments == getArguments()) 
            return MatchingInfos.DIRECT_MATCH;
        else
            throw new UnsupportedOperationException();
    }
    public final MatchingInfo canHaveAsArgumentAt(int index, IArgument argument) {
        if (argument == getArgumentAt(index))
            return MatchingInfo.DIRECT_MATCH;
        else
            throw new UnsupportedOperationException();
    }
    
    public void visitArguments(IArgumentVisitor visitor) throws Exception {
        if (! visitor.recurse()) return;
        for (IArgument argument : getArguments()) argument.accept(visitor);
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
    	for (IArgument argument : getArguments()) argument.accept(visitor);
    }
    
    @Override
    public int hashCode() {
        return getArguments().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return (obj instanceof BasicArgumented)
            && ((BasicArgumented)obj).getArguments().equals(getArguments());
    }
}