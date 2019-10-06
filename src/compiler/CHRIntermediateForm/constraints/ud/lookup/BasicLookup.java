package compiler.CHRIntermediateForm.constraints.ud.lookup;

import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.lookup.category.ILookupCategory;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;
import compiler.CHRIntermediateForm.variables.IActualVariable;

/**
 * Always make sure the lookup type can no longer be changed after
 * the lookup category was set! 
 * 
 * @author Peter Van Weert
 */
public class BasicLookup extends Lookup {

    private Occurrence occurrence;
    
    private ILookupType lookupType;
    
    private ILookupCategory lookupCategory;
    
    private IArguments arguments;
    
    public BasicLookup(Lookup base, ILookupType lookupType, IArguments arguments) {
        this(base.getOccurrence(), base.getVariables(), lookupType, arguments);
    }
    
    public BasicLookup(Occurrence occurrence, IActualVariable[] variables, 
            ILookupType lookupType, IArguments arguments) {
        
        super(variables);
        setOccurrence(occurrence);
        setLookupType(lookupType);
        setArguments(arguments);
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
        return lookupType;
    }
    protected void setLookupType(ILookupType lookupType) {
        if (!canChangeLookupInformation())
            throw new IllegalStateException();
        this.lookupType = lookupType;
    }
    
    @Override
    public ILookupCategory getLookupCategory() {
        return lookupCategory;
    }
    @Override
    public void setLookupCategory(ILookupCategory lookupCategory) {
        if (!canChangeLookupInformation())
            throw new IllegalStateException();
        this.lookupCategory = lookupCategory;
    }
    
    public IArguments getArguments() {
        return arguments;
    }
    protected void setArguments(IArguments arguments) {
        this.arguments = arguments;
    }
}
