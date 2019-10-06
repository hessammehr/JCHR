package compiler.CHRIntermediateForm.arg.visitor;

import util.visitor.AbstractExtendedVisitor;

public abstract class AbstractLeafArgumentVisitor 
    extends AbstractExtendedVisitor     // not strictly necessary, but makes type hierarchy easier
    implements ILeafArgumentVisitor {
    
    private boolean explicitOnly;
    
    public AbstractLeafArgumentVisitor() {
        this(true);
    }
    
    public AbstractLeafArgumentVisitor(boolean explicitOnly) {
        this.explicitOnly = explicitOnly;
    }
    
    protected void setExplicitOnly(boolean explicitOnly) {
		this.explicitOnly = explicitOnly;
	}
    
    public boolean explicitVariablesOnly() {
        return explicitOnly;
    }
}
