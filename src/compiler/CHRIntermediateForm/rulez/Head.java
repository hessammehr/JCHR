package compiler.CHRIntermediateForm.rulez;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import util.Terminatable;
import util.collections.CollectionPrinter;
import util.exceptions.IllegalArgumentException;

import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitable;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.VariableCollector;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitable;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitor;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunctVisitable;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunctVisitor;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.OccurrenceType;
import compiler.CHRIntermediateForm.variables.Variable;

/**
 * A &quot;head&quot; in the model contains also the guard. Note that this makes
 * sense: in many production rule systems the guard and the head are not
 * separated. Also, it makes more sense when negated heads come into play.
 * 
 * @author Peter Van Weert
 */
public abstract class Head 
implements Iterable<Occurrence>, IOccurrenceVisitable,
    IConjunctVisitable, IGuardConjunctVisitable,
    IArgumentVisitable, 
    Terminatable {
    
    /**
     * The rule this head belongs to.
     */
    private Rule rule;

    /**
     * The list of occurrences of this head.
     */
    private List<Occurrence> occurrences;
    
    /**
     * The guard assosiated with this head.
     */
    private Guard guard;
    
    protected Head(Rule rule) {
        this();
        setRule(rule);
    }
    
    protected Head() {
        setGuard(new Guard(this));
        setOccurrences(new LinkedList<Occurrence>());
    }
    
    public boolean isNegative() {
        return ! isPositive();
    }
    public boolean isPositive() {
        return getRule().getPositiveHead() == this;
    }

    /***************************************************************************
     * The actual head (the occurrences)
     **************************************************************************/

    public List<Occurrence> getOccurrences() {
        return Collections.unmodifiableList(getOccurrencesRef());
    }
    public Occurrence[] getOccurrencesArray() {
        return getOccurrencesRef().toArray(
            new Occurrence[getNbOccurrences()]
        );
    }
    protected List<Occurrence> getOccurrencesRef() {
        return occurrences;
    }
    
    public Iterator<Occurrence> iterator() {
        return getOccurrencesRef().iterator();
    }

    protected void setOccurrences(List<Occurrence> occurrences) {
        this.occurrences = occurrences;
    }

    public int getNbOccurrences() {
        return getOccurrencesRef().size();
    }

    public boolean hasOccurrences() {
        return getNbOccurrences() > 0;
    }

    public int getNbActiveOccurrences() {
        int result = 0;
        for (Occurrence occurrence : getOccurrencesRef())
            if (!occurrence.isPassive()) result++;
        return result;
    }
    
    public Occurrence getOccurrenceAt(int index) {
        return getOccurrencesRef().get(index);
    }

    public Occurrence getLastOccurence() {
        return getOccurrenceAt(getNbOccurrences() - 1);
    }

    public int getOccurrenceIndex(Occurrence occurrence) {
        return getOccurrencesRef().indexOf(occurrence);
    }
    public int getOccurrenceNumber(Occurrence occurrence) {
        return getOccurrenceIndex(occurrence) + 1;
    }
    
    public boolean canAddOccurrence(Occurrence occurrence) {
        return (occurrence != null)
            && canAddOccurrenceType(occurrence.getType());
    }

    public abstract boolean canAddOccurrenceType(OccurrenceType occurrence);

    public void addOccurrence(Occurrence occurrence) {
        if (canAddOccurrence(occurrence))
            getOccurrencesRef().add(occurrence);
        else
            throw new IllegalArgumentException("Cannot add "
                    + occurrence.getType() + " occurrence " + occurrence);
    }
    
    public void removeOccurrence(Occurrence occurrence) {
        if (!getOccurrencesRef().remove(occurrence))
            throw new IllegalStateException();
    }

    public List<Occurrence> getOccurrences(OccurrenceType type) {
        List<Occurrence> result = new ArrayList<Occurrence>();
        for (Occurrence occurrence : getOccurrencesRef())
            if (occurrence.getType() == type)
                result.add(occurrence);
        return result;
    }

    public int getNbOccurrences(OccurrenceType type) {
        int result = 0;
        for (Occurrence occurrence : getOccurrencesRef())
            if (occurrence.getType() == type)
                result++;
        return result;
    }
    
    public boolean isReactive() {
        for (Occurrence occurrence : getOccurrencesRef())
            if (occurrence.isReactive())
                return true;
        return false;
    }

    public boolean hasOccurrences(OccurrenceType type) {
        for (Occurrence occurrence : getOccurrencesRef())
            if (occurrence.getType() == type)
                return true;
        return false;
    }
    
    public void accept(IOccurrenceVisitor visitor) throws Exception {
        for (Occurrence occurrence : this) occurrence.accept(visitor);
    }
    
    public void accept(IConjunctVisitor visitor) throws Exception {
        accept((IOccurrenceVisitor)visitor);
        accept((IGuardConjunctVisitor)visitor);
    }
    public void accept(IGuardConjunctVisitor visitor) throws Exception {
        getGuard().accept(visitor);
    }
    
    public void accept(IArgumentVisitor visitor) throws Exception {
        for (Occurrence occurrence : this) {
            visitor.resetVisiting();
            occurrence.accept(visitor);
        }
        getGuard().accept(visitor);
    }
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        for (Occurrence occurrence : this) occurrence.accept(visitor);
        getGuard().accept(visitor);
    }
    
    /***************************************************************************
     * The explicit guard for this head 
     * (might also contains some explicitized conjuncts) 
     **************************************************************************/

    public Guard getGuard() {
        return guard;
    }
    protected void setGuard(Guard guard) {
        this.guard = guard;
    }
    
    /***************************************************************************
     * The rule the head is part of
     **************************************************************************/
    
    public Rule getRule() {
        return rule;
    }
    protected void setRule(Rule rule) {
        this.rule = rule;
    }
    
    public int getNbr() {
        return getRule().getHeadNbr(this);
    }
    
    /***************************************************************************
     * Variables 
     ***************************************************************************/
    
    public SortedSet<Variable> getVariables() {
        SortedSet<Variable> result = new TreeSet<Variable>();
        for (Occurrence occurrence : getOccurrencesRef())
            result.addAll(occurrence.getVariables());
        return result;
    }
    
    public SortedSet<Variable> getAllVariables() {
        return VariableCollector.collectVariables(getGuard(), getVariables());
    }
    
    
    /***************************************************************************
     * Some convenience methods
     **************************************************************************/

    @Override
    public final String toString() {
        return appendTo(new StringBuilder()).toString();
    }
    public StringBuilder appendTo(StringBuilder result) {
        return appendGuardTo(appendHeadTo(result).append(" | "));
    }
    
    public StringBuilder appendHeadTo(StringBuilder result) {
        // default implementation:
        return CollectionPrinter.getCommaSeperatedInstance().appendTo(result, getOccurrences());
    }
    public String getHeadString() {
        // default implementation:
        return CollectionPrinter.getCommaSeperatedInstance().toString(getOccurrences());
    }

    public StringBuilder appendGuardTo(StringBuilder result) {
        // default implementation:
        return result.append(getGuard());
    }
    public String getGuardString() {
        return getGuard().toString();
    }
    
    public boolean isValid() {
        return hasOccurrences();
    }
    
    public void terminate() {
        Occurrence[] occurrences = getOccurrencesArray();
        for (int i = 0; i < occurrences.length; i++)
            occurrences[i].terminate();
        
        getGuard().terminate();
        setGuard(null);
        setOccurrences(null);
    }
    
    public boolean isTerminated() {
        return getOccurrencesRef() == null;
    }

    /*
     * do not change/override the methods below seperately!
     */    
    @Override
    public int hashCode() {
        return 37 * (37 * 23 + getRule().getNbr()) + getNbr();
    }
    @Override
    public boolean equals(Object other) {
        return this == other;
    }
}