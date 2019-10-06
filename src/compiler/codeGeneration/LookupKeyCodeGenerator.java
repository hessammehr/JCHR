package compiler.codeGeneration;

import java.util.SortedSet;

import util.StringUtils;
import util.exceptions.IllegalArgumentException;

import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.BinaryGuardedLookupType;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.BinaryGuardedLookupType.BinaryGuardInfo;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.variables.FormalVariable;

public class LookupKeyCodeGenerator extends KeyCodeGenerator {
    private BinaryGuardInfo[] guards;
    private String type;
    
    public LookupKeyCodeGenerator(
        ConstraintStoreCodeGenerator cg, int lookupTypeIndex, String indexName
    ) {
        this(cg, cg.getConstraint(), cg.getCategory(), lookupTypeIndex, indexName);
    }
    
    public LookupKeyCodeGenerator(
        CodeGenerator codeGenerator, UserDefinedConstraint constraint, 
        ILookupCategory category, int lookupTypeIndex,
        String indexName
    ) {
        super(codeGenerator, indexName, constraint);
        
        ILookupType lookupType = category.getLookupTypeAt(lookupTypeIndex);
        if (lookupType instanceof BinaryGuardedLookupType) {
        	SortedSet<BinaryGuardInfo> guards = ((BinaryGuardedLookupType)lookupType).getGuards();
        	setGuards(guards.toArray(new BinaryGuardInfo[guards.size()]));
    	} else {
        	throw new IllegalArgumentException(lookupType);
        }
        
        setType(getType(indexName, lookupTypeIndex));
    }
    
    @Override
    protected void generateClassSignature() throws GenerationException {
    	tprint("private final ");
    	if (isStatic()) print("static ");
        print("class ");
        print(getType());
        print(" implements LookupKey");
    }
    
    @Override
    protected void generateInitializors() throws GenerationException {
    	if (usesSolvers()) {
    		tprint("public ");
            print(getType());
            print('(');
            print(HandlerCodeGenerator.getHandlerType(getHandler()));
            print(" handler");
            println(") {");
            ttprint("this(");
            generateSolverList("handler");
            println(");");
            tprintln('}');
            nl();
    	}
    	super.generateInitializors();
    }
    
    @Override
    protected void printIsNewKey() throws GenerationException {
    	if (usesSolvers()) {
    		println(';');
    	} else {
    		super.printIsNewKey();
    	}
    }
    
    protected void printInitalisationCode(String keyName) throws GenerationException {
    	if (usesSolvers()) {
    		tprint(keyName); print(" = new "); print(getType()); println("(this);");
    	}
    }
    
    @Override
    protected void generateCloneMethod() throws GenerationException {
    	// no clone method required
    }
    @Override
    protected boolean needsRehashMethod() {
    	return false;
    }
    @Override
    protected void doGenerateRehashMethods() throws GenerationException {
    	throw new InternalError();
    }
    
    @Override
    protected String getOneDummy(int index) {
    	return "((" + StorageKeyCodeGenerator.getType(getIndexName()) + ")other).X" + index;
    }
    @Override
    protected String getOtherDummy(int index) {
    	return "this.X" + index;
    }
    
    @Override
    protected int getArity() {
    	return getGuards().length;
    }
    
    @Override
    protected IType getTypeAt(int index) {
    	return getGuardAt(index).getOtherType();
    }
    
    @Override
    protected IConjunct getEqAt(int index) {
    	return getGuardAt(index).getGuardConjunct();
    }

    protected FormalVariable getFormalVariableAt(int index) {
    	return getConstraint().getFormalVariableAt(getGuardAt(index).getVariableIndex());
    }
    
    protected BinaryGuardInfo getGuardAt(int index) {
    	return getGuards()[index];
    }
    
    
    protected BinaryGuardInfo[] getGuards() {
		return guards;
    }
    public void setGuards(BinaryGuardInfo[] guards) {
		this.guards = guards;
	}
    
    @Override
    public String getType() {
        return type;
    }
    protected void setType(String type) {
        this.type = type;
    }
    public static String getType(String indexName, int lookupTypeIndex) {
        return StringUtils.capFirst(indexName) + "_LookupKey_" + lookupTypeIndex;
    }
}