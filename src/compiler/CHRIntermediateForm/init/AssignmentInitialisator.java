package compiler.CHRIntermediateForm.init;

import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.constraints.java.Assignment;
import compiler.CHRIntermediateForm.constraints.java.AssignmentConjunct;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.variables.Variable;

public class AssignmentInitialisator extends Assignment 
implements IDeclarator<AssignmentInitialisator> {
    
    private IInitialisator<?> initialisator;
    
    public AssignmentInitialisator(IInitialisator<?> initialisator) {
        super(initialisator.getType());
        if (! initialisator.isValidDeclarationInitialisator())
            throw new IllegalArgumentException();
        else
            setInitialisator(initialisator);
    }
    
    public IType getType() {
        return getArgumentType();
    }

    public boolean usesIdentifier() {
        return getInitialisator().usesIdentifier();
    }

    public AssignmentConjunct getInstance(Variable variable) {
        return createInstance(new Arguments(variable, getInitialisator().getInstance()));    
    }

    public AssignmentConjunct getInstance(Variable variable, String identifier) {
        return createInstance(new Arguments(variable, getInitialisator().getInstance(identifier)));
    }
    
    @Override
    public AssignmentConjunct createInstance(IArguments arguments) {
        final AssignmentConjunct result = super.createInstance(arguments);
        result.setDeclarator();
        return result;
    }
    
    protected IInitialisator<?> getInitialisator() {
        return initialisator;
    }
    protected void setInitialisator(IInitialisator<?> initialisator) {
        this.initialisator = initialisator;
    }
}
