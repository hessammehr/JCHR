package compiler.analysis;

import static compiler.analysis.JCHRFreeAnalysor.hasJCHRFreeArguments;
import static compiler.analysis.JCHRFreeAnalysor.isJCHRFree;

import compiler.CHRIntermediateForm.CHRIntermediateFormDecorator;
import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.conjuncts.AbstractConjunctVisitor;
import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.constraints.bi.Failure;
import compiler.CHRIntermediateForm.constraints.bi.IBuiltInConjunct;
import compiler.CHRIntermediateForm.constraints.bi.IBuiltInConstraint;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConjunct;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.init.InitialisatorMethodInvocation;
import compiler.CHRIntermediateForm.members.FieldAccess;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.CHRIntermediateForm.variables.Variable;
import compiler.options.Options;

public abstract class CifAnalysor 
    extends CHRIntermediateFormDecorator 
    implements Runnable {
    
    private Options options;
    
    public CifAnalysor(ICHRIntermediateForm cif, Options options) {
        super(cif);
        setOptions(options);
    }
    
    public Options getOptions() {
        return options;
    }
    public void setOptions(Options options) {
        this.options = options;
    }
    
    protected void prepRules() throws AnalysisException {
        for (Rule rule : getRules()) prep(rule);
    }
    protected void prep(Rule rule) throws AnalysisException {
        throw new UnsupportedOperationException("not implemented");
    }
    
    protected void analyseRules() throws AnalysisException {
        for (Rule rule : getRules()) analyse(rule);
    }
    protected void analyseRulesWith(UserDefinedConstraint constraint) throws AnalysisException {
    	analyseRulesFrom(constraint.getPositiveOccurrences());
    }
    protected void analyseRulesFrom(Iterable<Occurrence> occurrences) throws AnalysisException {
    	Rule previous = null;
        for (Occurrence occurrence : occurrences) {
            Rule rule = occurrence.getRule();
            if (rule == previous) continue;
            analyse(rule);
        }
    }
    protected void analyse(Rule rule) throws AnalysisException {
        throw new UnsupportedOperationException("not implemented");
    }
    
    protected void prepConstraints() throws AnalysisException {
        for (UserDefinedConstraint constraint : getUserDefinedConstraints()) 
            prep(constraint);
    }
    protected void prep(UserDefinedConstraint constraint) throws AnalysisException {
        throw new RuntimeException("not implemented");
    }
    
    protected void analyseConstraints() throws AnalysisException {
        for (UserDefinedConstraint constraint : getUserDefinedConstraints()) 
            analyse(constraint);
    }
    protected void analyse(UserDefinedConstraint constraint) throws AnalysisException {
        throw new RuntimeException("not implemented");
    } 
    
    public abstract boolean doAnalysis() throws AnalysisException;
    
    public void run() {
        try {
            doAnalysis();
        } catch (AnalysisException e) {
            e.printStackTrace();
        }
    }
    
    public static abstract class AbstractBodyVisitor extends AbstractConjunctVisitor {
        @Override
        public abstract void visit(UserDefinedConjunct conjunct);
                            
        @Override
        public void visit(Failure __) throws IllegalStateException {
            // NOP
        }
        @Override
        public final void visit(FieldAccess __) throws IllegalStateException {
        	throw new IllegalStateException();
        }
        @Override
        public final void visit(Variable __) throws IllegalStateException {
        	throw new IllegalStateException();
        }
        @Override
        public final void visit(InitialisatorMethodInvocation conjunct) throws Exception {
        	throw new IllegalStateException();
        }
        
        public void payVisitTo(Rule rule) {
            try {
                rule.getBody().accept(this);
            } catch (Exception x) {
                throw new RuntimeException(x);
            }
        }
    }
    
    public static abstract class BasicBodyVisitor extends AbstractBodyVisitor {
		@Override
		@SuppressWarnings("unchecked")
    	protected void visit(IConjunct conjunct) throws Exception {
    		if (conjunct instanceof IBuiltInConjunct<?>) {
    			visit((IBuiltInConjunct<IBuiltInConstraint<?>>)conjunct);
    		} else if (isJCHRFree(conjunct, false)) {
    			visitJCHRFreeConjunct(conjunct);
    		} else {
    			visitPessimisticConjunct(conjunct);
    		}
    	}

		protected void visit(IBuiltInConjunct<IBuiltInConstraint<?>> builtIn) {
			if (builtIn.getConstraint().triggersConstraints())
				if (hasJCHRFreeArguments(builtIn))
					visitTriggeringConjunct(builtIn);
				else
					visitPessimisticConjunct(builtIn);
			else
				visitPessimisticConjunct(builtIn);
		}
		
		@Override
		public void visit(UserDefinedConjunct conjunct) {
			visitJCHRConjunct(conjunct);
			if (!hasJCHRFreeArguments(conjunct)) visitPessimisticConjunct(conjunct);
		}

    	protected abstract void visitPessimisticConjunct(IConjunct conjunct);
    	protected abstract void visitTriggeringConjunct(IConjunct conjunct);
    	protected abstract void visitJCHRFreeConjunct(IConjunct conjunct);
    	protected abstract void visitJCHRConjunct(UserDefinedConjunct conjunct);
    }
    
    protected void raiseWarning(String message) {
    	System.err.print(" --> warning: ");
    	System.err.println(message);
    }
}
