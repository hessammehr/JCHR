package compiler.CHRIntermediateForm;

import java.util.ArrayList;
import java.util.List;

import compiler.CHRIntermediateForm.exceptions.DuplicateIdentifierException;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.id.AbstractIdentified;
import compiler.CHRIntermediateForm.id.Identifier;
import compiler.CHRIntermediateForm.modifiers.IModified;
import compiler.CHRIntermediateForm.modifiers.IllegalAccessModifierException;
import compiler.CHRIntermediateForm.modifiers.IllegalModifierException;
import compiler.CHRIntermediateForm.modifiers.Modifier;
import compiler.CHRIntermediateForm.types.TypeParameter;

/**
 * @author Peter Van Weert
 */
public class Handler extends AbstractIdentified implements IModified {
    private String packageName;
    
    private int modifiers;
    
    private List<TypeParameter> typeParameters; 
    
    public Handler(String identifier, String packageName) 
    throws IllegalIdentifierException {
        super(identifier);
        setTypeParameters(new ArrayList<TypeParameter>());
        setPackageName(packageName);
    }
    
    public Handler(String identifier, String packageName, int modifiers) 
    throws IllegalIdentifierException, IllegalModifierException {
        this(identifier, packageName);
        changeModifier(modifiers);
    }
    
    protected void changeModifier(int modifiers) throws IllegalModifierException {
        if (Modifier.isDefaultAccess(modifiers)
                    || java.lang.reflect.Modifier.isPublic(modifiers))
            setModifiers(modifiers);
        else
            throw new IllegalAccessModifierException(modifiers);
    }
    
    public void addTypeParameter(TypeParameter typeParameter) 
    throws DuplicateIdentifierException {        
        if (hasAsTypeParameter(typeParameter.getIdentifier()))
            throw new DuplicateIdentifierException(typeParameter.getIdentifier());
        typeParameters.add(typeParameter);
    }
    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }    
    public int getNbTypeParameters() {
        return getTypeParameters().size();
    }
    public boolean hasTypeParameters() {
        return getNbTypeParameters() > 0;
    }
    public TypeParameter getTypeParameter(String name) {
        for (TypeParameter typeParameter : getTypeParameters())
            if (typeParameter.getIdentifier().equals(name))
                return typeParameter;
        return null;
    }
    public boolean hasAsTypeParameter(String name) {
        return (getTypeParameter(name) != null);
    }
    protected void setTypeParameters(List<TypeParameter> typeParameters) {
        this.typeParameters = typeParameters;
    }
    
    /**
     * A handler's identifier has to be a valid user-defined
     * identifier, not starting with an upper-case.
     * 
     * @return Returns whether the given identifier is a valid user-defined
     *  identifier (cf. {@link Identifier#isValidUdSimpleIdentifier(String)}),
     *  not starting with an upper-case. 
     * 
     * @see Identifier#isValidUdSimpleIdentifier(String)
     * @see Identifier#startsWithLowerCase(String)
     */
    @Override
    public boolean canHaveAsIdentifier(String identifier) {
        return Identifier.isValidUdSimpleIdentifier(identifier)
            && Identifier.startsWithLowerCase(identifier);
    }
    
    protected void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public String getPackageName() {
        return packageName;
    }
    
    public int getModifiers() {
        return modifiers;
    }
    protected void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }
}
