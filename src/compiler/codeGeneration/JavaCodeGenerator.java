package compiler.codeGeneration;

import java.io.BufferedWriter;
import java.io.Writer;
import java.util.Date;

import be.kuleuven.jchr.About;

import util.DateUtils;


/**
 * A class that groups together several convenience methods that
 * come in handy when generating Java code. Java code will be all
 * we are ever generating probably, but this way these methods
 * are better grouped...
 * 
 * @author Peter Van Weert
 */
public abstract class JavaCodeGenerator extends CodeGenerator {

    public JavaCodeGenerator(BufferedWriter out) {
        super(out);
    }

    public JavaCodeGenerator(CodeGenerator codeGenerator) {
        super(codeGenerator);
    }

    public JavaCodeGenerator(Writer out) {
        super(out);
    }
    
    public JavaCodeGenerator(BufferedWriter out, boolean terminate) {
		super(out, terminate);
	}

	public JavaCodeGenerator(Writer out, boolean terminate) {
		super(out, terminate);
	}

	protected void printLiteral(char c) throws GenerationException {
        print('\'');
        print(c);
        print('\'');
    }
    protected void printLiteral(String s) throws GenerationException {
        print('"');
        print(s);
        print('"');
    }
    
    protected void openAccolade() throws GenerationException {
    	println(" {");
    	incNbTabs();
    }
    protected void closeAccolade() throws GenerationException {
    	decNbTabs();
    	tprintln('}');
    }
    
    protected void tprintOverride() throws GenerationException {
        tprintln("@Override");
    }
    protected void tprintSuppress(String warning) throws GenerationException {
    	tprint("@SuppressWarnings(\""); print(warning); println("\")");
    }
    protected void tprintSuppressUnchecked() throws GenerationException {
    	tprintSuppress("unchecked");
    }
    
    
    /**
     * Prints an import statement for the given class. The advantage
     * of doing it this way is that it is safe under refactoring of
     * the class-name. 
     * 
     * @param clazz
     *  The class that has to be imported.
     * @throws GenerationException
     *  If an I/O exception occurrs.
     */
    protected void printImport(Class<?> clazz) throws GenerationException {
        print("import ");
        print(clazz.getCanonicalName());
        println(';');
    }
    
    /**
     * Prints a string, followed by a colon (hence the c) and a space.
     * 
     * @param s
     * 	The string to print
     * 
     * @throws GenerationException
     *  If an I/O exception occurrs.
     */
    protected void printc(String s) throws GenerationException {
    	print(s);
    	print("; ");
    }
    
    /**
     * Prints a string, followed by a colon (hence the c) and a line break.
     * 
     * @param s
     * 	The string to print
     * @throws GenerationException
     *  If an I/O exception occurrs.
     */
    protected void printcln(String s) throws GenerationException {
    	print(s);
    	println(';');
    }
    
    /**
     * Prints a character, followed by a colon (hence the c) and a line break.
     * 
     * @param c
     *  The character to print
     * @throws GenerationException
     *  If an I/O exception occurrs.
     */
    protected void printcln(char c) throws GenerationException {
        print(c);
        println(';');
    }
    
    /**
     * Prints an integer, followed by a colon (hence the c) and a line break.
     * 
     * @param i
     *  The integer to print
     * @throws GenerationException
     *  If an I/O exception occurrs.
     */
    protected void printcln(int i) throws GenerationException {
        print(i);
        println(';');
    }
    
    /**
     * Prints a single-line-comment, preceded by a space, and followed by a line break.
     * 
     * @param s
     * 	The string to print
     * @throws GenerationException
     *  If an I/O exception occurrs.
     */
    protected void printSingleLineComment(String s) throws GenerationException {
    	print(" // ");
    	print(s);
    	println("; ");
    }
    
    protected void tprintStaredMultiLineComment(String... comments) throws GenerationException {
    	printStaredMultiLineComment(getNbTabs(), comments);
    }
    
    /**
     * Prints a multi-line-comment, with on the left stars.
     * 
     * @param comments
     * 	The strings to print
     * @throws GenerationException
     *  If an I/O exception occurrs.
     */
    protected void printStaredMultiLineComment(String... comments) throws GenerationException {
    	printStaredMultiLineComment(0, comments);
    }
    
    private void printStaredMultiLineComment(int tabs, String... comments) throws GenerationException {
		printTabs(tabs); 
		println("/*");
		for (String comment : comments) {
			printTabs(tabs);
			print(" * ");
			println(comment);
		}
		printTabs(tabs);
		println("*/");
    }
    
    
    protected void generateGeneratedAnnotation() throws GenerationException {
    	tprintln("/* @javax.annotation.Generated(");
    	ttprint("value = "); printLiteral(About.getFullSystemNameAndVersion()); println(',');
		ttprint("date = "); printLiteral(DateUtils.toISO8601String(new Date())); println(',');
		ttprint("comments = "); printLiteral(About.SYSTEM_URL.toString());
		tprintln(") */");    	
    }
}