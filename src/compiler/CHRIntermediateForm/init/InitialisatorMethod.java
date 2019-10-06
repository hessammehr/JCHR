package compiler.CHRIntermediateForm.init;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import runtime.IdGenerator;

import util.Annotations;
import util.comparing.Comparison;
import annotations.JCHR_Declare;
import annotations.JCHR_Init;

import static compiler.CHRIntermediateForm.members.Method.getStaticMethods;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.StringArgument;
import compiler.CHRIntermediateForm.arg.argumentable.AbstractMethod;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.arguments.EmptyArguments;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.exceptions.AmbiguityException;
import compiler.CHRIntermediateForm.types.GenericType;
import compiler.CHRIntermediateForm.types.IType;

public class InitialisatorMethod 
    extends AbstractMethod<InitialisatorMethod>
    implements IInitialisator<InitialisatorMethod> {
    
    private int identifierIndex = -1;
    
    public InitialisatorMethod() {
        // NOP
    }
    
    public InitialisatorMethod(Method method) {
        super(method);
    }
    
    @Override
    public void init(GenericType base, Method method, boolean imported) {     
        super.init(base, method, imported);
        
        if (method.isAnnotationPresent(JCHR_Init.class))
            setIdentifierIndex(method.getAnnotation(JCHR_Init.class).identifier() + 1);
        else
            setIdentifierIndex(-1);
    }

    public boolean isConstructor() {
        return false;
    }

    public IType getType() {
        return getReturnType();
    }
    
    public Comparison compareTo(IInitialisator<?> other) {
        return Initialisator.compare(this, other);
    }

    protected static InitialisatorMethod getInitialisatorMethodFrom(GenericType base, InitialisatorMethod best, Class<?> raw, IType type)
    throws AmbiguityException {
        List<Method> methods = Annotations.getAnnotatedMethods(raw, JCHR_Init.class);
        return methods.isEmpty()? best : getInitialisatorMethodFrom(base, best, methods, type);
    }
    
    protected static InitialisatorMethod getInitialisatorMethodFrom(GenericType base, InitialisatorMethod best, List<Method> methods, IType type)
    throws AmbiguityException {
        InitialisatorMethod temp = new InitialisatorMethod();
        boolean ambiguous = false;
        
        for (Method method : methods) {
            temp.init(method);
            
            if (temp.isValidInitialisatorFrom(type) && temp.getType().isAssignableTo(base).isMatch()) {
                if (best == null)
                    best = temp.clone();
                else {                    
                    switch (temp.compareTo(best)) {
                        case BETTER:
                            ambiguous = false;
                            best = temp.clone();
                        break;
                        
                        case EQUAL:
                        case AMBIGUOUS:
                            ambiguous = true;
                        break;
                    }
                }
            }
        }
        
        if (ambiguous) throw new AmbiguityException();

        return best;
    }
    
    public static InitialisatorMethod getWrapperInitialisatorMethodFrom(GenericType base, IType type) 
    throws AmbiguityException {
        return getInitialisatorMethodFrom(base, null, getWrapperInitialisatorMethods(base), type);
    }
    
    private final static Map<GenericType, List<Method>> WRAPPER_INITIALISATORS
        = new HashMap<GenericType, List<Method>>();
    public static List<Method> getWrapperInitialisatorMethods(GenericType base) {
        if (!base.isLiteralType()) throw new IllegalArgumentException();
        
        List<Method> result = WRAPPER_INITIALISATORS.get(base);
        
        if (result == null) {
            result = new ArrayList<Method>();
            for (Method method : base.getRawType().getMethods())
                if (isWrapperInititialisatorMethod(method))
                    result.add(method);
            WRAPPER_INITIALISATORS.put(base, result);
        }
        
        return result;
    }
    public static boolean isWrapperInititialisatorMethod(Method method) {
        return GenericType.getInstance(method.getDeclaringClass()).isLiteralType() && method.getName().equals("valueOf");
    }
    
    public static InitialisatorMethod getDeclaratorInitialisatorMethod(GenericType base) 
    throws AmbiguityException {
        InitialisatorMethod result = null;
        JCHR_Declare declare = base.getRawType().getAnnotation(JCHR_Declare.class);
        if (declare != null) try {
            InitialisatorMethod temp = new InitialisatorMethod();
            
            String id = declare.factoryClass();
            if (id.length() > 0) {
                for (Method method : Class.forName(id).getMethods()) {
                    if (method.isAnnotationPresent(JCHR_Declare.class)) {
                        temp.init(method);
                        
                        if (temp.getType().isAssignableTo(base).isMatch()) {
                            if (result == null)
                                result = new InitialisatorMethod(method);
                            else {                            
                                if (temp.usesIdentifier() && !result.usesIdentifier())
                                    result = temp.clone();
                                else if (!temp.usesIdentifier() && result.usesIdentifier())
                                    continue;
                                else
                                    throw new AmbiguityException();
                            }
                        }
                    }
                }
            }
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Unable to find factory class");
            cnfe.printStackTrace();
        }
        
        return result;
    }    
    
    public static InitialisatorMethod getInitialisatorMethodFrom(GenericType base, IType type) 
    throws AmbiguityException {
        InitialisatorMethod result = null;
        
        JCHR_Init init = base.getRawType().getAnnotation(JCHR_Init.class);
        if (init != null) try {
            String id = init.factoryClass();
            if (id.length() > 0)
                result = getInitialisatorMethodFrom(base, null, Class.forName(id), type);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Unable to find factory class: ");
            cnfe.printStackTrace();
        }
        
        return getInitialisatorMethodFrom(base, result, base.getRawType(), type);
    }

    public int getIdentifierIndex() {
        return identifierIndex;
    }
    public boolean usesIdentifier() {
        return getIdentifierIndex() >= 0;
    }
    protected void setIdentifierIndex(int identifierIndex) {
        this.identifierIndex = identifierIndex;
    }
    
    public IInitialisatorInvocation<InitialisatorMethod> getInstance(IArgument argument, String identifier) {
        IArguments arguments = new Arguments(3 /* foert, is al beter dan de standaard */);
        arguments.addArgument(argument);
        
        if (usesIdentifier()) 
            arguments.addArgumentAt(
                getIdentifierIndex(), getUniqueIdentifierArgument(identifier)
            );
        
        arguments = setImplicitArgumentOf(arguments);
        return createInstance(arguments);
    }    
    public IInitialisatorInvocation<InitialisatorMethod> getInstance(IArgument argument) {
        return getInstance(argument, null);
    }
    
    public final static IArgument getUniqueIdentifierArgument(String base) {
        IArguments arguments = base == null
            ? EmptyArguments.getInstance()
            : new Arguments(new StringArgument(base));
        
        return getStaticMethods(IdGenerator.class, "generateUniqueId").
            iterator().next().createStaticInstance(arguments);
    }
    
    @Override
    public InitialisatorMethodInvocation createInstance(IArgument... arguments) {
        return (InitialisatorMethodInvocation)super.createInstance(arguments);
    }
    public InitialisatorMethodInvocation createInstance(IArguments arguments) {
        return new InitialisatorMethodInvocation(this, arguments);
    }
    
    public boolean isValidInitialisatorFrom(IType type) {
        return Initialisator.isValidInitialisatorFrom(this, type);
    }
    public boolean isValidDeclarationInitialisator() {
        return getArity() == (usesIdentifier()? 0 : 1);
    }
    public boolean isValidInitialisator() {        
        return (isWrapperInititialisatorMethod(getMethod()) || getMethod().isAnnotationPresent(JCHR_Init.class))
            && Initialisator.hasValidIdentifierParameter(this)
            && getArity() == (usesIdentifier()? 3 : 2);
    }
    
    public InitialisatorMethodInvocation getInstance() {
        return getInstance((String)null);
    }
    public InitialisatorMethodInvocation getInstance(String identifier) {
        if (usesIdentifier())            
            return createInstance(getUniqueIdentifierArgument(identifier));
        else 
            return createInstance(EmptyArguments.getInstance());
    }
}
