package compiler.analysis.passiveness;

import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.analysis.AnalysisException;
import compiler.analysis.CifAnalysor;
import compiler.options.Options;

public abstract class PassivenessAnalysis extends CifAnalysor {

    public PassivenessAnalysis(ICHRIntermediateForm cif, Options options) {
        super(cif, options);
    }

    private int nbDetected;
    
    protected void resetNbDetected() {
        nbDetected = 0;
    }
    public int getNbDetected() {
        return nbDetected;
    }
    protected void incNbDetected() {
        nbDetected++;
    }
    
    @Override
    public final boolean doAnalysis() throws AnalysisException {
        resetNbDetected();
        doPassivenessAnalysis();
        return getNbDetected() > 0;
    }
    
    protected abstract void doPassivenessAnalysis() throws AnalysisException;
    
    protected void setPassive(Occurrence occurrence) {
        if (! occurrence.isPassive()) {
            occurrence.setPassive();
            incNbDetected();
        }
    }
}
