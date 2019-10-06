package compiler.CHRIntermediateForm.init;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumented.IArgumented;

public interface IInitialisatorInvocation<T extends IInitialisator<?>> 
extends IArgumented<T>, IArgument {
    // no new methods
}
