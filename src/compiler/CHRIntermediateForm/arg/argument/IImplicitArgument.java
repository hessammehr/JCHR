package compiler.CHRIntermediateForm.arg.argument;

import java.util.Set;

import compiler.CHRIntermediateForm.Cost;
import compiler.CHRIntermediateForm.exceptions.AmbiguityException;
import compiler.CHRIntermediateForm.members.Field;
import compiler.CHRIntermediateForm.members.Method;

/**
 * @author Peter Van Weert
 */
public interface IImplicitArgument extends IArgument {

    public Set<Method> getMethods(String id);
    
    public Field getField(String name) 
    	throws AmbiguityException, NoSuchFieldException;
    
    public Cost getExpectedCost();
}
