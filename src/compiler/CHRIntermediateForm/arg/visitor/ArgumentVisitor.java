package compiler.CHRIntermediateForm.arg.visitor;

import util.visitor.AbstractExtendedVisitor;

public abstract class ArgumentVisitor 
    extends AbstractExtendedVisitor
    implements IArgumentVisitor {

    private boolean explicitOnly;
    
    public ArgumentVisitor(boolean explicitOnly) {
        this.explicitOnly = explicitOnly;
    }
    
    public boolean explicitVariablesOnly() {
        return explicitOnly;
    }
    
    public boolean recurse() {
        return true;        // default implementation
    }
}
