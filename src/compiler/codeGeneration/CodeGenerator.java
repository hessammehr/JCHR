package compiler.codeGeneration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Formatter;
import java.util.IllegalFormatException;
import java.util.Iterator;

import util.collections.Stack;
import util.iterator.IteratorUtilities;


/**
 * <p>
 * An abstract class that will serve as the basis for all code generators.
 * It provides common functionality each generator will need. One of the
 * main functions of this class will be to wrap a <code>Writer</code> object
 * and offer the necessary convenience methods to its subclasses.
 * </p>
 * <p> 
 * We could also use one huge code generator class, but we opted to use
 * several smaller code generators for several reasons: we can experiment
 * with several versions of the same part, combining them in different ways,
 * exploit inheritance to create smaller variations, allow user definable 
 * options to choose the actual implementation used (we might need a factory)
 * etc 
 * </p>
 * 
 * @author Peter Van Weert
 */
public abstract class CodeGenerator implements Appendable {
    
    /**
     * The writer that is used to write the output to. Note that we are generally 
     * using a <code>BufferedWriter</code> for top performance. The buffered writer
     * might not always be necessary, but we are opting for the easiest solution here
     * (works perfectly well with <code>{@link java.io.FileWriter}</code>s and
     * <code>{@link java.io.OutputStreamWriter}</code>s). 
     */
    private Writer out;
    
    /**
     * Does the code generator terminate after code generation is done.
     * If <code>true</code>, e.g. all streams will be flushed, all open files will 
     * be closed, once all generation is done.
     */
    private boolean terminate;
    
    private final static String LINE_SEPARATOR 
    	= System.getProperty("line.separator");
    
    /**
     * Convenience constructor. This code generator will serve as a helper
     * for the give code generator, i.e. it will it write its output to
     * the writer of the given codegenerator.
     * The generator does <em>not</em> auto-terminate.
     * 
     * @param out
     *  The writer that is to be used to write the generated code to.
     *  
     * @see #CodeGenerator(BufferedWriter)
     */
    public CodeGenerator(CodeGenerator codeGenerator) {
        this(codeGenerator.getOut(), false);
        // start (and end!) at the same number of tabs
        setNbTabs(codeGenerator.getNbTabs());
    }

    /**
     * Convenience constructor. The given writer will be wrapped
     * in a <code>BufferedWriter</code> if it does not allready
     * extend this class. The buffered writer might not always be necessary, 
     * but we are opting for the easiest solution here
     * (works perfectly well with <code>{@link java.io.FileWriter}</code>s and
     * <code>{@link java.io.OutputStreamWriter}</code>s).
     * The generator auto-terminates.
	 *
     * @param out
     *  The writer that is to be used to write the generated code to.
     *  
     * @see #CodeGenerator(BufferedWriter)
     */
    public CodeGenerator(Writer out) {
        this(out, true);
    }
    
    /**
     * Creates a code generator that generates the necessary code 
     * to the given writer.
     * The generator auto-terminates.
     * 
     * @param out
     *  The writer that is to be used to write the generated code to.
     */
    public CodeGenerator(BufferedWriter out) {
        this(out, true);
    }
    
    /**
     * Convenience constructor. The given writer will be wrapped
     * in a <code>BufferedWriter</code> if it does not allready
     * extend this class. The buffered writer might not always be necessary, 
     * but we are opting for the easiest solution here
     * (works perfectly well with <code>{@link java.io.FileWriter}</code>s and
     * <code>{@link java.io.OutputStreamWriter}</code>s).

     * @param out
     *  The writer that is to be used to write the generated code to.
     * @param terminate
     * 	Terminate (close open files etc.) when done generating
     *  
     * @see #CodeGenerator(BufferedWriter)
     */
    public CodeGenerator(Writer out, boolean terminate) {
        this((out instanceof BufferedWriter)
            ? (BufferedWriter)out 
            : new BufferedWriter(out),
        terminate);
    }
    
    /**
     * Creates a code generator that writes the necessary code 
     * to the given writer.
     * 
     * @param out
     *  The writer that is to be used to write the generated code to.
     */
    public CodeGenerator(BufferedWriter out, boolean terminate) {
        setOut(out);
        setAutoTerminate(terminate);
    }
    
    public final void generate() throws GenerationException {
    	init();
    	try { doGenerate(); } finally { if (autoTerminates()) terminate(); }
    }
    
    protected void init() {
    	// NOP
    }
    
    protected abstract void doGenerate() throws GenerationException;
    
    
    /**
     * Does the generator auto-terminate after code generation is done or not?
     * If <code>true</code>, e.g. all streams will be flushed, all open files will 
     * be closed, once all generation is done.
     * 
     * @param terminate
     * 	A boolean.
     */
    public void setAutoTerminate(boolean terminate) {
		this.terminate = terminate;
	}
    /**
     * Does the generator auto-terminate after code generation is done or not?
     * If <code>true</code>, e.g. all streams will be flushed, all open files will 
     * be closed, once all generation is done.
     */
    public boolean autoTerminates() {
		return terminate;
	}
    
    
    /* * * * * * * * * * * * * * * * * *\
     * NON-TRIVIAL CONVENIENCE METHODS *
     * * * * * * * * * * * * * * * * * */ 
    
    /**
     * Writes the contents of a given file.
     * 
     * @throws GenerationException
     *  If an I/O exception occurs. 
     */
    protected void copyFile(File file) throws GenerationException {
    	try {
    		BufferedReader in = new BufferedReader(new FileReader(file));
            int read; while ((read = in.read()) != -1) write(read);
        	in.close();
        } catch (FileNotFoundException fnfe) {
            throw new GenerationException(fnfe);
        } catch (IOException ioe) {
            throw new GenerationException(ioe);
        }
    }
    
    /* * * * * *\
     * TABBING *
     * * * * * */

    /**
     * The number of tabs to print (used for code indentation) 
     */
    private int nbTabs;

    /**
     * Writes a number of tabs. This can be used to indent the
     * generated code. The number of tabs is controlled by
     * a number of mutator methods (all ending in <code>Tabs</code>).
     * All <code>tprint</code> methods will first call this method.
     * 
     * @see #tprint(String)
     * @see #tprintln(String)
     * @see #incNbTabs()
     * @see #decNbTabs()
     */
    protected void printTabs() throws GenerationException {
        printTabs(getNbTabs());
    }
    
    /**
     * Writes one tab more than {@link #printTabs()}.
     *  
     * @see #printTabs()
     */
    protected void tprintTabs() throws GenerationException {
        printTabs(getNbTabs() + 1);
    }
    
    /**
     * Writes a single tab. This can be used to indent the generated code.
     */
    protected void printTab() throws GenerationException {
        printTabs(1);
    }
    
    /**
     * Writes a given number of tabs. This can be used to indent the
     * generated code. The number of tabs is controlled by
     * a number of mutator methods (all ending in <code>Tabs</code>).
     * All <code>tprint</code> methods will first call this method.
     * 
     * @param tabs
     * 	The number of tabs to print.
     * 
     * @see #tprint(String)
     * @see #tprintln(String)
     * @see #incNbTabs()
     * @see #decNbTabs()
     */
    protected void printTabs(int tabs) throws GenerationException {
        printx('\t', tabs);
    }
    
    protected int getNbTabs() {
        return nbTabs;
    }
    protected void setNbTabs(int nbTabs) {
        this.nbTabs = nbTabs;
    }
    protected void incNbTabs() {
        nbTabs++;
    }
    protected void decNbTabs() {
        nbTabs--;
    }
    protected void incNbTabs(int nbTabs) {
        this.nbTabs += nbTabs;
    }
    protected void decNbTabs(int nbTabs) {
        this.nbTabs -= nbTabs;
    }
    
    /**
     * Convenience method for printing a series of strings, each 
     * preceeded by a number of tabs.
     *
     * @param s   
     *  The <code>String</code>s to be printed
     * @throws GenerationException 
     *  If an I/O exception occurs, or if the <code>null</code> array is passed.
     *  
     * @see #printTabs()
     * @see #print(String)
     */
    protected void tprintln(String... ss) throws GenerationException {
    	if (ss == null) throw new GenerationException("null");
    	for (String s : ss) tprintln(s);
    }
    
    /**
     * Convenience method for printing a string preceeded by 
     * a number of tabs, followed by a new line.
     *  
     * @param s   
     *  The <code>String</code> to be printed
     * 
     * @throws GenerationException 
     *  If an I/O exception occurs.
     *  
     * @see #printTabs()
     * @see #println(String)
     */
    protected void tprintln(String s) throws GenerationException {
        printTabs();
        println(s);
    }
    
    /**
     * Convenience method for printing an object preceeded by 
     * a number of tabs.
     *  
     * @param o
     *  The <code>Object</code> to be printed
     * 
     * @throws GenerationException 
     *  If an I/O exception occurs.
     *  
     * @see #printTabs()
     * @see #print(String)
     */
    protected void tprint(Object o) throws GenerationException {
        printTabs();
        print(o);
    }
    
    /**
     * Convenience method for printing a string preceeded by 
     * a number of tabs.
     *  
     * @param s
     *  The <code>String</code> to be printed
     * 
     * @throws GenerationException 
     *  If an I/O exception occurs.
     *  
     * @see #printTabs()
     * @see #print(String)
     */
    protected void tprint(String s) throws GenerationException {
        printTabs();
        print(s);
    }
    
    /**
     * Convenience method for printing a character preceeded by 
     * a number of tabs.
     *  
     * @param c   
     *  The <code>char</code> to be printed
     * 
     * @throws GenerationException 
     *  If an I/O exception occurs.
     *  
     * @see #printTabs()
     * @see #print(String)
     */
    protected void tprint(char c) throws GenerationException {
        printTabs();
        print(c);
    }
    
    /**
     * Convenience method for printing a character preceeded by 
     * a number of tabs, followed by a new line.
     *  
     * @param c   
     *  The <code>char</code> to be printed
     * 
     * @throws GenerationException 
     *  If an I/O exception occurs.
     *  
     * @see #printTabs()
     * @see #println(char)
     */
    protected void tprintln(char c) throws GenerationException {
        printTabs();
        println(c);
    }
    
    /**
     * Convenience method for printing a formatted string preceeded by 
     * a number of tabs.
     *  
     * @param s   
     *  The <code>String</code> to be printed
     * 
     * @throws GenerationException 
     *  If an I/O exception occurs.
     *  
     * @see #printTabs()
     * @see #printf(String, Object[])
     */
    protected void tprintf(String s, Object... args) throws GenerationException {
        printTabs();
        printf(s, args);
    }
    
    /**
     * Convenience method for printing a series of strings, each 
     * preceeded by a number of tabs (one extra).
     *
     * @param s   
     *  The <code>String</code>s to be printed
     * @throws GenerationException 
     *  If an I/O exception occurs, or if the <code>null</code> array is passed.
     *  
     * @see #tprintTabs()
     * @see #print(String)
     */
    protected void ttprintln(String... ss) throws GenerationException {
        if (ss == null) throw new GenerationException("null");
        for (String s : ss) ttprintln(s);
    }
    
    /**
     * Convenience method for printing a string preceeded by 
     * a number of tabs (one extra), followed by a new line.
     *  
     * @param s   
     *  The <code>String</code> to be printed
     * 
     * @throws GenerationException 
     *  If an I/O exception occurs.
     *  
     * @see #tprintTabs()
     * @see #println(String)
     */
    protected void ttprintln(String s) throws GenerationException {
        tprintTabs();
        println(s);
    }
    
    /**
     * Convenience method for printing a boolean preceeded by 
     * a number of tabs (one extra), followed by a new line.
     *  
     * @param b  
     *  The <code>boolean</code> to be printed
     * 
     * @throws GenerationException 
     *  If an I/O exception occurs.
     *  
     * @see #tprintTabs()
     * @see #println(boolean)
     */
    protected void ttprintln(boolean b) throws GenerationException {
        tprintTabs();
        println(b);
    }
    
    /**
     * Convenience method for printing a character preceeded by 
     * a number of tabs (one extra).
     *  
     * @param c   
     *  The <code>char</code> to be printed
     * 
     * @throws GenerationException 
     *  If an I/O exception occurs.
     *  
     * @see #tprintTabs()
     * @see #print(String)
     */
    protected void ttprint(String s) throws GenerationException {
        tprintTabs();
        print(s);
    }
    
    /**
     * Convenience method for printing a character preceeded by 
     * a number of tabs (one extra), followed by a new line.
     *  
     * @param c   
     *  The <code>char</code> to be printed
     * 
     * @throws GenerationException 
     *  If an I/O exception occurs.
     *  
     * @see #tprintTabs()
     * @see #println(char)
     */
    protected void ttprintln(char c) throws GenerationException {
        tprintTabs();
        println(c);
    }
    
    /**
     * Convenience method for printing a formatted string preceeded by 
     * a number of tabs (one extra).
     *  
     * @param s   
     *  The <code>String</code> to be printed
     * 
     * @throws GenerationException 
     *  If an I/O exception occurs.
     *  
     * @see #tprintTabs()
     * @see #printf(String, Object[])
     */
    protected void ttprintf(String s, Object... args) throws GenerationException {
        tprintTabs();
        printf(s, args);
    }
    
    /**
     * Writes a given character a number of times.
     * 
     * @param c
     *  The character that has to be written <code>nbrTimes</code> times.
     * @param nbrTimes
     *  The number of times <code>c</code> has to be written.
     *  
     * @throws GenerationException
     *  If an I/O exception occurs. 
     */
    protected void printx(char c, int nbrTimes) throws GenerationException {
        try {
            if (nbrTimes == 0) return;
            if (nbrTimes == 1) write(c);
            else if (c == '\t' && nbrTimes <= 10) // most common usage
                write("\t\t\t\t\t\t\t\t\t\t", 0, nbrTimes);
            else if (c == ' ' && nbrTimes <= 10)
            	write("          ", 0, nbrTimes);
            else for (int i = 0; i < nbrTimes; i++) write(c);
            
        } catch (IOException ioe) {
            throw new GenerationException(ioe);
        }
    }
    
    /**
     * Writes a given string a number of times.
     * 
     * @param s
     *  The string that has to be written <code>nbrTimes</code> times.
     * @param nbrTimes
     *  The number of times <code>s</code> has to be written.
     *  
     * @throws GenerationException
     *  If an I/O exception occurs. 
     */
    protected void printx(String s, int nbrTimes) throws GenerationException {
        try {
            if (nbrTimes == 0) return;
            if (nbrTimes == 1) write(s);
            else for (int i = 0; i < nbrTimes; i++) write(s);
            
        } catch (IOException ioe) {
            throw new GenerationException(ioe);
        }
    }
    
    /* * * * * * * * * * * * * * * *\
     * TRIVIAL CONVENIENCE METHODS *
    \* * * * * * * * * * * * * * * */
    
    // (comments based on those in PrintWriter.java)
    
    /**
     * Print a boolean value.  The string produced by 
     * <code>{@link java.lang.String#valueOf(boolean)}</code> is translated 
     * into bytes according to the platform's default character encoding, 
     * and these bytes are written in exactly the manner of the 
     * <code>{@link #write(int)}</code> method.
     *
     * @param b   
     *  The <code>boolean</code> to be printed
     *  
     * @throws GenerationException
     *  If an I/O exception occurs. 
     */
    protected void print(boolean b) throws GenerationException {
        try { 
            write(b ? "true" : "false"); 
        } catch (IOException ioe) { 
            throw new GenerationException(ioe); 
        }
    }

    /**
     * Print a character.  The character is translated into one or more bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param c   
     *  The <code>char</code> to be printed
     *  
     * @throws GenerationException
     *  If an I/O exception occurs. 
     */
    protected void print(char c) throws GenerationException {
        try {
            write(c);
        } catch (IOException ioe) {
            throw new GenerationException(ioe);
        }
    }

    /**
     * Print an integer.  The string produced by 
     * <code>{@link java.lang.String#valueOf(int)}</code> is translated into bytes 
     * according to the platform's default character encoding, and these bytes are
     * written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param i   
     *  The <code>int</code> to be printed
     *  
     * @throws GenerationException
     *  If an I/O exception occurs.
     *   
     * @see String#valueOf(int)
     */
    protected void print(int i) throws GenerationException  {
        try {
            write(String.valueOf(i));
        } catch (IOException ioe) {
            throw new GenerationException(ioe);
        }
    }

    /**
     * Print a long integer.  The string produced by <code>{@link
     * java.lang.String#valueOf(long)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param l   
     *  The <code>long</code> to be printed
     * 
     * @throws GenerationException
     *  If an I/O exception occurs.
     * 
     * @see String#valueOf(long)
     */
    protected void print(long l) throws GenerationException {
        try {
            write(String.valueOf(l));
        } catch (IOException ioe) {
            throw new GenerationException(ioe);
        }
    }
    
    /**
     * Print a string.  The string  is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method. 
     * If the string passed is <code>null</code>, the string <code>"null"</code>
     * is written.
     * 
     * @param s
     *  The <code>string</code> to be printed
     * 
     * @throws GenerationException
     *  If an I/O exception occurs.
     * 
     * @see String#valueOf(long)
     */
    protected void print(String s) throws GenerationException {
        try {
            write(s == null? "null" : s);
        } catch (IOException ioe) {
            throw new GenerationException(ioe);
        }
    }

    /**
     * Print a floating-point number.  The string produced by 
     * <code>{@link java.lang.String#valueOf(float)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param f
     *  The <code>float</code> to be printed
     *  
     * @throws GenerationException
     *  If an I/O exception occurs.
     * 
     * @see String#valueOf(float)
     */
    protected void print(float f) throws GenerationException {
        try {
            write(String.valueOf(f));
        } catch (IOException ioe) {
            throw new GenerationException(ioe);
        }
    }

    /**
     * Print a double-precision floating-point number.  The string produced by
     * <code>{@link java.lang.String#valueOf(double)}</code> is translated into
     * bytes according to the platform's default character encoding, and these
     * bytes are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param d
     *  The <code>double</code> to be printed
     *  
     * @throws GenerationException
     *  If an I/O exception occurs.
     * 
     * @see String#valueOf(double)
     */
    protected void print(double d) throws GenerationException {
        try {
            write(String.valueOf(d));   
        } catch (IOException ioe) {
            throw new GenerationException(ioe);
        }
    }
    
    /**
     * Print a number of strings.
     * After each string a line break is inserted.
     * If the argument is <code>null</code> then the string
     * <code>"null"</code> is printed.  Otherwise, the string's characters are
     * converted into bytes according to the platform's default character
     * encoding, and these bytes are written in exactly the manner of the
     * <code>{@link #write(int)}</code> method.
     *
     * @param s   The <code>String</code>s to be printed. 
     * 
     * @throws GenerationException
     *  If an I/O exception occurs, or if the given list is a null pointer.
     */
    protected void println(String... ss) throws GenerationException {
    	if (ss == null) throw new GenerationException("null");
    	for (int i = 0; i < ss.length; i++)  println(ss[i]);
    }
    
    /**
     * Print an object.  The string produced by the <code>{@link
     * java.lang.String#valueOf(Object)}</code> method is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param obj
     *  The <code>Object</code> to be printed
     * 
     * @see String#valueOf(java.lang.Object)
     * 
     * @throws GenerationException
     *  If an I/O exception occurs.
     */
    protected void print(Object obj) throws GenerationException {
        try {
            write(String.valueOf(obj));
        } catch (IOException ioe) {
            throw new GenerationException(ioe);
        }
    }
    
    /**
     * Print a boolean value, followed by a space.  The string produced by 
     * <code>{@link java.lang.String#valueOf(boolean)}</code> is translated 
     * into bytes according to the platform's default character encoding, 
     * and these bytes are written in exactly the manner of the 
     * <code>{@link #write(int)}</code> method.
     *
     * @param b   
     *  The <code>boolean</code> to be printed
     *  
     * @throws GenerationException
     *  If an I/O exception occurs. 
     */
    protected void prints(boolean b) throws GenerationException {
    	print(b); print(' ');
    }

    /**
     * Print a character, followed by a space.  The character is translated into one or more bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param c   
     *  The <code>char</code> to be printed
     *  
     * @throws GenerationException
     *  If an I/O exception occurs. 
     */
    protected void prints(char c) throws GenerationException {
    	print(c); print(' ');
    }

    /**
     * Print an integer, followed by a space.  The string produced by 
     * <code>{@link java.lang.String#valueOf(int)}</code> is translated into bytes 
     * according to the platform's default character encoding, and these bytes are
     * written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param i   
     *  The <code>int</code> to be printed
     *  
     * @throws GenerationException
     *  If an I/O exception occurs.
     *   
     * @see String#valueOf(int)
     */
    protected void prints(int i) throws GenerationException  {
    	print(i); print(' ');
    }

    /**
     * Print a long integer, followed by a space.  The string produced by <code>{@link
     * java.lang.String#valueOf(long)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param l   
     *  The <code>long</code> to be printed
     * 
     * @throws GenerationException
     *  If an I/O exception occurs.
     * 
     * @see String#valueOf(long)
     */
    protected void prints(long l) throws GenerationException {
    	print(l); print(' ');
	}

    /**
     * Print a floating-point number, followed by a space.  The string produced by 
     * <code>{@link java.lang.String#valueOf(float)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param f
     *  The <code>float</code> to be printed
     *  
     * @throws GenerationException
     *  If an I/O exception occurs.
     * 
     * @see String#valueOf(float)
     */
    protected void prints(float f) throws GenerationException {
    	print(f); print(' ');
    }

    /**
     * Print a double-precision floating-point number, followed by a space.  The string produced by
     * <code>{@link java.lang.String#valueOf(double)}</code> is translated into
     * bytes according to the platform's default character encoding, and these
     * bytes are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param d
     *  The <code>double</code> to be printed
     *  
     * @throws GenerationException
     *  If an I/O exception occurs.
     * 
     * @see String#valueOf(double)
     */
    protected void prints(double d) throws GenerationException {
    	print(d); print(' ');
    }
    
    /**
     * Print a string, followed by a space.
     * If the argument is <code>null</code> then the string
     * <code>"null"</code> is printed.  Otherwise, the string's characters are
     * converted into bytes according to the platform's default character
     * encoding, and these bytes are written in exactly the manner of the
     * <code>{@link #write(int)}</code> method.
     *
     * @param s   The <code>String</code>s to be printed. 
     * @throws GenerationException 
     * 
     * @throws GenerationException
     *  If an I/O exception occurs.
     */
    protected void prints(String s) throws GenerationException {
        print(s); print(' ');
    }
    
    /**
     * Print an object, followed by a space.  The string produced by the <code>{@link
     * java.lang.String#valueOf(Object)}</code> method is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param obj
     *  The <code>Object</code> to be printed
     * 
     * @see String#valueOf(java.lang.Object)
     * 
     * @throws GenerationException
     *  If an I/O exception occurs.
     */
    protected void prints(Object obj) throws GenerationException {
    	print(obj); print(' ');
    }
    
    /**
     * Terminate the current line by writing the line separator string.  The
     * line separator string is defined by the system property
     * <code>line.separator</code>, and is not necessarily a single newline
     * character (<code>'\n'</code>).
     * 
     * @throws GenerationException
     *  If an I/O exception occurs.
     */
    protected void println() throws GenerationException {
        print(LINE_SEPARATOR);
    }
    
    /**
     * Shorter notation for {@link #println()}.
     * 
     * @throws GenerationException
     *  If an I/O exception occurs.
     */
    protected void nl() throws GenerationException {
    	println();
    }

    /**
     * Print a boolean value and then terminate the line.  This method behaves
     * as though it invokes <code>{@link #print(boolean)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x the <code>boolean</code> value to be printed
     */
    protected void println(boolean x) throws GenerationException {
        print(x);
        nl();
    }

    /**
     * Print a character and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(char)}</code> and then <code>{@link
     * #println()}</code>.
     *
     * @param x the <code>char</code> value to be printed
     * 
     * @throws GenerationException
     *  If an I/O exception occurs.
     */
    protected void println(char x) throws GenerationException {
        print(x);
        nl();
    }

    /**
     * Print an integer and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(int)}</code> and then <code>{@link
     * #println()}</code>.
     *
     * @param x the <code>int</code> value to be printed
     * 
     * @throws GenerationException
     *  If an I/O exception occurs.
     */
    protected void println(int x) throws GenerationException {
    	print(x);
        nl();
    }

    /**
     * Print a long integer and then terminate the line.  This method behaves
     * as though it invokes <code>{@link #print(long)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x the <code>long</code> value to be printed
     * 
     * @throws GenerationException
     *  If an I/O exception occurs.
     */
    protected void println(long x) throws GenerationException {
    	print(x);
        nl();
    }

    /**
     * Print a floating-point number and then terminate the line.  This method
     * behaves as though it invokes <code>{@link #print(float)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x the <code>float</code> value to be printed
     * 
     * @throws GenerationException
     *  If an I/O exception occurs.
     */
    protected void println(float x) throws GenerationException {
    	print(x);
        nl();
    }
    
    /**
     * Print a string and then terminate the line.  This method
     * behaves as though it invokes <code>{@link #print(String)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x the <code>float</code> value to be printed
     * 
     * @throws GenerationException
     *  If an I/O exception occurs.
     */
    protected void println(String x) throws GenerationException {
    	print(x);
        nl();
    }

    /**
     * Print a double-precision floating-point number and then terminate the
     * line.  This method behaves as though it invokes <code>{@link
     * #print(double)}</code> and then <code>{@link #println()}</code>.
     *
     * @param x the <code>double</code> value to be printed
     * 
     * @throws GenerationException
     *  If an I/O exception occurs.
     */
    protected void println(double x) throws GenerationException {
    	print(x);
        nl();
    }
    
    /**
     * Print an Object and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(Object)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x 
     *  The <code>Object</code> value to be printed
     *  
     * @throws GenerationException
     *  If an I/O exception occurs.
     */
    protected void println(Object x) throws GenerationException {
        print(x);
        nl();
    }
    
    /**
     * A convenience method to write a formatted string to this writer using
     * the specified format string and arguments. 
     *
     * @param  format
     *         A format string as described in <a
     *         href="../util/Formatter.html#syntax">Format string syntax</a>.
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         the <a href="http://java.sun.com/docs/books/vmspec/">Java
     *         Virtual Machine Specification</a>.  The behaviour on a
     *         <tt>null</tt> argument depends on the <a
     *         href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @throws GenerationException If ...
     *  <ul>
     *          <li>
     *          ...a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.  For specification of all possible
     *          formatting errors, see the <a
     *          href="../util/Formatter.html#detail">Details</a> section of the
     *          formatter class specification.
     *          </li>
     *          <li>
     *          ...the <tt>format</tt> is <tt>null</tt>
     *          </li>
     *          <li>
     *          ...an I/O exception occurs.
     *          </li>
     * </ul>
     */
    protected void printf(String format, Object... args) 
    throws GenerationException {
    	Formatter formatter = new Formatter(this);
        
    	try {
            formatter.format(format, args);
        } catch (NullPointerException npe) {
            throw new GenerationException(npe);
        } catch (IllegalFormatException ife) {
            throw new GenerationException(ife);
        }
        
        if (formatter.ioException() != null)
            throw new GenerationException(formatter.ioException()); 
    }
    
    /**
     * A convenience method to write a series of formatted strings
     * to this writer using the specified format strings and arguments. 
     *
     * @param  format
     *         An array of format strings as described in <a
     *         href="../util/Formatter.html#syntax">Format string syntax</a>.
     *
     * @param  args
     *         Arguments referenced by the format specifiers in all the format
     *         strings.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         the <a href="http://java.sun.com/docs/books/vmspec/">Java
     *         Virtual Machine Specification</a>.  The behaviour on a
     *         <tt>null</tt> argument depends on the <a
     *         href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @throws GenerationException If ...
     *  <ul>
     *          <li>
     *          ...a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.  For specification of all possible
     *          formatting errors, see the <a
     *          href="../util/Formatter.html#detail">Details</a> section of the
     *          formatter class specification.
     *          </li>
     *          <li>
     *          ...the <tt>format</tt> is <tt>null</tt>
     *          </li>
     *          <li>
     *          ...an I/O exception occurs.
     *          </li>
     * </ul>
     * 
     * @see #printf(String, Object[])
     */
    protected void printf(String[] formats, Object... args) 
    throws GenerationException {
        for (String format : formats) printf(format, args); 
    }
    
    
    protected void print(Iterable<?> iterable) throws GenerationException {
    	print(iterable.iterator());
    }
    protected void print(Iterator<?> iterator) throws GenerationException {
    	print(iterator, "", "", ", ");
    }
    protected void print(Iterable<?> iterable, String prefix, String postfix, String infix) throws GenerationException {
    	print(iterable.iterator(), prefix, postfix, infix);
    }
    protected void print(Iterator<?> iterator, String prefix, String postfix, String infix) throws GenerationException {
    	try {
    		IteratorUtilities.deepAppendTo(this, iterator, prefix, postfix, infix);
    	} catch (IOException ioe) {
    		throw new GenerationException(ioe);
    	}
    }
    
    protected void terminate() throws GenerationException {
    	try {
    		close();
    	} catch (IOException ioe) {
    		throw new GenerationException(ioe);
    	}
    }
    
    
    /* * * * * * * * * * * *\
     * APPENDABLE METHODS  *
    \* * * * * * * * * * * */
    
    public Appendable append(char c) throws IOException {
    	write(c);
    	return this;
    }
    
    public Appendable append(CharSequence csq) throws IOException {
    	write(csq.toString());
    	return this;
    }
    
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
    	write(csq.toString(), start, end);
    	return this;
    }
            
    
    /* * * * * * * * * * *\
     * DELEGATE METHODS  *
    \* * * * * * * * * * */
    
    /**
     * Delegate method.
     * 
     * @see #getOut()
     * @see BufferedWriter#close()
     * 
     * @throws IllegalStateException
     * 	If recording (cf. {@link #isRecording()})
     */
    protected void close() throws IOException, IllegalStateException {
    	if (isRecording()) throw new IllegalStateException();
        out.close();
    }
    /**
     * Delegate method.
     * 
     * @see #getOut()
     * @see BufferedWriter#flush()()
     */
    protected void flush() throws IOException {
        out.flush();
    }
    /**
     * Delegate method.
     * 
     * @see #getOut()
     * @see BufferedWriter#write(int)
     */
    private void write(int c) throws IOException {
        out.write(c);
    }
    /**
     * Delegate method.
     * 
     * @see #getOut()
     * @see BufferedWriter#write(java.lang.String, int, int)
     */
    private void write(String str) throws IOException {
        out.write(str, 0, str.length());
    }
    /**
     * Delegate method.
     * 
     * @see #getOut()
     * @see BufferedWriter#write(java.lang.String, int, int)
     */
    public void write(String s, int off, int len) throws IOException {
        out.write(s, off, len);
    }
    
    
    /* * * * * * * * * * * *\
     * GETTERS AND SETTERS *
    \* * * * * * * * * * * */

    protected Writer getOut() {
        return out;
    }
    protected void setOut(Writer out) {
        this.out = out;
    }
    
    /* * * * * * *\ 
     * RECORDING *
    \* * * * * * */
    
    private final Stack<Writer> WIRTER_STACK = new Stack<Writer>(4);
    
    protected void beginRecording() {
    	WIRTER_STACK.push(getOut());
    	setOut(new StringWriter());
    }
    
    protected boolean isRecording() {
    	return !WIRTER_STACK.isEmpty();
    }
    
    protected String endRecording() throws IllegalStateException {
    	if (isRecording()) {
	    	StringWriter recorded = (StringWriter)getOut();
	    	setOut(WIRTER_STACK.pop());
	    	return recorded.toString();
    	}
    	
    	throw new IllegalStateException("not recording");
    }
}
