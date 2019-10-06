package compiler.CHRIntermediateForm.rulez;

import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.NEGATIVE;

import java.util.Set;
import java.util.SortedSet;

import compiler.CHRIntermediateForm.Cost;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.OccurrenceType;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrder;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrderVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleElement;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleElements;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduled;
import compiler.CHRIntermediateForm.constraints.ud.schedule.ISelector;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IVariableInfoQueue;
import compiler.CHRIntermediateForm.constraints.ud.schedule.Schedule;
import compiler.CHRIntermediateForm.variables.Variable;

public class NegativeHead extends Head 
    implements ISelector, IScheduleElement, IScheduled {
    
    /**
     * The schedule for this negative head. If none is set a default 
     * will be returned. Note that this is done lazily.
     */
    private Schedule schedule;
    
    protected NegativeHead(Rule rule) {
        super(rule);
    }
    
    public Head getPositiveHead() {
        return getRule().getPositiveHead();
    }
    
    @Override
    public boolean canAddOccurrenceType(OccurrenceType occurrenceType) {
        return (occurrenceType == NEGATIVE);
    }
    
    @Override
    public void accept(IOccurrenceVisitor visitor) throws Exception {
        if (visitor.visits(NEGATIVE)) super.accept(visitor);
    }
    
    public Schedule getSchedule() {
        if (schedule == null) 
            setSchedule(Schedule.createNegativeInstance(this));
        return schedule;
    }
    public int getScheduleLength() {
    	return getSchedule().getScheduleLength();
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

    private SortedSet<Variable> joinOrderPrecondition;
    public SortedSet<Variable> getJoinOrderPrecondition() {
        if (joinOrderPrecondition == null) {
            joinOrderPrecondition = getAllVariables();
            joinOrderPrecondition.retainAll(getPositiveHead().getVariables());
        }
        return joinOrderPrecondition;
    }
    
    public Set<Variable> getInitiallyKnownVariables() {
        return getJoinOrderPrecondition();
    }
    
    public void accept(IJoinOrderVisitor visitor) throws Exception {
        if (visitor.isVisiting())
            visitor.visit(this);
        else
            getJoinOrder().accept(visitor);
    }
    
    public void accept(IScheduleVisitor visitor) throws Exception {
        if (visitor.isVisiting())
            visitor.visit(this);
        else
            getSchedule().accept(visitor);
    }
    
    public float getSelectivity() {
        return .75f;
    }
    public Cost getSelectionCost() {
    	return Cost.EXPENSIVE;
    }
    
    @Override
    public StringBuilder appendTo(StringBuilder result) {
        return super.appendTo(result.append(" \\\\ "));
    }
    
    public boolean isSelective() {
        return !succeeds();
    }
    
    public boolean isPassive() {
        for (Occurrence occurrence : getOccurrencesRef())
            if (occurrence.isActive()) return false;
        return true;
    }
    public boolean isActive() {
        return !isPassive();
    }
    
    
    public boolean succeeds() {
        if (getGuard().fails()) return true;
        for (Occurrence occurrence : getOccurrencesRef())
            if (!occurrence.getStorageInfo().mayBeStored())
                return true;
        return false;
    }
    public boolean fails() {
        return false;
    }
}
