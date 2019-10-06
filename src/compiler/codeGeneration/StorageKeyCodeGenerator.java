package compiler.codeGeneration;

import static compiler.codeGeneration.AbstractHashIndexCodeGenerator.needsHashObserver;
import static compiler.codeGeneration.ConstraintStoreCodeGenerator.getConstraintListType;
import static compiler.codeGeneration.ConstraintStoreCodeGenerator.getConstraintTypeName;
import runtime.list.RehashableDoublyLinkedConstraintList;
import runtime.list.RehashableSinglyLinkedConstraintList;
import util.StringUtils;

import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.variables.FormalVariable;
import compiler.CHRIntermediateForm.variables.VariableType;

public class StorageKeyCodeGenerator extends KeyCodeGenerator {

	private ILookupCategory category; 
	private String indexValueType;
	
    public StorageKeyCodeGenerator(ConstraintStoreCodeGenerator cg,
        String indexName, String indexValueType
    ) { 
        this(cg, cg.getConstraint(), cg.getCategory(), indexName, indexValueType);
    }
    
	public StorageKeyCodeGenerator(
		CodeGenerator codeGenerator,
		UserDefinedConstraint constraint, ILookupCategory category, 
		String indexName, String indexValueType
	) {
		super(codeGenerator, indexName, constraint);
		setCategory(category);
		setIndexValueType(indexValueType);
	}
	
	@Override
    protected void generateClassSignature() throws GenerationException {
		tprint("private final ");
		if (isStatic()) print("static ");
		print("class ");
		println(getType());
		ttprint("extends "); 
		if (needsRehashMethod()) {
			print(getConstraint().mayBeRemoved()
				? RehashableDoublyLinkedConstraintList.class.getCanonicalName()
				: RehashableSinglyLinkedConstraintList.class.getCanonicalName()
			);
			print('<'); 
			print(getConstraintTypeName(getConstraint()));
			println('>');
		} else
			println(getConstraintListType(getConstraint()));
		
		ttprint("implements Cloneable<"); print(getType()); print('>');
	}
	
	@Override
	protected boolean needsRehashMethod() {
		for (int i = 0; i < getArity(); i++)
			if (needsHashObserver(getVariableAt(i)))
				return true;
		return false;
	}
	
	@Override
    protected void doGenerateRehashMethods() throws GenerationException {
		tprintln("public boolean rehash() {");
		incNbTabs();
		tprint(getIndexName()); println(".remove(this);");
		tprintln("if (head == null || isEmpty()) return false;");
		generateHashCodeInitialization();
		tprint("final "); print(getIndexValueType()); print(" temp = ");
			print(getIndexName()); println(".insertOrGet(this);");
		tprintln("if (temp == null) return true;",
				"temp.mergeWith(this);",
				"head = null;",
				"return false;"
		);
		decNbTabs();
		tprintln('}');
		nl();
		tprintln("public boolean isSuperfluous() {");
		ttprintln("return (head == null || isEmpty());");
		tprintln('}');
	}
	
	@Override
	protected String getOneDummy(int index) {
		return "this.X" + index;
	}
	
	@Override
	protected String getOtherDummy(int index) {
		return "((" + getType() + ")other).X" + index;
	}
	
    @Override
    protected void generateCloneMethod() throws GenerationException {
        tprintOverride();
        tprint("public ");
        print(getType());
        println(" clone() {");
        incNbTabs();
        tprintln("try {");
        ttprint("return (");
        print(getType());
        println(")super.clone();");
        tprintln(
            "} catch (CloneNotSupportedException cnse) {",
                "\tthrow new InternalError();",
            "}");
        decNbTabs();
        tprintln('}');
    }
    
	
	@Override
    protected int getArity() {
		return getCategory().getNbVariables();
	}
	protected FormalVariable getVariableAt(int i) {
		return getConstraint().getFormalVariableAt(getCategory().getVariableIndices()[i]);
	}
	protected VariableType getVariableTypeAt(int i) {
		return getConstraint().getFormalVariableTypeAt(getCategory().getVariableIndices()[i]);
	}
	
	@Override
	protected IType getTypeAt(int index) {
		return getVariableTypeAt(index).getType();
	}
	@Override
	protected IConjunct getEqAt(int index) {
		return getVariableTypeAt(index).getEq();
	}

	public ILookupCategory getCategory() {
		return category;
	}
	public void setCategory(ILookupCategory category) {
		this.category = category;
	}

	public String getIndexValueType() {
		return indexValueType;
	}
	public void setIndexValueType(String indexValueType) {
		this.indexValueType = indexValueType;
	}
	
	@Override
    public String getType() {
		return getType(getIndexName());
	}
	public static String getType(String indexName) {
		return StringUtils.capFirst(indexName).concat("_StorageKey");
	}
}