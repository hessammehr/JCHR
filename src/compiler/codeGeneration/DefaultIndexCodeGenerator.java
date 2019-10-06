package compiler.codeGeneration;

import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.DefaultLookupCategory;

public class DefaultIndexCodeGenerator extends ConstraintStoreCodeGenerator {

	public DefaultIndexCodeGenerator(CodeGenerator codeGenerator, UserDefinedConstraint constraint) {
		super(codeGenerator, constraint, DefaultLookupCategory.getInstance());
	}
	
	public String getConstraintListName() {
		return getConstraintListName(getConstraint());
	}
	public static String getConstraintListName(UserDefinedConstraint constraint) {
		return "$$" + constraint.getIdentifier() + "ConstraintList";
	}

	@Override
	protected void generateMembers() throws GenerationException {
		printInitialisation(getConstraintListType(), getConstraintListName());
	}
	
	@Override
	protected void generateStorageCode() throws GenerationException {
		tprint(getConstraintListName());
		println(".addFirst(constraint);");
	}
	
	@Override
	protected void printCreateIteratorCode() throws GenerationException {
        print(getConstraintListName()); print(".iterator()");
	}
	
	@Override
	protected void generateFilteredMasterLookupCode() throws GenerationException {
		tprint("return new FilteredIterable<");
		print(getConstraintTypeName());
		print(">(");
		print(getConstraintListName());
		println(", filter);");
	}
	
    @Override
    protected void generateLookupArgumentList(int lookupTypeIndex) throws GenerationException {
        // no arguments
    }
	@Override
	protected void generateLookupCode(int lookupTypeIndex) throws GenerationException {
        tprint("return ");
		printcln(getConstraintListName());
	}
	
	@Override
	protected void doGenerateResetCode() throws GenerationException {
		tprint("terminateAll(");
		print(getConstraintListName());
		println(");");
	}
}
