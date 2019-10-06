package compiler.codeGeneration;

import runtime.hash.HashIndex;

import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;

public class SSHashIndexCodeGenerator extends AbstractHashIndexCodeGenerator {

	public SSHashIndexCodeGenerator(
        CodeGenerator codeGenerator, 
		UserDefinedConstraint constraint, ILookupCategory category
    ) {
		super(codeGenerator, constraint, category);
	}
	
    @Override
    protected String getHashIndexModifiers() {
        return "protected final";
    }
    @Override
    protected LookupKeyCodeGenerator getLookupKeyCodeGenerator(int i) {
        return new SSLookupKeyCodeGenerator(this, i, getIndexName());
    }
	
	@Override
	protected void generateStorageCode() throws GenerationException {
        printAddHashObserverCode("constraint");
        tprint(getIndexName()); println(".putFirstTime(constraint);");
	}
    
	@Override
	protected void printCreateIteratorCode() throws GenerationException {
        print(getIndexName()); print(".iterator()");
	}
	
	@Override
	protected void generateFilteredMasterLookupCode() throws GenerationException {
        tprint("return new FilteredIterable<"); 
            print(getConstraintTypeName());
        println(">(");
            ttprint(getIndexName()); println(", filter");
        tprintln(");");
	}
	
	@Override
	protected void printLookupReturnType() throws GenerationException {
		print(getConstraintTypeName());
	}
	
	@Override
	protected void doGenerateResetCode() throws GenerationException {
        tprint("terminateAll(");
    	print(getIndexName()); 
    	println(".iterator());");
    }
    
	@Override
    public String getIndexType() {
		return getSSHashIndexType(getConstraint());
	}
	public static String getSSHashIndexType(UserDefinedConstraint constraint) {
		return HashIndex.class.getSimpleName()
			+ '<' + getConstraintTypeName(constraint) + '>';
	}
}