package runtime.primitive;

import static annotations.JCHR_Constraint.Value.YES;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQ;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQi;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQi2;

import annotations.*;

/**
 * @author Peter Van Weert
 */
@JCHR_Constraints({
    @JCHR_Constraint(
        identifier = EQ,
        arity = 2,
        ask_infix = {EQi, EQi2},
        tell_infix = EQi,
        idempotent = YES
    )
})
public interface IntEqualitySolver {
    @JCHR_Tells(EQ)
    public void tellEqual(LogicalInt X, int val);
    @JCHR_Tells(EQ)
    public void tellEqual(int val, LogicalInt X);
    @JCHR_Tells(EQ)
    public void tellEqual(LogicalInt X, LogicalInt Y);
    
    @JCHR_Asks(EQ)
    public boolean askEqual(LogicalInt X, int val);
    @JCHR_Asks(EQ)
    public boolean askEqual(int val, LogicalInt X);
    @JCHR_Asks(EQ)
    public boolean askEqual(LogicalInt X, LogicalInt Y);
}