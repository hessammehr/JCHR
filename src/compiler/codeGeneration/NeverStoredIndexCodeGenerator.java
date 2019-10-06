package compiler.codeGeneration;

import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.NeverStoredLookupCategory;

public class NeverStoredIndexCodeGenerator extends ConstraintStoreCodeGenerator {

	public NeverStoredIndexCodeGenerator(CodeGenerator codeGenerator, UserDefinedConstraint constraint) {
		super(codeGenerator, constraint, NeverStoredLookupCategory.getInstance());
	}
	
	@Override
	protected void generateMembers() throws GenerationException {
		// NOP
	}
	
	@Override
	protected void generateStorageCode() throws GenerationException {
		tprintln("throw new UnsupportedOperationException(\"This constraint should never be stored\");");
	}
	
	@Override
	protected void printCreateIteratorCode() throws GenerationException {
		print("EmptyIterator.getInstance()");
	}
	
	@Override
	protected void generateFilteredMasterLookupCode() throws GenerationException {
		tprintln("return Empty.getInstance();");
	}
	
	@Override
	protected void printLookupReturnType() throws GenerationException {
		throw new GenerationException("unsupported operation for this index");
	}
	@Override
    protected void generateLookupArgumentList(int lookupTypeIndex) throws GenerationException {
        throw new GenerationException("unsupported operation for this index");
    }
	@Override
	protected void generateLookupCode(int lookupTypeIndex) throws GenerationException {
        throw new GenerationException("unsupported operation for this index");
	}
	
	@Override
	protected boolean generateResetCode() throws GenerationException {
		return false;
	}
}
