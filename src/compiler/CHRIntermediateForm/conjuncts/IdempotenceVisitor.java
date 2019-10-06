package compiler.CHRIntermediateForm.conjuncts;

import compiler.CHRIntermediateForm.constraints.IConstraintConjunct;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConjunct;

public class IdempotenceVisitor extends AbstractConjunctVisitor {
	
	/**
	 * @see #isIdempotent(IConjunctVisitable)
	 */
	protected IdempotenceVisitor() {
		// assume idempotent until proven otherwise
		setResult(true);
	}
	
	public static boolean isIdempotent(IConjunctVisitable conjunction) {
		try {
			IdempotenceVisitor visitor = new IdempotenceVisitor();
			conjunction.accept(visitor);
			return visitor.getResult();
		} catch (Exception x) {
			throw new InternalError();
		}
	}
	
	private boolean result;
	
	protected void setResult(boolean result) {
		this.result = result;
	}
	
	public boolean getResult() {
		return result;
	}
	
	@Override
	protected void visit(IConjunct conjunct) throws Exception {
		// conservative:
		setResult(false);
	}
	
	@Override
	public void visit(UserDefinedConjunct conjunct) throws Exception {
		visit((IConstraintConjunct)conjunct);
	}
	
	@Override
	protected void visit(IConstraintConjunct<?> conjunct) throws Exception {
		// TODO: the constraints argument could in principle cause side effects!
		if (!conjunct.getConstraint().isIdempotent())
			setResult(false);
	}
}
