package runtime;

import static annotations.JCHR_Constraint.Value.YES;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQ;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQi;
import static compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint.EQi2;

import java.util.Iterator;

import annotations.JCHR_Asks;
import annotations.JCHR_Constraint;
import annotations.JCHR_Tells;

@JCHR_Constraint(
    identifier = EQ,
    arity = 2,
    ask_infix = {EQi, EQi2},
    tell_infix = EQi,
    idempotent = YES
)
public final class FreeLogicalEqualitySolver {
	
	public FreeLogicalEqualitySolver() {
		this(ConstraintSystem.get());
	}
	public FreeLogicalEqualitySolver(ConstraintSystem constraintSystem) {
		this.constraintSystem = constraintSystem;
	}
	
	final ConstraintSystem constraintSystem;
	public ConstraintSystem getConstraintSystem() {
		return constraintSystem;
	}

	@JCHR_Tells(EQ)
    public void tellEqual(FreeLogical X, FreeLogical Y) {
        if (X == Y) return;
        
        final FreeLogical Xrepr = X.find();
        final FreeLogical Yrepr = Y.find();

        if (Xrepr != Yrepr) {
        	if (!constraintSystem.isQueuing()) {
	
	            final int Xrank = Xrepr.rank;
	            int Yrank = Yrepr.rank;
	
	            if (Xrank >= Yrank) {
	                Yrepr.parent = Xrepr;
	                if (Xrank == Yrank) Xrepr.rank++;
	
                    if (Yrepr.hashObservers != null) {
                        Xrepr.mergeHashObservers(Yrepr.hashObservers);
                        Yrepr.hashObservers = null;
                    }
                    
                    final DoublyLinkedConstraintList<Constraint> Xobs, Yobs;
                    if ((Yobs = Yrepr.variableObservers) != null) {
                        Yrepr.variableObservers = null;
                        if ((Xobs = Xrepr.variableObservers) != null) {
                        	final Iterator<Constraint> iter = Xobs.iterator();
                        	Xobs.mergeWith(Yobs);
                            while (iter.hasNext()) 
                                iter.next().reactivate();   /* notify */
                        } else {
                            Xrepr.variableObservers = Yobs;
                        }
                    }
	            } else {
	                Xrepr.parent = Yrepr;
	
                    if (Xrepr.hashObservers != null) {
                        Yrepr.mergeHashObservers(Xrepr.hashObservers);
                        Xrepr.hashObservers = null;
                    }
                    
                    final DoublyLinkedConstraintList<Constraint> Xobs, Yobs;
                    if ((Xobs = Xrepr.variableObservers) != null) {
                        Xrepr.variableObservers = null;
                        if ((Yobs = Yrepr.variableObservers) != null) {
                            final Iterator<Constraint> iter = Yobs.iterator();
                        	Yobs.mergeWith(Xobs);
                            while (iter.hasNext()) 
                                iter.next().reactivate();   /* notify */
                        } else {
                            Yrepr.variableObservers = Xobs;
                        }
                    }
	            }
        	} else {
        		constraintSystem.new QueuedBuiltInConstraint() {
    				@Override
    				public void run() { tellEqual(Xrepr, Yrepr); }
    			};
        	}
        }
    }

	@JCHR_Asks(EQ)
    public boolean askEqual(FreeLogical X, FreeLogical Y) {
        return (X == Y) || (X.find() == Y.find());
    }
}