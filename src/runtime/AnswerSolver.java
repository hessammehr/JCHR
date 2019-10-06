package runtime;

import static annotations.JCHR_Constraint.Value.NO;
import static annotations.JCHR_Constraint.Value.YES;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQ;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQi;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQi2;
import annotations.JCHR_Asks;
import annotations.JCHR_Constraint;
import annotations.JCHR_Constraints;
import annotations.JCHR_Tells;

@JCHR_Constraints({
    @JCHR_Constraint(
        identifier = EQ,
        arity = 2,
        ask_infix = {EQi, EQi2},
        tell_infix = EQi,
        idempotent = YES,
        triggers = NO
    )
})
public final class AnswerSolver<Type> {
	public AnswerSolver() {
		this(ConstraintSystem.get());
	}
	public AnswerSolver(ConstraintSystem constraintSystem) {
		this.constraintSystem = constraintSystem;
	}
	
	final ConstraintSystem constraintSystem;
	public ConstraintSystem getConstraintSystem() {
		return constraintSystem;
	}

	@JCHR_Tells(EQ)
    public void tellEqual(final Answer<Type> X, final Type value) {
        final Type oldValue = X.value; 

        if (oldValue != null) {
            if (!oldValue.equals(value))
                throw new FailureException("Cannot make equal: " + oldValue + " != " + value);
        } else if (!constraintSystem.isQueuing()) {
            X.value = value;
        } else {
        	constraintSystem.new QueuedBuiltInConstraint() {
				@Override
				public void run() { tellEqual(X, value); }
			};
        }
    }
	
	@JCHR_Tells(EQ)
    public void tellEqual(final Type value, final Answer<Type> Y) {
        final Type oldValue = Y.value; 

        if (oldValue != null) {
            if (!oldValue.equals(value))
                throw new FailureException("Cannot make equal: " + value + " != " + oldValue);
        } else if (!constraintSystem.isQueuing()) {
            Y.value = value;
        } else {
        	constraintSystem.new QueuedBuiltInConstraint() {
				@Override
				public void run() { tellEqual(value, Y); }
			};
        }
    }
	

	@JCHR_Asks(EQ)
    public boolean askEqual(Answer<Type> X, Type value) {
        return value.equals(X.value);
    }
	@JCHR_Asks(EQ)
    public boolean askEqual(Type value, Answer<Type> Y) {
        return value.equals(Y.value);
    }
	@JCHR_Asks(EQ)
    public boolean askEqual(Answer<Type> X, Answer<Type> Y) {
        return (X == Y) || (X.value != null && X.value.equals(Y.value));
    }
}