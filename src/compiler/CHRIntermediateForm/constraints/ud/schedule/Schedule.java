package compiler.CHRIntermediateForm.constraints.ud.schedule;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import compiler.CHRIntermediateForm.arg.visitor.VariableCollector;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.lookup.Lookup;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.variables.Variable;

/**
 * A schedule consists of three parts:
 * <dl>
 *  <dt>1) join ordering</dt>
 *  <dd>
 *      Determines the order in which occurrences and 
 *      explicit guards will be scheduled.
 *  </dd>
 *  <dt>2) schedule elements</dt>
 *  <dd>
 *      The actual schedule elements. The order of lookups
 *      and explicit guards is based on the join ordering.
 *      
 *  </dd>
 *  <dt>3) variable information</dt>
 *  <dd>
 *      information regarding variables
 *  </dd>
 * </dl>
 * A schedule is constructed in three corresponding phases.
 * Once one of the next phases (this does <em>not</em> have to 
 * be the next one) is done, a previous phase can no
 * longer be changed. For example, once the schedule elements
 * have been set, the join ordering can no longer be changed
 * (because the latter is based on the order given by the former).
 * Analogous for variable information and schedule elements.
 * If one of the phases is skipped or not done, a default
 * (implicit) value will be used.
 * 
 * @author Peter Van Weert
 */
public abstract class Schedule 
implements IScheduleVisitable, Iterable<IScheduleElement> {

    protected Schedule() { /* FACTORY METHODS */ }
    
    public static Schedule createPositiveInstance(final Occurrence occurrence) {
        return new Schedule() {
            @Override
            protected IJoinOrder createImplicitJoinOrder2() {
                return new DefaultPositiveJoinOrder(occurrence);
            }
            @Override
            protected IScheduleElements createImplicitScheduleElements2() {
                return DefaultScheduleElements.createPositiveInstance(occurrence);
            }
            @Override
            protected IVariableInfoQueue createImplicitVariableInfos2() {
            	final SortedSet<Variable> used = new TreeSet<Variable>();
    			VariableCollector.collectVariables(occurrence.getBody(), used);
        		ScheduleVariableCollector.collectVariables(occurrence, used);
        		
        		IVariableInfoQueue result = DefaultVariableInfoQueue.createPositiveInstance(occurrence);
            	Iterator<IVariableInfo> iter = result.iterator();
            	while (iter.hasNext())
            		if (!used.contains(iter.next().getActualVariable()))
            			iter.remove();
            	return result;
            }
        };
    }
    
    protected static class ScheduleVariableCollector extends AbstractScheduleVisitor {
    	private SortedSet<Variable> variables;
    	
    	public ScheduleVariableCollector(SortedSet<Variable> variables) {
    		this.variables = variables;
    	}

		@Override
		public void visit(IGuardConjunct guard) throws Exception {
			variables.addAll(guard.getVariables());
		}

		@Override
		public void visit(Lookup lookup) throws Exception {
			VariableCollector.collectVariables(lookup, variables);
		}
		
	    public static void collectVariables(
    		IScheduled scheduled, SortedSet<Variable> result
	    ) {
	    	try {
	    		ScheduleVariableCollector collector = 
	    			new ScheduleVariableCollector(result);
				scheduled.accept(collector);
	    	} catch (Exception e) {
				e.printStackTrace();
				throw new InternalError();
			}
	    }
    }
    
    public static Schedule createNegativeInstance(final NegativeHead head) {
        return new Schedule() {
            @Override
            protected IJoinOrder createImplicitJoinOrder2() {
                return new DefaultNegativeJoinOrder(head);
            }
            @Override
            protected IScheduleElements createImplicitScheduleElements2() {
                return DefaultScheduleElements.createNegativeInstance(head);
            }
            @Override
            protected IVariableInfoQueue createImplicitVariableInfos2() {
                return DefaultVariableInfoQueue.createNegativeInstance(head); 
            }
        };
    }
    
    /* * * * * * * * * * *\
     * (1) JOIN ORDERING *
    \* * * * * * * * * * */
    
    private IJoinOrder explicitJoinOrder, implicitJoinOrder;
    
    public IJoinOrder getJoinOrder() {
        if (hasExplicitJoinOrder())
            return getExplicitJoinOrder();
        else if (hasImplicitJoinOrder())
            return getImplicitJoinOrder();
        else
            return createImplicitJoinOrder();
    }
    public void changeJoinOrder(IJoinOrder joinOrder) throws IllegalStateException {
        if (!canChangeJoinOrder()) 
            throw new IllegalStateException();
        
        setExplicitJoinOrder(joinOrder);
        resetImplicitScheduleElements();
        resetImplicitVariableInfos();
    }
    public void resetJoinOrder() throws IllegalStateException {
        if (hasExplicitJoinOrder()) {
            if (!canChangeJoinOrder()) 
                throw new IllegalStateException();
            resetImplicitScheduleElements();
            resetImplicitVariableInfos();
        }
        resetExplicitJoinOrder();
    }
    public boolean canChangeJoinOrder() {
        return !hasExplicitScheduleElements() && !hasExplicitVariableInfos(); 
    }
    
    protected IJoinOrder getExplicitJoinOrder() {
        return explicitJoinOrder;
    }
    protected boolean hasExplicitJoinOrder() {
        return explicitJoinOrder != null;
    }
    protected void setExplicitJoinOrder(IJoinOrder explicitJoinOrder) {
        this.explicitJoinOrder = explicitJoinOrder;
    }
    protected void resetExplicitJoinOrder() {
        this.explicitJoinOrder = null;
    }
    protected boolean hasImplicitJoinOrder() {
        return implicitJoinOrder != null;
    }
    protected IJoinOrder getImplicitJoinOrder() {
        return implicitJoinOrder;
    }
    protected void setImplicitJoinOrder(IJoinOrder implicitJoinOrder) {
        this.implicitJoinOrder = implicitJoinOrder;
    }
    protected void resetImplicitJoinOrder() {
        this.implicitJoinOrder = null;
    }
    protected final IJoinOrder createImplicitJoinOrder() {
        IJoinOrder result = createImplicitJoinOrder2();
        setImplicitJoinOrder(result);
        return result;
    }
    protected abstract IJoinOrder createImplicitJoinOrder2();
    
    
    
    /* * * * * * * * * * * * *\
     * (2) SCHEDULE ELEMENTS *
    \* * * * * * * * * * * * */
    
    private IScheduleElements explicitScheduleElements, implicitScheduleElements;
    
    public int getScheduleLength() {
    	return getScheduleElements().getLength();
    }
    
    public IScheduleElements getScheduleElements() {
        if (hasExplicitScheduleElements())
            return getExplicitScheduleElements();
        else if (hasImplicitScheduleElements())
            return getImplicitScheduleElements();
        else
            return createImplicitScheduleElements();
    }
    public void changeScheduleElements(IScheduleElements ScheduleElements) throws IllegalStateException {
        if (!canChangeScheduleElements()) 
            throw new IllegalStateException();
        
        setExplicitScheduleElements(ScheduleElements);
        resetImplicitVariableInfos();
    }
    public void resetScheduleElements() throws IllegalStateException {
        if (hasExplicitScheduleElements()) {
            if (!canChangeScheduleElements())
                throw new IllegalStateException();
            resetImplicitVariableInfos();
        }
        resetExplicitScheduleElements();
    }
    public boolean canChangeScheduleElements() {
        return !hasExplicitVariableInfos();
    }
    
    protected IScheduleElements getExplicitScheduleElements() {
        return explicitScheduleElements;
    }
    protected boolean hasExplicitScheduleElements() {
        return explicitScheduleElements != null;
    }
    protected void setExplicitScheduleElements(IScheduleElements explicitScheduleElements) {
        this.explicitScheduleElements = explicitScheduleElements;
    }
    protected void resetExplicitScheduleElements() {
        this.explicitScheduleElements = null;
    }
    protected boolean hasImplicitScheduleElements() {
        return implicitScheduleElements != null;
    }
    protected IScheduleElements getImplicitScheduleElements() {
        return implicitScheduleElements;
    }
    protected void setImplicitScheduleElements(IScheduleElements implicitScheduleElements) {
        this.implicitScheduleElements = implicitScheduleElements;
    }
    protected void resetImplicitScheduleElements() {
        this.implicitScheduleElements = null;
    }
    protected final IScheduleElements createImplicitScheduleElements() {
        IScheduleElements result = createImplicitScheduleElements2();
        setImplicitScheduleElements(result);
        return result;
    }
    protected abstract IScheduleElements createImplicitScheduleElements2();
    
    /* DECORATOR */
    public void accept(IScheduleVisitor visitor) throws Exception {
        getScheduleElements().accept(visitor);
    }
    public Iterator<IScheduleElement> iterator() {
        return getScheduleElements().iterator();
    }
    
    
    
    /* * * * * * * * * * *\
     * (3) VARIABLE INFO *
    \* * * * * * * * * * */
    
    private IVariableInfoQueue explicitVariableInfos, implicitVariableInfos;
    
    public IVariableInfoQueue getVariableInfos() {
        if (hasExplicitVariableInfos())
            return getExplicitVariableInfos();
        else if (hasImplicitVariableInfos())
            return getImplicitVariableInfos();
        else
            return createImplicitVariableInfos();
    }
    public void changeVariableInfos(IVariableInfoQueue VariableInfos) throws IllegalStateException {
        if (!canChangeVariableInfos()) 
            throw new IllegalStateException();
        setExplicitVariableInfos(VariableInfos);
    }
    public void resetVariableInfos() throws IllegalStateException {
        if (hasExplicitVariableInfos() && !canChangeVariableInfos()) 
            throw new IllegalStateException();
        resetExplicitVariableInfos();
    }
    public boolean canChangeVariableInfos() {
        return true;
    }
    
    protected IVariableInfoQueue getExplicitVariableInfos() {
        return explicitVariableInfos;
    }
    protected boolean hasExplicitVariableInfos() {
        return explicitVariableInfos != null;
    }
    protected void setExplicitVariableInfos(IVariableInfoQueue explicitVariableInfos) {
        this.explicitVariableInfos = explicitVariableInfos;
    }
    protected void resetExplicitVariableInfos() {
        this.explicitVariableInfos = null;
    }
    protected boolean hasImplicitVariableInfos() {
        return implicitVariableInfos != null;
    }
    protected IVariableInfoQueue getImplicitVariableInfos() {
        return implicitVariableInfos;
    }
    protected void setImplicitVariableInfos(IVariableInfoQueue implicitVariableInfos) {
        this.implicitVariableInfos = implicitVariableInfos;
    }
    protected void resetImplicitVariableInfos() {
        this.implicitVariableInfos = null;
    }
    protected final IVariableInfoQueue createImplicitVariableInfos() {
        IVariableInfoQueue result = createImplicitVariableInfos2();
        setImplicitVariableInfos(result);
        return result;
    }
    protected abstract IVariableInfoQueue createImplicitVariableInfos2();
    
    
    
    @Override
    public String toString() {
    	return "{elements=" + getScheduleElements() + ", variables=" + getVariableInfos() + '}';
    }
}