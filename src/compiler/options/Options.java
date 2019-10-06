package compiler.options;

import compiler.CHRIntermediateForm.builder.tables.ClassTable;
import compiler.CHRIntermediateForm.debug.DebugInfo;
import compiler.CHRIntermediateForm.debug.DebugLevel;
import compiler.CHRIntermediateForm.types.Type;
import compiler.options.BooleanOptionHandler.Generic;
import compiler.options.BooleanOptionHandler.True;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * A class that deals with the compiler options and arguments.
 * The current version uses the 
 * <a href="https://args4j.dev.java.net/">args4j</a> library
 * (version 2.0.4 or higher). 
 *  
 * @author Peter Van Weert
 */
public class Options {
    static {
        BooleanOptionHandler.registerDefault();
        ClassOptionHandler.register();
    }
    
    private CmdLineParser parser;
    
    /**
     * Creates a new {@code Options} object.
     */
    public Options() {
        setDebug(new DebugInfo());
        setParser(new CmdLineParser(this));
    }

    /**
     * Creates a new {@code Options} object and processes
     * the given arguments. 
     * 
     * @param args
     *  A series of options, option operands and other arguments.
     * @throws OptionsException
     *  An illegal option, option operand or argument was provided.
     */
    public Options(String... args) throws OptionsException {
        this();
        processArguments(args);
    }
    
    public void useClassTable(ClassTable classTable) {
        ClassOptionHandler.useClassTable(classTable);
    }
    
    
    protected void setParser(CmdLineParser parser) {
        this.parser = parser;
    }
    protected CmdLineParser getParser() {
        return parser;
    }
    
    /**
     * Processes the given arguments. Options are parsed and processed,
     * extra arguments are saved.
     * 
     * @param arguments
     *  A series of options, option operands and other arguments.
     * @throws OptionsException
     *  An illegal option, option operand or argument was provided.
     *  
     * @see #getArguments()
     */
    public void processArguments(String... arguments) throws OptionsException {
        try {
            getParser().parseArgument(arguments);
        } catch (CmdLineException cle) {
            throw new OptionsException(cle.getMessage(), cle.getCause());
        }
    }
    
    /**
     * Processes the given options: no extra arguments can be provided.
     * 
     * @param arguments
     *  A series of options, option operands and other arguments.
     * @throws OptionsException
     *  If an illegal option or option operand was provided, or extra arguments
     *  or unknown options were given.
     */
    public void processOptions(String... options) throws OptionsException {
        int nbArguments = getNbArguments();
        processArguments(options);
        if (getNbArguments() > nbArguments)
            throw new OptionsException("Illegal arguments given: "
                    + getArgumentsRef().subList(nbArguments, getNbArguments()));
    }
    
    /**
     * Processes the given options: extra arguments will be ignored.
     * 
     * @param arguments
     *  A series of options, option operands and other arguments.
     * @throws OptionsException
     *  If an illegal option or option operand was provided.
     */
    public void processOptionsIgnoringArguments(String... options) 
    throws OptionsException {
        int nbArguments = getNbArguments();
        processArguments(options);
        setArguments(getArgumentsRef().subList(0, nbArguments));
    }
    
    /**
     * Prints the usage to the &quot;standard&quot; error output
     * stream. This method is equivalent with:
     * <pre>    printUsage(System.err);</pre>
     *
     * @see System#err
     * @see System#setErr(java.io.PrintStream)
     */
    public void printUsage() {
        printUsage(System.err);
    }
    
    /**
     * Prints the usage to a given output stream.
     * 
     * @param out
     *  The output stream the usage has to printed to.
     */
    public void printUsage(OutputStream out) {
        getParser().printUsage(out);
    }
    
    public void printUsage(OutputStream out, ResourceBundle bundle) {
    	printUsage(new OutputStreamWriter(out), bundle);
    }
    public void printUsage(Writer out, ResourceBundle bundle) {
        getParser().printUsage(out, bundle);
    }
    
    /* * * * * * * *\
     * THE OPTIONS * 
    \* * * * * * * */

    private boolean analysis = true;
    
    @Option(
        name    = "-analysis", 
        handler = Generic.class, 
        usage   = "set use of static analysis"
    )
    public void toggleAnalysis(boolean analysis) {
        this.analysis = analysis;
    }
    public boolean performAnalysis() {
        return analysis;
    }
    
    // ----------------------------------------------------------
    
    private boolean hash = true;
    
    @Option(
        name    = "-hash", 
        handler = Generic.class, 
        usage   = "set use of hash indexing"
    )
    public void toggleHashIndexing(boolean hash) {
        this.hash = hash;
    }
    public boolean hashIndexing() {
        return hash;
    }
    
    // ----------------------------------------------------------
    
    private boolean generationOptimization = true;
    
    @Option(
		name = "-generation",
		handler = Generic.class,
		usage = "set use of generation optimization"
    )
    public void toggleGenerationOptimization(boolean generationOptimization) {
    	this.generationOptimization = generationOptimization;
    }
    public boolean doGenerationOptimization() {
        return generationOptimization;
    }
    
    //  ----------------------------------------------------------
    
    private boolean stackOptimizations = true;
    
    @Option(
		name = "-stack",
		handler = Generic.class,
		usage = "set use of stack optimizations"
    )
    public void toggleStackOptimizations(boolean stackOptimizations) {
    	this.stackOptimizations = stackOptimizations;
    }
    public boolean doStackOptimizations() {
        return stackOptimizations;
    }
    
    // ----------------------------------------------------------
    
    private boolean sysin;
    
    @Option(
        name    = "-standardinput",
        handler = True.class,
        usage   = "toggle blocking input from standard input stream"
    )
    public void toggleBlockingStandardInput(boolean b) {
        sysin = b;
    }
    public boolean useBlockingStandardInput() {
        return sysin;
    }
    
    //  ----------------------------------------------------------
    @Option(
        name    = "-fixed",
        usage   = "set class with given fully qualified name to be fixed"
    )
    public void addFixed(Class<?> clazz) {
        Type.FIXED_CLASSES.add(clazz);
    }
    
    //  ----------------------------------------------------------
    
    private DebugInfo debug;
    public void setDebug(DebugInfo debug) {
        this.debug = debug;
    }
    public DebugInfo getDebug() {
        return debug;
    }
    
    @Option(
        name    = "-debug",
        metaVar = "<level>",
        usage   = "set the debug level (off/default/full)"
    )
    public void setDebugLevel(DebugLevel level) {
        getDebug().setDebugLevel(level);
    }
    
    //  ----------------------------------------------------------
    
    public static enum Output {
        SOURCE {
            @Override public boolean generateSourceCode() { return true; }
            @Override public boolean generateByteCode() { return false; }
        },
        BYTECODE {
            @Override public boolean generateSourceCode() { return false; }
            @Override public boolean generateByteCode() { return true; }
        },
        DEFAULT {
            @Override public boolean generateSourceCode() { return true; }
            @Override public boolean generateByteCode() { return true; }
        };
        
        public abstract boolean generateSourceCode();
        public abstract boolean generateByteCode();
    }
    
    private Output output = Output.DEFAULT;
    public Output getOutput() {
        return output;
    }
    @Option(
		name = "-output",
        metaVar = "<output>",
        usage   = "set compiler output (default/source/bytecode)"
	)
    public void setOutput(Output output) {
        this.output = output;
    }
    
    /* * * * * * * * * *\
     * EXTRA ARGUMENTS *
    \* * * * * * * * * */
    private List<String> arguments = new ArrayList<String>();

    protected List<String> getArgumentsRef() {
        return arguments;
    }
    public List<String> getArguments() {
        return Collections.unmodifiableList(arguments);
    }
    public String getArgumentAt(int index) {
        return getArgumentsRef().get(index);
    }
    
    @Argument
    public void addArgument(String argument) {
        getArgumentsRef().add(argument);
    }
    
    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
    
    public int getNbArguments() {
        return getArgumentsRef().size();
    }
    public boolean hasArguments() {
        return getNbArguments() > 0; 
    }
}
