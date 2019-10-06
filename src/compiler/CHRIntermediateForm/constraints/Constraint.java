package compiler.CHRIntermediateForm.constraints;

import util.Arrays;
import compiler.CHRIntermediateForm.arg.argumentable.Argumentable;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.id.AbstractIdentified;
import compiler.CHRIntermediateForm.id.Identifier;

/**
 * @author Peter Van Weert
 */
public abstract class Constraint<T extends IConstraint<?>> 
    extends AbstractIdentified
    implements IConstraint<T> {

    private String[] infixIdentifiers;

    protected Constraint() {
        // NOP
    }
    
    public Constraint(String id) throws IllegalIdentifierException {
        super(id);
    }
    
    public Constraint(String id, String infix) throws IllegalIdentifierException {
        super(id);
        setInfixIdentifiers(infix);
    }
    
    public String[] getInfixIdentifiers() {
        return infixIdentifiers;
    }
    
    /**
     * Checks whether infix identifiers for this constraint 
     * have been defined or not.
     *  
     * @return <code>true</code> iff one or more 
     *  infix identifiers are defined for this constraint; 
     *  <code>false</code> otherwise. 
     */
    public boolean hasInfixIdentifiers() {
        return (getInfixIdentifiers() != null);
    }
    
    public int getNbInfixIdentifiers() {
        return hasInfixIdentifiers()
            ? getInfixIdentifiers().length
            : 0;
    }
    
    /**
     * Checks whether the infix identifier of this constraint 
     * is equal to the given argument.
     * 
     * @param id
     *  An identifier.
     *   
     * @return True iff the infix identifier of this constraint 
     *  is equal to the given argument. False otherwise.
     */
    public boolean hasAsInfix(String id) {
        return hasInfixIdentifiers() && Arrays.contains(getInfixIdentifiers(), id);
    }

    /**
     * Sets the infix identifiers of this constraint
     * (<code>null</code> if undefined).
     * 
     * @pre canHaveAsInfix(infix)
     * 
     * @param infixes
     *  The infix identifiers for this constraint
     *  (<code>null</code> if undefined).
     */
    protected void setInfixIdentifiers(String... infixes) {
        this.infixIdentifiers = infixes;
    }
    
    /**
     * Checks whether this constraint can have the given
     * identifier as an infix identifier.
     * 
     * @param id
     *  The id that has to be checked.
     * 
     * @return True if and only if <code>id</code> is a valid infix
     *  identifier for this constraint. <code>null</code>
     *  is always valid (indicates that the infix is not specified).
     *  Only binary constraints can be given an infix identifier. 
     *  Other then that the only constraint on the identifier is that
     *  it mustn't contain a ` character.
     */
    public boolean canHaveAsInfix(String id) {
        return (id == null)
            || ((getArity() == 2) && Identifier.isValidInfixIdentifier(id));
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(getIdentifier());
        if (getArity() > 0) result.append(Argumentable.toString(this));
        return result.toString();
    }

    public boolean isIdempotent() {
    	return isAskConstraint();	// by default this property is only known for ask constraints
    }
    
    public boolean isAntisymmetric() {
        return false;  // by default this property is not known
    }
    
    public boolean isAsymmetric() {
        return false;  // by default this property is not known
    }

    public boolean isCoreflexive() {
        return false;  // by default this property is not known
    }

    public boolean isIrreflexive() {
        return false;  // by default this property is not known
    }

    public boolean isReflexive() {
        return false;  // by default this property is not known
    }

    public boolean isSymmetric() {
        return false;  // by default this property is not known
    }

    public boolean isTotal() {
        return false;  // by default this property is not known
    }

    public boolean isTransitive() {
        return false;  // by default this property is not known
    }

    public boolean isTrichotomous() {
        return false;  // by default this property is not known
    }
}