package compiler.CHRIntermediateForm.members;

import java.lang.reflect.Modifier;

import util.exceptions.IllegalStateException;
import util.exceptions.IndexOutOfBoundsException;

import compiler.CHRIntermediateForm.arg.argument.ClassNameImplicitArgument;
import compiler.CHRIntermediateForm.arg.argumentable.Argumentable;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.matching.MatchingInfos;
import compiler.CHRIntermediateForm.types.GenericType;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.Reflection;

/**
 * @author Peter Van Weert
 */
public class Field extends Argumentable<Field> implements IMember {
    
    private GenericType implicitArgumentType;
    
    private boolean staticallyImported;
    
    private java.lang.reflect.Field field;
    
    /**
     * Creates a <code>Field</code> object that reflects a static field of the given
     * class, with the given name. 
     * 
     * @param clazz
     *  The class of which this object will be reflecting the 
     *  (static) field with the given name (other parameter). 
     * @param name
     *  The name of the (static) field this object will be
     *  reflecting.
     * @throws NoSuchFieldException
     *  If there is no field present with the given name, or if the
     *  field with the given name is not static. 
     */
    public Field(Class<?> clazz, String name) throws NoSuchFieldException {
        this(GenericType.getInstance(clazz), clazz.getField(name));
        if (! isStatic()) {
            throw new NoSuchFieldException(
                name + " is not a static field of " + clazz
            );
        }
    }
    
    /**
     * Creates a <code>Field</code> object that reflects the non-static
     * member field of an object of the given base type with the given name.
     * Note that it is perfectly possible for this field to be a static 
     * field. We could generate a warning if a static field is accessed
     * in a non-static manner like this, but for now we don't.
     * 
     * @param base
     *  The type of the object this <code>Field</code> reflects a member 
     *  field of. 
     * @param name
     *  The name of this <code>Field</code>.
     * @throws NoSuchFieldException
     *  If there is no field present with the given name.
     */
    public Field(GenericType base, String name) throws NoSuchFieldException {
        this(base, base.getRawType().getField(name));
    }
    
    /**
     * Creates a <code>Field</code> object reflecting a given 
     * field of objects of the given base type.
     * 
     * @param base
     *  The type of the objects this <code>Field</code> is reflecting
     *  a field of.
     * @param field
     *  The field this <code>Field</code> is decorating.
     */
    protected Field(GenericType base, java.lang.reflect.Field field) {        
        setImplicitArgumentType(base);
        setField(field);
    }
    
    @Override
    public String toString() {
        return getImplicitArgumentType().toString() + "." + getName();
    }

    public IType getType() {
        return Reflection.reflect(getImplicitArgumentType(), getField().getGenericType());
    }
    
    public GenericType getImplicitArgumentType() {
        return implicitArgumentType;
    }
    protected void setImplicitArgumentType(GenericType implicitArgumentType) {
        this.implicitArgumentType = implicitArgumentType;
    }
    
    public java.lang.reflect.Field getField() {
        return field;
    }
    protected void setField(java.lang.reflect.Field field) {
        this.field = field;
    }
    
    public String getName() {
        return getField().getName();
    }
    
    @Override
    public boolean equals(Object other) {
        return (other instanceof Field)
        	&& this.getField().equals(((Field)other).getField());
    }
    
    @Override
    public IType[] getFormalParameterTypes() {
        return new IType[] { getImplicitArgumentType() };
    }
    @Override
    public IType[] getExplicitFormalParameterTypes() {
        if (haveToIgnoreImplicitArgument())
            return new IType[0];
        else
            return new IType[] { getImplicitArgumentType() };
    }
    public int getArity() {
        return 1;
    }
    
    @Override
    public MatchingInfos canHaveAsArguments(IArguments arguments) {
        if ((arguments.getArity() != 1) || !arguments.hasImplicitArgument())
            return MatchingInfos.NO_MATCH;
        else 
            return new MatchingInfos(true, arguments.getArgumentAt(0).isAssignableTo(getImplicitArgumentType()));
    }
    public IType getFormalParameterTypeAt(int index) {
        if (index != 0) 
            throw new IndexOutOfBoundsException(index, 0, 0);
        else
            return getImplicitArgumentType();
    }
    public FieldAccess createInstance(IArguments arguments) {
        return new FieldAccess(this, arguments);
    }
    
    /**
     * Creates a new field access of a static field.
     * 
     * @pre isStatic()
     * 
     * @return A new field access of a static field.
     */
    public FieldAccess createStaticInstance() {
        if (! isStatic()) throw new IllegalStateException();
        
        return new FieldAccess(
            this, 
            new ClassNameImplicitArgument(getField().getDeclaringClass())
        );
    }
    
    public boolean haveToIgnoreImplicitArgument() {
        return isStatic();
    }
    
    public int getModifiers() {
        return getField().getModifiers();
    }
    public boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }
    public boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }
    
    public boolean isStaticallyImported() {
        return staticallyImported;
    }
    public void setStaticallyImported() {
        staticallyImported = true;
    }
}
