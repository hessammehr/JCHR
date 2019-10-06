package compiler.analysis.history;

import runtime.BooleanAnswer;
import runtime.PrimitiveAnswerSolver;

import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConjunct;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.Lookup;
import compiler.CHRIntermediateForm.constraints.ud.schedule.AbstractScheduleVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduled;
import compiler.CHRIntermediateForm.rulez.Head;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.analysis.AnalysisException;
import compiler.analysis.CifAnalysor;
import compiler.options.Options;

/**
 * Analyses which occurrences can safely skip checking the propagation history,
 * while they are activated for the first time (<em>if they are reactivated, the
 * history is always checked</em>), and as a result, which rules should not keep 
 * a propagation history.
 * <br/>
 * <ul>
 * <li>
 * <em>
 *  This analysis should be done after all storage analyses. It also
 *  uses reactiveness and idempotence information.
 * </em>
 * </li>
 * <li>
 * <em>
 *  This analysis has to be done after join ordering.
 * </em>
 * </li>
 * <li>
 * <em>
 *  This analysis assumes generation optimization is in place.
 * </em>
 * </li>
 * <li>
 * <em>
 *  This analysis assumes iterators, once instantiated,
 *  do not include constraints newer then themselves.
 * </em>
 * </li>
 * </ul>
 * 
 * @author Peter Van Weert
 */
public class HistoryAnalysis extends CifAnalysor {
    
    private HistoryHandler historyHandler;

    public HistoryAnalysis(ICHRIntermediateForm cif, Options options) {
        super(cif, options);
        setHistoryHandler(new HistoryHandler(new PrimitiveAnswerSolver()));
    }
    
    @Override
    public boolean doAnalysis() throws AnalysisException {
        reset();
        
        // first try some cheap tests:
        analyseRules();
        
        // then a more expensive, but more complete test:
        prepConstraints();
        analyseConstraints();
        
        return printResult();
    }
    
    @Override
    protected void analyse(Rule rule) throws AnalysisException {
    	if (rule.needsHistory()) {
    		Head head = rule.getPositiveHead();
    		for (Occurrence occurrence : head) {
    			if (!occurrence.getConstraint().mayBeStored()) {
    				setNotCheckHistory(rule);
    				return; 
    			}
    		}
    		
    		if (head.getNbOccurrences() > 1 
    				&& rule.getBody().isIdempotent())
    			setNotCheckHistory(rule);
    	}
    }
    
    @Override
    protected void prep(UserDefinedConstraint constraint) throws AnalysisException {
        try {
            Phase1BodyVisitor visitor = new Phase1BodyVisitor(constraint);
            Rule previous = null;
            
            for (Occurrence occurrence : constraint.getPositiveOccurrences()) {
                if (occurrence.isPassive()) continue;
                Rule rule = occurrence.getRule();
                if (rule == previous) continue;
                previous = rule;
                visitor.payVisitTo(rule);
            }
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }
    
    protected class Phase1BodyVisitor extends AbstractBodyVisitor {
        private UserDefinedConstraint constraint;
    	
    	public Phase1BodyVisitor(UserDefinedConstraint constraint) {
            this.constraint = constraint;
        }
    	
    	public UserDefinedConstraint getConstraint() {
			return constraint;
		}
        
        @Override
        protected void visit(IConjunct __) {
            getHistoryHandler().tellUnknown(getConstraint());
        }
        @Override
        public void visit(UserDefinedConjunct conjunct) {
            getHistoryHandler().tellTells(getConstraint(), conjunct.getConstraint());
        }
    }
    
    
    @Override
    protected void analyse(UserDefinedConstraint constraint) throws AnalysisException {
        try {
            getHistoryHandler().tellReset();
            Phase2BodyVisitor visitor = new Phase2BodyVisitor();
            
            boolean storedBefore = false;
            for (Occurrence occurrence : constraint.getPositiveOccurrences()) {
                if (occurrence.isPassive()) continue;
                
                Rule rule = occurrence.getRule();
                if (rule.needsHistory()) {
                    Head head = rule.getPositiveHead();
                    boolean stored = occurrence.isStored();
                    boolean history = false;

                    /* 
                     * VERY IMPORTANT: only activate transitions have to be considered, 
                     *   (if the occurrence is reactive, the history is ALWAYS checked)
                     */
                    switch (head.getNbOccurrences()) {
                        case 1:
                            history = (storedBefore && askReactivated(occurrence));
                            storedBefore |= stored;
                            // if the constraint is not stored, 
                            // nothing the rule does will observe the constraint
                            if (stored) visitor.payVisitTo(rule);
                        break;
                        
                        case 2: {
                            Lookup lookup = getPartnerLookup(occurrence);
                            Occurrence partner = lookup.getOccurrence();
                            
                            // if the active constraint may already be stored,
                            // the rule might have fired before with the same combination:
                            history = storedBefore &&
                            	// if the generation optimization is done,
                            	// an earlier reactivation cannot cause a problem: 
                            	// the active constraint will have been stopped prior 
                            	// to reaching the current active occurrence:
                                ((!getOptions().doGenerationOptimization() && askReactivated(occurrence)) 
                                		|| askReactivated(partner) || askTold(partner))
                            ;
                            
                            // otherwise, if the active constraint is stored now,
                            // firing the body must not fire the same combination again
                            // (if the partner lookup is a singleton, 
                            // and there is no reason to check the history sofar,
                            // the history does not have to be checked:
                            // the active constraint will jump to the next occurrence immediately):
                            if (!history && stored && !lookup.isSingleton()) {
                                getHistoryHandler().tellSelf();
                                visitor.payVisitTo(rule);
                                history = 
                                	((!getOptions().doGenerationOptimization() && askReactivated(occurrence))
                                			|| askReactivated(partner) || askTold(partner));
                                getHistoryHandler().tellReset_self();
                                storedBefore = true;
                            } else {
                                storedBefore |= stored;
                                if (stored) visitor.payVisitTo(rule);
                            }
                        } break;
                        
                        default: // generalizing the 2-headed case:
                        	if (storedBefore) {
                            	if (!getOptions().doGenerationOptimization() && askReactivated(occurrence)) {
                            		history = true;
                            	} else for (Occurrence partner : occurrence.getPartners()) {
                                    if (askReactivated(partner) || askTold(partner)) {
                                        history = true;
                                        break;
                                    }
                                }

                            	if (history) {
    	                    		if (stored) visitor.payVisitTo(rule);
    	                    		break;
    	                    	}
                            }
                            if (stored && occurrence.looksUpNonSingletonPartners()) {
                                storedBefore = true;
                                getHistoryHandler().tellSelf();
                                visitor.payVisitTo(rule);
                                
                                if (!getOptions().doGenerationOptimization() && askReactivated(occurrence)) {
                            		history = true;
                            	} else for (Occurrence partner : occurrence.getPartners()) {
                            		if (askReactivated(partner) || askTold(partner)) {
                            			history = true;
                            			break;
                            		}
                                }
                                
                                getHistoryHandler().tellReset_self();
                            } else {
                            	if (stored) visitor.payVisitTo(rule);
                            }
                        break;
                    }
                    
                    if (! history) setNotCheckHistory(occurrence);
                    
                } else {
                    if (storedBefore |= occurrence.isStored()) 
                    	visitor.payVisitTo(rule);
                }
            }
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }
    
    
    private static Lookup getPartnerLookup(IScheduled scheduled) {
        class SimpleScheduleVisitor extends AbstractScheduleVisitor {
            public Lookup result;
            
            @Override
            public void visit(Lookup lookup) throws Exception {
                if (result == null) result = lookup;
            }
        }
        
        try {
            SimpleScheduleVisitor visitor = new SimpleScheduleVisitor();
            scheduled.accept(visitor);
            return visitor.result;
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }
    
    protected boolean askTold(Occurrence occurrence) {
        return occurrence.isActive()
            && askTold(occurrence.getConstraint());
    }
    protected boolean askTold(UserDefinedConstraint constraint) {
        BooleanAnswer result = new BooleanAnswer();
        getHistoryHandler().tellTold(constraint, result);
        return result.getValue();
    }
    
    protected boolean askReactivated(Occurrence occurrence) {
        return occurrence.isReactive()
            && askReactivated(occurrence.getConstraint());
    }
    protected boolean askReactivated(UserDefinedConstraint constraint) {
    	BooleanAnswer result = new BooleanAnswer();
        getHistoryHandler().tellReactivated(constraint, result);
        return result.getValue();
    }
    
    protected class Phase2BodyVisitor extends AbstractBodyVisitor {
        @Override
        protected void visit(IConjunct __) {
            getHistoryHandler().tellPessimistic();
        }
        @Override
        public void visit(UserDefinedConjunct conjunct) {
            getHistoryHandler().tellTell(conjunct.getConstraint());
        }
    }
    
    
    protected void setNotCheckHistory(Rule rule) {
    	rule.setNoHistory();
    	incNbDetected();
    }
    protected void setNotCheckHistory(Occurrence occurrence) {
        occurrence.doNotCheckHistoryOnActivate();
        setResult();
        if (! occurrence.getRule().needsHistory())
            incNbDetected();
    }
    
    
    private int nbDetected;
    private boolean result;
    
    protected void reset() {
        nbDetected = 0;
        result = false;
    }
    
    protected void setResult() {
        result = true;
    }
    protected boolean hasResult() {
        return result;
    }
    
    public int getNbDetected() {
        return nbDetected;
    }
    protected void incNbDetected() {
        nbDetected++;
    }
    
    
    public boolean printResult() {
    	final int num = getNbDetected();
        switch (num) {
            case 0: return false;
            case 1: 
                System.out.println(" --> optimization: no history will be kept for one rule");
            break;
            default:
                System.out.printf(" --> optimization: no history will be kept for %d rules%n", num);
        }
        return true;
    }
    
    
    protected HistoryHandler getHistoryHandler() {
        return historyHandler;
    }
    protected void setHistoryHandler(HistoryHandler historyHandler) {
        this.historyHandler = historyHandler;
    }
}