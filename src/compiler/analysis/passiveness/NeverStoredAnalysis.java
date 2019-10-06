package compiler.analysis.passiveness;

import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.REMOVED;
import static compiler.CHRIntermediateForm.constraints.ud.StorageInfo.FINALLY;
import static compiler.CHRIntermediateForm.constraints.ud.StorageInfo.NEVER;

import java.util.HashMap;
import java.util.Map;

import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.rulez.Head;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.analysis.AnalysisException;
import compiler.options.Options;

/*
our conditions for unguarded removal are straightforward

Note:
 - we assume all trivial ... 
       e.g.  ... <=> X = X, 5 > 3 | ...
   ... and entailed guards are removed 
       e.g. c(X) <=> X > 0 | ...
            c(X) <=> X <= 0 | ...  // entailed guard can be removed!

 - XXX: we do not have to consider partner constraints as a
     condition if we somehow now they are always present
     (at least when the active occurrence is here)
     Simple example:
       c ==> d.
       d \ c <=> true.
     Not sure what the general case would look like...
 
 - what to do about negative heads?
     e.g.
         c \ c <=> true.   // singleton
         c \\ c <=> true.  // ???
   we should simplify those away!
*/

/**
 * Detects constraints that are never stored (or always stored 
 * sometimes, but this is less important momentarily). 
 * This information is important, because it plays a role in 
 * further analysis, plus it avoids the allocation of unneeded
 * fields and/or data structures.
 * <br/>
 * But why is it in this package? Well, when is something never stored? 
 * If it is at some point always removed (i.e. never stored). And all 
 * occurrences of that constraint past that point are then of course, 
 * indeed, passive. Also: when we know a constraint is never stored,
 * there is no point in looking it up, is there! 
 * It might not be a good idea to mix these two, but for now, 
 * it works well enough.
 * 
 * @author Peter Van Weert
 */
public class NeverStoredAnalysis extends PassivenessAnalysis {
    
    NeverStoredAnalysis(ICHRIntermediateForm cif, Options options) {
        super(cif, options);
    }
    
    @Override
    protected void resetNbDetected() {
    	RULES.clear();
    	super.resetNbDetected();
    }
    
    @Override
    protected void doPassivenessAnalysis() throws AnalysisException {
        analyseRules();
        analyseConstraints();
    }
    
    @Override
    protected void analyse(Rule rule) {
        if  (rule.getPositiveGuard().isEmpty() && !rule.hasSelectiveNegativeHeads()) {
            Head head = rule.getPositiveHead();
            if (head.getNbOccurrences() != 1) return;
            Occurrence theOccurrence = head.getOccurrenceAt(0);
            UserDefinedConstraint constraint = theOccurrence.getConstraint();
            
            //assert !theOccurrence.isPassive();  // could only be semantic-changing annotation...
            if (theOccurrence.hasIntraConstraintImplicitGuards()) return;

            if (theOccurrence.getType() == REMOVED) {
                boolean alreadyRemoved = false, possiblyStored = false;
                for (Occurrence occurrence : constraint.getPositiveOccurrences()) {
                    if (occurrence == theOccurrence)
                        alreadyRemoved = true;
                    else if (alreadyRemoved)
                        setPassive(occurrence);
                    else if (!possiblyStored && occurrence.isStored()) 
                        possiblyStored = true;
                }
                
                if (!possiblyStored) setNeverStored(constraint, rule.getNbr());
            }
        }
    }
    
    private final Map<UserDefinedConstraint, Integer> RULES
    	= new HashMap<UserDefinedConstraint, Integer>();
    
    protected void setNeverStored(UserDefinedConstraint constraint, int ruleNbr) {
    	Integer previousRuleNb = RULES.get(constraint);
    	if (previousRuleNb == null || previousRuleNb > ruleNbr) {
    		RULES.put(constraint, ruleNbr);
    		
    		if (constraint.getStorageInfo() != NEVER) {
    			incNbNeverStored();
    			constraint.updateStorageInfo(NEVER);
    		}
    		
    		// if not re-analysing:
    		if (previousRuleNb == null) {
		        Rule previous = null;
		        for (Occurrence occurrence : constraint.getPositiveOccurrences()) {
		            Rule rule = occurrence.getRule();
		            if (rule == previous) continue;
		            previous = rule;
		            
		            for (Occurrence partner : rule.getPositiveHead()) {
		                if (partner == occurrence) continue;
		                setPassive(partner);
		                if (partner.getConstraint().equals(constraint))
		                	setPassive(occurrence);
		            }
		            for (NegativeHead negativeHead : rule.getNegativeHeads()) {
		                for (Occurrence negative : negativeHead)
		                    setPassive(negative);
		            }
		        }
	        
	    		// re-analyse earlier rules 
		        // required because in earlier rules other constraints
		        // may be detected never stored now 
		        // 			(I guess, haven't checked in a while)
	    		previous = null;
		        for (Occurrence occurrence : constraint.getNegativeOccurrences()) {
		            Rule r = occurrence.getRule(); 
		            if (r.getNbr() >= ruleNbr) return;
		            if (r == previous) continue;
		            analyse(r);
		            previous = r;
		        }
    		}
    	}
    }
    
    @Override
    protected void analyse(UserDefinedConstraint constraint) throws AnalysisException {
    	if (constraint.mayBeStored()) {
    		for (Occurrence occurrence : constraint.getPositiveOccurrences())
    			if (occurrence.isActive() && 
    					(occurrence.getType() == REMOVED || occurrence.isStored())
				) return;
    		constraint.updateStorageInfo(FINALLY);
    	}
    }
    
    private int nbNeverStored;
    
    public int getNbNeverStored() {
        return nbNeverStored;
    }
    protected void incNbNeverStored() {
        nbNeverStored++;
    }
    
    protected void printResult() {
        switch (getNbNeverStored()) {
            case 0: break;
            case 1: 
                System.out.println(" --> optimization: detected one never stored constraint");
            break;
            default:
                System.out.printf(" --> optimization: detected %d never stored constraints%n", getNbNeverStored());
        }
    }
}