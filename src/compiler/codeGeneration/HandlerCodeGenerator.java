package compiler.codeGeneration;

import static compiler.CHRIntermediateForm.debug.DebugLevel.FULL;
import static compiler.CHRIntermediateForm.modifiers.Modifier.isDefaultAccess;
import static compiler.CHRIntermediateForm.modifiers.Modifier.isPrivate;
import static compiler.CHRIntermediateForm.modifiers.Modifier.isProtected;
import static compiler.CHRIntermediateForm.modifiers.Modifier.isPublic;
import static compiler.codeGeneration.AbstractHashIndexCodeGenerator.getHashIndexName;
import static compiler.codeGeneration.ConstraintCodeGenerator.getConstraintTypeName;
import static compiler.codeGeneration.HandlerCodeGenerator.XXX.DEBUG;
import static compiler.codeGeneration.HandlerCodeGenerator.XXX.PACKAGE;
import static compiler.codeGeneration.HandlerCodeGenerator.XXX.PROTECTED;
import static util.StringUtils.capFirst;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import runtime.Constraint;
import runtime.ConstraintIterable;
import runtime.ConstraintSystem;
import runtime.ContinuationStack;
import runtime.IConstraint;
import runtime.debug.Tracer;
import runtime.hash.FDSSHashIndex;
import runtime.hash.HashIndex;
import util.Cloneable;
import util.collections.AbstractUnmodifiableCollection;
import util.collections.Empty;
import util.iterator.ChainingIterator;
import util.iterator.ConvertingIterator;
import util.iterator.EmptyIterator;
import util.iterator.Filtered;
import util.iterator.FilteredIterable;
import util.iterator.FilteredIterator;
import util.iterator.IteratorUtilities;
import util.iterator.NestedIterable;
import util.iterator.NestedIterator;
import util.iterator.SingletonIterator;
import util.iterator.ConvertingIterator.Convertor;
import util.iterator.Filtered.Filter;
import annotations.JCHR_Constraint;
import annotations.JCHR_Constraints;
import annotations.JCHR_Tells;
import annotations.JCHR_Constraint.Value;

import compiler.CHRIntermediateForm.Handler;
import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.Lookup;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategories;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.NeverStoredLookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;
import compiler.CHRIntermediateForm.id.Identifier;
import compiler.CHRIntermediateForm.solver.Solver;
import compiler.CHRIntermediateForm.types.TypeParameter;
import compiler.options.Options;

public class HandlerCodeGenerator extends CIFJavaCodeGenerator {

    private Set<Integer> usedTupleArities, usedPushes;
    
    public static boolean LINKED_LIST = true;
    
    public HandlerCodeGenerator(ICHRIntermediateForm cif, Options options, BufferedWriter out) {
		super(cif, options, out);
	}

	public HandlerCodeGenerator(ICHRIntermediateForm cif, Options options, CodeGenerator codeGenerator) {
		super(cif, options, codeGenerator);
	}

	public HandlerCodeGenerator(ICHRIntermediateForm cif, Options options, Writer out) {
		super(cif, options, out);
	}
    
    @Override
	protected void init() {
        setUsedTupleArities(new HashSet<Integer>(4));
        setUsedPushes(new HashSet<Integer>(4));
    }
    
    protected Set<Integer> getUsedTupleArities() {
        return usedTupleArities;
    }
    protected void setUsedTupleArities(Set<Integer> usedTupleArities) {
        this.usedTupleArities = usedTupleArities;
    }
    
    protected Set<Integer> getUsedPushes() {
		return usedPushes;
	}
    protected void setUsedPushes(Set<Integer> usedPushes) {
		this.usedPushes = usedPushes;
	}

	@Override
    protected void doGenerate() throws GenerationException {
        generateHeader();
        nl();
        generatePackageDeclaration();
        nl();
        generateImports();
        nl();
        generateAnnotations();
        generateHandlerClass();
    }
    
    protected void generateHeader() throws GenerationException {
        new HeaderCodeGenerator(this).generate();
    }
    
    protected void generatePackageDeclaration() throws GenerationException {
        print("package ");
    	printcln(getHandler().getPackageName());
    }
    
    protected void generateImports() throws GenerationException {
        printImport(runtime.Handler.class);
        printImport(IConstraint.class);
        printImport(Constraint.class);
        println();
        printImport(HashIndex.class);
        printImport(FDSSHashIndex.class);
        if (hasToTrace()) {
            println();
            printImport(Tracer.class);
        }
        println();
        printImport(JCHR_Constraints.class);
        printImport(JCHR_Constraint.class);
        printImport(JCHR_Tells.class);
        println();
        printImport(Cloneable.class);
        printImport(ConstraintIterable.class);
        printImport(NestedIterator.class);
        printImport(NestedIterable.class);
        printImport(SingletonIterator.class);
        printImport(FilteredIterable.class);
        printImport(FilteredIterator.class);
        printImport(Filtered.Filter.class);
        printImport(EmptyIterator.class);
        printImport(Empty.class);
        printImport(AbstractUnmodifiableCollection.class);
        println();
        printImport(Collection.class);
        printImport(Iterator.class);
    }
    
    /*
        Iterator<XX> xxs = ;
        XX xx;
        if (xxs.hasNext()) do {
            xx = xxs.next();
            
            if (! xxs.hasNext()) break;
            print(", ");
        } while (true);
     */
    
    protected void generateAnnotations() throws GenerationException {
    	generateGeneratedAnnotation();
    	generateConstraintAnnotations();
    	generateSuppressWarningsAnnotation();
    }
    
    protected void generateSuppressWarningsAnnotation() throws GenerationException {
    	println("@SuppressWarnings(\"unused\")	// eclipse-specific tag?");
    }
    
	protected void generateConstraintAnnotations() throws GenerationException {
        tprintAnnotationStart(JCHR_Constraints.class); 
        println('{'); incNbTabs();
        
        Iterator<UserDefinedConstraint> constraints = 
        	getDebugInfo().getDebugLevel() == FULL
        		? getUserDefinedConstraints().iterator() 
    			: getAccessibleConstraints().iterator();

        UserDefinedConstraint constraint;
        if (constraints.hasNext()) do {
            constraint = constraints.next();
            tprintAnnotationStart(JCHR_Constraint.class);
            nl();
            incNbTabs();
                tprint("identifier = "); 
                printLiteral(constraint.getIdentifier()); 
                println(',');
                tprint("arity = ");      
                print(constraint.getArity());
                if (constraint.hasInfixIdentifiers()) {
                	println(',');
                    tprint("infix = ");
                    String[] infixes = constraint.getInfixIdentifiers();
                    if (infixes.length > 1) print('{');
                    printLiteral(infixes[0]);
                    for (int i = 1; i < infixes.length; i++) {
                    	print(", ");
                    	printLiteral(infixes[1]);                    	
                    }
                    if (infixes.length > 1) print('}');
                }
                if (constraint.isIdempotent()) {
                	println(',');
                	tprint("idempotent = ");
                	print(JCHR_Constraint.class.getSimpleName());
                	print('.');
                	print(Value.class.getSimpleName());
                	print('.');
                	print(Value.YES.toString());
                }
            nl();
            decNbTabs();
            tprint(')');
            if (! constraints.hasNext()) { println(); break; }
            println(',');
        } while (true);
        
        decNbTabs();
        tprintln("})");
    }
    
    /*
     * The code generated using this method will remain correct after 
     * refactoring. Also, it reads better then the four statements needed...
     */
    private void tprintAnnotationStart(Class<? extends Annotation> annotation) throws GenerationException {
        printTabs(); 
        print('@');
        print(annotation.getSimpleName());
        print('(');
    }
    
    protected void generateHandlerClass() throws GenerationException {
    	printAccessModifier(getHandler());
    	print("class ");
    	printFullHandlerType();
        println(" extends Handler {");
        incNbTabs();
        
        generateConstructorsAndFields();
        nl();
        generateIdentifierGetter(); 
        nl();
        generateConstraintClassesMethod(getAccessibleConstraints());
        nl();
        generateLookupMethod("iterator", getAccessibleConstraints());
        nl();
		generateLookupMethod("lookup", getAccessibleConstraints());
        nl();
        generateAccessibilityMethods();
        nl();
        generateTellMethods();
        nl();
        generateConstraintStoreCode();
        nl();
        generateConstraintClasses();
        nl();
        generateIsStoredMethod();
                
        decNbTabs();
        println('}');
    }
    
    public void printHandlerType() throws GenerationException {
    	printType(getHandlerTypeName(), getHandler().getTypeParameters());
    }
    public void printFullHandlerType() throws GenerationException {
    	printFullType(getHandlerTypeName(), getHandler().getTypeParameters());
    }
    
    public String getFullHandlerType() throws GenerationException {
    	return getHandlerType(getHandler(), true);
    }
    public String getHandlerType() throws GenerationException {
    	return getHandlerType(getHandler(), false);
    }
    public static String getHandlerType(Handler handler) {
    	return getHandlerType(handler, false);
    }
    public static String getFullHandlerType(Handler handler) {
    	return getHandlerType(handler, true);
    }
    private static String getHandlerType(Handler handler, boolean full) {
    	StringBuilder result = new StringBuilder();
    	result.append(getHandlerTypeName(handler));
        Iterator<TypeParameter> iter = handler.getTypeParameters().iterator();
        if (iter.hasNext()) {
        	result.append('<');
        	do {
        		result.append(full
					? iter.next().toFullTypeString()
					: iter.next().toTypeString()
				);
        		if (iter.hasNext())
        			result.append(", ");
        		else
        			break;
        	} while (true);
        	result.append('>');
        }
    	return result.toString();
    }
    
    public String getHandlerTypeName() {
    	return getHandlerTypeName(getHandler());
    }
    public static String getHandlerTypeName(Handler handler) {
    	return Identifier.makeJavaLike(handler.getIdentifier()).concat("Handler");
    }
    
    protected boolean hasSolvers() {
    	return getNbSolvers() > 0;
    }
    
    protected void generateConstructorsAndFields() throws GenerationException {
    	printConstraintSystemCode();
    	nl();
    	if (hasSolvers()) {
			generateSolverCode();
			nl();
		}
    	generateConstructor(false, false);
		nl();
		generateConstructorSignature(true, false);
		
		if (hasToTrace()) {
			generateConstructorInvocation(true, false);
			nl();
			printTracerCode();
			nl();
			generateConstructor(false, true);
			nl();
			generateConstructorSignature(true, true);
			tprintln("super($$constraintSystem);");
			tprintln("this.tracer = $$tracer;");
		} else {
			tprintln("super($$constraintSystem);");
		}
		
		if (getOptions().doStackOptimizations())
			tprintln("$$continuationStack = getContinuationStack();");
		for (Solver solver : getSolvers()) {
			tprint("this."); 
			print(solver.getIdentifier());
			print(" = ");
			printcln(solver.getIdentifier());
		}
		generateConstraintStoreInitialisationCode();
		closeAccolade();
    }
    
    protected void generateConstraintStoreInitialisationCode() throws GenerationException {
    	for (UserDefinedConstraint constraint : getUserDefinedConstraints())
            for (ILookupCategory category : constraint.getLookupCategories())
                ConstraintStoreCodeGeneratorFactory
                    .getInstance(this, constraint, category)
                    .generateInitialisationCode();
    }
    
    protected void generateConstructor(
		boolean includeConstraintSystem, boolean includeTracer
	) throws GenerationException {
    	generateConstructorSignature(includeConstraintSystem, includeTracer);
    	generateConstructorInvocation(includeConstraintSystem, includeTracer);
    }
    	
    protected void generateConstructorSignature(
		boolean includeConstraintSystem, boolean includeTracer
	) throws GenerationException {
    	tprint("public ");
		print(getHandlerTypeName());
		print('(');
		if (hasSolvers()) {
			printSolverList(true);
			if (includeConstraintSystem || includeTracer) print(", ");
		}
		if (includeConstraintSystem) {
			print(ConstraintSystem.class.getCanonicalName());
			print(" $$constraintSystem");
			if (includeTracer) print(", ");
		}
		if (includeTracer) {
			print("Tracer $$tracer");
		}
		println(") {");
		incNbTabs();
    }
    
	protected void generateConstructorInvocation(
		boolean includeConstraintSystem, boolean includeTracer
	) throws GenerationException {
		generateConstructorInvocation(
			includeConstraintSystem
				? "$$constraintSystem" 
				: ConstraintSystem.class.getCanonicalName() + ".get()", 
			includeTracer? "$$tracer" : "null"
		);
    }

    protected void generateConstructorInvocation(String stackDefault, String tracerDefault) throws GenerationException {
    	tprint("this("); 
		if (hasSolvers()) {
			printSolverList(false);
			print(", ");
		}
		print(stackDefault);
		if (hasToTrace()) {
			print(", ");
			print(tracerDefault);
		}
		println(");");
		closeAccolade();
    }
    
    protected void printConstraintSystemCode() throws GenerationException {
    	if (getOptions().doStackOptimizations()) {
	    	tprint("protected final ");
	    	print(ContinuationStack.class.getCanonicalName());
	    	println(" $$continuationStack;");
	    	nl();
	    	tprintln(
			"@Override protected final Continuation dequeue() { return super.dequeue(); }",
			"@Override protected final Continuation dequeue(Continuation continuation) { return super.dequeue(continuation); }",
			"@Override protected final void enterHostLanguageMode() { super.enterHostLanguageMode(); }",
			"@Override protected final void exitHostLanguageMode() { super.exitHostLanguageMode(); }"
			);
    	}
    }
    
    protected void printTracerCode() throws GenerationException {
    	tprintln(
			"Tracer tracer;",
			"",
			"@Override",
			"public Tracer getTracer() {",
				"\treturn tracer;",
			"}",
			"",
			"@Override",
			"public void setTracer(Tracer tracer) {",
				"\tthis.tracer = tracer;",
			"}",
			"",
			"@Override",
			"public boolean canBeTraced() {",
				"\treturn true;",
			"}"
    	);
    }
    
    protected void generateSolverCode() throws GenerationException {
    	for (Solver solver : getSolvers()) {
    		String identifier = solver.getIdentifier();
    		String typeString = solver.toTypeString();
			tprint("final "); prints(typeString); printcln(identifier);
			
			if (! identifier.startsWith("$") && !isPrivate(solver)) {
				nl();
				printAccessModifier(solver);
				prints(typeString);
				print("get"); print(capFirst(identifier)); println("Solver() {");
					ttprint("return this."); printcln(identifier); 
				tprintln("}");
			}
		}
    }
    
    protected void generateIdentifierGetter() throws GenerationException {
    	tprintOverride();
    	tprintln("public String getIdentifier() {");
    	ttprint("return ");
    	printLiteral(getHandlerName());
    	println(';');
    	tprintln('}');
    }
    
    protected void generateAccessibilityMethods() throws GenerationException {
		generateIncludeXXXMethod(PACKAGE);
		generateIncludeXXXMethod(PROTECTED);
    	if (getDebugInfo().getDebugLevel() == FULL)
    		generateIncludeXXXMethod(DEBUG);
    }
    
    protected enum XXX {
    	PACKAGE {
    		@Override
    		public String getAccessModifier() {
    			return "";
    		}
    		@Override
    		public String getMethodName() {
    			return "includePackage";
    		}
    	}, 
    	PROTECTED {
    		@Override
    		public String getAccessModifier() {
    			return "protected ";
    		}
    		@Override
    		public String getMethodName() {
    			return "includeProtected";
    		}
    	}, 
    	DEBUG {
    		@Override
    		public String getAccessModifier() {
    			return "public ";
    		}
    		@Override
    		public String getMethodName() {
    			return "getTracerView";
    		}
    	};
    	
    	public abstract String getAccessModifier();
    	public abstract String getMethodName();
    };
    private void generateIncludeXXXMethod(XXX XXX) throws GenerationException {
    	final Collection<UserDefinedConstraint> constraints;
    	switch (XXX) {
    		case PACKAGE:	constraints = getPackageConstraints(); break;
    		case PROTECTED:	constraints = getProtectedConstraints(); break;
    		case DEBUG:		constraints = getUserDefinedConstraints(); break;
			
    		default: throw new InternalError(); 
    	}
    	
    	if (XXX == DEBUG) tprintOverride();
		tprint(XXX.getAccessModifier());
		printHandlerType();
		print(' ');
		print(XXX.getMethodName());
		println("() {");
		incNbTabs();
		
    	if (constraints.size() > getAccessibleConstraints().size()) {
    		tprint("return new ");
    		printHandlerType();
    		print('(');
    		printSolverList(false);
    		if (hasToTrace()) {
    			if (hasSolvers()) print(", ");
    			print("getTracer()");
    		}
    		println(") {");
    		incNbTabs();
    	
    		generateIncludeXXXMethodOverrider(PROTECTED);    		
    		if (XXX != PROTECTED) 
    			generateIncludeXXXMethodOverrider(PACKAGE);
    		if (XXX == DEBUG)
    			generateIncludeXXXMethodOverrider(DEBUG);
    		
    		generateLookupMethod("iterator", constraints); nl();
    		generateLookupMethod("lookup", constraints); nl();
    		
    		generateConstraintClassesMethod(constraints);
    		
    		decNbTabs();
    		tprintln("};");

    	} else {
    		tprintln("return this;");
    	}
    	
    	decNbTabs();
    	tprintln('}');
    }
    
    private void generateIncludeXXXMethodOverrider(XXX XXX) throws GenerationException {
    	tprintOverride();
		tprint(XXX.getAccessModifier());
		printHandlerType();
		print(' ');
		print(XXX.getMethodName());
		println("() {");
		ttprintln("return this;");
		tprintln('}');
		nl();
    }
    
    protected void generateLookupMethod(String name, Iterable<UserDefinedConstraint> constraints) throws GenerationException {
    	tprintln("/**");
    	tprintln(" * {@inheritDoc}");
    	tprintln(" *");
    	for (UserDefinedConstraint constraint : constraints)
    		tprintln(" * @see #" + getMasterLookupMethodName(constraint) + "()");
    	tprintln(" */");
    	tprintOverride();
    	tprintln("@SuppressWarnings(\"unchecked\")");
    	tprint("public Iterator<IConstraint> ");
    	print(name);
		println("() {");
		incNbTabs();
    	
		if (IteratorUtilities.isEmpty(constraints))
    		tprintln("return EmptyIterator.<IConstraint>getInstance();");
    	else {
    		Iterator<UserDefinedConstraint> iter = new FilteredIterator<UserDefinedConstraint>(
				constraints.iterator(),
				new Filter<UserDefinedConstraint>() {
					@Override
					public boolean include(UserDefinedConstraint elem) {
						return elem.mayBeStored();
					}
				}
			);
    		if (iter.hasNext()) {
	    		tprint("return new ");
	    		print(ChainingIterator.class.getCanonicalName());
				println("<IConstraint>(");
	    		do {
	    			ttprint(getMasterLookupMethodName(iter.next()));
	    			print("()");
	    			if (iter.hasNext())
	    				println(',');
	    			else
	    				break;
				} while (true);
	    		nl();
	    		printTabs();
	    		println(");");
    		} else {
    			tprintln("return EmptyIterator.<IConstraint>getInstance();");
    		}
    	}
		
		decNbTabs();
		tprintln('}');
    }
    
    protected void generateConstraintClassesMethod(Collection<UserDefinedConstraint> constraints) throws GenerationException {
    	tprintln("@Override",
    		"@SuppressWarnings(\"unchecked\")",
    		"public Class<? extends Constraint>[] getConstraintClasses() {",
    		"\treturn new Class[] {"
		);
    	incNbTabs(2);
    	for (Iterator<UserDefinedConstraint> constraint = constraints.iterator(); constraint.hasNext();) { 
    		tprint(getConstraintTypeName(constraint.next()));
    		print(".class");
    		if (constraint.hasNext()) println(", ");
    	}
    	nl();
    	decNbTabs();
    	tprintln("};");
    	decNbTabs();
    	tprintln('}');
    }

    public static String getTellMethodFor(UserDefinedConstraint constraint) {
        return "tell" + capFirst(constraint.getIdentifier());
    }
	
    public List<UserDefinedConstraint> accessibleConstraints;
    public List<UserDefinedConstraint> getAccessibleConstraints() {
    	if (accessibleConstraints == null) {
    		List<UserDefinedConstraint> temp = new ArrayList<UserDefinedConstraint>();
			for (UserDefinedConstraint constraint : getUserDefinedConstraints())
				if (isAccessible(constraint)) 
					temp.add(constraint);
			accessibleConstraints = Collections.unmodifiableList(temp);
    	}
		return accessibleConstraints;
    }
    
    protected boolean isAccessible(UserDefinedConstraint constraint) {
    	return isPublic(constraint) 
    		|| (isDefaultAccess(getHandler()) && !isPrivate(constraint)); 
    }
    
    public List<UserDefinedConstraint> protectedConstraints;
    public List<UserDefinedConstraint> getProtectedConstraints() {
    	if (protectedConstraints == null) {
    		List<UserDefinedConstraint> temp = new ArrayList<UserDefinedConstraint>();
			for (UserDefinedConstraint constraint : getUserDefinedConstraints())
				if (isAccessible(constraint) || isProtected(constraint))
					temp.add(constraint);
			protectedConstraints = Collections.unmodifiableList(temp);
    	}
		return protectedConstraints;
    }

    public List<UserDefinedConstraint> packageConstraints;
    public List<UserDefinedConstraint> getPackageConstraints() {
    	if (packageConstraints == null) {
    		List<UserDefinedConstraint> temp = new ArrayList<UserDefinedConstraint>();
			for (UserDefinedConstraint constraint : getUserDefinedConstraints())
				if (!isPrivate(constraint)) 
					temp.add(constraint);
			packageConstraints = Collections.unmodifiableList(temp);
    	}
		return packageConstraints;
    }
    
    protected void printSolverList(final boolean printTypes) throws GenerationException {
    	try {
			IteratorUtilities.deepAppendTo(this, 
				new ConvertingIterator<Solver, String>(
	    			getSolvers(),
	    			new Convertor<Solver, String>() {
	    				public String convert(Solver solver) {
	    					StringBuilder result = new StringBuilder();
	    					if (printTypes)
	    						result.append(solver.getType().toTypeString()).append(' '); 
	    					result.append(solver.getIdentifier());
	    					return result.toString();
	    				}
	    			}
    		), "", "", ", ");
    	} catch (IOException ioe) {
    		throw new GenerationException(ioe);
    	}
    }
    
    boolean isRecursive(UserDefinedConstraint constraint) {
		return getOptions().doStackOptimizations() && constraint.isRecursive();
	}
    
    protected void generateTellMethods() throws GenerationException {
        UserDefinedConstraint constraint;
        Iterator<UserDefinedConstraint> constraints = 
        	new FilteredIterator<UserDefinedConstraint>(
    			getUserDefinedConstraints(),
    			new Filter<UserDefinedConstraint>() {
					@Override
					public boolean exclude(UserDefinedConstraint constraint) {
						return isPrivate(constraint);
					}
				}
        	);
        
        while (constraints.hasNext()) {
            constraint = constraints.next();
            tprintAnnotationStart(JCHR_Tells.class); 
            if (isRecursive(constraint)) {
            	nl();
            	ttprint("constraint = ");
            	printLiteral(constraint.getIdentifier());
            	println(",");
            	ttprintln("warrantsStackOpimization = true");
            	printTabs();
            }
            else 
            	printLiteral(constraint.getIdentifier());
            println(')');

            printAccessModifier(constraint, "private");
            print("final void ");
            print(getTellMethodFor(constraint));
            print('(');
            printFullVariableList(constraint.getFormalVariables());
            print(')');
            openAccolade();
            
            if (isRecursive(constraint)) {
            	tprintln("if ($$constraintSystem.inDefaultHostLanguageMode())");
            		ttprint("call("); printNewConstraint(constraint); println(");");
            	tprintln("else if (!$$constraintSystem.isQueuing())");	// implies not host language mode
            		ttprint("$$continuationStack.push("); printNewConstraint(constraint); println(");");
            	tprintln("else");
            		ttprint("$$continuationQueue.enqueue("); printNewConstraint(constraint); println(");");
        		
            } else {
            	printTabs();
            	printNewConstraint(constraint);
            	println(".activate();");
            }
            
            closeAccolade();
            if (constraints.hasNext()) nl();
        }
    }
    
    protected void printNewConstraint(UserDefinedConstraint constraint) throws GenerationException {
    	print("new ");
    	print(getConstraintTypeName(constraint));
    	print('(');
    	printVariableList(constraint.getFormalVariables());
    	print(')');
    }
    
    protected void generateConstraintClasses() throws GenerationException {
    	for (UserDefinedConstraint constraint : getUserDefinedConstraints())
    		new ConstraintCodeGenerator(this, constraint).generate();
    }
    
    protected void generateIsStoredMethod() throws GenerationException {
        tprintOverride();
        tprintln("public boolean isStored(Class<? extends IConstraint> constraintClass) {");
        incNbTabs();
        for (UserDefinedConstraint constraint : getUserDefinedConstraints()) {
            tprint("if (constraintClass == ");
            print(getConstraintTypeName(constraint));
            println(".class)");
            ttprint("return ");
            print(constraint.mayBeStored());
            println(";");
        }
        tprintln("throw new IllegalArgumentException(constraintClass.getSimpleName());");
        decNbTabs();
        tprintln('}');
    }
    
    protected void generateConstraintStoreCode() throws GenerationException {
    	for (UserDefinedConstraint constraint : getUserDefinedConstraints()) {
            for (ILookupCategory category : constraint.getLookupCategories()) {
                ConstraintStoreCodeGeneratorFactory
                    .getInstance(this, constraint, category)
                    .generateMembers();
                nl();
            }
            
            generateStorageMethod(constraint);
            generateLookupMethods(constraint);
            generateMasterLookupMethod(constraint);
            generateFilteredMasterLookupMethod(constraint);
            generateMasterGetter(constraint);
            generateReactivationMethods(constraint);
        }
        
    	generateMasterReactivationMethods();
    	nl();
    	printSizeMethods();
    	nl();
    	generateResetMethod();
    }
    
    protected void generateStorageMethod(UserDefinedConstraint constraint) throws GenerationException {
        if (constraint.mayBeStored()) {	
	    	tprintln("/**");
	        tprint(" * Adds the given {@link ");
	        print(getConstraintTypeName(constraint));
	        println("} <code>constraint</code> to the"); tprintln(
	            " * constraint store.", 
	            " *",
	            " * @param constraint",
	            " *  The constraint that has to be added to the constraint store.",
	            " *",
	            " * @pre <code>constraint != null</code>",
	            " * @pre The constraint is newer then all other constraints in the store.",
	            " *",
	            " * @see runtime.Constraint#isNewerThan(runtime.Constraint)",
	            " */"
	        );
	        tprint("void "); print(getStorageMethodNameFor(constraint)); print('(');
	            print(getConstraintTypeName(constraint)); 
	        println(" constraint) {");
	        incNbTabs();
	        for (ILookupCategory category : constraint.getLookupCategories()) {
	            ConstraintStoreCodeGeneratorFactory
	                .getInstance(this, constraint, category)
	                .generateStorageCode();
	        }
	
	        if (hasToTrace())
	            tprintln("if (tracer != null) tracer.stored(constraint);");
	        
	        decNbTabs();
	        tprintln('}');
	        nl();
        }
    }
    
    public static String getStorageMethodNameFor(UserDefinedConstraint constraint) {
        return "store" + capFirst(constraint.getIdentifier());
    }
    
    protected void generateMasterLookupMethod(UserDefinedConstraint constraint) throws GenerationException {
    	if (isPrivate(constraint) && getDebugInfo().getDebugLevel() != FULL) return;
    	
    	ILookupCategories lookups = constraint.getLookupCategories();
    	
    	tprintln(
			"/**",
			" * Returns an iterator over all <code>"+ getConstraintTypeName(constraint) +"</code>s currently",
			" * in the constraint store. The <code>Iterator.remove()</code> method is never supported.",
			" * Besides that, we offer very few guarantees about the behavior of these iterators:",
			" * <ul>",
			" *  <li>",
			" *      There are no guarantees concerning the order in which the constraints ",
			" *      are returned.",
			" *  </li>",
			" *  <li>",
			" *      The iterators <em>might</em> fail if the constraint store is structurally modified",
			" *      at any time after the <code>Iterator</code> is created. In the face of concurrent modification",
			" *      it cannot recover from, the <code>Iterator</code> fails quickly and cleanly (throwing a",
			" *      <code>ConcurrentModificationException</code>), rather than risking arbitrary, ",
			" *      non-deterministic behavior at an undetermined time in the future.",
			" *      <br/>",
			" *      The <i>fail-fast</i> behavior of the <code>Iterator</code> is not guaranteed,",
			" *      even for single-threaded applications (this constraint is inherited from the ",
			" *      <a href=\"http://java.sun.com/j2se/1.5.0/docs/guide/collections/\">Java Collections Framework</a>).",
			" *      and should only be used to detect bugs. ",
			" *      <br/>",
			" *      Important is that, while <code>Iterator</code>s returned by collections of the ",
			" *      Java Collections Framework generally &quot;fail fast on a best-effort basis&quot;, ",
			" *      this is not the case with our <code>Iterator</code>s. On the contrary: our",
			" *      iterators try to recover from structural changes &quot;on a best-effort basis&quot;,",
			" *      and fail cleanly when this is not possible (or possibly to expensive). So,",
			" *      in general you can get away with many updates on the constraint store during",
			" *      iterations (there is no way of telling which will fail though...)",
			" *  </li>",
			" *  <li>",
			" *      The failure of the <code>Iterator</code> might only occur some time after",
			" *      the structural modification was done: this is again because many parts",
			" *      of the constraint store are iterable in the presence of modification.",
			" *  </li>",
			" *  <li>",
			" *      When a constraint is added to the constraint store after the creation of the",
			" *      iterator it is possible it appears somewhere later in the iteration, but",
			" *      it is equally possible it does not.",
			" *  </li>",
			" *  <li>",
			" *      Removal of constraints on the other hand does mean the iterator will never return",
			" *      this constraint.",
			" *      Note that it still remains possible that the iterator fails somewhere after",
			" *      (and because of) this removal.",
			" *  </li>",
			" * </ul>",
			" * The lack of guarantees is intentional. Some <i>Iterator</i>s might behave perfectly ",
			" * in the presence of constraint store updates, whilst others do not. Some might return",
			" * constraints in order of their creation (and only iterate over constraints that existed",
			" * at the time of their creation), others do not. In fact: it is perfectly possible that ",
			" * their behavior changes between two compilations (certainly when moving to a new version",
			" * of the compiler). This is the price (and at the same time the bless) of declarative ",
			" * programming: it is the compiler that chooses the data structures that seem optimal ",
			" * to him at the time!",
			" *",
			" * @return An iterator over all <code>"+ getConstraintTypeName(constraint) +"</code>s currently",
			" * \tin the constraint store.",
			" */"
		);
    	
    	if (!lookups.isTrivial() || !isPrivate(constraint))
			printAccessModifier(constraint);
    	else
    		printTabs();
    	
    	print("Iterator<"); print(getConstraintTypeName(constraint)); print("> ");
    		print(getMasterLookupMethodName(constraint)); println("() {");
		
        incNbTabs();
		ConstraintStoreCodeGeneratorFactory
        	.getInstance(this, constraint, lookups.getMasterLookupCategory())
			.generateMasterLookupCode();
		decNbTabs();
		
		tprintln('}');
		nl();
    }
    
    protected void generateFilteredMasterLookupMethod(UserDefinedConstraint constraint) throws GenerationException {
    	if (isPrivate(constraint)) return;
    	
    	tprintln(
			"/**",
			" * Returns an {@link Iterable} over all {@link "+ getConstraintTypeName(constraint) +"}s ",
			" * currently in the constraint store, filtered by a user-provided filter. ",
			" * The <code>Iterator.remove()</code> method is never supported.",
			" * Besides that, we offer the same guarantees about the behavior of the iterators",
			" * as the ones given by the {@link #"+ getMasterLookupMethodName(constraint) +"()}.",
			" * Also: if the condition the filter tests for is altered during an iteration,",
			" * behavior is, as always, undefined.",
			" *",
			" * @param filter",
			" *  A user defined filter that will be used to filter the iterated elements.",
			" *",
			" * @see #"+ getMasterLookupMethodName(constraint),
			" */"
    	);
    	
		printAccessModifier(constraint);
    	print("Iterable<"); print(getConstraintTypeName(constraint)); print("> ");
    		print(getFilteredMasterLookupMethodName(constraint)); 
    		print("(Filter<? super "); print(getConstraintTypeName(constraint)); println("> filter) {");
    	
		incNbTabs();
		ConstraintStoreCodeGeneratorFactory
        	.getInstance(this, constraint, constraint.getMasterLookupCategory())
			.generateFilteredMasterLookupCode();
		decNbTabs();
		
		tprintln('}');
		nl();
    }
    
    protected void generateMasterGetter(UserDefinedConstraint constraint) throws GenerationException {
    	if (isPrivate(constraint)) return;
    	
    	tprintln(
			"/**",
			" * Returns (an unmodifiable view of) the current collection of ",
			" * <code>"+ getConstraintTypeName(constraint) +"</code>s currently in the constraint store. ",
			" * Iterators over this collection are the equivalents of those",
			" * created by the <code>"+ getMasterLookupMethodName(constraint) +"</code>-method.",
			" * We refer to this method for more information on their behavior.",
			" * This collection is backed by the constraint store: updates ",
			" * to the store will be reflected in the collection.",
			" *",
			" * @return (An unmodifiable view of) the current collection of ",
			" * \t<code>"+ getConstraintTypeName(constraint) +"</code>s currently ",
			" *\tin the constraint store. ",
			" *",
			" * @see #"+ getMasterLookupMethodName(constraint),
			" */"
		);
    	printAccessModifier(constraint);
    	print("Collection<"); print(getConstraintTypeName(constraint)); print("> ");
    		print(getMasterGetterName(constraint)); println("()");
		
		openAccolade();
		
		if (!constraint.mayBeStored())
			tprintln("return Empty.getInstance();");
		else
			printReturnUnmodifiableCollection(constraint);
		
		closeAccolade();
		nl();
    }
    
    protected void generateMasterReactivationMethods() throws GenerationException {
    	generateMasterReactivationMethod(false);
    	generateMasterReactivationMethod(true);
    }
    
    private void generateMasterReactivationMethod(boolean filtered) throws GenerationException {
    	tprintln("/** {@inheritDoc} */");
    	tprintOverride();
    	tprint("public void reactivateAll(");
    	if (filtered) {
			print(Filter.class.getCanonicalName());
			print("<? super Constraint> filter");
		}
    	print(')');
    	openAccolade();
	    	boolean empty = true;
	    	for (UserDefinedConstraint constraint : getUserDefinedConstraints())
	    		if (constraint.isReactive()) {
	    			empty = false;
	    			printTabs();
	    			printReactivateCode(constraint, filtered);
	    		}
	    	if (empty) tprintln("// NOP");
    	closeAccolade();
    	nl();
    }
    
    protected void generateReactivationMethods(UserDefinedConstraint constraint) throws GenerationException {
    	generateReactivationMethod(constraint, false);
    	generateReactivationMethod(constraint, true);
    }
    
    private void generateReactivationMethod(UserDefinedConstraint constraint, boolean filtered) throws GenerationException {
    	if (isPrivate(constraint)) return;
    	
    	tprintln("/**");
    	tprint(" * Reactivates all constraints of type <code>");
    	print(getConstraintTypeName(constraint)); print("</code>");
    	if (!filtered) {
    		println('.');
    	} else {
    		nl();
    		tprintln(" * that are not excluded by the provided filter.");
    		tprintln(" *");
    		tprintln(" * @param filter");
    		tprintln(" *   A filter on the constraints to reactivate.");
    	}
    	tprintln(" */");
    	printAccessModifier(constraint);
    	print("void "); print(getReactivationMethodName(constraint));
    	print("("); 
		if (filtered) {
			print(Filter.class.getCanonicalName());
			print("<? super ");
			print(getConstraintTypeName(constraint));
			print("> filter");
		}
    	print(") ");
    	if (!constraint.isReactive()) {
    		println('{');
    		ttprintln("// NOP");
    		tprintln('}');
    	} else {
    		printReactivateCode(constraint, filtered);
    	}
    	nl();
    }
    
    private void printReactivateCode(UserDefinedConstraint constraint, boolean filtered) throws GenerationException {
    	println('{');
    	incNbTabs();
		tprint("Iterator<"); 
		print(getConstraintTypeName(constraint));
		print("> iter = ");
		ConstraintStoreCodeGeneratorFactory
			.getInstance(this, constraint, constraint.getMasterLookupCategory())
			.printCreateIteratorCode();
		println(';');
		if (filtered) {
			tprintln("while (iter.hasNext()) {");
			ttprint(getConstraintTypeName(constraint)); println(" current = iter.next();");
			ttprintln("if (!filter.exclude(current)) current.reactivate();");
			tprintln('}');
		} else {
			tprintln("while (iter.hasNext()) iter.next().reactivate();");
		}
    	closeAccolade();
    }
    
    protected void generateFilteredReactivationMethod(UserDefinedConstraint constraint) throws GenerationException {
    	if (isPrivate(constraint)) return;
    	
    }
    
    protected void printReturnUnmodifiableCollection(UserDefinedConstraint constraint) throws GenerationException {
    	ILookupCategory category = null;
    	for (ILookupCategory temp : constraint.getLookupCategories())
    		if (temp.getIndexType().isSetSemantics()) {
    			category = temp;
    			break;
    		}
    	
    	String type = getConstraintTypeName(constraint);
    	
    	if (category == null)
	    	tprintln(
				"// This implementation is still quite inefficient",
				"// (collection size has to be computed on-demand): ",
				"// best not to over-use this feature yet!"
			);
    	tprint("return new AbstractUnmodifiableCollection<"); print(type); println(">() {");
    	incNbTabs();
		tprintOverride();
		tprintln("public int size() {");
		ttprint("return ");
		if (category == null)
			println("util.iterator.IteratorUtilities.size(iterator());");
		else {
			print(getHashIndexName(constraint, category));
			println(".size();");
		}
		tprintln('}');
		nl();
		tprintOverride();
		tprint("public Iterator<"); print(type); println("> iterator() {");
		ttprint("return ");
		print(getMasterLookupMethodName(constraint));
		println("();");
		tprintln('}');
		decNbTabs();
		tprintln("};");
    }
    
    protected void generateLookupMethods(UserDefinedConstraint constraint) throws GenerationException {
		for (ILookupCategory category : constraint.getLookupCategories()) {
			if (category == NeverStoredLookupCategory.getInstance()) continue;
			
    		ConstraintStoreCodeGenerator generator = 
    			ConstraintStoreCodeGeneratorFactory.getInstance(this, constraint, category);
    		generator.incNbTabs();
    		
    		for (int lookupTypeIndex = 0; lookupTypeIndex < category.getNbLookupTypes(); lookupTypeIndex++) {
    			tprint("final ");
    			generator.printLookupReturnType(); print(' ');
					print(getLookupMethodName(constraint, category, lookupTypeIndex));
    				print('('); generator.generateLookupArgumentList(lookupTypeIndex); println(") {");
				generator.generateLookupCode(lookupTypeIndex);
				tprintln('}');
				nl();
    		}
    	}
    }
    
    protected void printSizeMethods() throws GenerationException {
    	tprintln(
			"// This implementation is still very inefficient: ",
			"// don\'t over-use this feature yet!",
			"@Override",
			"public int size() {",
			"\treturn util.iterator.IteratorUtilities.size(iterator());",
			"}",
			"",
			"@Override",
			"public boolean isEmpty() {",
			"\treturn !iterator().hasNext();",
			"}"
    	);
    }
    
    protected void generateResetMethod() throws GenerationException {
    	tprintln(
			"/**",
			" * Resets the handler, i.e. it terminates and removes all constraints",
			" * from the constraint store.",
			" * The resulting constraint store will be empty.",
			" */",
			"public void reset() {"
		);
    	
    	incNbTabs();
    	boolean notEmpty = false;
    	for (UserDefinedConstraint constraint : getUserDefinedConstraints()) {
    		notEmpty |= ConstraintStoreCodeGeneratorFactory
    			.getInstance(this, constraint, constraint.getMasterLookupCategory())
    			.generateResetCode();
    	}
    	if (!notEmpty) tprintln("// NOP");
    	decNbTabs();
    	tprintln('}');
    }
    
    public static String getMasterLookupMethodName(UserDefinedConstraint constraint) {
    	return "lookup" + capFirst(constraint.getIdentifier());
    }
    public static String getFilteredMasterLookupMethodName(UserDefinedConstraint constraint) {
    	return getMasterGetterName(constraint);
    }
    public static String getReactivationMethodName(UserDefinedConstraint constraint) {
    	return "reactivate" + capFirst(constraint.getIdentifier()) + "Constraints";
    }
    public static String getFilteredReactivationMethodName(UserDefinedConstraint constraint) {
    	return getReactivationMethodName(constraint);
    }
    public static String getMasterGetterName(UserDefinedConstraint constraint) {
    	return "get" + capFirst(constraint.getIdentifier()) + "Constraints";
    }
    public static String getLookupMethodName(Lookup lookup) {
    	return getLookupMethodName(lookup.getConstraint(), lookup.getLookupCategory(), lookup.getLookupType());
    }
    public static String getLookupMethodName(UserDefinedConstraint constraint, ILookupCategory category, ILookupType type) {
    	return getLookupMethodName(constraint, category, category.getIndexOf(type));
    }
    public static String getLookupMethodName(UserDefinedConstraint constraint, ILookupCategory category, int lookupTypeIndex) {
    	return "lookup" + capFirst(constraint.getIdentifier()) 
    		+ '_' + String.valueOf(constraint.getLookupCategories().getIndexOf(category))
    		+ '_' + String.valueOf(lookupTypeIndex);
    }
}