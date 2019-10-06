package compiler.analysis.passiveness;

import compiler.CHRIntermediateForm.CHRIntermediateForm;
import compiler.analysis.AnalysisException;
import compiler.options.Options;

public final class PassivenessFacade {
    
    private CHRIntermediateForm cif;
    
    private Options options;
    
    protected NeverStoredAnalysis neverStoredAnalysis;

    public PassivenessFacade(CHRIntermediateForm cif, Options options) {
        this.cif = cif;
        this.options = options;
    }
    
    public boolean doAnalysises() throws AnalysisException {
        PassivenessAnalysis detector;
        int oldNbDetected = getNbDetectedPassiveOccurrences();
        
        if (!hasRun()) { 
            // avoid spending time, or making mistakes, on certain 
            // trivial cases (like unconditional removal that is annotated passive)
            new RulesRemover(cif, options).doAnalysis();
            
            // it IS important symmetry detection is done before
            // never stored analysis (the latter looks at kept 
            // occurrences that might be subsumed by other ones!)
            detector = new SymmetryAnalysis(cif, options);
            if (detector.doAnalysis())
                addToNbDetected(detector.getNbDetected());
            
            neverStoredAnalysis = new NeverStoredAnalysis(cif, options);
        }
        
        detector = neverStoredAnalysis;
        if (detector.doAnalysis())
            addToNbDetected(detector.getNbDetected());
        
        if (oldNbDetected != getNbDetectedPassiveOccurrences())
            new RulesRemover(cif, options).doAnalysis();
        
        detector = new NeverRemovedAnalysis(cif, options);
        if (detector.doAnalysis()) {
            addToNbDetected(detector.getNbDetected());
            new RulesRemover(cif, options).doAnalysis();
        }
        
        return (oldNbDetected != getNbDetectedPassiveOccurrences());
    }
    
    public void printResults() {
        neverStoredAnalysis.printResult();
        printResult(getNbDetectedPassiveOccurrences());
    }
    
    private static void printResult(int nbDetected) {
        switch (nbDetected) {
            case 0: break;
            case 1: 
                System.out.println(" --> optimization: detected one passive occurrence");
            break;
            default:
                System.out.printf(" --> optimization: detected %d passive occurrences%n", nbDetected);
        }
    }
    
    private int nbDetected;
    
    public int getNbDetectedPassiveOccurrences() {
        return nbDetected;
    }
    protected void addToNbDetected(int num) {
        nbDetected += num;
    }
    
    public CHRIntermediateForm getCif() {
        return cif;
    }

    public Options getOptions() {
        return options;
    }
    
    public boolean hasRun() {
        return neverStoredAnalysis != null;
    }
}