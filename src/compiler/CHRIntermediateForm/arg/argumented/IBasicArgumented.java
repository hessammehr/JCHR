package compiler.CHRIntermediateForm.arg.argumented;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitable;

public interface IBasicArgumented extends Iterable<IArgument>, IArgumentVisitable {

    public IArguments getArguments();
    
    public int getArity();
}
