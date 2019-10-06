package compiler;

import java.io.IOException;

import util.JavaCompiler;

public class Bootstrap {
    public static void main(String[] args) throws IOException {
    	compileAnalysisJCHRFiles(true);
        compileAnalysisJavaFiles();
        compileAnalysisJCHRFiles(false);
    }
    
    private static void compileAnalysisJCHRFiles(boolean bootstrapping) throws IOException {
    	compiler.Main.main(
    		"-analysis", bootstrapping? "off" : "on",
            "compiler/analysis/observation/observation.jchr",
            "compiler/analysis/history/history.jchr",
            "compiler/analysis/stack/recursion.jchr",
            "compiler/analysis/eq.jchr",
            "compiler/analysis/removal/removal.jchr"
        );
    }
    
    private static void compileAnalysisJavaFiles() throws IOException {
        JavaCompiler javac = JavaCompiler.createInstance();
        javac.compile(
            "compiler/analysis/observation/ObservationAnalysis.java",
            "compiler/analysis/history/HistoryAnalysis.java",
            "compiler/analysis/setsemantics/SetSemanticsDetection.java",
            "compiler/analysis/removal/RemovalAnalysor.java",
            "compiler/analysis/stack/RecursionAnalysor.java"
        );
        javac.close();
    }
}