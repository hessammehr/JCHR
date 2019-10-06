package compiler.CHRIntermediateForm.members;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argument.IImplicitArgument;
import compiler.CHRIntermediateForm.arg.argumentable.AbstractMethod;
import compiler.CHRIntermediateForm.arg.argumented.Argumented;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.types.IType;

/**
 * @author Peter Van Weert
 */
public abstract class AbstractMethodInvocation<T extends AbstractMethod<?>> 
extends Argumented<T>
implements IArgument, IConjunct {
    
    public AbstractMethodInvocation(T type, IArguments arguments) {
        super(type, arguments);
        
    	if (!haveToIgnoreImplicitArgument() && !arguments.hasImplicitArgument())
    		throw new IllegalArgumentException();
    }
    
    public java.lang.reflect.Method getMethod() {
    	return getArgumentable().getMethod();
    }
    public String getMethodName() {
        return getArgumentable().getName();
    }
    
    public IType getType() {
        return getArgumentable().getReturnType();
    }
    
    public boolean canBeAskConjunct() {
        return getArgumentable().returnsBoolean();
    }
    
    public boolean canBeArgument() {
        return getArgumentable().isValidArgument(); 
    }
    
    public MatchingInfo isAssignableTo(IType type) {
        return getType().isAssignableTo(type);
    }
    public boolean isDirectlyAssignableTo(IType type) {
        return getType().isDirectlyAssignableTo(type);
    }
    
    public boolean isFixed() {
        return getType().isFixed();
    }
    public boolean isConstant() {
        return false;
    }
    
    public IImplicitArgument getImplicitArgument() {
        return (IImplicitArgument)getArgumentAt(0);
    }
    
    @Override
    public String toString() {
        return new StringBuilder()
            .append(getImplicitArgument()).append('.')
            .append(getMethodName()).append(getArguments())
            .toString();
    }
    
    public void accept(IArgumentVisitor visitor) throws Exception {
        if (visitor.isVisiting()) {
            visitor.visit(this);
            visitArguments(visitor);
            visitor.leave(this);
        } else {
            visitArguments(visitor);
        }
    }
}