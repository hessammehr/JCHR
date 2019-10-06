package compiler.CHRIntermediateForm.arg.visitor;

import java.util.SortedSet;
import java.util.TreeSet;

import compiler.CHRIntermediateForm.variables.Variable;

public class VariableCollector extends NOPLeafArgumentVisitor {

    private SortedSet<Variable> result;
    
    public VariableCollector() {
        this(null);
    }
    
    public VariableCollector(SortedSet<Variable> variables) {
    	if (variables == null)
    		variables = new TreeSet<Variable>();
        setResult(variables);
    }
    
    @Override
    public void visit(Variable arg) {
        getResult().add(arg);
    }
    
    public SortedSet<Variable> getResult() {
        return result;
    }
    protected void setResult(SortedSet<Variable> result) {
        this.result = result;
    }
    
    public SortedSet<Variable> collectFrom(ILeafArgumentVisitable visitable) {
        try{
            visitable.accept(this);
            return getResult();
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }
    
    public static SortedSet<Variable> collectVariables(
        ILeafArgumentVisitable visitable
    ) {
        return new VariableCollector().collectFrom(visitable);
    }
    
    public static SortedSet<Variable> collectVariables(
        ILeafArgumentVisitable visitable, SortedSet<Variable> result
    ) {
        return new VariableCollector(result).collectFrom(visitable);
    }
}
