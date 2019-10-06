package util.visitor;

public class AbstractExtendedVisitor implements IExtendedVisitor {

    private boolean visiting;
    
    public boolean isVisiting() {
        if (visiting) return true;
        visiting = true;
        return false;
    }
    
    public void resetVisiting() {
        visiting = false;
    }
    
    public void reset() throws Exception {
        resetVisiting();
    }
}
