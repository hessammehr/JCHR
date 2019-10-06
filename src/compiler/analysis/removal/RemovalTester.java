package compiler.analysis.removal;

import static compiler.CHRIntermediateForm.modifiers.Modifier.isExported;

import java.util.Collection;

import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConjunct;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.rulez.Body;
import compiler.analysis.CifAnalysor.BasicBodyVisitor;

public class RemovalTester extends BasicBodyVisitor {
	private final Collection<UserDefinedConstraint> constraints;
	private final UserDefinedConstraint constraint;
		
	protected RemovalTester(
		Collection<UserDefinedConstraint> constraints,
		UserDefinedConstraint constraint
	) {
		this.constraints = constraints;
		this.constraint = constraint;
	}
	
	private boolean result;
	
	@Override
	protected void visitJCHRConjunct(UserDefinedConjunct conjunct) {
		if (!getResult())
			setResult(conjunct.getConstraint().removes(getConstraint()));
	}
	@Override
	protected void visitPessimisticConjunct(IConjunct conjunct) {
		if (!getResult()) { 
			for (UserDefinedConstraint constraint : getConstraints()) {
				if ((isExported(constraint) || constraint.isReactive()) 
						&& constraint.removes(getConstraint())) {
					setResult(true);
					break;
				}
			}
		}
	}
	@Override
	protected void visitTriggeringConjunct(IConjunct conjunct) {
		if (!getResult()) { 
			for (UserDefinedConstraint constraint : getConstraints()) {
				if (constraint.isReactive() && constraint.removes(getConstraint())) {
					setResult(true);
					break;
				}
			}
		}
	}
	@Override
	protected void visitJCHRFreeConjunct(IConjunct conjunct) {
		// NOP
	}

	public Collection<UserDefinedConstraint> getConstraints() {
		return constraints;
	}
	public UserDefinedConstraint getConstraint() {
		return constraint;
	}
	
	protected void setResult(boolean result) {
		this.result = result;
	}
	public boolean getResult() {
		return result;
	}

	public static boolean mayRemove(Body body, Occurrence occurrence, ICHRIntermediateForm cif) {
		return mayRemove(body, occurrence.getConstraint(), cif);
	}
	public static boolean mayRemove(Body body, UserDefinedConstraint constraint, ICHRIntermediateForm cif) {
		try {	
			RemovalTester visitor =
				new RemovalTester(cif.getUserDefinedConstraints(), constraint);
			body.accept(visitor);
			return visitor.getResult();
	
		} catch (Exception e) {
			e.printStackTrace();
			return true;	// conservative (should never happen though)
		}
	}
}