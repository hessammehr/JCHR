package compiler.codeGeneration;

import compiler.CHRIntermediateForm.arg.argument.ClassNameImplicitArgument;
import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argument.IImplicitArgument;
import compiler.CHRIntermediateForm.arg.argument.FormalArgument.OneDummy;
import compiler.CHRIntermediateForm.arg.argument.FormalArgument.OtherDummy;
import compiler.CHRIntermediateForm.arg.argument.constant.BooleanArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.ByteArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.CharArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.DoubleArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.FloatArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.IntArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.LongArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.NullArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.ShortArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.StringArgument;
import compiler.CHRIntermediateForm.arg.argumented.IArgumented;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.conjuncts.ConjunctConjunction;
import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitor;
import compiler.CHRIntermediateForm.conjuncts.IConjunction;
import compiler.CHRIntermediateForm.constraints.bi.Failure;
import compiler.CHRIntermediateForm.constraints.java.AssignmentConjunct;
import compiler.CHRIntermediateForm.constraints.java.NoSolverConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.OccurrenceType;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConjunct;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.init.InitialisatorMethodInvocation;
import compiler.CHRIntermediateForm.members.AbstractMethodInvocation;
import compiler.CHRIntermediateForm.members.ConstructorInvocation;
import compiler.CHRIntermediateForm.members.FieldAccess;
import compiler.CHRIntermediateForm.members.MethodInvocation;
import compiler.CHRIntermediateForm.solver.Solver;
import compiler.CHRIntermediateForm.variables.NamelessVariable;
import compiler.CHRIntermediateForm.variables.Variable;

public class ConjunctCodeGenerator extends JavaCodeGenerator 
	implements IConjunctVisitor {

	public final static boolean GUARD = true, BODY = false; 
	
	private IConjunction<? extends IConjunct> conjunction;
	
	private boolean GB;
	
    public ConjunctCodeGenerator(CodeGenerator codeGenerator, IConjunct conjunct, boolean GB) {
        this(codeGenerator, new ConjunctConjunction<IConjunct>(conjunct), GB);
    }
    
	public ConjunctCodeGenerator(CodeGenerator codeGenerator, IConjunction<? extends IConjunct> conjunction, boolean GB) {
		super(codeGenerator);
		setConjunction(conjunction);
		setGB(GB);
	}
	
	@Override
	protected void doGenerate() throws GenerationException {
		try {
			boolean first = true;
			
			for (IConjunct conjunct : getConjunction()) {
				if (!first) printConjunctInfix(); else first = false;
				printConjunctPrefix();
				conjunct.accept(this);
				printConjunctPostfix();
			}
		} catch (Exception x) {
            if (x instanceof GenerationException)
                throw (GenerationException)x;
            else    
                // only other possibility is a RuntimeException 
                // (even though not supposed to happen):
                throw (RuntimeException)x;
		}
	}
	
	protected void printConjunctPrefix() throws GenerationException {
		if (isBody()) printTabs();
	}
	protected void printConjunctPostfix() throws GenerationException {
		if (isBody()) println(';');
	}
	protected void printConjunctInfix() throws GenerationException {
		if (isGuard()) print(" && ");
	}

	public IConjunction<? extends IConjunct> getConjunction() {
		return conjunction;
	}
	public void setConjunction(IConjunction<? extends IConjunct> conjunction) {
		this.conjunction = conjunction;
	}
	
	
	public void setGB(boolean gb) {
		GB = gb;
	}
	protected boolean getGB() {
		return GB;
	}
	public boolean isGuard() {
		return getGB() == GUARD;
	}
	public boolean isBody() {
		return getGB() == BODY;
	}
		
	
	/*
	 * CONJUNCTS
	 */
	
	public void visit(AssignmentConjunct conjunct) throws GenerationException {
		if (haveToDeclare(conjunct))
			prints(conjunct.getTypeString());
		
		printArgument(conjunct.getArgumentAt(0));
		print(" = ");
		printArgument(conjunct.getArgumentAt(1));
	}
    
    protected boolean haveToDeclare(AssignmentConjunct conjunct) {
        return conjunct.isDeclaration();
    }
	
	public void visit(Failure conjunct) throws GenerationException {
		if (conjunct.hasMessage()) {
			print("throw new runtime.FailureException(\"");
			print(conjunct.getMessage());
			print("\")");
		} else
			print("throw new runtime.FailureException()");
	}
	
	public void visit(ConstructorInvocation conjunct) throws GenerationException {
		print("new ");
		print(conjunct.getTypeString());
		printExplicitArguments(conjunct);
	}
	
	public void visit(FieldAccess conjunct) throws GenerationException {
		printImplicitArgument(conjunct.getImplicitArgument());
		print(conjunct.getName());
	}
	
	public void visit(InitialisatorMethodInvocation conjunct) throws GenerationException {
		visit((AbstractMethodInvocation<?>)conjunct);
	}
	public void visit(MethodInvocation<?> conjunct) throws GenerationException {
		visit((AbstractMethodInvocation<?>)conjunct);
	}
	
	public void visit(AbstractMethodInvocation<?> conjunct) throws GenerationException {
		printMethodInvocation(conjunct);
	}
	protected void printMethodInvocation(AbstractMethodInvocation<?> invocation) throws GenerationException {
		String macro = InlineDefinitionStore.getInlineDefinition(invocation.getMethod());
		if (macro != null) {
			print(InlineDefinitionStore.format(macro, recordArguments(invocation)));
		} else {
			printImplicitArgument(invocation.getImplicitArgument());
			print(invocation.getMethodName());
			printExplicitArguments(invocation);
		}
	}
	
	protected String[] recordArguments(IArgumented<?> argumented) throws GenerationException {
		IArguments arguments = argumented.getArguments();
		String[] result = new String[arguments.getArity()];
		for (int i = 0; i < result.length; i++) {
			beginRecording();
			printArgument(arguments.getArgumentAt(i));
			result[i] = endRecording();
		}
		return result;
	}
	
	public void visit(NoSolverConjunct conjunct) throws GenerationException {
		printArgument(conjunct.getArgumentAt(0));
		print(conjunct.getActualInfixIdentifier());
		printArgument(conjunct.getArgumentAt(1));
	}
	
	public void visit(UserDefinedConjunct conjunct) throws GenerationException {
		UserDefinedConstraint constraint = conjunct.getConstraint(); 
        print("new ");
        print(ConstraintCodeGenerator.getConstraintTypeName(constraint));
        printExplicitArguments(conjunct);
        print(".activate()");
	}
	
	public void visit(Variable conjunct) throws GenerationException {
		print(conjunct.getIdentifier());
	}
	
	public void visit(Occurrence occurrence) throws GenerationException {
		throw new IllegalStateException();
	}
	public boolean visits(OccurrenceType type) {
		return false;
	}
	
	protected void tprintImplicitArgument(IImplicitArgument argument) throws GenerationException {
		printTabs();
		printImplicitArgument(argument);
	}
	protected void printImplicitArgument(IImplicitArgument argument) throws GenerationException {
		printArgument(argument);
		print('.');
	}
	
	protected void printExplicitArguments(IArgumented<?> argumented) throws GenerationException {
		print('(');
		IArguments arguments = argumented.getExplicitArguments();
		int i = arguments.hasImplicitArgument() ? 1 : 0;
		for (int j = i; j < arguments.getArity(); j++) {
			if (j != i) print(", ");
			printArgument(arguments.getArgumentAt(j));
		}
		print(')');
	}
	
	protected void printArgument(IArgument argument) throws GenerationException {
		try {
			argument.accept(ARGUMENT_CODE_GENERATOR);
		} catch (Exception x) {
			if (x instanceof GenerationException)
				throw (GenerationException)x;
			else
                // only other possibility is a RuntimeException 
                // (even though not supposed to happen):
                throw (RuntimeException)x;
		}
	}
	
	protected void printOneDummy() throws GenerationException {
		throw new GenerationException("unexpected dummy encountered");
	}
	protected void printOtherDummy() throws GenerationException {
		throw new GenerationException("unexpected dummy encountered");
	}

	protected final IArgumentVisitor ARGUMENT_CODE_GENERATOR = new IArgumentVisitor() {
		public void visit(AbstractMethodInvocation<?> arg) throws GenerationException {
			printMethodInvocation(arg);
		}
		public void leave(AbstractMethodInvocation<?> arg) throws GenerationException {
			// NOP
		}
		
		public void visit(ConstructorInvocation conjunct) throws GenerationException {
			print("new ");
			print(conjunct.getTypeString());
			printExplicitArguments(conjunct);
		}
		public void leave(ConstructorInvocation arg) throws GenerationException {
			// NOP
		}
		
		public void visit(FieldAccess conjunct) throws GenerationException {
			printImplicitArgument(conjunct.getImplicitArgument());
			print(conjunct.getName());
		}
		public void leave(FieldAccess arg) throws GenerationException {
			// NOP
		}
		
		public void visit(BooleanArgument arg) throws GenerationException {
			print(arg);
		}
		public void visit(ByteArgument arg) throws GenerationException {
			print(arg);
		}
		public void visit(CharArgument arg) throws GenerationException {
			print(arg);
		}
		public void visit(ClassNameImplicitArgument arg) throws GenerationException {
			print(arg);
		}
		public void visit(DoubleArgument arg) throws GenerationException {
			print(arg);
		}
		public void visit(FloatArgument arg) throws GenerationException {
			print(arg);
		}
		public void visit(IntArgument arg) throws GenerationException {
			print(arg);
		}
		public void visit(LongArgument arg) throws GenerationException {
			print(arg);
		}
		public void visit(ShortArgument arg) throws GenerationException {
			print(arg);
		}
		public void visit(StringArgument arg) throws GenerationException {
			print(arg);
		}
		
		public void visit(NamelessVariable arg) throws IllegalStateException {
			throw new IllegalStateException();
		}
		public void visit(NullArgument arg) throws GenerationException {
			print("null");
		}
		
		public void visit(Solver arg) throws GenerationException {
			print(arg.getIdentifier());
		}
		
		public void visit(Variable arg) throws GenerationException {
			print(arg.getIdentifier());
		}
		
		public void visit(OneDummy arg) throws GenerationException {
			printOneDummy();
		}
		public void visit(OtherDummy arg) throws GenerationException {
			printOtherDummy();
		}
	
		public boolean recurse() {
			return false;
		}
	
		public boolean explicitVariablesOnly() {
			return false;
		}
	
		public boolean isVisiting() {
			return true;
		}
	
		public void resetVisiting() {
			// NOP
		}
	
		public void reset() throws GenerationException {
			// NOP
		}
	};
}