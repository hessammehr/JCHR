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
public interface BooleanEqualitySolver {
    @JCHR_Tells(EQ)
    public void tellEqual(LogicalBoolean X, boolean val);
    @JCHR_Tells(EQ)
    public void tellEqual(boolean val, LogicalBoolean X);
    @JCHR_Tells(EQ)
    public void tellEqual(LogicalBoolean X, LogicalBoolean Y);
    
    @JCHR_Asks(EQ)
    public boolean askEqual(LogicalBoolean X, boolean val);
    @JCHR_Asks(EQ)
    public boolean askEqual(boolean val, LogicalBoolean X);
    @JCHR_Asks(EQ)
    public boolean askEqual(LogicalBoolean X, LogicalBoolean Y);
}