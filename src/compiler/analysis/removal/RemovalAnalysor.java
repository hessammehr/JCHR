package compiler.analysis.removal;

import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.REMOVED;
import static compiler.CHRIntermediateForm.modifiers.Modifier.isExported;

import java.util.HashSet;
import java.util.Set;

import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConjunct;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.rulez.Body;
import compiler.CHRIntermediateForm.rulez.Head;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.analysis.AnalysisException;
import compiler.analysis.CifAnalysor;
import compiler.analysis.removal.RemovalHandler.RemovesConstraint;
import compiler.options.Options;

/*
 * TODO:
 * 	- onderscheid built-in constraints (reactivate) en gewone code
 * 		(tellsAll). Hoewel: eigenlijk kan een built-in iets
 * 		externs activeren, en dus ook tellsAll...
 * 	- eigenlijk kan gelijk welke externe code gelijk welke niet-private
 * 		of -lokale constraint verwijderen (terminate() is public).  
 */
public class RemovalAnalysor extends CifAnalysor {

	private RemovalHandler removalHandler;
	
	public RemovalAnalysor(ICHRIntermediateForm cif, Options options) {
		super(cif, options);
		setRemovalHandler(new RemovalHandler());
	}

	@Override
	public boolean doAnalysis() throws AnalysisException {
		prepConstraints();
		prepRules();
		analyseConstraints();
		return true;
	}
	
	@Override
	protected void prep(UserDefinedConstraint constraint) throws AnalysisException {
		if (constraint.isReactive() || isExported(constraint))
			getRemovalHandler().tellExported(constraint);
	}

	@Override
	protected void prep(Rule rule) throws AnalysisException {
		Head head = rule.getPositiveHead();
		Body body = rule.getBody();
		for (Occurrence one : head) if (one.isActive()) {
			final UserDefinedConstraint constraint = one.getConstraint(); 
			try {
				body.accept(new BasicBodyVisitor() {
					@Override
					protected void visitJCHRFreeConjunct(IConjunct conjunct) {
						// NOP
					}					
					@Override
					protected void visitJCHRConjunct(UserDefinedConjunct conjunct) {
						getRemovalHandler().tellTells(constraint, conjunct.getConstraint());
					}
					@Override
					protected void visitPessimisticConjunct(IConjunct conjunct) {
						getRemovalHandler().tellTells_exported(constraint);
					}
					@Override
					protected void visitTriggeringConjunct(IConjunct conjunct) {
						getRemovalHandler().tellTells_reactive(constraint);
					}
				});
			} catch (Exception e) {
				throw new InternalError();
			}
			
			for (Occurrence other : head)
				if (one != other && other.getType() == REMOVED)
					getRemovalHandler().tellRemoves(constraint, other.getConstraint());
		}
	}
	
	@Override
	protected void analyse(UserDefinedConstraint constraint) throws AnalysisException {
		Set<UserDefinedConstraint> removees 
			= new HashSet<UserDefinedConstraint>(getNbUdConstraints());
		for (RemovesConstraint removes : getRemovalHandler().getRemovesConstraints())
			if (removes.getRemover() == constraint)
				removees.add(removes.getRemovee());
		constraint.setRemovees(removees);
	}
	
	protected RemovalHandler getRemovalHandler() {
		return removalHandler;
	}
	protected void setRemovalHandler(RemovalHandler removalHandler) {
		this.removalHandler = removalHandler;
	}
}