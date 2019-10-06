package compiler.CHRIntermediateForm.conjuncts;

import compiler.CHRIntermediateForm.arg.argumentable.Argumentable;
import compiler.CHRIntermediateForm.arg.argumentable.IArgumentable;
import compiler.CHRIntermediateForm.arg.argumented.IArgumented;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.variables.IActualVariable;
import compiler.CHRIntermediateForm.variables.Variable;

public class ImplicitGuardFactory {

    private ImplicitGuardFactory() { /* FACTORY */ }
    
    public static ImplicitGuardConjunct createImplicitGuard(
        Variable implicit, IActualVariable explicit, boolean positive
    ) {
        // we know how to compare variables of *the same type*!!
        IArgumented<?> argumented_eq = (IArgumented<?>)implicit.getVariableType().getEq();
        IArgumentable<?> argumentable_eq = argumented_eq.getArgumentable();
        
        // Replace the dummy variables with the correct variables:
        IArguments arguments = new Arguments(argumented_eq.getArguments());
        int i = Argumentable.getFirstArgumentIndex(argumentable_eq, arguments);
        arguments.replaceArgumentAt(i+0, implicit);
        arguments.replaceArgumentAt(i+1, explicit);
        
        // And we are done:
        return new ImplicitGuardConjunct((IGuardConjunct)
            argumentable_eq.createInstance(arguments),
            positive
        );
    }
}
