package compiler.CHRIntermediateForm.builder.tables;

import static annotations.Default.isDefault;
import static annotations.Default.isSet;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQ;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQi;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQi2;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQi3;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.GEQ;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.GEQi;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.GT;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.GTi;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.LEQ;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.LEQi;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.LEQi2;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.LT;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.LTi;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.NEQ;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.NEQi;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.NEQi2;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.REF_EQ;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.REF_NEQ;
import static compiler.CHRIntermediateForm.solver.ISolver.NO_SOLVER;
import static compiler.CHRIntermediateForm.solver.ISolver.NO_SOLVER_ID;
import static compiler.CHRIntermediateForm.types.PrimitiveType.BOOLEAN;
import static compiler.CHRIntermediateForm.types.PrimitiveType.VOID;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import annotations.JCHR_Asks;
import annotations.JCHR_Constraint;
import annotations.JCHR_Constraints;
import annotations.JCHR_Tells;

import compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint;
import compiler.CHRIntermediateForm.constraints.bi.SolverBuiltInConstraint;
import compiler.CHRIntermediateForm.constraints.java.Assignment;
import compiler.CHRIntermediateForm.constraints.java.Comparison;
import compiler.CHRIntermediateForm.constraints.java.EnumEquality;
import compiler.CHRIntermediateForm.constraints.java.Equals;
import compiler.CHRIntermediateForm.constraints.java.INoSolverConstraint;
import compiler.CHRIntermediateForm.constraints.java.NoSolverConstraint;
import compiler.CHRIntermediateForm.constraints.java.ReferenceEquality;
import compiler.CHRIntermediateForm.exceptions.DuplicateIdentifierException;
import compiler.CHRIntermediateForm.exceptions.IdentifierException;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.exceptions.ToDoException;
import compiler.CHRIntermediateForm.exceptions.UnknownIdentifierException;
import compiler.CHRIntermediateForm.id.Identifier;
import compiler.CHRIntermediateForm.matching.CoerceMethod;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.solver.ISolver;
import compiler.CHRIntermediateForm.solver.Solver;
import compiler.CHRIntermediateForm.types.GenericType;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.PrimitiveType;
import compiler.CHRIntermediateForm.variables.VariableType;

/*
 * version 1.0.3
 *  - Assignments have become a special case, no longer the
 *      responsibility of the BuiltInConstraintTable. 
 */
/**
 * @author Peter Van Weert
 */
public class BuiltInConstraintTable extends ConstraintTable<Set<IBuiltInConstraint<?>>> {
    public BuiltInConstraintTable() {
        super();
        setIndex(new HashMap<String, BuiltInConstraintTable>());
        reset();
    }
    protected BuiltInConstraintTable(Object arg) {
        super();
    }
    
    private Map<String, BuiltInConstraintTable> index;
    
    /**
     * Registers the built-in constraints solved by the given solver.
     * 
     * @param solver
     *  The solver whose built-in constraints are to be registered.
     * @param allowNoTrigger
     * 	Determines whether or not no-trigger constraints are allowed.
     * @throws DuplicateIdentifierException
     *  If the solver solves two or more constraints with the same given identifier.
     * @throws UnknownIdentifierException
     *  If one a <code>JCHR_Asks</code> or <code>JCHR_Tells</code> annotation is
     *  present that uses an identifier that was not declared by a <code>JCHR_Constraint</code>
     *  annotation.
     *  
     * @see #registerComparings(Solver, BuiltInConstraintTable)
     * @see #registerJCHR_Constraints(Solver, BuiltInConstraintTable)
     */
    public void registerSolver(Solver solver) 
    throws IdentifierException {
        
        // Create an extra table for registering the built-in constraints
        // of the given solver only.
        final BuiltInConstraintTable other = indexSolver(solver);
        
        // Register the comparing constraints
        registerComparings(solver, other);

        // Register the other, declared constraints
        registerJCHR_Constraints(solver, other);
        
        // A solver should be solving at least one built-in constraint
        if (other.getSize() == 0) 
            throw new IllegalArgumentException(solver + " is not a valid solver");
    }
    
    /**
     * Registers the built-in constraint involving the given variable type
     * that can be solved without explicit intervention of a solver.
     * 
     * @param type
     *  The variable type.
     */
    public void registerVariableType(VariableType type) {
        // Constraints involving primitive types are known in advance
        if (PrimitiveType.isPrimitive(type.getType())) return; 
        
        // Register the comparing constraints
        registerComparings(type.getType());
        
        // Register the other, declared constraints
        registerJCHR_Constraints(type.getType());
    }
    
    /**
     * Registers the comparing-constraints if the given solver is a subclass of
     * @link{ java.util.Comparator }
     * 
     * @param solver
     *  The solver whose comparing constraints have to be registered if present.
     * @param other
     *  The table to which the comparing constraints have to be registered
     *  (besides this one that is).
     * 
     * @see Solver#isComparatorSolver()
     * @see java.util.Comparator
     */
    protected void registerComparings(Solver solver, BuiltInConstraintTable other) {
        if (solver.isComparatorSolver()) {
            try {
                for (Comparison comparing : Comparison.getInstances(solver))
                    registerBiConstraintAlfa(comparing.getIdentifier(), comparing.getInfixIdentifier(), 2, true, comparing, other);
                
            } catch (IdentifierException e) {
                throw new RuntimeException();
            }
        }
    }
    
    /**
     * Registers the comparing-constraints if the given solver is a subclass of
     * @link{ java.lang.Comparable }
     * 
     * @param solver
     *  The solver whose comparing constraints have to be registered if present.
     * @param other
     *  The table to which the comparing constraints have to be registered
     *  (besides this one that is).
     * 
     * @see java.lang.Comparable
     */
    protected void registerComparings(IType type) {
        try {
            final MatchingInfo info 
                = type.isAssignableTo(GenericType.getNonParameterizableInstance(Comparable.class));
            if (info.isDirectMatch())
                for (Comparison comparing : Comparison.getInstances(type))
                    registerNoSolverBiConstraint(comparing.getIdentifier(), comparing.getInfixIdentifier(), true, comparing);
            else if (info.isNonInitMatch())
                for (CoerceMethod method : info.getCoerceMethods())
                    registerComparings(method.getReturnType());
        } catch (IllegalIdentifierException e) {
            throw new InternalError();
        }
    }
    
    /**
     * Registers the built-in constraints declared using <code>JCHR_Constraints</code>
     * (if present).
     * 
     * @param solver
     *  The solver whose declared built-in constraints have to be registered if present.
     * @param other
     *  The table to which the comparing constraints have to be registered
     *  (besides this one that is).
     * 
     * @throws DuplicateIdentifierException
     *  If the solver declares two constraints with the same identifier, or an identifier
     *  of a constraint that it already solves (e.g. by implementing <code>Comparator</code>).
     * @throws UnknownIdentifierException
     *  If one of the <code>JCHR_Asks</code> or <code>JCHR_Tells</code> annotations
     *  uses an identifier.
     * 
     * @see JCHR_Constraints
     * @see JCHR_Asks
     * @see JCHR_Tells
     */
    protected void registerJCHR_Constraints(Solver solver, BuiltInConstraintTable other) 
    throws IdentifierException {
    	
        final Map<String, JCHR_Constraint> map = new HashMap<String, JCHR_Constraint>();
        
        for (JCHR_Constraints constrs : solver.getJCHR_ConstraintsAnnotations())
        	mapConstraints(constrs.value(), map);
        mapConstraints(solver.getJCHR_ConstraintAnnotations(), map);
        	
        for (Method method : solver.getMethods()) {
        	String name = null, infixes[] = null;
        	JCHR_Constraint constraint = null;
        	IBuiltInConstraint<?> biConstraint = null;
        	boolean asks = false;
        	
            if (method.isAnnotationPresent(JCHR_Asks.class)) {
                JCHR_Asks anno = method.getAnnotation(JCHR_Asks.class);
                name = getIdentifier(anno);
                infixes = getInfixIdentifiers(anno);
                constraint = map.get(name);
                if (constraint == null)
                    throw new UnknownIdentifierException(name);
                biConstraint = new SolverBuiltInConstraint(solver, constraint, method, anno);
                asks = true;
            }
            if (method.isAnnotationPresent(JCHR_Tells.class)) {
            	if (asks) throw new IllegalStateException(); 
            	
            	JCHR_Tells anno = method.getAnnotation(JCHR_Tells.class);
                name = getIdentifier(anno);
                infixes = getInfixIdentifiers(anno);
                constraint = map.get(name);
                if (constraint == null)
                    throw new UnknownIdentifierException(name);
            	biConstraint = new SolverBuiltInConstraint(solver, constraint, method, anno);
            } else {
            	if (!asks) continue;
            }
            
            if (infixes == null)
            	infixes = asks? constraint.ask_infix() : constraint.tell_infix();
            if (isDefault(infixes))
            	infixes = constraint.infix();
            
            if (isDefault(infixes) || infixes == null || infixes.length == 0)
                registerBiConstraintAlfa(name, "", constraint.arity(), asks, biConstraint, other);
            else for (String infix : infixes)
                registerBiConstraintAlfa(name, infix, constraint.arity(), asks, biConstraint, other);
        }
    }
    
    private static void mapConstraints(JCHR_Constraint[] constraints, Map<String, JCHR_Constraint> map)
    throws DuplicateIdentifierException, IdentifierException {
    	mapConstraints(Arrays.asList(constraints), map);
    }
    private static void mapConstraints(Iterable<JCHR_Constraint> constraints, Map<String, JCHR_Constraint> map) 
    throws DuplicateIdentifierException, IdentifierException {
    	for (JCHR_Constraint constr : constraints) {
    		if (map.put(constr.identifier(), constr) != null)
    			throw new DuplicateIdentifierException(constr.identifier());
    		check(constr);
		}
    }
    
    protected static void check(JCHR_Constraint constraint) throws IdentifierException{
        boolean a = isSet(constraint.infix()),
            b = isSet(constraint.ask_infix()),
            c = isSet(constraint.tell_infix());
        
        if (a) {
            if (b || c) throw new IdentifierException(
                "use either 'infix', OR both 'ask_infix' and 'ask_tell'"
            );
        } else {
            if (b ^ c) throw new IdentifierException(
                "use either 'infix', or BOTH 'ask_infix' and 'ask_tell'"
            );
        }
    }
    
    protected static String getIdentifier(JCHR_Asks asks) throws IdentifierException {
        return getIdentifier(asks.value(), asks.constraint());
    }
    protected static String getIdentifier(JCHR_Tells tells) throws IdentifierException {
        return getIdentifier(tells.value(), tells.constraint());
    }
    protected static String getIdentifier(String value, String constraint) throws IdentifierException {
        boolean a = isSet(value), b = isSet(constraint);
        
        if (a && b)
            throw new IdentifierException(
                "use either 'value' or 'constraint' " +
                "(now value=%s AND constraint=%s are specified)", 
                value, constraint
            );
        
        if (a) return value;
        
        if (b) return constraint;
        
        throw new IdentifierException(
            "constraint identifier not specified in ask or tell annotation"
        );
    }
    
    protected static String[] getInfixIdentifiers(JCHR_Asks asks) throws IdentifierException {
        return getInfixIdentifiers(asks.infix(), isDefault(asks.value()));
    }
    protected static String[] getInfixIdentifiers(JCHR_Tells tells) throws IdentifierException {
        return getInfixIdentifiers(tells.infix(), isDefault(tells.value()));
    }
    protected static String[] getInfixIdentifiers(String[] infixes, boolean value) throws IdentifierException {
        if (isDefault(infixes))
            return null;
        else if (value)
            return infixes;
        else    
            throw new IdentifierException("Use 'constraint', not 'value', together with 'infix'");
    }
    
    protected void registerJCHR_Constraints(IType type) {
        if (! type.getJCHR_ConstraintsAnnotations().isEmpty())                
            throw new ToDoException("Declared \"no solver\" constraints...");
    }
    
     
    
    protected BuiltInConstraintTable indexSolver(ISolver solver) {
        final BuiltInConstraintTable result = new BuiltInConstraintTable(null);
        if (getIndex().put(solver.getIdentifier(), result) != null)
            throw new IllegalArgumentException(solver.getIdentifier() + " is already declared");
        return result;
    }
    
    public void registerAssignment(IType type) {
        try {
            registerNoSolverBiConstraint(null, "=", false, new Assignment(type));
        } catch (IllegalIdentifierException e) {
            throw new InternalError();
        }
    }
    
    /**
     * Registers the built-in constraint <code>biConstraint</code> with given
     * needed parameters (<code>identifier</code>, <code>infixIdentifier</code>, 
     * <code>arity</code> and <code>asks</code>) in <code>this</code>
     * (as in the implicit argument) built-in constraint table. 
     * 
     * @pre identifier != null || infixIdentifier != null
     * 
     * @param identifier
     *  The (prefix) identifier to be used for the built-in constraint. 
     *  This may be a null-reference indicating that the constraint cannot
     *  be written prefix.
     * @param infixIdentifier
     *  The infix identifier to be used for the built-in constraint. 
     *  This may be a null-reference indicating that the constraint cannot
     *  be written infix.
     * @param arity
     *  The arity of the constraint.
     * @param asks
     *  Indicates whether the registered constraint is a ask constraint
     *  or not (i.e. if false, this is a tell constraint).
     * @param biConstraint
     *  The built-in constraint to be registered.
     * @throws IllegalIdentifierException
     *  <code>identifier == null && infixIdentifier == null</code>
     */
    public void registerBiConstraint(String identifier, String infixIdentifier, int arity, boolean asks, IBuiltInConstraint<?> biConstraint) 
    throws IdentifierException {
        registerBiConstraint(identifier, infixIdentifier, arity, asks, biConstraint, this);
    }
    
    /**
     * Registers the built-in constraint <code>biConstraint</code> with given
     * needed parameters (<code>identifier</code>, <code>infixIdentifier</code>, 
     * <code>arity</code> and <code>asks</code>) in both <code>this</code>
     * (as in the implicit argument) built-in constraint table and in the
     * <code>other</code> one (the final argument). 
     * 
     * @pre identifier != null || infixIdentifier != null
     * 
     * @param identifier
     *  The (prefix) identifier to be used for the built-in constraint. 
     *  This may be a null-reference indicating that the constraint cannot
     *  be written prefix.
     * @param infixIdentifier
     *  The infix identifier to be used for the built-in constraint. 
     *  This may be a null-reference indicating that the constraint cannot
     *  be written infix.
     * @param arity
     *  The arity of the constraint.
     * @param asks
     *  Indicates whether the registered constraint is a ask constraint
     *  or not (i.e. if false, this is a tell constraint).
     * @param biConstraint
     *  The built-in constraint to be registered.
     * @param other
     *  The built-in constraint table <code>biConstraint</code> has
     *  to be registered to, asides from <code>this</code> one 
     *  (the implicit argument). 
     * @throws IllegalIdentifierException
     *  <code>identifier == null && infixIdentifier == null</code>
     */
    protected void registerBiConstraintAlfa(String identifier, String infixIdentifier, int arity, boolean asks, IBuiltInConstraint<?> biConstraint, BuiltInConstraintTable other) 
    throws IllegalIdentifierException {
        registerBiConstraint(identifier, infixIdentifier, arity, asks, biConstraint, this);
        registerBiConstraint(identifier, infixIdentifier, arity, asks, biConstraint, other);
    }
    
    /**
     * Registers the built-in constraint <code>biConstraint</code> with given
     * needed parameters (<code>identifier</code>, <code>infixIdentifier</code>, 
     * <code>arity</code> and <code>asks</code>) in the given
     * built-in constraint table <code>table</code>. 
     * 
     * @pre One of the identifiers is non-null.<br/> 
     *  <code>identifier != null || infixIdentifier != null</code>
     * 
     * @param identifier
     *  The (prefix) identifier to be used for the built-in constraint. 
     *  This may be a null-reference indicating that the constraint cannot
     *  be written prefix.
     * @param infixIdentifier
     *  The infix identifier to be used for the built-in constraint. 
     *  This may be a null-reference indicating that the constraint cannot
     *  be written infix.
     * @param arity
     *  The arity of the constraint.
     * @param asks
     *  Indicates whether the registered constraint is a ask constraint
     *  or not (i.e. if false, this is a tell constraint).
     * @param biConstraint
     *  The built-in constraint to be registered.
     * @param table
     *  The built-in constraint table <code>biConstraint</code> has
     *  to be registered to. 
     * @throws IllegalIdentifierException
     *  <code>identifier == null && infixIdentifier == null</code>
     */
    protected void registerBiConstraint(String identifier, String infixIdentifier, int arity, boolean asks, IBuiltInConstraint<?> biConstraint, BuiltInConstraintTable table) 
    throws IllegalIdentifierException {
        final boolean 
            identifierOK = Identifier.isValidUdSimpleIdentifier(identifier),
            infixOK = (infixIdentifier != null && infixIdentifier.length() > 0 && infixIdentifier.indexOf('?') < 0);

        if (infixOK && arity != 2)
            throw new IllegalArgumentException(biConstraint.getIdentifier()
                + " is not a binary constraint => cannot have infix notation"
            );
        
        if (identifierOK)
            table.declare(identifier, asks, arity, biConstraint, false);
        if (infixOK)
            table.declare(infixIdentifier, asks, arity, biConstraint, true);
        
        if (!identifierOK && !infixOK)
            throw new IllegalIdentifierException("No valid identifier present for: " + biConstraint);
    }
    
    /*
     * version 1.0.3
     *  - Assignments have become a special case, no longer the
     *      responsibility of the BuiltInConstraintTable. 
     */
    /**
     * Registers the constraints related to the primitive types.
     */
    public void registerPrimitiveBiConstraints() {
        final String[][] askPairs = new String[][] {
            { GEQ , GEQi },
            { GT  , GTi  },
            { LT  , LTi  },
            { NEQ , NEQi }
        };
        final String[][] askTriplets = new String[][] {
            { EQ  , EQi2 , EQi}, 
            { LEQ , LEQi , LEQi2 }
        };
        
        for (PrimitiveType type : PrimitiveType.values()) {
            if (type == VOID) continue;
            if (type == BOOLEAN) continue;
            
            for (String[] triplet : askTriplets)
                registerNoSolverBiAskConstraint(type, triplet);
            for (String[] pair : askPairs)
                registerNoSolverBiAskConstraint(type, pair);
        }
        
        registerNoSolverBiAskConstraint(BOOLEAN, askTriplets[0]);
        registerNoSolverBiAskConstraint(BOOLEAN, askPairs[3]);
    }
    
    private INoSolverConstraint<?> registerNoSolverBiAskConstraint(IType type, String... ids) {
        try {
            final NoSolverConstraint constraint = new NoSolverConstraint(type, ids[1], true);
            registerNoSolverBiConstraint(ids[0], ids[1], true, constraint);
            
            if (ids.length == 3)
                registerNoSolverBiConstraint(null, ids[2], true, constraint);
            else if (ids.length != 2)
                throw new IllegalStateException();
            
            return constraint;
                
        } catch (IllegalIdentifierException e) {
            throw new InternalError();
        }
    }
    
    public void registerObjectBiConstraints() {
        try {
            registerNoSolverBiConstraint(EQ, EQi, true, Equals.getInstance());
            registerNoSolverBiConstraint(NEQ, NEQi, true, Equals.getNegatedInstance());
            registerNoSolverBiConstraint(null, EQi2, true, Equals.getInstance());
            registerNoSolverBiConstraint(REF_EQ, EQi3, true, ReferenceEquality.getForcedInstance());
            registerNoSolverBiConstraint(REF_NEQ, NEQi2, true, ReferenceEquality.getNegatedInstance());
            
        } catch (IdentifierException iie) {
            throw new InternalError();
        }
    }
    
    public void registerEnumBiConstraints() {
        // preferred over equals(): more specific argument types
        
        try {
            registerNoSolverBiConstraint(EQ, EQi, true, EnumEquality.getInstance());
            registerNoSolverBiConstraint(NEQ, NEQi, true, EnumEquality.getNegatedInstance());
            
        } catch (IllegalIdentifierException e) {
            throw new InternalError();
        }
        
        // comparisons are implicit: Enum implements Comparable
    }
    
    protected void registerNoSolverBiConstraint(String identifier, String infixIdentifier, boolean asks, INoSolverConstraint<?> constraint) 
    throws IllegalIdentifierException {
        registerBiConstraint(identifier, infixIdentifier, 2, asks, constraint, getNoSolverBiConstraintTable());
    }
    
    protected BuiltInConstraintTable getNoSolverBiConstraintTable() {
        return getIndex().get(NO_SOLVER_ID);
    }
    
    protected void declare(String name, boolean asks, int arity, IBuiltInConstraint<?> constraint, boolean infix) {
        declare(createBiConstraintId(name, asks, arity), constraint, infix);
    }
    
    protected void declare(String id, IBuiltInConstraint<?> constraint, boolean infix) {
        try {
            Set<IBuiltInConstraint<?>> list = get(id, infix);
            if (list == null) 
                list = declare(id, new HashSet<IBuiltInConstraint<?>>(), infix);
            list.add(constraint);
            
        } catch (DuplicateIdentifierException die) {
            throw new RuntimeException();
        }
    }
    
    public Set<IBuiltInConstraint<?>> get(String id, boolean asks, int arity, boolean infix) {
        return get(createBiConstraintId(id, asks, arity), infix);
    }
    
    public Set<IBuiltInConstraint<?>> getNoSolverConstraints(String id, boolean asks, boolean infix) {
        return get(NO_SOLVER, id, asks, 2, infix);
    }
    
    public Set<IBuiltInConstraint<?>> get(ISolver solver, String id, boolean asks, int arity, boolean infix) {
        return getIndex().get(solver.getIdentifier()).get(id, asks, arity, infix);
    }
    
    public final static String createBiConstraintId(String constraintId, boolean asks, int arity) {
        return (asks? "ask" : "tell") + constraintId + '/' + arity;
    }

    protected Map<String, BuiltInConstraintTable> getIndex() {
        return index;
    }
    protected void setIndex(Map<String, BuiltInConstraintTable> index) {
        this.index = index;
    }
    
    @Override
    public void reset() {
        super.reset();
        getIndex().clear();
        indexSolver(NO_SOLVER);
        registerPrimitiveBiConstraints();
        registerEnumBiConstraints();
        registerObjectBiConstraints();
    }
}