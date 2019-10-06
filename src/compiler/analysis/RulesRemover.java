package compiler.analysis;

import java.util.Iterator;

import compiler.CHRIntermediateForm.CHRIntermediateForm;
import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.rulez.Head;
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
public abstract class RulesRemover extends CifAnalysor {
    
    public RulesRemover(ICHRIntermediateForm cif, Options options) {
        super(cif, options);
    }

    @Override
    public boolean doAnalysis() throws AnalysisException {
        Iterator<Rule> rules = getRules().iterator();
        int ruleNbr = 1;
        while (rules.hasNext()) {
            Rule rule = rules.next();
            if (isPassive(rule)) {
                rule.terminate();
                rules.remove();
                
                System.err.printf(
                    " --> warning: rule %s will never fire (%s)%n",
                    rule.getIdentifier(), getReason()
                );
            } else {
                // XXX once handler has list of rules, we can use 
                //  getHandler().indexOf(this) to determine rule numbers
                rule.setNbr(ruleNbr++);
            }
        }
        return true;
    }
    
    private String reason;
    
    protected String getReason() {
        return reason;
    }
    protected void setReason(String reason) {
        this.reason = reason;
    }
    
    /**
     * A rule is passive if all its positive occurrences and negative heads
     * are passive, if one of its guards can never succeed, one of its 
     * negative heads is always present, if its a propagation rule 
     * with empty body, etc, etc
     * <br/>
     * In other words: in general a rule is passive if it will or should 
     * never fire.
     * 
     * @return <code>true</code> if this rule is passive.
     */
    protected abstract boolean isPassive(Rule rule);
        
    public static boolean isPassive(Head head) {
        return head.getNbActiveOccurrences() == 0;
    }
}
