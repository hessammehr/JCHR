package compiler.codeGeneration;

import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;

public class ConstraintStoreCodeGeneratorFactory {
	private ConstraintStoreCodeGeneratorFactory() { /* FACTORY CLASS */ }
	
	public static ConstraintStoreCodeGenerator getInstance(
        CodeGenerator base, 
		UserDefinedConstraint constraint, ILookupCategory category
    ) {
		switch (category.getIndexType()) {
			case DEFAULT:
				return new DefaultIndexCodeGenerator(base, constraint);
				
			case NEVER_STORED:
				return new NeverStoredIndexCodeGenerator(base, constraint);
				
			case HASH_MAP:
				return new HashIndexCodeGenerator(base, constraint, category);
			
			case SS_HASH_MAP:
				return new SSHashIndexCodeGenerator(base, constraint, category);
				
			case FD_SS_HASH_MAP:
				return new FDSSHashIndexCodeGenerator(base, constraint, category);
                
			default:
				throw new InternalError();
		}
	}
}