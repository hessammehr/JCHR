package compiler.CHRIntermediateForm.arg.arguments;

import java.util.List;

import util.iterator.ListIterable;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argument.IImplicitArgument;
import compiler.CHRIntermediateForm.matching.MatchingInfos;
import compiler.CHRIntermediateForm.types.IType;

/**
 * @author Peter Van Weert
 */
public interface IArguments extends ListIterable<IArgument> {
    public List<IArgument> asList();

    public int getArity();

    public IArgument getArgumentAt(int index);
    
    public int getIndexOf(IArgument argument);
    
    public void replaceArgumentAt(int index, IArgument argument);
    
    public IType[] getTypes();
    
    public IType getTypeAt(int index);

    public void addArgument(IArgument argument);
    
    public void addArgumentAt(int index, IArgument argument);
    
    public void addImplicitArgument(IImplicitArgument implicitArgument);
    
    public void removeImplicitArgument();
    
    public void markFirstAsImplicitArgument();
    
    public boolean isMutable();
    
    public void removeImplicitArgumentMark();
    
    public boolean hasImplicitArgument();
    
    public void incorporate(MatchingInfos matchingInfos, boolean ignoreImplicitArgument);

    public boolean allFixed();
    
    public boolean allConstant();
}