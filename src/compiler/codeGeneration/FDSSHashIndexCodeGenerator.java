package compiler.codeGeneration;

import runtime.hash.FDSSHashIndex;

import static compiler.codeGeneration.ConstraintCodeGenerator.*;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;
import compiler.CHRIntermediateForm.variables.FormalVariable;

public class FDSSHashIndexCodeGenerator extends SSHashIndexCodeGenerator {
	
	public FDSSHashIndexCodeGenerator(
        CodeGenerator codeGenerator, 
		UserDefinedConstraint constraint, ILookupCategory category
    ) {
		super(codeGenerator, constraint, category);
	}
	
	@Override
	protected void printHashIndexInitialisation() throws GenerationException {
		doPrintInitialisation(getHashIndexModifiers(), getIndexType(), getIndexName());
		openAccolade();
		tprintOverride();
		tprint("protected final int hash(");
		print(getConstraintTypeName()); 
		println(" constraint) {");
		
		ttprint("int hashCode = ");
		
		int arity = getCategory().getNbVariables();
        for (int i = 0; i < arity; i++) print("37 * (");
        print(23);
        for (int i = 0; i < arity; i++) {
            print(") + ");
            FormalVariable var = getIndexedVariableAt(i);
            print(getHashCodeCode(var.getType(), "constraint." + var.getIdentifier()));
        }
        println(';');
        ttprintln(
			"hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);",
        	"hashCode ^= (hashCode >>> 7) ^ (hashCode >>> 4);"
		);
        ttprintln("return hashCode;");
		tprintln('}');
		decNbTabs();
		tprintln("};");
	}
    
	@Override
    public String getIndexType() {
		return getFDSSHashIndexType(getConstraint());
	}
	public static String getFDSSHashIndexType(UserDefinedConstraint constraint) {
		return FDSSHashIndex.class.getSimpleName()
			+ '<' + getConstraintTypeName(constraint) + '>';
	}
}
