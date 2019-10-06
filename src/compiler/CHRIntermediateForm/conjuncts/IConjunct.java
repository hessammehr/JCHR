package compiler.CHRIntermediateForm.conjuncts;

import java.util.SortedSet;

import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitable;
import compiler.CHRIntermediateForm.variables.Variable;

/**
 * @author Peter Van Weert
 */
public interface IConjunct extends IArgumentVisitable, IConjunctVisitable {
    
    public SortedSet<Variable> getVariables();
    
    public int getNbVariables();
}
