package compiler.analysis.passiveness;

import static compiler.CHRIntermediateForm.rulez.RuleType.PROPAGATION;

import compiler.CHRIntermediateForm.CHRIntermediateForm;
import compiler.CHRIntermediateForm.rulez.Head;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.options.Options;

/**
 * A very simple <code>Analysor</code> removing rules that cannot or
 * do not have to fire from a given {@link CHRIntermediateForm}. 
 * This analysis can be called multiple times, and will avoid spending 
 * more time than needed on such rules.
 * 
 * @author Peter Van Weert
 */
public class RulesRemover extends compiler.analysis.RulesRemover {
    
    public RulesRemover(CHRIntermediateForm intermediateForm, Options options) {
        super(intermediateForm, options);
    }

    @Override
    protected boolean isPassive(Rule rule) {
        for (NegativeHead negativeHead : rule.getNegativeHeads())
            if (!isPassive(negativeHead)) return false;
        
        if (isPassive(rule.getPositiveHead()))
            setReason("all heads passive");
        else if (rule.getPositiveGuard().fails())
            setReason("guard cannot succeed");
        else if (rule.getType() == PROPAGATION && rule.getBody().isEmpty())
            setReason("empty body");
        else return false;
        
        return true;
    }
        
    public static boolean isPassive(Head head) {
        return head.getNbActiveOccurrences() == 0;
    }
}
