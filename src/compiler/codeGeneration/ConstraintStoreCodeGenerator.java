package compiler.codeGeneration;

import runtime.ConstraintIterable;
import runtime.DoublyLinkedConstraintList;
import runtime.SinglyLinkedConstraintList;
import runtime.list.RehashableDoublyLinkedConstraintList;
import runtime.list.RehashableSinglyLinkedConstraintList;
import util.iterator.GeneratingIterable;

import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;
import compiler.CHRIntermediateForm.variables.FormalVariable;

public abstract class ConstraintStoreCodeGenerator extends JavaCodeGenerator {

	private UserDefinedConstraint constraint;
	
	private ILookupCategory category;
	
	public ConstraintStoreCodeGenerator(CodeGenerator codeGenerator, 
			UserDefinedConstraint constraint, ILookupCategory category) {
		super(codeGenerator);
		setConstraint(constraint);
		setCategory(category);
	}
	
	@Override
	protected final void doGenerate() throws GenerationException {
        throw new GenerationException("unsupported operation");
	}
    
	protected void generateInitialisationCode() throws GenerationException {
		// NOP unless overridden
	}
	
	protected abstract void generateMembers() throws GenerationException;
	
	protected void printInitialisation(String type, String name) throws GenerationException {
		printInitialisation("private final", type, name);
	}
	protected void printInitialisation(String modifiers, String type, String name) throws GenerationException {
		doPrintInitialisation(modifiers, type, name);
		println(';');
	}
	protected void doPrintInitialisation(String modifiers, String type, String name) throws GenerationException {
		printTabs();
		prints(modifiers);
		prints(type);
		print(name);
		print(" = new ");
		print(type);
		print("()");
	}
	
	protected abstract void generateStorageCode() throws GenerationException;
	
	protected final void generateMasterLookupCode() throws GenerationException {
		tprint("return ");
		printCreateIteratorCode();
		println(';');
	}
	
	protected abstract void printCreateIteratorCode() throws GenerationException;
	
	protected abstract void generateFilteredMasterLookupCode() throws GenerationException;
	
    protected void printLookupReturnType() throws GenerationException {
    	print(ConstraintIterable.class.getCanonicalName());
    		print('<'); print(getConstraintTypeName()); print('>');
    }
    protected abstract void generateLookupArgumentList(int lookupTypeIndex) throws GenerationException;
	protected abstract void generateLookupCode(int lookupTypeIndex) throws GenerationException;
    
    protected ILookupType getLookupTypeAt(int index) {
        return getCategory().getLookupTypeAt(index);
    }
	
	protected boolean generateResetCode() throws GenerationException {
		doGenerateResetCode();
		return true;
	}
	
	protected void doGenerateResetCode() throws GenerationException {
		// NOP (default implementation, should be overridden)
	}
    
	public UserDefinedConstraint getConstraint() {
		return constraint;
	}
	public void setConstraint(UserDefinedConstraint constraint) {
		this.constraint = constraint;
	}

	public ILookupCategory getCategory() {
		return category;
	}
	public void setCategory(ILookupCategory category) {
		this.category = category;
	}
    
    protected int getIndexedVariableIndexAt(int index) {
        return getCategory().getVariableIndices()[index];
    }
    protected FormalVariable getIndexedVariableAt(int index) {
        return getConstraint().getFormalVariableAt(getIndexedVariableIndexAt(index));
    }
    protected int getNbIndexedVariables() {
        return getCategory().getNbVariables();
    }
    protected Iterable<FormalVariable> getIndexedFormalVariables() {
    	return new GeneratingIterable<FormalVariable>(getNbIndexedVariables()) {
    		@Override
    		protected FormalVariable generate(int index) {
    			return getIndexedVariableAt(index);
    		}
    	};
    }
    
    protected Iterable<FormalVariable> getAllFormalVariables() {
        return getConstraint().getFormalVariables();
    }

	public String getConstraintTypeName() {
		return getConstraintTypeName(getConstraint());
	}
	public static String getConstraintTypeName(UserDefinedConstraint constraint) {
		return ConstraintCodeGenerator.getConstraintTypeName(constraint);
	}
	public String getConstraintListType() {
		return getConstraintListType(getConstraint());
	}
	public static String getConstraintListType(UserDefinedConstraint constraint) {
		return getConstraintListType(constraint, constraint.mayBeRemoved()
			? DoublyLinkedConstraintList.class
			: SinglyLinkedConstraintList.class
		);
	}
	public static String getRehashableConstraintListType(UserDefinedConstraint constraint) {
		return getConstraintListType(constraint, constraint.mayBeRemoved()
			? RehashableDoublyLinkedConstraintList.class
			: RehashableSinglyLinkedConstraintList.class
		);
	}
	private static String getConstraintListType(UserDefinedConstraint constraint, Class<?> listClass) {
		return new StringBuilder(32)
			.append(listClass.getCanonicalName())
			.append('<')
			.append(getConstraintTypeName(constraint))
			.append('>')
			.toString();
	}
}