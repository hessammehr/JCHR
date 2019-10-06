package compiler.CHRIntermediateForm.rulez;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import annotations.JCHR_Fixed;

import util.Terminatable;
import util.iterator.IteratorUtilities;

import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitable;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitable;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitor;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunctVisitable;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunctVisitor;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.id.AbstractIdentified;
import compiler.CHRIntermediateForm.id.Identifier;

/**
 * @author Peter Van Weert
 */
@JCHR_Fixed // as long as only used in equality constraints...
public abstract class Rule extends AbstractIdentified
implements Comparable<Rule>, 
    IOccurrenceVisitable, IGuardConjunctVisitable, IConjunctVisitable, 
    Terminatable, 
    IArgumentVisitable {
    
    private int nbr;

    private PositiveHead positiveHead;
    private List<NegativeHead> negativeHeads;
    private Body body;

    protected Rule(String id, int nbr, PositiveHead head) throws IllegalIdentifierException {
        super(id);
        setNbr(nbr);
        
        setBody(new Body());
        linkPositiveHead(head);
        setNegativeHeads(new ArrayList<NegativeHead>());
    }
    
    /**
     * This factory method creates a new rule with a given identifier and
     * rule-number. The final argument determines the type of the rule
     * (i.e. propagation, simplification or simpagation).
     * 
     * @param id
     *  The (unique) identifier of this rule.
     * @param nbr
     *  The (unique) number of this rule.
     * @param type
     *  The type of this rule (i.e. propagation, simplification or simpagation).
     *  
     * @return A new instance of some subclass of <code>Rule</code>
     *  that represents a rule with the given name, number and type.
     *  
     * @throws NullPointerException
     *  If <code>type</code> is <code>null</code>
     * @throws IllegalIdentifierException
     *  If the given identifier is of an invalid format.
     *  
     * @see RuleType
     * @see RuleType#newInstance(String, int)
     */
    public static Rule newInstance(String id, int nbr, RuleType type) 
    throws NullPointerException, IllegalIdentifierException  {
        return type.newInstance(id, nbr);
    }
    
    public abstract RuleType getType();
    
    public abstract boolean isValid();
    
    public String toFullString() {
        final StringBuilder result = new StringBuilder(getIdentifier()).append(" @ ");
        
        getPositiveHead().appendHeadTo(result);
        
        for (Head head : getNegativeHeads())
            head.appendTo(result);
        
        result.append(getOperator());
        
        if (getPositiveHead().getGuard().hasConjuncts())
            getPositiveHead().appendGuardTo(result).append(" | ");
        
        result.append(getBody()).append('.');
        
        return result.toString();
    }
    
    public String getOperator() {
        return getType().getOperator();
    }
    
    public int getNbr() {
        return nbr;
    }
    public void setNbr(int nbr) {
        this.nbr = nbr;
    }
    
    public int compareTo(Rule rule) {
        if (rule == null) return -1;
        return nbr - rule.nbr;
    }

    public Body getBody() {
        return body;
    }
    protected void setBody(Body body) {
        this.body = body;
    }

    public List<NegativeHead> getNegativeHeads() {
        return Collections.unmodifiableList(getNegativeHeadsRef());
    }
    protected List<NegativeHead> getNegativeHeadsRef() {
        return negativeHeads;
    }
    protected void setNegativeHeads(List<NegativeHead> negativeHeads) {
        this.negativeHeads = negativeHeads;
    }
    
    public int getNbNegativeHeads() {
        return getNegativeHeads().size();
    }
    public boolean hasNegativeHeads() {
        return getNbNegativeHeads() > 0;
    }
    
    public boolean hasSelectiveNegativeHeads() {
        for (NegativeHead head : getNegativeHeadsRef())
            if (head.isSelective()) return true;
        return false;
    }
    
    public NegativeHead addNegativeHead() {
        NegativeHead negativeHead = new NegativeHead(this);
        getNegativeHeadsRef().add(negativeHead);
        return negativeHead;
    }
    public NegativeHead getNegativeHeadAt(int index) {
        return getNegativeHeadsRef().get(index);
    }

    public Guard getPositiveGuard() {
        return getPositiveHead().getGuard();
    }
    public PositiveHead getPositiveHead() {
        return positiveHead;
    }
    protected void linkPositiveHead(PositiveHead positiveHead) {
        positiveHead.setRule(this);
        setPositiveHead(positiveHead);
    }
    private void setPositiveHead(PositiveHead positiveHead) {
        this.positiveHead = positiveHead;
    }
    
    public void accept(IOccurrenceVisitor visitor) throws Exception {
        getPositiveHead().accept(visitor);
        for (NegativeHead head : getNegativeHeadsRef())
            head.accept(visitor);
    }
    
    public void accept(IConjunctVisitor visitor) throws Exception {
        getPositiveHead().accept(visitor);
        for (NegativeHead head : getNegativeHeadsRef())
            head.accept(visitor);
        getBody().accept(visitor);
    }
    public void accept(IGuardConjunctVisitor visitor) throws Exception {
        getPositiveHead().accept(visitor);
        for (NegativeHead head : getNegativeHeadsRef())
            head.accept(visitor);
    }
    
    public void accept(IArgumentVisitor visitor) throws Exception {
        getPositiveHead().accept(visitor);
        for (NegativeHead head : getNegativeHeadsRef())
            head.accept(visitor);
        getBody().accept(visitor);
    }
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        getPositiveHead().accept(visitor);
        for (NegativeHead head : getNegativeHeadsRef())
            head.accept(visitor);
        getBody().accept(visitor);
    }
    
    /**
     * Returns the number of the given head. If it is the positive head,
     * the number is <em>zero</em>. The first negative head (left-to-right) gets
     * the number <em>one</em>, the second (we considered five, but still went for)
     * <em>two</em>, etc
     * 
     * @param head
     *  The head we want to know the number of.
     * @return The number of the given head in this rule.
     */
    public int getHeadNbr(Head head) {
        if (head == getPositiveHead()) return 0;
        return 1 + IteratorUtilities.identityIndexOf(getNegativeHeadsRef(), head);
    }
    
    /**
     * Returns the total number of occurrences (both in the positive
     * head and in the negative heads).
     * 
     * @return The total number of occurrences (both in the positive
     *  head and in the negative heads).
     */
    public int getTotalNbOccurrences() {
        int result = getPositiveHead().getNbOccurrences();
        for (NegativeHead head : getNegativeHeadsRef())
            result += head.getNbOccurrences();
        return result;
    }
    
    public abstract void setNoHistory();
    
    public abstract boolean needsHistory();
    
    /**
     * A rule's identifier has to be a valid simple identifier, 
     * starting with a lower-case. It can also be a generated name, 
     * starting with an '$'.
     * 
     * @return Returns whether the given identifier is a valid simple
     *  identifier (cf. {@link Identifier#isValidUdSimpleIdentifier(String)}),
     *  not starting with a lower-case. 
     * 
     * @see Identifier#isValidSimpleIdentifier(String)
     * @see Identifier#startsWithLowerCase(String)
     */
    @Override
    public boolean canHaveAsIdentifier(String identifier) {
        return Identifier.isValidSimpleIdentifier(identifier)
            && (
                Identifier.startsWithLowerCase(identifier) 
                    || identifier.startsWith("$")
            );
    }
    
    public void terminate() {
        getPositiveHead().terminate();
        setPositiveHead(null);
        for (NegativeHead negativeHead : getNegativeHeads())
            negativeHead.terminate();
        setNegativeHeads(null);
    }
    
    public boolean isTerminated() {
        return getPositiveHead() == null;
    }
}