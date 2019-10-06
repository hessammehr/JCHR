package compiler.CHRIntermediateForm.constraints.ud.schedule;

import java.util.RandomAccess;

import compiler.CHRIntermediateForm.variables.IActualVariable;

public interface IScheduleElements 
    extends Iterable<IScheduleElement>, IScheduleVisitable, RandomAccess {

    /**
     * Returns the implicit variables chosen for the active entity,
     * or <code>null</code> if there are none. 
     * 
     * @return A view of the implicit variables for the active
     *  entity for this list of schedule elements.  
     */
    public IActualVariable[] getActiveImplicitVariables();
    
    public int getLength();
    
    public IScheduleElement getElementAt(int index);
    
    public int getIndexOf(IScheduleElement element);
    
}