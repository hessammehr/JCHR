package util.visitor;

import util.Resettable;

public interface IExtendedVisitor extends IVisitor, Resettable {
    
    /**
     * Used to check whether this visitor is already visiting
     * elements or not. All subsequent calls to this method
     * will return <code>true</code>, until this visitor is reset.
     * <p>
     * This method is to be used when something that can be
     * visited (if visiting an &quot;outer&quot; visitable) 
     * can also be visited recursively.  
     * 
     * @return <code>true</code> if this visitor is already visiting 
     *  elements; <code>false</code> otherwise. 
     */
    public boolean isVisiting();
    
    /**
     * Resets the visiting status of this visitor.
     */
    public void resetVisiting();
}
