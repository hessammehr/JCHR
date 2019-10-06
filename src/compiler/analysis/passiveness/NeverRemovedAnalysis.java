package compiler.analysis.passiveness;

import static compiler.CHRIntermediateForm.constraints.ud.StorageInfo.ALWAYS;

import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.analysis.AnalysisException;
import compiler.options.Options;

public class NeverRemovedAnalysis extends PassivenessAnalysis {

    public NeverRemovedAnalysis(ICHRIntermediateForm cif, Options options) {
        super(cif, options);
    }

    @Override
    protected void doPassivenessAnalysis() throws AnalysisException {
        analyseConstraints();
    }

    @Override
    protected void analyse(UserDefinedConstraint constraint) {
        if (!constraint.mayBeRemoved()) {
            constraint.updateStorageInfo(ALWAYS);
            for (Occurrence occurrence : constraint.getNegativeOccurrences())
                setPassive(occurrence);
        }
    }
}
