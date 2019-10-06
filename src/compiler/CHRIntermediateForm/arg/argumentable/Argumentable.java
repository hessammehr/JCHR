package compiler.CHRIntermediateForm.arg.argumentable;

import static util.comparing.Comparison.AMBIGUOUS;
import static util.comparing.Comparison.EQUAL;
import util.comparing.Comparison;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumented.IArgumented;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.matching.MatchingInfos;
import compiler.CHRIntermediateForm.types.IType;

/**
 * @author Peter Van Weert
 */
public abstract class Argumentable<T extends IArgumentable<?>> 
implements IArgumentable<T> {
    
    public int getExplicitArity() {
        return getArity() - getIgnoreInt();
    }
    
    protected int getIgnoreInt() {
        return haveToIgnoreImplicitArgument()? 1 : 0;
    }
    
    public IType[] getExplicitFormalParameterTypes() {
        final IType[] result = new IType[getExplicitArity()];
        for (int i = 0; i < result.length; i++)
            result[i] = getExplicitFormalParameterTypeAt(i);
        return result;
    }
    
    public IType[] getFormalParameterTypes() {
        final IType[] result = new IType[getArity()];
        for (int i = 0; i < result.length; i++)
            result[i] = getFormalParameterTypeAt(i);
        return result;
    }
    
    public IType getExplicitFormalParameterTypeAt(int index) {
        return getFormalParameterTypeAt(index + getIgnoreInt());
    }
    
    public static MatchingInfos canHaveAsArguments(IArgumentable<?> argumentable, IArguments arguments) {
        final int arity = argumentable.getArity();
        
        if (arity != arguments.getArity())
            return MatchingInfos.NO_MATCH;
        if (arity == 0)
            return MatchingInfos.EXACT_MATCH;

        return canHaveAsFirstArguments(argumentable, arguments, arity);
    }
    /*
     * @pre argumentable.getArity() > 0
     */
    protected static MatchingInfos canHaveAsFirstArguments(IArgumentable<?> argumentable, IArguments arguments, int number) {
        MatchingInfos result = new MatchingInfos(
            argumentable.getArity(), 
            argumentable.haveToIgnoreImplicitArgument()
        );
        int i = 0;
        
        do
            result = addMatchingInfoFor(result, argumentable, i, arguments, i++);
        while 
            (i < number && result.isNonAmbiguousMatch());
                
        return result;
    }
    protected static MatchingInfos addMatchingInfoFor(
        MatchingInfos result,
        IArgumentable<?> argumentable, int i, IArguments arguments, int j 
    ) {
        MatchingInfo temp = argumentable.canHaveAsArgumentAt(i, arguments.getArgumentAt(j));
                       
        if (temp.isAmbiguous())
            return temp.isInitMatch()
                ? MatchingInfos.AMBIGUOUS_INIT
                : MatchingInfos.AMBIGUOUS_NO_INIT;
        else if (!temp.isMatch()) 
            return MatchingInfos.NO_MATCH;
        else {
            result.setAssignmentInfoAt(temp, i);
            return result;
        }
    }
    
    public static boolean haveToIgnoreFirstArgument(
        IArgumentable<?> argumentable, IArguments arguments
    ) {
        return argumentable.haveToIgnoreImplicitArgument() 
            && arguments.hasImplicitArgument();
    }
    public static int getFirstArgumentIndex(
        IArgumentable<?> argumentable, IArguments arguments
    ) {
        return haveToIgnoreFirstArgument(argumentable, arguments)? 1 : 0; 
    }
    
    public MatchingInfos canHaveAsArguments(IArguments arguments) { 
        return canHaveAsArguments(this, arguments);
    }
        
    public MatchingInfo canHaveAsArgumentAt(int index, IArgument argument) {
        return argument.isAssignableTo(getFormalParameterTypeAt(index));
    }
    
    @Override
    public String toString() {
        return toString(this);
    }
    
    public IArgumented<T> createInstance(IArgument... arguments) {
        return createInstance(new Arguments(arguments));
    }
    public IArgumented<T> createInstance(MatchingInfos infos, IArgument... arguments) {
        return createInstance(infos, new Arguments(arguments));
    }
    
    public IArgumented<T> createInstance(MatchingInfos infos, IArguments arguments) {
        arguments.incorporate(infos, haveToIgnoreImplicitArgument());
        return createInstance(arguments);
    }
    
    public static String toString(IArgumentable<?> argumentable) {
        return toString(argumentable, 0);
    }
    
    public static String toString(IArgumentable<?> argumentable, int from) {
        StringBuilder result = new StringBuilder().append('(');
        final int arity = argumentable.getArity();
        if (arity > from)
            result.append(argumentable.getFormalParameterTypeAt(from).toTypeString());
        for (int i = from+1; i < arity; i++)
            result.append(", ").append(argumentable.getFormalParameterTypeAt(i).toTypeString());
        return result.append(')').toString();
    }
    
    public static Comparison compare(IArgumentable<?> one, IArgumentable<?>  other) {
        final int arity = one.getExplicitArity();
        if (other.getExplicitArity() != arity)
            throw new IllegalStateException("different arities!");
        
        Comparison comparison = EQUAL, temp;
        
        for (int i = 0; i < arity; i++) {
            temp = other.getExplicitFormalParameterTypeAt(i).compareWith(one.getExplicitFormalParameterTypeAt(i));
            switch (temp) {
            	// als er minstens 1 vergelijking ambigu is, is vergelijking niet mogelijk
            	case AMBIGUOUS:
            	    return AMBIGUOUS;
        	    /*break;*/
            
        	    // als er een verschil is moet...
        	    case BETTER:
    	        case WORSE:
    	            if (comparison == EQUAL) // ... na de eerste keer ...
    	                comparison = temp;
	            	else if (comparison != temp) // ... de vergelijking steeds hetzelfde zijn
	            	    return AMBIGUOUS;
            	break;
            }
        }
        
        return comparison;
    }
    
    public Comparison compareWith(IArgumentable<?> other) {
        return compare(this, other);
    }
    
    @Override
    public int hashCode() {
        int result = 23;
        final int arity = getArity();
        for (int i = 0; i < arity; i++)            
            result = 37 * result + getFormalParameterTypeAt(i).hashCode();            
        return result;
    }
    
    @Override
    public abstract boolean equals(Object obj);
}
