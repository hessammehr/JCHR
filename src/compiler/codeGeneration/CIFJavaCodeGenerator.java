package compiler.codeGeneration;

import static compiler.CHRIntermediateForm.modifiers.Modifier.getAccessStringFor;
import static compiler.CHRIntermediateForm.modifiers.Modifier.isDefaultAccess;
import static compiler.CHRIntermediateForm.modifiers.Modifier.isLocal;

import java.io.BufferedWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import util.StringUtils;

import compiler.CHRIntermediateForm.Handler;
import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.debug.DebugInfo;
import compiler.CHRIntermediateForm.id.Identified;
import compiler.CHRIntermediateForm.modifiers.IModified;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.CHRIntermediateForm.solver.Solver;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.PrimitiveType;
import compiler.CHRIntermediateForm.types.TypeParameter;
import compiler.CHRIntermediateForm.variables.IVariable;
import compiler.CHRIntermediateForm.variables.TypedVariable;
import compiler.CHRIntermediateForm.variables.Variable;
import compiler.CHRIntermediateForm.variables.VariableType;
import compiler.options.Options;

/**
 * Adds a number of auxiliary methods useful when generating Java code
 * starting from (a part of) a CIF.
 */
public abstract class CIFJavaCodeGenerator extends JavaCodeGenerator {
	private ICHRIntermediateForm cif;
	
	private Options options;
	
	public CIFJavaCodeGenerator(ICHRIntermediateForm cif, Options options, BufferedWriter out) {
		super(out);
		setOptions(options);
		setCHRIntermediateForm(cif);
	}

	public CIFJavaCodeGenerator(ICHRIntermediateForm cif, Options options, CodeGenerator codeGenerator) {
		super(codeGenerator);
		setOptions(options);
		setCHRIntermediateForm(cif);
	}
	
	public CIFJavaCodeGenerator(CIFJavaCodeGenerator codeGenerator) {
		super(codeGenerator);
		setOptions(codeGenerator.getOptions());
		setCHRIntermediateForm(codeGenerator.getCHRIntermediateForm());
	}

	public CIFJavaCodeGenerator(ICHRIntermediateForm cif, Options options, Writer out) {
		super(out);
		setOptions(options);
		setCHRIntermediateForm(cif);
	}
	
	public CIFJavaCodeGenerator(ICHRIntermediateForm cif, Options options, BufferedWriter out, boolean terminate) {
		super(out, terminate);
		setOptions(options);
		setCHRIntermediateForm(cif);
	}

	public CIFJavaCodeGenerator(ICHRIntermediateForm cif, Options options, Writer out, boolean terminate) {
		super(out, terminate);
		setOptions(options);
		setCHRIntermediateForm(cif);
	}

	protected void printType(String clazz, TypeParameter... typeParameters) throws GenerationException {
		printType(clazz, Arrays.asList(typeParameters));
	}
	protected void printType(String clazz, Iterable<TypeParameter> typeParameters) throws GenerationException {
		printType(clazz, typeParameters, false);
	}
	
	protected void printFullType(String clazz, TypeParameter... typeParameters) throws GenerationException {
		printType(clazz, Arrays.asList(typeParameters));
	}
	protected void printFullType(String clazz, Iterable<TypeParameter> typeParameters) throws GenerationException {
		printType(clazz, typeParameters, true);
	}
	
	protected void printType(String clazz, Iterable<TypeParameter> typeParameters, boolean full) throws GenerationException {
		print(clazz);
        Iterator<TypeParameter> iter = typeParameters.iterator();
        if (iter.hasNext()) {
        	print('<');
        	do {
        		if (full) 
        			print(iter.next().toFullTypeString());
        		else
        			print(iter.next().toTypeString());
        		if (! iter.hasNext()) break;
        		print(", ");
        	} while (true);
        	print('>');
        }
	}
	
	protected void printAccessModifier(IModified modified) throws GenerationException {
		printAccessModifier(modified, "public ");
	}
	protected void printAccessModifier(IModified modified, String whatIfLocal) throws GenerationException {
		if (isLocal(modified))
			tprint(whatIfLocal);
		else
			tprint(getAccessStringFor(modified));
		if (!isDefaultAccess(modified))
			print(' ');
	}
    
	protected <T extends Identified & IModified> void printGetter(IType fieldType, T field) throws GenerationException {
		printAccessModifier(field);
		prints(fieldType.toTypeString());
		print("get");
		print(StringUtils.capFirst(field.getIdentifier()));
		println("() {");
		ttprint("return this.");
		printcln(field.getIdentifier());
		tprintln("}");
	}
    
    protected void printVariableList(List<? extends IVariable> variables) throws GenerationException {
        if (variables.isEmpty()) return;
        print(variables.get(0));
        for (int i = 1; i < variables.size(); i++) {
            print(", ");
            print(variables.get(i));
        }
    }
	protected void printFullVariableList(List<? extends TypedVariable> variables) throws GenerationException {
        if (variables.isEmpty()) return;
        for (int i = 0; i < variables.size(); i++) {
            if (i != 0) print(", ");
            print(variables.get(i).getTypeString());
            print(' ');
            print(variables.get(i));
        }
    }
	
	public static String getHashCodeCode(IType type, String xxx) {
    	if (PrimitiveType.isPrimitive(type)) {
    		switch ((PrimitiveType)type) {
    			case BOOLEAN:
    				// same hash function as in java.lang.Boolean
    				return '(' + xxx + "? 1231 : 1237)";
    				
    			case INT:
    			case BYTE:
    			case CHAR:
    			case SHORT:
    				return xxx;
    			
    			case FLOAT:
    				// Two NaN floats are not equal according to ==,
    				// so they should have different hash values.
    				// Therefore we use the floatToRawIntBits method, unlike 
    				// in java.lang.Float where floatToIntBits is used
    				return "Float.floatToRawIntBits(" + xxx + ')';
    				
    			case DOUBLE:
    				// as with FLOAT 
    				xxx = "Double.doubleToRawLongBits("+ xxx+ ')';
    			case LONG:
					return "(int)(" + xxx + " ^ (" + xxx + " >>> 32))";
    		}
    	}
    	
    	return xxx + ".hashCode()";
    }
	
	public Options getOptions() {
		return options;
	}
	protected void setOptions(Options options) {
		this.options = options;
	}
	
	/* DECORATOR */
	
	public ICHRIntermediateForm getCHRIntermediateForm() {
		return cif;
	}
	protected void setCHRIntermediateForm(ICHRIntermediateForm cif) {
		this.cif = cif;
	}
	
	
    public Collection<UserDefinedConstraint> getUserDefinedConstraints() {
        return getCHRIntermediateForm().getUserDefinedConstraints();
    }
    public int getNbUdConstraints() {
        return getCHRIntermediateForm().getNbUdConstraints();
    }

    public List<Rule> getRules() {
        return getCHRIntermediateForm().getRules();
    }
    public int getNbRules() {
        return getCHRIntermediateForm().getNbRules();
    }
    
    public Rule getRuleAt(int index) {
        return getRules().get(index);
    }

    public Collection<Solver> getSolvers() {
        return getCHRIntermediateForm().getSolvers();
    }
    public int getNbSolvers() {
        return getCHRIntermediateForm().getNbSolvers();
    }
    
    public Handler getHandler() {
        return getCHRIntermediateForm().getHandler();
    }
    public String getHandlerName() {
        return getCHRIntermediateForm().getHandlerName();
    }
    
    public Set<VariableType> getVariableTypes() {
        return getCHRIntermediateForm().getVariableTypes();
    }
    public int getNbVariableTypes() {
        return getCHRIntermediateForm().getNbVariableTypes();
    }
    
    public DebugInfo getDebugInfo() {
        return getCHRIntermediateForm().getDebugInfo();
    }
    protected boolean hasToTrace() {
    	return getDebugInfo().hasToDebug();
    }

    public HashSet<Variable> getLocalVariables() {
        return getCHRIntermediateForm().getLocalVariables();
    }
    public int getNbLocalVariables() {
        return getCHRIntermediateForm().getNbLocalVariables();
    }
}
