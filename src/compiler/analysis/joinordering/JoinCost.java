package compiler.analysis.joinordering;

import java.util.HashSet;
import java.util.Set;

import util.Cloneable;
import util.Resettable;
import util.Terminatable;

import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.schedule.AbstractJoinOrderVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrder;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.variables.Variable;

/**
 * A cost class to be used in join-ordering heuristics based on the 
 * descriptions in the HAL optimization papers.
 */
class JoinCost implements Comparable<JoinCost>, Cloneable<JoinCost>, Resettable {
    
    public final static JoinCost MAX_VALUE 
        = new JoinCost(Integer.MAX_VALUE, Integer.MAX_VALUE);
    
    private float u, f;
    
    public JoinCost() {
        this(0,0);
    }
    
    public JoinCost(float u, float f) {
        this.u = u;
        this.f = f;
    }
    
    public void reset() {
        this.u = 0;
        this.f = 0;
    }
    
    public void set(float u, float f) {
        this.u = u;
        this.f = f;
    }
    public void set(JoinCost other) {
        this.u = other.getU();
        this.f = other.getF();
    }
    
    public void add(float u, float f) {
        this.u += u;
        this.f += f;
    }
    public void add(JoinCost cost) {
        this.u += cost.getU();
        this.f += cost.getF();
    }
    
    public void subtract(float u, float f) {
        this.f -= f;
        this.u -= u;
    }
    public void subtract(JoinCost cost) {
        this.f -= cost.getF();
        this.u -= cost.getU();
    }
    
    public int compareTo(JoinCost other) {
        float diff = this.getU() - other.getU();
        return (int)Math.signum((diff == 0)? this.getF() - other.getF() : diff);
    }
    
    public float getF() {
        return f;
    }
    
    public float getU() {
        return u;
    }
    
    @Override
    public String toString() {
        return new StringBuilder()
            .append('(').append(getU()).append(", ").append(getF()).append(')')
            .toString();
    }
    
    @Override
    public JoinCost clone() {
        try {
            return (JoinCost)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}    

class SCost extends JoinCost {
    private float s;
    
    public SCost() {
        this(0, 0, 0);
    }
    
    public SCost(float u, float f) {
        this(u, f, 0);
    }
    
    public SCost(float u, float f, float s) {
        super(u, f);
        this.s = s;
    }
    
    public void set(float u, float f, float s) {
        super.set(u, f);
        this.s = s;
    }
    
    public void add(float s) {
        this.s += s;
    }
    
    public void subtract(float s) {
        this.s -= s;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.s = 0;
    }
    
    @Override
    public float getU() {
        float result = super.getU() - s;
        return (result < 0)? 0 : result;
    }
    
    @Override
    public float getF() {
        return super.getF() - s;
    }
}

class ScoreCalculator extends AbstractJoinOrderVisitor implements Terminatable {
    private JoinCost sum = new JoinCost();
    private JoinCost score = new JoinCost();
    private SCost cost = new SCost();
    
    private boolean firstLookup;
    
    private Set<Variable> initiallyFixed;
    private Set<Variable> alsoFixed = new HashSet<Variable>();
    
    public ScoreCalculator(Set<Variable> fixedVariables) {
        initiallyFixed = fixedVariables;
    }
    
    @Override
    public void visit(NegativeHead negativeHead) throws Exception {
        if (firstLookup) 
            cost.add(negativeHead.getSelectivity());
    }
        
    @Override
    public void visit(IGuardConjunct explicitGuard) throws Exception {
        if (firstLookup) 
            cost.add(explicitGuard.getSelectivity());
    }
        
    @Override
    public void visit(Occurrence occurrence) throws Exception {
        firstLookup = true;
        
        sum.add(cost);
        score.add(sum);
        cost.reset();
        
        Set<Variable> variables = occurrence.getVariables(); 
        int unfixed = 0;
        for (Variable variable : variables)
            if (!initiallyFixed.contains(variable) && alsoFixed.add(variable)) 
                unfixed++;
        
        cost.add(unfixed, unfixed - variables.size());
    }
    
    public void terminate() {
        sum.add(cost);
        score.add(sum);
        cost = null;
        sum = null;
    }
    
    public boolean isTerminated() {
        return (cost == null);
    }
    
    public JoinCost getScore() {
        return score;
    }
    
    public static JoinCost calculateScore(
        Set<Variable> fixedVariables, IJoinOrder schedule
    ) {
        try {
            ScoreCalculator calculator = new ScoreCalculator(fixedVariables);
            schedule.accept(calculator);
            calculator.terminate();
            return calculator.getScore();
            
        } catch (Exception e) {
            // schould not happen
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public void reset() throws Exception {
        super.reset();
        
        firstLookup = false;
        score.reset();
        alsoFixed.clear();
        if (sum == null) sum = new JoinCost(); else sum.reset();
        if (cost == null) cost = new SCost(); else cost.reset();
    }
}