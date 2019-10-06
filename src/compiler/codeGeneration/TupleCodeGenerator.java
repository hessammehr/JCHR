package compiler.codeGeneration;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import runtime.history.TuplePropagationHistory;


public class TupleCodeGenerator extends JavaCodeGenerator {
    private int arity;
    
    public TupleCodeGenerator(BufferedWriter out, int arity) {
        super(out);
        setArity(arity);
    }
    public TupleCodeGenerator(Writer out, int arity) {
        super(out);
        setArity(arity);
    }
    
    protected void setArity(int arity) {
        this.arity = arity;
    }
    public int getArity() {
        return arity;
    }

    @Override
    protected void doGenerate() throws GenerationException {
        generateHeader();
        nl();
        generatePackageDeclaration();
        nl();
        generateImports();
        nl();
        generateGeneratedAnnotation();
        generateClassSignature();
        println(" {");
        incNbTabs();
            generateMembers();
            nl();
            generateConstructor();
            nl();
            generateEqualsMethods();
            nl();
            generateToStringMethod();
        decNbTabs();
        println('}');
    }
    
    protected void generateHeader() throws GenerationException {
    	new HeaderCodeGenerator(this).generate();
    }
    protected void generatePackageDeclaration() throws GenerationException {
        print("package ");
        print(getTuplePackage().getName());
        println(';');
    }
    
    protected void generateImports() throws GenerationException {
    	printImport(TuplePropagationHistory.class);
    }
    
    protected void generateClassSignature() throws GenerationException {
        print("public final class "); print(getTupleClassName());
        print(" extends "); 
        print(TuplePropagationHistory.class.getSimpleName()); 
        print(".Tuple"); 
    }
    
    protected void generateMembers() throws GenerationException {
        for (int i = 1; i <= getArity(); i++) {
            tprint("private final int X"); print(i); println(';');
        }
    }
    
    protected void generateConstructor() throws GenerationException {
        tprint("public "); print(getTupleClassName()); print('(');
        print("int X1");
        for (int i = 2; i <= getArity(); i++) {
            print(", int X"); print(i);
        }
        println(") {");
        
        for (int i = 1; i <= getArity(); i++) {
            ttprint("this.X"); print(i); print(" = X"); printcln(i);
        }
        ttprint("int hash = ");
        for (int i = 1; i <= getArity(); i++) print("37 * (");
        print(23);
        for (int i = 1; i <= getArity(); i++) {
            print(") + X"); print(i);
        }
        println(';');
        ttprintln("hash ^= (hash >>> 20) ^ (hash >>> 12);");
        ttprintln("this.hash = hash ^ (hash >>> 7) ^ (hash >>> 4);");
        tprintln('}');
    }
    
    protected void generateEqualsMethods() throws GenerationException {
        generateGenericEqualsMethod();
        nl();
        generateSpecificEqualsMethod();
    }
    
    protected void generateGenericEqualsMethod() throws GenerationException {
        tprintln("/**");
        tprintln(" * {@inheritDoc}");
        tprintln(" *");
        tprint(  " * @pre other instanceof "); println(getTupleClassName());
        tprintln(" */");
        tprintOverride();
        tprintln("public boolean equals(Object other) {");
        ttprint("return ");
        for (int i = 1; i <= getArity(); i++) {
            if (i != 1) { nl(); ttprint("    && "); }
            print("(this.X"); print(i); print(" == (("); print(getTupleClassName()); 
                print(")other).X"); print(i); print(')');
        }
        println(';');
        tprintln('}');
    }
    protected void generateSpecificEqualsMethod() throws GenerationException {
        tprint("public boolean equals("); print(getTupleClassName()); println(" other) {");
        ttprint("return ");
        for (int i = 1; i <= getArity(); i++) {
            if (i != 1) { nl(); ttprint("    && "); }
            print("(this.X"); print(i); print(" == "); 
                print("other.X"); print(i); print(')');
        }
        println(';');
        tprintln('}');
    }
    
    protected void generateToStringMethod() throws GenerationException {
        tprintOverride();
        tprintln("public String toString() {");
        ttprint("final StringBuilder result = new StringBuilder(");
            print(getArity() << 4); printcln(')');
        ttprintln("result.append('(');");
        for (int i = 1; i <= getArity(); i++) {
            ttprint("result.append(X"); print(i); println(");");
            if (i != getArity()) ttprintln("result.append(\", \");");
        }
        ttprintln("result.append(')');");
        ttprintln("return result.toString();");
        tprintln('}');
    }
    
    public static Package getTuplePackage() {
        return TuplePropagationHistory.class.getPackage();
    }
    
    public static String getTupleFQN(int arity) {
        return getTuplePackage().getName() + '.' + getTupleClassName(arity);
    }
    
    public static String getTupleClassName(int arity) {
        return "Tuple" + arity;
    }
    public String getTupleClassName() {
        return getTupleClassName(getArity());
    }
    
    public static void generate(int... arities) throws GenerationException, IOException {
    	for (int i = 0; i < arities.length; i++)
    		CodeGeneration.generateTupleSourceFiles(arities);
    }
    
}