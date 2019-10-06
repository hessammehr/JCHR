package runtime;

import static annotations.JCHR_Constraint.Value.YES;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQ;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQi;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQi2;
import annotations.JCHR_Asks;
import annotations.JCHR_Constraint;
import annotations.JCHR_Constraints;
import annotations.JCHR_Tells;

/**
 * @author Peter Van Weert
 */
//@WakeConditions( {"touched", "bound"} )
@JCHR_Constraints({
    @JCHR_Constraint(
        identifier = EQ,
        arity = 2,
        ask_infix = {EQi, EQi2},
        tell_infix = EQi,
        idempotent = YES
    )
})
public interface EqualitySolver<T> {
    @JCHR_Tells(EQ)
    public void tellEqual(Logical<T> X, T val);
    @JCHR_Tells(EQ)
    public void tellEqual(T val, Logical<T> X);
    @JCHR_Tells(EQ)
    public void tellEqual(Logical<T> X, Logical<T> Y);
    
//    @WakesList( "bound" )
    @JCHR_Asks(EQ)
    public boolean askEqual(Logical<T> X, T val);
//  @WakesList( "bound" )
    @JCHR_Asks(EQ)
    public boolean askEqual(T val, Logical<T> X);
    
//    @WakesList( { "bound", "touched" } )
    @JCHR_Asks(EQ)
    public boolean askEqual(Logical<T> X, Logical<T> Y);
}