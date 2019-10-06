package compiler.CHRIntermediateForm.arg.argumentable;

import util.comparing.Comparable;
import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumented.IArgumented;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.matching.MatchingInfos;
import compiler.CHRIntermediateForm.types.IType;

/**
 * @author Peter Van Weert
 */
public interface IArgumentable<T extends IArgumentable<?>> extends Comparable<IArgumentable<?>> {
    
    public int getArity();

    /**
     * @return getArity() - (haveToIgnoreImplicitArgument()? 1 : 0)
     */
    public int getExplicitArity();
    
    public IType[] getFormalParameterTypes();
    public IType[] getExplicitFormalParameterTypes();
    
    public IType getFormalParameterTypeAt(int index);
    public IType getExplicitFormalParameterTypeAt(int index);
    
    public MatchingInfos canHaveAsArguments(IArguments arguments);
    
    /**
     * This method should never be used from outside a call to 
     * {@link #canHaveAsArguments(IArguments)}: it only looks at one 
     * argument at a time, not all arguments together...
     */
    public MatchingInfo canHaveAsArgumentAt(int index, IArgument argument);
    
    public IArgumented<T> createInstance(MatchingInfos infos, IArgument... arguments);
    public IArgumented<T> createInstance(MatchingInfos infos, IArguments arguments);
    
    /*
     * @pre Er moet niets meer gebeuren met deze arguments, i.e.
     *  alles ivm coercing, initialisatie en impliciete argumenten is OK!
     */
    public IArgumented<T> createInstance(IArgument... arguments);
    public IArgumented<T> createInstance(IArguments arguments);
    
    public boolean haveToIgnoreImplicitArgument();
}
