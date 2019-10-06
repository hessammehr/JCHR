package compiler.analysis.passiveness;

import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.REMOVED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.comparing.LexComparator;

import compiler.CHRIntermediateForm.ICHRIntermediateForm;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.CHRIntermediateForm.variables.FormalVariable;
import compiler.CHRIntermediateForm.variables.IActualVariable;
import compiler.CHRIntermediateForm.variables.NamelessVariable;
import compiler.analysis.AnalysisException;
import compiler.options.Options;

/**
 * A relatively basic symmetry checker (detects passive 
 * occurrences). More advanced subsumption analysis is possible, 
 * but requires entailment checking and/or partial evaluation.
 * Nonetheless, this checker is already capable of detecting
 * many of the more frequently occurring subsumptions. 
 * For example: for the classical leq solver it will find all 
 * two passive occurrences; in the bool solver it detects
 * no less than nine passive occurrences.
 * 
 * @author Peter Van Weert
 */
public class SymmetryAnalysis extends PassivenessAnalysis {

    SymmetryAnalysis(ICHRIntermediateForm intermediateForm, Options options) {
        super(intermediateForm, options);
    }
    
    @Override
    protected void doPassivenessAnalysis() throws AnalysisException {
        analyseRules();
    }
    
    @Override
    protected void analyse(Rule rule) {
        Occurrence[] occurrences 
            = rule.getPositiveHead().getOccurrencesArray();
                
        for (int i = occurrences.length-2; i >= 0; i--) {
            if (occurrences[i].isPassive()) continue;
            for (int j = i+1; j < occurrences.length; j++) {
                if (isSubsumedBy(occurrences[i], occurrences[j])) {
                    incNbDetected();
                    occurrences[i].setPassive();
                }
            }
        }
    }
    
    protected static boolean isSubsumedBy(Occurrence one, Occurrence other) {
        // a passive occurrence cannot subsume another occurrence:
        if (other.isPassive()) return false;
        
        // a necessary condition for subsumption of course (it might subsume, 
        // but we don't care, that active constraint is still alive):
        if (other.getType() != REMOVED) return false;
        
        // first of course, it has to be the same constraint:
        if (one.getConstraint() != other.getConstraint())
            return false;
        
        // of course...
        if (one.getArguments().equals(other.getArguments()))
            return true;
        
        // the more complicated case:
        return symmetric(one, other);
    }
    
    protected static boolean symmetric(Occurrence one, Occurrence other) {
        // for now: only one trivial case!
        if (other.isExplicitlyGuarded()) return false;
        
        Map<IActualVariable, FormalVariable> 
            oneMap = getVariableMap(one),
            otherMap = getVariableMap(other);
        
        // intra-constraint-implicit-guards should be equal:
        if (!Arrays.equals(map(one, oneMap), map(other, otherMap)))
            return false;
        
        // note that for other partners the intra-constraint-guards
        // do not matter: both one and other will look them up!
        
        List<Occurrence> occurrences = new ArrayList<Occurrence>();
        UserDefinedConstraint constraint = one.getConstraint(); 
        for (Occurrence occurrence : one.getHead())
            if (occurrence.getConstraint() == constraint)
                occurrences.add(occurrence);
        
        return Arrays.deepEquals(
            map(occurrences, one, oneMap), map(occurrences, other, otherMap)
        );
    }

    protected static Map<IActualVariable, FormalVariable> getVariableMap(Occurrence occurrence) {
        int arity = occurrence.getArity();
        Map<IActualVariable, FormalVariable> map
            = new HashMap<IActualVariable, FormalVariable>(arity);
        for (int i = 0; i < arity; i++) {
            IActualVariable variable = occurrence.getArgumentAt(i);
            
            if (variable != NamelessVariable.getInstance() 
                    && !map.containsKey(variable))
                map.put(variable, occurrence.getFormalVariableAt(i));
        }
        return map;
    }
    
    protected static FormalVariable[] map(Occurrence occurrence, Map<IActualVariable, FormalVariable> map) {
        int arity = occurrence.getArity();
        FormalVariable[] result = new FormalVariable[arity];
        for (int i = 0; i < arity; i++)
            result[i] = map.get(occurrence.getArgumentAt(i));
        return result;
    }
    
    protected static FormalVariable[][] map(List<Occurrence> occurrences, Occurrence excluded, Map<IActualVariable, FormalVariable> map) {
        FormalVariable[][] result = new FormalVariable[occurrences.size()-1][];
        int i = 0;
        for (Occurrence occurrence : occurrences)
            if (occurrence != excluded) 
                result[i++] = map(occurrence, map);
        Arrays.sort(result, LexComparator.<FormalVariable>getInstance());
        return result;
    }
}