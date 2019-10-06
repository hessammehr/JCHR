package util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * At least one of {@link #compile(File[])} or
 * {@link #compile(String[])} has to be overriden, as
 * both forward to the other in this abstract class
 * (not overriden either method would result in a stack overflow
 * due to this mutual recursion).
 * 
 * @author Peter Van Weert
 */
public abstract class JavaCompiler implements Closeable {
	
	/**
	 * Compiles a series of files 
	 * (given their absolute or relative paths).
	 * 
	 * @param fileNames
	 * 	A list of names of files that have to be compiled
	 *  (absolute or relative paths).
	 * @see #compile(File[]) 
	 */
	public void compile(String... fileNames) {
		File[] files = new File[fileNames.length];
		for (int i = 0; i < files.length; i++)
			files[i] = new File(fileNames[i]);
		compile(files);
	}
	
	/**
	 * Compiles a series of files.
	 * 
	 * @param files
	 * 	A list of files that have to be compiled.
	 * @see #compile(String[]) 
	 */
	public void compile(File... files) {
		String[] fileNames = new String[files.length];
		for (int i = 0; i < files.length; i++)
			fileNames[i] = files[i].getAbsolutePath();
		compile(fileNames);
	}
	
	public static JavaCompiler createInstance() {
		try {
			Class.forName("javax.tools.ToolProvider");
			JDK6JavaCompiler compiler = new JDK6JavaCompiler();
			if (compiler.isValid()) return compiler;
		} catch (ClassNotFoundException x) {
            // NOP
        }
		
        try {
			Class.forName("com.sun.tools.javac.Main");
			JDK5JavaCompiler compiler = new JDK5JavaCompiler();
			return compiler;
		} catch (ClassNotFoundException y) {
			// NOP
		}
        
        return null;
	}
	
	protected static class JDK6JavaCompiler extends JavaCompiler {
		private StandardJavaFileManager fileManager;
		private javax.tools.JavaCompiler javac;
		
		public JDK6JavaCompiler() {
			init();
		}
		
		@Override
		public void compile(String... fileNames) {
			Iterable<? extends JavaFileObject> javaFiles = getFileManager().getJavaFileObjects(fileNames);
			getJavac().getTask(null, getFileManager(), null, null, null, javaFiles).call();			
		}
		@Override
		public void compile(File... files) {
	        Iterable<? extends JavaFileObject> javaFiles = getFileManager().getJavaFileObjects(files);
			getJavac().getTask(null, getFileManager(), null, null, null, javaFiles).call();
		}
		
		@Override
        public String toString() {
			return getJavac().toString();
		}
		
		public void init() {
			javac = ToolProvider.getSystemJavaCompiler();
		}
		
		public boolean isValid() {
			return javac != null;
		}
		
		protected javax.tools.JavaCompiler getJavac() {
			return javac;
		}
		
		protected StandardJavaFileManager getFileManager() {
			if (fileManager == null) 
				fileManager = getJavac().getStandardFileManager(null, null, null);
			return fileManager;
		}
		
		public void close() throws IOException {
			if (fileManager != null) {
				fileManager.close();
				fileManager = null;
			}
		}
	}

	protected static class JDK5JavaCompiler extends JavaCompiler {
		@Override
		public void compile(String... fileNames) {
			try {
				Class.forName("com.sun.tools.javac.Main")
					.getMethod("compile", String[].class)
					.invoke(null, (Object[])fileNames);
			} catch (IllegalArgumentException e) {
				throw new InternalError();
			} catch (IllegalAccessException e) {
				throw new InternalError();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				throw new InternalError();
			} catch (ClassNotFoundException e) {
				throw new InternalError();
			}
		}
		
		public void close() throws IOException {
			// NOP
		}
	}
}