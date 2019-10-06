package compiler.CHRIntermediateForm.arg.argument;

import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;

/**
 * @author Peter Van Weert
 */
public abstract class LeafArgument extends Argument implements ILeafArgument {

    @Override
    public abstract String toString();
    
    public void accept(IArgumentVisitor visitor) throws Exception {
        accept((ILeafArgumentVisitor)visitor);
    }
}