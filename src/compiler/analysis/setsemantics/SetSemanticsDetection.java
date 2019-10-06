package compiler.analysis.setsemantics;

import static compiler.CHRIntermediateForm.constraints.ud.MultisetInfo.SET;
import static compiler.CHRIntermediateForm.constraints.ud.Occurrence.getOnlyPartner;
import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.REMOVED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import runtime.BooleanAnswer;
import runtime.PrimitiveAnswerSolver;
import util.Resettable;

import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumented.IArgumented;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.conjuncts.AbstractGuardConjunctVisitor;
import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunctVisitor;
import compiler.CHRIntermediateForm.constraints.bi.Failure;
import compiler.CHRIntermediateForm.constraints.bi.IBuiltInConjunct;
import compiler.CHRIntermediateForm.constraints.java.AssignmentConjunct;
import compiler.CHRIntermediateForm.constraints.java.NoSolverConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.members.ConstructorInvocation;
import compiler.CHRIntermediateForm.members.FieldAccess;
import compiler.CHRIntermediateForm.members.MethodInvocation;
import compiler.CHRIntermediateForm.rulez.Head;
import compiler.CHRIntermediateForm.rulez.PositiveHead;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.CHRIntermediateForm.variables.IActualVariable;
import compiler.CHRIntermediateForm.variables.IVariable;
import compiler.CHRIntermediateForm.variables.NamelessVariable;
import compiler.CHRIntermediateForm.variables.Variable;
import compiler.analysis.AnalysisException;
import compiler.analysis.EqHandler;
import compiler.analysis.FunctionalDependency;
import compiler.analysis.RulesRemover;
import compiler.options.Options;

/**
 * <p>
 * Detects two properties of constraints: 
 * 	<em>set semantics</em> and <em>idempotence</em>.
 * (XXX idempotence is turned off: will be for version 1.6.1)
 * </p><p> 
 * Set semantics constraints are constraints for which the store never
 * contains two syntactically identical instances.
 * </p><p>
 * There is however a caveat if constraint arguments are not fixed.
 * Consider for instance the <code>leq</code> program, with the following query: 
 * <pre>leq(A,B), leq(A,C), B=C</pre>
 * In this case, both <code>leq</code> constraints are stored, 
 * and by unifying <code>B</code> and <code>C</code>, 
 * two syntactically identical constraints suddenly become present in
 * the constraint store.
 * So I suppose that there may be a problem
 * iff the set semantics occurrence is reactive.
 * </p>
 * <p>
 * If a set semantics rehashing hash index is used, the problem could be solved 
 * at &quot;rehash&quot; time, by simply throwing away the constraint that 
 * rehashes on a syntactically identical instance.
 * Is this always correct with respect to the refined operational semantics?
 * To know this, we have to look at the reactive occurrences prior to
 * the set semantics one (recall that the latter is reactive).
 * If there is such an occurrence whose corresponding body is not idempotent 
 * (idempotence = has no effect if executed more than once; 
 * built-in constraints e.g. will commonly be idempotent),
 * this body may not be executed sufficiently many times.
 * Also, if such an occurrence is removed that removes other partner constraints,
 * too few constraints may be removed, unless these partners all have set semantics.
 * </p><p>
 * In other cases, either the identical constraint already fired the
 * rule (removing the partners, and firing the idempotent body), or
 * the rule is about to be fired using the identical constraint due to
 * the same reactivation series.
 * Watch out: <em>those constraints have to be reactivated!</em>
 * So make sure in the built-in constraint stores that 
 * <em>the constraints that are rehashed and those that are reactivated
 * are not the same!</em>
 * </p><p>
 * What about other indices? If no rehasing occurs,
 * identical constraints will simply become stored more than once.
 * Of course, at least one will become reactivated shortly after, 
 * and the set semantics rule will take care of the doubles.
 * If idempotent rules are fired prior to the set semantics occurrence,
 * this poses no problem.
 * </p>
 * <p>
 * A constraint is <em>idempotent</em> if telling it a second time or more 
 * with equal arguments has no observable effect.
 * Obviously, only set semantics constraints can be idempotent. 
 * 
 * @author Peter Van Weert
 */
public class SetSemanticsDetection extends RulesRemover implements Resettable {

    public SetSemanticsDetection(ICHRIntermediateForm cif, Options options) {
        super(cif, options);
        PASSIVE = new boolean[getNbRules()];
        SS_RULES = new HashMap<UserDefinedConstraint, Rule>(getNbUdConstraints());
        WATCHEES = new HashSet<UserDefinedConstraint>(getNbUdConstraints());
    }

    @Override
    public boolean doAnalysis() throws AnalysisException {
        if (getNbDetected() != 0) reset();
        analyseConstraints();
        if (!printResult()) return false;
        analyseRules();
        super.doAnalysis();
        return true;
    }
    
    @Override
    protected void analyseConstraints() throws AnalysisException {
    	do {
    		resetHaveToRepeat();
    		super.analyseConstraints();
    	} while ( haveToRepeat() );
    }
    
    @Override
    protected void analyse(UserDefinedConstraint constraint) {
        if (!constraint.mayBeStored()) return;
        if (constraint.getMultisetInfo() == SET) return;
        
        for (Occurrence occurrence : constraint.getPositiveOccurrences()) {
            if (occurrence.isPassive()) continue;
            if (occurrence.isStored()) return;
            
            FunctionalDependency functionalDependency = 
            				isSetSemanticOccurrence(occurrence);
            if (functionalDependency != null) {
                SS_RULES.put(constraint, occurrence.getRule());
                setSetSemantics(constraint);
                constraint.addFunctionalDependency(functionalDependency);
                return;
            }
        }
    }
    
    @Override
    protected void analyseRules() throws AnalysisException {
        Rule firstSSRule = Collections.min(SS_RULES.values());
        Iterator<Rule> iter = getRules().iterator();
        while (iter.next() != firstSSRule);
        while (iter.hasNext()) analyse(iter.next());
    }
    
    @Override
    protected void analyse(Rule rule) throws AnalysisException {
        try {
            // collect candidate couples
            Occurrence[] occurrences = rule.getPositiveHead().getOccurrencesArray();
            List<Occurrence> candidates = new ArrayList<Occurrence>(occurrences.length);
            for (int i = 0; i < occurrences.length-1; i++) {
                UserDefinedConstraint constraint = occurrences[i].getConstraint();
                if (constraint.getMultisetInfo() != SET 
                        || rule.compareTo(SS_RULES.get(constraint)) <= 0
                        || occurrences[i].getNbVariables() != constraint.getArity()
                    ) continue;
                
                for  (int j = i+1; j < occurrences.length; j++) {
                    if (occurrences[j].getConstraint() == constraint
                            && occurrences[i].getNbVariables() == constraint.getArity()) {
                        
                        if (constraint.getArity() == 0) {
                            setPassive(rule, constraint);
                            return;
                        }
                        
                        candidates.add(occurrences[i]);
                        candidates.add(occurrences[j]);
                    }
                }
            }
            
            if (candidates.isEmpty()) return;
        
            // figure out which arguments are equal:
            EqHandler handler = new EqHandler(new PrimitiveAnswerSolver());        
            rule.getPositiveGuard().accept(new EqTeller(handler));
            
            // with this info, determine which candidates are in fact equal:
            BooleanAnswer result = new BooleanAnswer();
            Iterator<Occurrence> iter = candidates.iterator();
            iter: while (iter.hasNext()) {
                Occurrence occurrence = iter.next();
                IArguments candidate1 = occurrence.getArguments(),
                           candidate2 = iter.next().getArguments();
                
                for (int i = 0; i < candidate1.getArity(); i++) {
                    result.reset();
                    handler.tellAsk(
                        candidate1.getArgumentAt(i),
                        candidate2.getArgumentAt(i), 
                        result
                    );
                    if (!result.getValue()) continue iter;
                }
                
                setPassive(rule, occurrence.getConstraint());
            }
            
        } catch (Exception x) {
            throw new AnalysisException(x);
        }
    }
    
    public final static class EqTeller extends AbstractGuardConjunctVisitor {
        protected final EqHandler handler;
        
        public EqTeller(EqHandler handler) {
            this.handler = handler;
        }
        
        @Override
        protected void visit(IConjunct conjunct) throws Exception {
            // NOP
        }
        
        protected <T extends IGuardConjunct & IArgumented<?>> void visitPossibleEquality(T conjunct) {
            if ( /* only coreflexive constraints */
                    (conjunct instanceof IBuiltInConjunct<?>)
                        && ((IBuiltInConjunct<?>)conjunct).getConstraint().isCoreflexive()
            ) {
                IArgument arg1 = conjunct.getExplicitArgumentAt(0),
                          arg2 = conjunct.getExplicitArgumentAt(1);
                boolean var1 = (arg1 instanceof IVariable),
                        var2 = (arg2 instanceof IVariable);
                if (
                    var1
                        ? (var2 || arg2.isConstant())
                        : (var2 && arg1.isConstant())
                ) // then
                    handler.tellEq(arg1, arg2);
            }
        }
        
        @Override
        public void visit(NoSolverConjunct conjunct) {
            visitPossibleEquality(conjunct);
        }
        @Override
        public void visit(MethodInvocation<?> conjunct) {
            visitPossibleEquality(conjunct);
        }
    }
    
    protected final Map<UserDefinedConstraint, Rule> SS_RULES;
    
    protected void setPassive(Rule rule, UserDefinedConstraint cause) {
        PASSIVE[rule.getNbr()-1] = true;
        setReason(cause.getIdentifier());
    }
    
    protected final boolean[] PASSIVE;
    
    @Override
    protected boolean isPassive(Rule rule) {
        return PASSIVE[rule.getNbr()-1];
    }
    
    @Override
    protected String getReason() {
        return super.getReason() + " has set semantics";
    }
    
    /**
     * Checks whether the given occurrence is known to be 
     * a &quot;set semantics occurrence&quot;. If it is not known
     * (this does not necessarily mean it is does not enforce 
     * set semantics), the result will be <code>null</code>.
     * Otherwise, the result returns the 
     * {@link CompleteFunctionalDependency} responsible for 
     * the set semantics.
     * 
     * @param occurrence
     *  The occurrence to check.
     * @return The {@link CompleteFunctionalDependency}
     * 	responsible for the set semantics if the analysor can prove the
     *  rule to be a set semantics rule; <code>null</code> otherwise.
     */
    public FunctionalDependency isSetSemanticOccurrence(Occurrence occurrence) {
        Rule rule = occurrence.getRule();
        if (rule.hasSelectiveNegativeHeads()) return null;
        PositiveHead head = rule.getPositiveHead();
        if (head.getNbOccurrences() != 2) return null;
        
        if (occurrence.getType() != REMOVED) {
        	// some special considerations for passives added by the user:
        	// 		(e.g.:	c(X) \ c(X)#passive <=> true.	)
        	Occurrence partner = getOnlyPartner(occurrence);
        	if (partner.isPassive())
        		occurrence = partner;
        	else
        		return null;
        }
        
        FunctionalDependency result = matchIdentical(head, 0, 1); 
        if (result == null) return null;
        
        boolean idempotent = !hasNonIdempotentPredecessors(occurrence, false);
        if (occurrence.isReactive()
        		&& !idempotent 
    			&& hasNonIdempotentPredecessors(occurrence, true)
			) return null;
        
        if (idempotent) setIdempotent(occurrence.getConstraint());

        return result;
    }
    
    protected static class SetSemanticsFunctionalDependency extends FunctionalDependency {
    	
    	public SetSemanticsFunctionalDependency(UserDefinedConstraint constraint) {
			super(constraint);
		}
    	
    	private int[] det = new int[0];
    	
    	public void addDeterminant(int i) {
			int oldDet[] = det, l = oldDet.length, newDet[] = new int[l+1];
			int j = 0;
			while (j < l && oldDet[j] <= i) 
				if ((newDet[j] = oldDet[j++]) == i) return;
			newDet[j] = i;
			while (j < l) newDet[j+1] = oldDet[j++];
			det = newDet;
    	}
    	
    	@Override
		public int[] getDeterminantSet() {
    		int[] copy = new int[det.length];
            System.arraycopy(det,0,copy,0,det.length);
    		return copy;
    	}
    	
    	@Override
    	public int getNbDeterminants() {
    		return det.length;
    	}
    	
    	@Override
    	public int[] getDependentSet() {
    		int[] result = new int[getArity() - det.length];
    		int j = 0, k = 0;
    		for (int i = 0; i < getArity(); i++) {
    			if (j >= det.length || det[j] != i)
    				result[k++] = i;
    			else
    				j++;
    		}
    		return result;
    	}
    	
    	@Override
    	public int getNbDependents() {
    		return getArity() - getNbDeterminants();
    	}
    	
    	@Override
    	public boolean isComplete() {
    		return true;
    	}
    }
    
    protected boolean hasNonIdempotentPredecessors(Occurrence occurrence, boolean onlyReactive) {
    	UserDefinedConstraint constraint = occurrence.getConstraint(); 
    	if (constraint.canNeverBeReactived()) return false;
    	for (Occurrence predecessor : constraint.getPositiveOccurrences()) {
    		if (predecessor == occurrence) return false;
    		if (!onlyReactive || predecessor.isReactive()) {
    			if (!predecessor.getBody().isIdempotent())
    				return true;
    			if (predecessor.getType() == REMOVED 
    						&& removesNonUniquePartner(predecessor))
    				return true;
    		}
    	}
    	throw new InternalError();
    }
    
    protected boolean removesNonUniquePartner(Occurrence occurrence) {
    	for (Occurrence partner : occurrence.getPartners())
    		if (partner.getType() == REMOVED && !isUnique(partner, occurrence.getConstraint())) 
    			return true;
    	return false;
    }
    
    protected boolean isUnique(Occurrence occurrence, UserDefinedConstraint assumedSet) {
    	UserDefinedConstraint constraint = occurrence.getConstraint();
    	if (constraint != assumedSet && constraint.getMultisetInfo() != SET) {
    		watch(constraint);
    		return false;
    	}
    	
    	SortedSet<Variable> variables = new TreeSet<Variable>();
    	for (Variable variable : occurrence.getVariables())
    		if (!variable.isImplicit()) variables.add(variable);
    	
    	for (Occurrence partner : occurrence.getPartners())
    		for (Variable variable : partner.getVariables())
        		if (!variable.isImplicit()) variables.remove(variable);
    	
    	return variables.isEmpty();
    }
    
    private final Set<UserDefinedConstraint> WATCHEES;
    private boolean haveToRepeat;
    
    protected void unwatch(UserDefinedConstraint constraint) {
    	WATCHEES.remove(constraint);
    }
    protected void watch(UserDefinedConstraint constraint) {
    	WATCHEES.add(constraint);
    }
    
    protected void setSetSemantics(UserDefinedConstraint constraint) {
    	constraint.setMultisetInfo(SET);
        incNbSetSemantics();
        if (WATCHEES.contains(constraint)) setHaveToRepeat(true);
    }
    
    protected void setHaveToRepeat(boolean haveToRepeat) {
		this.haveToRepeat = haveToRepeat;
	}    
    protected boolean haveToRepeat() {
    	return haveToRepeat;
    }
    
    protected static FunctionalDependency matchIdentical(Occurrence one, Occurrence other) {
        return matchIdentical(one.getHead(), one, other);
    }
    protected static FunctionalDependency matchIdentical(PositiveHead head, int one, int other) {
        return matchIdentical(head, head.getOccurrenceAt(one), head.getOccurrenceAt(other));
    }
    
    protected static FunctionalDependency matchIdentical(Head head, Occurrence one, Occurrence other) {
        if (one.getConstraint() != other.getConstraint()
            // no intra-constraint implicit guards:
            || one.hasIntraConstraintImplicitGuards() || other.hasIntraConstraintImplicitGuards()) 
                return null;
        
        SetSemanticsFunctionalDependency result = 
        	new SetSemanticsFunctionalDependency(one.getConstraint());
        
        // check the inter-constraint implicit guards:
        int arity = one.getArity();
        for (int i = 0; i < arity; i++) {
            IActualVariable var = one.getArgumentAt(i);
            if (var == NamelessVariable.getInstance()) continue;
            int index = other.getIndexOf(var);
            if (index >= 0) 
            	if (index != i) 
            		return null;
            	else
            		result.addDeterminant(i);
        }
        
        // check the explicit guards:
        try {
            GuardChecker checker = new GuardChecker(result, one, other);
            head.getGuard().accept(checker);
            return checker.getResult();
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected final static class GuardChecker implements IGuardConjunctVisitor {
        private SetSemanticsFunctionalDependency result;
        
        private Occurrence one, other;
        
        public GuardChecker(SetSemanticsFunctionalDependency result, Occurrence one, Occurrence other) {
            this.result = result;
        	this.one = one;
            this.other = other;
        }
        
        protected <T extends IGuardConjunct & IArgumented<?>> void visitPossibleEquality(T conjunct) {
            if (result == null || conjunct.succeeds()) return;
            
            if (!( /* only reflexive constraints */
                (conjunct instanceof IBuiltInConjunct<?>)
                    && ((IBuiltInConjunct<?>)conjunct).getConstraint().isReflexive()
            )) {
                result = null;
                return;
            }
            
            // reflexivity implies a binary constraint: 
            IArgument 
            	arg1 = conjunct.getExplicitArgumentAt(0),
                arg2 = conjunct.getExplicitArgumentAt(1);
            
            int index = one.getIndexOf(arg1);
            if (index < 0) {	
            	// <arg1> is no variable, or a variable not occurring in <one>
                if ((index = other.getIndexOf(arg1)) >= 0)
                	if (one.getIndexOf(arg2) == index)
                		result.addDeterminant(index);
                	else
                		result = null;
            } else { 
            	// <arg1> is a variable, and must thus occur at the same index
                if (other.getIndexOf(arg2) == index)
                	result.addDeterminant(index);
                else
                	result = null;
            }
        }
        
        public void visit(NoSolverConjunct conjunct) {
            visitPossibleEquality(conjunct);
        }
        public void visit(MethodInvocation<?> conjunct) {
            visitPossibleEquality(conjunct);
        }
        
        public void visit(ConstructorInvocation conjunct) {
            result = null;
        }
        public void visit(Failure conjunct) {
            result = null;
        }
        public void visit(FieldAccess conjunct) {
            result = null;
        }
        public void visit(AssignmentConjunct conjunct) {
            result = null;
        }
        public void visit(Variable conjunct) {
            result = null;
        }
        
        public SetSemanticsFunctionalDependency getResult() {
            return result;
        }
    }
    
    
    protected void setIdempotent(UserDefinedConstraint constraint) {
    	/* XXX will be released in version 1.6.1 */
//    	if (!constraint.isIdempotent()) {
//    		constraint.setIdempotent();
//    		incNbIdempotent();
//    	}
    }
    
    
    public int getNbDetected() {
    	return getNbSetSemantics() + getNbIdempotent();
    }
    
    private int nbSetSemantics;
    
    public int getNbSetSemantics() {
        return nbSetSemantics;
    }
    public void incNbSetSemantics() {
        nbSetSemantics++;
    }
    
    private int nbIdempotent;
    
    public int getNbIdempotent() {
        return nbIdempotent;
    }
    public void incNbIdempotent() {
        nbIdempotent++;
    }
    
    protected boolean printResult() {
    	boolean result = true;
    	
    	int nbDetected = getNbSetSemantics(); 
        switch (nbDetected) {
            case 0: result = false; break;
            case 1: 
                System.out.println(" --> optimization: detected one set semantics constraint");
            break;
            default:
                System.out.printf(" --> optimization: detected %d set semantics constraints%n", nbDetected);
        }
        
        nbDetected = getNbIdempotent(); 
        switch (nbDetected) {
            case 0: return result;
            case 1: 
                System.out.println(" --> optimization: detected one idempotent constraint");
            break;
            default:
                System.out.printf(" --> optimization: detected %d idempotent constraints%n", nbDetected);
        }
        
        return true;
    }
    
    protected void resetHaveToRepeat() {
    	setHaveToRepeat(false);
        WATCHEES.clear();
    }
    
    public void reset() {
        nbSetSemantics = 0;
        nbIdempotent = 0;
        SS_RULES.clear();
        Arrays.fill(PASSIVE, false);
    }
}