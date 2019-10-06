package compiler.CHRIntermediateForm.arg.argumentable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import util.Cloneable;

import compiler.CHRIntermediateForm.arg.argument.ClassNameImplicitArgument;
import compiler.CHRIntermediateForm.arg.argument.IImplicitArgument;
import compiler.CHRIntermediateForm.arg.argumented.IArgumented;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.matching.MatchingInfos;
import compiler.CHRIntermediateForm.members.IMember;
import compiler.CHRIntermediateForm.types.GenericType;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.PrimitiveType;
import compiler.CHRIntermediateForm.types.Reflection;
import compiler.CHRIntermediateForm.types.Type;

/**
 * @author Peter Van Weert
 */
public abstract class AbstractMethod<T extends AbstractMethod<?>>
extends Argumentable<T> implements IMember, Cloneable<T> {

    /**
     * The type of the implicit argument, or <code>null</code> if this is a 
     * static method.
     */
    private GenericType implicitArgumentType;
    
    /**
     * The default implicit argument of this method, if any.
     * This default argument can be:
     *  <ul>
     *      <li>A solver (for built-in constraint calls)</li>
     *      <li>A class name (for static method calls)</li>
     *      <li>Or <code>null</code> (if no default implicit argument)</li>
     *  </ul>
     */
    private IImplicitArgument defaultImplicitArgument;
    
    /**
     * Was this method statically imported or not? Members that are 
     * not statically imported are always preferred over statically
     * imported ones.
     */
    private boolean staticallyImported;

    /**
     * The actual method this represents. 
     */
    private Method method;
    
    /**
     * If this constructor is used, one is expected to use one of the 
     * <code>init</code> methods afterwards to initialize all 
     * required fields. 
     */
    public AbstractMethod() {
        // NOP
    }
    
    public AbstractMethod(Method method) {
        init(method);
    }
    
    public AbstractMethod(GenericType implicitArgumentType, Method method) {        
        init(implicitArgumentType, method, false);
    }
    
    public AbstractMethod(GenericType implicitArgumentType, Method method, boolean imported) {        
        init(implicitArgumentType, method, imported);
    }
    
    protected final void init(Method method) {
        init(method, false);
    }
    
    protected final void init(Method method, boolean imported) {
        // here is the reason we used init-methods: the
        // getImplicitArgumentType() member method cannot be called like e.g.:
        //      this(getImplicitArgumentType(),...)
        init(getImplicitArgumentType(), method, imported);
    }
    
    protected void init(GenericType base, Method method, boolean imported) {
        setImplicitArgumentType(base);
        setMethod(method);
        if (imported) setStaticallyImported();
        
        if (isStatic()) setDefaultImplicitArgument(
            new ClassNameImplicitArgument(method.getDeclaringClass())
        );
    }
  
    public Method getMethod() {
        return method;
    }
    public String getName() {
        return getMethod().getName();        
    }
    protected void setMethod(Method method) {
        this.method = method;
    }
    
    public IType getFormalParameterTypeAt(int index) {
    	if (index == 0)
    		return getImplicitArgumentType();
		else
	        return Reflection.reflect(getImplicitArgumentType(), getMethod().getGenericParameterTypes()[index-1]);
    }
    public int getArity() {
        return getMethod().getGenericParameterTypes().length + 1;
    }

    public IType getReturnType() {
        return Reflection.reflect(getImplicitArgumentType(), getMethod().getGenericReturnType());
    }
    
    protected GenericType getImplicitArgumentType() {
        return implicitArgumentType;
    }
    protected void setImplicitArgumentType(GenericType implicitArgumentType) {
        this.implicitArgumentType = implicitArgumentType;
    }
    
    @Override
    public boolean equals(Object other) {
        return (other instanceof AbstractMethod) 
            && this.equals((AbstractMethod<?>)other); 
    }
    
    public boolean equals(AbstractMethod<?> other) {
        return  (this == other) || ( other != null
            &&  this.getMethod().equals(other.getMethod())
	    	&&  this.getImplicitArgumentType().equals(other.getImplicitArgumentType())
        );
    }
    
    @Override
    public int hashCode() {
        return 37 * (37 * 23 
            + getMethod().hashCode()) 
            + getImplicitArgumentType().hashCode();
    }
    
    public boolean isValidArgument() {
        return getReturnType() != PrimitiveType.VOID; 
    }
    
    public boolean returnsBoolean() {
        return Type.isBoolean(getReturnType());
        /* we allow auto-unboxing here: no explicit coercion is done! */
    }
    
    @Override
    public String toString() {
    	if (isStatic())
    		return getDefaultImplicitArgument().toString()
    			+ '.' + getName() 
    			+ toString(this, 1);
    	else
    		return getFormalParameterTypeAt(0).toTypeString()
            	+ '.' + getName() 
            	+ toString(this, 1);
    }
    
    public boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }
    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }
    public Class<?> getDeclaringClass() {
        return getMethod().getDeclaringClass();
    }
    public String getDeclaringClassName() {
        return getDeclaringClass().getCanonicalName();
    }
    
    public boolean isStaticallyImported() {
        return staticallyImported;
    }
    public void setStaticallyImported() {
        staticallyImported = true;
    }
    
    public int getModifiers() {
        return getMethod().getModifiers();
    }
    
    @Override
    public final MatchingInfos canHaveAsArguments(IArguments arguments) {
        arguments = setImplicitArgumentOf(arguments);
        MatchingInfos result = canHaveAsArguments2(arguments);
        unsetImplicitArgumentOf(arguments);
        return result;
    }
    
    protected MatchingInfos canHaveAsArguments2(IArguments arguments) {
        if (! arguments.hasImplicitArgument()) 
            return MatchingInfos.NO_MATCH;
        else {
            if (! isVarArgs()) return super.canHaveAsArguments(arguments);
            
            int formalArity = getExplicitArity();
            int formalArityX = formalArity-1;
            int actualArity = arguments.getArity();
            
            if (formalArityX > actualArity)      
                return MatchingInfos.NO_MATCH;
            
            MatchingInfos result = canHaveAsFirstArguments(this, arguments, formalArityX);
            
            int i = formalArityX;
            while (i < actualArity && result.isNonAmbiguousMatch())
                result = addMatchingInfoFor(result, this, formalArity-1, arguments, i++);
                
            return result;
        }
    }
    
    public boolean isVarArgs() {
        return getMethod().isVarArgs();
    }
    
    protected IImplicitArgument getDefaultImplicitArgument() {
        return defaultImplicitArgument;
    }
    protected void setDefaultImplicitArgument(IImplicitArgument defaultImplicitArgument) {
        this.defaultImplicitArgument = defaultImplicitArgument;
    }
    protected boolean hasDefaultImplicitArgument() {
        return getDefaultImplicitArgument() != null;
    }
    
    @Override
    public IArgumented<T> createInstance(MatchingInfos infos, IArguments arguments) {
        arguments = setImplicitArgumentOf(arguments);
        return super.createInstance(infos, arguments);
    }
    
    protected IArguments setImplicitArgumentOf(IArguments arguments) {
//        if (arguments.hasImplicitArgument())
//            throw new IllegalStateException("Implicit argument set for built-in-constraint");
        
        if (!arguments.isMutable())
            arguments = new Arguments(arguments);
        
        if (hasDefaultImplicitArgument())
            arguments.addImplicitArgument(getDefaultImplicitArgument());
        else
            arguments.markFirstAsImplicitArgument();
        
        return arguments;
    }
    
    protected void unsetImplicitArgumentOf(IArguments arguments) {
        if (hasDefaultImplicitArgument())
            arguments.removeImplicitArgument();
        else
            arguments.removeImplicitArgumentMark();
    }
    
    public boolean haveToIgnoreImplicitArgument() {
        return hasDefaultImplicitArgument();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public T clone() {
        try {
            // niet type-safe, weet't wel, maar liever lui dan moe ;-)
            return (T)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}