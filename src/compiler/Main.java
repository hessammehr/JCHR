package compiler;

import static java.lang.System.nanoTime;
import static util.Timing.nano2secs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import util.JavaCompiler;
import util.builder.BuilderException;
import util.collections.Singleton;

import compiler.CHRIntermediateForm.CHRIntermediateForm;
import compiler.CHRIntermediateForm.builder.CHRIntermediateFormBuilder;
import compiler.analysis.Analysis;
import compiler.codeGeneration.CodeGeneration;
import compiler.options.Options;
import compiler.options.OptionsException;
import compiler.options.BooleanOptionHandler.Generic;
import compiler.parser.CHRLexer;
import compiler.parser.CHRParser;

/**
 * @author Peter Van Weert
 */
public class Main {
    public static void main(String... args) throws IOException {
        Options options = new Options();
        JavaCompiler compiler = null;
        
        try {
            options.processArguments(args);

            for (InputStream in : getInputStreams(options)) {
                try {
                    // Parse the source file and build the intermediate model
                    long l, l0;
                    l0 = nanoTime();
                    CHRIntermediateForm cif = constructCHRIntermediateFrom(in, options);
                    l = nanoTime() - l0;
                    System.out.printf("Handler %s read in %s%n", cif.getHandlerName(), nano2secs(l));
                    
                    // Command line arguments override those specified in the source
                    options.processOptionsIgnoringArguments(args);
                    System.out.println();

                    // Optimise through static analysis
                    if (!options.performAnalysis()) {
                    	System.out.println("Skipping static analysis");
                    } else {
                    	System.out.println("Performing static analysis...");
                    	l = nanoTime();
                        Analysis.analyse(cif, options);
                        l = nanoTime() - l;
                        System.out.printf("Analysis terminated (%s)%n", nano2secs(l));
                    }
                    System.out.println();
                    
                    // Generate the java files
                    l = nanoTime();
//                    File[] generatedFiles = CodeGenerator.generateAllSourceFiles(cif);
                    File[] generatedFiles = CodeGeneration.generateAllSourceFiles(cif, options);
                    l = nanoTime() - l;
                    System.out.printf("Code generation took %s%n", nano2secs(l));
                    System.out.println();
                    
                    // Compile the generated files
                    if (options.getOutput().generateByteCode()) {
                    	if (compiler == null)
                    		compiler = JavaCompiler.createInstance();
                    	if (compiler != null) {               
		                    System.out.printf("Compiling %s generated java files...%n", generatedFiles.length);
		                    System.out.println(" --> using " + compiler);
		                    l = nanoTime();
		                    compiler.compile(generatedFiles);
		                    l = nanoTime() - l;
		                    l0 = nanoTime() - l0;
		                    double percentage = (double)l/l0*100D;
		                    System.out.printf("Compilation took %s (%.0f%% of total time)%n", nano2secs(l), percentage);
                            
                            if (! options.getOutput().generateSourceCode())
                                for (File file : generatedFiles) file.delete();
                    	} else {
                    		l0 = nanoTime() - l0;
                    		System.out.println("Generated java files could not be compiled (no compiler found)");
                    	}
                    } else {
                    	l0 = nanoTime() - l0;
                    	System.out.println("Generated java files are not compiled to bytecode");
                    }
                    
                    System.out.printf("%nCompilation of %s handler completed (%s)%n%n", cif.getHandlerName(), nano2secs(l0));
                    
                } catch (Throwable x) {
                    x.printStackTrace();
                    System.err.println();
                }
            }
        } catch (OptionsException oe) {
            System.err.println(oe.getMessage());
            System.err.println();
            printUsage(options);
        } finally {
    		if (compiler != null) compiler.close();
        }
    }
    
    public static CHRIntermediateForm constructCHRIntermediateFrom(Reader in, Options options) throws Throwable {
        return constructCHRIntermediateFrom(new CHRLexer(in), options);
    }

    public static CHRIntermediateForm constructCHRIntermediateFrom(InputStream in, Options options) throws Throwable {
        return constructCHRIntermediateFrom(new CHRLexer(in), options);
    }

    protected static CHRIntermediateForm constructCHRIntermediateFrom(CHRLexer lexer, Options options) throws Throwable {
        try {
            CHRIntermediateFormBuilder builder = new CHRIntermediateFormBuilder(options);
            CHRParser director = new CHRParser(lexer, builder, options);
            director.construct();
            return builder.getResult();
        } catch (BuilderException be) {
            throw (be.getCause() != null)? be.getCause() : be;
        }
    }

    public static void printUsage(Options options) {
        printUsage(options, System.out);
    }

    public static void printUsage(Options options, PrintStream out) {
        out.println("Usage: java compiler.Main [-options] files...");
        out.println("       (compile one or more jchr source files)");
        out.println("   or: java compiler.Main [-options] < file");
        out.println("       (compile a jchr source supplied through the standard input stream)");

        out.println();
        out.println("where possible options include (boolean values: "+ Generic.VALUES +"):");
        options.printUsage(out);
    }

    private static Iterable<InputStream> getInputStreams(final Options options) 
    throws OptionsException {
        if (options.hasArguments()) {
            return new Iterable<InputStream>() {
                public Iterator<InputStream> iterator() {
                    return new Iterator<InputStream>() {
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    
                        private int i;
                        private boolean atLeastOne;
                        private InputStream next;
                        
                        public InputStream next() {
                            if (! hasNext()) throw new NoSuchElementException();
                            atLeastOne = true;
                            InputStream result = next;
                            next = null;
                            return result;
                        }

                        public boolean hasNext() {
                            if (next != null) return true;
                            if (i >= options.getNbArguments()) {
                                if (!atLeastOne) printUsage(options);
                                return false;
                            }
                            
                            try {
                                next = new FileInputStream(options.getArgumentAt(i++));
                                return true;
                                
                            } catch (FileNotFoundException fnfe) {
                                System.err.println(fnfe);
                                return hasNext();
                            }
                        }
                    };
                }
            };             
        } else
            try {
                if (options.useBlockingStandardInput() || System.in.available() > 0) {
                    return new Singleton<InputStream>(System.in);
                }
            } catch (IOException ioe) {
                System.err.println(ioe);
            }

        throw new OptionsException("No input file(s) given...");
    }
}
