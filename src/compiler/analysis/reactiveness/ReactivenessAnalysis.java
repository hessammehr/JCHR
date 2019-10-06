package compiler.analysis.reactiveness;

import java.util.Set;

import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.variables.Variable;
import compiler.analysis.AnalysisException;
import compiler.analysis.CifAnalysor;
import compiler.options.Options;

public class ReactivenessAnalysis extends CifAnalysor {

    public ReactivenessAnalysis(ICHRIntermediateForm cif, Options options) {
        super(cif, options);
    }

    @Override
    public boolean doAnalysis() throws AnalysisException {
        prepConstraints();
        analyseConstraints();
        
        return true;
    }
    
    @Override
    protected void prep(UserDefinedConstraint constraint) throws AnalysisException {
        if (constraint.canNeverBeReactived()) return;
        
        // first assume all variables unreactive, reset where needed during analysis:
        for (Occurrence occurrence : constraint.getPositiveOccurrences())
            for (Variable variable : occurrence.getVariables())
                variable.setUnreactive();
    }
    
    @Override
    protected void analyse(UserDefinedConstraint constraint) throws AnalysisException {
        if (constraint.canNeverBeReactived()) return;
        
        boolean stored = false, yes = false, no = false;
        for (Occurrence occurrence : constraint.getPositiveOccurrences()) {
            if (occurrence.isStored()) stored = true;
            if (!occurrence.isReactive()) continue;
            if (isUnreactive(occurrence)) {
                occurrence.setUnreactive();
                if (stored) no = true;
            } else {
                if (stored) yes = true;
            }
        }
        
        // for now, we maintain refined semantics by resetting
        // some results if a problematic occurrence is found
        // (i.e. if there is a stored occurrence after which
        // some occurrences are reactivated, and some are not:
        // this clashes with the generation optimization)
        if (yes && no) {
            stored = false;
            for (Occurrence occurrence : constraint.getPositiveOccurrences()) {
                if (occurrence.isStored()) stored = true;
                if (stored) occurrence.resetReactiveness();
            }
        }
    }
    
    protected static boolean isUnreactive(Occurrence occurrence) {
        Variable[] unfixed = getUnfixedVariables(occurrence);
        if (unfixed.length == 0) return true;
        boolean result = analyseImplicitGuards(occurrence, unfixed);
        // keep analysing: everything needs to be reset where needed!
        for (Variable variable : unfixed) {
            if (variable.isReactive()) {
            	result = false;
            	continue;
            }
            if (occurrence.isExplicitlyGuardedOn(variable)) {
                variable.resetReactiveness();
                result = false;
            }
        }
        return result;
    }

    private static Variable[] getUnfixedVariables(Occurrence occurrence) {
        Set<Variable> variables = occurrence.getVariables();
        int numVars = variables.size();
        Variable[] unfixed = new Variable[numVars];
        int num = 0;        
        for (Variable variable : variables)
            if (! variable.isFixed())
                unfixed[num++] = variable;        
        if (num == numVars) return unfixed;
        Variable[] result = new Variable[num];
        if (num != 0) 
            System.arraycopy(unfixed, 0, result, 0, num);
        return result;
    }
    
    /**
     * @return <code>true</code> iff there were <em>no</em> reactive implicit 
     *  guards found
     */
    private static boolean analyseImplicitGuards(Occurrence occurrence, Variable[] unfixed) {
        IArguments arguments = occurrence.getArguments();
        int arity = arguments.getArity();
        
        boolean result = true;
        
        // test for intra-constraint unfixed implicit guards:
        if (occurrence.getNbVariables() != arity) {
            variable: for (Variable variable : unfixed) {
                // could be the reactiveness has been reset when 
                // analyzing another occurrence:
                if (variable.isReactive()) { 
                    result = false; 
                    continue; 
                }
                
                boolean before = false;
                for (int i = 0; i < arity; i++) {
                    if (arguments.getArgumentAt(i) == variable) {
                        if (before) {
                            result = false;
                            variable.resetReactiveness();
                            continue variable;
                        }
                        before = true;
                    }
                }
            }
        }
        
        // test for inter-constraint unfixed implicit guards:
        for (Occurrence partner : occurrence.getHead()) {
            if (partner == occurrence) continue;
            Set<Variable> variables = partner.getVariables();
            for (Variable variable : unfixed) {
                if (variable.isReactive()) {
                    result = false;
                    continue;
                }
                if (variables.contains(variable)) {
                    result = false;
                    variable.resetReactiveness();
                    continue;
                }
            }
        }
        
        return result;
    }
}