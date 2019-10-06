package compiler.CHRIntermediateForm.arg.argumented;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumentable.IArgumentable;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;

/**
 * @author Peter Van Weert
 */
public interface IArgumented<T extends IArgumentable<?>> 
extends IBasicArgumented, IArguments, IArgumentable<T> {
    
    public T getArgumentable();
    
    public boolean hasAsArgumentableType(T type);
    
    public IArguments getExplicitArguments();
    
    /**
     * Returns the explicit argument with the given index. This means that 
     * if we have to ignore the implicit argument (e.g. a solver), the result
     * will be the argument with index <code>(index+1)</code>.
     * 
     * @param index
     *  The explicit index.
     * @return The explicit argument with the given index. This means that 
     *  if we have to ignore the implicit argument (e.g. a solver), the result
     *  will be the argument with index <code>(index+1)</code>.
     */
    public IArgument getExplicitArgumentAt(int index);
    
    /**
     * Checks whether this argumented is in a valid state. One obvious
     * requirement is that all arguments are set and have a valid type.
     * 
     * @return If the arity is invalid, or the type of one of the types
     *  is not equal to the formal type, the result will be <code>false</code>.
     */
    public boolean isValid();
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception;
    
    public void accept(IArgumentVisitor visitor) throws Exception;
}