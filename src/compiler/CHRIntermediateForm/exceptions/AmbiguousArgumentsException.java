package compiler.CHRIntermediateForm.exceptions;

import java.util.Set;

import compiler.CHRIntermediateForm.arg.argumentable.IArgumentable;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;

/**
 * @author Peter Van Weert
 *
 */
public class AmbiguousArgumentsException extends ArgumentsException {
    private static final long serialVersionUID = 1L;
    
    public AmbiguousArgumentsException(IArgumentable<?> argumentable, IArguments arguments) {
        super(arguments + " are ambiguous for " + argumentable);
    }
    
    public <T extends IArgumentable<?>> AmbiguousArgumentsException(Set<T> argumentables, IArguments arguments) {
        super(arguments + " are ambiguous for candidates " + argumentables);
    }
}