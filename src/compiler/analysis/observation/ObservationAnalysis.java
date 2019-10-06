package compiler.analysis.observation;

import static compiler.CHRIntermediateForm.modifiers.Modifier.isExported;
import runtime.BooleanAnswer;
import runtime.PrimitiveAnswerSolver;

import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConjunct;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.analysis.AnalysisException;
import compiler.analysis.CifAnalysor;
import compiler.options.Options;

/*
 * TODO: verwijderingen kunnen met actieve negatieve heads ook zorgen voor observaties
 */
public class ObservationAnalysis extends CifAnalysor {

    private ObservationHandler handler;
    
    public ObservationAnalysis(ICHRIntermediateForm cif, Options options) {
        super(cif, options);
        setObservationHandler(new ObservationHandler(new PrimitiveAnswerSolver()));
    }
    
    @Override
    public boolean doAnalysis() throws AnalysisException {
    	getObservationHandler().reset();
        
        int oldNb = getNbDelays();
        prepRules();
        analyseConstraints();
        return oldNb != getNbDelays();
    }
    
    /* * * * * *\
     * PHASE 1 *
    \* * * * * */
    
    @Override
    protected void prep(Rule rule) {
        for (Occurrence occurrence : rule.getPositiveHead()) {
            if (occurrence.isPassive()) continue;
            
            final UserDefinedConstraint constraint = occurrence.getConstraint();
            
            for (Occurrence partner : rule.getPositiveHead())
                if (partner != occurrence) 
                    observes(constraint, partner.getConstraint());
            
            for (NegativeHead negativeHead : rule.getNegativeHeads())
                if (negativeHead.isSelective())
                    for (Occurrence negative : negativeHead)
                        observes(constraint, negative.getConstraint());

            try {
                rule.getBody().accept(new ObservingBodyVisitor(constraint));
            } catch (Exception x) {
                throw new RuntimeException(x);
            }
        }
    }
    
    protected class ObservingBodyVisitor extends BasicBodyVisitor {
    	private UserDefinedConstraint constraint;
        
        public ObservingBodyVisitor(UserDefinedConstraint constraint) {
        	this.constraint = constraint;
        }
        
        public UserDefinedConstraint getConstraint() {
			return constraint;
		}

        @Override
        protected void visitJCHRFreeConjunct(IConjunct conjunct) {
        	// NOP
        }
        @Override
        protected void visitJCHRConjunct(UserDefinedConjunct conjunct) {
        	activates(getConstraint(), conjunct.getConstraint());
        }
        @Override
        protected void visitTriggeringConjunct(IConjunct conjunct) {
        	activateReactives(getConstraint());
        }
        @Override
        protected void visitPessimisticConjunct(IConjunct conjunct) {
        	activateExported(getConstraint());
        }
    }
    
    /* * * * * *\ 
     * PHASE 2 *
    \* * * * * */
    
    @Override
    @SuppressWarnings("all")
    protected void analyse(UserDefinedConstraint constraint) {
        if (!constraint.mayBeStored()) return;
        
        Rule current, previous = null;
        boolean dontStore = false;
        for (Occurrence occurrence : constraint.getPositiveOccurrences()) {
            if (! occurrence.isStored()) continue;
            if ((current = occurrence.getRule()) == previous) {
                if (dontStore) setUnstored(occurrence);
            } else try {
                previous = current;
                
                TestingBodyVisitor visitor = new TestingBodyVisitor(constraint);
                current.getBody().accept(visitor);
                if (dontStore = !visitor.isObserved())
                    setUnstored(occurrence);
                
            } catch (Exception x) {
                throw new RuntimeException(x);
            }
        }
    }
    
    protected class TestingBodyVisitor extends BasicBodyVisitor {
        private boolean observed;
        private UserDefinedConstraint constraint;
        
        public TestingBodyVisitor(UserDefinedConstraint constraint) {
        	this.constraint = constraint;
        }
        
        public UserDefinedConstraint getConstraint() {
			return constraint;
		}
        
        @Override
        protected void visitJCHRFreeConjunct(IConjunct conjunct) {
        	// NOP
        }
        @Override
        protected void visitPessimisticConjunct(IConjunct conjunct) {
        	if (!isObserved()) setObserved(testObservedByExported(getConstraint()));
        }
        @Override
        protected void visitTriggeringConjunct(IConjunct conjunct) {
        	if (!isObserved()) setObserved(testObservedByReactivated(getConstraint()));
        }
        @Override
        protected void visitJCHRConjunct(UserDefinedConjunct conjunct) {
        	if (!isObserved()) 
        		setObserved(testObserved(conjunct.getConstraint(), getConstraint()));
        }
        
        protected void setObserved(boolean observed) {
			this.observed = observed;
		}
        public boolean isObserved() {
            return observed;
        }
    }
    
    protected void setUnstored(Occurrence occurrence) {
        occurrence.setUnstored();
        incNbDelays();
    }
    
    /* * * * * * *\ 
     * PHASE "3" *
    \* * * * * * */
    
    private int nbDelays;
    
    public int getNbDelays() {
        return nbDelays;
    }
    protected void incNbDelays() {
        nbDelays++;
    }
    
    public void printResult() {
        switch (getNbDelays()) {
            case 0: break;
            case 1: 
                System.out.println(" --> optimization: delaying storage for one occurrence");
            break;
            default:
                System.out.printf(" --> optimization: delaying storage for %d occurrences%n", getNbDelays());
        }
    }
    
    
    /* * * * * * * * * *\
     * HELPER METHODS  * 
     * * * * * * * * * */ 
    
    protected void setObservationHandler(ObservationHandler handler) {
        this.handler = handler;
    }
    protected ObservationHandler getObservationHandler() {
        return handler;
    }
    
    protected boolean testObservedByExported(UserDefinedConstraint X) {
    	if (X.isReactive()) return true;
    	for (UserDefinedConstraint constraint : getUserDefinedConstraints())
    		if ((constraint.isReactive() || isExported(constraint))
    				&& testObserved(constraint, X)) return true;
    	return false;
    }
    
    protected boolean testObservedByReactivated(UserDefinedConstraint X) {
    	if (X.isReactive()) return true;
    	for (UserDefinedConstraint constraint : getUserDefinedConstraints())
    		if (constraint.isReactive() && testObserved(constraint, X)) 
    			return true;
    	return false;
    }
    
    protected boolean testObserved(UserDefinedConstraint X, UserDefinedConstraint Y) {
    	BooleanAnswer Result = new BooleanAnswer();
        getObservationHandler().tellObserved(X, Y, Result);
        return Result.getValue();
    }
    
    protected void activateReactives(UserDefinedConstraint X) {
    	for (UserDefinedConstraint Y : getUserDefinedConstraints())
    		if (Y.isReactive()) activates(X, Y);
    }
    protected void activateExported(UserDefinedConstraint X) {
    	for (UserDefinedConstraint Y : getUserDefinedConstraints())
    		if (Y.isReactive() || isExported(Y)) activates(X, Y);
    }
    
    protected void activates(UserDefinedConstraint X, UserDefinedConstraint Y) {
        getObservationHandler().tellActivates(X, Y);
    }

    protected void observes(UserDefinedConstraint X, UserDefinedConstraint Y) {
        getObservationHandler().tellObserves(X, Y);
    }
}