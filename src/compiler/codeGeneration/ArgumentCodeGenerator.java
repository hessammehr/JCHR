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
import compiler.CHRIntermediateForm.arg.argumented.IBasicArgumented;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.members.AbstractMethodInvocation;
import compiler.CHRIntermediateForm.members.ConstructorInvocation;
import compiler.CHRIntermediateForm.members.FieldAccess;
import compiler.CHRIntermediateForm.solver.Solver;
import compiler.CHRIntermediateForm.variables.NamelessVariable;
import compiler.CHRIntermediateForm.variables.Variable;

public class ArgumentCodeGenerator extends JavaCodeGenerator 
	implements IArgumentVisitor {

	private IArguments arguments;
	
	public ArgumentCodeGenerator(CodeGenerator codeGenerator, IBasicArgumented argumented) {
		this(codeGenerator, argumented.getArguments());
	}
	public ArgumentCodeGenerator(CodeGenerator codeGenerator, IArguments arguments) {
		super(codeGenerator);
		setArguments(arguments);
	}
	
	public void setArguments(IArguments arguments) {
		this.arguments = arguments;
	}
	public IArguments getArguments() {
		return arguments;
	}
	
	@Override
	protected void doGenerate() throws GenerationException {
		printExplicitArguments(getArguments());
	}
	
	public void printImplicitArgument() throws GenerationException {
		if (! getArguments().hasImplicitArgument())
			throw new GenerationException("No implicit argument found");
		printImplicitArgument((IImplicitArgument)getArguments().getArgumentAt(0));
	}
	public void tprintImplicitArgument() throws GenerationException {
		printTabs();
		printImplicitArgument((IImplicitArgument)getArguments().getArgumentAt(0));
	}

	protected void tprintImplicitArgument(IImplicitArgument argument) throws GenerationException {
		printTabs();
		printImplicitArgument(argument);
	}
	protected void printImplicitArgument(IImplicitArgument argument) throws GenerationException {
		printArgument(argument);
		print('.');
	}
	
	protected void printExplicitArguments(IArguments arguments) throws GenerationException {
		print('(');
		int i = arguments.hasImplicitArgument()? 1 : 0;
		for (int j = i; j < arguments.getArity(); j++) {
			if (j != i) print(", ");
			printArgument(arguments.getArgumentAt(j));
		}
		print(')');
	}
	
	protected void printArgument(IArgument argument) throws GenerationException {
		try {
			argument.accept(this);
		} catch (Exception x) {
			if (x instanceof GenerationException)
				throw (GenerationException)x;
			else
				throw new GenerationException(x);
		}
	}
	
	protected void printOneDummy() throws GenerationException {
		throw new GenerationException("unexpected dummy encountered");
	}
	protected void printOtherDummy() throws GenerationException {
		throw new GenerationException("unexpected dummy encountered");
	}

	public void visit(AbstractMethodInvocation<?> arg) throws GenerationException {
		String macro = InlineDefinitionStore.getInlineDefinition(arg.getMethod());
		if (macro != null) {
			print(InlineDefinitionStore.format(macro, recordArguments(arg)));
		} else {
			printImplicitArgument(arg.getImplicitArgument());
			print(arg.getMethodName());
			printExplicitArguments(arg);
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
	public void leave(AbstractMethodInvocation<?> arg) throws GenerationException {
		// NOP
	}
	
	public void visit(ConstructorInvocation conjunct) throws GenerationException {
		print("new ");
		print(conjunct.getTypeString());
		printExplicitArguments(conjunct.getArguments());
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
}