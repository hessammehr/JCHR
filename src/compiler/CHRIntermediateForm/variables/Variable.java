package compiler.CHRIntermediateForm.variables;

import java.util.Set;
import java.util.SortedSet;

import util.collections.Singleton;

import compiler.CHRIntermediateForm.Cost;
import compiler.CHRIntermediateForm.arg.argument.IImplicitArgument;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitor;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunctVisitor;
import compiler.CHRIntermediateForm.constraints.java.NoSolverConjunct;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrderVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleVisitor;
import compiler.CHRIntermediateForm.exceptions.AmbiguityException;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.init.IDeclarator;
import compiler.CHRIntermediateForm.matching.CoerceMethod;
import compiler.CHRIntermediateForm.members.Field;
import compiler.CHRIntermediateForm.members.Method;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.Type;

/*
 * TODO
 *  maak wsh best onderscheid tussen RuleVariable, LocalVariable en ImplicitVariable
 *      - als local variables ook in guards worden toegelaten zou
 *          anders de getGuards() methode niet langer interessant zijn
 *      - voor local variables ook enkel geinteresseerd of ooit gebruikt
 *          of niet, al rest moeten we niet weten
 *      - local variable zal (voorlopig) steeds gedeclareerd zijn
 *      - implicit variables daarentegen nooit
 *      - type van implicit variable kan via occurrence, identifier is
 *          niet nodig
 */

/**
 * An actual variable is a variable that, in contrast to a formal variable,
 * be used as an argument to occurrences, constraints, method calls, etc.
 * There is one more special kind of actual variables, namely the untyped nameless
 * variable (represented by the singleton class {@link NamelessVariable}).
 * 
 * @author Peter Van Weert
 */ 
public class Variable extends TypedVariable
    implements IActualVariable, IImplicitArgument, Comparable<Variable>, IGuardConjunct {

    public Variable(String identifier, VariableType type) 
    throws IllegalIdentifierException {
        super(identifier, type);
    }

    public Field getField(String name) throws AmbiguityException, NoSuchFieldException {
        return CoerceMethod.getFieldCoerced(getType(), name);
    }
    
    public Set<Method> getMethods(String name) {
        return CoerceMethod.getMethodsCoerced(getType(), name);
    }
    
    public NoSolverConjunct getDeclaratorInstance() throws AmbiguityException {
        final IDeclarator<?> declarator = getType().getDeclarator();
        if (declarator == null)
            return null;
        else
            return declarator.getInstance(this);
    }
    
    public NoSolverConjunct getInitialisingDeclaratorInstanceFrom(IType base) throws AmbiguityException {
        final IDeclarator<?> declarator = getType().getInitialisationDeclaratorFrom(base);
        if (declarator == null)
            return null;
        else
            return declarator.getInstance(this);
    }
    
    public void accept(IArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    public boolean isAnonymous() {
        return getIdentifier().startsWith("_");
    }
    public boolean isImplicit() {
        return getIdentifier().startsWith("$");
    }
    
    public int compareTo(Variable other) {
        return this.getIdentifier().compareTo(other.getIdentifier());
    }
    
    public boolean canBeAskConjunct() {
        return Type.isBoolean(getType());
    }
    
    public int getNbVariables() {
        return 1;
    }
    public SortedSet<Variable> getVariables() {
        return new Singleton<Variable>(this);
    }
    
    public SortedSet<Variable> getJoinOrderPrecondition() {
        return getVariables();
    }
    
    public float getSelectivity() {
        return 1;
    }
    public Cost getExpectedCost() {
    	return Cost.FREE;
    }
    public Cost getSelectionCost() {
    	return Cost.FREE;
    }
    
    public boolean isNegated() {
        return false;
    }
    public boolean isEquality() {
        return false;
    }
    public boolean fails() {
        return false;
    }
    public boolean succeeds() {
        return false;
    }
    
    public boolean isConstant() {
        return false;
    }
    
    public void setUnreactive() {
        maybeReactive = false;
    }
    public void resetReactiveness() {
        maybeReactive = true;
    }
    
    private boolean maybeReactive = true;
    public boolean isReactive() {
        return maybeReactive && !isFixed();
    }
    
    
    public void accept(IConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    public void accept(IGuardConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    public void accept(IJoinOrderVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    public void accept(IScheduleVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}