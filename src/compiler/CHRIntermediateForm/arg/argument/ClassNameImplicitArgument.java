package compiler.CHRIntermediateForm.arg.argument;

import java.util.Set;

import compiler.CHRIntermediateForm.Cost;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.exceptions.AmbiguityException;
import compiler.CHRIntermediateForm.members.Field;
import compiler.CHRIntermediateForm.members.Method;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.TypeFactory;

/**
 * @author Peter Van Weert
 */
public class ClassNameImplicitArgument extends LeafArgument implements IImplicitArgument {
    
    private Class<?> theClass;
    
    public ClassNameImplicitArgument(Class<?> theClass) {
        setTheClass(theClass);
    }

    public Set<Method> getMethods(String id) {        
        return Method.getStaticMethods(getTheClass(), id);
    }

    public Field getField(String name) throws AmbiguityException, NoSuchFieldException {
        return new Field(getTheClass(), name);
    }

    public IType getType() {
        return TypeFactory.getClassInstance(getTheClass());
    }
    
    public boolean isFixed() {
        return true;
    }
    public boolean isConstant() {
        return true;
    }

    public Class<?> getTheClass() {
        return theClass;
    }
    protected void setTheClass(Class< ? > theClass) {
        this.theClass = theClass;
    }
    
    public String getName() {
        return getTheClass().getName();
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ClassNameImplicitArgument)
            && ((ClassNameImplicitArgument)obj).getClass().equals(getClass());
    }
    
    public Cost getExpectedCost() {
    	return Cost.FREE;
    }
}