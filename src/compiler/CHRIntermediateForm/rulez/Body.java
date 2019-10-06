package compiler.CHRIntermediateForm.rulez;

import compiler.CHRIntermediateForm.conjuncts.Conjunction;
import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.constraints.bi.Failure;

/**
 * @author Peter Van Weert
 */
public class Body extends Conjunction<IConjunct> {

    public Body() {
        super();
    }
    public Body(Conjunction<IConjunct> conjunction) {
        super(conjunction);
    }
    
    public IConjunct getFinalConjunct() {
        return getConjuncts().get(getLength() - 1);
    }
    
    @Override
    public void addConjunct(IConjunct conjunct) {
        if (endsWithFailure())
            throw new IllegalStateException("Unreachable conjunct: " + conjunct);
        
        super.addConjunct(conjunct);
    }
    
    public boolean endsWithFailure() {
        return hasConjuncts() 
            && getFinalConjunct() instanceof Failure;        
    }
    
    public IConjunct[] getConjunctsArray() {
        return getConjunctsArray(new IConjunct[getLength()]);
    }
}