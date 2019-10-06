package compiler.CHRIntermediateForm.constraints.ud.schedule;

import java.util.Set;

import compiler.CHRIntermediateForm.variables.Variable;

public interface IScheduled extends IScheduleVisitable, IJoinOrderVisitable {

    Set<Variable> getInitiallyKnownVariables();
    
    Schedule getSchedule();
    
    int getScheduleLength();
    
    /* DECORATOR METHODS */
    IJoinOrder getJoinOrder();
    boolean canChangeJoinOrder();
    void changeJoinOrder(IJoinOrder joinOrder) throws IllegalStateException;
    void resetJoinOrder() throws IllegalStateException;
    
    IScheduleElements getScheduleElements();
    boolean canChangeScheduleElements();
    void changeScheduleElements(IScheduleElements scheduleElements) throws IllegalStateException;
    void resetScheduleElements() throws IllegalStateException;
    
    IVariableInfoQueue getVariableInfos();
    boolean canChangeVariableInfos();
    void changeVariableInfos(IVariableInfoQueue variableInfos) throws IllegalStateException;
    void resetVariableInfos() throws IllegalStateException;
}
