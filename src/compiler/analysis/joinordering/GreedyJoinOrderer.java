package compiler.analysis.joinordering;

import static java.lang.Math.signum;
import static java.util.Arrays.asList;
import static java.util.Arrays.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import util.collections.IdentityHashSet;
import util.iterator.ChainingIterator;
import util.iterator.IteratorUtilities;

import compiler.CHRIntermediateForm.CHRIntermediateForm;
import compiler.CHRIntermediateForm.Cost;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrderElement;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduled;
import compiler.CHRIntermediateForm.constraints.ud.schedule.ISelector;
import compiler.CHRIntermediateForm.constraints.ud.schedule.JoinOrder;
import compiler.CHRIntermediateForm.rulez.Guard;
import compiler.CHRIntermediateForm.rulez.Head;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.CHRIntermediateForm.variables.IActualVariable;
import compiler.CHRIntermediateForm.variables.NamelessVariable;
import compiler.CHRIntermediateForm.variables.Variable;
import compiler.analysis.AnalysisException;
import compiler.analysis.CifAnalysor;
import compiler.analysis.FunctionalDependencies;
import compiler.options.Options;

public class GreedyJoinOrderer extends CifAnalysor {
    
    public GreedyJoinOrderer(CHRIntermediateForm intermediateForm, Options options) {
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
    
    @Override
    protected void analyse(Rule rule) {
        analysePositive(rule.getPositiveHead());
        analyseNegative(rule.getNegativeHeads());
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
            analyse();
            finalize(head);
        }
    }
    
    protected void analysePositive(Occurrence active, Occurrence[] partners) {
        if (! active.isPassive()) {
            initialize(active, partners);
            analyse();
            finalize(active);
        }
    }
    
    private SortedSet<Variable> 
        fixedVariables = new TreeSet<Variable>();
    
    protected void initialize(Occurrence active, Occurrence[] partners) {
        initialize(active, partners, getSelectors(active.getRule()));
    }
    
    protected void initialize(NegativeHead head) {
        initialize(head, head.getOccurrencesArray(), head.getGuard());
    }
    
    protected <S extends ISelector> void initialize(
        IScheduled scheduled, Occurrence[] toLookup, Iterable<S> allSelectors
    ) {
        Set<Variable> initiallyFixed = scheduled.getInitiallyKnownVariables();

        fixedVariables.clear();
        fixedVariables.addAll(initiallyFixed);

        joinOrder = new JoinOrder();
        sum.reset();
        score.reset();
        
        // first add all guards to the ordering that can be added already:
        List<S> remainingSelectors = new ArrayList<S>();
        selectors: for (S selector : new FilteredSelectors<S>(allSelectors)) {
            for (Variable variable : selector.getJoinOrderPrecondition())
                if (!initiallyFixed.contains(variable)) {
                    remainingSelectors.add(selector);                    
                    continue selectors;
                }
            addToJoinOrder(selector);
        }
        
        Set<Variable> alsoFixed = new HashSet<Variable>();
        
        for (Occurrence occurrence : toLookup) {
        	boolean[] fixed = new boolean[occurrence.getArity()];
            for (int i = 0; i < fixed.length; i++) {
            	IActualVariable variable = occurrence.getArgumentAt(i);
            	if (variable == NamelessVariable.getInstance()) continue;
            	if (!(fixed[i] = initiallyFixed.contains(variable)))
            		alsoFixed.add((Variable)variable);
            }
            
            int nbAlsoFixed = alsoFixed.size();
            
            // determine the selectors that could be ordered after the occurrence:
            SelectorSet certains = new SelectorSet();
            SelectorMap possibles = new SelectorMap();
            
            for (S selector : remainingSelectors) {
                boolean possible = false;
                int unfixed = 0;
                for (Variable variable : selector.getJoinOrderPrecondition()) {
                    if (! initiallyFixed.contains(variable)) {
                        unfixed++;
                        possible |= alsoFixed.contains(variable); 
                    }
                }
                
                if (unfixed == 0)
                    certains.add(selector);
                else if (possible)
                    possibles.put(selector, unfixed);
            }
            
            // calculate the cost of scheduling the lookup of the occurrence:
            SCost cost = new SCost(
                nbAlsoFixed, 
                nbAlsoFixed - occurrence.getNbVariables(),
                certains.getSelectivity()
            );

            // we now have all we need:
            new ToOrder(
        		occurrence,
        		fixed,
        		certains, 
        		possibles, 
        		cost, 
        		unordered
            );
            
            alsoFixed.clear();
        }
        
        unordered.incorporateFunctionalDependencies();
    }
    
    protected void finalize(Occurrence active) {
        active.changeJoinOrder(getJoinOrder());
    }
    
    protected void finalize(NegativeHead negativeHead) {
        negativeHead.changeJoinOrder(joinOrder);
    }
    
    protected void scheduleVariablelessGuards(Guard guard) {
        for (IGuardConjunct guardConjunct : guard)
            if (guardConjunct.getNbVariables() == 0)
                joinOrder.addElement(guardConjunct);
    }
    
    protected void analyse() {
        while (unordered.next != null) {
            ToOrder min = unordered.next; /* ~> min != null */
            
            ToOrder toSchedule = min.next;
            while (toSchedule != null) {
                // equal is also preferred, because this came *earlier* in the head
                if (toSchedule.compareTo(min) <= 0)
                    min = toSchedule;
                toSchedule = toSchedule.next;
            }
            
            addToJoinOrder(min);
        }
    }
    
    private JoinCost 
        sum = new JoinCost(), 
        score = new JoinCost();
    
    protected JoinCost getScore() {
        return score;
    }

    private JoinOrder joinOrder = new JoinOrder();
    
    public JoinOrder getJoinOrder() {
        return joinOrder;
    }
    
    protected void addToJoinOrder(IJoinOrderElement element) {
        joinOrder.addElement(element);
    }
    protected void addToJoinOrder(Collection<ISelector> selectors) {
    	ISelector[] array = selectors.toArray(new ISelector[selectors.size()]);
    	sort(array, new Comparator<ISelector>() {
			public int compare(ISelector s1, ISelector s2) {
				Cost c1 = s1.getSelectionCost(), c2 = s2.getSelectionCost();
				if (c1.compareTo(Cost.VERY_CHEAP) <= 0) {
					if (c2.compareTo(Cost.VERY_CHEAP) > 0)
						return -1;
				} else {
					if (c2.compareTo(Cost.VERY_CHEAP) <= 0)
						return +1;
				}
				return (int)signum(s2.getSelectivity() - s1.getSelectivity());
			}
		});
    	joinOrder.addElements(asList(array));
    }
    
    private ToOrder unordered = new ToOrder(); 
    
    protected void addToJoinOrder(ToOrder min) {
        // remove from the list
        if ((min.previous.next = min.next) != null)
            min.next.previous = min.previous;

        Set<Variable> variables = min.occurrence.getVariables();
        ArrayList<Variable> alsoFixed 
            = new ArrayList<Variable>(variables.size());
        boolean atLeastOneExplicitSelector = false;
        
        for (Variable variable : variables) {
            if (variable.isImplicit()) {
                min.checkPossibles(variable);
            } else if (fixedVariables.add(variable)) {
                atLeastOneExplicitSelector = true;
                min.checkPossibles(variable);
                alsoFixed.add(variable);
            }
        }
        
        sum.add(min.cost);
        score.add(sum);

        addToJoinOrder(min.occurrence);
        addToJoinOrder(min.certains);
        
        if (atLeastOneExplicitSelector)
            unordered.removeSelectorsOf(min.certains);
        unordered.fixVariables(alsoFixed);
    }
    
    protected void printUnordered() {
        ToOrder toOrder = this.unordered;
        while (toOrder != null) {
            System.out.print(toOrder);
            toOrder = toOrder.next;
        }
        System.out.println();
    }
    
    protected static class ToOrder implements Comparable<ToOrder> {
        public final Occurrence occurrence;
        public final boolean[] fixed;
        public final SortedSet<Variable> fixedByDependency;
        public final SelectorSet certains;
        public final SelectorMap possibles;
        public final SCost cost;
        
        public ToOrder next;
        public ToOrder previous;
        
        public ToOrder() {
            this.occurrence = null;
            this.fixed = null;
            this.fixedByDependency = null;
            this.certains = null;
            this.possibles = null;
            this.cost = null;
        }
        
        public ToOrder(
            Occurrence occurrence,
            boolean[] fixed,
            SelectorSet selectors, 
            SelectorMap possibles, 
            SCost cost
        ) {
            this.occurrence = occurrence;
            this.fixed = fixed;
            this.fixedByDependency = new TreeSet<Variable>();
            this.certains = selectors;
            this.possibles = possibles;
            this.cost = cost;
        }
        
        public ToOrder(
            Occurrence occurrence,
            boolean[] fixed,
            SelectorSet selectors,
            SelectorMap possibles,
            SCost cost, 
            ToOrder previous
        ) {
            this(occurrence, fixed, selectors, possibles, cost);
            
            this.previous = previous;
            if ((this.next = previous.next) != null)
                next.previous = this;
            previous.next = this;
        }
        
        public int getArity() {
        	return occurrence.getArity();
        }
        public IActualVariable getArgumentAt(int i) {
        	return occurrence.getArgumentAt(i);
        }
        public FunctionalDependencies getFunctionalDependencies() {
        	return occurrence.getConstraint().getFunctionalDependencies();
        }
        
        public void removeSelectorsOf(SelectorSet selectors) {
            if (previous != null) { // i.e.: we are not in the empty head of the list
                float selectivity = 0;
                
                for (ISelector selector : selectors) {
                    if (this.certains.remove(selector))
                        selectivity += selector.getSelectivity();
                    possibles.remove(selector);
                }
                
                if (selectivity != 0) cost.subtract(selectivity);
                
            } else {    // don't propagate to actual list in trivial cases:
                if (selectors.isEmpty()) return;
            }
            if (next != null) {
                next.removeSelectorsOf(selectors);
            }
        }
        
        private void doIncorporateFunctionalDependencies() {
        	int numAlsoFixed = 0;
        	int[] indexes = getFunctionalDependencies().getDependents(fixed);
    		for (int i = 0; i < indexes.length; i++) {
    			IActualVariable variable = getArgumentAt(indexes[i]);
    			if (variable == NamelessVariable.getInstance()) continue;
    			if (fixedByDependency.add((Variable)variable))
    				numAlsoFixed++;
    			checkPossibles((Variable)variable);
    		}
    		if (numAlsoFixed != 0)
    			cost.subtract(numAlsoFixed,numAlsoFixed);
        }
        
        public void incorporateFunctionalDependencies() {
        	if (previous != null) doIncorporateFunctionalDependencies();
        	if (next != null) next.incorporateFunctionalDependencies();
        }
        
        public void fixVariables(Collection<Variable> alsoFixed) {
            if (previous == null) { // i.e.: we are in the (empty) head of the list
                if (alsoFixed.isEmpty()) return;
                
            } else {
                int numAlsoFixed = 0;
                for (Variable variable : alsoFixed) {
                	if (fixedByDependency.contains(variable)) continue;
                    if (occurrence.getVariables().contains(variable))
                        numAlsoFixed++;
                    checkPossibles(variable);
                }
                
                // some less unfixed, some more fixed
                if (numAlsoFixed != 0) {
                	cost.subtract(numAlsoFixed,numAlsoFixed);
                	
                	int arity = getArity();
                	for (int i = 0; i < arity; i++) 
                		if (!fixed[i]) fixed[i] = alsoFixed.contains(getArgumentAt(i));
                	doIncorporateFunctionalDependencies();
                }
            }
            
            if (next != null)
                next.fixVariables(alsoFixed);
        }
        
        public void checkPossibles(Variable variable) {
            Iterator<Entry<ISelector, Integer>> iterator 
                                           = possibles.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<ISelector, Integer> possible = iterator.next();
                ISelector selector = possible.getKey();
                if (selector.getJoinOrderPrecondition().contains(variable)) {
                    int unfixed = possible.getValue();
                    if (unfixed == 1) {
                        iterator.remove();
                        certains.add(selector);
                        cost.add(selector.getSelectivity());
                    } else {
                        possible.setValue(unfixed - 1);
                    }
                }
            }
        }
        
        public boolean hasSetSemantics() {
            return occurrence.getMultisetInfo().isSet();
        }
        
        public int compareTo(ToOrder other) {
            int result = this.cost.compareTo(other.cost);
            if (result == 0) {
                boolean x = this.hasSetSemantics();
                boolean y = other.hasSetSemantics();
                return x? (y? 0 : +1) : (y? -1 : 0);
                
            } else {
                return result;
            }
        }
        
        @Override
        public String toString() {
            return '{'  + String.valueOf(occurrence) 
                + ", !" + String.valueOf(certains) 
                + ", ?" + String.valueOf(possibles) 
                + ", @" + String.valueOf(cost)
                + '}';
        }
    }
    
    protected static class SelectorSet extends IdentityHashSet<ISelector> {
        private static final long serialVersionUID = 1L;

        public float getSelectivity() {
            float selectivity = 0;
            for (ISelector selector : this)
                selectivity += selector.getSelectivity();
            return selectivity;
        }
    }
    
    protected static class SelectorMap extends IdentityHashMap<ISelector, Integer> {
        private static final long serialVersionUID = 1L;

        public float getSelectivity() {
            float selectivity = 0;
            for (ISelector selector : keySet())
                selectivity += selector.getSelectivity();
            return selectivity;
        }
    }
    
    protected static Iterable<? extends ISelector> getSelectors(final Rule rule) {
        return new Iterable<ISelector>() {
            @SuppressWarnings("unchecked")
            public Iterator<ISelector> iterator() {
                return new ChainingIterator<ISelector>(
                    rule.getPositiveGuard().iterator(),
                    rule.getNegativeHeads().iterator()
                );
            }
            
            @Override
            public String toString() {
                return IteratorUtilities.deepToString(this);
            }
        }; 
    }
}
