package compiler.CHRIntermediateForm.arg.argument.constant;

import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.PrimitiveType;

/**
 * @author Peter Van Weert
 */
public class BooleanArgument extends LiteralArgument<Boolean> {

    private BooleanArgument(Boolean value) {
        super(value);
    }

    private static BooleanArgument trueInstance;
    private static BooleanArgument falseInstance;
    public static BooleanArgument getInstance(boolean value) {
        if (value) 
            return getTrueInstance();
        else
            return getFalseInstance();
    }
    public static BooleanArgument getTrueInstance() {
        if (trueInstance == null)
            trueInstance = new BooleanArgument(Boolean.TRUE);
        return trueInstance;
    }
    public static BooleanArgument getFalseInstance() {
        if (falseInstance == null)
            falseInstance = new BooleanArgument(Boolean.FALSE);
        return falseInstance;
    }
    
    @Override
    public IType getType() {
        return PrimitiveType.BOOLEAN;
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}