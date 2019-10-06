package compiler.analysis.joinordering;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import compiler.CHRIntermediateForm.CHRIntermediateForm;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.schedule.ISelector;
import compiler.CHRIntermediateForm.constraints.ud.schedule.JoinOrder;
import compiler.CHRIntermediateForm.rulez.Guard;
import compiler.CHRIntermediateForm.rulez.Head;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.CHRIntermediateForm.variables.IActualVariable;
import compiler.CHRIntermediateForm.variables.Variable;
import compiler.analysis.AnalysisException;
import compiler.analysis.CifAnalysor;
import compiler.options.Options;

/*
 XXX
     heuristiek:
         . sorteer de partners op #var (stel grootste eerst, noem lijst p)
             ==> p[0].getNbVariables() * p.length * (p.length - 1) / 2
 */
@SuppressWarnings("all") // XXX
public class AStarJoinOrderer extends CifAnalysor {

    /**
     * A priority queue containing partial schedules. Of course,
     * the partial schedule with the lowest estimated cost will
     * be in front of the queue. 
     */
	private PriorityQueue<PartialSchedule> queue;
    
    public AStarJoinOrderer(CHRIntermediateForm cif, Options options) {
        super(cif, options);
    }
    
    @Override
    public boolean doAnalysis() throws AnalysisException {
        analyseRules();
        
        return true;
    }
    
    protected static boolean haveToAnalysePositive(Head head) {
        return head.getNbOccurrences() > 2;
    }
    protected static boolean haveToAnalyseNegative(NegativeHead head) {
        return head.getNbOccurrences() > 1;
    }
    
    protected void analyseRules() {
        for (Rule rule : getRules()) {
            analysePositive(rule.getPositiveHead());
            analyseNegative(rule.getNegativeHeads());
        }
    }
    
    protected void analyseNegative(Iterable<NegativeHead> heads) {
        for (NegativeHead head : heads) analyseNegative(head);
    }
    
    protected void analysePositive(Head head) {
        if (haveToAnalysePositive(head)) {
            Occurrence[] occurrences = head.getOccurrencesArray();
            final int num = occurrences.length-1;
            Occurrence[] partners = new Occurrence[num];
            
            System.arraycopy(occurrences, 1, partners, 0, num);
            analysePositive(occurrences[0], partners);
            
            for (int i = 0; i < num; i++) {
                partners[i] = occurrences[i];
                analysePositive(occurrences[i+1], partners);
            }
        }
    }
    
    protected void analyseNegative(NegativeHead head) {
        if (haveToAnalyseNegative(head)) {
            
        }
    }
    
    protected void analysePositive(Occurrence active, Occurrence[] partners) {
        if (! active.isPassive()) {
            
        }
    }

    protected static class PartialSchedule 
        implements Comparable<PartialSchedule> {
        
        public PartialSchedule(NegativeHead head) {
            fixedVariables = head.getJoinOrderPrecondition();
            
            guards: for (IGuardConjunct guard : head.getGuard()) {
                for (IActualVariable var : guard.getVariables()) {
                    if (!fixedVariables.contains(var))
                        continue guards;
                }
                schedule.addElement(guard);
            }
            
            initialize(head.getOccurrencesArray());
        }
        
        public PartialSchedule(Occurrence active, Occurrence[] partners) {
            scheduleVariablelessGuards(active.getHead().getGuard());
            
            fixedVariables = new HashSet<Variable>();
            for (Variable var1 : active.getVariables()) {
                if (fixedVariables.add(var1)) {
                    selectors:
                    for (ISelector selector : getSelectors(var1)) {
                        for (Variable var2 : selector.getJoinOrderPrecondition())
                            if (! fixedVariables.contains(var2))
                                continue selectors;
                        schedule.addElement(selector);
                    }
                }
            }
            
            initialize(partners);
        }
        
        protected void initialize(Occurrence[] partners) {
            partnersLeft = partners;
            
            
        }
        
        public JoinOrder schedule = new JoinOrder();
        
        public JoinCost 
            sum = new JoinCost(), 
            score = new JoinCost(),
            estimate = new JoinCost();
        
        private Occurrence[] partnersLeft;
        
        private Set<Variable> fixedVariables;
        
        public int compareTo(PartialSchedule other) {
            return this.estimate.compareTo(other.estimate);
        }
        
        public boolean isFinished() {
            return partnersLeft.length == 0; 
        }
        
        protected void scheduleVariablelessGuards(Guard guard) {
            for (IGuardConjunct guardConjunct : guard)
                if (guardConjunct.getNbVariables() == 0)
                    schedule.addElement(guardConjunct);
        }
    }
    
    protected static Iterable<ISelector> getSelectors(final Variable var) {
//        return new Iterable<ISelector>() {
//            @SuppressWarnings("unchecked")
//            public Iterator<ISelector> iterator() {
//                return new ChainingIterator<ISelector>(
//                    var.getPositiveGuards().iterator(), var.getNegativeHeads().iterator()
//                );
//            }
//        };
    	return null; // TODO
    }
}