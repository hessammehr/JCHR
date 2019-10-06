package compiler.CHRIntermediateForm.init;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumentable.IArgumentable;
import compiler.CHRIntermediateForm.types.IType;

public interface IInitialisator<T extends IInitialisator<?>> extends IArgumentable<T> { 

    public boolean isConstructor();
    
    public IType getType();
    
    public int getIdentifierIndex();
    
    public boolean usesIdentifier();
    
    public IInitialisatorInvocation<T> getInstance();

    /**
     * @pre isValidDeclarationInitialisator()
     */
    public IInitialisatorInvocation<T> getInstance(String identifier);

    /**
     * @pre isValidDeclarationInitialisator()
     */
    public IInitialisatorInvocation<T> getInstance(IArgument argument);
    
    public IInitialisatorInvocation<T> getInstance(IArgument argument, String identifier);
    
    public boolean isValidInitialisator();
    
    public boolean isValidInitialisatorFrom(IType type);
    
    public boolean isValidDeclarationInitialisator();
}