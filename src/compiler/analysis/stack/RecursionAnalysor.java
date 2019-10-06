package compiler.analysis.stack;

import runtime.BooleanAnswer;
import runtime.PrimitiveAnswerSolver;

import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.constraints.bi.IBuiltInConjunct;
import compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConjunct;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.analysis.AnalysisException;
import compiler.analysis.CifAnalysor;
import compiler.options.Options;

public class RecursionAnalysor extends CifAnalysor {
	
	private RecursionHandler recursionHandler;
	
	public RecursionAnalysor(ICHRIntermediateForm cif, Options options) {
		super(cif, options);
	}

	@Override
	public boolean doAnalysis() throws AnalysisException {
		setRecursionHandler(new RecursionHandler(new PrimitiveAnswerSolver()));
		prepConstraints();
		analyseConstraints();
		printResult();
		return getNbDetected() > 0;
	}
	
	@Override
	protected void prep(final UserDefinedConstraint constraint) throws AnalysisException {
		getRecursionHandler().tellCons(constraint);
		Rule rule = null;
		for (Occurrence occurrence : constraint.getPositiveOccurrences()) {
			if (occurrence.isActive() && occurrence.getRule() != rule) {
				rule = occurrence.getRule();
				
				try {
					rule.getBody().accept(new BasicBodyVisitor() {
						@Override
						protected void visit(IBuiltInConjunct<IBuiltInConstraint<?>> builtIn) {
							if (builtIn.warrantsStackOptimization())
								getRecursionHandler().tellRecursive(constraint);
							else
								super.visit(builtIn);
						}
						
						@Override
						protected void visitPessimisticConjunct(IConjunct conjunct) {
							getRecursionHandler().tellReactivates(constraint);
							getRecursionHandler().tellTells_exported(constraint);
						}
						@Override
						protected void visitJCHRConjunct(UserDefinedConjunct conjunct) {
							getRecursionHandler().tellTells(constraint, conjunct.getConstraint());
						}
						@Override
						protected void visitTriggeringConjunct(IConjunct conjunct) {
							getRecursionHandler().tellReactivates(constraint);	
						}
						@Override
						protected void visitJCHRFreeConjunct(IConjunct conjunct) {
							// NOP
						}
					});
				} catch (Exception e) {
					throw new RuntimeException();
				}
			}
		}
	}
	
	@Override
	protected void analyse(UserDefinedConstraint constraint) throws AnalysisException {
		if (constraint.isRecursive()) {  
			BooleanAnswer result = new BooleanAnswer();
			getRecursionHandler().tellIs_recursive(constraint, result);
			if (!result.getValue()) {
				constraint.setRecursive(false);
				incNbDetected();
			}
		}
	}
	
	protected void setRecursionHandler(RecursionHandler loopHandler) {
		this.recursionHandler = loopHandler;
	}
	protected RecursionHandler getRecursionHandler() {
		return recursionHandler;
	}
	
	
	private int nbDetected;
    
    public int getNbDetected() {
        return nbDetected;
    }
    public void incNbDetected() {
        nbDetected++;
    }
    
    protected void printResult() {
    	printResult(getNbDetected());
    }
    protected static void printResult(int nbDetected) {
        switch (nbDetected) {
            case 0: break;
            case 1: 
                System.out.println(" --> optimization: detected one non-recursive constraint");
            break;
            default:
                System.out.printf(" --> optimization: detected %d non-recursive constraints%n", nbDetected);
        }
    }
}
