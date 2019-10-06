package compiler.CHRIntermediateForm.variables;

import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.types.IType;

/**
 * <p>
 * The nameless variable is in many ways a special case: it is not
 * typed and can thus be assigned to arbitrary typed argument posititions, 
 * but only as an argument to occurrences, it does not result in 
 * implicit guards when used more than once in the same rule, etc.s
 * </p>
 * <p>
 * It is implemented using the well-known singleton creational pattern.
 * </p>
 * 
 * @author Peter Van Weert
 */
public class NamelessVariable extends AbstractVariable implements IActualVariable {

    public final static String IDENTIFIER = "_";
    
    /* SINGLETON */
    private NamelessVariable() throws IllegalIdentifierException {
        super(IDENTIFIER);
    }
    
    private static NamelessVariable instance;
    public static NamelessVariable getInstance() {
        try {
            if (instance == null)
                instance = new NamelessVariable();
            return instance;
        } catch (IllegalIdentifierException iie) {
            throw new InternalError();
        }
    }

    /**
     * Throws an {@link UntypedArgumentException}: the nameless
     * variable is not typed.
     * 
     * @throws UntypedArgumentException
     *  the nameless variable is not typed.
     */
    public IType getType() throws UntypedArgumentException {
        throw new UntypedArgumentException();
    }

    /**
     * Throws an {@link UntypedArgumentException}: the nameless
     * variable is not typed.
     * 
     * @throws UntypedArgumentException
     *  the nameless variable is not typed.
     */
    public boolean isFixed() throws UntypedArgumentException {
        throw new UntypedArgumentException();
    }
    
    public boolean isConstant() {
        return true;
    }

    public MatchingInfo isAssignableTo(IType type) {
        return MatchingInfo.DIRECT_MATCH;
    }

    public boolean isDirectlyAssignableTo(IType type) {
        return true;
    }

    public void accept(IArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        /* SINGLETON */
        throw new UnsupportedOperationException();
    }
    
    public static boolean isNamelessIdentifier(String identifier) {
        return IDENTIFIER.equals(identifier);
    }
    
    public boolean isAnonymous() {
        return true;
    }
    public boolean isImplicit() {
        return false;
    }
    public boolean isReactive() {
        return false;
    }
    
}