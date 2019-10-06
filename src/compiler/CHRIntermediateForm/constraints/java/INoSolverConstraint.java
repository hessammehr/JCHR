package compiler.CHRIntermediateForm.constraints.java;

import compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint;

/**
 * A no solver constraint is a constraint that does not require the explicit
 * use of a built-in solver to be asked and/or told.
 * 
 * @author Peter Van Weert
 */
public interface INoSolverConstraint<T extends INoSolverConstraint<?>> 
extends IBuiltInConstraint<T> {
    // no new methods
}