package compiler.CHRIntermediateForm.conjuncts;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumentable.IArgumentable;
import compiler.CHRIntermediateForm.arg.argumented.Argumented;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;

public abstract class ArgumentedGuardConjunct<T extends IArgumentable<?>> 
    extends Argumented<T> 
    implements IGuardConjunct {

    public ArgumentedGuardConjunct(T type, IArgument... arguments) {
        super(type, arguments);
    }

    public ArgumentedGuardConjunct(T type, IArguments arguments) {
        super(type, arguments);
    }
    
    public boolean isNegated() {
        return false;
    }
    public boolean isEquality() {
        return false; // default implementation!
    }
}
