package compiler.CHRIntermediateForm.arg.visitor;

import compiler.CHRIntermediateForm.variables.Variable;

public class ImplicitVariableScanner extends AbstractVariableScanner {
    @Override
    protected boolean scanVariable(Variable variable) {
        return variable.isImplicit();
    }
}
