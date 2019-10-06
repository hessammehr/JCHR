package compiler.CHRIntermediateForm.types;

import java.util.List;
import java.util.Set;

import annotations.JCHR_Constraint;
import annotations.JCHR_Constraints;

import util.comparing.Comparable;

import compiler.CHRIntermediateForm.exceptions.AmbiguityException;
import compiler.CHRIntermediateForm.init.IDeclarator;
import compiler.CHRIntermediateForm.init.IInitialisator;
import compiler.CHRIntermediateForm.matching.CoerceMethod;
import compiler.CHRIntermediateForm.matching.IAssignable;
import compiler.CHRIntermediateForm.members.Field;
import compiler.CHRIntermediateForm.members.Method;

/**
 * @author Peter Van Weert
 */
public interface IType extends IAssignable, Comparable<IType> {
    public final static IType OBJECT = TypeFactory.getInstance(Object.class);
    
    public boolean isCompatibleWith(IType other);
    
    public boolean isInterface();
    
    public List<CoerceMethod> getCoerceMethods();
    
    /**
     * Returns a reflection of <code>JCHR_Constraints</code> annotation if present, 
     * <code>null</code> otherwise.
     * 
     * @return A reflection of <code>JCHR_Constraints</code> annotation if present, 
     *  <code>null</code> otherwise.
     */
    public List<JCHR_Constraints> getJCHR_ConstraintsAnnotations();
    
    /**
     * Returns a reflection of <code>JCHR_Constraint</code> annotation if present, 
     * <code>null</code> otherwise.
     * 
     * @return A reflection of <code>JCHR_Constraint</code> annotation if present, 
     *  <code>null</code> otherwise.
     */
    public List<JCHR_Constraint> getJCHR_ConstraintAnnotations();
    
    public IInitialisator<?> getInitialisatorFrom(IType type)
        throws AmbiguityException;
    
    public IDeclarator<?> getDeclarator()
        throws AmbiguityException;
    
    public IDeclarator<?> getInitialisationDeclaratorFrom(IType type)
        throws AmbiguityException;
    
    public Field getField(String name) 
    	throws NoSuchFieldException, AmbiguityException;
    
    public Set<Method> getMethods(String id);
    
    public String toTypeString();
    
    public boolean isFixed();
    
    public boolean isHashObservable();
    public boolean isBuiltInConstraintObservable();
    
    public Class<?> getErasure();
    public String getClassString();
}
