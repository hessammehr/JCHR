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
public final class PrimitiveAnswerSolver {
	public PrimitiveAnswerSolver() {
		this(ConstraintSystem.get());
	}
	public PrimitiveAnswerSolver(ConstraintSystem constraintSystem) {
		this.constraintSystem = constraintSystem;
	}
	
	final ConstraintSystem constraintSystem;
	public ConstraintSystem getConstraintSystem() {
		return constraintSystem;
	}

	@JCHR_Tells(EQ)
    public void tellEqual(final IntAnswer X, final int value) {
        if (X.hasValue) {
            if (X.value != value)
                throw new FailureException("Cannot make equal: " + X.value + " != " + value);
        } else if (!constraintSystem.isQueuing()) {
        	X.hasValue = true;
            X.value = value;
        } else {
        	constraintSystem.new QueuedBuiltInConstraint() {
				@Override
				public void run() { tellEqual(X, value); }
			};
        }
    }
	
	@JCHR_Tells(EQ)
    public void tellEqual(final IntAnswer X, final IntAnswer Y) {
		if (X.hasValue)
			tellEqual(X.value, Y);
		else if (Y.hasValue)
			tellEqual(X, Y.value);
		else
			throw new InstantiationException();
    }

	@JCHR_Tells(EQ)
    public void tellEqual(final int value, final IntAnswer Y) {
        if (Y.hasValue) {
            if (Y.value != value)
                throw new FailureException("Cannot make equal: " + value + " != " + Y.value);
        } else if (!constraintSystem.isQueuing()) {
        	Y.hasValue = true;
            Y.value = value;
        } else {
        	constraintSystem.new QueuedBuiltInConstraint() {
				@Override
				public void run() { tellEqual(value, Y); }
			};
        }
    }

	@JCHR_Asks(EQ)
    public boolean askEqual(IntAnswer X, int value) {
        return X.hasValue && X.value == value;
    }
	@JCHR_Asks(EQ)
    public boolean askEqual(int value, IntAnswer Y) {
		return Y.hasValue && Y.value == value;
    }
	@JCHR_Asks(EQ)
    public boolean askEqual(IntAnswer X, IntAnswer Y) {
        return (X == Y) || (X.hasValue && Y.hasValue && X.value == Y.value);
    }
	
	
	@JCHR_Tells(EQ)
    public void tellEqual(final BooleanAnswer X, final boolean value) {
        if (X.hasValue) {
            if (X.value != value)
                throw new FailureException("Cannot make equal: " + X.value + " != " + value);
        } else if (!constraintSystem.isQueuing()) {
        	X.hasValue = true;
            X.value = value;
        } else {
        	constraintSystem.new QueuedBuiltInConstraint() {
				@Override
				public void run() { tellEqual(X, value); }
			};
        }
    }

	@JCHR_Tells(EQ)
    public void tellEqual(final boolean value, final BooleanAnswer Y) {
        if (Y.hasValue) {
            if (Y.value != value)
                throw new FailureException("Cannot make equal: " + value + " != " + Y.value);
        } else if (!constraintSystem.isQueuing()) {
        	Y.hasValue = true;
            Y.value = value;
        } else {
        	constraintSystem.new QueuedBuiltInConstraint() {
				@Override
				public void run() { tellEqual(value, Y); }
			};
        }
    }
	
	@JCHR_Tells(EQ)
    public void tellEqual(final BooleanAnswer X, final BooleanAnswer Y) {
		if (X.hasValue)
			tellEqual(X.value, Y);
		else if (Y.hasValue)
			tellEqual(X, Y.value);
		else
			throw new InstantiationException();
    }

	@JCHR_Asks(EQ)
    public boolean askEqual(BooleanAnswer X, boolean value) {
        return X.hasValue && X.value == value;
    }
	@JCHR_Asks(EQ)
    public boolean askEqual(boolean value, BooleanAnswer Y) {
		return Y.hasValue && Y.value == value;
    }
	@JCHR_Asks(EQ)
    public boolean askEqual(BooleanAnswer X, BooleanAnswer Y) {
        return (X == Y) || (X.hasValue && Y.hasValue && X.value == Y.value);
    }	
}