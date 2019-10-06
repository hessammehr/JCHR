package compiler.CHRIntermediateForm.arg.visitor;

import compiler.CHRIntermediateForm.arg.argumented.IBasicArgumented;
import compiler.CHRIntermediateForm.variables.Variable;

public class VariableScanner extends AbstractVariableScanner {

    private Variable scanFor;
    
    public VariableScanner(Variable scanFor) {
        this.scanFor = scanFor;
    }
    
    @Override
    protected boolean scanVariable(Variable variable) {
        return variable == scanFor;
    }
    
    /*
     * dis-ambiguating between two other static methods...
     */
    public static boolean scanFor(
        IBasicArgumented visitable, Variable variable
    ) {
        return new VariableScanner(variable).scan(visitable);
    }
    
    public static boolean scanFor(
        ILeafArgumentVisitable visitable, Variable variable
    ) {
        return new VariableScanner(variable).scan(visitable);
    }
    
    public static <T extends ILeafArgumentVisitable> boolean scanFor(
        Iterable<T> visitables, Variable variable
    ) {
        VariableScanner scanner = new VariableScanner(variable);
        for (T visitable : visitables)
            if (scanner.scan(visitable)) return true;
        return false;
    }
}