package compiler.codeGeneration;

import static compiler.codeGeneration.ConjunctCodeGenerator.GUARD;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import util.collections.Empty;

import compiler.CHRIntermediateForm.Handler;
import compiler.CHRIntermediateForm.arg.visitor.NOPLeafArgumentVisitor;
import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.solver.Solver;
import compiler.CHRIntermediateForm.types.IType;

public abstract class KeyCodeGenerator extends JavaCodeGenerator {
    private UserDefinedConstraint constraint;
    private Set<Solver> solvers;
    private String indexName;
    
    public KeyCodeGenerator(ConstraintStoreCodeGenerator codeGenerator, String indexName) {
        this(codeGenerator, indexName, codeGenerator.getConstraint());
    }
    
    public KeyCodeGenerator(CodeGenerator codeGenerator, String indexName, UserDefinedConstraint constraint) {
        super(codeGenerator);
    	setConstraint(constraint);
    	setIndexName(indexName);
    }
    
    @Override
    protected void doGenerate() throws GenerationException {
    	generateClassSignature(); 
        println(" {"); 
        incNbTabs();
    		generateMembers();
    		nl();
    		generateInitializors();
    		nl();
    		generateRehashMethods();
    		nl();
    		generateEqualsMethod();
            nl();
            generateHashCodeMethod();
            nl();
            generateCloneMethod();
        decNbTabs(); 
        tprintln('}');
    }
    
    protected boolean isStatic() {
    	return !getHandler().hasTypeParameters()
    		&& !needsRehashMethod();
    }
    
    protected abstract void generateClassSignature() throws GenerationException;
    
    protected void generateMembers() throws GenerationException {
        for (int i = 0; i < getArity(); i++) {
            tprint("protected ");
            prints(getTypeAt(i).toTypeString());
            print('X');
            print(i);
            println(';');
        }
        
        for (Solver solver : getSolvers()) {
        	tprint("private final ");
        	prints(solver.toTypeString());
        	printcln(solver.getIdentifier());
        }
        
        tprintln("private int hashCode;");
    }
    
    protected void generateInitializors() throws GenerationException {
    	tprint("public ");
        print(getType());
        print('(');
        final boolean usesSolvers = generateSolverList();
        println(") {");
        incNbTabs();
        if (usesSolvers)
        	generateSolverAssignments();
        else
        	tprintln("// NOP");
        decNbTabs();
        tprintln('}');
        
        nl();
        
        tprint("public "); print(getType());
        generateInitializorCode(usesSolvers);
        nl();
        tprint("public void init");
        generateInitializorCode(false);
    }
    
    protected void printIsNewKey() throws GenerationException {
		print(" = new ");
		print(getType());
		print('(');
		generateSolverList(false);
		println(");");
	}
    
    protected boolean generateSolverList() throws GenerationException {
    	return generateSolverList(true, null);
    }
	protected boolean generateSolverList(boolean types) throws GenerationException {
    	return generateSolverList(types, null);
    }
	protected boolean generateSolverList(String implicitArg) throws GenerationException {
    	return generateSolverList(false, implicitArg);
    }
	
	private boolean generateSolverList(boolean types, String implicitArg) throws GenerationException {
		Iterator<Solver> iter = getSolvers().iterator();
    	if (!iter.hasNext()) return false;
    	
    	printSolver(iter.next(), types, implicitArg);
		while (iter.hasNext()) {
			print(", ");
			printSolver(iter.next(), types, implicitArg);
		}
		
		return true;
	}
	private void printSolver(Solver solver, boolean types, String implicitArg) throws GenerationException {
		if (types) prints(solver.toTypeString());
		else if (implicitArg != null) { 
			print(implicitArg); 
			print('.');
		}
		print(solver.getIdentifier());
	}
    
    private void generateSolverAssignments() throws GenerationException {
    	for (Solver solver : getSolvers()) {
    		tprint("this.");
    		print(solver.getIdentifier());
    		print(" = ");
    		printcln(solver.getIdentifier());
    	}
    }
    
    private void generateInitializorCode(boolean generateSolvers) throws GenerationException {
        print('(');
        
        if (generateSolvers) 
        	generateSolvers = generateSolverList();
        
        for (int i = 0; i < getArity(); i++) {
            if (i != 0 || generateSolvers) print(", ");
            prints(getTypeAt(i).toTypeString());
            print('X');
            print(i);
        }
        println(") {");
        incNbTabs();
        
        for (int i = 0; i < getArity(); i++) {
            tprint("this.X");
            print(i);
            print(" = X");
            print(i);
            println(";");
        }
        
        if (generateSolvers) generateSolverAssignments();
        
        generateHashCodeInitialization();
        
        decNbTabs();
        tprintln('}');
    }
    
    protected void generateHashCodeInitialization() throws GenerationException {
        tprint("int hashCode = ");
        for (int i = 0; i < getArity(); i++) print("37 * (");
        print(23);
        for (int i = 0; i < getArity(); i++) {
            print(") + ");
            print(CIFJavaCodeGenerator.getHashCodeCode(getTypeAt(i), "X" + i));
        }
        println(';');
        tprintln(
			"hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);",
        	"hashCode ^= (hashCode >>> 7) ^ (hashCode >>> 4);"
		);
        tprintln("this.hashCode = hashCode;");
    }
    
    protected final void generateRehashMethods() throws GenerationException {
    	if (needsRehashMethod()) doGenerateRehashMethods();
    }
    
    protected abstract boolean needsRehashMethod();
    
    protected abstract void doGenerateRehashMethods() throws GenerationException;
    
    protected void generateEqualsMethod() throws GenerationException {
        tprintOverride();
        tprintln("public boolean equals(final Object other) {");
        incNbTabs();
        tprint("return ");
        for (int i = 0; i < getArity(); i++) {
            if (i != 0) { println(" &&"); tprintTabs(); }
            final int index = i;
            
            new ConjunctCodeGenerator(this, getEqAt(i), GUARD) {
                @Override
                protected void printOneDummy() throws GenerationException {
                	print(getOneDummy(index));
                }
                @Override
                protected void printOtherDummy() throws GenerationException {
                	print(getOtherDummy(index));
                }
            }.generate();
        }
        println(';');
        decNbTabs();
        tprintln('}');
    }
    
    protected abstract void generateCloneMethod() throws GenerationException;
    
    protected void generateHashCodeMethod() throws GenerationException {
        tprintln(
            "@Override",
            "public int hashCode() {",
            "\treturn hashCode;",
            "}"
        );
    }
    
    protected static class SolverGetter extends NOPLeafArgumentVisitor {
    	private final Set<Solver> solvers;
    	
    	public SolverGetter(Set<Solver> solvers) {
    		super(false);
			this.solvers = solvers;
		}
    	
    	@Override
		public void visit(Solver solver) throws Exception {
    		solvers.add(solver);
    	}
    }
    
    public UserDefinedConstraint getConstraint() {
        return constraint;
    }
    public Handler getHandler() {
    	return getConstraint().getHandler();
    }
    protected void setConstraint(UserDefinedConstraint constraint) {
        this.constraint = constraint;
    }
    
    
    protected boolean usesSolvers() {
    	return isStatic() && !getSolvers().isEmpty();
    }
    protected Set<Solver> getSolvers() {
    	if (!isStatic()) return Empty.<Solver>getInstance();
    	if (solvers == null) initSolvers();
		return solvers;
	}
    protected void initSolvers() {
    	try {
			Set<Solver> solvers = new HashSet<Solver>();
			SolverGetter getter = new SolverGetter(solvers);
			for (int i = 0; i < getArity(); i++)
				getEqAt(i).accept(getter);
			setSolvers(solvers);
    	} catch (Exception x) {
    		throw new InternalError();
    	}
    }
    protected void setSolvers(Set<Solver> solvers) {
		this.solvers = solvers;
	}
    
    public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

    protected abstract int getArity();
    
    protected abstract IType getTypeAt(int index);
    
    protected abstract IConjunct getEqAt(int index);
    
    protected abstract String getOneDummy(int index);
    
    protected abstract String getOtherDummy(int index);
    
    public abstract String getType();
}