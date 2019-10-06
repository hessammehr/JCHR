package compiler.CHRIntermediateForm.builder;

import static compiler.CHRIntermediateForm.builder.CHRIntermediateFormBuilder.ConjunctBuildingBlock.Type.BI_INFIX;
import static compiler.CHRIntermediateForm.builder.CHRIntermediateFormBuilder.ConjunctBuildingBlock.Type.BUILT_IN;
import static compiler.CHRIntermediateForm.builder.CHRIntermediateFormBuilder.ConjunctBuildingBlock.Type.COMPOSED_ID;
import static compiler.CHRIntermediateForm.builder.CHRIntermediateFormBuilder.ConjunctBuildingBlock.Type.CONSTRUCTOR_INVOCATION_ARG;
import static compiler.CHRIntermediateForm.builder.CHRIntermediateFormBuilder.ConjunctBuildingBlock.Type.CONSTRUCTOR_INVOCATION_CON;
import static compiler.CHRIntermediateForm.builder.CHRIntermediateFormBuilder.ConjunctBuildingBlock.Type.DECLARATION;
import static compiler.CHRIntermediateForm.builder.CHRIntermediateFormBuilder.ConjunctBuildingBlock.Type.INFIX;
import static compiler.CHRIntermediateForm.builder.CHRIntermediateFormBuilder.ConjunctBuildingBlock.Type.MARKED_BUILTIN;
import static compiler.CHRIntermediateForm.builder.CHRIntermediateFormBuilder.ConjunctBuildingBlock.Type.MARKED_INFIX;
import static compiler.CHRIntermediateForm.builder.CHRIntermediateFormBuilder.ConjunctBuildingBlock.Type.METHOD_INVOCATION_ARG;
import static compiler.CHRIntermediateForm.builder.CHRIntermediateFormBuilder.ConjunctBuildingBlock.Type.METHOD_INVOCATION_CON;
import static compiler.CHRIntermediateForm.builder.CHRIntermediateFormBuilder.ConjunctBuildingBlock.Type.SIMPLE_ID;
import static compiler.CHRIntermediateForm.builder.CHRIntermediateFormBuilder.ConjunctBuildingBlock.Type.UD_INFIX;
import static compiler.CHRIntermediateForm.builder.CHRIntermediateFormBuilder.ConjunctBuildingBlock.Type.USER_DEFINED;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQ;
import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.KEPT;
import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.NEGATIVE;
import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.REMOVED;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import util.Terminatable;
import util.builder.AbstractBuilder;
import util.builder.BuilderException;
import util.builder.Current;
import util.collections.Singleton;
import util.collections.Stack;
import util.exceptions.IllegalStateException;

import compiler.CHRIntermediateForm.CHRIntermediateForm;
import compiler.CHRIntermediateForm.Handler;
import compiler.CHRIntermediateForm.arg.argument.ClassNameImplicitArgument;
import compiler.CHRIntermediateForm.arg.argument.FormalArgument;
import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argument.IImplicitArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.BooleanArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.ByteArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.CharArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.DoubleArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.FloatArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.IntArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.LiteralArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.LongArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.NullArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.ShortArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.StringArgument;
import compiler.CHRIntermediateForm.arg.argumentable.AbstractMethod;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.arguments.ArgumentsDecorator;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.builder.tables.BuiltInConstraintTable;
import compiler.CHRIntermediateForm.builder.tables.ClassTable;
import compiler.CHRIntermediateForm.builder.tables.FieldTable;
import compiler.CHRIntermediateForm.builder.tables.MethodTable;
import compiler.CHRIntermediateForm.builder.tables.OccurrenceTable;
import compiler.CHRIntermediateForm.builder.tables.RuleTable;
import compiler.CHRIntermediateForm.builder.tables.SolverTable;
import compiler.CHRIntermediateForm.builder.tables.StaticImporter;
import compiler.CHRIntermediateForm.builder.tables.UserDefinedConstraintTable;
import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.bi.SolverBuiltInConstraintInvocation;
import compiler.CHRIntermediateForm.constraints.bi.Failure;
import compiler.CHRIntermediateForm.constraints.bi.IBuiltInConjunct;
import compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint;
import compiler.CHRIntermediateForm.constraints.bi.SolverBuiltInConstraint;
import compiler.CHRIntermediateForm.constraints.java.Assignment;
import compiler.CHRIntermediateForm.constraints.java.AssignmentConjunct;
import compiler.CHRIntermediateForm.constraints.java.IJavaConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.OccurrenceType;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.debug.DebugInfo;
import compiler.CHRIntermediateForm.exceptions.AmbiguityException;
import compiler.CHRIntermediateForm.exceptions.AmbiguousArgumentsException;
import compiler.CHRIntermediateForm.exceptions.AmbiguousIdentifierException;
import compiler.CHRIntermediateForm.exceptions.DuplicateIdentifierException;
import compiler.CHRIntermediateForm.exceptions.IdentifierException;
import compiler.CHRIntermediateForm.exceptions.IllegalArgumentsException;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.exceptions.ToDoException;
import compiler.CHRIntermediateForm.exceptions.UnknownIdentifierException;
import compiler.CHRIntermediateForm.id.Identifier;
import compiler.CHRIntermediateForm.matching.Matching;
import compiler.CHRIntermediateForm.matching.MatchingInfos;
import compiler.CHRIntermediateForm.members.AbstractMethodInvocation;
import compiler.CHRIntermediateForm.members.ConstructorInvocation;
import compiler.CHRIntermediateForm.members.Field;
import compiler.CHRIntermediateForm.members.FieldAccess;
import compiler.CHRIntermediateForm.modifiers.IllegalModifierException;
import compiler.CHRIntermediateForm.modifiers.Modifier;
import compiler.CHRIntermediateForm.rulez.Body;
import compiler.CHRIntermediateForm.rulez.Guard;
import compiler.CHRIntermediateForm.rulez.Head;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.CHRIntermediateForm.rulez.RuleType;
import compiler.CHRIntermediateForm.solver.Solver;
import compiler.CHRIntermediateForm.types.GenericType;
import compiler.CHRIntermediateForm.types.PrimitiveType;
import compiler.CHRIntermediateForm.types.TypeParameter;
import compiler.CHRIntermediateForm.variables.AbstractVariable;
import compiler.CHRIntermediateForm.variables.FormalVariable;
import compiler.CHRIntermediateForm.variables.IActualVariable;
import compiler.CHRIntermediateForm.variables.IVariable;
import compiler.CHRIntermediateForm.variables.NamelessVariable;
import compiler.CHRIntermediateForm.variables.Variable;
import compiler.CHRIntermediateForm.variables.VariableFactory;
import compiler.CHRIntermediateForm.variables.VariableType;
import compiler.CHRIntermediateForm.variables.VariableFactory.IdentifierRestriction;
import compiler.CHRIntermediateForm.variables.exceptions.IllegalVariableException;
import compiler.CHRIntermediateForm.variables.exceptions.MultiTypedVariableException;
import compiler.options.Options;

/*
 * What is wrong with this builder?
 *  - Clearly, the one-pass parsing was a big mistake:
 *      . for infix occurrences we do not know which user-defined
 *          constraint it is until we reach the infix identifier
 *      . makes it virtually impossible to parse arithmetic expressions
 *      . etc
 *  - etc
 */

/**
 * The main builder for the CHR intermediate form (I do no like the name
 * anymore, but I am also to lazy to change it). As anyone can see: this
 * baby has become one huge builder!
 * 
 * @author Peter Van Weert
 */
public class CHRIntermediateFormBuilder extends
        AbstractBuilder<CHRIntermediateForm> implements
        ICHRIntermediateFormBuilder<CHRIntermediateForm> {

    protected final Current<Rule> currentRule = new Current<Rule>();

    /**
     * The current negative head we are building. We need this to bypass the
     * building of the negative heads occurrences and its guard.
     */
    protected final Current<NegativeHead> currentNegativeHead = new Current<NegativeHead>();

    protected final Current<UserDefinedConstraint> currentUdConstraint = new Current<UserDefinedConstraint>();

    protected final Current<ConjunctBuildingStrategy> currentCBS = new Current<ConjunctBuildingStrategy>();

    protected final Current<TypeParameter> currentTypeParameter = new Current<TypeParameter>();

    protected final Current<VariableType> currentVariableType = new Current<VariableType>() {
        @Override
        public void set(VariableType current)
                throws java.lang.IllegalStateException {
            
            try {
                super.set(ensureUniqueAndCorrect(current));
            } catch (BuilderException be) {
                throw new IllegalStateException(be);
            }
        }
    };

    private boolean currentBoolean;

    private final Stack<GenericType> currentParametrizable = new Stack<GenericType>();

    private Handler handler;

    private Map<VariableType, VariableType> variableTypes;

    private UserDefinedConstraintTable udConstraintTable;

    private BuiltInConstraintTable biConstraintTable;
    
    protected VariableFactory variableFactory;

    private RuleTable ruleTable;

    private ClassTable classTable;
    
    private FieldTable fieldTable;
    
    private MethodTable methodTable;
    
    private StaticImporter staticImporter;

    private SolverTable solverTable;
    
    private OccurrenceTable identifiedOccurrences;
    
    private DebugInfo debugInfo;

    protected void beginParametrizing(GenericType parametrizable) {
        currentParametrizable.push(parametrizable);
    }

    protected void beginNonParametrizable() {
        currentParametrizable.push(null);
    }

    protected boolean isParametrizing() {
        return !currentParametrizable.isEmpty()
                && currentParametrizable.peek() != null;
    }

    protected GenericType getCurrentParametrizable() {
        if (!isParametrizing())
            throw new IllegalStateException();
        return currentParametrizable.peek();
    }

    protected GenericType endParametrizing() throws BuilderException {
        if (!isParametrizing()) throw new IllegalStateException();
        
        GenericType current = currentParametrizable.pop();
        
        if (!current.isValid())
            throw new BuilderException("Invalid type arguments");
        
        GenericType result = GenericType.getUniqueInstance(current);
        
        if (result == current && !result.hasTypeParameters()) {
            System.err.printf(
                " --> warning: references to generic type %s should be parameterized%n",
                result.toParametrizableString()
            );
            // XXX werken met raw types is mogelijk niet volledig correct, maar iets is beter dan niets
        } 

        return result;
    }
    
    protected void cancelParametrizing() {
        currentParametrizable.pop();
    }

    protected void endNonParametrizable() {
        if (currentParametrizable.pop() != null)
            throw new IllegalStateException();
    }

    public CHRIntermediateFormBuilder(Options options) {
        setBiConstraintTable(new BuiltInConstraintTable());
        
        setClassTable(new ClassTable());
        options.useClassTable(getClassTable());
        setFieldTable(new FieldTable());
        setMethodTable(new MethodTable());
        setStaticImporter(new StaticImporter(
            getClassTable(), getFieldTable(), getMethodTable())
        );
        
        setRuleTable(new RuleTable());
        setSolverTable(new SolverTable());
        setUdConstraintTable(new UserDefinedConstraintTable());
        
        setVariableFactory(new VariableFactory());
        getVariableFactory().addIdentifierRestriction(
            new IdentifierRestriction() {
                public void testIdentifier(String id, boolean local) throws IdentifierException {
                    if (isImportedClass(id)) {
                        Formatter cause = new Formatter();
                        try {
                            Class<?> clazz = CHRIntermediateFormBuilder.this.getClass(id);
                            cause.format(
                                " --> warning: variable %s hides type name: %s", id, clazz
                            );
                        } catch (ClassNotFoundException cnfe) {
                            throw new RuntimeException(cnfe);   // should not happen!
                        } catch (AmbiguousIdentifierException aee) {
                            cause.format(
                                " --> warning: variable %s hides ambiguous type name", id
                            );
                        }
                        System.err.println(cause.toString());
                    }
                }
            }
        );
        getVariableFactory().addIdentifierRestriction(
            new IdentifierRestriction() {
                public void testIdentifier(String id, boolean local) throws IdentifierException {
                    if (isField(id)) {
                        Formatter cause = new Formatter();
                        try {
                            if (!local && isFinalField(id)) {
                                Field field = getField(id);
                                cause.format(
                                    "Variable name %s collapses with final field name (%s)", id, field 
                                );
                                throw new IllegalIdentifierException(cause.toString());
                            } else {
                                Field field = getField(id);
                                cause.format(
                                    " --> warning: local variable name %s collapses with field name (%s)", id, field
                                );
                            }
                        } catch (AmbiguousIdentifierException aie) {
                            // should not happen too often :-)
                            if (!local) {
                                cause.format(
                                    "Variable name %s collapses with ambiguous field name", id
                                );
                                throw new IllegalIdentifierException(cause.toString());
                            } else { 
                                cause.format(
                                    " --> warning: local variable name %s collapses with ambiguous field name", id
                                );
                            }
                        }
                        System.err.println(cause.toString());
                    }
                }
            }
        );
        
        setDebugInfo(options.getDebug());
        setVariableTypes(new HashMap<VariableType, VariableType>());
        setIdentifiedOccurrences(new OccurrenceTable());
    }

    public void init() throws BuilderException {
        reset();
    }

    public void abort() {
        currentBoolean = false;
        currentCBS.reset();
        currentNegativeHead.reset();
        currentParametrizable.clear();
        currentRule.reset();
        currentTypeParameter.reset();
        currentUdConstraint.reset();
        currentVariableType.reset();
        
        reset();
    }

    protected void reset() {
        getBiConstraintTable().reset();
        getClassTable().reset();
        getRuleTable().reset();
        getSolverTable().reset();
        getUdConstraintTable().reset();
        getVariableFactory().reset();
        getVariableTypes().clear();
        getMethodTable().reset();
        getFieldTable().reset();
        getIdentifiedOccurrences().reset();
        setResult(null);
    }

    public void finish() throws BuilderException {
        if (super.getResult() != null)
            throw new BuilderException("Building already finished");
        
        setResult(new CHRIntermediateForm(
            getHandler(), 
            getVariableTypes().keySet(),
            getVariableFactory().getLocalVariables(),
            getUdConstraintTable().getValues(), 
            getRuleTable().getValues(),
            getSolverTable().getValues(),
            getDebugInfo()
        ));
    }

    @Override
    public CHRIntermediateForm getResult() throws BuilderException {
        final CHRIntermediateForm result = super.getResult();
        if (result == null)
            throw new BuilderException("Did you forget to finish the building?");
        return result;
    }
    
    public void buildPackageDeclaration(String packageName) throws BuilderException {
        try {
            getClassTable().setCurrentPackage(packageName);
        } catch (NullPointerException npe) {
            throw new BuilderException(npe);
        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        }
    }

    public void beginHandler(String identifier, String access) throws BuilderException {
        try {
            changeHandler(new Handler(
                    identifier, 
                    getClassTable().getCurrentPackage(),
                    Modifier.getAccessModifier(access)
                )
            );
            
        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } catch (IllegalModifierException ime) {
            throw new BuilderException(ime);
        } 
    }

    public void beginTypeParameters() throws BuilderException {
        // NOP
    }

    public void beginTypeParameter(String identifier) throws BuilderException {
        try {
            currentTypeParameter.set(new TypeParameter(identifier));

        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (IllegalIdentifierException ie) {
            throw new BuilderException(ie);
        }
    }

    public void beginUpperBound(String name) throws BuilderException {
        try {
            Identifier.testIdentifier(name);
            if (PrimitiveType.isPrimitiveType(name))
                throw new IdentifierException("A primitive type cannot be a upperbound");
            else if (isTypeParameter(name)) {
                if (isCurrentTypeParameter(name))
                    throw new BuilderException("Illegal forward reference to type parameter " + name);
                else {
                    currentTypeParameter.get()
                            .addUpperBound(getTypeParameter(name));
                    beginNonParametrizable();
                }
            } else {
                final GenericType genericType = 
                    GenericType.getInstance(getClass(name));

                if (genericType.isParametrizable())
                    beginParametrizing(genericType);
                else {
                    currentTypeParameter.get().addUpperBound(genericType);
                    beginNonParametrizable();
                }
            }

        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (IndexOutOfBoundsException iobe) {
            throw new BuilderException(iobe);
        } catch (IdentifierException ie) {
            throw new BuilderException(ie);
        } catch (IllegalArgumentException iae) {
            throw new BuilderException(iae);
        } catch (ClassNotFoundException cnfe) {
            throw new BuilderException(cnfe);
        }
    }

    public void endUpperBound() throws BuilderException {
        try {
            if (isParametrizing())
                currentTypeParameter.get().addUpperBound(endParametrizing()
                );
            else
                endNonParametrizable();

        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (ClassCastException cce) {
            throw new BuilderException(cce);
        }
    }

    public void endTypeParameter() throws BuilderException {
        try {
            getHandler().addTypeParameter(currentTypeParameter.get());
            currentTypeParameter.reset();
        } catch (DuplicateIdentifierException die) {
            throw new BuilderException(die);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void endTypeParameters() throws BuilderException {
        // NOP
    }

    public void beginImports() throws BuilderException {
        // NOP
    }

    public void importSingleType(String id) throws BuilderException {
        try {
            Identifier.testIdentifier(id);
            getClassTable().importSingleType(id);

        } catch (ClassNotFoundException cnfe) {
            throw new BuilderException(cnfe);
        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } catch (DuplicateIdentifierException die) {
            throw new BuilderException(die);
        }
    }
    
    public void importOnDemand(String id) throws BuilderException {
        try {
            if (id.endsWith(".*")) 
                id = id.substring(0, id.length() - 2);
            Identifier.testIdentifier(id);
            getClassTable().importOnDemand(id);

        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } 
    }
    
    public void importSingleStatic(String id) throws BuilderException {
        try {
            Identifier.testIdentifier(id);
            getStaticImporter().importSingleStatic(id);

        } catch (ClassNotFoundException cnfe) {
            throw new BuilderException(cnfe);
        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } catch (DuplicateIdentifierException die) {
            throw new BuilderException(die);
        }
    }
    
    public void importStaticOnDemand(String id) throws BuilderException {
        try {
            if (id.endsWith(".*")) 
                id = id.substring(0, id.length() - 2);
            Identifier.testIdentifier(id);
            getStaticImporter().importStaticOnDemand(id);

        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } catch (ClassNotFoundException cnfe) {
            throw new BuilderException(cnfe);
        }
    }

    public void endImports() throws BuilderException {
        // NOP
    }

    public void beginDeclarations() throws BuilderException {
        // NOP
    }

    public void beginSolverDeclaration(String solverClass, String access)
            throws BuilderException {
        try {
            Identifier.testIdentifier(solverClass);
            
            Solver solver = new Solver(
                getClass(solverClass), 
                Modifier.getAccessModifier(access)
            );
            beginParametrizing(solver);

        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } catch (AmbiguousIdentifierException aie) {
            throw new BuilderException(aie);
        } catch (ClassNotFoundException cnfe) {
            throw new BuilderException(cnfe);
        } catch (IllegalArgumentException iae) {
            throw new BuilderException(iae);
        } catch (IllegalModifierException ime) {
            throw new BuilderException(ime);
        } 
    }
    
    public void beginSolverDefault() throws BuilderException {
        // TODO
        throw new BuilderException("TODO");
    }
    
    public void endSolverDefault() throws BuilderException {
        // TODO
        throw new BuilderException("TODO");
    }

    public void beginTypeArguments() throws BuilderException {
        if (!isParametrizing())
            throw new BuilderException("Not parametrizing");
    }

    public void beginTypeArgument(String id) throws BuilderException {
        try {
            Identifier.testIdentifier(id);

            if (PrimitiveType.isPrimitiveType(id))
                throw new IdentifierException("A primitive type cannot be a type parameter");
            if (isTypeParameter(id)) {
                getCurrentParametrizable().addTypeParameter(getTypeParameter(id));
                beginNonParametrizable();
            } else {
                GenericType genericType = GenericType.getInstance(getClass(id));

                if (genericType.isParametrizable())
                    beginParametrizing(genericType);
                else {
                    getCurrentParametrizable().addTypeParameter(genericType);
                    beginNonParametrizable();
                }
            }

        } catch (IdentifierException iie) {
            throw new BuilderException(iie);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (ClassNotFoundException cnfe) {
            throw new BuilderException(cnfe);
        }
    }

    public void endTypeArgument() throws BuilderException {
        try {
            if (isParametrizing()) {
                getCurrentParametrizable().addTypeParameter(endParametrizing());
            } else
                endNonParametrizable();

        } catch (IllegalArgumentException iae) {
            throw new BuilderException(iae);
        } catch (IndexOutOfBoundsException iobe) {
            throw new BuilderException(iobe);
        }
    }

    public void endTypeArguments() throws BuilderException {
        // NOP
    }

    public void buildSolverName(String identifier) throws BuilderException {
        try {
            if (identifier == null)
                identifier = getSolverTable().createID();
            else {
                Identifier.testUdSimpleIdentifier(identifier);
                if (getSolverTable().isDeclaredId(identifier))
                    throw new DuplicateIdentifierException(identifier);
            }
            ((Solver) getCurrentParametrizable()).setIdentifier(identifier);

        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } catch (DuplicateIdentifierException die) {
            throw new BuilderException(die);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (ClassCastException cce) {
            throw new BuilderException(cce);
        }
    }

    public void endSolverDeclaration() throws BuilderException {
        try {
            Solver solver = (Solver)getCurrentParametrizable();
            if (solver.isParametrizable())
                endParametrizing();
            else
                cancelParametrizing();
            registerNewSolver(solver);
            
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (IllegalArgumentException iae) {
            throw new BuilderException(iae);
        } catch (ClassCastException cce) {
            throw new BuilderException(cce);
        } catch (DuplicateIdentifierException die) {
            throw new BuilderException(die);
        } catch (IdentifierException ie) {
            throw new BuilderException(ie);
        }
    }
   
    protected void registerNewSolver(Solver solver) 
    throws DuplicateIdentifierException, IdentifierException {
        getSolverTable().declareSolver(solver);
        getBiConstraintTable().registerSolver(solver);
    }

    public void beginConstraintDeclaration(String id, String access) throws BuilderException {
        try {
            currentUdConstraint.set(
                getUdConstraintTable().declareConstraint(
                    id, Modifier.getAccessModifier(access)
                )
            );
        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } catch (DuplicateIdentifierException die) {
            throw new BuilderException(die);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void buildInfixIdentifier(String infix) throws BuilderException {
        try {
            final UserDefinedConstraint ud = currentUdConstraint.get();
            
            try {
                ud.addInfixIdentifier(infix);
                if (ud.getNbInfixIdentifiers() == 1) tempInfixId(ud);
            } catch (DuplicateIdentifierException die) {
                throw new BuilderException(die);
            } catch (IllegalIdentifierException iie) {
                throw new BuilderException(iie);
            } catch (IdentifierException ie) {
                throw new BuilderException(ie);
            }
        
            try {
                getUdConstraintTable().declareInfixIdentifier(ud, infix);
            } catch (DuplicateIdentifierException die) {
                throw new BuilderException(die, 
            		"Overloading user-defined constraint identifiers is not yet allowed"
        		);
            } 
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void beginConstraintArgument() throws BuilderException {
        // NOP
    }

    public void beginConstraintArgumentType(String type, boolean fixed)
            throws BuilderException {
        beginVariableType(type, fixed);
    }

    public void endConstraintArgumentType() throws BuilderException {
        endVariableType();
    }

    public void buildConstraintArgumentName(String name) throws BuilderException {
        try {
            final UserDefinedConstraint current = currentUdConstraint.get();
            if (name == null)
                name = new StringBuilder(3)
                    .append('$').append(current.getArity())
                    .toString();
            else
                Identifier.testUdSimpleIdentifier(name, true);

            current.addFormalVariable(name, currentVariableType.get());

        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (DuplicateIdentifierException die) {
            throw new BuilderException(die);
        }
    }

    public void endConstraintArgument() throws BuilderException {
        currentVariableType.reset();
    }

    public void endConstraintDeclaration() {
        currentUdConstraint.reset();
    }

    public void endDeclarations() throws BuilderException {
        // NOP
    }

    public void beginRules() throws BuilderException {
        if (currentRule.isSet())
            throw new BuilderException("The previous rules is not yet finished");
    }

    public void beginLocalVariableDeclarations() throws BuilderException {
        // NOP
    }

    public void beginVariableType(String type, boolean fixed)
            throws BuilderException {
        try {
            if (PrimitiveType.isPrimitiveType(type)) {
                currentVariableType.set(
                    new VariableType(PrimitiveType.getInstance(type), fixed)
                );
                beginNonParametrizable();
            } else {
                Identifier.testIdentifier(type);

                if (isTypeParameter(type)) {
                    currentVariableType.set(
                        new VariableType(getTypeParameter(type), fixed)
                    );
                    beginNonParametrizable();
                } else {
                    final GenericType genericType = GenericType
                            .getInstance(getClass(type));

                    if (genericType.isParametrizable()) {
                        beginParametrizing(genericType);
                        currentBoolean = fixed;
                    } else {
                        currentVariableType.set(
                            new VariableType(genericType, fixed)
                        );
                        beginNonParametrizable();
                    }
                }
            }
        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } catch (ClassNotFoundException cnfe) {
            throw new BuilderException(cnfe);
        } catch (IllegalArgumentException iae) {
            throw new BuilderException(iae);
        } catch (java.lang.IllegalStateException ise) {
            Throwable cause = ise.getCause();
            if (cause instanceof BuilderException)
                throw (BuilderException)cause;
            else
                throw ise;
        } catch (AmbiguousIdentifierException aie) {
            throw new BuilderException(aie);
        }
    }

    public void endVariableType() throws BuilderException {
        try {
            if (isParametrizing()) {
                currentVariableType.set(
                    new VariableType(endParametrizing(), currentBoolean)
                );
            }
        } catch (IllegalStateException ise) {
            Throwable cause = ise.getCause();
            if (cause instanceof BuilderException)
                throw (BuilderException)cause;
            else
                throw ise;
        }
    }

    public void declareLocalVariable(String id) throws BuilderException {
        try {
            getVariableFactory().registerLocalVariable(
                id, currentVariableType.get()
            );
        } catch (IdentifierException die) {
            throw new BuilderException(die);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void endLocalVariableDeclarations() throws BuilderException {
        currentVariableType.reset();
    }
    
    // XXX variable declarations are just a temporary fix (cf manual)
    public void beginVariableDeclarations() throws BuilderException {
        // NOP
    }
    
    public void declareVariable(String id) throws BuilderException {
        try {
            getVariableFactory().tempDeclareVariable(
                id, currentVariableType.get()
            );
        } catch (IdentifierException ie) {
            throw new BuilderException(ie);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }
    
    public void endVariableDeclarations() throws BuilderException {
        currentVariableType.reset();
    }
    
    public void beginRule() throws BuilderException {
        if (currentRule.isSet())
            throw new BuilderException("Previous rule not finished yet");
    }

    public void beginRuleDefinition(RuleType type) throws BuilderException {
        beginRuleDefinition(null, type);
    }

    public void beginRuleDefinition(String id, RuleType type)
            throws BuilderException {
        try {
            Rule rule = getRuleTable().declareRule(id, type);
            currentRule.set(rule);
            System.out.println("Reading rule: " + rule.getIdentifier());

        } catch (DuplicateIdentifierException die) {
            throw new BuilderException(die);
        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } catch (IllegalArgumentException iae) {
            throw new BuilderException(iae);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void beginBody() throws BuilderException {
        try {
            Body body = currentRule.get().getBody(); 
            beginVariableScope(true, false);
            currentCBS.set(new BodyConjunctBuildingStrategy(body));

        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void beginGuard() throws BuilderException {
        try {
            Guard guard = currentRule.get().getPositiveHead().getGuard();
            beginVariableScope(false, false);
            currentCBS.set(new GuardConjunctBuildingStrategy(guard));
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void beginPositiveHead() throws BuilderException {
        try {
            Rule rule = currentRule.get();
            beginVariableScope(false, true);
            currentCBS.set(new OccurrenceBuildingStrategy(rule.getPositiveHead()));
            
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void beginKeptOccurrences() throws BuilderException {
        setCurrentOccurrenceType(KEPT);
    }

    public void beginRemovedOccurrences() throws BuilderException {
        setCurrentOccurrenceType(REMOVED);
    }
    
    public void beginNegativeHeads() throws BuilderException {
        // NOP
    }
    
    public void beginNegativeHead() throws BuilderException {
        try {
            NegativeHead head = currentRule.get().addNegativeHead();
            beginVariableScope(false, true);
            currentNegativeHead.set(head);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }
    
    public void beginNegativeOccurrences() throws BuilderException {
        try {
            currentCBS.set(new OccurrenceBuildingStrategy(
                currentNegativeHead.get(),
                NEGATIVE
            ));
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }
    
    
    /*
     * The following members are just a temporary patch!
     * 
     * At the moment infix identifiers for user defined constraints can only
     * be used if all of them have the same formal types.
     * The reason is, as always, the one-pass parsing...
     */
    private Current<String> infixId = new Current<String>();    
    
    protected String getInfixId() throws IllegalStateException {
        return infixId.get();
    }
    protected void tempInfixId(UserDefinedConstraint ud) throws ToDoException {
        try {
            if (infixId.isSet()) {
                UserDefinedConstraint previous = getUdConstraint(infixId.get(), true);
                if (! previous.getFormalParameterTypeAt(0).equals(ud.getFormalParameterTypeAt(0)))
                    throw new ToDoException("Infix identifiers for differently typed user-defined constraints... ");
            } else {
                infixId.set(ud.getInfixIdentifiers()[0]);
            }
            
        } catch (IllegalIdentifierException iie) {
            // SHOULD NEVER HAPPEN:
            throw new IllegalStateException(iie);
        }
    }
    
    protected void setVariableFactory(VariableFactory variableFactory) {
        this.variableFactory = variableFactory;
    }    
    protected VariableFactory getVariableFactory() {
        return variableFactory;
    }
    protected void beginVariableScope(boolean local, boolean nameless) {
        getVariableFactory().newScope();
        getVariableFactory().endVariableDeclarations();
        getVariableFactory().allowLocalVariables(local);
        getVariableFactory().allowNamelessVariable(nameless);
    }
    protected void endVariableScope() throws BuilderException {
        try {
            getVariableFactory().endScope();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }
    
    public void beginNegativeGuard() throws BuilderException {
        try {
            currentCBS.set(new GuardConjunctBuildingStrategy(
                currentNegativeHead.get().getGuard()
            ));
            beginVariableScope(false, false);
            
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }
    
    public void addFailureConjunct() throws BuilderException {
        currentCBS.get().addFailureConjunct();
    }
    
    public void addFailureConjunct(String message) throws BuilderException {
        currentCBS.get().addFailureConjunct(message);
    }

    public void addFieldAccessConjunct(String id) throws BuilderException {
        try {
            Identifier.testComposedIdentifier(id);
            currentCBS.get().addFieldAccessConjunct(id);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } catch (UnsupportedOperationException uoe) {
            throw new BuilderException(uoe);
        }
    }
    
    public void addVariableConjunct(String id) throws BuilderException {
        try {
            Identifier.testUdSimpleIdentifier(id, true);
            currentCBS.get().addVariableConjunct(id);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } catch (UnsupportedOperationException uoe) {
            throw new BuilderException(uoe);
        }
    }
    
    public void addFlagConjunct(String id) throws BuilderException {
        try {
            Identifier.testUdSimpleIdentifier(id, false);
            currentCBS.get().addFlagConjunct(id);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } catch (UnsupportedOperationException uoe) {
            throw new BuilderException(uoe);
        }
    }
    
    public void addSimpleIdConjunct(String id) throws BuilderException {
        try {
            Identifier.testSimpleIdentifier(id);
            currentCBS.get().addSimpleIdConjunct(id);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } catch (UnsupportedOperationException uoe) {
            throw new BuilderException(uoe);
        }
    }

    public void beginArgumentedConjunct(String id) throws BuilderException {
        if (Identifier.isSimple(id))
            beginSimpleIdConjunct(id);
        else
            beginComposedIdConjunct(id);
    }

    public void beginSimpleIdConjunct(String id) throws BuilderException {
        try {
            currentCBS.get().beginSimpleIdConjunct(id);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void beginBuiltInConstraint(String id) throws BuilderException {
        try {
            currentCBS.get().beginBuiltInConstraint(id);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void beginUserDefinedConstraint(String id) throws BuilderException {
        try {
            currentCBS.get().beginUserDefinedConstraint(id);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void beginComposedIdConjunct(String id) throws BuilderException {
        try {
            currentCBS.get().beginComposedIdConjunct(id);
        } catch (IllegalStateException iie) {
            throw new BuilderException(iie);
        }
    }

    public void beginMarkedBuiltInConstraint(String id) throws BuilderException {
        try {
            currentCBS.get().beginMarkedBuiltInConjunct(id);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void beginMethodInvocationConjunct(String id)
            throws BuilderException {
        try {
            currentCBS.get().beginMethodInvocationConjunct(id);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }
    
    public void beginConstructorInvocationConjunct(String id)
            throws BuilderException {
        try {
            currentCBS.get().beginConstructorInvocationConjunct(id);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void beginArguments() throws BuilderException {
        try {
            currentCBS.get().beginArguments();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void addNullArgument() throws BuilderException {
        try {
            currentCBS.get().addNullArgument();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void addPrimitiveArgument(boolean value) throws BuilderException {
        try {
            currentCBS.get().addPrimitiveArgument(value);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void addPrimitiveArgument(byte value) throws BuilderException {
        try {
            currentCBS.get().addPrimitiveArgument(value);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void addPrimitiveArgument(char value) throws BuilderException {
        try {
            currentCBS.get().addPrimitiveArgument(value);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void addPrimitiveArgument(double value) throws BuilderException {
        try {
            currentCBS.get().addPrimitiveArgument(value);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void addPrimitiveArgument(float value) throws BuilderException {
        try {
            currentCBS.get().addPrimitiveArgument(value);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void addPrimitiveArgument(int value) throws BuilderException {
        try {
            currentCBS.get().addPrimitiveArgument(value);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void addPrimitiveArgument(long value) throws BuilderException {
        try {
            currentCBS.get().addPrimitiveArgument(value);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void addPrimitiveArgument(short value) throws BuilderException {
        try {
            currentCBS.get().addPrimitiveArgument(value);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void addStringLiteralArgument(String value) throws BuilderException {
        try {
            if (value == null)
                throw new BuilderException("null <> String literal");
            currentCBS.get().addStringLiteralArgument(value);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }
    
    public void addCharLiteralArgument(String value) throws BuilderException {
        try {
            if (value == null)
                throw new BuilderException("null <> char literal");
            currentCBS.get().addCharLiteralArgument(value);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }
    
    public void addFieldAccessArgument(String id) throws BuilderException {
        try {
            Identifier.testComposedIdentifier(id);
            currentCBS.get().addFieldAccessArgument(id);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        }
    }

    public void addIdentifiedArgument(String id) throws BuilderException {
        try {
            Identifier.testIdentifier(id);
            currentCBS.get().addIdentifiedArgument(id);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        }
    }

    public void beginConstructorInvocationArgument(String id)
            throws BuilderException {
        try {
            currentCBS.get().beginConstructorInvocationArgument(id);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void beginMethodInvocationArgument(String id)
            throws BuilderException {
        try {
            currentCBS.get().beginMethodInvocationArgument(id);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (UnsupportedOperationException uoe) {
            throw new BuilderException(uoe);
        }
    }

    public void endMethodInvocationArgument() throws BuilderException {
        try {
            currentCBS.get().endMethodInvocationArgument();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void endConstructorInvocationArgument() throws BuilderException {
        try {
            currentCBS.get().endConstructorInvocationArgument();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void endArguments() throws BuilderException {
        try {
            currentCBS.get().endArguments();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void endSimpleIdConjunct() throws BuilderException {
        try {
            currentCBS.get().endSimpleIdConjunct();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void endBuiltInConstraint() throws BuilderException {
        try {
            currentCBS.get().endBuiltinConstraint();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void endUserDefinedConstraint() throws BuilderException {
        try {
            currentCBS.get().endUserDefinedConstraint();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void endComposedIdConjunct() throws BuilderException {
        try {
            currentCBS.get().endComposedIdConjunct();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (UnsupportedOperationException uoe) {
            throw new BuilderException(uoe);
        }
    }

    public void endMarkedBuiltInConstraint() throws BuilderException {
        try {
            currentCBS.get().endMarkedBuiltinConstraint();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (UnsupportedOperationException uoe) {
            throw new BuilderException(uoe);
        }
    }

    public void endMethodInvocationConjunct() throws BuilderException {
        try {
            currentCBS.get().endMethodInvocationConjunct();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (UnsupportedOperationException uoe) {
            throw new BuilderException(uoe);
        }
    }
    
    public void endConstructorInvocationConjunct() throws BuilderException {
        try {
            currentCBS.get().endConstructorInvocationConjunct();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (UnsupportedOperationException uoe) {
            throw new BuilderException(uoe);
        }
    }

    public void endArgumentedConjunct() throws BuilderException {
        try {
            currentCBS.get().endArgumentedConjunct();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (UnsupportedOperationException uoe) {
            throw new BuilderException(uoe);
        }
    }
    
    public void beginDeclarationConjunct() throws BuilderException {
    	try {
            currentCBS.get().beginDeclarationConjunct();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }
    
    public void buildDeclaredVariable(String id) throws BuilderException {
    	try {
            currentCBS.get().buildDeclaredVariable(id);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void endDeclarationConjunct() throws BuilderException {
    	try {
            currentCBS.get().endDeclarationConjunct();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void beginInfixConstraint() throws BuilderException {
        try {
            currentCBS.get().beginInfixConstraint();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }
    
    public void buildBuiltInInfix(String infix) throws BuilderException {
        try {
            currentCBS.get().buildBuiltInInfix(infix);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void buildInfix(String infix) throws BuilderException {
        try {
            currentCBS.get().buildInfix(infix);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void buildMarkedInfix(String infix) throws BuilderException {
        try {
            currentCBS.get().buildMarkedInfix(infix);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void buildUserDefinedInfix(String infix) throws BuilderException {
        try {
            currentCBS.get().buildUserDefinedInfix(infix);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void endInfixConstraint() throws BuilderException {
        try {
            currentCBS.get().endInfixConstraint();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }
    
    public void setPassive() throws BuilderException {
        try {
            OccurrenceBuildingStrategy builder = 
                (OccurrenceBuildingStrategy)currentCBS.get();
            builder.currentOccurrence.get().setPassive();
            
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (IndexOutOfBoundsException iobe) {
            throw new BuilderException(iobe);
        } catch (ClassCastException cce) {
            throw new BuilderException(cce);
        }
    }
    
    public void buildOccurrenceId(String id) throws BuilderException {
        try {
            OccurrenceBuildingStrategy builder = 
                (OccurrenceBuildingStrategy)currentCBS.get();
            getIdentifiedOccurrences().identify(id, builder.currentOccurrence.get());
            
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (DuplicateIdentifierException die) {
            throw new BuilderException(die);
        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        } catch (IndexOutOfBoundsException iobe) {
            throw new BuilderException(iobe);
        } catch (ClassCastException cce) {
            throw new BuilderException(cce);
        }
    }
    
    public void endNegativeGuard() throws BuilderException {
        resetCurrentCBS();
        endVariableScope();
    }
    
    public void endNegativeOccurrences() throws BuilderException {
        resetCurrentCBS();        
    }
    
    public void endNegativeHead() throws BuilderException {
        try {
            final NegativeHead head = currentNegativeHead.get();
            if (! head.isValid())
                throw new BuilderException("Illegal negative head: " + head);

            endVariableScope();
            currentNegativeHead.reset();
            
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }
    
    public void endNegativeHeads() throws BuilderException {
        // NOP
    }

    public void endKeptOccurrences() throws BuilderException {
        resetCurrentOccurrenceType();
    }

    public void endRemovedOccurrences() throws BuilderException {
        resetCurrentOccurrenceType();
    }
    
    protected void setCurrentOccurrenceType(OccurrenceType type) throws BuilderException {
        try {
            ((OccurrenceBuildingStrategy)currentCBS.get()).currentType.set(type);
        } catch (ClassCastException cce) {
            throw new BuilderException(cce);
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }
    
    protected void resetCurrentOccurrenceType() throws BuilderException {
        try {
            ((OccurrenceBuildingStrategy)currentCBS.get()).currentType.reset();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        } catch (ClassCastException cce) {
            throw new BuilderException(cce);
        }
    }
    
    protected void resetCurrentCBS() throws BuilderException {
        try {
            currentCBS.get().terminate();
            currentCBS.reset();
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void endPositiveHead() throws BuilderException {
        resetCurrentCBS();
    }

    public void endGuard() throws BuilderException {
        try {
            currentCBS.get().terminate();
            currentCBS.reset();
            endVariableScope();
            
        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void endBody() throws BuilderException {
        endVariableScope();
        resetCurrentCBS();
    }

    public void endRuleDefinition() throws BuilderException {
        // NOP
    }
    
    public void beginPragmas() throws BuilderException {
        // NOP
    }
    
    public void addPassivePragma(String id) throws BuilderException {
        try {
            getIdentifiedOccurrence(id).setPassive();
        } catch (IllegalIdentifierException iie) {
            throw new BuilderException(iie);
        }
    }
    
    public void addNoHistoryPragma() throws BuilderException {
        currentRule.get().setNoHistory();
    }
    
    public void addDebugPragma() throws BuilderException {
        getDebugInfo().specifyRule(currentRule.get());
    }
    
    public void endPragmas() throws BuilderException {
        // NOP
    }
    
    public void endRule() throws BuilderException {
        try {
            final String id = currentRule.get().getIdentifier();

            if (!currentRule.get().isValid())
                throw new BuilderException("Invallid rule definition: " + id);

            currentRule.reset();
            endVariableScope();
            getIdentifiedOccurrences().reset();

        } catch (IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }

    public void endRules() throws BuilderException {
        if (currentRule.isSet())
            throw new BuilderException("Illegal state");
    }

    public void endHandler() throws BuilderException {
        // NOP
    }

    protected Handler getHandler() {
        return handler;
    }
    protected void changeHandler(Handler handler) {
    	setHandler(handler);
    	getUdConstraintTable().setHandler(handler);
    }
    private void setHandler(Handler handler) {
        this.handler = handler;
    }

    protected Map<VariableType, VariableType> getVariableTypes() {
        return variableTypes;
    }
    protected VariableType ensureUniqueAndCorrect(VariableType variableType) 
    throws BuilderException {
        VariableType deja = getVariableTypes().get(variableType);
        if (deja != null) return deja;

        GuardConjunctBuildingStrategy guardCBS = getNoGuardGCBS();
            
        guardCBS.beginBuiltInConstraint(IBuiltInConstraint.EQ);

        guardCBS.beginArguments();
        guardCBS.addArgument(FormalArgument.getOneInstance(variableType));
        guardCBS.addArgument(FormalArgument.getOtherInstance(variableType.getType()));
        guardCBS.endArguments();
            
        variableType.initEq(guardCBS.getBuiltinConstraint(variableType.isFixed()));
        
        getVariableTypes().put(variableType, variableType);
        getBiConstraintTable().registerVariableType(variableType);
        
        return variableType;
    }
    protected void setVariableTypes(Map<VariableType, VariableType> variableTypes) {
        this.variableTypes = variableTypes;
    }

    protected BuiltInConstraintTable getBiConstraintTable() {
        return biConstraintTable;
    }
    protected void setBiConstraintTable(BuiltInConstraintTable biConstraintTable) {
        this.biConstraintTable = biConstraintTable;
    }

    protected ClassTable getClassTable() {
        return classTable;
    }
    protected void setClassTable(ClassTable classTable) {
        this.classTable = classTable;
    }

    protected RuleTable getRuleTable() {
        return ruleTable;
    }
    protected void setRuleTable(RuleTable ruleTable) {
        this.ruleTable = ruleTable;
    }

    protected SolverTable getSolverTable() {
        return solverTable;
    }
    protected void setSolverTable(SolverTable solverTable) {
        this.solverTable = solverTable;
    }

    protected UserDefinedConstraintTable getUdConstraintTable() {
        return udConstraintTable;
    }
    protected void setUdConstraintTable(UserDefinedConstraintTable udConstraintTable) {
        this.udConstraintTable = udConstraintTable;
    }

    protected OccurrenceTable getIdentifiedOccurrences() {
        return identifiedOccurrences;
    }
    protected void setIdentifiedOccurrences(
            OccurrenceTable identifiedOccurrences) {
        this.identifiedOccurrences = identifiedOccurrences;
    }
    
    protected FieldTable getFieldTable() {
        return fieldTable;
    }
    protected boolean isField(String name) {
        return getFieldTable().isField(name);
    }
    protected boolean isFinalField(String name) throws AmbiguousIdentifierException {
        Field field = getField(name);
        return (field != null) && field.isFinal();
    }
    protected Field getField(String name) throws AmbiguousIdentifierException {
        return getFieldTable().getField(name);
    }    
    protected void setFieldTable(FieldTable fieldTable) {
        this.fieldTable = fieldTable;
    }

    protected MethodTable getMethodTable() {
        return methodTable;
    }
    protected void setMethodTable(MethodTable methodTable) {
        this.methodTable = methodTable;
    }

    protected StaticImporter getStaticImporter() {
        return staticImporter;
    }
    protected void setStaticImporter(StaticImporter staticImporter) {
        this.staticImporter = staticImporter;
    }

    protected IActualVariable getAllowedVariable(String id) throws BuilderException {
        try {
            return getVariableFactory().getAllowedVariable(id);
        } catch (UnknownIdentifierException uie) {
            throw new BuilderException(uie);
        } catch (IllegalVariableException ive) {
            throw new BuilderException(ive);
        } catch (java.lang.IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }
    protected Variable getNamedVariable(String id) throws BuilderException {
        try {
            return getVariableFactory().getNamedVariable(id);
        } catch (UnknownIdentifierException uie) {
            throw new BuilderException(uie);
        } catch (IllegalVariableException ive) {
            throw new BuilderException(ive);
        } catch (java.lang.IllegalStateException ise) {
            throw new BuilderException(ise);
        }
    }
    
    protected IActualVariable createImplicitVariable(FormalVariable formal) {
        return VariableFactory.createImplicitVariable(formal);
    }
    
    protected IActualVariable getVariable(String id, FormalVariable formal)
    throws BuilderException {
        try {
            return getVariableFactory().getVariable(id, formal);
        } catch (IllegalVariableException ive) {
            throw new BuilderException(ive);
        } catch (IdentifierException iie) {
            throw new BuilderException(iie);
        } catch (MultiTypedVariableException mtve) {
            throw new BuilderException(mtve);
        }
    } 
    
    protected boolean isAllowedVariable(String id) throws BuilderException {
        return getVariableFactory().isAllowedVariable(id);
    }
    protected boolean isNamedVariable(String id) throws BuilderException {
        return getVariableFactory().isNamedVariable(id);
    }
    protected boolean isRuleVariable(IVariable var) throws BuilderException {
        return isRuleVariable(var.getIdentifier());
    }
    protected boolean isRuleVariable(String id) throws BuilderException {
        return getVariableFactory().isRuleVariable(id);
    }

    protected Solver getSolver(String id) {
        return getSolverTable().get(id);
    }

    protected boolean isSolver(String id) {
        return getSolver(id) != null;
    }

    protected Class<?> getClass(String identifier) 
    throws ClassNotFoundException, AmbiguousIdentifierException {        
        return getClassTable().getClass(identifier);
    }
    
    protected boolean isImportedClass(String identifier) {
        return getClassTable().isImported(identifier);
    }
    
    protected boolean isClass(String identifier) {
    	return getClassTable().isImported(identifier);
    }

    protected boolean isTypeParameter(String id) {
        return getHandler().hasAsTypeParameter(id)
            || isCurrentTypeParameter(id);
    }
    
    protected boolean isCurrentTypeParameter(String id) {
        return currentTypeParameter.isSet()
            && currentTypeParameter.get().getIdentifier().equals(id);
    }

    protected TypeParameter getTypeParameter(String id) {
        if (isCurrentTypeParameter(id))
            return currentTypeParameter.get();
        else
            return getHandler().getTypeParameter(id);
    }

    protected UserDefinedConstraint getUdConstraint(ConjunctBuildingBlock current) 
    throws IllegalIdentifierException {
        return getUdConstraint(current.getIdentifier(), current.getType().isInfix());
    }
    protected UserDefinedConstraint getUdConstraint(String identifier, boolean infix)
    throws IllegalIdentifierException {
        UserDefinedConstraint result = getUdConstraintTable().get(identifier, infix);
        if (result == null)
            throw new IllegalIdentifierException(
                "No constraint with identifier %s has been defined by the user.",
                identifier
            );
        return result;
    }

    protected Set<IBuiltInConstraint<?>> getBiConstraints(Solver solver,
            String identifier, boolean asks, int arity, boolean infix)
            throws IllegalIdentifierException {
        Set<IBuiltInConstraint<?>> result = getBiConstraintTable().get(solver,
                identifier, asks, arity, infix);
        if (result == null)
            throw new IllegalIdentifierException(
                "No builtin constraint with identifier %s known.",
                BuiltInConstraintTable.createBiConstraintId(identifier, asks, arity)
            );
        return result;
    }

    protected Set<IBuiltInConstraint<?>> getBiConstraints(String identifier,
            boolean asks, int arity, boolean infix)
            throws IllegalIdentifierException {
        Set<IBuiltInConstraint<?>> result = getBiConstraintTable().get(identifier,
                asks, arity, infix);
        if (result == null)
            throw new IllegalIdentifierException(
                    "No builtin constraint with identifier "
                            + BuiltInConstraintTable.createBiConstraintId(
                                    identifier, asks, arity) + " known.");
        return result;
    }
    
    protected Occurrence getIdentifiedOccurrence(String id) throws IllegalIdentifierException {
        final Occurrence result = getIdentifiedOccurrences().get(id);
        if (result == null) 
            throw new IllegalIdentifierException("No occurrence was tagged with " + id);
        return result;
    }
    
    /*
     * version 1.0.3    (Peter Van Weert)
     *  - Assignments are treated differently: if it is a 
     *      tell-constraint with the right identifier, an assignment
     *      is always added to the result.
     *      See also changes in the built-in-constraint-table!
     */
    protected Set<IBuiltInConstraint<?>> getNoSolverConstraints(
        ConjunctBuildingBlock current, boolean asks, boolean infix
    ) throws IllegalIdentifierException {
        
        final String identifier = current.getIdentifier();
        final boolean canBeAssignment 
            = identifier.equals(Assignment.ID) && !asks; 
        final Set<IBuiltInConstraint<?>> result 
            = getBiConstraintTable().getNoSolverConstraints(identifier, asks, infix);
        
        if (canBeAssignment) {
            if (result == null) {            
                return new Singleton<IBuiltInConstraint<?>>(
                    new Assignment(current.getTypeAt(0))
                );
            }
            else    // result != null
                result.add(new Assignment(current.getTypeAt(0)));
        }
        
        // ! canBeAssignment
        if (result == null)
            throw new IllegalIdentifierException(
                "No java constraint with identifier "
                    + BuiltInConstraintTable.createBiConstraintId(identifier, asks, 2) 
                    + " known."
                );
        
        return result;
    }
    
    
    public DebugInfo getDebugInfo() {
        return debugInfo;
    }
    protected void setDebugInfo(DebugInfo debugInfo) {
		this.debugInfo = debugInfo;
	}

    protected interface ConjunctBuildingStrategy extends Terminatable {
        public void addFailureConjunct() throws BuilderException;
        public void addFailureConjunct(String message) throws BuilderException;
        public void addFieldAccessConjunct(String id) throws BuilderException;
        public void addVariableConjunct(String id) throws BuilderException;
        public void addSimpleIdConjunct(String id) throws BuilderException;
        public void addFlagConjunct(String id) throws BuilderException;
        public void beginSimpleIdConjunct(String id) throws BuilderException;
        public void beginBuiltInConstraint(String id) throws BuilderException;
        public void beginComposedIdConjunct(String id) throws BuilderException;
        public void beginMarkedBuiltInConjunct(String id) throws BuilderException;
        public void beginMethodInvocationConjunct(String id) throws BuilderException;
        public void beginConstructorInvocationConjunct(String id) throws BuilderException;
        public void beginUserDefinedConstraint(String id) throws BuilderException;
        public void beginArguments() throws BuilderException;
        public void addNullArgument() throws BuilderException;
        public void addPrimitiveArgument(boolean value) throws BuilderException;
        public void addPrimitiveArgument(byte value) throws BuilderException;
        public void addPrimitiveArgument(char value) throws BuilderException;
        public void addPrimitiveArgument(double value) throws BuilderException;
        public void addPrimitiveArgument(float value) throws BuilderException;
        public void addPrimitiveArgument(int value) throws BuilderException;
        public void addPrimitiveArgument(long value) throws BuilderException;
        public void addPrimitiveArgument(short value) throws BuilderException;
        public void addStringLiteralArgument(String value) throws BuilderException;
        public void addCharLiteralArgument(String value) throws BuilderException;
        public void addFieldAccessArgument(String value) throws BuilderException;
        public void addIdentifiedArgument(String id) throws BuilderException;
        public void beginMethodInvocationArgument(String id) throws BuilderException;
        public void endMethodInvocationArgument() throws BuilderException;
        public void beginConstructorInvocationArgument(String id) throws BuilderException;
        public void endConstructorInvocationArgument() throws BuilderException;
        public void endArguments() throws BuilderException;
        public void endArgumentedConjunct() throws BuilderException;
        public void endSimpleIdConjunct() throws BuilderException;
        public void endBuiltinConstraint() throws BuilderException;
        public void endUserDefinedConstraint() throws BuilderException;
        public void endComposedIdConjunct() throws BuilderException;
        public void endMarkedBuiltinConstraint() throws BuilderException;
        public void endMethodInvocationConjunct() throws BuilderException;
        public void endConstructorInvocationConjunct() throws BuilderException;
        public void beginInfixConstraint() throws BuilderException;
        public void buildInfix(String infix) throws BuilderException;
        public void buildBuiltInInfix(String infix) throws BuilderException;
        public void buildMarkedInfix(String infix) throws BuilderException;
        public void buildUserDefinedInfix(String infix) throws BuilderException;
        public void endInfixConstraint() throws BuilderException;
        public void beginDeclarationConjunct() throws BuilderException;
        public void buildDeclaredVariable(String id) throws BuilderException;
        public void endDeclarationConjunct() throws BuilderException;
    }
    
    protected abstract class StackBasedConjunctBuildingStrategy 
    implements ConjunctBuildingStrategy {
        private LinkedList<ConjunctBuildingBlock> buildingBlockStack;

        public StackBasedConjunctBuildingStrategy() {
            setBuildingBlockStack(new LinkedList<ConjunctBuildingBlock>());
        }

        protected LinkedList<ConjunctBuildingBlock> getBuildingBlockStack() {
            return buildingBlockStack;
        }

        protected void setBuildingBlockStack(LinkedList<ConjunctBuildingBlock> stack) {
            this.buildingBlockStack = stack;
        }

        protected void push(ConjunctBuildingBlock.Type type) {
            push(new ConjunctBuildingBlock(type));
        }

        protected void push(String id, ConjunctBuildingBlock.Type type) {
            push(new ConjunctBuildingBlock(id, type));
        }

        protected void push(ConjunctBuildingBlock buildingBlock) {
//            if (buildingBlock.isConstraint() != getBuildingBlockStack().isEmpty())
//                throw new IllegalStateException();
            getBuildingBlockStack().addFirst(buildingBlock);
        }

        protected ConjunctBuildingBlock pop() {
            try {
                return getBuildingBlockStack().removeFirst();
            } catch (NoSuchElementException nse) {
                throw new IllegalStateException("No current building block set");
            }
        }

        protected ConjunctBuildingBlock pop(ConjunctBuildingBlock.Type type) {
            final ConjunctBuildingBlock result = pop();
            if (result.getType() != type)
                throw new IllegalStateException(
                    "expected: %s <> found: %s", type, result.getType()
                );
            return result;
        }

        protected ConjunctBuildingBlock peek() throws IllegalStateException {
            try {
                return getBuildingBlockStack().peek();
            } catch (NoSuchElementException nse) {
                throw new IllegalStateException("No current building block set");
            }
        }

        protected ConjunctBuildingBlock peek(ConjunctBuildingBlock.Type type)
                throws IllegalStateException {
            final ConjunctBuildingBlock result = peek();
            if (result.getType() != type)
                throw new IllegalStateException(
                    "expected: %s <> found: %s", type, result.getType()
                );
            return result;
        }

        protected boolean buildingTopLevel() {
            return getBuildingBlockStack().size() == 1;
        }
        
        public void terminate() {
            buildingBlockStack = null;
        }
        public boolean isTerminated() {
            return (buildingBlockStack == null);
        }
    }

    protected abstract class BasicConjunctBuildingStrategy 
    extends StackBasedConjunctBuildingStrategy {
        
        public void addSimpleIdConjunct(String id) throws BuilderException {
            if (isAllowedVariable(id))
                addVariableConjunct(id);
            else
                addFlagConjunct(id);
        }
        
        public void addFlagConjunct(String id) throws BuilderException {
            endSimpleIdConjunct(new ConjunctBuildingBlock(id, SIMPLE_ID), false);
        }
        
        public void addFailureConjunct() throws BuilderException {
            addConjunct(Failure.getInstance());
        }
        public void addFailureConjunct(String message) throws BuilderException {
            addConjunct(Failure.getInstance(message));
        }

        public void beginSimpleIdConjunct(String id) throws BuilderException {
            try {
                Identifier.testSimpleIdentifier(id);
                push(id, SIMPLE_ID);

            } catch (IllegalIdentifierException iie) {
                throw new BuilderException(iie);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }

        public void beginBuiltInConstraint(String id) throws BuilderException {
            try {
                Identifier.testSimpleIdentifier(id);
                push(id, BUILT_IN);

            } catch (IllegalIdentifierException iie) {
                throw new BuilderException(iie);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }

        public void beginComposedIdConjunct(String id) throws BuilderException {
            try {
                Identifier.testComposedIdentifier(id);
                push(id, COMPOSED_ID);

            } catch (IllegalIdentifierException iie) {
                throw new BuilderException(iie);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }

        public void beginMarkedBuiltInConjunct(String id) throws BuilderException {
            try {
                Identifier.testComposedIdentifier(id);
                push(id, MARKED_BUILTIN);

            } catch (IllegalIdentifierException iie) {
                throw new BuilderException(iie);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }

        public void beginMethodInvocationConjunct(String id) throws BuilderException {
            try {
                Identifier.testComposedIdentifier(id);
                push(id, METHOD_INVOCATION_CON);

            } catch (IllegalIdentifierException iie) {
                throw new BuilderException(iie);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }
        
        public void beginConstructorInvocationConjunct(String id) throws BuilderException {
            try {
                Identifier.testIdentifier(id);
                push(id, CONSTRUCTOR_INVOCATION_CON);

            } catch (IllegalIdentifierException iie) {
                throw new BuilderException(iie);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }

        protected abstract boolean buildingAskConjunct();

        protected abstract void addConjunct(IConjunct conjunct) throws BuilderException;
        
        public void addIdentifiedArgument(String id) throws BuilderException {
            if (isAllowedVariable(id))
                addVariableArgument(getAllowedVariable(id));
            else
                addArgument(getArgument(id));
        }
        
        public void endBuiltinConstraint() throws BuilderException {
            addConjunct(getBuiltinConstraint());
        }

        public IBuiltInConjunct<?> getBuiltinConstraint(boolean allFixed) 
        throws BuilderException {
            try {
                return getBuiltInConstraint(pop(BUILT_IN), false, allFixed);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }

        public IBuiltInConjunct<?> getBuiltinConstraint() throws BuilderException {
            try {
                return getBuiltInConstraint(pop(BUILT_IN), false);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }
        
        protected void endBuiltInConstraintTry(
            ConjunctBuildingBlock current, boolean infix
        ) throws IllegalArgumentsException, IllegalIdentifierException, BuilderException {
            addConjunct(getBuiltInConstraintTry(current, infix, current.allFixed()));
        }

        protected void endBuiltInConstraint(
            ConjunctBuildingBlock current, boolean infix
        ) throws BuilderException {
            addConjunct(getBuiltInConstraint(current, infix));
        }

        protected IBuiltInConjunct<?> getBuiltInConstraint(
            ConjunctBuildingBlock current, boolean infix
        ) throws BuilderException {
            return getBuiltInConstraint(current, infix, current.allFixed());
        }
        
        protected IBuiltInConjunct<?> getBuiltInConstraintTry(ConjunctBuildingBlock current, boolean infix, boolean allFixed
        ) throws BuilderException, IllegalArgumentsException, IllegalIdentifierException {
            try {
                if (buildingAskConjunct() && allFixed)
                    return getNoSolverConstraintTry(current, infix);
                else
                    return getSolverConstraintTry(current, infix);

            } catch (AmbiguousArgumentsException aae) {
                throw new BuilderException(aae); // failure
            } catch (IllegalArgumentsException iae) {
                // NOP (try next)
            } catch (IllegalIdentifierException iie) {
                // NOP (try next)
            }

            try {
                if (!buildingAskConjunct() || !allFixed)
                    return getNoSolverConstraintTry(current, infix);
                else
                    return getSolverConstraintTry(current, infix);
            } catch (AmbiguousArgumentsException aae) {
                throw new BuilderException(aae); // failure
            }
        }
        
        /*
         * version 1.0.3    (Peter Van Weert)
         *  - Added buildingAskConjunct() to the test whether
         *      or not a Java-constraint should be tried first.
         */
        protected IBuiltInConjunct<?> getBuiltInConstraint(
            ConjunctBuildingBlock current, boolean infix, boolean allFixed
        ) throws BuilderException {
            try {
                return getBuiltInConstraintTry(current, infix, allFixed);

            } catch (IllegalArgumentsException iae) {
                throw new BuilderException(iae);
            } catch (IllegalIdentifierException iie) {
                throw new BuilderException(iie);
            }
        }

        protected void endSolverConstraintTry(
            ConjunctBuildingBlock current, boolean infix
        ) throws AmbiguousArgumentsException, IllegalArgumentsException, 
            BuilderException, IllegalIdentifierException {
            
            addConjunct(getSolverConstraintTry(current, infix));
        }

        protected IBuiltInConjunct<?> getSolverConstraintTry(
            ConjunctBuildingBlock current, boolean infix
        ) throws AmbiguousArgumentsException, IllegalArgumentsException,
            BuilderException, IllegalIdentifierException {
            
            try {
                IBuiltInConjunct<?> result = (IBuiltInConjunct<?>)
                    Matching.getBestMatch(
                        getBiConstraints(
                            current.getIdentifier(),
                            buildingAskConjunct(), 
                            current.getArity(),
                            infix), 
                        current.getArguments()
                    );
                return result;
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }

        protected IJavaConjunct<?> getNoSolverConstraintTry(
                ConjunctBuildingBlock current, boolean infix)
                throws BuilderException, IllegalArgumentsException,
                AmbiguousArgumentsException, IllegalIdentifierException {
            try {
                return (IJavaConjunct<?>) Matching.getBestMatch(
                    getNoSolverConstraints(current, buildingAskConjunct(), infix), 
                    current.getArguments()
                );
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }

        public void endComposedIdConjunct() throws BuilderException {
            try {
                final ConjunctBuildingBlock current = pop(COMPOSED_ID);
                if (Identifier.isSimple(
                    Identifier.getBody(current.getIdentifier())
                )) try {
                        endMarkedBuiltinConstraintTry(current, false);
                        return; // success
                    } catch (AmbiguousArgumentsException aae) {
                        throw new BuilderException(aae); // failure
                    } catch (IllegalArgumentsException iae) {
                        // NOP (try next)
                    } catch (IllegalIdentifierException iie) {
                        // NOP (try next)
                    }

                endMethodInvocationConjunct(current);

            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }

        public void endMarkedBuiltinConstraint() throws BuilderException {
            endMarkedBuiltinConstraint(pop(MARKED_BUILTIN), false);
        }

        protected void endMarkedBuiltinConstraint(
                ConjunctBuildingBlock current, boolean infix)
                throws BuilderException {
            try {
                endMarkedBuiltinConstraintTry(current, infix);

            } catch (IllegalIdentifierException iae) {
                throw new BuilderException(iae);
            } catch (IllegalArgumentsException iae) {
                throw new BuilderException(iae);
            } catch (AmbiguousArgumentsException aae) {
                throw new BuilderException(aae);
            }
        }

        protected void endMarkedBuiltinConstraintTry(
                ConjunctBuildingBlock current, boolean infix)
                throws IllegalArgumentsException, AmbiguousArgumentsException,
                IllegalIdentifierException, BuilderException {
            final String head = Identifier.getHead(current.getIdentifier()), tail = Identifier
                    .getTorso(current.getIdentifier());

            if (head.equals(IBuiltInConstraint.BUILTIN_MARK)) {
                current.setIdentifier(tail);
                endSolverConstraintTry(current, infix);
            } else {
                if (!isSolver(head)) {
                    throw new IllegalIdentifierException(current.getIdentifier());
                } else {
                    try {
                        addConjunct((IConjunct)Matching.getBestMatch(
                            getBiConstraints(
                                getSolver(head), 
                                current.getIdentifier(),
                                buildingAskConjunct(), 
                                current.getArity(), 
                                infix
                            ), 
                            current.getArguments()
                        ));
                    } catch (IllegalStateException ise) {
                        throw new BuilderException(ise);
                    }
                }
            }
        }
        
        public void endMethodInvocationConjunct() throws BuilderException {
            try {
                endMethodInvocationConjunct(pop(METHOD_INVOCATION_CON));
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }

        public void endConstructorInvocationConjunct() throws BuilderException {
            try {
                endConstructorInvocationConjunct(pop(CONSTRUCTOR_INVOCATION_CON));
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }

        protected void endMethodInvocationConjunct(ConjunctBuildingBlock current) 
        throws BuilderException {
            final AbstractMethodInvocation<?> invocation = endMethodInvocation(current);
            if (buildingAskConjunct() && !invocation.canBeAskConjunct())
                throw new BuilderException("Illegal ask constraint: " + invocation);
            addConjunct(invocation);
        }
        
        protected void endConstructorInvocationConjunct(ConjunctBuildingBlock current) 
        throws BuilderException {
            
            final ConstructorInvocation invocation = endConstructorInvocation(current);
            if (buildingAskConjunct() && !invocation.canBeAskConjunct())
                throw new BuilderException("Illegal ask constraint: " + invocation);
            addConjunct(invocation);
        }

        public void endInfixConstraint() throws BuilderException {
            final ConjunctBuildingBlock current = popInfixConstraint();
            switch (current.getType()) {
	            case INFIX:
	                endSimpleIdConjunct(current, true);
	                break;
	            case UD_INFIX:
	                endUserDefinedConstraint(current, true);
	                break;
	            case BI_INFIX:
	                endBuiltInConstraint(current, true);
	                break;
	            case MARKED_INFIX:
	                endMarkedBuiltinConstraint(current, true);
	                break;
	            default:
	                throw new IllegalStateException();
            }
        }

        public final void endSimpleIdConjunct() throws BuilderException {
            try {
                endSimpleIdConjunct(pop(SIMPLE_ID), false);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }

        public void endSimpleIdConjunct(ConjunctBuildingBlock current, boolean infix)
        throws BuilderException {
            try {
                endUserDefinedConstraintTry(current, infix);
                return; // success
            } catch (AmbiguousArgumentsException aae) {
                throw new BuilderException(aae); // failure
            } catch (IllegalArgumentsException iae) {
                // NOP (try next)
            } catch (IllegalIdentifierException iie) {
                // NOP (try next)
            }

            try {
                endBuiltInConstraintTry(current, infix);
                return; // success
            } catch (IllegalArgumentsException e) {
                // NOP (try next)
            } catch (IllegalIdentifierException e) {
                // NOP (try next)
            }
            
            endMethodInvocationConjunct(current);
        }
        
        public final void endUserDefinedConstraint() throws BuilderException {
            try {
                endUserDefinedConstraint(pop(USER_DEFINED), false);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }

        public void endUserDefinedConstraint(ConjunctBuildingBlock current, boolean infix)
        throws BuilderException {
            try {
                endUserDefinedConstraintTry(current, infix);
            } catch (AmbiguousArgumentsException aae) {
                throw new BuilderException(aae);
            } catch (IllegalArgumentsException iae) {
                throw new BuilderException(iae);
            } catch (IllegalIdentifierException iie) {
                throw new BuilderException(iie);
            }
        }

        protected void endUserDefinedConstraintTry(ConjunctBuildingBlock current, boolean infix)
        throws AmbiguousArgumentsException, IllegalArgumentsException,
            IllegalIdentifierException, BuilderException {

            final UserDefinedConstraint constraint = 
                getUdConstraint(current.getIdentifier(), infix);
            final IArguments arguments = current.getArguments();
            final MatchingInfos matchingInfos = constraint
                    .canHaveAsArguments(arguments);

            if (matchingInfos.isAmbiguous())
                throw new AmbiguousArgumentsException(constraint, arguments);
            if (!matchingInfos.isMatch())
                throw new IllegalArgumentsException(constraint, arguments);

            addUserDefinedConjunct((IConjunct) constraint.createInstance(matchingInfos, arguments));
        }
        
        protected abstract void addUserDefinedConjunct(IConjunct conjunct) throws BuilderException;

        public void beginUserDefinedConstraint(String id)
        throws BuilderException {
            try {
                if (!getUdConstraintTable().isDeclaredId(id, false))
                    throw new IllegalIdentifierException(id);
                push(id, USER_DEFINED);
        
            } catch (IllegalIdentifierException iie) {
                throw new BuilderException(iie);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }
        
        public void beginArguments() throws BuilderException {
            // NOP
        }
        
        protected void addArgument(IArgument argument) throws BuilderException {
            try {
                peek().addArgument(argument);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }
        
        public void addNullArgument() throws BuilderException {
            addArgument(NullArgument.getInstance());
        }
        
        public void addPrimitiveArgument(boolean value) throws BuilderException {
            addPrimitiveArgument(BooleanArgument.getInstance(value));
        }
        public void addPrimitiveArgument(byte value) throws BuilderException {
            addPrimitiveArgument(new ByteArgument(value));
        }
        public void addPrimitiveArgument(char value) throws BuilderException {
            addPrimitiveArgument(new CharArgument(value));
        }
        public void addCharLiteralArgument(String value) throws BuilderException {
            addPrimitiveArgument(new CharArgument(value));
        }
        public void addPrimitiveArgument(double value) throws BuilderException {
            addPrimitiveArgument(new DoubleArgument(value));
        }
        public void addPrimitiveArgument(float value) throws BuilderException {
            addPrimitiveArgument(new FloatArgument(value));
        }
        public void addPrimitiveArgument(int value) throws BuilderException {
            addPrimitiveArgument(new IntArgument(value));
        }
        public void addPrimitiveArgument(long value) throws BuilderException {
            addPrimitiveArgument(new LongArgument(value));
        }
        public void addPrimitiveArgument(short value) throws BuilderException {
            addPrimitiveArgument(new ShortArgument(value));
        }
        public void addStringLiteralArgument(String value) throws BuilderException {
            addPrimitiveArgument(new StringArgument(value));
        }
        
        protected void addPrimitiveArgument(LiteralArgument<?> constantArgument)
        throws BuilderException {
            addArgument(constantArgument);
        }
        
        public void addFieldAccessArgument(String value) throws BuilderException {
            addArgument(getFieldAccess(value));
        }
        
        protected abstract void addVariableArgument(IActualVariable var) throws BuilderException;
        
        public void beginMethodInvocationArgument(String id)
                throws BuilderException {
            try {
                Identifier.testIdentifier(id);
                push(id, METHOD_INVOCATION_ARG);
        
            } catch (IllegalIdentifierException iie) {
                throw new BuilderException(iie);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }
        
        public void endMethodInvocationArgument() throws BuilderException {
            final AbstractMethodInvocation<?> invocation = 
                endMethodInvocation(pop(METHOD_INVOCATION_ARG));
            if (invocation.canBeArgument())
                addArgument(invocation);
            else
                throw new BuilderException("Illegal argument: " + invocation);
        }
        
        public void beginConstructorInvocationArgument(String id)
                throws BuilderException {
            try {
                Identifier.testIdentifier(id);
                final Class<?> theClass = CHRIntermediateFormBuilder.this.getClass(id);
                final GenericType theType = GenericType.getInstance(theClass);
        
                if (theType.isParametrizable())
                    beginParametrizing(theType);
                else
                    beginNonParametrizable();
        
                push(id, CONSTRUCTOR_INVOCATION_ARG);
        
            } catch (IllegalIdentifierException iie) {
                throw new BuilderException(iie);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            } catch (ClassNotFoundException cnfe) {
                throw new BuilderException(cnfe);
            } catch (AmbiguousIdentifierException aie) {
                throw new BuilderException(aie);
            }
        }
        
        public void endConstructorInvocationArgument() throws BuilderException {
            addArgument(endConstructorInvocation(pop(CONSTRUCTOR_INVOCATION_ARG)));
        }
        
        protected AbstractMethodInvocation<?> endMethodInvocation(
                final ConjunctBuildingBlock current) throws BuilderException {

            IArguments arguments = current.getArguments();
            String id = current.getIdentifier();
            String methodName;
            Set<? extends AbstractMethod<?>> methods;
            
            if (Identifier.isComposed(id)) {
                IImplicitArgument implicitArgument = getArgument(Identifier.getBody(id));
                arguments.addImplicitArgument(implicitArgument);
                methodName = Identifier.getTail(id);
                methods = implicitArgument.getMethods(methodName);
            } else {
                methodName = id;
                methods = getMethodTable().getMethods(id);
            }
            
            try {
                return (AbstractMethodInvocation<?>)
                    Matching.getBestMatch(methods, arguments);
                
            } catch (AmbiguousArgumentsException aae) {
                throw new BuilderException("Unable to determine which method with name " + methodName + " was ment", aae);
            } catch (IllegalArgumentsException iae) {
                throw new BuilderException("No suited method found with name \"" + methodName + "\"", iae);
            } catch (IllegalArgumentException iae) {
                throw new BuilderException(iae);
            }
        }
        
        protected ConstructorInvocation endConstructorInvocation(
                final ConjunctBuildingBlock current) throws BuilderException {
        
            GenericType theType = null;
            
            try {
                if (isParametrizing())
                    theType = endParametrizing();
                else {
                    theType = GenericType.getInstance(
                        CHRIntermediateFormBuilder.this.getClass(current.getIdentifier())
                    );
                    endNonParametrizable();
                }
        
                return (ConstructorInvocation)Matching.getBestMatch(
                    util.Arrays.asSet(theType.getConstructors()), 
                    current.getArguments()
                );
                
            } catch (ClassNotFoundException cnfe) {
                throw new BuilderException(cnfe);
            } catch (AmbiguousIdentifierException aie) {
                throw new BuilderException(aie);
            } catch (AmbiguousArgumentsException ae) {
                throw new BuilderException("Unable to determine best suited constructor for type " + theType, ae);
            } catch (IllegalArgumentsException iae) {
                throw new BuilderException("No suitable constructor found for type " + theType, iae);
            } 
        }
        
        protected FieldAccess getFieldAccess(String id) throws BuilderException {
            try {
                final IImplicitArgument implicitArgument            
                    = getArgument(Identifier.getBody(id));
                final Field field 
                    = implicitArgument.getField(Identifier.getTail(id));
                
                return field.createInstance(new Arguments(implicitArgument));
                
            } catch (NoSuchFieldException nsfe) {
                throw new BuilderException(nsfe);
            } catch (AmbiguityException ae) {
                throw new BuilderException(ae);
            }
        }
        
        protected IImplicitArgument getArgument(String id) throws BuilderException {
            try {
                return getArgumentRec(id);
            } catch (IdentifierException ie) {
                throw new BuilderException(ie);
            }
        }
        
        private IImplicitArgument getArgumentRec(String id) 
        throws BuilderException, AmbiguousIdentifierException, IdentifierException {
            
            if (Identifier.isSimple(id)) {
            	if (isNamedVariable(id))
                    return getNamedVariable(id);
                else if (getFieldTable().isField(id))
                    return getFieldTable().getField(id).createStaticInstance();
                else if (getSolverTable().isDeclaredId(id))
                    return getSolverTable().get(id);                
				else if (isTypeParameter(id))
                    throw new BuilderException(
                        "Cannot use a type variable here: " + id
                    );
        
            } else { // Identifier.isComposed(id)
                try {
                    final IImplicitArgument implicitArgument = 
                        getArgumentRec(Identifier.getBody(id));
                    final Field field = 
                        implicitArgument.getField(Identifier.getTail(id));
                    final IArguments arguments = new Arguments(implicitArgument);
                    return (IImplicitArgument) field.createInstance(
                        field.canHaveAsArguments(arguments), arguments
                    );
        
                } catch (AmbiguityException ae) {
                    throw new BuilderException(ae);
                } catch (AmbiguousIdentifierException aie) {
                    throw new BuilderException(aie);
                } catch (NoSuchFieldException nsfe) {
                    // NOP (it might be a static field access)
                } catch (IdentifierException ie) {
                    // NOP (it might be a static field access)
                }
            }
            
            // fall-through (both simple and composed identifiers)
            try {
                // Accessing a static field through a class name implicit argument: 
                return new ClassNameImplicitArgument(
                    CHRIntermediateFormBuilder.this.getClass(id)
                );
            } catch (ClassNotFoundException cnfe) {
                throw new IdentifierException("Unknown identifier: " + id);
            }
        }
        
        public void endArguments() throws BuilderException {
            // NOP
        }
        
        public void endArgumentedConjunct() throws BuilderException {
            if (Identifier.isSimple(peek().getIdentifier()))
                endSimpleIdConjunct();
            else
                endComposedIdConjunct();
        }
        
        public void beginInfixConstraint() throws BuilderException {
            try {
                push(INFIX);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }
        
        public void buildInfix(String infix) throws BuilderException {
            if (Identifier.isComposed(infix)) {
                final String head = Identifier.getHead(infix);
                if (head.equals(IBuiltInConstraint.BUILTIN_MARK) || isSolver(infix)) {
                    buildMarkedInfix(infix);
                    return;
                }
            }
            peek(INFIX).setIdentifier(infix);
        }
        
        protected void buildInfix(String infix, ConjunctBuildingBlock.Type type)
                throws BuilderException {
            peek(INFIX).setIdentifier(infix);
            peek(INFIX).setType(type);
        }
        
        public void buildBuiltInInfix(String infix) throws BuilderException {
            buildInfix(infix, BI_INFIX);
        }
        
        public void buildMarkedInfix(String infix) throws BuilderException {
            buildInfix(infix, MARKED_INFIX);
        }
        
        public void buildUserDefinedInfix(String infix) throws BuilderException {
            buildInfix(infix, UD_INFIX);
        }
        
        protected ConjunctBuildingBlock popInfixConstraint()
                throws IllegalStateException {
            final ConjunctBuildingBlock current = pop();
        
            if (!current.hasIdentifier())
                throw new IllegalStateException(
                        "The identifier for the current constraint has not been set");
            if (current.getArity() != 2)
                throw new IllegalStateException(
                        "Illegal number of arguments for an infix constraint");
            if (!current.getType().isInfix())
                throw new IllegalStateException(
                        "Currently not building an infix constraint");
            return current;
        }

    }

    protected class BodyConjunctBuildingStrategy extends BasicConjunctBuildingStrategy {

        private Body body;
        
        public BodyConjunctBuildingStrategy(Body body) {
            setBody(body);
        }
        
        @Override
        protected boolean buildingAskConjunct() {
            return false;
        }

        @Override
        protected void addUserDefinedConjunct(IConjunct conjunct) throws BuilderException {
            addConjunct(conjunct);
        }
        
        @Override
        protected void addConjunct(IConjunct conjunct) throws BuilderException {
            try {
                boolean initialisation = false;
                
                if (hasVariablesToDeclare()) {
                    if (conjunct instanceof AssignmentConjunct) {
                        AssignmentConjunct assignment = (AssignmentConjunct)conjunct;
                        if (hasToDeclare(assignment.getArgumentAt(1)))
                            throw new BuilderException(
                                "Using unknown variable: " + assignment.getArgumentAt(1)
                            );
                        
                        // ==> hasToDeclare(conjunct.getArgumentAt(0)))
                        ((AssignmentConjunct) conjunct).setDeclarator();
                        addDeclaredVariable((Variable) assignment.getArgumentAt(0));
                        
                    } else if (conjunct instanceof SolverBuiltInConstraintInvocation) {
                        SolverBuiltInConstraintInvocation builtIn =
                            (SolverBuiltInConstraintInvocation) conjunct;
                        SolverBuiltInConstraint constraint =
                            builtIn.getArgumentable();
                        if (constraint.isEquality()) {
                            final IArgument
                                arg0 = builtIn.getExplicitArgumentAt(0), 
                                arg1 = builtIn.getExplicitArgumentAt(1);
                            final boolean 
                                dec0 = hasToDeclare(arg0),
                                dec1 = hasToDeclare(arg1);

                            if (dec0 && dec1) {
                                doDeclaration((Variable) arg1);
                                if (doInitialisation((Variable) arg0, arg1))
                                    initialisation = true;
                            } else if (dec0 && !dec1) {
                                if (doInitialisation((Variable) arg0, arg1))
                                    initialisation = true;
                                else
                                    doDeclaration((Variable) arg0);
                            } else if (!dec0 && dec1) {
                                if (doInitialisation((Variable) arg1, arg0))
                                    initialisation = true;
                                else
                                    doDeclaration((Variable) arg1);
                            }
                        }
                    } 
                    
                    for (Variable variable : getVariablesToDeclare())
                        doDeclaration(variable, false);
                    resetVariablesToDeclare();
                }

                
                if (!initialisation)
                    getBody().addConjunct(conjunct);

            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }
        
        public void addVariableConjunct(String id) throws BuilderException {
            throw new BuilderException("Illegal conjunct: " + id + " (variable in body)");
        }
        
        public void addFieldAccessConjunct(String id) throws BuilderException {
            throw new BuilderException(
                "You cannot use a field access as body-constraint!"
            );
        }
        
        @Override
        protected void addVariableArgument(IActualVariable var) throws BuilderException {
            ensureDeclared(var);
            addArgument(var);
        }
        
        public void beginDeclarationConjunct() throws BuilderException {
        	try {
                push(DECLARATION);
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }
        
        public void buildDeclaredVariable(String id) throws BuilderException {
        	try {
        		peek(DECLARATION).setIdentifier(id);
        	} catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }
        
        public void endDeclarationConjunct() throws BuilderException {
        	try {
        		ConjunctBuildingBlock current = pop(DECLARATION);
        		VariableType type = currentVariableType.get();
        		Variable variable = getVariableFactory().declareVariable(
    				current.getIdentifier(), type
				);
        		if (current.getArity() == 1)
        			doDeclaration(variable, false);
        		else {
        			AssignmentConjunct conjunct = 
        				new Assignment(type.getType()).createInstance(variable, current.getArgumentAt(0));
        			conjunct.setDeclarator();
        			getBody().addConjunct(conjunct);
        			addDeclaredVariable(variable);
        		}
        	} catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            } catch (DuplicateIdentifierException die) {
            	throw new BuilderException(die);
            } catch (IllegalIdentifierException iie) {
            	throw new BuilderException(iie);
			} catch (IdentifierException ie) {
				throw new BuilderException(ie);
			}
        }

        protected boolean isDeclared(Variable var) throws BuilderException {
            return isRuleVariable(var)
                || hasDeclared(var)
                || hasToDeclare(var);
        }

        private List<Variable> 
            variablesToDeclare = new ArrayList<Variable>(),
            declaredVariables = new ArrayList<Variable>();

        protected void doDeclaration(Variable var) throws BuilderException {
            doDeclaration(var, true);
        }
        
        protected void doDeclaration(Variable var, boolean register) throws BuilderException {
            if (! var.isFixed()) {
                try {
                    final IConjunct conjunct = var.getDeclaratorInstance();
                    if (conjunct == null)
                        throw new BuilderException("Cannot declare " + var);
                    getBody().addConjunct(conjunct);
                    addDeclaredVariable(var, register);
                } catch (IllegalStateException ise) {
                    throw new BuilderException(ise);
                } catch (AmbiguityException ae) {
                    throw new BuilderException(ae);
                }
            } else {
                throw new BuilderException(
                    "Using unknown \"fixed\" variable: " + var
                );
            }
        }

        protected boolean doInitialisation(Variable var, IArgument argument)
                throws BuilderException {
            try {
                final IConjunct conjunct = 
                    var.getInitialisingDeclaratorInstanceFrom(argument.getType());

                if (conjunct == null) return false;

                currentRule.get().getBody().addConjunct(conjunct);
                addDeclaredVariable(var);

                return true;

            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            } catch (AmbiguityException ae) {
                throw new BuilderException(ae);
            }
        }

        /**
         * Ensures the given variable will be declared. If we are building a
         * top-level argumentable (i.e. a conjunct), this means we will add
         * it to the "variables to be declared", which will be dealt with 
         * when this conjunct is finished. If not, we do declaration right
         * away.   
         */
        protected void ensureDeclared(IActualVariable variable) throws BuilderException {
            if (variable == NamelessVariable.getInstance()) return;
            if (! (variable instanceof Variable))
                throw new BuilderException("Unexpected variable type");
            Variable var = (Variable)variable;
            
            if (isDeclared(var)) return;
            
            if (!buildingTopLevel())
                doDeclaration(var);
            else
                addVariableToDeclare(var);
        }

        public void resetVariablesToDeclare() {
            getVariablesToDeclare().clear();
        }

        protected boolean hasVariablesToDeclare() {
            return !getVariablesToDeclare().isEmpty();
        }
        
        private boolean hasToDeclare(IArgument argument) {
            return (argument instanceof Variable) && hasToDeclare((Variable) argument);
        }

        protected boolean hasToDeclare(Variable var) {
            return getVariablesToDeclare().contains(var);
        }

        protected int getNbVariablesToDeclare() {
            return getVariablesToDeclare().size();
        }

        protected List<Variable> getVariablesToDeclare() {
            return variablesToDeclare;
        }

        protected void addVariableToDeclare(Variable var) {
            getVariablesToDeclare().add(var);
        }

        protected List<Variable> getDeclaredVariables() {
            return declaredVariables;
        }

        protected boolean hasDeclared(Variable variable) {
            return declaredVariables.contains(variable);
        }

        protected void addDeclaredVariable(Variable variable) {
            addDeclaredVariable(variable, true);
        }
        protected void addDeclaredVariable(Variable variable, boolean register) {
            getDeclaredVariables().add(variable);
            if (register && hasToDeclare(variable))
                getVariablesToDeclare().remove(variable);
        }

        protected void resetDeclaredVariables() {
            getDeclaredVariables().clear();
        }

        public Body getBody() {
            return body;
        }
        protected void setBody(Body body) {
            this.body = body;
        }
    }
    
    private GuardConjunctBuildingStrategy noGuardGCBS;
    protected GuardConjunctBuildingStrategy getNoGuardGCBS() {
        if (noGuardGCBS == null)
            noGuardGCBS = new GuardConjunctBuildingStrategy();            
        return noGuardGCBS;
    }
    
    final static BuilderException NO_DEEP_GUARDS =
        new BuilderException("In a guard no user-defined constraints (deep guards) are allowed");
    protected class GuardConjunctBuildingStrategy extends BasicConjunctBuildingStrategy {
        
        private Guard guard;
        
        GuardConjunctBuildingStrategy() {
            // NOP
        }
        
        public GuardConjunctBuildingStrategy(Guard guard) {
            setGuard(guard);
        }
        
        @Override
        protected boolean buildingAskConjunct() {
            return true;
        }
        
        @Override
        protected void addConjunct(IConjunct conjunct) throws BuilderException {
            try {
                final IGuardConjunct guard = (IGuardConjunct) conjunct;
                getGuard().addConjunct(guard);
                
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            } catch (ClassCastException cce) {
                throw new BuilderException(cce);
            } catch (IllegalArgumentException iae) {
                throw new BuilderException(iae);
            }
        }
        
        public void addVariableConjunct(String id) throws BuilderException {
            addConjunct(getNamedVariable(id));
        }

        public void addFieldAccessConjunct(String id) throws BuilderException {
            try {
                final IImplicitArgument implicitArgument = 
                    getArgument(Identifier.getBody(id));
                final Field field = 
                    implicitArgument.getField(Identifier.getTail(id));
                addConjunct(field.createInstance(new Arguments(implicitArgument)));
                
            } catch (IllegalArgumentException iae) {
                throw new BuilderException(iae);
            } catch (AmbiguityException ae) {
                throw new BuilderException(ae);
            } catch (NoSuchFieldException nsfe) {
                throw new BuilderException(nsfe);
            }
        }

        @Override
        protected void addVariableArgument(IActualVariable var) throws BuilderException {
            addArgument(var);
        }

        @Override
        public void beginUserDefinedConstraint(String id) throws BuilderException {
            throw NO_DEEP_GUARDS;
        }
        
        @Override
        protected void addUserDefinedConjunct(IConjunct conjunct) throws BuilderException {
            throw NO_DEEP_GUARDS;
        }
        
        @Override
        public void endUserDefinedConstraint(ConjunctBuildingBlock current, boolean infix) 
        throws BuilderException {
            throw NO_DEEP_GUARDS;
        }

        @Override
        public void buildUserDefinedInfix(String infix) throws BuilderException {
            throw NO_DEEP_GUARDS;
        }
        
        public void beginDeclarationConjunct() throws BuilderException {
        	throw new BuilderException("No variable declarations allowed in guards");
        }
        public void buildDeclaredVariable(String id) throws BuilderException {
        	throw new BuilderException("No variable declarations allowed in guards");
        }
        public void endDeclarationConjunct() throws BuilderException {
        	throw new BuilderException("No variable declarations allowed in guards");
        }

        public Guard getGuard() {
            return guard;
        }
        protected void setGuard(Guard guard) {
            this.guard = guard;
        }
    }
    
    protected class ImplicitGuardConjunctBuildingStrategy extends GuardConjunctBuildingStrategy {

        /**
         * The <code>OccurrenceBuildingStrategy</code> that has to
         * be restored when the implicit guard is finished (it will
         * continue building the occurrence). 
         */
        private OccurrenceBuildingStrategy toRestore;
        
        /**
         * An <code>ImplicitGuardConjunctBuildingStrategy</code> is
         * responsible for constructing implicit equality guards
         * for a given occurrence. It will of course be a built-in
         * equality guard we are building, of which the first 
         * argument has already been set (an implicit variable):
         * this strategy will construct the other. When this is
         * done it will give control back to the given 
         * <code>OccurrenceBuildingStrategy</code>.
         * 
         * @param guard
         *  The (implicit) guard we will be completing.
         * @param implicitVariable
         *  The implicit variable (the first argument of the equality conjunct
         *  we are constructing)
         * @param toRestore
         *  The <code>OccurrenceBuildingStrategy</code> to restore
         *  once finished.
         */
        public ImplicitGuardConjunctBuildingStrategy(
            Guard guard, IActualVariable implicitVariable, OccurrenceBuildingStrategy toRestore
        ) throws BuilderException {
            super(guard);
            setToRestore(toRestore);

            // start building an equality guard:
            beginBuiltInConstraint(EQ);
            // with as the first argument the given (implicit) variable:
            addArgument(implicitVariable);
        }
        
        /* * * * * * * * * * * * * * * * * * * * * *
         * RESTORING OCCURRENCE BUILDING STRATEGY  *
         * * * * * * * * * * * * * * * * * * * * * */
        
        protected OccurrenceBuildingStrategy getToRestore() {
            return toRestore;
        }
        protected void setToRestore(OccurrenceBuildingStrategy toRestore) {
            this.toRestore = toRestore;
        }
        
        @Override
        public void endConstructorInvocationArgument() throws BuilderException {
            super.endConstructorInvocationArgument();
            if (buildingTopLevel()) endImplicitGuard();
        }
        @Override
        public void endMethodInvocationArgument() throws BuilderException {
            super.endMethodInvocationArgument();
            if (buildingTopLevel()) endImplicitGuard();
        }
        
        @Override
        protected void addPrimitiveArgument(LiteralArgument<?> arg) throws BuilderException {
            super.addPrimitiveArgument(arg);
            if (buildingTopLevel()) endImplicitGuard();
        }
        @Override
        public void addNullArgument() throws BuilderException {
            super.addNullArgument();
            if (buildingTopLevel()) endImplicitGuard();
        }
        
        @Override
        public void addFieldAccessArgument(String arg) throws BuilderException {
            super.addFieldAccessArgument(arg);
            if (buildingTopLevel()) endImplicitGuard();
        }
        @Override
        public void addIdentifiedArgument(String arg) throws BuilderException {
            super.addIdentifiedArgument(arg);
            if (buildingTopLevel()) endImplicitGuard();
        }
        @Override
        protected void addVariableArgument(IActualVariable var) throws BuilderException {
            super.addVariableArgument(var);
            if (buildingTopLevel()) endImplicitGuard();
        }
        
        protected void endImplicitGuard() throws BuilderException {
            endArguments();
            endBuiltinConstraint();
            getToRestore().endImplicitGuard();
        }
    }

    protected class OccurrenceBuildingStrategy implements ConjunctBuildingStrategy {
        /**
         * The occurrence that was constructed. Unlike in guards and bodies,
         * we know in advance the formal types of the arguments and we
         * can construct a occurrence directly. This field will also be used for
         * in-head pragma's.
         */
        public Current<Occurrence> currentOccurrence = new Current<Occurrence>();
        
        /**
         * The head we are constructing new occurrences for.
         */ 
        private Head head;
        
        /**
         * The type of occurrences we are currently constructing.
         */
        public Current<OccurrenceType> currentType = new Current<OccurrenceType>();
        
        public OccurrenceBuildingStrategy(Head head) {
            setHead(head);
        }
        
        public OccurrenceBuildingStrategy(Head head, OccurrenceType type) {
            this(head);
            currentType.set(type);
        }
        
        /* * * * * * * * * *\
         * SETTERS/GETTERS *
        \* * * * * * * * * */
                
        public Head getHead() {
            return head;
        }
        protected void setHead(Head head) {
            this.head = head;
        }
        
        public boolean buildingNegativehead() {
            return getHead().isNegative();
        }
        public int getHeadNbr() {
            return getHead().getNbr();
        }
        
        /* * * * * * * * * * *\
         * FLAG CONSTRAINTS  *
        \* * * * * * * * * * */
        
        public void addSimpleIdConjunct(String id) throws BuilderException {
            addFlagConjunct(id);
        }
        
        public void addFlagConjunct(String id) throws BuilderException {
            beginUserDefinedConstraint(id);
            beginArguments();
            endArguments();
            endUserDefinedConstraint();
        }
        
        /* * * * * * *\
         * BEGINNERS *
         * * * * * * */
        
        public void beginSimpleIdConjunct(String id) throws BuilderException {
            beginUserDefinedConstraint(id);
        }
        public void beginUserDefinedConstraint(String id) throws BuilderException {
            try {
                currentOccurrence.reset();
                currentOccurrence.set(
                    getUdConstraint(id, false).createOccurrence(getHead(), currentType.get())
                );
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            } catch (IllegalIdentifierException iie) {
                throw new BuilderException(iie);
            }
        }
        
        public void beginInfixConstraint() throws BuilderException {
            try {
                currentOccurrence.reset();
                currentOccurrence.set(
                    getUdConstraint(getInfixId(), true).
                        createOccurrence(getHead(), currentType.get())
                );
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            } catch (IllegalIdentifierException iie) {
                throw new BuilderException(iie);
            }
        }
        public void buildInfix(String infix) throws BuilderException {
            buildUserDefinedInfix(infix);
        }
        public void buildUserDefinedInfix(String id) throws BuilderException {
            try {
                if (! currentOccurrence.get().hasAsInfix(id)) {
                    Occurrence oldOccurrence = currentOccurrence.get();
                    UserDefinedConstraint newConstraint = getUdConstraint(id, true);
                    Occurrence newOccurrence = newConstraint.createOccurrence(getHead(), currentType.get());
                    newOccurrence.addArgument(oldOccurrence.getArgumentAt(0));
                    
                    oldOccurrence.terminate();
                    currentOccurrence.reset();
                    currentOccurrence.set(newOccurrence);
                }
                // XXX the above code is just a temporary patch, normally we would do:
//                    throw new BuilderException("Illegal identifier: %s != %s",
//                        id,
//                        currentOccurrence.get().getInfix()
//                    );
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            } catch (IllegalIdentifierException iie) {
                throw new BuilderException(iie);
            }
        }
        
        public void beginArguments() throws BuilderException {
            // NOP
        }
        
        /* *  * * *\
         * ENDERS *
         * * *  * */
        public void endSimpleIdConjunct() throws BuilderException {
            endUserDefinedConstraint();
        }
        public void endUserDefinedConstraint() throws BuilderException {
            try {
                if (! currentOccurrence.get().isValid())
                    throw new BuilderException(
                        "Invalid occurrence: " + currentOccurrence.get()
                    );
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }
        
        public void endInfixConstraint() throws BuilderException {
            endUserDefinedConstraint();
        }
        public void endArgumentedConjunct() throws BuilderException {
            endUserDefinedConstraint();
        }
        
        public void endArguments() throws BuilderException {
            // NOP
        }
        
        
        /* * * *  * * * * * * *\
         * VARIABLE ARGUMENTS *
        \* * * * *  * * * * * */
        
        public void addIdentifiedArgument(String id) throws BuilderException {
            if (AbstractVariable.isValidVariableIdentifier(id)) {
                // It could be a variable, but we give preference to final fields
                // (imported statically or a handler member field). Non-final fields
                // starting with an uppercase will have to be accessed using their
                // implicit argument (this.FIELDNAME or ClassName.FieLdnAME). Note
                // that these would be against Java naming conventions. Note also
                // that this exception will allow enums to work as well.
                try {
                    if (isAllowedVariable(id) || !isFinalField(id)) {
                        addVariableArgument(getVariable(id, getCurrentFormalVariable()));
                        return;
                    }
                } catch (AmbiguousIdentifierException aie) {
                    throw new BuilderException(aie);
                }
            }
        
            /* ... or something that is not a variable (like e.g. a final field) */
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.addIdentifiedArgument(id);
        }
        
        protected void addVariableArgument(IActualVariable variable) throws BuilderException {
            try {
                Occurrence occurrence = currentOccurrence.get();
                occurrence.addArgument(variable);
                
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }
        
        protected FormalVariable getCurrentFormalVariable() throws BuilderException {
            try {
                final Occurrence current = currentOccurrence.get();
                return current.getFormalVariableAt(current.getArity());
                
            } catch (IllegalStateException ise) {
                throw new BuilderException(ise);
            }
        }
        
        /* * * * * * * * * * * * * * * * * * *\
         * OTHER ARGUMENTS (implicit guards) *
        \* * * * * * * * * * * * * * * * * * */
        
        protected void beginExplicitizedGuard() throws BuilderException {
            IActualVariable implicitVariable = 
                createImplicitVariable(getCurrentFormalVariable());
            
            // add the new (implicit) variable to the current occurrence:
            addVariableArgument(implicitVariable);

            // start building the explicitized guard:
            currentCBS.reset();
            currentCBS.set(new ImplicitGuardConjunctBuildingStrategy(
                getHead().getGuard(), implicitVariable, this
            ));
        }
        
        protected void endImplicitGuard() throws BuilderException {
            // the implicit guard is finished: return the control to us:
            currentCBS.reset();
            currentCBS.set(this);
        }
        
        public void addNullArgument() throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.addNullArgument();
        }
        public void addPrimitiveArgument(boolean value) throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.addPrimitiveArgument(value);
        }
        public void addPrimitiveArgument(byte value) throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.addPrimitiveArgument(value);
        }
        public void addPrimitiveArgument(char value) throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.addPrimitiveArgument(value);
        }
        public void addCharLiteralArgument(String value) throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.addCharLiteralArgument(value);
        }
        public void addPrimitiveArgument(double value) throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.addPrimitiveArgument(value);
        }
        public void addPrimitiveArgument(float value) throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.addPrimitiveArgument(value);
        }
        public void addPrimitiveArgument(int value) throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.addPrimitiveArgument(value);
        }
        public void addPrimitiveArgument(long value) throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.addPrimitiveArgument(value);
        }
        public void addPrimitiveArgument(short value) throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.addPrimitiveArgument(value);
        }
        public void addStringLiteralArgument(String value) throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.addStringLiteralArgument(value);
        }
        
        public void addFieldAccessArgument(String value) throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.addFieldAccessArgument(value);
        }

        public void beginConstructorInvocationArgument(String arg) throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.beginConstructorInvocationArgument(arg);
        }
        public void beginMethodInvocationArgument(String arg) throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.beginMethodInvocationArgument(arg);
        }
        
        public void endConstructorInvocationArgument() throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.endConstructorInvocationArgument();
        }
        public void endMethodInvocationArgument() throws BuilderException {
            beginExplicitizedGuard();
            CHRIntermediateFormBuilder.this.endMethodInvocationArgument();
        }
        
        /* * * * * * * * * *\
         * OTHER CONJUNCTS *
        \* * * * * * * * * */
        
        private final String UNSUPPORTED = "Only user defined constraints allowed in head";
        
        public void addFailureConjunct() throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }
        public void addFailureConjunct(String message) throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }
        public void addVariableConjunct(String arg0) throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }
        public void addFieldAccessConjunct(String id) throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }
        public void beginComposedIdConjunct(String id) throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }
        public void endComposedIdConjunct() throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }
        public void beginMarkedBuiltInConjunct(String id) throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }
        public void endMarkedBuiltinConstraint() throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }
        public void beginBuiltInConstraint(String id) throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }
        public void endBuiltinConstraint() throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }
        public void beginMethodInvocationConjunct(String id) throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }
        public void beginConstructorInvocationConjunct(String id) throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }
        public void endMethodInvocationConjunct() throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }
        public void beginDeclarationConjunct() throws BuilderException {
        	throw new BuilderException(UNSUPPORTED);
        }
        public void buildDeclaredVariable(String id) throws BuilderException {
        	throw new BuilderException(UNSUPPORTED);
        }
        public void endDeclarationConjunct() throws BuilderException {
        	throw new BuilderException(UNSUPPORTED);
        }
        public void endConstructorInvocationConjunct() throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }
        public void buildBuiltInInfix(String infix) throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }
        public void buildMarkedInfix(String infix) throws BuilderException {
            throw new BuilderException(UNSUPPORTED);
        }

        public boolean isTerminated() {
            return (currentOccurrence == null);
        }
        public void terminate() {
            if (isTerminated()) return;
            
            if (! getHead().isValid())
                throw new IllegalStateException("Illegal head: " + getHead());
            
            currentOccurrence = null;
        }
    }
    
    protected static class ConjunctBuildingBlock extends ArgumentsDecorator {
        public static enum Type {
            BUILT_IN, MARKED_BUILTIN, 
            USER_DEFINED, 
            METHOD_INVOCATION_CON(false, false), 
            METHOD_INVOCATION_ARG(false, false),
            CONSTRUCTOR_INVOCATION_CON(false, false), 
            CONSTRUCTOR_INVOCATION_ARG(false, false), 
            COMPOSED_ID, SIMPLE_ID, 
            DECLARATION,
            INFIX(true), UD_INFIX(true), BI_INFIX(true), MARKED_INFIX(true);

            private boolean constraint;

            private boolean infix;

            private Type() {
                this(false);
            }

            private Type(boolean infix) {
                this(infix, true);
            }

            private Type(boolean infix, boolean constraint) {
                setInfix(infix);
                setConstraint(constraint);
            }

            protected void setConstraint(boolean constraint) {
                this.constraint = constraint;
            }

            public boolean isConstraint() {
                return constraint;
            }

            public boolean isInfix() {
                return infix;
            }

            protected void setInfix(boolean infix) {
                this.infix = infix;
            }
        }

        private Type type;

        private String identifier;

        public ConjunctBuildingBlock(Type type) {
            setArguments(new Arguments());
            setType(type);
        }

        public ConjunctBuildingBlock(String id, Type type) {
            this(type);
            setIdentifier(id);
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public boolean hasIdentifier() {
            return getIdentifier() != null;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public boolean isConstraint() {
            return getType().isConstraint();
        }
    }
}