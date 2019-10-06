package compiler.CHRIntermediateForm.members;

import static compiler.CHRIntermediateForm.init.InitialisatorMethod.getUniqueIdentifierArgument;
import annotations.JCHR_Declare;
import annotations.JCHR_Init;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.arguments.EmptyArguments;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.exceptions.AmbiguityException;
import compiler.CHRIntermediateForm.init.IInitialisatorInvocation;
import compiler.CHRIntermediateForm.init.Initialisator;
import compiler.CHRIntermediateForm.types.GenericType;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.Reflection;

public class Constructor extends Initialisator<Constructor> {
    
    private java.lang.reflect.Constructor<?> constructor;
    
    private GenericType base;
    
    private int identifierIndex;
    
    public Constructor(GenericType base, java.lang.reflect.Constructor<?> constructor) {
        setBase(base);
        setConstructor(constructor);
        initIdentifierIndex();
    }
    
    public boolean isConstructor() {
        return true;
    }

    public IType getType() {
        return getBase();
    }
    protected GenericType getBase() {
        return base;
    }
    protected void setBase(GenericType base) {
        this.base = base;
    }

    protected boolean isInitialisator() {
        return getConstructor().isAnnotationPresent(JCHR_Init.class);
    }
    protected boolean isDeclarator() {
        return getConstructor().isAnnotationPresent(JCHR_Declare.class);
    }
    
    protected void initIdentifierIndex() {
        if (isInitialisator())
            identifierIndex = getConstructor().getAnnotation(JCHR_Init.class).identifier();
        else if (isDeclarator())
            identifierIndex = (getArity() == 1)? 0 : -1;
        else 
            identifierIndex = -1;
    }
    public int getIdentifierIndex() {
        if (!isInitialisator() && !isDeclarator())
            throw new UnsupportedOperationException();
        else
            return identifierIndex;
    }
    protected void setIdentifierIndex(int identifierIndex) {
        this.identifierIndex = identifierIndex;
    }
    @Override
    public boolean usesIdentifier() {
        if (!isInitialisator() && !isDeclarator())
            throw new UnsupportedOperationException();
        else
            return getIdentifierIndex() >= 0;
    }

    public ConstructorInvocation getInstance(IArgument argument) {
        return getInstance(argument, null);
    }
    public ConstructorInvocation getInstance(IArgument argument, String identifier) {
        IArguments arguments = new Arguments(usesIdentifier()? 2 : 1);
        arguments.addArgument(argument);
        
        if (usesIdentifier())
            arguments.addArgumentAt(
                getIdentifierIndex(), getUniqueIdentifierArgument(identifier)
            );
        
        return createInstance(arguments);
    }

    public ConstructorInvocation createInstance(IArguments arguments) {
        return new ConstructorInvocation(this, arguments);
    }

    public IType getFormalParameterTypeAt(int index) {
        return Reflection.reflect(getBase(), getConstructor().getGenericParameterTypes()[index]);
    }
    
    public int getArity() {
        return getConstructor().getParameterTypes().length;
    }

    public java.lang.reflect.Constructor<?> getConstructor() {
        return constructor;
    }
    protected void setConstructor(java.lang.reflect.Constructor<?> constructor) {
        this.constructor = constructor;
    }
    public String getTypeString() {
        return getType().toTypeString();
    } 
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Constructor) 
            && equals((Constructor)obj);  
    }
    
    public boolean equals(Constructor other) {
        return (this == other) || ( other != null
            && this.getConstructor().equals(other.getConstructor())
        );
    }
    
    public static Constructor getDeclaratorInitialisator(GenericType base) 
    throws AmbiguityException {
        Constructor best = null;
        boolean ambiguous = false;
        
        for (Constructor constructor : base.getConstructors()) {
            if (constructor.isValidDeclarationInitialisator()) {                    
                if (best == null)
                    best = constructor;
                else {
                    final boolean
                        x = best.usesIdentifier(),
                        y = constructor.usesIdentifier();
                    
                    if (x == y) {
                        ambiguous = true;
                        continue;
                    }
                    if (y) continue;    // we prefer not to use an identifier!
                    
                    best = constructor;
                    ambiguous = false;
                }
            }
        }
        
        if (ambiguous) throw new AmbiguityException();
    
        return best;
    }
    
    public static Constructor getInitialisatorFrom(GenericType base, IType type)
    throws AmbiguityException {
        Constructor best = null;
        boolean ambiguous = false;
        
        for (Constructor constructor : base.getConstructors()) {
            if (constructor.isInitialisator()) {
                if (constructor.isValidInitialisatorFrom(type)) {
                    if (best == null)
                        best = constructor;
                    else {                    
                        switch (constructor.compareTo(best)) {
                            case BETTER:
                                ambiguous = false;
                                best = constructor;
                            break;
                            
                            case EQUAL:
                            case AMBIGUOUS:
                                ambiguous = true;
                            break;
                        }
                    }
                }
            }
        }
        
        if (ambiguous) throw new AmbiguityException();
        
        return best;
    }
    
    @Override
    public String toString() {
        return "new " + getType().toTypeString() + super.toString();
    }
    
    public boolean isValidInitialisator() {
        return isInitialisator() 
            && Initialisator.hasValidIdentifierParameter(this)
            && getArity() == (usesIdentifier()? 2 : 1);
    }
    
    public boolean isValidDeclarationInitialisator() {
        return isDeclarator()
            && Initialisator.hasValidIdentifierParameter(this)
            && getArity() == (usesIdentifier()? 1 : 0);
    }
    
    public boolean haveToIgnoreImplicitArgument() {
        return false; // heeft er eenvoudigweg geen
    }
    
    public IInitialisatorInvocation<Constructor> getInstance() {
        return getInstance((String)null);
    }
    public IInitialisatorInvocation<Constructor> getInstance(String identifier) {
        if (usesIdentifier())            
            return createInstance(new Arguments(getUniqueIdentifierArgument(identifier)));
        else 
            return createInstance(EmptyArguments.getInstance());
    }
    
    public int getModifiers() {
        return getConstructor().getModifiers();
    }
    
}
