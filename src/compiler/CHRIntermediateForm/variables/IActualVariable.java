package compiler.CHRIntermediateForm.variables;

import compiler.CHRIntermediateForm.arg.argument.ILeafArgument;

/**
 * An actual variable is a variable that, unlike a formal variable,
 * is used as an argument.
 */
public interface IActualVariable extends IVariable, ILeafArgument {
    
    public boolean isAnonymous();
    public boolean isImplicit();
    public boolean isReactive();
    
}
