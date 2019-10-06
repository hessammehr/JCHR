package compiler.codeGeneration;

import java.util.Iterator;

import runtime.hash.HashIndex;

import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;
import compiler.CHRIntermediateForm.variables.FormalVariable;

public class HashIndexCodeGenerator extends AbstractHashIndexCodeGenerator {

	public HashIndexCodeGenerator(
        CodeGenerator codeGenerator, 
		UserDefinedConstraint constraint, ILookupCategory category
    ) {
		super(codeGenerator, constraint, category);
	}
	
	@Override
	protected void generateMembers() throws GenerationException {
        super.generateMembers();
		nl();
        KeyCodeGenerator generator = createStorageKeyGenerator();
		generator.generate();
        nl();
        printInitialisation("private", generator, getStorageKeyName());
	}
	
	protected KeyCodeGenerator createStorageKeyGenerator() {
		return new StorageKeyCodeGenerator(this, getIndexName(), getStorageKeyType());
	}
    
    @Override
    protected String getHashIndexModifiers() {
        return "final";
    }
    @Override
    protected LookupKeyCodeGenerator getLookupKeyCodeGenerator(int i) {
        return new LookupKeyCodeGenerator(this, i, getIndexName());
    }
    
	@Override
	protected void generateStorageCode() throws GenerationException {
		String key = getStorageKeyName();
        
        tprintln('{');
		incNbTabs();
		tprint(getStorageKeyType()); println(" list;");
		tprint(key); println(".init(");
        Iterator<FormalVariable> variables = getIndexedFormalVariables().iterator();
        while (variables.hasNext()) {
            tprintTabs();
            printGetVariable(variables.next());
            if (variables.hasNext()) println(','); else println();
        }
        tprintln(");");
        
        tprint("if ((list = "); 
        print(getIndexName()); print(".insertOrGet("); print(key);
        println(")) == null) {");
        incNbTabs();
        tprint(key); println(".addFirst(constraint);");
        printAddHashObserverCode(key);
        tprint(key); createStorageKeyGenerator().printIsNewKey();
        decNbTabs();
        tprintln("} else {   // list != null");
        incNbTabs();
        tprintln("list.addFirst(constraint);");
        printAddHashObserverCode("list");
        decNbTabs();
        tprintln('}');
        
		decNbTabs();
		tprintln('}');
	}
    
	@Override
	protected void printCreateIteratorCode() throws GenerationException {
		print("new NestedIterator<"); 
		print(getConstraintTypeName()); 
		print(">("); print(getIndexName());  print(')');
	}
	
	@Override
	protected void generateFilteredMasterLookupCode() throws GenerationException {
        tprint("return new FilteredIterable<"); 
            print(getConstraintTypeName());
        println(">(");
            ttprint("new NestedIterable<");
                print(getConstraintTypeName());
            print(">("); print(getIndexName()); println("), filter");
        tprintln(");");
	}
	
	@Override
	protected void doGenerateResetCode() throws GenerationException {
        tprintln('{');
        incNbTabs();
        tprint("Iterator<? extends "); print(getConstraintListType()); print("> outer = ");
            print(getIndexName()); println(".iterator();");
        tprintln("while (outer.hasNext()) terminateAll(outer.next());");
        decNbTabs();
        tprintln('}');
    }
    
    public String getStorageKeyName() {
        return getStorageKeyName(getIndexName());
    }
    public static String getStorageKeyName(String hashIndexName) {
        return hashIndexName.concat("_StorageKey");
    }
    public String getStorageKeyType() {
        return StorageKeyCodeGenerator.getType(getIndexName());
    }
    
	@Override
    public String getIndexType() {
		return getHashIndexType(getIndexName(), getConstraint());
	}
	public static String getHashIndexType(String indexName, UserDefinedConstraint constraint) {
		return HashIndex.class.getSimpleName()
			+ '<' + StorageKeyCodeGenerator.getType(indexName) + '>';
	}
}