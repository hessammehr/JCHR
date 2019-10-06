package compiler.codeGeneration;

import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.BinaryGuardedLookupType;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.BinaryGuardedLookupType.BinaryGuardInfo;
import compiler.CHRIntermediateForm.variables.FormalVariable;

public abstract class AbstractHashIndexCodeGenerator extends ConstraintStoreCodeGenerator {

	public AbstractHashIndexCodeGenerator(
        CodeGenerator codeGenerator, 
		UserDefinedConstraint constraint, ILookupCategory category
    ) {
		super(codeGenerator, constraint, category);
	}
	
	@Override
	protected void generateInitialisationCode() throws GenerationException {
		for (int i = 0; i < getCategory().getNbLookupTypes(); i++)
			getLookupKeyCodeGenerator(i).printInitalisationCode(getLookupKeyName(i));
	}
	
	@Override
	protected void generateMembers() throws GenerationException {
		printHashIndexInitialisation();
        
        KeyCodeGenerator generator;
        for (int i = 0; i < getCategory().getNbLookupTypes(); i++) {
            nl();
            (generator = getLookupKeyCodeGenerator(i)).generate();
            nl();
            printInitialisation("private final", generator, getLookupKeyName(i));
        }
	}
	
	protected void printHashIndexInitialisation() throws GenerationException {
		printInitialisation(getHashIndexModifiers(), getIndexType(), getIndexName());
	}
	
	protected void printInitialisation(String modifiers, KeyCodeGenerator generator, String name) throws GenerationException {
		tprint(modifiers);
		print(' ');
		prints(generator.getType());
		print(name);
		generator.printIsNewKey();
	}
	
    protected abstract String getHashIndexModifiers();
    
    protected abstract LookupKeyCodeGenerator getLookupKeyCodeGenerator(int i);
    
    protected void printGetVariable(FormalVariable variable) throws GenerationException {
        print("constraint.");
        print(ConstraintCodeGenerator.getVariableGetterName(variable));
        print("()");
    }
    protected void printAddHashObserverCode(String observer) throws GenerationException {
        for (FormalVariable variable : getIndexedFormalVariables()) {
            if (needsHashObserver(variable)) {
            	if (variable.getVariableType().isHashObservable()) {
            		printTabs();
            		printGetVariable(variable);
            		printf(".addHashObserver(%s);%n", observer);
            	} else {
            		throw new GenerationException("Cannot add hash observer to " + variable);
            	}
            }
        }
    }
    
    public static boolean needsHashObserver(FormalVariable formalVariable) {
    	return !formalVariable.isFixed();
    }
	
    @Override
    protected void generateLookupArgumentList(int lookupTypeIndex) throws GenerationException {
        printLookupArgumentList(lookupTypeIndex, true);
    }
    protected void printLookupArgumentList(int lookupTypeIndex, boolean printTypes) throws GenerationException {
        final int n = getNbIndexedVariables();
        
        final BinaryGuardInfo[] guardInfos = new BinaryGuardInfo[n];
        getLookupTypeAt(lookupTypeIndex).getGuards().toArray(guardInfos);
        
        if (n > 0) {
        	int i = 0;
        	do {
	            if (printTypes) prints(guardInfos[i].getOtherTypeString());
	            print(getIndexedVariableAt(i).getIdentifier());
	            print("_value");
	            if (++i < n) print(','); else break;
        	} while (true);
        } 
    }
    
    @Override
    protected BinaryGuardedLookupType getLookupTypeAt(int index) {
        return (BinaryGuardedLookupType)super.getLookupTypeAt(index);
    }
    
	@Override
	protected void generateLookupCode(int lookupTypeIndex) throws GenerationException {
        String key = getLookupKeyName(lookupTypeIndex);
        
        tprint(key); print(".init("); 
            printLookupArgumentList(lookupTypeIndex, false); 
        println(");");
        tprint("return "); print(getIndexName()); print(".get("); print(key); println(");");
	}
    
    public String getLookupKeyName(int lookupTypeIndex) {
        return getLookupKeyName(getIndexName(), lookupTypeIndex);
    }
    public static String getLookupKeyName(String hashIndexName, int lookupTypeIndex) {
        return hashIndexName + "_LookupKey_" + lookupTypeIndex;
    }
	
	public abstract String getIndexType();
	
	public String getIndexName() {
		return getHashIndexName(getConstraint(), getCategory());
	}
	public static String getHashIndexName(UserDefinedConstraint constraint, ILookupCategory category) {
		return "$$" 
			+ constraint.getIdentifier()
			+ "HashIndex_"
			+ constraint.getIndexOf(category);
	}
}