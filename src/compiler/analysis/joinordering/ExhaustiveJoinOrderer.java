package compiler.analysis.joinordering;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import util.collections.IdentityHashSet;
import util.iterator.ChainingIterator;
import util.iterator.Permutator;

import compiler.CHRIntermediateForm.CHRIntermediateForm;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrderElement;
import compiler.CHRIntermediateForm.constraints.ud.schedule.ISelector;
import compiler.CHRIntermediateForm.constraints.ud.schedule.JoinOrder;
import compiler.CHRIntermediateForm.constraints.ud.schedule.JoinOrder.Elements;
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
 * Play a role in the heuristics: 
 *   - number of variables already seeded
 *      . include explicit equality guards
 *      . include explicitized equality guards
 *      . of course include implicit guards
 *   - indices that we could use to do the lookup
 *      . might be interesting first to check in a way how 
 *          much we can use each index in theory
 *   - seeding of partners yet to come
 */

/*
 * TODO
 *  - guards of negative heads are NOT scheduled at the moment!      
 *      ==> they are in the new implementation, but the latter is slower
 *      ==> try to adjust the old implementation such that these guards are
 *          scheduled, AND that the getSelectors() method does not return
 *          wrong results (always think about negative heads / guards! )
 *          
 *  - are nameless variables to be counted (not counted at the moment)
 *  - rules with 2 or less occurrences, and similar 1-headed negative heads,
 *      could easily have guards (or even negative heads) that need to 
 *      be scheduled early.
 *      Device some clever way of doing these things (chain-of-command-like,
 *      with also the possibility of an a-star alternative, or different
 *      heuristics, on a per rule (negative head) basis)!
 * 
 * XXX
 *  - maybe add implicit guards? 
 *      ==> NO (do this later, also needs to be done when no ordering)     
 *  - maybe include in lookups which argument positions could be indexed?
 *      ==> BETTER NOT (do this together with implicit guard insertion) 
 */

/**
 * This is an exhaustive join orderer based on a heuristic similar to
 * the one used by the HAL CHR compiler.
 */
@SuppressWarnings("all") // XXX
public class ExhaustiveJoinOrderer extends CifAnalysor {
    
    public ExhaustiveJoinOrderer(CHRIntermediateForm intermediateForm, Options options) {
        super(intermediateForm, options);
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
            initialize(head);
            analyse(head.getOccurrencesArray());
            finalize(head);
        }
    }
    
    protected void analysePositive(Occurrence active, Occurrence[] partners) {
        if (! active.isPassive()) {
            initialize(active);
            analyse(partners);
            finalize(active);
        }
    }
    
    private Set<Variable> 
        initialFixedVariables = new TreeSet<Variable>(), 
        fixedVariables = new TreeSet<Variable>();
    
    protected void initialize(Occurrence active) {
        currentOrdering.reset();
        initialFixedVariables.clear();
        addVariablelessGuards(active.getHead().getGuard());
        
        for (Variable var1 : active.getVariables()) {
            if (initialFixedVariables.add(var1)) {
                selectors: 
                for (ISelector selector : getSelectors(var1)) {
                    for (Variable var2 : selector.getJoinOrderPrecondition())
                        if (! initialFixedVariables.contains(var2))
                            continue selectors;
                    currentOrdering.addElement(selector);
                }
            }
        }
        
        initialize();
    }
    
    protected void initialize(NegativeHead negativeHead) {
        currentOrdering.reset();
        initialFixedVariables = negativeHead.getJoinOrderPrecondition();
        
        guards: for (IGuardConjunct guard : negativeHead.getGuard()) {
            for (IActualVariable var : guard.getVariables()) {
                if (!initialFixedVariables.contains(var))
                    continue guards;
            }
            currentOrdering.addElement(guard);
        }
        
        initialize();
    }
    
    protected void initialize() {
        initialOrderingLength = currentOrdering.size();

        getMinimalScore().set(JoinCost.MAX_VALUE);
        getOptimalJoinOrdering().reset();
        getOptimalJoinOrdering().addElements(currentOrdering.getElements());
    }
    
    protected void finalize(Occurrence active) {
        active.changeJoinOrder(getOptimalJoinOrdering());
    }
    
    protected void finalize(NegativeHead negativeHead) {
        negativeHead.changeJoinOrder(getOptimalJoinOrdering());
    }
    
    protected void addVariablelessGuards(Guard guard) {
        for (IGuardConjunct guardConjunct : guard)
            if (guardConjunct.getNbVariables() == 0)
                currentOrdering.addElement(guardConjunct);
    }
    
    protected void analyse_(Occurrence[] partners) {
        Permutator<Occurrence> permutator = new Permutator<Occurrence>(partners);
        
        final IdentityHashSet<ISelector> selectors =
            new IdentityHashSet<ISelector>();
        Head head = partners[0].getHead();
        for (IGuardConjunct guard : head.getGuard())
            selectors.add(guard);
        final boolean positive = head.isPositive(); 
        if (positive)
            selectors.addAll(head.getRule().getNegativeHeads());
        selectors.removeAll(currentOrdering.getElements());
        
        while (permutator.hasNext()) {
            permutator.next();
            if (createJoinOrder(partners, selectors)) {
                // we found a new optimum:
                setMinimalScore(getCurrentScore().clone());
                // avoid allocation of some lists where possible: 
                Elements temp = getOptimalJoinOrdering().getElements();
                getOptimalJoinOrdering().setElements(currentOrdering.getElements());
                currentOrdering.setElements(temp);
            }
        }
        
        permutator.reset();
    }
    
    protected boolean createJoinOrder(
        Occurrence[] permutation, IdentityHashSet<ISelector> slctrs
    ) {
        currentOrdering.reset(initialOrderingLength);
        Set<Variable> fixed = new HashSet<Variable>(initialFixedVariables);
        
        sum.reset(); 
        score.reset();
        
        @SuppressWarnings("unchecked")
        IdentityHashSet<ISelector> selectors
            = (IdentityHashSet<ISelector>)slctrs.clone();
        
        for (Occurrence occurrence : permutation) {
            cost.reset();
            
            currentOrdering.addElement(occurrence);
            int unfixed = 0;
            for (Variable variable : occurrence.getVariables())
                if (fixed.add(variable)) unfixed++;
            cost.add(unfixed, unfixed - occurrence.getNbVariables());
            
            int l  = currentOrdering.size();
            
            selectors:
            for (ISelector selector : selectors) {
                for (Variable variable : selector.getJoinOrderPrecondition()) 
                    if (! fixed.contains(variable)) continue selectors;
                
                currentOrdering.addElement(selector);
                cost.add(selector.getSelectivity());
            }
            
            for (int i = l; i < currentOrdering.size(); i++)
                selectors.remove(currentOrdering.getElementAt(i));
            
            sum.add(cost);
            score.add(sum);
            
            if (score.compareTo(getMinimalScore()) >= 0) return false;
        }
        
        return true;
    }
    
    protected void analyse(Occurrence[] partners) {
        Permutator<Occurrence> permutator = new Permutator<Occurrence>(partners);
        
        permutations: 
        while (permutator.hasNext()) {
            permutator.next();
            newJoinOrder();
            for (Occurrence partner : partners) {
                nextPartner(partner);
                
                if (getCurrentScore().compareTo(getMinimalScore()) >= 0)
                    continue permutations;
            }
            
            // we found a new optimum:
            setMinimalScore(getCurrentScore().clone());
            // avoid allocation of some lists where possible: 
            Elements temp = getOptimalJoinOrdering().getElements();
            getOptimalJoinOrdering().setElements(currentOrdering.getElements());
            currentOrdering.setElements(temp);
        }
        
        // permutator changed the order: this has to be restored!
        permutator.reset();
    }
    
    protected void nextPartner(Occurrence partner) {
        addToJoinOrder(partner);
        
        int unfixed = 0, fixed = 0;
        for (Variable var1 : partner.getVariables()) {
            if (fixedVariables.add(var1)) {
                unfixed++;
                
                selectors:
                for (ISelector selector : getSelectors(var1)) {
                    for (Variable var2 : selector.getJoinOrderPrecondition()) {
                        if (! fixedVariables.contains(var2))
                            continue selectors;
                    }
                    addSelector(selector);
                }
            } else {
                fixed++;
            }
        }
        
        cost.add(unfixed, -fixed);
        sum.add(cost);
        score.add(sum);
        cost.reset();
    }
    
    private SCost cost = new SCost(); 
    private JoinCost sum = new JoinCost(), score = new JoinCost();
    
    protected void newJoinOrder() {
        currentOrdering.reset(initialOrderingLength);
        
        fixedVariables.clear();
        fixedVariables.addAll(initialFixedVariables);
        
        cost.reset();
        sum.reset();
        score.reset();
    }
    
    protected JoinCost getCurrentScore() {
        return score;
    }

    private int initialOrderingLength;
    private JoinOrder currentOrdering = new JoinOrder();
    
    protected void addToJoinOrder(IJoinOrderElement element) {
        currentOrdering.addElement(element);
    }
    protected void addSelector(ISelector selector) {
        cost.add(selector.getSelectivity());
        addToJoinOrder(selector);
    }
    
    private JoinCost minimalScore = new JoinCost();
    
    public JoinCost getMinimalScore() {
        return minimalScore;
    }
    protected void setMinimalScore(JoinCost minimalScore) {
        this.minimalScore = minimalScore;
    }
    
    private JoinOrder optimalJoinOrdering = new JoinOrder();
    
    protected JoinOrder getOptimalJoinOrdering() {
        return optimalJoinOrdering;
    }
    protected void setOptimalJoinOrdering(JoinOrder joinOrder) {
        this.optimalJoinOrdering = joinOrder;
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