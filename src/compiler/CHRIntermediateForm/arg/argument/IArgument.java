package compiler.CHRIntermediateForm.arg.argument;

import annotations.JCHR_Fixed;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitable;
import compiler.CHRIntermediateForm.matching.IAssignable;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.variables.NamelessVariable;

/**
 * @author Peter Van Weert
 */
@JCHR_Fixed
public interface IArgument extends IAssignable, IArgumentVisitable {
    /**
     * Returns the type of this argument.
     * 
     * @return The type of this argument.
     * 
     * @throws UntypedArgumentException
     *  If the argument is not typed 
     *  (the only untyped argument is the {@link NamelessVariable}!).
     */
    public IType getType() throws UntypedArgumentException;
    
    /**
     * Returns whether or not the type of this argument was annotated with 
     * the fixed type modifier.  
     * 
     * @return <code>true</code> if the type of this argument was annotated 
     *  with the fixed type modifier; <code>false</code> otherwise.
     * 
     * @throws UntypedArgumentException
     *  If the argument is not typed 
     *  (the only untyped argument is the {@link NamelessVariable}!).
     */
    public boolean isFixed() throws UntypedArgumentException;
    
    /**
     * Returns whether this argument is a constant value or not (e.g. a
     * either a primitive value, a reference value whose properties
     * can never change, or a final field of one of the former types).
     * <br/>
     * The essential question you should ask if implementing this inspector
     * is the following: can the result of the evaluation of this argument 
     * change if done later (or earlier) in time.
     * 
     * @return <code>true</code> if this argument is a constant value;
     *  <code>false</code> otherwise.
     */
    public boolean isConstant();

    
    /**
     * Thrown when trying to access the type of argument that is not 
     * typed. Important: the only untyped argument is the singleton
     * {@link NamelessVariable}, and this exception should never occur,
     * since it the latter variable is supposed to be treated as a
     * special case at all times.
     * 
     * @author Peter Van Weert
     * 
     * @see NamelessVariable
     */
    public static class UntypedArgumentException extends UnsupportedOperationException {
        private static final long serialVersionUID = 1L;
    }
}