package compiler.CHRIntermediateForm.types;

import static compiler.CHRIntermediateForm.matching.MatchingInfo.DIRECT_MATCH;
import static compiler.CHRIntermediateForm.matching.MatchingInfo.EXACT_MATCH;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import util.collections.Empty;
import annotations.JCHR_Constraint;
import annotations.JCHR_Constraints;

import compiler.CHRIntermediateForm.exceptions.AmbiguityException;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.id.Identified;
import compiler.CHRIntermediateForm.id.Identifier;
import compiler.CHRIntermediateForm.init.IDeclarator;
import compiler.CHRIntermediateForm.init.IInitialisator;
import compiler.CHRIntermediateForm.matching.CoerceMethod;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.members.Field;
import compiler.CHRIntermediateForm.members.Method;

/**
 * @author Peter Van Weert
 */
public class TypeParameter extends Type implements Identified {

    private String identifier;
    
    private List<IType> upperBounds;
    
    private List<CoerceMethod> coerceMethods;
    
    public TypeParameter(String identifier) throws IllegalIdentifierException {
        Identifier.testSimpleIdentifier(identifier);
        setIdentifier(identifier);
        setUpperBounds(new ArrayList<IType>());
    }    
    
    public List<IType> getUpperBounds() {
        return upperBounds;
    }
    public IType getUpperBound(int index) {
        return getUpperBounds().get(index);
    }
    protected void setUpperBounds(List<IType> upperBounds) {
        this.upperBounds = upperBounds;
    }
    public void addUpperBound(IType upperBound) {
        if (hasUpperBounds() && !upperBound.isInterface())
            throw new IllegalArgumentException("The additional bound " + upperBound + " is not an interface type");
        if (getNbUpperBounds() == 1 && getUpperBound(0) instanceof TypeParameter)
            throw new IllegalArgumentException("A type parameter may not be followed by other bounds");
            
        getUpperBounds().add(upperBound);
    }
    public int getNbUpperBounds() {
        return getUpperBounds().size();
    }
    public boolean hasUpperBounds() {
        return getNbUpperBounds() > 0;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    protected void changeIdentifier(String identifier) throws IllegalIdentifierException {
        if (! canHaveAsIdentifier(identifier))
            throw new IllegalIdentifierException(identifier);
        setIdentifier(identifier);
    }
    protected void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    /**
     * A type parameter can have any simple name as identifier.
     * 
     * @return <code>true</code> if the given identifier is a valid,
     *  <em>simple</em> identifier; <code>false</code> otherwise.
     *  
     * @see Identifier#isValidSimpleIdentifier(String)
     */
    public boolean canHaveAsIdentifier(String identifier) {
        return Identifier.isValidSimpleIdentifier(identifier);
    }
    
    public boolean isInterface() {
        return hasUpperBounds() && getUpperBounds().get(0).isInterface();
    }
    
    public MatchingInfo isAssignableTo(IType other) {
        if (other == this) return EXACT_MATCH;
        if (! hasUpperBounds())
            return IType.OBJECT.isAssignableTo(other);

        MatchingInfo best = new MatchingInfo(), temp;
        
        for (IType bound : getUpperBounds()) {
            if (best.isDirectMatch()) return DIRECT_MATCH;
            temp = bound.isAssignableTo(other);
            
            switch (temp.compareWith(best)) {
                case BETTER:
                    best = temp;
                break;
                
                case EQUAL:
                case AMBIGUOUS:
                    best.setAmbiguous();
                break;                
            }
        }
                        
        return best;
    }
    
    public boolean isDirectlyAssignableTo(IType other) {
        if (this.equals(other)) return true;
        
        if (! hasUpperBounds())
            return IType.OBJECT.isDirectlyAssignableTo(other);
        
        for (IType bound : getUpperBounds())
            if (bound.isDirectlyAssignableTo(other))
                return true;
            
        return false;
    }
    
    public boolean isHashObservable() {
        for (IType bound : getUpperBounds())
            if (bound.isHashObservable())
                return true;
        return false;
    }
    public boolean isBuiltInConstraintObservable() {
        for (IType bound : getUpperBounds())
            if (bound.isBuiltInConstraintObservable())
                return true;
        return false;
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(getIdentifier());
        if (hasUpperBounds()) {
            result.append(" extends ");
            
            Iterator<IType> bounds = getUpperBounds().iterator();
	        do {
	            result.append(bounds.next().toTypeString());
                if (! bounds.hasNext()) break;
                result.append(" & ");
	        } while (true);  
        }
        return result.toString();
    }
    
    @Override
    public int hashCode() {
        // aanname dat uniekheid van naam extern wordt gegarandeerd
        return 37 * 23 + getIdentifier().hashCode();
    }
    
    public List<CoerceMethod> getCoerceMethods() {
        // eens het als variabletype wordt gebruikt is het niet
        // onredelijk te verwachten dat deze info nog wel eens
        // zal worden opgevraagd ==> cachen?
        if (coerceMethods == null) initCoerceMethods();
        return coerceMethods;
    }
    
    public IInitialisator<?> getInitialisatorFrom(IType type) throws AmbiguityException {
        IInitialisator<?> result = null, temp;
        boolean ambiguous = false;
        
        for (IType bound : getUpperBounds()) {
            temp = bound.getInitialisatorFrom(type);
            if (result == null)
                result = temp;
            else switch (temp.compareWith(result)) {
                case BETTER:
                    ambiguous = false;
                    result = temp;
                break;
                
                case EQUAL:
                case AMBIGUOUS:
                    ambiguous = true;
                break;
            }
        }
               
        if (ambiguous) throw new AmbiguityException();
        
        return result;
    }
    
    public IDeclarator<?> getDeclarator() throws AmbiguityException {
        IDeclarator<?> result = null, temp;
        
        for (IType bound : getUpperBounds()) {
            temp = bound.getDeclarator();
            if (temp != null) {
                if (result != null)
                    throw new AmbiguityException();
                else
                    result = temp;
            }
        }
        
        return result;
    }
    
    public IDeclarator<?> getInitialisationDeclaratorFrom(IType type) throws AmbiguityException {
        IDeclarator<?> result = null, temp;
        
        for (IType bound : getUpperBounds()) {
            temp = bound.getInitialisationDeclaratorFrom(type);
            if (temp != null) {
                if (result != null)
                    throw new AmbiguityException();
                else
                    result = temp;
            }
        }
        
        return result;
    }
    
    public void initCoerceMethods() {
        final List<CoerceMethod> list;
        
        if (!hasUpperBounds())
            list = Empty.getInstance();
        else {
            list = new ArrayList<CoerceMethod>();
        
            for (IType bound : getUpperBounds())
                list.addAll(bound.getCoerceMethods());
        }
        
        setCoerceMethods(list);
    }
    
    protected void setCoerceMethods(List<CoerceMethod> coerceMethods) {
        this.coerceMethods = coerceMethods;
    }
    
    public boolean isFixed() {
        return hasUpperBounds() && getUpperBound(0).isFixed();
    }
    
    public Field getField(String name) 
    throws NoSuchFieldException, AmbiguityException {
        
        Field result = null, temp;
        for (IType bound : getUpperBounds()) 
	        try {
	            temp = bound.getField(name);
	            if (result != null && ! result.equals(temp))
	                throw new AmbiguityException(this + "." + name);
	            else
	                result = temp;
	            
	        } catch (NoSuchFieldException snfe) {
	            // NOP
	        }

        if (result == null)
            throw new NoSuchFieldException(name);
        else
            return result;
    }
    
    public List<JCHR_Constraints> getJCHR_ConstraintsAnnotations() {
        List<JCHR_Constraints> result = new ArrayList<JCHR_Constraints>();
        for (IType bound : getUpperBounds())
            result.addAll(bound.getJCHR_ConstraintsAnnotations());
        return result;
    }
    
    public List<JCHR_Constraint> getJCHR_ConstraintAnnotations() {
        List<JCHR_Constraint> result = new ArrayList<JCHR_Constraint>();
        for (IType bound : getUpperBounds())
            result.addAll(bound.getJCHR_ConstraintAnnotations());
        return result;
    }
    
    
    public Set<Method> getMethods(String id) {
        Set<Method> result = new HashSet<Method>();
        for (IType bound : getUpperBounds())
            result.addAll(bound.getMethods(id));
        return result;
    }
    
    @Override
    public String toTypeString() {
        return getIdentifier();
    }
    
    public String toFullTypeString() {
    	StringBuilder result = new StringBuilder();
    	result.append(toTypeString());
    	
    	if (hasUpperBounds()) {
    		result.append(" extends ").append(getUpperBound(0).toTypeString());
    		
    		for (int i = 1; i < getNbUpperBounds(); i++)
    			result.append('&').append(' ').append(getUpperBound(i).toTypeString());
    	}
    	
    	return result.toString();
    }
    
    public Class<?> getErasure() {
        if (hasUpperBounds())
            return getUpperBound(0).getErasure();
        else
            return Object.class;
    }
    public String getClassString() {
    	return getErasure().getCanonicalName().concat(".class");
    }    
}