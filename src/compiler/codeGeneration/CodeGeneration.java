package compiler.codeGeneration;

import static compiler.codeGeneration.HandlerCodeGenerator.getHandlerTypeName;
import static compiler.codeGeneration.TupleCodeGenerator.getTupleClassName;
import static compiler.codeGeneration.TupleCodeGenerator.getTuplePackage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.options.Options;

public final class CodeGeneration {

	private CodeGeneration() { /* non-instantiatable facade class */ }
	
	public static File[] generateAllSourceFiles(ICHRIntermediateForm cif, Options options) 
	throws IOException, GenerationException{
		Tuple tuple = doGenerateHandlerSourceFile(cif, options);
		
		Set<Integer> aritySet = tuple.tupleArities;
		int n = aritySet.size(), arities[] = new int[n], i = 0;
		for (Integer arity : aritySet) arities[i++] = arity;
		
		File[] tupleFiles = generateTupleSourceFiles(arities);
		
		File[] result = new File[n+1];
		result[0] = tuple.generatedFile;
		System.arraycopy(tupleFiles, 0, result, 1, n);
		
		return result;
	}
	
	public static File generateHandlerSourceFile(ICHRIntermediateForm cif, Options options) 
	throws GenerationException, IOException {
		return doGenerateHandlerSourceFile(cif, options).generatedFile;
	}
	protected static Tuple doGenerateHandlerSourceFile(ICHRIntermediateForm cif, Options options) 
	throws GenerationException, IOException {
        System.out.printf("Generating code for %s handler%n", cif.getHandlerName());
        
        File file = getHandlerFile(cif);
        HandlerCodeGenerator generator = new HandlerCodeGenerator(cif, options, new FileWriter(file));
        generator.generate();
        
        System.out.println("Generated code written to " + file);
        
        return new Tuple(generator.getUsedTupleArities(), file);
    }
    protected static class Tuple {
    	public final Set<Integer> tupleArities;
    	public final File generatedFile;
    	public Tuple(Set<Integer> tupleArities, File generatedFile) {
			this.tupleArities = tupleArities;
			this.generatedFile = generatedFile;
		}
    }
    

    public static File[] generateTupleSourceFiles(int... tupleArities) 
    throws GenerationException,IOException {
        if (tupleArities == null || tupleArities.length == 0) 
        	return new File[0];
        
        System.out.println("Generating helper classes...");
        
        final File[] files = new File[tupleArities.length];
        int i = 0;
        for (int arity : tupleArities)
            files[i++] = generateTupleSourceFile(arity);
        return files;
    }
    protected static File generateTupleSourceFile(int arity) throws GenerationException, IOException {
         File file = getTupleFile(arity); 
         new TupleCodeGenerator(new FileWriter(file), arity).generate();
         System.out.println(" --> " + file);
         return file;
    }
    
	
    public static File getHandlerFile(ICHRIntermediateForm cif) throws IOException {
    	return new File(getHandlerOutputDirectory(cif), getHandlerFileName(cif));
    }
	public static String getHandlerFileName(ICHRIntermediateForm cif) {
		return getHandlerTypeName(cif.getHandler()).concat(".java");
    }
    public static File getHandlerOutputDirectory(ICHRIntermediateForm cif) throws IOException {
        return getExistingDirectory(cif.getHandler().getPackageName()); 
    }
	
    public static String getTupleFileName(int arity) {
        return getTupleClassName(arity).concat(".java");
    }
	public static File getTupleOutputDirectory() throws IOException {
        return getExistingDirectory(getTuplePackage().getName());
    }
    public static File getTupleFile(int arity) throws IOException {
        return new File(getTupleOutputDirectory(), getTupleFileName(arity));
    }
	
	private static File getExistingDirectory(String packageName) throws IOException {
		File result = new File('.' + File.separator + packageName.replace('.', File.separatorChar));
        if (result.exists() && !result.isDirectory())
            throw new IOException("A file " + result + " exists!");
        if (!result.exists() && !result.mkdirs())
            throw new IOException("Could not create directory " + result);
        return result;
	}
}
