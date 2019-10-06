package compiler.CHRIntermediateForm.constraints.ud;

import static compiler.CHRIntermediateForm.constraints.ud.schedule.LookupsGetter.getNonSingletonLookupsOf;

import java.util.AbstractList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import util.Terminatable;
import util.exceptions.IllegalArgumentException;
import util.iterator.CastingListIterator;
import util.iterator.ChainingIterator;
import util.iterator.FilteredIterable;
import util.iterator.FilteredIterator;
import util.iterator.Filtered.Filter;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumented.IBasicArgumented;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.visitor.VariableScanner;
import compiler.CHRIntermediateForm.conjuncts.ArgumentedConjunctVisitor;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitable;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitor;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.java.AssignmentConjunct;
import compiler.CHRIntermediateForm.constraints.ud.lookup.Lookup;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrder;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrderElement;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrderVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleElements;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduled;
import compiler.CHRIntermediateForm.constraints.ud.schedule.ISelector;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IVariableInfoQueue;
import compiler.CHRIntermediateForm.constraints.ud.schedule.Schedule;
import compiler.CHRIntermediateForm.exceptions.ToDoException;
import compiler.CHRIntermediateForm.rulez.Body;
import compiler.CHRIntermediateForm.rulez.Head;
import compiler.CHRIntermediateForm.rulez.IOccurrenceVisitable;
import compiler.CHRIntermediateForm.rulez.IOccurrenceVisitor;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.CHRIntermediateForm.variables.FormalVariable;
import compiler.CHRIntermediateForm.variables.IActualVariable;
import compiler.CHRIntermediateForm.variables.NamelessVariable;
import compiler.CHRIntermediateForm.variables.Variable;
import compiler.CHRIntermediateForm.variables.VariableType;
import compiler.analysis.FunctionalDependencies;

/**
 * @author Peter Van Weert
 */
public class Occurrence extends UserDefinedConjunct 
implements IScheduled, IJoinOrderElement, 
    Comparable<Occurrence>, IOccurrenceVisitable,
    Terminatable {
    
    private Head head;
    
    private OccurrenceType type;
    
    /**
     * Stores the set of unique variables that are argument
     * of this occurrence. This means that if a variable
     * <i>X</i> appears more then once as an argument
     * (meaning that at least all but one are implicit 
     * variables), it will only appear once in this set.
     */
    private SortedSet<Variable> variables;
    
    /**
     * The schedule for this occurrence. Note that this is 
     * created lazily: no need e.g. to create schedules for 
     * occurrences that turn out to be passive.
     */
    private Schedule schedule;
    
    /**
     * Is the occurrence passive or not? A passive occurrence is an
     * occurrence that can never become active (either by adding
     * a constraint to the store, or by reactivating as the result
     * of a triggering). 
     */    
    private boolean passive;
    
    Occurrence(Head head, UserDefinedConstraint constraint, /*IArguments args,*/ OccurrenceType type) {        
        super(constraint, new Arguments(constraint.getArity()) /*args*/);
        
        // IMPORTANT: type has to be set prior to addition to the head
        if (type == null)
            throw new IllegalArgumentException("The occurrence type is not specified");
        setType(type);
        
        setHead(head);
        head.addOccurrence(this);
        
        setVariables(new TreeSet<Variable>());
        
        // IMPORTANT: head has to be known prior to addition to the constraint
        getConstraint().addOccurrence(this);   
    }
    
    /**
     * Returns the rule of which this occurrence is a conjunct of
     * one of the heads.  
     * 
     * @return The rule of which this occurrence is a conjunct of
     *  one of the heads.
     */
    public Rule getRule() {
        return getHead().getRule();
    }
    
    public StorageInfo getStorageInfo() {
        return getConstraint().getStorageInfo();
    }
    public MultisetInfo getMultisetInfo() {
        return getConstraint().getMultisetInfo();
    }
    
    /**
     * Returns the head of which this occurrence is a conjunct.
     * 
     * @return The head of which this occurrence is a conjunct.
     */
    public Head getHead() {
        return head;
    }
    protected void setHead(Head head) {
        this.head = head;
    }
    
    public Body getBody() {
    	return getRule().getBody();
    }
    
    public Iterable<Occurrence> getPartners() {
        return getPartners((Filter<Occurrence>)null);
    }
    public Iterable<Occurrence> getPartners(final Filter<Occurrence> filter) {
        return new FilteredIterable<Occurrence>(getHead().getOccurrences(), 
            new Filter<Occurrence>() {
                @Override
                public boolean exclude(Occurrence occurrence) {
                    return occurrence == Occurrence.this
                        || (filter != null && filter.exclude(occurrence));
                }
                @Override
                public boolean include(Occurrence occurrence) {
                    return occurrence != Occurrence.this
                        && (filter == null || filter.include(occurrence));
                }
            }
        );
    }
    
    public Occurrence getPartnerAt(int index) {
        if (index == getOccurrenceIndex())
            throw new IllegalArgumentException("not partner index");
        return getHead().getOccurrenceAt(index);
    }
    
    public boolean hasPartners(Filter<Occurrence> filter) {
    	return getPartners(filter).iterator().hasNext();
    }
    
    public boolean hasPartners() {
        return getNbPartners() != 0;
    }
    public int getNbPartners() {
        return getHead().getNbOccurrences() - 1;
    }
    
    public OccurrenceType getType() {
        return type;
    }
    protected void setType(OccurrenceType type) {
        this.type = type;
    }
    
    public boolean looksUpNonSingletonPartners() {
        return !getNonSingletonLookups().isEmpty();
    }
    public List<Lookup> getNonSingletonLookups() {
    	return getNonSingletonLookupsOf(getSchedule());
    }
    
    public Occurrence getNextActiveOccurrence() {
    	List<Occurrence> occurrences = getConstraint().getOccurrencesRef(this);
    	Occurrence occurrence, result = null;
    	for (int i = occurrences.size()-1; i >= 0; i--)
    		if ((occurrence = occurrences.get(i)) == this)
    			return result;
    		else if (occurrence.isActive())
    			result = occurrence;
    	throw new InternalError();
    }
    public boolean isLastActiveOccurrence() {
    	List<Occurrence> occurrences = getConstraint().getOccurrencesRef(this);
    	Occurrence occurrence; boolean result = true;
    	for (int i = occurrences.size()-1; i >= 0; i--)
    		if ((occurrence = occurrences.get(i)) == this)
    			return result;
    		else if (result && occurrence.isActive())
    			result = false;
    	throw new InternalError();
    }
    
    /**
     * Returns the number of the rule this <code>Occurrence</code> is part of.
     * Rules are number top-to-bottom, starting from one.
     * 
     * @return The number of the rule this <code>Occurrence</code> is part of.
     */
    public int getRuleNbr() {
        return getRule().getNbr();
    }
    
    /**
     * Returns the index of the head this <code>Occurrence</code> is part of.
     * If it is the positive head, the number is zero. 
     * Negative heads are number left-to-right, starting with one. 
     * 
     * @return The index of the given head in this rule.
     */
    public int getHeadNbr() {
        return getRule().getHeadNbr(getHead());
    }
    
    public Iterable<Occurrence> getPreviousActivePositiveOccurrences() {
    	return new Iterable<Occurrence>() {
			public Iterator<Occurrence> iterator() {
				return new Iterator<Occurrence>() {
					private int next = 0;
					
					public Occurrence next() {
						if (!hasNext()) throw new NoSuchElementException();
						return getConstraint().getPositiveOccurrenceAt(next++);
					}
				
					public boolean hasNext() {
						while (next != getConstraintOccurrenceIndex()) {
							if (getConstraint().getPositiveOccurrenceAt(next).isActive())
								return true;
							else
								next++;
						}
						return false;
					}
				
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
    }
    public Iterable<Occurrence> getRemainingActivePositiveOccurrences() {
    	return new Iterable<Occurrence>() {
			public Iterator<Occurrence> iterator() {
				return new Iterator<Occurrence>() {
					private int next = getConstraintOccurrenceNbr();
					
					public Occurrence next() {
						if (!hasNext()) throw new NoSuchElementException();
						return getConstraint().getPositiveOccurrenceAt(next++);
					}
				
					public boolean hasNext() {
						while (next < getConstraint().getNbPositiveOccurrences()) {
							if (getConstraint().getPositiveOccurrenceAt(next).isActive())
								return true;
							else
								next++;
						}
						return false;
					}
				
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
    }
    
    /**
     * Returns the <em>number</em> of this occurrence in the head it appears in. 
     * The numbering is done left-to-right, starting with <em>one</em>.
     * This is equal to one more then the <em>index</em> of 
     * this occurrence.
     * 
     * @return The <em>number</em> of this occurrence in the head it 
     *  appears in. This is equal to one more then the <em>index</em> of 
     *  this occurrence.
     * 
     * @see #getOccurrenceIndex()
     */
    public int getOccurrenceNbr() {
        return getOccurrenceIndex() + 1;
    }
    /**
     * Returns the <em>index</em> of this occurrence in the head it appears in. The
     * numbering is done left-to-right, starting with <em>zero</em>.
     * 
     * @return The number of this occurrence in the head it appears in.
     * 
     * @see #getOccurrenceNbr()
     */
    public int getOccurrenceIndex() {
        return getHead().getOccurrenceIndex(this);
    }
    
    /**
     * Returns the occurrence <em>number</em> of this occurrence.
     * <ul>
     * <li> 
     * For positive occurrences, the occurrences are numbered top-to-bottom, 
     * right-to-left per user-defined constraint, starting from <em>one</em> 
     * (<em>incrementing</em>). This is equal to <em>on more then</em> 
     * the <em>index</em> of this occurrence.  
     * </li>
     * <li>
     * For negative occurrences the numbering is also top-to-bottom, 
     * right-to-left starting from -1 (<em>decrementing</em>). If
     * the <em>index</em> of this occurrence is <i>i</i>, the result
     * is equal to <i>(-i-1)</i>.
     * </li>
     * </ul>
     * 
     * @return The occurrence number of this occurrence.
     * 	This number is positive for positive occurrences, 
     * 	and negative for negative occurrences.
     */
    public int getConstraintOccurrenceNbr() {
        if (isPositive()) 
            return getConstraintOccurrenceIndex() + 1;
        else // isNegative()
            return -getConstraintOccurrenceIndex() - 1;
    }
    
    /**
     * Returns the occurrence <em>index</em> of this occurrence. 
     * For positive occurrences, the occurrences are numbered top-to-bottom, 
     * right-to-left per user-defined constraint, starting from 
     * <em>zero</em> (<em>incrementing</em>).
     * For negative occurrences the numbering is also top-to-bottom, 
     * right-to-left starting from <em>zero</em> (<em>incrementing</em>).
     * 
     * @return The occurrence index of this occurrence.
     */
    public int getConstraintOccurrenceIndex() {
        return getConstraint().getIndexOf(this);
    }
    
    /**
     * Checks whether this occurrence is a positive occurrence or not.
     * 
     * @return True iff this occurrence is a positive occurrence, i.e.
     *  if it occurs in the positive head of some rule.
     */
    public boolean isPositive() {
        return ! isNegative(); 
    }
    
    /**
     * Checks whether this occurrence is a negative occurrence or not.
     * 
     * @return True iff this occurrence is a negative occurrence, i.e.
     *  if it occurs in a negative head of some rule.
     */
    public boolean isNegative() {
        return getType() == OccurrenceType.NEGATIVE;
    }

    public Schedule getSchedule() {
        if (isPassive()) throw new IllegalStateException(
            "no schedule (passive occurrence)"
        );
        
        if (schedule == null) 
            setSchedule(Schedule.createPositiveInstance(this));
        return schedule;
    }
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
    public void resetSchedule() {
        this.schedule = null;
    }
    
    public boolean canChangeJoinOrder() {
        return getSchedule().canChangeJoinOrder();
    }
    public boolean canChangeScheduleElements() {
        return getSchedule().canChangeScheduleElements();
    }
    public boolean canChangeVariableInfos() {
        return getSchedule().canChangeVariableInfos();
    }
    public void changeJoinOrder(IJoinOrder joinOrder) throws IllegalStateException {
        getSchedule().changeJoinOrder(joinOrder);
    }
    public void changeScheduleElements(IScheduleElements ScheduleElements) throws IllegalStateException {
        getSchedule().changeScheduleElements(ScheduleElements);
    }
    public void changeVariableInfos(IVariableInfoQueue VariableInfos) throws IllegalStateException {
        getSchedule().changeVariableInfos(VariableInfos);
    }
    public IJoinOrder getJoinOrder() {
        return getSchedule().getJoinOrder();
    }
    public IScheduleElements getScheduleElements() {
        return getSchedule().getScheduleElements();
    }
    public int getScheduleLength() {
    	return getSchedule().getScheduleLength();
    }
    public IVariableInfoQueue getVariableInfos() {
        return getSchedule().getVariableInfos();
    }
    public void resetJoinOrder() throws IllegalStateException {
        getSchedule().resetJoinOrder();
    }
    public void resetScheduleElements() throws IllegalStateException {
        getSchedule().resetScheduleElements();
    }
    public void resetVariableInfos() throws IllegalStateException {
        getSchedule().resetVariableInfos();
    }

    public Set<Variable> getInitiallyKnownVariables() {
        return getVariables();
    }
    
    public List<FormalVariable> getFormalVariables() {
        return getConstraint().getFormalVariables();
    }
    public FormalVariable getFormalVariableAt(int index) throws IndexOutOfBoundsException {
        return getConstraint().getFormalVariableAt(index);
    }
    public VariableType getFormalVariableTypeAt(int index) throws IndexOutOfBoundsException {
        return getConstraint().getFormalVariableTypeAt(index);
    }
    
    public boolean hasIntraConstraintImplicitGuards() {
        int i = getArity() - getNbVariables();
        if (i == 0) return false;
        for (IArgument argument : this)
            if (argument == NamelessVariable.getInstance())
                if (--i == 0) return false;
        return true;
    }
    
    @Override
    public SortedSet<Variable> getVariables() {
        return Collections.unmodifiableSortedSet(getVariablesRef());
    }
    public Variable[] getVariableArray() {
    	return getVariablesRef().toArray(new Variable[getNbVariables()]);
    }
    @Override
    public int getNbVariables() {
        return getVariablesRef().size();
    }
    
    protected SortedSet<Variable> getVariablesRef() {
        return variables;
    }
    protected void setVariables(SortedSet<Variable> variables) {
        this.variables = variables;
    }
    
    @Override
    public void addArgument(IArgument argument) {
        super.addArgument(argument);
        
        try {
            if (argument != NamelessVariable.getInstance())
                getVariablesRef().add((Variable)argument);

        } catch (ClassCastException cce) {
            throw new IllegalArgumentException(
                "Non-variable argument in head (" + argument + ")"
            );
        }
    }
    
    public List<IActualVariable> getVariableList() {
        return new AbstractList<IActualVariable>(){
            @Override
            public int size() {
                return getArity();
            }
        
            @Override
            public IActualVariable get(int index) {
                return getArgumentAt(index);
            }
            
            @Override
            public IActualVariable set(int index, IActualVariable variable) {
                IActualVariable result = get(index);
                replaceArgumentAt(index, variable);
                return result;
            }
        };
    }
    
    @Override
    public IActualVariable getArgumentAt(int index) {
        return (IActualVariable)super.getArgumentAt(index);
    }
    
    public ListIterator<IActualVariable> getVariableIterator() {
        return new CastingListIterator<IArgument, IActualVariable>(
            getArguments().listIterator()
        );
    }
    
    public Iterator<? extends ISelector> getSelectorIterator() {
        Iterator<IGuardConjunct> guardIterator = new FilteredIterator<IGuardConjunct>(
            getHead().getGuard().iterator(),
            new Filter<IGuardConjunct>() {
                @Override
                public boolean exclude(IGuardConjunct elem) {
                    return Collections.disjoint(elem.getVariables(), getVariablesRef());
                }
            }
        );
        
        if (isNegative())
            return guardIterator;
        else {
            Iterator<NegativeHead> negativeIterator = new FilteredIterator<NegativeHead>(
                getRule().getNegativeHeads(),
                new Filter<NegativeHead>() {
                    @Override
                    public boolean exclude(NegativeHead elem) {
                        return Collections.disjoint(elem.getVariables(), getVariablesRef());
                    }
                }
            );
            
            @SuppressWarnings("unchecked")
            Iterator<ISelector> result = new ChainingIterator<ISelector>(
                guardIterator, negativeIterator
            );
            return result;
        }
    }
    
    public boolean isExplicitlyGuarded() {
        return getSelectorIterator().hasNext();
    }
    
    public boolean isExplicitlyGuardedOn(int variableIndex) {
        return isExplicitlyGuardedOn(getArgumentAt(variableIndex));
    }
    
    public boolean isExplicitlyGuardedOn(IActualVariable variable) {
        if (variable.isAnonymous()) return false;
        if (variable.isImplicit()) return true;
        
        final Variable var = (Variable)variable;        
        class Visitor extends ArgumentedConjunctVisitor {
            public boolean result;
            
            @Override
            public void visit(IBasicArgumented conjunct) {
                result |= VariableScanner.scanFor(conjunct, var);
            }
        
            @Override
            public void visit(Variable conjunct) {
                result |= conjunct == var;
            }
        
            @Override
            public void visit(AssignmentConjunct conjunct) throws ToDoException {
                // cannot yet happen, but just to be sure...
                throw new ToDoException();
            }
            
            @Override
            public void visit(Occurrence occurrence) throws Exception {
                // since we know the variable set is cached...
                result |= occurrence.getVariablesRef().contains(var);
            }
        }
        
        
        try {
            Visitor visitor = new Visitor();
            IConjunctVisitable visitable = getHead().getGuard();
            Iterator<NegativeHead> iter = getRule().getNegativeHeads().iterator();
            
            while (true) {
                visitable.accept(visitor);
                if (visitor.result) return true;
                if (!iter.hasNext()) return false;
                visitable = iter.next();
            } 
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public int getIndexOf(ILookupCategory lookupCategory) {
        return getConstraint().getIndexOf(lookupCategory);
    }
    
    public void setPassive() {
        passive = true;
    }
    public boolean isPassive() {
        return passive;
    }
    public boolean isActive() {
        return !passive;
    }
    
    /**
     * Is the occurrence reactive or not? A non-reactive occurrence is
     * an occurrence that cannot become active through a reactive transition. 
     * I.e. a non-reactive occurrence will be active exactly once, namely when
     * first activated. An occurrence can never be reactive if it is passive.
     * 
     * @return True iff this occurrence is <i>reactive</i>.
     */
    public boolean isReactive() {
        return maybeReactive
            && !getConstraint().canNeverBeReactived()
            && !isPassive() 
            && getStorageInfo().mayBeStored()
            && getArity() != 0;
    }
    
    public boolean isReactiveOn(int variableIndex) {
        return isReactive() && getArgumentAt(variableIndex).isReactive();
    }
    
    private boolean maybeReactive = true;
    public void setUnreactive() {
        this.maybeReactive = false;
    }
    public void resetReactiveness() {
        this.maybeReactive = true;
        
        for (Variable variable : getVariablesRef())
            variable.resetReactiveness();
    }
    
    protected Set<Variable> getUnfixedVariables() {
        Set<Variable> result = new TreeSet<Variable>();
        for (Variable variable : getVariables())
            if (!variable.isFixed()) result.add(variable);
        return result;
    }
    
    public boolean isStored() {
        return getType() != OccurrenceType.REMOVED
            && !isPassive()
            && !unstored 
            && getBody().hasConjuncts();
    }
    
    private boolean unstored;
    public void setUnstored() {
        unstored = true;
    }
    
    public boolean checksHistoryOnActivate() {
        return isActive() && checkHistoryOnActivate;
    }
    
    private boolean checkHistoryOnActivate = true;
    public void doNotCheckHistoryOnActivate() {
        checkHistoryOnActivate = false;
    }
    
    public int compareTo(Occurrence other) {
        return this.getOccurrenceIndex() - other.getOccurrenceIndex();
    }
    
    @Override
    protected boolean isValidArgument(IArgument argument, int index) {
        return (argument == NamelessVariable.getInstance()) 
            || super.isValidArgument(argument, index);
    }
    
    public boolean isTerminated() {
        return (getType() == null);
    }
    public void terminate() {
        setType((OccurrenceType)null);
        getConstraint().removeOccurrence(this);
        getHead().removeOccurrence(this);
    }
    
    public void accept(IOccurrenceVisitor visitor) throws Exception {
        if (visitor.visits(getType())) visitor.visit(this);
    }
    @Override
    public void accept(IConjunctVisitor visitor) throws Exception {
        accept((IOccurrenceVisitor)visitor);
    }
    
    public void accept(IJoinOrderVisitor visitor) throws Exception {
        if (visitor.isVisiting())
            visitor.visit(this);
        else
            getJoinOrder().accept(visitor);
    }
    
    public void accept(IScheduleVisitor visitor) throws Exception {
        visitor.isVisiting();
        getSchedule().accept(visitor);
    }
    
    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }
    
    public static Occurrence getOnlyPartner(Occurrence occurrence) {
    	if (occurrence.getHead().getNbOccurrences() != 2)
    		throw new IllegalArgumentException(occurrence);
        return occurrence.getPartnerAt(occurrence.getOccurrenceNbr() % 2); 
    }
    
    public FunctionalDependencies getFunctionalDependencies() {
    	return getConstraint().getFunctionalDependencies();
    }
}