package compiler.CHRIntermediateForm.conjuncts;


import compiler.CHRIntermediateForm.Cost;
import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumented.IArgumented;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrderVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleVisitor;
import compiler.CHRIntermediateForm.rulez.GuardConjunctDecorator;
import compiler.CHRIntermediateForm.variables.Variable;

/**
 * A conjunct of an implicit guard. This guard will always be an
 * equality constraint. There are two types of implicit guard conjunct:
 * <ol>
 *  <li>A non-variable is used in an occurrence, e.g. <code>c(1)<code></li>
 *  <li>A variable is used more then once in a head, e.g. 
 *      <code>a(X), b(X)</code>. Note that it is possible this 
 *      variable is used more then once in the <em>same</em>
 *      occurrence, e.g. <code>c(X, X)</code>.
 *  </li>
 * </ol>
 * You can see that one (the first) of the arguments of an implicit guard 
 * will always be an implicit variable in an occurrence, whilst the other
 * (the second) will be:
 * <ol>
 *  <li>A non-variable.</li>
 *  <li>An explicit variable of some occurrence in the same head
 *      (possibly the same occurrence as the one the implicit variable
 *      is part of).
 *  </li>
 * </ol>
 *   
 * @author Peter Van Weert
 */
public class ImplicitGuardConjunct extends GuardConjunctDecorator {
    
    public final static int IMPLICIT_VARIABLE_INDEX = 0, OTHER_ARGUMENT_INDEX = 1;
    
    private boolean positive;
    
    ImplicitGuardConjunct(IGuardConjunct decorated, boolean positive) {
        super(decorated);
        setPositive(positive);
    }
    
    public boolean isPositive() {
        return positive;
    }
    protected void setPositive(boolean positive) {
        this.positive = positive;
    }
    
    @Override
    public void accept(IJoinOrderVisitor visitor) throws Exception {
        throw new IllegalStateException(
            "An implicit guard cannot be part of a join ordering"
        );
    }
    
    public Variable getImplicitVariable() {
        return (Variable)((IArgumented<?>)getDecorated()).
            getExplicitArgumentAt(IMPLICIT_VARIABLE_INDEX);
    }
    public IArgument getOtherArgument() {
        return ((IArgumented<?>)getDecorated()).
            getExplicitArgumentAt(OTHER_ARGUMENT_INDEX);
    }
    
    public Cost getSelectionCost() {
    	return getDecorated().getSelectionCost();
    }
    
    @Override
    public void accept(IScheduleVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    public void accept(IGuardConjunctVisitor visitor) throws Exception {
        getDecorated().accept(visitor);
    }
    public void accept(IConjunctVisitor visitor) throws Exception {
        getDecorated().accept(visitor);
    }
}