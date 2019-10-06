package compiler.codeGeneration;

import static compiler.codeGeneration.ConstraintCodeGenerator.getConstraintTypeName;
import static compiler.codeGeneration.ConstraintCodeGenerator.getVariableGetterName;

import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;

public class SSLookupKeyCodeGenerator extends LookupKeyCodeGenerator {

    public SSLookupKeyCodeGenerator(ConstraintStoreCodeGenerator cg, int lookupTypeIndex, String indexName) {
        super(cg, lookupTypeIndex, indexName);
    }
    public SSLookupKeyCodeGenerator(CodeGenerator codeGenerator, UserDefinedConstraint constraint, ILookupCategory category, int lookupTypeIndex, String indexName) {
		super(codeGenerator, constraint, category, lookupTypeIndex, indexName);
	}
    
    @Override
    protected boolean isStatic() {
    	return false;
    }
	
	@Override
	protected String getOneDummy(int index) {
		return "((" + getConstraintTypeName(getConstraint()) + ")other)." 
			+ getVariableGetterName(getFormalVariableAt(index)) + "()";
	}
}
