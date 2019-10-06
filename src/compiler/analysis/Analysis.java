package compiler.analysis;

import compiler.CHRIntermediateForm.CHRIntermediateForm;
import compiler.analysis.history.HistoryAnalysis;
import compiler.analysis.indexselection.GreedyHashLookupInsertor;
import compiler.analysis.indexselection.LookupCategorizer;
import compiler.analysis.join.JoinAnalysor;
import compiler.analysis.joinordering.GreedyJoinOrderer;
import compiler.analysis.observation.ObservationAnalysis;
import compiler.analysis.passiveness.PassivenessFacade;
import compiler.analysis.reactiveness.ReactivenessAnalysis;
import compiler.analysis.removal.RemovalAnalysor;
import compiler.analysis.setsemantics.SetSemanticsDetection;
import compiler.analysis.stack.RecursionAnalysor;
import compiler.analysis.variables.SingletonDetector;
import compiler.options.Options;

public final class Analysis {

    private Analysis() { /* non-instantiatable FACADE */ }

    public static void analyse(CHRIntermediateForm cif, Options options) 
    throws AnalysisException {
        new SingletonDetector(cif, options).doAnalysis();
        
        PassivenessFacade passiveness = new PassivenessFacade(cif, options);
        ObservationAnalysis observation = new ObservationAnalysis(cif, options);

        passiveness.doAnalysises();
        while (observation.doAnalysis() && passiveness.doAnalysises());
        
        passiveness.printResults();
        observation.printResult();

        new ReactivenessAnalysis(cif, options).doAnalysis();
        new SetSemanticsDetection(cif, options).doAnalysis();
        new JoinAnalysor(cif, options).doAnalysis();
        new GreedyJoinOrderer(cif, options).doAnalysis();
        new GreedyHashLookupInsertor(cif, options).doAnalysis();
        new LookupCategorizer(cif, options).doAnalysis();
        new HistoryAnalysis(cif, options).doAnalysis();
        new RecursionAnalysor(cif, options).doAnalysis();
        new RemovalAnalysor(cif, options).doAnalysis();
        new ValidityTest(cif, options).doAnalysis();
    }
}