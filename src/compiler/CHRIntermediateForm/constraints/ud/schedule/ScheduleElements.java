package compiler.CHRIntermediateForm.constraints.ud.schedule;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import compiler.CHRIntermediateForm.variables.IActualVariable;

public class ScheduleElements 
    extends ArrayList<IScheduleElement> 
    implements IScheduleElements {

    private static final long serialVersionUID = 1L;

    public ScheduleElements() {
        // NOP
    }
    public ScheduleElements(IScheduleElements base) {
        setActiveImplicitVariables(base.getActiveImplicitVariables());
    }
    public ScheduleElements(IActualVariable[] vars) {
        setActiveImplicitVariables(vars);
    }
    
    private IActualVariable[] activeImplicitVariables;
    
    public IActualVariable[] getActiveImplicitVariables() {
        return activeImplicitVariables;
    }
    protected void setActiveImplicitVariables(
            IActualVariable[] activeImplicitVariables) {
        this.activeImplicitVariables = activeImplicitVariables;
    }
    
    public void accept(IScheduleVisitor visitor) throws Exception {
        visitor.isVisiting();
        for (IScheduleElement element : this) element.accept(visitor);
    }
    
    public int getLength() {
    	return size();
    }
    public IScheduleElement getElementAt(int index) {
    	return get(index);
    }
    public int getIndexOf(IScheduleElement element) {
    	for (int i = 0; i < getLength(); i++)
    		if (getElementAt(i) == element)
    			return i;
    	throw new NoSuchElementException();
    }
}