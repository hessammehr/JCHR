package compiler.CHRIntermediateForm.constraints.ud;

import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.NEGATIVE;
import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.REMOVED;
import static compiler.CHRIntermediateForm.constraints.ud.StorageInfo.NEVER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

import util.comparing.Comparison;
import annotations.JCHR_Fixed;

import compiler.CHRIntermediateForm.Handler;
import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumentable.Argumentable;
import compiler.CHRIntermediateForm.arg.argumentable.IArgumentable;
import compiler.CHRIntermediateForm.arg.argumented.IArgumented;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.constraints.Constraint;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.DefaultLookupCategories;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.DefaultLookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategories;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.LookupCategories;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.LookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.NeverStoredLookupCategories;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.DefaultLookupType;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;
import compiler.CHRIntermediateForm.exceptions.DuplicateIdentifierException;
import compiler.CHRIntermediateForm.exceptions.IdentifierException;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.id.Identifier;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.matching.MatchingInfos;
import compiler.CHRIntermediateForm.rulez.AbstractOccurrenceVisitor;
import compiler.CHRIntermediateForm.rulez.Head;
import compiler.CHRIntermediateForm.rulez.IOccurrenceVisitable;
import compiler.CHRIntermediateForm.rulez.IOccurrenceVisitor;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.variables.FormalVariable;
import compiler.CHRIntermediateForm.variables.VariableType;
import compiler.analysis.FunctionalDependencies;
import compiler.analysis.FunctionalDependency;

/**
 * @author Peter Van Weert
 */
@JCHR_Fixed // can only be used for equality constraints, so...
public class UserDefinedConstraint 
    extends Constraint<UserDefinedConstraint> 
    implements IOccurrenceVisitable {
    
    protected final static int INITIAL_ARITY_CAPACITY = 4;    	
    
    /**
     * The list of formal variables for this user defined
     * constraint.
     */
    private List<FormalVariable> formalVariables;
    
    private Set<UserDefinedConstraint> removees;
    
    private List<Occurrence> positiveOccurrences;
    private List<Occurrence> negativeOccurrences;
    
    private ILookupCategories lookupCategories;
    
    private StorageInfo storageInfo;
    private MultisetInfo multisetInfo;
    
    private boolean idempotent;
    
    private FunctionalDependencies functionalDependencies;
    
    private int modifiers;
    
    private Handler handler;
    
    public UserDefinedConstraint(Handler handler, String id) throws IllegalIdentifierException {
        this(handler, id, 0);
    }
    
    public UserDefinedConstraint(Handler handler, String id, int modifiers) throws IllegalIdentifierException {
        this(handler, id, null, modifiers);
    }
    
    public UserDefinedConstraint(Handler handler, String id, String infix, int modifiers) 
    throws IllegalIdentifierException {
        super(id);
        setHandler(handler);
        if (infix != null) setInfixIdentifiers(infix);
        setModifiers(modifiers);
        
        setArguments(new ArrayList<FormalVariable>(INITIAL_ARITY_CAPACITY));
        
        setPositiveOccurrences(new LinkedList<Occurrence>());
        setNegativeOccurrences(new LinkedList<Occurrence>());
        
        setFunctionalDependencies(new FunctionalDependencies());
    }
    
    public Handler getHandler() {
		return handler;
	}
    protected void setHandler(Handler handler) {
		this.handler = handler;
	}
    
    void addOccurrence(Occurrence occurrence) {
        final Rule rule = occurrence.getRule();
        
        ListIterator<Occurrence> iterator = getOccurrencesRef(occurrence).listIterator();
        while (iterator.hasNext()) {
            // Look for the first occurrence from the new occurrence's rule, or
            // from a rule that comes further in the file (won't happen with default
            // compilation, but it is safer to keep an open mind)
            if (iterator.next().getRule().compareTo(rule) >= 0) {
                iterator.previous();        // go one back
                iterator.add(occurrence);   // and insert the new occurrence
                return;
            }
        }
        
        // If its a new rule, add as the last occurrence:
        iterator.add(occurrence);
    }
    
    void removeOccurrence(Occurrence occurrence) {
        getOccurrencesRef(occurrence).remove(occurrence);
    }

    /**
     * Returns the reference to the list of occurrences the given
     * occurrence belongs to / has to belong to. 
     * 
     * @param occurrence
     *  An occurrence.
     * @return A reference to the list of negative occurrences
     *  if <code>occurrence</code> is a negative occurrence,
     *  or else a reference to the list of positive occurrences
     *  (since occurrence is of course a positive occurrence)
     */
    protected List<Occurrence> getOccurrencesRef(Occurrence occurrence) {
        return occurrence.isNegative()
            ? getNegativeOccurrencesRef() 
            : getPositiveOccurrencesRef();
    }
    public int getIndexOf(Occurrence occurrence) {
        List<Occurrence> occurrences = getOccurrencesRef(occurrence); 
        
        final int size = occurrences.size();
        for (int i = 0; i < size; i++)             
            if (occurrences.get(i) == occurrence) return i;
        
        throw new RuntimeException();
    }
    public boolean hasAsOccurrence(Occurrence occurrence) {
        return getOccurrencesRef(occurrence).contains(occurrence);
    }
    
    public void accept(IOccurrenceVisitor visitor) throws Exception {
        AbstractOccurrenceVisitor.visitPositiveOccurrencesWith(
            visitor, getPositiveOccurrencesRef()
        );
        
        if (visitor.visits(NEGATIVE))
            for (Occurrence occurrence : getNegativeOccurrencesRef())
                visitor.visit(occurrence);
    }
    
    protected List<Occurrence> getPositiveOccurrencesRef() {
        return positiveOccurrences;
    }
    public List<Occurrence> getPositiveOccurrences() {
        return Collections.unmodifiableList(getPositiveOccurrencesRef());
    }
    public Occurrence getPositiveOccurrenceAt(int index) {
        return getPositiveOccurrencesRef().get(index);
    }
    public int getNbPositiveOccurrences() {
        return getPositiveOccurrencesRef().size();
    }
    public int getNbActivePositiveOccurrences() {
        int result = 0;
        for (Occurrence occurrence : getPositiveOccurrencesRef())
            if (occurrence.isActive()) result++;
        return result;
    }
    public Occurrence getLastActivePositiveOccurrence() throws NoSuchElementException {
        List<Occurrence> occurrences = getPositiveOccurrencesRef();
        for (int i = occurrences.size()-1; i >= 0; i--)
            if (occurrences.get(i).isActive()) 
                return occurrences.get(i);
        throw new NoSuchElementException();
    }
    protected void setPositiveOccurrences(List<Occurrence> occurrences) {
        this.positiveOccurrences = occurrences;
    }
    
    protected List<Occurrence> getNegativeOccurrencesRef() {
        return negativeOccurrences;
    }
    public List<Occurrence> getNegativeOccurrences() {
        return Collections.unmodifiableList(getNegativeOccurrencesRef());
    }
    public Occurrence getNegativeOccurrenceAt(int index) {
        return getNegativeOccurrencesRef().get(index);
    }
    public int getNbNegativeOccurrences() {
        return getNegativeOccurrencesRef().size();
    }
    public int getNbActiveNegativeOccurrences() {
        int result = 0;
        for (Occurrence occurrence : getNegativeOccurrencesRef())
            if (! occurrence.isPassive()) result++;
        return result;
    }
    protected void setNegativeOccurrences(List<Occurrence> occurrences) {
        this.negativeOccurrences = occurrences;
    }
    
    public Set<Rule> getRules() {
        final Set<Rule> result = new HashSet<Rule>();
        for (Occurrence occurrence : getPositiveOccurrencesRef())
            result.add(occurrence.getRule());
        return result;
    }

    public String getFormalVariableIdAt(int index) {
        return getFormalVariableAt(index).getIdentifier();
    }
    public FormalVariable getFormalVariableAt(int index) {
        return getFormalVariablesRef().get(index);
    }
    public VariableType getFormalVariableTypeAt(int index) {
        return getFormalVariableAt(index).getVariableType();
    }
    public FormalVariable getFormalVariableWith(String id) {
        for (FormalVariable result : getFormalVariablesRef())
            if (result.getIdentifier().equals(id))
                return result;
        return null;
    }
    public boolean hasFormalVariableWith(String id) {
        return getFormalVariableWith(id) != null;
    }   
    public List<FormalVariable> getFormalVariables() {
        return Collections.unmodifiableList(getFormalVariablesRef());
    }
    protected List<FormalVariable> getFormalVariablesRef() {
        return formalVariables;        
    }    
    public void addFormalVariable(String identifier, VariableType type) 
    	throws DuplicateIdentifierException, IllegalIdentifierException {
        
        new FormalVariable(this, identifier, type);
    }
    public void linkFormalVariable(FormalVariable variable) 
        throws DuplicateIdentifierException {
        
        if ( hasFormalVariableWith(variable.getIdentifier() ) )
            throw new DuplicateIdentifierException(variable.getIdentifier());
        getFormalVariablesRef().add(variable);
    }
    
    protected void setArguments(List<FormalVariable> variables) {
        this.formalVariables = variables;
    }
    
    public int getArity() {
        return getFormalVariablesRef().size();
    }
    public int getExplicitArity() {
        return getArity();
    }
    public IType getFormalParameterTypeAt(int index) throws ArrayIndexOutOfBoundsException {
        return getFormalVariableAt(index).getType();
    }
    public IType getExplicitFormalParameterTypeAt(int index) {
        return getFormalParameterTypeAt(index);
    }
    public IType[] getFormalParameterTypes() {
        IType[] result = new IType[getArity()];
        for (int i = 0; i < getArity(); i++)
            result[i] = getFormalParameterTypeAt(i);
        return result;
    }
    public IType[] getExplicitFormalParameterTypes() {
        return getFormalParameterTypes();
    }
    
    public UserDefinedConjunct createInstance(IArgument... arguments) {
        return createInstance(new Arguments(arguments));
    }
    public UserDefinedConjunct createInstance(IArguments arguments) {
        return new UserDefinedConjunct(this, arguments);
    }
    
    public IArgumented<UserDefinedConstraint> createInstance(MatchingInfos infos, IArgument... arguments) {
        return createInstance(infos, new Arguments(arguments));
    }
    public IArgumented<UserDefinedConstraint> createInstance(MatchingInfos infos, IArguments arguments) {
        arguments.incorporate(infos, false);
        return createInstance(arguments);
    }
    
    public Occurrence createOccurrence(Head head, /*IArguments args,*/ OccurrenceType type) {
        // de dubbele bindingen worden door deze constructor geregeld:
        return new Occurrence(head, this, /*args,*/ type);
    }
    
    public boolean isAskConstraint() {
        return false;
    }
    
    public boolean triggersConstraints() {
    	return true;
    }
    
    public boolean isEquality() {
        return false;
    }
    
    public boolean haveToIgnoreImplicitArgument() {
        return false;   // heeft er geen!
    }

    public MatchingInfos canHaveAsArguments(IArguments arguments) {
        return Argumentable.canHaveAsArguments(this, arguments);
    }

    public MatchingInfo canHaveAsArgumentAt(int index, IArgument argument) {
        return argument.getType().isAssignableTo(getFormalVariableAt(index).getType());
    }

    public Comparison compareWith(IArgumentable<?> other) {
        return Argumentable.compare(this, other);
    }
    
    public ILookupCategories getLookupCategories() {
        return lookupCategoriesNotSet()
            ? DefaultLookupCategories.getInstance()
            : lookupCategories;
    }
    public int getNbLookupCategories() {
        return getLookupCategories().getNbLookupCategories();
    }
    protected void setLookupCategories(ILookupCategories lookupCategories) {
        this.lookupCategories = lookupCategories;
    }
    public int getIndexOf(ILookupCategory lookupCategory) {
        return getLookupCategories().getIndexOf(lookupCategory);
    }
    /**
     * Makes sure the default category is present in the set of lookup
     * categories. If currently this constraint has default lookup
     * categories, this also has to be changed to a non default
     * set of lookup categories (containing only the default lookup
     * category), because else the default lookup category might
     * get lost later.
     */
    public void ensureDefaultLookupCategory() {
        if (lookupCategoriesNotSet() || 
            !hasLookupCategory(DefaultLookupCategory.getInstance()))                
            addLookupCategory(DefaultLookupCategory.getInstance());
    }
    /**
     * Makes sure a lookup category for the given type is present in 
     * the current collection of lookup categories. 
     * It also makes sure the category and the type are linked
     * correctly.
     */
    public ILookupCategory ensureLookupCategory(ILookupType lookupType) {
        if (lookupType == DefaultLookupType.getInstance()) {
            ensureDefaultLookupCategory();
            return DefaultLookupCategory.getInstance();
        } else {
            ILookupCategory result = getLookupCategory(lookupType);
            if (result != null) return result;
            result = new LookupCategory(lookupType);
            addLookupCategory(result);
            return result;
        }
    }
    /**
     * Adds a given lookup category to the current collection
     * of lookup categories. 
     * Also makes sure that the current lookup-category is no longer
     * the default one, but an optimised (possibly empty) 
     * collection of lookup categories.
     */
    protected void addLookupCategory(ILookupCategory lookupCategory) {
        ensureLookupCategoriesSet();
        getLookupCategories().addLookupCategory(lookupCategory);
    }
    
    public ILookupCategory getLookupCategory(ILookupType lookupType) {
        return getLookupCategories().getLookupCategory(lookupType);
    }
    public boolean hasLookupCategory(ILookupCategory category) {
        return getLookupCategories().contains(category);
    }
    public ILookupCategory getMasterLookupCategory() {
        return getLookupCategories().getMasterLookupCategory();
    }
    public boolean hasMasterLookupCategory() {
        for (ILookupCategory category : getLookupCategories())
            if (category.isMasterCategory()) return true;
        return false;
    }
    
    protected boolean lookupCategoriesSet() {
        return (lookupCategories != null);
    }
    protected boolean lookupCategoriesNotSet() {
        return !lookupCategoriesSet();
    }
    protected void ensureLookupCategoriesSet() {
        if (lookupCategoriesNotSet()) 
            setLookupCategories(new LookupCategories());
    }

    /**
     * Changes the infix identifier of this user defined constraint
     * (<code>null</code> if undefined).
     * 
     * @param infix 
     *  The infix identifier of this user defined constraint
     *  (<code>null</code> if undefined).
     *  
     * @throws IllegalIdentifierException
     *  If the given identifier is an illegal infix identifier.
     * @throws IdentifierException
     *  If this user defined constraint is not a binary 
     *  constraint.
     */
    public void addInfixIdentifier(String infix) 
    throws IllegalIdentifierException, IdentifierException, 
        DuplicateIdentifierException {
        
        if (getArity() != 2)
            throw new IdentifierException(
                "A non-binary constraint cannot have an infix id"
            );
            
        if (infix != null) {
            Identifier.testInfixIdentifier(infix);
            
            if (hasAsInfix(infix))
                throw new DuplicateIdentifierException(infix);
            
            int num = getNbInfixIdentifiers();
            String[] identifiers = new String[num + 1];
            if (num != 0) System.arraycopy(getInfixIdentifiers(), 0, identifiers, 0, num);
            identifiers[num] = infix;
            setInfixIdentifiers(identifiers);
        }
    }
    
    @Override
    public boolean canHaveAsIdentifier(String identifier) {
        return Identifier.isValidUdSimpleIdentifier(identifier)
            && Identifier.startsWithLowerCase(identifier);
    }
    
    public boolean updateStorageInfo(StorageInfo storageInfo) {
        if (storageInfoSet() && !storageInfo.isBetterThan(getStorageInfo())) 
        	return false;
        
        if (storageInfo == NEVER) {
            if (lookupCategoriesSet()) throw new IllegalStateException();
            setLookupCategories(NeverStoredLookupCategories.getInstance());
        }
        
        setStorageInfo(storageInfo);
        return true;
    }
    protected void setStorageInfo(StorageInfo storageInfo) {
		this.storageInfo = storageInfo;
	}
    public boolean storageInfoSet() {
        return storageInfo != null;
    }
    public StorageInfo getStorageInfo() {
        return storageInfoSet()
            ? storageInfo
            : StorageInfo.getDefault();
    }
    
    public boolean mayBeStored() {
    	return getStorageInfo().mayBeStored();
    }
    public boolean mayBeRemoved() {
    	for (Occurrence occurrence : getPositiveOccurrencesRef())
    		if (occurrence.getType() == REMOVED)
    			return true;
    	return false;
    }
    
    public boolean isSingleton() {
        return getArity() == 0
            && getMultisetInfo().isSet();
    }
    
    public void setMultisetInfo(MultisetInfo multisetInfo) {
        if (multisetInfoSet()) throw new IllegalStateException();
        this.multisetInfo = multisetInfo;
    }
    public boolean multisetInfoSet() {
        return multisetInfo != null;
    }
    
    public MultisetInfo getMultisetInfo() {
        return multisetInfoSet() 
            ? multisetInfo
            : MultisetInfo.getDefault();
    }
    
    public boolean canNeverBeReactived() {
        if (!mayBeStored() || (getArity() == 0)) 
            return true;
        for (FormalVariable variable : getFormalVariablesRef())
            if (! variable.isFixed()) return false;
        return true;
    }
    
    public boolean isReactiveOn(int index) {
        if (canNeverBeReactived()) return false;
        for (Occurrence occurrence : getPositiveOccurrencesRef())
            if (occurrence.isReactiveOn(index)) return true;
        return false;
    }
    
    /**
     * Can occurrences of this constraint be reactive or not? 
     * A non-reactive occurrence is an occurrence that cannot become active 
     * through a reactive transition.
     * I.e. a non-reactive occurrence will be active exactly once, namely when
     * first activated.
     * 
     * @return True iff occurrences of this constraint could be <i>reactive</i>.
     */
    public boolean isReactive() {
        if (canNeverBeReactived()) return false;
        for (Occurrence occurrence : getPositiveOccurrencesRef())
            if (occurrence.isReactive()) return true;
        return false;
    }
    
    public boolean hasActivePositiveOccurrences() {
        for (Occurrence occurrence : getPositiveOccurrencesRef())
            if (occurrence.isActive()) return true;
        return false;
    }
    
    public int getModifiers() {
        return modifiers;
    }
    protected void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }
    
    
    
    private boolean recursive = true;
    
    /**
     * Is this constraint recursive or not
     * (note that this method is conservative: if no analysis is
     * performed to tell whether this constraint is recursive,
     * a pessimistic view is taken and the result will be <code>true</code>).  
     *  
     * @return <code>true</code> if this constraint is known 
     * (or assumed) to be recursive, <code>false</code> if it
     * is known that this is not the case.
     */
    public boolean isRecursive() {
    	return recursive; 
    }
    public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}
    
    public void setRemovees(Set<UserDefinedConstraint> removees) {
		this.removees = removees;
	}
    public Set<UserDefinedConstraint> getRemovees() {
		return removees;
	}
    public boolean removes(UserDefinedConstraint other) {
    	Set<UserDefinedConstraint> removees = getRemovees();
    	if (removees == null)
    		return true;
    	else
    		return removees.contains(other);
    }
    
    @Override
    public String toString() {
    	return getIdentifier() + "/" + getArity();
    }
    
    
    public FunctionalDependencies getFunctionalDependencies() {
		return functionalDependencies;
	}
    protected void setFunctionalDependencies(
    		FunctionalDependencies functionalDependencies) {
		this.functionalDependencies = functionalDependencies;
	}
    public void addFunctionalDependency(FunctionalDependency functionalDependency) {
    	if (!functionalDependency.isTrivial())
    		getFunctionalDependencies().add(functionalDependency);
	}
    
    
    public void setIdempotent() {
		idempotent = true;
	}
    @Override
    public boolean isIdempotent() {
    	return idempotent;
    }
}