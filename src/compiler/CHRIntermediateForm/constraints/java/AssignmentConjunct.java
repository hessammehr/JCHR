package compiler.CHRIntermediateForm.constraints.java;

import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitor;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunctVisitor;
import compiler.CHRIntermediateForm.types.IType;

public class AssignmentConjunct extends NoSolverConjunct {
    private boolean declaration;
    
    public AssignmentConjunct(Assignment constraint, IArguments arguments) {
        super(constraint, arguments);
    }

    public boolean isDeclaration() {
        return declaration;
    }
    public void setDeclarator() {
        declaration = true;
    }
    public IType getType() {
        return getArgumentAt(0).getType();
    }
    public String getTypeString() {
        return getType().toTypeString();
    }
    
    @Override
    public void accept(IGuardConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    @Override
    public void accept(IConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
