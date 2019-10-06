package compiler.CHRIntermediateForm.variables;

import static compiler.CHRIntermediateForm.variables.NamelessVariable.isNamelessIdentifier;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import util.Resettable;
import util.collections.Stack;

import compiler.CHRIntermediateForm.exceptions.DuplicateIdentifierException;
import compiler.CHRIntermediateForm.exceptions.IdentifierException;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.exceptions.UnknownIdentifierException;
import compiler.CHRIntermediateForm.variables.exceptions.IllegalVariableException;
import compiler.CHRIntermediateForm.variables.exceptions.MultiTypedVariableException;

/**
 * <p>
 * A factory class to deal with the creation of variables. 
 * We are using the flyweight creational pattern as much as possible.
 * </p>
 * <p>
 * In order not to complicate things, variables within the same scope,
 * with identical identifiers, always have to have the same variable
 * type.
 * </p>
 * <p> 
 * This might seem logical, and it is in a way, but it might also
 * make sense to allow identically named variables in the head to have
 * different types: the resulting implicit guard easily be satisfied.
 * Whereas in other languages like e.g. Prolog, having different types
 * implies disequality, in Java (JCHR) this is not the case.
 * </p>
 * <p>
 * Nonetheless, after carefull cost-benefit analysis, we decided to 
 * add the single-typedness restriction:
 * </p>
 * <dl>
 *  <dt>costs (high)</dt>
 *  <dd>
 *      <ul>
 *      <li>
 *          Ambiguity in (implicit and explicit) guards: 
 *          we would no longer know the type of each variable.           
 *          The latter is needed quite a lot in the current 
 *          implementation.
 *      </li>
 *      <li>
 *          Problems with infix occurrences 
 *          (very implementation specific this one). 
 *      </li>
 *      </ul>
 *  </dd>
 *  <dt>benefits (low)</dt>
 *  <dd>
 *      At the moment we have no knowledge of a handler where multi-typed
 *      variables would be used. We think it would indeed hardly ever be
 *      used. If, for some reason, you do seem to need multi-typed variables,
 *      you can always use different variable names and include an
 *      explicit equality guard (remember: using the same variable more than
 *      once is nothing more than syntactic sugar!)
 *  </dd>
 * </dl>
 * <p>
 * It might be an interesting thought to allow 
 * multi-typed variables e.g. as long as they are not used outside 
 * the head. (We tried this once before, but it gave some complications, 
 * so we decided to leave it out the current release) 
 * </p>
 * 
 * @author Peter Van Weert
 */
public class VariableFactory implements Resettable {

    private Stack<VariableScope> scopeStack;
    
    private boolean allowedLocalVariables;
    
    private boolean allowedNamelessVariable;
    
    private List<IdentifierRestriction> identifierRestrictions;
    
    public VariableFactory() {
        setIdentifierRestrictions(new ArrayList<IdentifierRestriction>(4));
        setScopeStack(new Stack<VariableScope>());
        reset();
    }
    
    protected void setScopeStack(Stack<VariableScope> scopeStack) {
        this.scopeStack = scopeStack;
    }
    protected Stack<VariableScope> getScopeStack() {
        return scopeStack;
    }
    
    protected void setIdentifierRestrictions(
        List<IdentifierRestriction> identifierRestrictions
    ) {
        this.identifierRestrictions = identifierRestrictions;
    }
    protected List<IdentifierRestriction> getIdentifierRestrictions() {
        return identifierRestrictions;
    }
    
    protected void testIdentifierRestrictions(String identifier) 
    throws IdentifierException {
        final boolean local = inLocalVariableScope();
        for (IdentifierRestriction restriction : getIdentifierRestrictions())
            restriction.testIdentifier(identifier, local);
    }
    
    public void addIdentifierRestriction(IdentifierRestriction restriction) {
        getIdentifierRestrictions().add(restriction);
    }
    
    protected boolean isAllowedLocalVariables() {
        return allowedLocalVariables;
    }
    public void allowLocalVariables(boolean localVariablesAllowed) {
        this.allowedLocalVariables = localVariablesAllowed;
    }
    
    protected boolean isAllowedNamelessVariable() {
        return allowedNamelessVariable;
    }
    public void allowNamelessVariable(boolean namelessVariableAllowed) {
        this.allowedNamelessVariable = namelessVariableAllowed;
    }
    
    protected VariableScope getLocalVariableScope() {
        return getScopeStack().get(0);
    }
    public boolean inLocalVariableScope() {
        return getScopeStackSize() == 1;
    }
    
    protected boolean disAllowDuplicateIdentifiers() {
        return declaringVariables
            || inLocalVariableScope();
    }
    
    // XXX this is just to allow variable declarations (temporary fix, cf manual)
    private boolean declaringVariables;
    public void newScope() {
        if (!declaringVariables) 
            getScopeStack().push(new VariableScope());
    }
    protected VariableScope getCurrentScope() {
        return getScopeStack().peek();
    }
    protected int getScopeStackSize() {
        return getScopeStack().size();
    }
    protected VariableScope getScope(int index) throws IndexOutOfBoundsException {
        // the stack expects a 1-based index, given is 0-based index
        return getScopeStack().peek(index+1);
    }
    public void endScope() throws IllegalStateException {
        try {
            getScopeStack().pop();
        } catch (EmptyStackException ese) {
            throw new IllegalStateException("No current variable scope set");
        }
    }
    
    public void reset() {
        getScopeStack().reset();
        newScope();     // this is the scope for local variables
    }
    
    public HashSet<Variable> getLocalVariables() {
        return new HashSet<Variable>(getLocalVariableScope().values());
    }
    
    public Variable getLocalVariable(String identifier) 
    throws UnknownIdentifierException {
        Variable result = getLocalVariable0(identifier);
        if (result == null)
            throw new UnknownIdentifierException(identifier);
        return result;
    }
    
    protected Variable getLocalVariable0(String identifier) {
        return getLocalVariableScope().get(identifier);
    }
    
    public Variable getRuleVariable(String identifier)
    throws UnknownIdentifierException {
        Variable result = getRuleVariable0(identifier);
        if (result == null)
            throw new UnknownIdentifierException(identifier);
        return result;
    }
    
    protected Variable getRuleVariable0(String identifier) {
        final int local = getScopeStackSize()-1;
        
        Variable result;         
        for (int i = 0; i < local; i++) {
            result = getScope(i).get(identifier);
            if (result != null) return result;
        }
        
        return null;
    }
    
    protected Variable getVariableFromScope0(String identifier) {
        Variable result = getRuleVariable0(identifier);
        if (result == null && isAllowedLocalVariables()) 
            result = getLocalVariable0(identifier);
        return result;
    }
    
    public boolean isLocalVariable(String identifier) {
        return getLocalVariableScope().containsKey(identifier);
    }
    public boolean isAllowedLocalVariable(String identifier) {
        return isAllowedLocalVariables() && isLocalVariable(identifier);
    }
    
    public Variable getNamedVariable(String identifier) 
    throws UnknownIdentifierException, IllegalVariableException {
        
        Variable result = getVariableFromScope0(identifier);
        if (result == null) {
            if (isNamelessIdentifier(identifier))
                throw new IllegalVariableException("nameless variable");
            
            throw new UnknownIdentifierException(identifier);
        }
        return result;
    }
    
    public boolean isAllowedVariable(String identifier) {
        return (isAllowedNamelessVariable() && isNamelessIdentifier(identifier)) 
            || isAllowedNamedVariable(identifier);
    }
    
    public boolean isVariable(String identifier) {
        return isNamelessIdentifier(identifier)
            || isNamedVariable(identifier);
    }
    
    public boolean isNamedVariable(String identifier) {
        return isRuleVariable(identifier)
            || isLocalVariable(identifier);
    }
    public boolean isAllowedNamedVariable(String identifier) {
        return isRuleVariable(identifier)
            || isAllowedLocalVariable(identifier);
    }
    
    public boolean isNonLocalVariable(String identifier) {
        return isNamelessIdentifier(identifier) 
            || isRuleVariable(identifier);
    }
    
    public boolean isRuleVariable(String identifier) {
        final int local = getScopeStackSize()-1;
        
        for (int i = 0; i < local; i++)
            if (getScope(i).containsKey(identifier))
                return true;
        
        return false;
    }
    
    public IActualVariable getAllowedVariable(String identifier) 
        throws IllegalStateException, IllegalVariableException, UnknownIdentifierException {
        
        IActualVariable result;
        if ((result = getVariable0(identifier)) != null) return result;
        if ((result = getRuleVariable0(identifier)) != null) return result;
        if (isAllowedLocalVariables()) result = getLocalVariable0(identifier);
        if (result == null) throw new UnknownIdentifierException(identifier);
        return result;
    }
    
    public IActualVariable getVariable(String identifier, FormalVariable formal) 
        throws IllegalStateException, IllegalIdentifierException, 
            IdentifierException, MultiTypedVariableException, IllegalVariableException {
        
        try {
            if (inLocalVariableScope()) 
                throw new IllegalStateException();
            
            if (isNamelessIdentifier(identifier)) {
                if (isAllowedNamelessVariable())
                    return NamelessVariable.getInstance();
                throw new IllegalVariableException("nameless variable");
            }
            
            return putVariable(identifier, formal.getVariableType());
            
        } catch (DuplicateIdentifierException e) {
            // CANNOT happen!
            throw new InternalError();
        }
    }
    
    protected IActualVariable getVariable0(String identifier) 
    throws IllegalStateException, IllegalVariableException {
        
        if (inLocalVariableScope()) 
            throw new IllegalStateException();
        
        if (isNamelessIdentifier(identifier)) {
            if (isAllowedNamelessVariable())
                return NamelessVariable.getInstance();
            throw new IllegalVariableException("nameless variable");
        }
        
        return null;
    }
    
    public Variable registerLocalVariable(String identifier, VariableType type) 
    throws DuplicateIdentifierException, IllegalIdentifierException, IdentifierException {
        try {
            if (! inLocalVariableScope()) 
                throw new IllegalStateException(String.valueOf(getScopeStackSize()));
            else
                return putVariable(identifier, type);
            
        } catch (MultiTypedVariableException mtve) {
            // CANNOT happen!!
            throw new InternalError();
        }
    }
    
    // XXX these methods are just to allow variable delcarations (temporary fix)
    public void tempDeclareVariable(String identifier, VariableType type)
    throws DuplicateIdentifierException, IllegalIdentifierException, IdentifierException {
        newScope();
        declaringVariables = true;
        
        try {
            putVariable(identifier, type);
        } catch (MultiTypedVariableException e) {
            throw new InternalError();
        }
    }
    public void endVariableDeclarations() {
        declaringVariables = false;
    }
    
    public Variable declareVariable(String identifier, VariableType type)
    throws IllegalIdentifierException, IdentifierException {
    	try {
    		declaringVariables = true;
    		Variable result = putVariable(identifier, type);
    		declaringVariables = false;
    		return result;

    	} catch (MultiTypedVariableException mtve) {
    		// CANNOT HAPPEN
    		throw new InternalError();
    	}
    }
    
    protected Variable putVariable(String identifier, VariableType type) 
    throws DuplicateIdentifierException,
        IllegalIdentifierException,
        IdentifierException,
        MultiTypedVariableException {
        
        Variable variable = getVariableFromScope0(identifier);
        
        if (variable != null) {
            if (disAllowDuplicateIdentifiers())
                throw new DuplicateIdentifierException(identifier);
            else if (variable.hasAsVariableType(type))
                return variable;    // FLYWEIGHT PATTERN
            else
                throw new MultiTypedVariableException(identifier);
        }
            
        /* else (variable == null) */
        variable = createNewVariable(identifier, type);
        getCurrentScope().put(identifier, variable);
        return variable;
    }
    
    protected Variable createNewVariable(String identifier, VariableType type) 
    throws IllegalIdentifierException, IdentifierException {
        testIdentifierRestrictions(identifier);
        return new Variable(identifier, type);
    }
    
    private static int counter;
    public static String createImplicitVariableIdentifier() {
        return "$"+counter++;
    }
    public static Variable createImplicitVariable(IVariable var) {
        try {
            return createImplicitVariable((TypedVariable)var);
        } catch (ClassCastException cce) {
            throw new IllegalArgumentException(var + " is untyped");
        }
    }
    public static Variable createImplicitVariable(TypedVariable var) {
        return createImplicitVariable(var.getVariableType());
    }
    public static Variable createImplicitVariable(VariableType type) {
        try {
            return new Variable(createImplicitVariableIdentifier(), type);
        } catch (IllegalIdentifierException e) {
            throw new InternalError();
        }
    }
    
    static class VariableScope extends HashMap<String, Variable> {
        private static final long serialVersionUID = 1L;
    }
    
    public interface IdentifierRestriction {
        public void testIdentifier(String identifier, boolean local) 
            throws IdentifierException;
    }
}