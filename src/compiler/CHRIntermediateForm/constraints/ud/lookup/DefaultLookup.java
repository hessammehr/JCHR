package compiler.CHRIntermediateForm.constraints.ud.lookup;

import java.util.Iterator;

import util.iterator.EmptyIterator;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.arguments.EmptyArguments;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.DefaultLookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.DefaultLookupType;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;
import compiler.CHRIntermediateForm.variables.IActualVariable;

public class DefaultLookup extends Lookup {
    private Occurrence occurrence;
    
    public DefaultLookup(Occurrence occurrence, IActualVariable... variables) {
        super(variables);
        setOccurrence(occurrence);
    }
    
    @Override
    public Occurrence getOccurrence() {
        return occurrence;
    }
    protected void setOccurrence(Occurrence occurrence) {
        this.occurrence = occurrence;
    }
    
    @Override
    public ILookupType getLookupType() {
        return DefaultLookupType.getInstance();
    }
    
    @Override
    public ILookupCategory getLookupCategory() {
        return DefaultLookupCategory.getInstance();
    }
    @Override
    public void setLookupCategory(ILookupCategory lookupCategory) 
    throws IllegalStateException {
        throw new IllegalStateException();
    }
    @Override
    public boolean canChangeLookupInformation() {
        return false;
    }

    public IArguments getArguments() {
        return EmptyArguments.getInstance();
    }
    @Override
    public Iterator<IArgument> iterator() {
        return EmptyIterator.getInstance();
    }
}
