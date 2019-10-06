package compiler.codeGeneration;

import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.KEPT;
import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.NEGATIVE;
import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.REMOVED;
import static compiler.CHRIntermediateForm.constraints.ud.schedule.LastScheduleElementGetter.getLastScheduleElementOf;
import static compiler.CHRIntermediateForm.constraints.ud.schedule.LookupsGetter.getLastLookupOf;
import static compiler.CHRIntermediateForm.constraints.ud.schedule.LookupsGetter.getLookupsOf;
import static compiler.CHRIntermediateForm.debug.DebugLevel.FULL;
import static compiler.CHRIntermediateForm.rulez.RuleType.PROPAGATION;
import static compiler.CHRIntermediateForm.types.PrimitiveType.CHAR;
import static compiler.analysis.JCHRFreeAnalysor.hasJCHRFreeArguments;
import static compiler.analysis.JCHRFreeAnalysor.isJCHRFree;
import static compiler.codeGeneration.AbstractHashIndexCodeGenerator.getHashIndexName;
import static compiler.codeGeneration.AbstractHashIndexCodeGenerator.needsHashObserver;
import static compiler.codeGeneration.ConjunctCodeGenerator.BODY;
import static compiler.codeGeneration.ConjunctCodeGenerator.GUARD;
import static compiler.codeGeneration.HandlerCodeGenerator.getHandlerType;
import static compiler.codeGeneration.HandlerCodeGenerator.getHandlerTypeName;
import static compiler.codeGeneration.HandlerCodeGenerator.getLookupMethodName;
import static compiler.codeGeneration.HandlerCodeGenerator.getStorageMethodNameFor;
import static compiler.codeGeneration.HistoryCodeGenerator.hasToDoHistoryTest;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import runtime.ConstraintIterable;
import util.StringUtils;
import util.Terminatable;
import util.collections.Stack;
import util.iterator.ArrayIterator;
import util.iterator.Filtered;
import util.iterator.FilteredIterator;

import compiler.CHRIntermediateForm.Handler;
import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumentable.IArgumentable;
import compiler.CHRIntermediateForm.arg.argumented.IArgumented;
import compiler.CHRIntermediateForm.arg.argumented.IBasicArgumented;
import compiler.CHRIntermediateForm.arg.visitor.AbstractVariableScanner;
import compiler.CHRIntermediateForm.arg.visitor.UpCastingArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.VariableCollector;
import compiler.CHRIntermediateForm.conjuncts.AbstractConjunctVisitor;
import compiler.CHRIntermediateForm.conjuncts.Conjunction;
import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.conjuncts.IConjunction;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.bi.Failure;
import compiler.CHRIntermediateForm.constraints.bi.IBuiltInConjunct;
import compiler.CHRIntermediateForm.constraints.java.AssignmentConjunct;
import compiler.CHRIntermediateForm.constraints.java.NoSolverConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConjunct;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.Lookup;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.NeverStoredLookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.schedule.AbstractScheduleVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleVisitable;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduled;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IVariableInfo;
import compiler.CHRIntermediateForm.constraints.ud.schedule.Schedule;
import compiler.CHRIntermediateForm.members.AbstractMethodInvocation;
import compiler.CHRIntermediateForm.members.ConstructorInvocation;
import compiler.CHRIntermediateForm.rulez.Body;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.CHRIntermediateForm.types.GenericType;
import compiler.CHRIntermediateForm.variables.FormalVariable;
import compiler.CHRIntermediateForm.variables.Variable;
import compiler.analysis.JCHRFreeAnalysor;
import compiler.analysis.CifAnalysor.AbstractBodyVisitor;
import compiler.analysis.removal.RemovalTester;

public class ConstraintCodeGenerator extends CIFJavaCodeGenerator {
	
	private UserDefinedConstraint constraint;
    
    private Set<Integer> usedTupleArities, usedPushes;
    
    private Queue<AbstractContinuationInfo> continuationsToGenerate;
    private Set<AbstractContinuationInfo> generatedContinuations;
	
	private boolean hasSetSemanticsLookup, hasStorageBackPointers;
    
    protected final HistoryCodeGenerator PROPAGATION_HISTORY_CODE_GENERATOR;
	
	public ConstraintCodeGenerator(HandlerCodeGenerator generator, UserDefinedConstraint constraint) {
		super(generator);
		changeConstraint(constraint);
        setUsedTupleArities(generator.getUsedTupleArities());
        setUsedPushes(generator.getUsedPushes());
        setContinuationsToGenerate(new LinkedList<AbstractContinuationInfo>());
        setGeneratedContinuations(new HashSet<AbstractContinuationInfo>());
        
        PROPAGATION_HISTORY_CODE_GENERATOR = new HistoryCodeGenerator(this);
	}
	
	public UserDefinedConstraint getConstraint() {
		return constraint;
	}
	public void changeConstraint(UserDefinedConstraint constraint) {
		setConstraint(constraint);
		initConstraintStoreInfo();
	}
	protected void setConstraint(UserDefinedConstraint constraint) {
		this.constraint = constraint;
	}
	
	public boolean isReactive() {
		return getConstraint().isReactive();
	}
	public boolean isRecursive() {
		return isRecursive(getConstraint());
	}
	
	boolean isRecursive(UserDefinedConstraint constraint) {
		return getOptions().doStackOptimizations() && constraint.isRecursive();
	}
	
	public String getIdentifier() {
		return getConstraint().getIdentifier();
	}
	public int getArity() {
		return getConstraint().getArity();
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
    
    protected Queue<AbstractContinuationInfo> getContinuationsToGenerate() {
        return continuationsToGenerate;
    }
    protected void setContinuationsToGenerate(Queue<AbstractContinuationInfo> continuations) {
        this.continuationsToGenerate = continuations;
    }
    protected Set<AbstractContinuationInfo> getGeneratedContinuations() {
        return generatedContinuations;
    }
    protected void setGeneratedContinuations(Set<AbstractContinuationInfo> continuations) {
        this.generatedContinuations = continuations;
    }
    protected void scheduleContinuation(AbstractContinuationInfo continuation) {
    	if (!getGeneratedContinuations().contains(continuation)
		 &&	!getContinuationsToGenerate().contains(continuation))
    		getContinuationsToGenerate().offer(continuation);
    }
	
	protected boolean hasSetSemanticsLookup() {
		return hasSetSemanticsLookup;
	}
	protected void initConstraintStoreInfo() {
		boolean mayBeRemoved = getConstraint().mayBeRemoved();
		hasSetSemanticsLookup = false;
		hasStorageBackPointers = needsStorageBackPointersForObserverLists();
		for (ILookupCategory category : getConstraint().getLookupCategories())
			if (category.getIndexType().isSetSemantics()) 
				hasSetSemanticsLookup = true;
			else if (mayBeRemoved && category != NeverStoredLookupCategory.getInstance())
				hasStorageBackPointers = true;
	}
	protected boolean hasStorageBackPointers() {
		return hasStorageBackPointers;
	}
	
	protected boolean hasToBeRehashable() {
		for (int i = 0; i < getArity(); i++)
			if (needsHashObserver(getFormalVariableAt(i)))
				return true;
		return false;
	}
	
	protected boolean needsStorageBackPointersForObserverLists() {
		return hasToBeRehashable();
	}
	
	@Override
	protected void doGenerate() throws GenerationException {
		System.out.printf(" --> generating code for %s/%d constraint%n", 
				getConstraint().getIdentifier(), getConstraint().getArity());
		
		generateAnnotations();
		generateClassSignature();		
		openAccolade();
			generateConstructor();
			generateArgumentMembers();
			generateConstraintMethods();
            generateHistoryMembers();
			generateOccurrencesCode();
			generateContinuations();
			generateToStringMethod();
			generateEqualsMethods();
			generateHashCode();
		closeAccolade();
	}
	
	protected void generateConstraintMethods() throws GenerationException {
		if (mayBeStored()) {
			generateStorageCode();
			generateReactivationCode();
			generateBackPointerCode();
			generateTerminateMethod();
		} else {
			generateNotStoredMethods();
		}
		generateTrivialInspectors();
	}
	
	protected void generateAnnotations() throws GenerationException {
		generateGeneratedAnnotation();
		generateMetaAnnotation();
	}
	protected void generateMetaAnnotation() throws GenerationException {
		tprintln("@Constraint.Meta(");
			ttprint("identifier = "); printLiteral(getIdentifier()); println(',');
			ttprint("arity = "); print(getArity()); println(',');
			ttprint("fields = {");
			boolean first = true;
			for (FormalVariable variable : getFormalVariables()) {
				if  (first) first = false; else print(", ");
				printLiteral(variable.getIdentifier());
			}
			println('}');
		tprintln(')');
	}
	
	protected void generateClassSignature() throws GenerationException {
		final boolean mayBeStored = mayBeStored();
		
		if (getDebugInfo().getDebugLevel() == FULL)
			tprint("public ");
		else
			printAccessModifier(getConstraint());
		print("final class ");
		print(getConstraintTypeName());
		print(" extends ");
		print(mayBeStored? "Constraint" : "Continuation");
		
		if (hasSetSemanticsLookup() || !mayBeStored) {
			print(" implements ");
			if (hasSetSemanticsLookup()) {
				if (hasToBeRehashable()) print("Rehashable");
				print("Key");
				if (!mayBeStored) print(", ");
			}
			if (!mayBeStored) print("IConstraint");
		}
	}
	
	protected void generateArgumentMembers() throws GenerationException {
		for (FormalVariable variable : getFormalVariables()) {
			nl();
			tprint("final ");
			print(variable.getTypeString());
			print(' ');
			print(variable.getIdentifier());
			println(';');
			tprint("public ");
			print(variable.getTypeString());
			print(' ');
			print(getVariableGetterName(variable));
			print("() { return this.");
			print(variable.getIdentifier());
			println("; }");
		}
	}
	
	public boolean mayBeStored() {
		return getConstraint().mayBeStored();
	}
    
    protected void generateStorageCode() throws GenerationException {
    	nl();
    	tprint("protected void store()");
    	openAccolade();
        printStoreCode(true);
        closeAccolade();
    }
	
	protected void generateReactivationCode() throws GenerationException {
		boolean doGeneration = doGenerationOptimization();
		if (doGeneration) {
			nl();
			tprintln("protected boolean reactivated;");
		}
		
		nl();
		tprintOverride();
		if (isReactive() && isRecursive()) tprintSuppress("synthetic-access");
		tprintln("public final void reactivate() {");
		incNbTabs();
		if (isReactive()) {
			if (hasToTrace())
				tprintln("if (tracer != null) tracer.reactivated(this);");
			
			if (isRecursive()) {
				if (warrantsReactivationClass())
					printReactivate("REACTIVATION", doGeneration);
				else
					printReactivate("this", doGeneration);
			} else {
				printNonRecursive__ctivationCode(false);
				if (doGeneration) tprintln("reactivated = true;");
			}
		} else {
			tprintln("// NOP");
		}
		closeAccolade();
	}
	
	protected void printReactivate(String it, boolean doGeneration) throws GenerationException {
		tprintln("if ($$constraintSystem.inHostLanguageMode()) {");
		ttprint(getHandlerTypeName(getHandler()));
			print(".this.call("); print(it); println(");");
		tprintln("} else {");
		if (doGeneration) ttprintln("reactivated = false;");
		printTab(); printPush(it);
		tprintln('}');
	}
	
	protected boolean warrantsReactivationClass() {
		if (isReactive() && isRecursive())
			for (Occurrence occurrence : getConstraint().getPositiveOccurrences())
				if (occurrence.isActive() && !occurrence.isReactive())
					return true;
		return false;
	}
	
	
	protected void generateConstructor() throws GenerationException {
		tprint(getConstraintTypeName());
		print('(');
		printFullVariableList(getFormalVariables());
		println(") {");
		
		if (getArity() == 0) {
			ttprintln("// NOP");
		} else { 
			for (FormalVariable variable : getFormalVariables()) {
				ttprint("this.");
				print(variable.getIdentifier());
				print(" = ");
				print(variable.getIdentifier());
				println(";");
			}
		}
		tprintln('}');
	}
	
	protected void generateTerminateMethod() throws GenerationException {
		nl();
		tprintOverride();
		tprintln("protected final void terminate() {");
		incNbTabs();
		generateTerminateCode(false);
		closeAccolade();
	}
	protected void generateBackPointerCode() throws GenerationException {
		if (hasStorageBackPointers()) {
			nl();
			tprintln("private StorageBackPointer $$storageBackPointers;");
			nl();
			tprintOverride();
			tprint("public void addStorageBackPointer(");
			print(Terminatable.class.getCanonicalName());
			println(" x) {");
			ttprintln("$$storageBackPointers = new StorageBackPointer($$storageBackPointers, x);");
			tprintln('}');
		}
	}
	protected void generateTerminateCode(boolean inRehash) throws GenerationException {
		final UserDefinedConstraint constraint = getConstraint();
		
		tprintln("alive = false;");
		
		if (constraint.mayBeStored()) {
			if (!inRehash) {
				nl();
				tprintln("if (stored) {");
				incNbTabs();
			}
			tprintln("stored = false;");
			
			PROPAGATION_HISTORY_CODE_GENERATOR.setNbTabs(getNbTabs());
	        for (Rule rule : getRules())
	        	PROPAGATION_HISTORY_CODE_GENERATOR.generateTerminationCode(constraint, rule);
			
	        if (hasStorageBackPointers())
				tprintln(
					"StorageBackPointer cursor = $$storageBackPointers;",
					"if (cursor != null) {",
					"\t$$storageBackPointers = null;",
		            "\tdo {",
		            "\t\tcursor.value.terminate();",
		            "\t} while ( (cursor = cursor.next) != null);",
		            "}"
				);
			
			if (!inRehash) printRemoveSelfCode();
			
			if (hasToTrace())
				tprintln("if (tracer != null) tracer.removed(this);");
			
			if (!inRehash) closeAccolade();
		}
		
		if (hasToTrace())
			tprintln("if (tracer != null) tracer.terminated(this);");
	}
	
	protected void generateTrivialInspectors() throws GenerationException {
		nl();
		generateGetIdentifierMethod();
		nl();
		generateGetArityMethod();
		nl();
		generateGetArgumentsMethod();
		nl();
		generateGetArgumentTypesMethod();
		nl();
		generateGetInfixIdentifiersMethod();
		nl();
		generateHasInfixIdentifiersMethod();
		nl();
		generateGetHandlerMethod();
	}
	
	protected void generateGetIdentifierMethod() throws GenerationException {
		tprintln("public final String getIdentifier() {");
		ttprint("return "); printLiteral(getIdentifier()); println(';');
		tprintln('}');
	}
	
	protected void generateGetArityMethod() throws GenerationException {
		tprintln("public final int getArity() {");
		ttprint("return "); print(getConstraint().getArity()); println(';');
		tprintln('}');
	}
	
	protected void generateGetArgumentsMethod() throws GenerationException {
		tprintln("public final Object[] getArguments() {");
		ttprint("return new Object[] {");
		if (getArity() != 0) {
			nl();
			incNbTabs(2);
			int i = 0;
			while (true) {
				tprint(getVariableGetterName(getFormalVariableAt(i)));
				print("()");
				if (++i == getArity()) break;
				println(',');
			}
			nl();
			decNbTabs();
			printTabs();
			decNbTabs();
		}
		println("};");
		tprintln('}');
	}
	
	protected void generateGetArgumentTypesMethod() throws GenerationException {
		tprintSuppressUnchecked();
		tprintln("public final Class<?>[] getArgumentTypes() {");
		ttprint("return new Class[] {");
		if (getArity() != 0) {
			nl();
			incNbTabs(2);
			int i = 0;
			while (true) {
				tprint(getFormalVariableAt(i).getType().getClassString());
				if (++i == getArity()) break;
				println(',');
			}
			nl();
			decNbTabs();
			printTabs();
			decNbTabs();
		}
		println("};");
		tprintln('}');
	}
	
	protected void generateHasInfixIdentifiersMethod() throws GenerationException {
		tprintln("public final boolean hasInfixIdentifiers() {");
		ttprint("return ");
		print(getConstraint().hasInfixIdentifiers());
		println(';');
		tprintln('}');
	}

	protected void generateGetInfixIdentifiersMethod() throws GenerationException {
		tprintln("public final String[] getInfixIdentifiers() {");
		ttprint("return new String[] {"); 
		String[] ids = getConstraint().getInfixIdentifiers();
		if (ids != null && ids.length > 0) {
			printLiteral(ids[0]);
			for (int i = 1; i < ids.length; i++) {
				print(',');
				printLiteral(ids[i]);
			}
		}
		println("};");
		tprintln('}');
	}
	
	protected void generateGetHandlerMethod() throws GenerationException {
		tprint("public final "); print(getHandlerType(getHandler())); println(" getHandler() {");
		ttprint("return "); 
			print(getHandlerTypeName(getHandler())); println(".this;");
		tprintln('}');
	}
	
	protected void generateNotStoredMethods() throws GenerationException {
		nl();
		printSingleLineComment("No objects of this class should be accessible...");
		tprintln("public final boolean isAlive() { return true; }");
		tprintln("public final boolean isStored() { return false; }");
		tprintln("public final boolean isTerminated() { return true; }");
	}
	
	protected void generateToStringMethod() throws GenerationException {
		nl();
		tprintOverride();
		tprintln("public String toString() {");
		incNbTabs();
		tprintln("return new StringBuilder()");
		ttprint(".append("); printLiteral(getIdentifier() + '('); println(')');
		Iterator<FormalVariable> variables = getFormalVariables().iterator();
		if (variables.hasNext()) do {
			printAppendVariable(variables.next());
			if (variables.hasNext())
				ttprintln(".append(\", \")");
			else
				break;
		} while (true);
		ttprintln(".append(')')");
		ttprintln(".toString();");
		closeAccolade();
	}
	
	private void printAppendVariable(FormalVariable variable) throws GenerationException {
		ttprint(".append(");
		if (variable.getType() == GenericType.getNonParameterizableInstance(String.class)) {
			print("'\"').append(");
			print(variable.getIdentifier());
			print(").append('\"'");
		} else if (variable.getType() == CHAR) {
			print("'\\'').append(");
			print(variable.getIdentifier());
			print(").append('\\''");
		} else {
			print(variable.getIdentifier());
		}
		println(')');
	}
	
	protected void generateEqualsMethods() throws GenerationException {
		nl();
		generateGenericEqualsMethod();
		nl();
		generateSpecificEqualsMethod();
	}
	
	protected void generateGenericEqualsMethod() throws GenerationException {
		tprintOverride();
		tprintSuppressUnchecked();
		tprintln("public boolean equals(Object other) {");
		ttprint("return (other instanceof "); print(getFullConstraintTypeName()); print(')');
		if (getArity() > 0) {
			nl();
			ttprint("    && this.equals(("); print(getConstraintTypeName()); print(")other)");
		}
		println(';');
		tprintln('}');
	}
	protected void generateSpecificEqualsMethod() throws GenerationException {
		tprint("public boolean equals("); print(getConstraintTypeName()); println(" other) {");
		if (getArity() > 0) {
			ttprintln("if (this == other) return true;");
			ttprint("return ");
			Iterator<FormalVariable> variables = getFormalVariables().iterator();
			do {
				final FormalVariable variable = variables.next();
				new ConjunctCodeGenerator(this, variable.getVariableType().getEq(), ConjunctCodeGenerator.GUARD) {
					@Override
					protected void printOneDummy() throws GenerationException {
						print("this."); print(variable.getIdentifier());
					}
					@Override
					protected void printOtherDummy() throws GenerationException {
						print("other."); print(variable.getIdentifier());
					}
				}.generate();
				
				if (variables.hasNext()) {
					nl(); ttprint("    && ");
				} else
					break;
			} while (true);
			println(';');
		} else {
			ttprintln("return true;");
		}
		tprintln('}');
	}
	
	protected void generateHashCode() throws GenerationException {
		nl();
		if (hasSetSemanticsLookup()) {
			tprintln("private int hashCode;");

			if (hasToBeRehashable()) {
				nl();
				tprintln("public int getRehashableKeyId() {");
				ttprintln("return ID;");
				tprintln('}');
				nl();
				tprintln("public boolean rehash() {");
	            incNbTabs();
	            tprintln("if (!alive) return false;");
				printRemoveSelfCode();
				printHashCodeComputation(); 
				for (ILookupCategory category : getConstraint().getLookupCategories()) {
					if (category.getIndexType().isSetSemantics()) {
						tprint("if (!");
						print(getHashIndexName(constraint, category));
						println(".insert(this)) {");
						incNbTabs();
						generateTerminateCode(true);
						tprintln("return false;");
						closeAccolade();
					}
				}
				tprintln("return true;");
				closeAccolade();
				nl();
				tprintln("public boolean isSuperfluous() {");
				ttprintln("return !alive;");
				tprintln('}');				
			}
			
			nl();
			tprintOverride();
			tprintln("public int hashCode() {");
			incNbTabs();
			tprintln("if (!stored) {"); 
			incNbTabs();
			printHashCodeComputation();
			tprintln("return hashCode;");
			closeAccolade();
			tprintln("return this.hashCode;");
			closeAccolade();
		} else {
			tprintOverride();
			tprintln("public int hashCode() {");
			incNbTabs();
			printHashCodeComputation(false);
			tprint("return hashCode + "); 
				printcln(getIdentifier().hashCode());
			closeAccolade();
		}
	}
	
	protected void printRemoveSelfCode() throws GenerationException {
		for (ILookupCategory category : getConstraint().getLookupCategories()) {
			if (category.getIndexType().isSetSemantics()) {
				tprint(getHashIndexName(constraint, category));
				println(".remove(this);");
				return;
			}
		}
	}
	
	protected void printHashCodeComputation() throws GenerationException {
		printHashCodeComputation(true);
	}
	protected void printHashCodeComputation(boolean assign) throws GenerationException {
		tprint("int hashCode = ");
		for (int i = 0; i < getArity(); i++) print("37 * (");
        print(23);
        for (int i = 0; i < getArity(); i++) {
            print(") + ");
            FormalVariable var = getFormalVariableAt(i);
            print(getHashCodeCode(var.getType(), var.getIdentifier()));
        }
        println(';');
        tprintln(
			"hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);",
        	"hashCode ^= (hashCode >>> 7) ^ (hashCode >>> 4);"
		);
        if (assign) tprintln("this.hashCode = hashCode;");
	}
	
	protected List<FormalVariable> getFormalVariables() {
		return getConstraint().getFormalVariables();
	}
	protected FormalVariable getFormalVariableAt(int i) {
		return getConstraint().getFormalVariableAt(i);
	}
	
	public String getConstraintTypeName() {
		return getConstraintTypeName(getConstraint());
	}
	public String getFullConstraintTypeName() {
		return getFullConstraintTypeName(getHandler(), getConstraint());
	}
	public static String getFullConstraintTypeName(Handler handler, UserDefinedConstraint constraint) {
		return getHandlerTypeName(handler) 
			+ '.'
			+ getConstraintTypeName(constraint);
	}
	public static String getConstraintTypeName(UserDefinedConstraint constraint) {
		return StringUtils.capFirst(constraint.getIdentifier()) + "Constraint";	
	}
	
	public static String getVariableGetterName(FormalVariable variable) {
		return "get" + variable.getIdentifier();
	}
	
    protected void generateHistoryMembers() throws GenerationException {
    	PROPAGATION_HISTORY_CODE_GENERATOR.setNbTabs(getNbTabs());
        PROPAGATION_HISTORY_CODE_GENERATOR.generateMembers(getConstraint(), getRules());
    }
    
	protected void generateOccurrencesCode() throws GenerationException {
		for (Occurrence occurrence : getConstraint().getPositiveOccurrences()) {
			if (occurrence.isActive()) {
				nl();
				generateOccurrenceMethod(occurrence);
			}
		}
	}
	
	protected static interface GenerationTask {
		public void generate() throws GenerationException;
	}
	protected final GenerationTask CLOSE_ACCOLADE = 
		new GenerationTask() {
			public void generate() throws GenerationException {
				closeAccolade();
			}
			@Override
			public String toString() {
				return "}";
			}
		};
	protected void generateOccurrenceMethod(final Occurrence active) throws GenerationException {
		tprint("protected final ");
		if (isRecursive()) print("Continuation "); else print("boolean ");
		print(getOccurrenceMethodName(active)); print("()");
		openAccolade();
		doGenerateOccurrenceMethod(active);
		closeAccolade();
	}
	
	protected void doGenerateOccurrenceMethod(Occurrence active) throws GenerationException {
		doGenerateOccurrenceMethod(active, -1, null);
	}
	protected void doGenerateOccurrenceMethod(
		Occurrence active,
		int continuationIndex,
		List<ContinuationArgument> declaredVariables
	) throws GenerationException {
		doGenerateOccurrenceMethod(active, continuationIndex, declaredVariables, false);
	}
	protected void doGenerateOccurrenceMethod(
		Occurrence active, 
		int continuationIndex,
		List<ContinuationArgument> declaredVariables,
		boolean hasNextTested
	) throws GenerationException {
		
		final Stack<GenerationTask> stack 
			= new Stack<GenerationTask>(active.getScheduleLength());
        final boolean continuation
            = declaredVariables != null;
		
		boolean testsPriorToBody = 
			generateScheduleCode(active, stack, declaredVariables, hasNextTested);
		
		boolean historyTest = hasToDoHistoryTest(active)
			&& (!continuation || active.hasPartners());
		
		if (historyTest) {
			PROPAGATION_HISTORY_CODE_GENERATOR.setNbTabs(getNbTabs());
			PROPAGATION_HISTORY_CODE_GENERATOR.generateNotInHistoryTest(active); 
	        openAccolade();
	        stack.push(CLOSE_ACCOLADE);
		}
		
		boolean continued = generateCommitCode(active, continuationIndex, declaredVariables);

		generatePops(stack);
		
		boolean bodyReturns = continued 
				|| active.getBody().endsWithFailure()
				|| active.getType() == REMOVED;

		if (testsPriorToBody || historyTest || !bodyReturns)
			tprintLiveContinuation();
	}
	
	protected static class DifferentPartnersTestChecker extends AbstractScheduleVisitor {
		private boolean result;
		final private Occurrence active;
		final private PartnerConstraintsStore partners;
		
		public DifferentPartnersTestChecker(Occurrence active) {
			this.active = active;
			this.partners = new PartnerConstraintsStore(active);
		}
		
		@Override
		public void visit(Lookup lookup) throws Exception {
			Occurrence partner = lookup.getOccurrence();
			String constraintId = partner.getIdentifier(),
				occurrenceId = getOccurrenceName(partner);
			result = result || (
				( partner.isStored() || partner.isReactive() )
					&& !isEmpty(partners.getIdentifiers(constraintId))
			);
			partners.add(constraintId, occurrenceId);
		}
		
		@Override
		public void reset() throws Exception {
			super.reset();
			result = false;
			partners.reset(active.getIdentifier());
		}
		
		public boolean getResult() {
			return result;
		}
		
		public static boolean needsDifferentPartnersTest(Occurrence active) {
			try {
				DifferentPartnersTestChecker checker = new DifferentPartnersTestChecker(active);
				active.accept(checker);
				return checker.getResult();
			} catch (Exception e) {
				e.printStackTrace();
				throw new InternalError();
			}
		}
	}
	
	static boolean isEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}
	
	protected static class GuardChecker extends AbstractScheduleVisitor {
		private boolean result;
		private boolean pastFirst;
		
		public GuardChecker(boolean ignoreBeforeFirst) {
			pastFirst = !ignoreBeforeFirst;
		}
		
		@Override
		public void visit(IGuardConjunct guard) throws Exception {
			result = pastFirst;
		}
		@Override
		public void visit(Lookup lookup) throws Exception {
			pastFirst = true;
		}
		
		public boolean getResult() {
			return result;
		}
		
		@Override
		public void reset() throws Exception {
			super.reset();
			pastFirst = result = false;
		}
		
		public static boolean isGuarded(IScheduleVisitable schedule, boolean ignoreBeforeFirst) {
			try {
				GuardChecker checker = new GuardChecker(ignoreBeforeFirst);
				schedule.accept(checker);
				return checker.getResult();
			} catch (Exception e) {
				throw new InternalError();
			}
		}
	}
	
	protected void generatePops(Stack<GenerationTask> stack) 
	throws GenerationException {
		generatePops(stack, 0);
	}
	protected void generatePops(Stack<GenerationTask> stack, int until) 
	throws GenerationException {
		int n = stack.size();
		while (n-->until) stack.pop().generate();
	}

	
	/**
	 * @param hasNextTested
	 * 	 <code>hasNextTested == true</code> implies there 
	 * 	is one only one non-singleton lookup, and that <code>hasNext()</code> 
	 * 	was tested safely for this lookup 
	 * @return whether or not one or more tests precede the execution of the body.
	 */
	protected boolean generateScheduleCode(
		Occurrence active, 
		Stack<GenerationTask> stack,
        List<ContinuationArgument> declaredVariables,
        boolean hasNextTested
	) throws GenerationException {
		return generateScheduleCode(
            active, 
            active, 
            new PartnerConstraintsStore(active), 
            stack,
            declaredVariables,
            getLabelInfo(active),
            hasNextTested
        );
	}
	protected void generateScheduleCode(
		NegativeHead negativeHead,
		Occurrence active,
		PartnerConstraintsStore partnerConstraints,
		Stack<GenerationTask> stack
	) throws GenerationException {
		generateScheduleCode(
			negativeHead, 
			active, 
			partnerConstraints, 
			stack, 
			null, 
			null, 
			false
		);
	}
	
	/**
	 * @param hasNextTested
	 * 	 <code>hasNextTested == true</code> implies there 
	 * 	is one only one non-singleton lookup, and that <code>hasNext()</code> 
	 * 	was tested safely for this lookup 
	 * @return whether or not one or more tests precede the execution of the body.
	 */
	protected boolean generateScheduleCode(
		final IScheduled scheduled, 
		final Occurrence active, 
		final PartnerConstraintsStore partnerConstraints, 
		final Stack<GenerationTask> stack,
		final List<ContinuationArgument> declaredVariables,
		final Iterator<Boolean> labelInfo,
		final boolean hasNextTested
	) throws GenerationException {
		try {
			final boolean 
            	continuation = declaredVariables != null,
            	_addFirstCode = continuation && active.getNbPartners() > 1;
		
			if (_addFirstCode) tprintln("boolean first = true;");
			final ListIterator<IVariableInfo> variableInfos =
				scheduled.getVariableInfos().listIterator();
			
			boolean temp = active.isReactive() || active.isStored();
			if (!temp) for (Occurrence occurrence : active.getPreviousActivePositiveOccurrences())
				if (occurrence.isStored()) { temp = true; break; }
			final boolean activeMayBeStoredBefore = temp;
					
			class ScheduleCodeGenerater extends AbstractScheduleVisitor {
				String previousLabel = null;
				boolean first = true;
                boolean addFirstCode = _addFirstCode;
                boolean universal = active.getType() == KEPT;
                boolean result = false;
				
                public boolean getResult() {
                	return result;
				}
                
				@Override
                public void visit(NegativeHead negativeHead) throws GenerationException {
					printDeclarations();
					
					if (!continuation || !first) {
						if (addFirstCode)
							tprint("if (!first) ");
						else
							printTabs();
						
						println('{'); incNbTabs();
						partnerConstraints.save();
						int n = stack.size();
						
						generateScheduleCode(negativeHead, active, partnerConstraints, stack);
						if (previousLabel == null || hasNextTested) {
							tprintLiveContinuation();
						} else {
							tprint("continue "); print(previousLabel); println(';');
						}
						
						generatePops(stack, n);
						partnerConstraints.restore();
						closeAccolade();
					}
				}
			
				@Override
                public void visit(IGuardConjunct guard) throws GenerationException {
					printDeclarations();
					
					if (!continuation || !first) { 
                        tprint("if (");
                        if (addFirstCode) print("first || ");
                        new ConjunctCodeGenerator(ConstraintCodeGenerator.this, guard, GUARD).generate();
                        print(')');
                        openAndCloseAccolades();
                        
                        result = true;
                    }
				}
				
				protected void tprintNotFirstTest() throws GenerationException {
					if (addFirstCode) tprint("if (!first) "); else printTabs();
				}
				protected void printNextTest(Lookup lookup) throws GenerationException {
					Occurrence partner = lookup.getOccurrence();
					if (isSSLookup(lookup)) {
						print(getOccurrenceName(partner)); print(" != null)");
					} else {
						print(getIteratorName(partner)); print(".hasNext())");
					}
					result = true;

					openAndCloseAccolades();
				}
				protected void printIfNotNextTest(Lookup lookup) throws GenerationException {
					tprint("if (");
					Occurrence partner = lookup.getOccurrence();
					if (isSSLookup(lookup)) {
						print(getOccurrenceName(partner)); print(" == null)");
					} else {
						print('!'); print(getIteratorName(partner)); print(".hasNext())");
					}
				}
			
				@Override
                public void visit(Lookup lookup) throws GenerationException {
					printDeclarations();
					
					final String constraintType = getConstraintTypeName(lookup.getConstraint());
					final Occurrence partner = lookup.getOccurrence();
					final String iter = getIteratorName(partner);
					final String iterable = getIterableName(partner);
					final String name = getOccurrenceName(partner);
					final boolean ss = isSSLookup(lookup);
					boolean nextTested = hasNextTested;
                    
                    // declare the iteratables and iterators, and do the lookups:
					if (!ss && (!continuation || !first)) {
						tprint(ConstraintIterable.class.getCanonicalName());
							print('<'); print(constraintType); print("> "); 
						printcln(iterable);
						
						tprint(iterable); print(" = ");
						if (addFirstCode) print("first? null : ");
						printLookup(lookup);
						
						if (lookup.getLookupCategory().getIndexType().mayReturnNull()) {						
							if (addFirstCode)
								tprint("if (first || ");
							else
								tprint("if (");
							result = true;
						
							print(iterable); print(" != null)");
							openAndCloseAccolades();
						}
						
						if (!continuation || !includeIteratorInContinuation(active, lookup)) {
                            tprint("Iterator<"); print(constraintType); print("> ");
                            printcln(iter);
                        }
						
						tprintNotFirstTest();
						print(iter); print(" = "); print(iterable); print('.');
						if (universal)
							print(mayBeRemoved(partner)? "universal" : "semiUniversal");
						else
							print("existential");
						println("Iterator();");
						
						if (partner.getType() == REMOVED) universal = false;
					}
					
					if (addFirstCode && getLastNonSSHashMapLookup(scheduled) == lookup) {
                        tprintln("first = false;");
                        addFirstCode = false;
                    }
					
					// declare the current constraint if necessary:
					if (!continuation || !includeCurrentInContinuation(active, lookup)) {
						tprint(constraintType);
						print(' ');
						print(name);
						if (addFirstCode) print(" = null");							
						println(';');
					}
					
					// do the set semantics lookup (always if ss)
					if (ss) {
						tprintNotFirstTest();
						print(name); print(" = "); printLookup(lookup);
					}
					
					boolean moreTests =
						getLastScheduleElementOf(scheduled) != lookup
						    || isNeededInDifferentPartnersTest(partner);
					
					if (scheduled instanceof Occurrence) {
						if (addFirstCode && mayBeRemoved(partner)) {
							tprint("if (!"); printAliveTest(partner); println(") first = false;");
							result = true;
						}
						
						if (labelInfo.next()) {
							tprint(previousLabel = getLabel(partner));
							print(": ");
						} else
							printTabs();
						
						Lookup seed;
						if (!first && (seed = getLastSeedingLookup(scheduled, lookup)) != getPreviousLookup(scheduled, lookup)) {
							printIfNotNextTest(lookup);
							incNbTabs();
							if (seed == null)
								tprintLiveContinuation();
							else
								printContinueLabel(active, seed);
							decNbTabs();
							nextTested = true;
						}

						if (nextTested && !ss) {
							println("do {");
							incNbTabs();
							stack.push(new GenerationTask() {
								public void generate() throws GenerationException {
									decNbTabs();
									tprint("} while (");
									print(iter); println(".hasNext());");
								}
								@Override
								public String toString() {
									return "} while (" + iter + ".hasNext());";
								}
							});
						} else if (!nextTested) {
							if (ss)
								print("if (");
							else
								print("while (");
							
							if (addFirstCode) print("first || ");
							printNextTest(lookup);
						}
					} else {	// scheduled instanceof NegativeHead
						if (moreTests && !ss)
							tprint("while (");
						else
							tprint("if (");
						
						printNextTest(lookup);
					}
					
					if (!ss && (scheduled instanceof Occurrence || moreTests)) {
						tprintNotFirstTest();
						print(name); print(" = "); print(iter); println(".next();");
					}
					
					printDifferentPartnersTest(partner);
					
                    first = false;
				}
				
				/*
			 		When do we need to do a different partner's test?
			 			--> if it is possible the new partner is already part of the partial match
			 			--> the active constraint can be excluded if it is certainly not stored yet 
				*/
				protected boolean isNeededInDifferentPartnersTest(Occurrence partner) {
					String id = partner.getIdentifier();
					List<String> ids = partnerConstraints.getIdentifiers(id);
					if (ids == null) return false;
					int n = ids.size();
					if (!activeMayBeStoredBefore && id.equals(active.getIdentifier())) n--;
					return n > 0;
				}
				protected void printDifferentPartnersTest(Occurrence partner) throws GenerationException {
					final String 
						constraintId = partner.getIdentifier(),
						occurrenceId = getOccurrenceName(partner);
					
					List<String> identifiers = partnerConstraints.getIdentifiers(constraintId);
					
					int size; 
					if (identifiers != null && (size = identifiers.size()) != 0) {
						boolean excludeActive = 
							!activeMayBeStoredBefore && constraintId.equals(active.getIdentifier());
						if (!excludeActive || --size > 0) {
							tprint("if (");
							if (addFirstCode) print("first || (");
							if (size > 2 && activeMayBeStoredBefore) print("!stored || (");
							boolean first = true;
							for (int i = 0; i < identifiers.size(); i++) {
								String theId = identifiers.get(i);
								if (excludeActive && theId.equals("this")) continue;
								if (first) first = false; else print(" && ");
								print(theId); print(" != "); print(occurrenceId);
							}
							if (size > 2 && activeMayBeStoredBefore) print(')');
							if (addFirstCode) print(')');
							print(')');
							
							result = true;
	
							openAndCloseAccolades();
						}
					}
					
					partnerConstraints.add(constraintId, occurrenceId);
				}
				
				int index = 0;
				protected void printDeclarations() throws GenerationException {
					ConstraintCodeGenerator.this.printDeclarations(
                        active, variableInfos, index, 
                            declaredVariables, addFirstCode, first
                        );
					index++;
				}
				
				protected void openAndCloseAccolades() throws GenerationException {
					println(" {");
					incNbTabs();
					scheduleCloseAccolade();
				}
				protected void scheduleCloseAccolade() {
					stack.push(CLOSE_ACCOLADE);
				}
			}
			
			ScheduleCodeGenerater generator = new ScheduleCodeGenerater();
			scheduled.accept(generator);
			return generator.getResult();
			
		} catch (Exception x) {
			if (x instanceof GenerationException)
				throw (GenerationException)x;
			else
				throw new GenerationException(x);
		}
	}
	
	void printDeclarations(
		Occurrence active, List<ContinuationArgument> declaredVariables
	) throws GenerationException {
		printDeclarations(
			active, 
			active.getVariableInfos().listIterator(), 
			active.getScheduleLength(), 
			declaredVariables, 
			false,
			false
		);
	}
	void printDeclarations(
        Occurrence active, 
        ListIterator<IVariableInfo> variableInfos, 
        int index, 
        List<ContinuationArgument> declaredVariables,
        boolean printFirstCode,
        boolean first
    ) throws GenerationException {
        
		while (variableInfos.hasNext()) {
			IVariableInfo varInfo = variableInfos.next();
			if (varInfo.getDeclarationIndex() < index) continue;

			if (varInfo.getDeclarationIndex() == index) {
            	boolean declaring = haveToDeclare(varInfo, declaredVariables);
            	
            	if (declaring || !first) {
            		printTabs();
            		
	                if (declaring) {
	                    print(varInfo.getFormalVariable().getTypeString());
	                    print(' ');
	                } else if (printFirstCode/* && !first*/) {
	                	// only test if not declaring, as we'd have to assign null anyway)
	            		print("if (!first) ");
	            	}
                
	                printDeclaration(active, varInfo, false);
                }
			} else {
				variableInfos.previous();
				break;
			}
		}
	}
	void printLookup(Lookup lookup) throws GenerationException {
		print(getLookupMethodName(lookup));
		new ArgumentCodeGenerator(this, lookup).generate();
		println(';');
	}
	
	protected void tprintDeclaration(Occurrence active, IVariableInfo varInfo) throws GenerationException {
		printTabs();
		printDeclaration(active, varInfo);
	}
	protected void printDeclaration(Occurrence active, IVariableInfo varInfo) throws GenerationException {
		printDeclaration(active, varInfo, true);
	}
	protected void tprintDeclaration(Occurrence active, IVariableInfo varInfo, boolean includeType) throws GenerationException {
		printTabs();
		printDeclaration(active, varInfo, includeType);
	}
	protected void printDeclaration(Occurrence active, IVariableInfo varInfo, boolean includeType) throws GenerationException {
		if (includeType) {
			print(varInfo.getFormalVariable().getTypeString());
			print(' ');
		}
		
		print(varInfo.getActualVariable().getIdentifier());
		print(" = ");
		
		Occurrence occurrence = varInfo.getDeclaringOccurrence();
		print(getOccurrenceName(occurrence, active));
		
		print('.');
		print(varInfo.getFormalVariable().getIdentifier());
		println(';');
	}
	
	protected void printCommitDeclarations(Occurrence active, List<ContinuationArgument> declaredVariables, Body restBody) throws GenerationException {
		Set<Variable> bodyVars = VariableCollector.collectVariables(restBody);
		for (IVariableInfo varInfo : active.getSchedule().getVariableInfos()) {
			if (varInfo.getDeclarationIndex() != 0) return;
			if (haveToDeclare(varInfo, declaredVariables) && bodyVars.contains(varInfo.getActualVariable()))
				tprintDeclaration(active, varInfo);
		}
	}
	
	protected static Lookup getLastSeedingLookup(IScheduleVisitable scheduled, final Lookup lookup) {
		try {
			class LastSeedingLookupGetter extends AbstractScheduleVisitor {
				public Lookup lastLookup;
				private boolean looking;
				
				@Override
				public void visit(Lookup visited) throws Exception {
					if (looking) {
						if (visited == lookup)
							looking = false;
						else if (lookup.isSeededBy(visited.getOccurrence()))
							lastLookup = visited;
					}
				}
			}
			
			LastSeedingLookupGetter getter = new LastSeedingLookupGetter();
			scheduled.accept(getter);
			return getter.lastLookup;
			
		} catch (Exception x) {
			throw new InternalError();
		}
	}
	protected static Lookup getPreviousLookup(IScheduleVisitable scheduled, final Lookup lookup) {
		try {
			class PreviousLookupGetter extends AbstractScheduleVisitor {
				public Lookup previousLookup;
				private boolean looking;
				
				@Override
				public void visit(Lookup visited) throws Exception {
					if (looking) {
						if (visited == lookup)
							looking = false;
						else
							previousLookup = visited;
					}
				}
			}
			
			PreviousLookupGetter getter = new PreviousLookupGetter();
			scheduled.accept(getter);
			return getter.previousLookup;
			
		} catch (Exception x) {
			throw new InternalError();
		}
	}
	

	protected static Lookup getLastNonSSHashMapLookup(IScheduleVisitable scheduled) {
		try {
			class LastNonSSHashMapLookupGetter extends AbstractScheduleVisitor {
				public Lookup lastLookup;
				
				@Override
				public void visit(Lookup lookup) throws Exception {
					if (!isSSLookup(lookup)) lastLookup = lookup;
				}
			}
			
			LastNonSSHashMapLookupGetter getter = new LastNonSSHashMapLookupGetter();
			scheduled.accept(getter);
			return getter.lastLookup;
			
		} catch (Exception x) {
			throw new InternalError();
		}
	}
    
	protected static boolean isUsed(IVariableInfo variableInfo, SortedSet<Variable> usedVariables) {
		return (usedVariables == null)
			|| usedVariables.contains(variableInfo.getActualVariable());
	}
	
    protected static boolean haveToDeclare(
        IVariableInfo variableInfo, List<ContinuationArgument> declaredVariables
    ) {
        return declaredVariables == null
            || !declaredVariables.contains(new VariableInfoContinuationArgument(variableInfo));
    }
    
    public static String getIteratorName(Occurrence occurrence) {
		return getOccurrenceName(occurrence) + "_iter";
	}
    public static String getIterableName(Occurrence occurrence) {
		return getOccurrenceName(occurrence) + "_lookup";
	}
	public static String getLabel(Occurrence occurrence) {
		return getOccurrenceName(occurrence) + "_label";
	}
	public static String getOccurrenceMethodName(Occurrence occurrence) {
		return getOccurrenceName(occurrence);
	}
	public static String getOccurrenceName(Occurrence occurrence) {
		StringBuilder result = new StringBuilder();
		int num = occurrence.getConstraintOccurrenceNbr();
		if (occurrence.getType() == NEGATIVE) {
			result.append("not_");
			num = -num;
		}
		return result.append(occurrence.getIdentifier()).append('_').append(num).toString();
		
	}
    
    protected static String getOccurrenceName(Occurrence occurrence, Occurrence active) {
        return occurrence.equals(active)? "this" : getOccurrenceName(occurrence);
    }
	
	protected boolean generateCommitCode(
        Occurrence active,
        int continuationIndex,
        List<ContinuationArgument> declaredVariables
    ) throws GenerationException {
		final Rule rule = active.getRule();
        final boolean continuation = declaredVariables != null;
        
        printDeclarations(active, declaredVariables);
        
        if (hasToTrace()) printRuleFire_('s', active);
        
        boolean store = !continuation && active.isStored();
        if (store) {
        	// it has to be stored if this rule fires,
        	// but if firing this body always returns (possibly with continuation)
        	// this can also not be the case...
        	boolean mayBePreviouslyStored = active.isReactive() || !alwaysReturns(rule.getBody());
        	if (!mayBePreviouslyStored)
        		for (Occurrence previous : active.getPreviousActivePositiveOccurrences())
        			if (previous.isStored()) {
        				mayBePreviouslyStored = true;
        				break;
        			}
            printStoreCode(mayBePreviouslyStored, active);
            nl();
        }
        
        if (rule.getType() == PROPAGATION) {
        	if (rule.needsHistory()) {
            	PROPAGATION_HISTORY_CODE_GENERATOR.setNbTabs(getNbTabs());
    			PROPAGATION_HISTORY_CODE_GENERATOR.generateAddToHistory(active);
            }
        } else {
            for (Occurrence occurrence : active.getHead().getOccurrences(REMOVED)) {
            	if (occurrence == active && !mayBeStored()) continue;
                tprint(getOccurrenceName(occurrence, active));
                println(".terminate();");
            }
        }
        
        return generateCommitBody(active, continuationIndex, declaredVariables);
    }
	
	/**
	 * @return <code>true</code> iff push*-return was done
	 */
	protected boolean generateCommitBody(
        Occurrence active,
        int continuationIndex,
        List<ContinuationArgument> declaredVariables
    ) throws GenerationException {
		return generateCommitBody(active, active.getBody(), 0, continuationIndex, declaredVariables);
	}
	/**
	 * @return <code>true</code> iff push*-return was done 
	 */
	protected boolean generateCommitBody(
        Occurrence active, 
        Body restBody, 
        int baseIndex,
        int continuationIndex,
        List<ContinuationArgument> declaredVariables
    ) throws GenerationException {
        final int index = getContinuationIndex(restBody);
        final boolean continuation = declaredVariables != null; 
        
        final boolean doGeneration = doGenerationOptimization(active);
        boolean setReactivatedFalse = doGeneration && baseIndex == 0; 
        
        if (continuation && baseIndex > 0)
        	printCommitDeclarations(active, declaredVariables, restBody);
        
        if (index < 0) {
        	if (setReactivatedFalse)
            	tprintln("reactivated = false;");
        	
        	getCommitBodyConjunctCodeGenerator(active, restBody, baseIndex, declaredVariables).generate();
            
            if (! restBody.endsWithFailure()) {
            	if (hasToTrace()) printRuleFire_('d', active);
            	
                if (active.getType() == REMOVED) {
                    tprintDeadContinuation();
                    return true;
                } else {	// active.getType() == KEPT
                	printConditionalDeadContinuations(active, continuation);
                    
                    if (baseIndex == 0) {	// only if body is inside (nested) loops
                    	List<Lookup> lookups = getLookupsOf(active);
                    	Iterator<Lookup> iter = lookups.iterator();
                    
                    	if (iter.hasNext()) do {
	                        Lookup lookup = iter.next();
	                        Occurrence occurrence = lookup.getOccurrence();
	                        if (!iter.hasNext()) break;
	                        if (occurrence.getType() == REMOVED) {
	                            printContinueLabel(lookup, lookups);
	                            break;
	                        } else if (mayBeRemoved(occurrence)) {
	                            tprint("if (!"); printAliveTest(occurrence); println(')');
	                            incNbTabs();
	                            printContinueLabel(lookup, lookups);
	                            decNbTabs();
	                        }
	                    } while (true);
                    }
                }
            }
            
            return false;
            
        } else {
            if (index != 0) {
            	if (setReactivatedFalse) {
            		tprintln("reactivatied = false;");
            		setReactivatedFalse = false;
            	}
            	getCommitBodyConjunctCodeGenerator(
                    active, restBody.getSubConjunction(0, index), baseIndex, declaredVariables
                ).generate();
            }
            
            generatePushContinuations(active, continuationIndex, baseIndex+index, setReactivatedFalse);
            printReturnContinuation(restBody, index);
            
            return true;
        }
	}
	
	protected void printAliveTest(Occurrence occurrence) throws GenerationException {
		print(getOccurrenceName(occurrence));
    	print(occurrence.getConstraint() == getConstraint()? ".alive" : ".isAlive()");
	}
	
	protected boolean doGenerationOptimization() {
		if (getOptions().doGenerationOptimization())
			for (Occurrence occurrence : getConstraint().getPositiveOccurrences())
				if (occurrence.isActive() && doGenerationOptimization(occurrence))
					return true;
		return false;
	}
	protected boolean doGenerationOptimization(Occurrence active) {
		return getOptions().doGenerationOptimization()
			&& active.isStored()	// if not stored: cannot be reactivated by body
        	&& active.getType() == KEPT
        	&& active.getConstraint().isReactive()
        	&& !active.getBody().endsWithFailure()
        	&& noNonReactiveOccurrencesRemaining(active)	// cf below
        	&& !JCHRFreeAnalysor.isJCHRFree(active.getBody());	// XXX more specific: body can reactivate active constraint
	}
	/* otherwise generation optimization not correct! Example:
 		a(X), a(X) ==> X = foo. 
  		a(_) ==> bar.
  	
  		de occurrence in de laatste regel is niet reactive meer,
  		maar generation zal zorgen dat voor ?- a(X), a(X)
  		het resultaat bar is, in plaats van bar, bar!
  		
  		XXX: we could, if generation occurs in such a case, only do the
  			remaining non-reactive occurrences (i.e. return NonReactive...(...); )
	*/
	protected static boolean noNonReactiveOccurrencesRemaining(Occurrence active) {
		if (!active.isReactive()) return false;
		for (Occurrence occurrence : active.getRemainingActivePositiveOccurrences())
			if (!occurrence.isReactive()) return false;
		return true;
	}
	
	/**
	 * Tests whether generation and alive tests have to be performed after the 
	 * execution of a given body conjunction.
	 */
	protected boolean doTestsAfter(Body body) {
		try {
			GenerationTestBodyVisitor visitor = new GenerationTestBodyVisitor();
			body.accept(visitor);
			return visitor.getResult();
		} catch (Exception e) {
			throw new InternalError();
		}
	}
	protected class GenerationTestBodyVisitor extends AbstractBodyVisitor {
		private boolean result;

		@Override
		public void visit(UserDefinedConjunct conjunct) {
			// if the constraint is recursive, a push-pop sequence will be done
			if (!result && !isRecursive(conjunct.getConstraint()))
				result = true;
		}

		@Override
		protected void visit(IConjunct conjunct) throws Exception {
			// exclude the cases where a swap is performed:
			if (!result 
				&& conjunct instanceof IBuiltInConjunct<?>
				&& !((IBuiltInConjunct<?>)conjunct).warrantsStackOptimization()
				&& !reactivationMayCauseRecursion()
			)
				result = true;
		}
		
		public boolean getResult() {
			return result;
		}
	}
	
	
	void printContinueLabel(Occurrence active, Lookup lookup) throws GenerationException {
		printContinueLabel(lookup, getLookupsOf(active));
	}
	/**
	 * @see #getLabelInfo(Occurrence) 
	 */
	void printContinueLabel(Lookup lookup, List<Lookup> lookups) throws GenerationException {
		if (isSSLookup(lookup)) {
			int i = lookups.size();
			while (lookups.get(--i) != lookup);
			
			while (--i >= 0) if (!isSSLookup(lookup = lookups.get(i))) break;
			
			if (i < 0) {
				tprintLiveContinuation();
				return;
			}
		}
		
		tprint("continue ");
		printcln(getLabel(lookup.getOccurrence()));
	}
	
	protected static boolean isSSLookup(Lookup lookup) {
		return lookup.getLookupCategory().getIndexType().isSetSemantics();
	}
	
	private void printRuleFire_(char X, Occurrence active) throws GenerationException {
		printRuleFire_(X, active, false);
	}
	private void printRuleFire_(char X, Occurrence active, boolean continuation) throws GenerationException {
		if (X == 'd') nl();
		tprintln("if (tracer != null)");
        ttprint("tracer.fire"); print(X); print('(');
        printLiteral(active.getRule().getIdentifier());
        print(", ");
        print(active.getOccurrenceIndex());
        for (Occurrence occurrence : active.getHead()) {
            print(", ");
            if (occurrence == active) {
            	if (continuation) {
            		print(getConstraintTypeName());
            		print('.');
            	}
            	print("this");
            } else {
                print(getOccurrenceName(occurrence));
            }
        }
        println(");");
        if (X == 's') nl();
	}
	
	protected void printPushRuleFiredContinuation(Occurrence active, boolean suspendAfter) throws GenerationException {
		printPush(getRuleFiredContinuationCode(active, suspendAfter));
	}
	protected String getRuleFiredContinuationCode(Occurrence active, boolean suspendAfter) {
		scheduleContinuation(new RuleFiredContinuationInfo());

		StringBuilder result = new StringBuilder()
			.append("new ")
			.append(getRuleFiredContinuationType())
			.append("(\"")
			.append(active.getRule().getIdentifier())
			.append("\", ")
			.append(active.getOccurrenceIndex())
			.append(", ")
			.append(suspendAfter);
		Occurrence[] occurrences = active.getHead().getOccurrencesArray();
		for (int i = 0; i < occurrences.length; i++) {
			result.append(", ")
				.append(getOccurrenceName(occurrences[i], active));
		}
		return result.append(')').toString();
	}

	/**
	 * @see #printContinueLabel(Lookup, List) 
	 */
	private Iterator<Boolean> getLabelInfo(Occurrence active) {
		List<Lookup> lookups = getLookupsOf(active);
		int nbLookups = lookups.size();
		Boolean[] needsLabel = new Boolean[nbLookups];
		boolean[] ssLookup = new boolean[nbLookups];
		for (int i = 0; i < nbLookups; i++) {
			Lookup lookup = lookups.get(i);
			needsLabel[i] = needsLabelOld(active, lookup);
			ssLookup[i] = isSSLookup(lookup);
		}
		// the old label info does not take into account ss lookups: 
		for (int i = nbLookups-1; i >= 0; i--) {
			if (ssLookup[i]) {
				needsLabel[i] = FALSE;
				while (--i >= 0) {
					if (ssLookup[i]) {
						needsLabel[i] = FALSE;
					} else {	// !ssLookup[i]
						needsLabel[i] = TRUE;
						break;	// will do i-- next!
					}
				}
			}
		}
		return new ArrayIterator<Boolean>(needsLabel);
	}
	private boolean needsLabelOld(final Occurrence active, final Lookup theLookup) {
		try {
			final class NeedsLabelScheduleVisitor extends AbstractScheduleVisitor {
				private Lookup previous;
				private boolean negHead, 
					afterLookup, 
					seedingJump;
				
				private boolean noJumpAfterCommit = active.getType() == REMOVED 
					|| alwaysReturns(active.getBody())
					|| ( 
						theLookup.getOccurrence().getType() == KEPT
							&& !mayBeRemoved(theLookup.getOccurrence())
					);
					
				@Override
				public void visit(NegativeHead negativeHead) {
					if (previous == theLookup) negHead = true;
				}
				
				@Override
				public void visit(Lookup lookup) {
					afterLookup = afterLookup || (previous == theLookup);
					if (afterLookup) {
						seedingJump = seedingJump 
							|| getLastSeedingLookup(active, lookup) == theLookup;
					} else {
						noJumpAfterCommit = noJumpAfterCommit
							|| (lookup != theLookup && lookup.getOccurrence().getType() == REMOVED);
					}
					
					previous = lookup;
				}
				
				public boolean getResult() {
					return negHead
						|| ( afterLookup && !noJumpAfterCommit )
						|| seedingJump;
				}
			}
			
			NeedsLabelScheduleVisitor visitor = new NeedsLabelScheduleVisitor();
			active.accept(visitor);
			return visitor.getResult();
			
		} catch (Exception x) {
			x.printStackTrace();
			throw new InternalError();
		}
	}
	
	protected boolean mayBeRemoved() {
		return getConstraint().mayBeRemoved();
	}
	protected boolean mayBeRemoved(Occurrence occurrence) {
		return mayRemove(occurrence.getBody(), occurrence);
	}
	protected boolean mayRemove(Body body, Occurrence occurrence) {
		return RemovalTester.mayRemove(body, occurrence, getCHRIntermediateForm());
	}
	
	protected CommitBodyConjunctCodeGenerator getCommitBodyConjunctCodeGenerator(
		Occurrence active,
        IConjunction<? extends IConjunct> conjunction,
        int baseIndex,
        List<ContinuationArgument> variables
    ) {
		if (getOptions().doStackOptimizations())
			return new OptimizedCommitBodyConjunctCodeGenerator(active, conjunction, baseIndex, variables);
		else
			return new CommitBodyConjunctCodeGenerator(conjunction, variables);
	}
	
	protected class CommitBodyConjunctCodeGenerator extends ConjunctCodeGenerator {
		public CommitBodyConjunctCodeGenerator(
            IConjunction<? extends IConjunct> conjunction,
            List<ContinuationArgument> variables
        ) {
            super(ConstraintCodeGenerator.this, conjunction, BODY);
            this.variables = variables;
		}
		
		private List<ContinuationArgument> variables;
		
		@Override
        protected boolean haveToDeclare(AssignmentConjunct conjunct) {
            return super.haveToDeclare(conjunct)
                && (variables == null || !variables.contains(new VariableContinuationArgument(conjunct)));
        }
	}
	protected class OptimizedCommitBodyConjunctCodeGenerator extends CommitBodyConjunctCodeGenerator {
		public OptimizedCommitBodyConjunctCodeGenerator(
			Occurrence active,
            IConjunction<? extends IConjunct> conjunction,
            int baseIndex,
            List<ContinuationArgument> variables
        ) {
            super(conjunction, variables);
            this.active = active;
            this.index = baseIndex;
        }
		private Occurrence active;
		private int index;
		
		@Override
		protected void doGenerate() throws GenerationException {
			super.doGenerate();
			exitHostLanguageMode();
		}
		
		@Override
		public void visit(UserDefinedConjunct conjunct) throws GenerationException {
			exitHostLanguageMode();
			super.visit(conjunct);
			index++;
		}
		@Override
		public void visit(Failure conjunct) throws GenerationException {
			super.visit(conjunct);
			index++;
		}
		
		// deal with built-in constraints \\
		
        private final boolean reactivationMayCauseRecursion 
    		= reactivationMayCauseRecursion();

		@Override
        public void visit(AbstractMethodInvocation<?> conjunct) throws GenerationException {
			boolean isBuiltIn = false, isHostLanguage = false;
			
        	if (conjunct instanceof IBuiltInConjunct<?>) {
        		IBuiltInConjunct<?> builtin = ((IBuiltInConjunct<?>)conjunct);
        		if (!hasJCHRFreeArguments(builtin))
        			isHostLanguage = true;
        		else if (builtin.warrantsStackOptimization() || reactivationMayCauseRecursion)
    				isBuiltIn = true;
        	} else if (!isJCHRFree(conjunct, false)) {
        		isHostLanguage = true;
        	}
        			
    		if (isHostLanguage) {
    			beforeHostLanguageStatement();
        		super.visit(conjunct);
        		afterHostLanguageStatement();
    		} else if (isBuiltIn) {
    			exitHostLanguageMode();
				beforeBuiltInConstraint();
				super.visit(conjunct);
				afterBuiltInConstraint();
    		} else {
    			super.visit(conjunct);
    		}

        	index++;
        }

        @Override
        public void visit(NoSolverConjunct conjunct) throws GenerationException {
        	if (hasJCHRFreeArguments((IBasicArgumented)conjunct)) {
        		beforeHostLanguageStatement();
        		super.visit(conjunct);
        		afterHostLanguageStatement();
        	} else if (reactivationMayCauseRecursion) {
        		exitHostLanguageMode();
    			beforeBuiltInConstraint();
    			super.visit(conjunct);
    			afterBuiltInConstraint();
        	} else {
        		super.visit(conjunct);
        	}

        	index++;
        }
        
        private boolean sizeSaved;
        
        protected boolean bodyRemaining() {
        	return active.getBody().getLength() > index + 1;
        }
        
        protected boolean haveToPushExtraContinuation() {
        	return bodyRemaining() || (
    			active.getType() == KEPT && (
    					active.looksUpNonSingletonPartners() 
    						|| !active.isLastActiveOccurrence()
    				)
        		);
        }
        
        protected void beforeBuiltInConstraint() throws GenerationException {
        	if (!sizeSaved && haveToPushExtraContinuation()) {
    			println("final int $tack$ize = $$continuationStack.getSize();");
    			printTabs();
    			sizeSaved = true;
        	}
        }
        protected void afterBuiltInConstraint() throws GenerationException {
        	if (haveToPushExtraContinuation()) {
        		println(';');
        		assert sizeSaved;
	    		tprintln("if ($$continuationStack.getSize() != $tack$ize)");
	    		ttprint("return $$continuationStack.replace($tack$ize, ");
	    		if (bodyRemaining() || active.looksUpNonSingletonPartners())
	    			print(getContinuationCode(active, index, false));
	    		else	// active.getType() == KEPT && !active.isLastActiveOccurrence()
	    			print(getNextOccurrenceContinuationCode(active));
	    		print(')');
        	}
        }
        
        @Override
		public void visit(AssignmentConjunct conjunct) throws GenerationException {
        	boolean JCHRFree = isJCHRFree(conjunct, false);
        	if (!JCHRFree) beforeHostLanguageStatement();
        	super.visit(conjunct);
        	if (!JCHRFree) afterHostLanguageStatement();
        	index++;
		}
		@Override
		public void visit(ConstructorInvocation conjunct) throws GenerationException {
			boolean JCHRFree = isJCHRFree(conjunct, false);
        	if (!JCHRFree) beforeHostLanguageStatement();
			super.visit(conjunct);
        	if (!JCHRFree) afterHostLanguageStatement();
			index++;
		}
		
		protected boolean hostLanguageMode;
		
		protected void beforeHostLanguageStatement() throws GenerationException {
			if (!hostLanguageMode) {
				println("enterHostLanguageMode();");
				hostLanguageMode = true;
				printTabs();
			}
		}
		protected void afterHostLanguageStatement() throws GenerationException {
			println(';');
			assert hostLanguageMode;
			
			tprintln("if (getConstraintSystem().hasQueued())");
			ttprint("return dequeue(");
			
			if (bodyRemaining() || (active.getType() == KEPT && active.looksUpNonSingletonPartners())) {
    			print(getContinuationCode(active, index, false));
    		} else if (active.getType() == KEPT && !active.isLastActiveOccurrence()) {
    			print(getNextOccurrenceContinuationCode(active));
			}
			
			print(')');
		}
				
		protected void exitHostLanguageMode() throws GenerationException {
			if (hostLanguageMode) {
				tprintln("exitHostLanguageMode();");
				hostLanguageMode = false;
			}
		}
    }
	
	protected boolean reactiveConstraints() {
		for (UserDefinedConstraint constraint : getUserDefinedConstraints())
			if (constraint.isReactive()) return true;
		return false;
	}
	protected boolean reactivationMayCauseRecursion() {
		for (UserDefinedConstraint constraint : getUserDefinedConstraints())
			if (constraint.isReactive() && isRecursive(constraint)) return true;
		return false;
	}
    
    protected boolean alwaysReturns(Body body) {
    	return body.hasConjuncts() && (
			body.endsWithFailure() || needsContinuation(body)
		);
    }
    
    protected boolean needsContinuation(Body body) {
    	return getContinuationIndex(body) >= 0;
    }
    
    protected int getContinuationIndex(Body body) {
        int result = 0;
        for (IConjunct conjunct : body) {
            if (conjunct instanceof UserDefinedConjunct
            		&& isRecursive(((UserDefinedConjunct)conjunct).getConstraint())) {
            	return result;
            }
            result++;
        }
        return -1;
    }
    
    protected void generatePushContinuations(Occurrence active, int continuationIndex, int index, boolean setReactivatedFalse) throws GenerationException {
    	boolean kept = active.getType() == KEPT;
    	boolean lastKept = kept && active.isLastActiveOccurrence();
    	List<Lookup> lookups = kept? active.getNonSingletonLookups() : null;
    	boolean keptOccurrenceWithMoreLookups = kept && !lookups.isEmpty();
    	
    	Body body = active.getBody();
    	int nbPushes = 0;
    	
    	int l = body.getLength() - 1;
    	for (int i = index+1; i <= l; i++) {
    		IConjunct conjunct = body.getConjunctAt(i);
    		if (safeToPush(conjunct)
    				&& (i == l || keptOccurrenceWithMoreLookups || isRecursive(((UserDefinedConjunct)conjunct).getConstraint()))
    		)
    			nbPushes++;
    		else
    			break;
    	}
    	
    	boolean bodyRemaining = index+nbPushes < body.getLength()-1;
    	
    	boolean pushedRuleFiredContinuation = false;
    	String lastPush = null;
    	
    	boolean doSelfTailCall = false;
    	
    	if (bodyRemaining || keptOccurrenceWithMoreLookups) {
        	boolean testedSafeHasNext = false, testedHasNext = false;
        	if (bodyRemaining) {
        		if (setReactivatedFalse)
        			tprintln("reactivated = false;");
        	} else {	// i.e. keptOccurrenceWithMoreLookups
    			// TODO: ook mogelijk: na de body testen en pop() doen als verwijderd
    			if (lookups.size() == 1) {
					Occurrence occurrence = lookups.get(0).getOccurrence(); 
					if (setReactivatedFalse && !lastKept)
						tprintln("reactivated = false;");
					tprint("if (");
					print(getIteratorName(occurrence));
					println(".hasNext()) {");
					incNbTabs();
					if (setReactivatedFalse && lastKept)
						tprintln("reactivated = false;");
					testedHasNext = true;
					testedSafeHasNext = !mayRemove(body, occurrence);
    			}
    			
    			// TODO if none of the partners can be removed,
    			// it is possible to go through them all,
    			// building all joins 
    			// and pushing all the corresponding body continuations
    			// HOWEVER if is propagation: propagation history could
    			//	prevent these firings if fired by executing body
    		}
    		
        	doSelfTailCall = continuationIndex >= 0
        		&& nbPushes == 0
        		&& index == continuationIndex
				&& active.getNbPartners() == 1; // XXX lookups.size() == 1???????
        	
        	if (!doSelfTailCall)
        		lastPush = getContinuationCode(active, index+nbPushes, testedSafeHasNext);

	        if (testedHasNext) {
	        	if (doSelfTailCall) {
	        		printUndoPop();
	        		doSelfTailCall = false;
	        	} else {
	        		printPush(lastPush);
	        		lastPush = null;
	        	}

	        	decNbTabs();
	        	if (kept) {
	        		if (!lastKept) {
	        			tprintln("} else {");
		        		incNbTabs();
		        		printPushNextOccurrenceContinuation(active);
		        		if (hasToTrace()) {
		        			printPushRuleFiredContinuation(active, false);
		        			pushedRuleFiredContinuation = true;
		        		}
		        		decNbTabs();
	        		} else if (doGenerationOptimization()) {	// lastKept
	        			tprintln("} else {");
	        			ttprintln("reactivated = true;");
	        		}
				}
	        	
	        	tprintln('}');
	        }
        } else { // !bodyRemaining && !keptOccurrenceWithMoreLookups
        	if (kept) {
        		if (!lastKept)
        			lastPush = getNextOccurrenceContinuationCode(active);
        		else if (doGenerationOptimization())
        			tprintln("reactivated = true;");
        	}
        }
    	
    	boolean pushRuleFiredContinuation = 
    		!bodyRemaining && hasToTrace() && !pushedRuleFiredContinuation;
    	
    	if (nbPushes > 0 || lastPush != null || pushRuleFiredContinuation) {
    		printBeginPushing(); nl();
    		if (nbPushes > 0) for (int i = 1;; i++) {
    			tprintTabs(); printNewActivation(body, index+i);
    			if (i < nbPushes) println(',');
    			else break;
    		}
    		if (pushRuleFiredContinuation) {
    			if (nbPushes > 0) println(',');
    			ttprint(getRuleFiredContinuationCode(active, lastKept));
    		}
    		if (lastPush != null) {
    			if (nbPushes > 0 || pushRuleFiredContinuation) println(','); 
    			ttprint(lastPush);
    		}
    		nl(); tprintln(");");
    	}
    }
    
    String getContinuationCode(Occurrence active, int bodyIndex, boolean testedSafeHasNext) {
    	scheduleContinuation(new ContinuationInfo(active, bodyIndex, testedSafeHasNext));
    	
    	StringBuilder result = new StringBuilder()
    		.append("new ").append(getContinuationType(active, bodyIndex))
    		.append('(');
        appendContinuationArgumentList(result, active, bodyIndex, false);	        
        return result.append(')').toString();
    }
    
    protected void printPushNextOccurrenceContinuation(Occurrence active) throws GenerationException {
    	printPush(getNextOccurrenceContinuationCode(active));
    }
    protected String getNextOccurrenceContinuationCode(Occurrence active) {
    	scheduleContinuation(new NextOccurrenceContinuationInfo(active));
    	return "new " + getNextOccurrenceContinuationType(active) + "()";
    }
        
    private static class SafeToPushScanner extends UpCastingArgumentVisitor {
		public SafeToPushScanner() { super(true); }
		
		boolean safeToPush = true;
		
		@Override
		public <T extends IArgumented<? extends IArgumentable<?>> & IArgument> void visitArgumented(T arg) throws Exception {
			if (safeToPush) {
				AbstractVariableScanner scanner = new AbstractVariableScanner() {
					@Override
					protected boolean scanVariable(Variable variable) {
						return !variable.getType().isFixed();
					}
				};
				arg.accept(scanner);
				safeToPush = !scanner.getResult();
			}
		}
	}
    protected static boolean safeToPush(IConjunct conjunct) {
    	if (conjunct instanceof UserDefinedConjunct) {
    		try {
    			SafeToPushScanner scanner = new SafeToPushScanner();
	    		conjunct.accept(scanner);
	    		return scanner.safeToPush;
	    		
    		} catch (Exception x) {
    			throw new RuntimeException();
    		}
    	} else {
    		return false;
    	}
    }
    
	protected void printReturnContinuation(Body body, int index) throws GenerationException {
        tprint("return ");
        printNewActivation(body, index);
        println(';');
    }
	protected void printNewActivation(Body body, int index) throws GenerationException {
		new ConjunctCodeGenerator(this, body.getConjunctAt(index), BODY) {
			@Override
			protected void printConjunctPrefix() throws GenerationException { /* NOP */ }
			@Override
			protected void printConjunctInfix() throws GenerationException { /* NOP */ }
			@Override
			protected void printConjunctPostfix() throws GenerationException { /* NOP */ }
			
            @Override
            public void visit(UserDefinedConjunct conjunct) throws GenerationException {
                print("new ");
                print(getConstraintTypeName(conjunct.getConstraint()));
                printExplicitArguments(conjunct);
            }
        }.generate();
	}
    
    protected static String getContinuationType(Occurrence active, int index) {
        return StringUtils.capFirst(getContinuationMethod(active, index));
    }
    protected static String getNextOccurrenceContinuationType(Occurrence active) {
        return StringUtils.capFirst(getOccurrenceName(active.getNextActiveOccurrence()));
    }
    protected static String getRuleFiredContinuationType() {
        return "RuleFiredContinuation";
    }
    protected static String getContinuationMethod(Occurrence active, int index) {
		return getOccurrenceName(active) + "_" + (index+1);
	}

    protected static abstract class AbstractContinuationInfo {
    	public abstract void generateContinuation() throws GenerationException;
    	public abstract void generateContinuationMethod() throws GenerationException;
    	
    	@Override
        public final boolean equals(Object obj) {
        	return (obj instanceof AbstractContinuationInfo)
        		&& equals((AbstractContinuationInfo)obj);
        }
    	public abstract boolean equals(AbstractContinuationInfo other);
    	
    	@Override
		public abstract int hashCode();
    }
    
    protected class ContinuationInfo extends AbstractContinuationInfo {
        public ContinuationInfo(Occurrence active, int index, boolean hasNextTested) {
            this.active = active;
            this.index = index;
            this.hasNextTested = hasNextTested;
        }
        public final Occurrence active;
        public final int index;
        public final boolean hasNextTested;
        
        @Override
        public boolean equals(AbstractContinuationInfo other) {
        	return (other instanceof ContinuationInfo)
        		&& equals((ContinuationInfo)other);
        }
        public boolean equals(ContinuationInfo other) {
        	return this.active == other.active
        		&& this.index == other.index;
        }
        
        @Override
        public void generateContinuation() throws GenerationException {
        	ConstraintCodeGenerator.this.generateContinuation(active, index);
        }
        @Override
		public void generateContinuationMethod() throws GenerationException {
        	nl();
        	ConstraintCodeGenerator.this.generateContinuationMethod(active, index, hasNextTested);
        }
        
        @Override
        public int hashCode() {
        	return 33 * (33 * (23 + index) + active.getConstraintOccurrenceIndex());
        }
    }
    
    protected class NextOccurrenceContinuationInfo extends AbstractContinuationInfo {
    	private final Occurrence active;
    	public NextOccurrenceContinuationInfo(Occurrence active) {
            this.active = active;
        }
    	
    	@Override
    	public void generateContinuation() throws GenerationException {
    		generateNextOccurrenceContinuation(active);
    	}
    	
    	@Override
    	public void generateContinuationMethod() throws GenerationException {
    		// NOP
    	}
    	
    	@Override
        public boolean equals(AbstractContinuationInfo other) {
        	return (other instanceof NextOccurrenceContinuationInfo)
        		&& equals((NextOccurrenceContinuationInfo)other);
        }
        public boolean equals(NextOccurrenceContinuationInfo other) {
        	return this.active.equals(other.active);
        }
        
        @Override
        public int hashCode() {
        	return 123456 + active.getConstraintOccurrenceIndex();
        }
    }
    
    protected class RuleFiredContinuationInfo extends AbstractContinuationInfo {
		@Override
		public void generateContinuation() throws GenerationException {
			ConstraintCodeGenerator.this.generateRuleFiredContinuation();
		}

		@Override
		public void generateContinuationMethod() throws GenerationException {
			// NOP
		}

		@Override
		public int hashCode() {
			return 17011983;
		}
		@Override
        public boolean equals(AbstractContinuationInfo other) {
        	return (other instanceof RuleFiredContinuationInfo);
        }
    }
    
    protected void generateContinuations() throws GenerationException {
    	nl();
        generateActivation();
        nl();
        generateReactivationContinuation();
        
        Queue<AbstractContinuationInfo> continuations = getContinuationsToGenerate();
        while (!continuations.isEmpty()) {
        	AbstractContinuationInfo continuation = continuations.poll();
        	getGeneratedContinuations().add(continuation);
        	nl();
        	continuation.generateContinuation();
        	continuation.generateContinuationMethod();
        }
    }
    
    protected void generateActivation() throws GenerationException {
    	if (isRecursive())
    		generateActivationCallMethod();
    	else {
    		generateNonRecursiveCallMethod();
    		nl();
    		generateNonRecursiveActivateMethod();
    	}
    }
    protected void generateReactivationContinuation() throws GenerationException {
    	if (isReactive() && isRecursive() && warrantsReactivationClass()) {
			tprintln("final Reactivation REACTIVATION = new Reactivation();");
	    	tprint("protected final class Reactivation extends Continuation");
	        openAccolade();
	        generateReactivationCallMethod();
	        nl();
	    	tprintOverride();
	        tprintln("public String toString() {");
	        ttprint("return "); printLiteral("Reactivate "); 
	        	print(" + "); print(getConstraintTypeName()); println(".this;");
	        tprintln('}');
	        closeAccolade();
    	}
    }
    
    protected void generateNonRecursiveCallMethod() throws GenerationException {
    	tprintOverride();
    	tprint("protected Continuation call()");
    	openAccolade();
    	if (getOptions().doStackOptimizations()) {
	    	printNonRecursiveActivationCode();
	    	tprintln("return $$continuationStack.pop();");
    	} else {
    		tprintln("throw new IllegalStateException();");
    	}
    	closeAccolade();
    }
    protected void generateNonRecursiveActivateMethod() throws GenerationException {
    	if (mayBeStored()) tprintOverride();
    	tprint("protected void activate()");
    	openAccolade();
    	printNonRecursiveActivationCode();
    	closeAccolade();
    }
    
    protected void printNonRecursiveActivationCode() throws GenerationException {
    	if (hasToTrace()) {
        	tprintln("if (tracer != null) tracer.activated(this);");
        	nl();
        }
    	printNonRecursive__ctivationCode(true);
    }
    
    private void printNonRecursive__ctivationCode(final boolean activate) throws GenerationException {
    	Iterator<Occurrence> iterator = new FilteredIterator<Occurrence>(
			getConstraint().getPositiveOccurrences(), 
    		new Filtered.Filter<Occurrence>() {
				@Override
				public boolean include(Occurrence occurrence) {
					return occurrence.isActive()
						&& (activate || occurrence.isReactive());
				}
			}
    	);
    		
    	if (iterator.hasNext()) {
    		boolean 
    			stored = getConstraint().mayBeStored(),
    			accos = stored || hasToTrace();
    		
    		tprint("if (");
    		do {
	    		Occurrence occurrence = iterator.next();
				print(getOccurrenceMethodName(occurrence));
				print("()");
				
				if (iterator.hasNext()) print(" && ");
				else { print(')'); break; }
	    	} while (true);
    		
    		if (accos) {
    			openAccolade();
    			if (stored) printStoreCode(true);
    		
	    		if (hasToTrace()) {
	        		nl();
	        		tprintln("if (tracer != null) tracer.suspended(this);");
	        	}
	    		closeAccolade();
    		} else {
    			println(';');
    		}
    	} else {
    		if (activate) printStoreCode(false);
    		
    		if (hasToTrace()) {
        		nl();
        		tprintln("if (tracer != null) tracer.suspended(this);");
        	}
    	}
    }
    
    protected void generateActivationCallMethod() throws GenerationException {
    	generate__ctivationCallMethod(true);
    }
    protected void generateReactivationCallMethod() throws GenerationException {
    	generate__ctivationCallMethod(false);
    }
    
    private void generate__ctivationCallMethod(boolean activate) 
    throws GenerationException {
        tprintOverride();
    	tprint("protected Continuation call()");
        openAccolade();
        
        if (activate && hasToTrace()) {
        	tprintln("if (tracer != null) tracer.activated(this);");
        	nl();
        }
        
        boolean mayBeReactivation = !activate || !warrantsReactivationClass();
        boolean doGeneration = doGenerationOptimization();
        // test alive also if constraint is used to push on reactivations:
		boolean testAlive = mayBeReactivation && mayBeRemoved();
		
		// TODO: waarom MOET MOET MOET dit lijntje hier staan voor bool????
		doGeneration &= !activate;

    	if (testAlive || doGeneration) {
    		tprint("if (");
    		if (testAlive) {
    			print("isAlive()");
    			if (doGeneration) print(" && ");
    		}
    		if (doGeneration) print("!reactivated");
    		print(')');
    		openAccolade();
    	}
    	
        if (getConstraint().hasActivePositiveOccurrences()) {
	        tprintln("Continuation continuation;");
	        for (Occurrence occurrence : getConstraint().getPositiveOccurrences()) {
	            if (occurrence.isPassive()) continue;
	            if (!activate && !occurrence.isReactive()) continue;
	            tprint("if ((continuation = ");
	            print(getOccurrenceMethodName(occurrence));
	            println("()) != null) return continuation;");
	        }
	        if (activate) printStoreCode(true);
        } else {
        	if (activate) printStoreCode(false);
        }
        
        if (hasToTrace()) {
    		nl();
    		tprint("if (tracer != null) tracer.suspended(");
    		if (!activate) {
    			print(getConstraintTypeName(getConstraint()));
    			print('.');
    		}
    		println("this);");
    	}
        
        if (mayBeReactivation && doGeneration) tprintln("reactivated = true;");
    	if (testAlive || doGeneration) closeAccolade();
        
        tprintDeadContinuation();
        closeAccolade();
    }
    
    /**
     * @param active
     * @param index
     * 	The index of the last body conjunction that was already
     * 	executed (<code>0 &lt;= index &lt; length(body)</code>).
     * @param hasNextKnown
     * 	It is known that there is a next instance in the iterator
     * 	for the first (only) partner constraint 
     * 	(<code>do ... while</code> can be used).
     * 	
     * @throws GenerationException
     */
    protected void generateContinuationMethod(
		Occurrence active, int index, boolean hasNextKnown
	) throws GenerationException {
    	
    	Body body = active.getBody();
    	boolean bodyRemaining = index < body.getLength()-1;
    	
    	tprint("protected final Continuation ");
    	print(getContinuationMethod(active, index));
    	print('(');
    	printContinuationArgumentList(active, index, true);
    	print(')');
    	openAccolade();
    	
    	List<ContinuationArgument> variables = getContinuationVariables(active, index);
    	if (bodyRemaining) {
			Body restBody = new Body(body.getSubConjunction(++index));
            
			if (!generateCommitBody(active, restBody, index, index, variables)) {
				if (active.getType() == KEPT) {
					if (active.looksUpNonSingletonPartners())
						doGenerateOccurrenceMethod(active, index, variables, hasNextKnown);
					else
						tprintLiveContinuation();
                } else {
                    tprintDeadContinuation();
                }
			}
    	} else {
            assert active.getType() == KEPT;
            assert active.looksUpNonSingletonPartners();
            
    		doGenerateOccurrenceMethod(active, index, variables, hasNextKnown);
        }
    	
    	closeAccolade();
    }
    
    protected void printCallContinuationMethod(Occurrence active, int index) throws GenerationException {
    	print(getContinuationMethod(active, index));
    	print('(');
        printContinuationArgumentList(active, index, false);
        print(')');
    }
    
    protected void generateContinuation(Occurrence active, int index) throws GenerationException {
    	tprint("protected final class ");
    	print(getContinuationType(active, index));
    	print(" extends Continuation");
    	openAccolade();

        List<ContinuationArgument> arguments  
            = getContinuationArguments(active, index);
    
        for (ContinuationArgument argument : arguments) {
            tprint("private final ");
            prints(argument.getTypeString());
            print(argument.getIdentifier());
            println(';');
        }
        
        /* constructor */
    	if (!arguments.isEmpty()) {
            nl();
            tprint("public ");
        	print(getContinuationType(active, index));
            print('(');
            printContinuationArgumentList(active, index, true);
            print(")");
            openAccolade();
            for (ContinuationArgument argument : arguments) {
                tprint("this.");
                print(argument.getIdentifier());
                print(" = ");
                print(argument.getIdentifier());
                println(';');
            }
            closeAccolade();       
        }

        nl();
        tprintOverride();
    	tprintln("protected Continuation call() {");
        incNbTabs();
        
        boolean bodyRemaining = bodyRemaining(active, index);
        boolean tests = !bodyRemaining && generateTests(active);
        
        if (active.getType() == KEPT) {
        	tprintln("Continuation continuation;");
            tprint("if ((continuation = ");
            printCallContinuationMethod(active, index);
            println(") != null) return continuation;");
        	
        	final int activeIndex = active.getConstraintOccurrenceIndex();
        	for (Occurrence occurrence : getConstraint().getPositiveOccurrences()) {
        		if (occurrence.isActive() && occurrence.getConstraintOccurrenceIndex() > activeIndex) {
        			tprint("if ((continuation = ");
        			print(getOccurrenceMethodName(occurrence));
        			println("()) != null) return continuation;");
        		}
        	}
        	if (doGenerationOptimization())
        		tprintln("reactivated = true;");
        	if (getConstraint().mayBeStored() && !active.isStored())
    			tprintln("store();");
        	
        	if (hasToTrace()) {
            	tprint("if (tracer != null) tracer.suspended(");
            	print(getConstraintTypeName(getConstraint()));
            	println(".this);");
            }
        } else { 	// active.getType() == REMOVED
        	tprint("return ");
        	printCallContinuationMethod(active, index);
        	println(';');
        }
        
        if (tests) closeAccolade();
        if (tests || active.getType() == KEPT) tprintDeadContinuation();
        closeAccolade();
        
        nl();
        tprintOverride();
        tprint("public String toString()");
        openAccolade();
        Iterator<ContinuationArgument> iter = arguments.iterator();
    	if (iter.hasNext()) {
    		tprintln("return new StringBuilder()");
    		ttprint(".append("); 
    			printLiteral(getContinuationType(active, index));
        		println(')');
    		ttprintln(".append('(')");
    		ttprint(".append(");
    		print(iter.next().getDescription());
    		println(')');
	    	while (iter.hasNext()) {
	    		ttprintln(".append(\", \")");
	    		ttprint(".append(");
	    		print(iter.next().getDescription());
	    		println(')');
	    	}
	    	ttprintln(".append(')')");
	    	ttprintln(".toString();");
    	} else {
    		tprint("return ");
    		printLiteral(getContinuationType(active, index) + "()");
    		println(';');
    	}
        
        closeAccolade();
        
        
        closeAccolade();
    }
    
    protected boolean generateTests(Occurrence active) throws GenerationException {
    	boolean mayBeRemoved = mayBeRemoved(active);
        boolean doGeneration = doGenerationOptimization(active);
        
        if (!mayBeRemoved && !doGeneration) 
        	return false;
    	if (mayBeRemoved && doGeneration)
    		tprint("if (isAlive() && !reactivated)");
    	else if (doGeneration)
    		tprint("if (!reactivated)");
    	else // if (mayBeRemoved)
    		tprint("if (isAlive())");
		openAccolade();
		return true;
    }
    
    protected void printConditionalDeadContinuations(Occurrence active, boolean continuation) throws GenerationException {
		if (doTestsAfter(active.getBody())) {
    		if (doGenerationOptimization(active))
    			printConditionalDeadContinuation("reactivated");
    		if (active.isStored() && mayBeRemoved(active))
    			printConditionalDeadContinuation("!alive");
    	}
	}
    
    protected void generateRuleFiredContinuation() throws GenerationException {
    	tprint("protected final class ");
    	print(getRuleFiredContinuationType());
    	println(" extends Continuation {");
    	incNbTabs();
    	tprintln("private final String ruleId;");
    	tprintln("private final int activeIndex;");
    	tprintln("private final boolean suspendAfter;");
    	tprintln("private final Constraint[] constraints;");
    	nl();
    	tprint("public "); 
    	print(getRuleFiredContinuationType());
    	println("(String ruleId, int activeIndex, boolean suspendAfter, Constraint... constraints) {");
    	ttprintln("this.ruleId = ruleId;");
    	ttprintln("this.activeIndex = activeIndex;");
    	ttprintln("this.suspendAfter = suspendAfter;");
    	ttprintln("this.constraints = constraints;");
    	tprintln('}');
    	nl();
		tprintOverride();
    	tprintln("protected Continuation call() {");
        incNbTabs();
        tprintln("if (tracer != null) {");
        ttprintln("tracer.fired(ruleId, activeIndex, constraints);");
        ttprintln("if (suspendAfter) tracer.suspended(constraints[activeIndex]);");
        tprintln('}');
        tprintDeadContinuation();
        closeAccolade();
        nl();
        tprintOverride();
        tprintln("public String toString() {");
        ttprint("return \""); 
    	print(getRuleFiredContinuationType()); printcln("(\" + ruleId + \", ...)\"");
        tprintln('}');
        closeAccolade();
    }
    
    protected void generateNextOccurrenceContinuation(Occurrence active) throws GenerationException {
    	boolean doGeneration = doGenerationOptimization(active),
    		testAlive = mayBeRemoved(active),
    		doTests = doGeneration || testAlive;
    	
    	tprint("protected final class ");
    	print(getNextOccurrenceContinuationType(active));
    	println(" extends Continuation {");
    	incNbTabs();
    	
    	tprintOverride();
    	tprintln("protected Continuation call() {");
    	incNbTabs();
    	
    	if (doTests) {
    		tprint("if (");
    		if (testAlive) {
    			print("isAlive()");
    			if (doGeneration) print(" && ");
    		}
    		if (doGeneration) print("!reactivated");
			print(')');
    		openAccolade();
    	}
        
        tprintln("Continuation continuation;");
        
        final int activeIndex = active.getConstraintOccurrenceIndex();
        for (Occurrence occurrence : getConstraint().getPositiveOccurrences()) {
			if (occurrence.isActive() && occurrence.getConstraintOccurrenceIndex() > activeIndex) {
		        tprint("if ((continuation = ");
		        print(getOccurrenceMethodName(occurrence));
		        println("()) != null) return continuation;");
			}
        }
        if (!active.isStored()
        		&& getConstraint().mayBeStored()
        	) tprintln("store();");
        if (doGeneration)
        	tprintln("reactivated = true;");
        if (hasToTrace()) {
        	tprint("if (tracer != null) tracer.suspended(");
        	print(getConstraintTypeName(getConstraint()));
        	println(".this);");
        }
        
        if (doTests) closeAccolade();
        
        tprintDeadContinuation();
        closeAccolade();
        
        nl();
        tprintOverride();
        tprintln("public String toString() {");
        ttprint("return "); print('"'); 
        print(getNextOccurrenceContinuationType(active));
        printcln("()\"");
        tprintln('}');
        
        closeAccolade();
    }
    
    protected static boolean bodyRemaining(Occurrence active, int index) {
    	return index < active.getBody().getLength()-1;
    }
    
    protected void appendContinuationArgumentList(
		StringBuilder string,
		Occurrence active, int index, boolean printTypes
	) {
    	Iterator<ContinuationArgument> arguments 
        	= getContinuationArguments(active, index).iterator();
    
	    if (arguments.hasNext()) do {
	        ContinuationArgument argument = arguments.next();
	        if (printTypes) string.append(argument.getTypeString()).append(' ');
	        string.append(argument.getIdentifier());
	        
	        if (arguments.hasNext())
	        	string.append(", ");
	        else
	            break;
	        
	    } while (true);
    }
    
    protected void printContinuationArgumentList(
		Occurrence active, int index, boolean printTypes
	) throws GenerationException {
        Iterator<ContinuationArgument> arguments 
            = getContinuationArguments(active, index).iterator();
        
        if (arguments.hasNext()) do {
            ContinuationArgument argument = arguments.next();
            if (printTypes) prints(argument.getTypeString());
            print(argument.getIdentifier());
            
            if (arguments.hasNext())
                print(", ");
            else
                break;
            
        } while (true);
    }
    
    protected List<ContinuationArgument> getContinuationArguments(
		final Occurrence active, int index
	) {
        try {
	    	final List<ContinuationArgument> arguments = new ArrayList<ContinuationArgument>();
	    	
	    	if (hasToTrace())
	    		for (Occurrence partner : active.getPartners())
	    			arguments.add(new PartnerContinuationArgument(partner));
	        
	    	if (active.getType() == KEPT) {
	    		final List<Lookup> lookups = getLookupsOf(active);
	    		final Lookup last = getLastNonSSHashMapLookup(active);
	    		if (last != null) {
			        active.accept(new AbstractScheduleVisitor() {
			        	final int lastIndex = lookups.lastIndexOf(last);
			        	
						@Override
						public void visit(Lookup lookup) throws Exception {
							Occurrence partner = lookup.getOccurrence();
							if (!isSSLookup(lookup) && includeInContinuation(active, lookups, lookup, lastIndex + 1))
				        		arguments.add(new IteratorContinuationArgument(partner));
				        	if (!hasToTrace() && includeInContinuation(active, lookups, lookup, lastIndex))
				        		arguments.add(new PartnerContinuationArgument(partner));
						}
					});
	    		}
	    	}
	        
	        arguments.addAll(getContinuationVariables(active, index));
	        
	        return arguments;
    	} catch (Exception x) {
    		throw new RuntimeException(x);
    	}
    }
    
    public boolean includeIteratorInContinuation(Occurrence active, Lookup partner) {
    	return !isSSLookup(partner) && includeInContinuation(active, partner, 1);
    }
    public boolean includeCurrentInContinuation(Occurrence active, Lookup partner) {
    	return hasToTrace() || includeInContinuation(active, partner, 0);
    }
    private boolean includeInContinuation(Occurrence active, Lookup partner, int i) {
    	if (active.getType() == REMOVED) return false;
    	List<Lookup> lookups = getLookupsOf(active);
    	Lookup last = getLastNonSSHashMapLookup(active);
    	if (last == null) return false;
    	int index = lookups.indexOf(last);
    	return includeInContinuation(active, lookups, partner, index + i);
    }
    protected boolean includeInContinuation(Occurrence active, List<Lookup> lookups, Lookup partner, int lastIndex) {
    	return lookups.indexOf(partner) < lastIndex; 
	}
    
    public static boolean isNeededInDifferentPartnersTest(Occurrence occurrence) {
    	if (!occurrence.isStored() && !occurrence.isReactive())
    		return false;
    	UserDefinedConstraint constraint = occurrence.getConstraint();
    	for (Occurrence partner : occurrence.getPartners())
    		if (partner.getConstraint() == constraint)
    			return true;
    	return false;
    }
    
    protected static class ScheduleVariableCollector extends AbstractScheduleVisitor {
    	private Lookup last;
    	private SortedSet<Variable> variables;
    	private boolean collecting = true, first = true;
    	
    	private ScheduleVariableCollector(SortedSet<Variable> variables, Lookup last) {
    		this.variables = variables;
    		this.last = last;
    	}

		@Override
		public void visit(IGuardConjunct guard) throws Exception {
			if (collecting)
				variables.addAll(guard.getVariables());
		}

		@Override
		public void visit(Lookup lookup) throws Exception {
			if (first)
				first = false;
			else if (collecting) {
				if (lookup == last) collecting = false;
				VariableCollector.collectVariables(lookup, variables);
			}
		}
		
		@Override
		public boolean isVisiting() {
			return collecting;
		}
		
	    public static void collectVariables(
    		IScheduled scheduled, SortedSet<Variable> result
	    ) {
	    	try {
	    		ScheduleVariableCollector collector = 
	    			new ScheduleVariableCollector(result, getLastLookupOf(scheduled));
				scheduled.accept(collector);
				
	    	} catch (Exception e) {
				e.printStackTrace();
				throw new InternalError();
			}
	    }
    }
    
    public static SortedSet<Variable> getVariablesUsedInContinuation(Occurrence active, int index) {
    	Conjunction<?> body = active.getBody();
    	
    	final SortedSet<Variable> result, 
    		temp = (index < body.getLength())
    			 ? VariableCollector.collectVariables(body.getSubConjunction(index+1))
    			 : null;
        
    	if (active.getType() == KEPT && active.looksUpNonSingletonPartners()) {
    		result = VariableCollector.collectVariables(body);
			result.removeAll(getVariablesDeclaredAfterLastLookup(active.getSchedule()));
			result.addAll(temp);
    	} else {
    		result = temp;
    	}
    		
//    	if (active.hasPartners())
//    		ScheduleVariableCollector.collectVariables(active, result);
    	
    	result.removeAll(active.getVariables());
    	
    	return result;
    }
    
    public static SortedSet<Variable> getVariablesDeclaredAfterLastLookup(Schedule schedule) {
    	SortedSet<Variable> result = new TreeSet<Variable>();
    	int index = schedule.getScheduleElements().getIndexOf(getLastLookupOf(schedule));
    	for (IVariableInfo varInfo : schedule.getVariableInfos())
    		if (varInfo.getDeclarationIndex() > index)
    			result.add((Variable)varInfo.getActualVariable());
    	return result;
    }
    
    // XXX we could take into account assignments, but this only rarely makes a difference
    public static List<ContinuationArgument> getContinuationVariables(Occurrence active, int index) {
        final List<ContinuationArgument> variables = new ArrayList<ContinuationArgument>();
        final SortedSet<Variable> usedVariables = getVariablesUsedInContinuation(active, index);
        
        for (IVariableInfo varInfo : active.getVariableInfos())
        	if (usedVariables.contains(varInfo.getActualVariable()))
        		variables.add(new VariableInfoContinuationArgument(varInfo));
        
        try {
        	active.getBody().getSubConjunction(0, index+1)
                .accept(new AbstractConjunctVisitor() {
                    @Override
                    protected void visit(IConjunct __) throws Exception {
                        // NOP
                    }
                    @Override
                    public void visit(AssignmentConjunct conjunct) throws Exception {
                        if (conjunct.isDeclaration()) {
                        	Variable var = (Variable)conjunct.getArgumentAt(0);
                        	if (usedVariables.contains(var))
                        		variables.add(new VariableContinuationArgument(var));
                        }
                    }
                });
        } catch (Exception e) {
            throw new InternalError();
        }
        
        return variables;
    }
    
    protected static abstract class ContinuationArgument {
    	public String getDescription() {
    		return getIdentifier();
    	}
        public abstract String getTypeString();
        public abstract String getIdentifier();
        
        @Override
        public boolean equals(Object other) {
            return (other instanceof ContinuationArgument)
                && this.getIdentifier().equals(((ContinuationArgument)other).getIdentifier())
                && this.getTypeString().equals(((ContinuationArgument)other).getTypeString());
        }
        @Override
        public int hashCode() {
            return getIdentifier().hashCode() ^ getTypeString().hashCode();
        }
        @Override
        public String toString() {
            return getTypeString() + ' ' + getIdentifier();
        }
    }
    protected static class VariableInfoContinuationArgument extends ContinuationArgument {
        private IVariableInfo variableInfo;
        public VariableInfoContinuationArgument(IVariableInfo variableInfo) {
            this.variableInfo = variableInfo;
        }
        
        @Override
        public String getIdentifier() {
            return variableInfo.getActualVariable().getIdentifier();
        }
        @Override
        public String getTypeString() {
            return variableInfo.getFormalVariable().getTypeString();
        }
    }
    protected static class VariableContinuationArgument extends ContinuationArgument {
        private Variable variable;
        public VariableContinuationArgument(AssignmentConjunct assignment) {
            this((Variable)assignment.getArgumentAt(0));
        }
        public VariableContinuationArgument(Variable variable) {
            this.variable = variable;
        }
        
        @Override
        public String getIdentifier() {
            return variable.getIdentifier();
        }
        @Override
        public String getTypeString() {
            return variable.getTypeString();
        }
    }
    protected static class PartnerContinuationArgument extends ContinuationArgument {
        private Occurrence partner;
        public PartnerContinuationArgument(Occurrence partner) {
            this.partner = partner;
        }
        
        @Override
        public String getIdentifier() {
            return getOccurrenceName(partner);
        }
        @Override
        public String getTypeString() {
            return getConstraintTypeName(partner.getConstraint());
        }
    }
    protected static class IteratorContinuationArgument extends ContinuationArgument {
        private Occurrence partner;
        protected IteratorContinuationArgument(Occurrence partner) {
            this.partner = partner;
        }
        protected Occurrence getPartner() {
			return partner;
		}
        
        @Override
        public String getIdentifier() {
            return getIteratorName(getPartner());
        }
        @Override
        public String getDescription() {
        	return '"' + getIdentifier() + '"';
        }
        @Override
        public String getTypeString() {
            return "Iterator<" + getConstraintTypeName(getPartner().getConstraint()) + '>';
        }
    }
    
    protected void printStoreCode(boolean printStoredTest) throws GenerationException {
    	printStoreCode(printStoredTest, null);
    }
    protected void printStoreCode(boolean printStoredTest, Occurrence active) throws GenerationException {
    	if (!getConstraint().mayBeStored()) return;
    	
    	if (printStoredTest) {
        	tprint("if (!stored)");
        	openAccolade();
        }
        
        tprintln("stored = true;");
        tprintln("ID = IDcounter++;");
        
        // history updaten moet mogelijk alleen als niet met insert gebeurde...
        if (active != null && active.getRule().needsHistory()) {
        	PROPAGATION_HISTORY_CODE_GENERATOR.setNbTabs(getNbTabs());
			PROPAGATION_HISTORY_CODE_GENERATOR.generateAddToHistoryOnStore(active);
        }
        
        if (hasSetSemanticsLookup()) printHashCodeComputation(true); 
        tprint(getStorageMethodNameFor(getConstraint()));
        	print('('); print(getConstraintTypeName()); println(".this);");
        
        for (int i = 0; i < getArity(); i++) {
        	// XXX: if the variable is known fixed at the current occurrence: do not add observer!
        	if (getConstraint().isReactiveOn(i)) {
        		FormalVariable var = getFormalVariableAt(i);
        		if (var.getVariableType().isBuiltInConstraintObservable()) {
        			tprint(var.getIdentifier());
        			print(".addBuiltInConstraintObserver("); 
	            		print(getConstraintTypeName()); println(".this);");
        		} else {
        			if (getOptions().performAnalysis())
        				throw new GenerationException("Reactive argument is not observable: " + var);
        			else
        				System.err.println(" --> Warning: reactive argument is not observable: " + var + " (note: analysis turned off)");
        		}
        	}
        }
        
        if (printStoredTest) closeAccolade(); 
    }
    
    protected void tprintLiveContinuation() throws GenerationException {
		tprintln(isRecursive()? "return null;" : "return true;");
	}
    protected void printDeadContinuation() throws GenerationException {
    	println(isRecursive()? "return $$continuationStack.pop();" : "return false;");
    }
    
    protected void tprintDeadContinuation() throws GenerationException {
    	printTabs(); printDeadContinuation();
    }
    protected void printConditionalDeadContinuation(String condition) throws GenerationException {
    	tprint("if ("); print(condition); print(") "); printDeadContinuation();
    }
    
    protected void printBeginPushing() throws GenerationException {
    	tprint("$$continuationStack.push(");
    }
    protected void printPush(String it) throws GenerationException {
    	printBeginPushing(); print(it); println(");");
    }
    
    protected void printUndoPop() throws GenerationException {
    	tprintln("$$continuationStack.undoPop();");
    }
}