package compiler.analysis;

import static compiler.analysis.JCHRFreeAnalysor.isJCHRFree;
import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.rulez.Guard;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.options.Options;

/**
 * Is the given intermediate form valid or not?
 * Currently this analysis tests whether all negative
 * heads are passive (raises an error if an active negative
 * head is found), and whether all guards are JCHR free
 * (raises warnings for guards that may include call backs
 * to the JCHR handler).
 *  
 * @author Peter Van Weert
 */
public class ValidityTest extends CifAnalysor {

    public ValidityTest(ICHRIntermediateForm cif, Options options) {
        super(cif, options);
    }

    @Override
    public boolean doAnalysis() throws AnalysisException {
        analyseConstraints();
        analyseRules();
        return false;
    }
    
    @Override
    protected void analyse(UserDefinedConstraint constraint) throws AnalysisException {
    	for (Occurrence occurrence : constraint.getNegativeOccurrences())
            if (! occurrence.isPassive())
                throw new AnalysisException(
                    "Active negative occurrences are not yet supported (%s)",
                    occurrence
                );
    }
    
    @Override
    protected void analyse(Rule rule) {
    	checkWhetherJCHRFree(rule.getPositiveHead().getGuard());
    	for (NegativeHead head : rule.getNegativeHeads())
    		checkWhetherJCHRFree(head.getGuard());
    }
    private void checkWhetherJCHRFree(Guard guard) {
    	if (!isJCHRFree(guard))
    		raiseWarning("The guard " + guard + " may call JCHR constraints");    	
    }
}
