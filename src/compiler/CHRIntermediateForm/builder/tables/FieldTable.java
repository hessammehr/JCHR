package compiler.CHRIntermediateForm.builder.tables;

import compiler.CHRIntermediateForm.exceptions.AmbiguousIdentifierException;
import compiler.CHRIntermediateForm.exceptions.DuplicateIdentifierException;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.id.Identifier;
import compiler.CHRIntermediateForm.members.Field;

import static compiler.CHRIntermediateForm.builder.tables.ClassTable.getStaticImportBase;

public class FieldTable extends MemberTable<Field> {

    /**
     * <p>
     * Performs a single-static-import. Quoting the Java Language
     * Specification:
     * </p>
     * <p>
     * A single-static-import declaration imports [the] [...] 
     * static [member field] with a given simple name from a type.
     * This makes [this] static [member field] available under [its] 
     * simple name [...].
     * </p>
     * <p>
     * The [<code>id</code>] must start with the canonical name 
     * of a class or interface type; [an exception is thrown] if 
     * the named type does not exist. [...] 
     * [<code>id</code>] must name [a static member field];
     * [the import does not succeed] if there is no member [field] 
     * of that name [...].
     * </p>
     * <p>
     * A single-static-import declaration [...] that imports a field 
     * named <i>n</i> shadows the declaration of any static field 
     * named <i>n</i> imported by a static-import-on-demand declaration 
     * [...].
     * </p>
     * <p>
     * [...]
     * </p>
     * <p>
     * Note that it is permissable for one single-static-import declaration 
     * to import several fields [...] with the same name [...].
     * </p>
     * <p>
     * [...]
     * </p>
     * 
     * @param id
     *  The fully qualified name of a static member field.
     *  
     * @return True if the import succeeded. False otherwise. This means that
     *  if the identifier does name a valid type (i.e. does no
     *  <code>ClassNotFoundException</code> or <code>DuplicateIdentifierException</code>
     *  is thrown), but there is no member field with the given name, <code>false</code>
     *  is returned (no exception!).
     * 
     * @throws ClassNotFoundException
     *  If no class is found with the &quot;body&quot; of the given 
     *  identifier.
     * @throws DuplicateIdentifierException
     *  If the identifier names a field and another field has already 
     *  been imported with the same simple name using another single-static 
     *  declaration.
     * @throws IllegalIdentifierException
     *  If the given identifier starts with the identifier of a 
     *  primitive type or a type from the nameless package (cannot
     *  be imported).
     *  
     * @see StaticImporter#importSingleStatic(String)
     */
    public boolean importSingleStatic(String id) 
    throws ClassNotFoundException, 
        DuplicateIdentifierException, 
        IllegalIdentifierException {
        
        return importSingleStatic(
            getStaticImportBase(Identifier.getBody(id)), 
            Identifier.getTail(id)
        );
    }
    
    /**
     * Statically imports the static field with the given name from a given
     * base class. For more information on single static imports, we 
     * refer to the specification of {@link #importSingleStatic(String)}.
     * 
     * @param name
     *  The of a static member field.
     * @param base
     *  The class we are importing a static field from.
     * 
     * @return True if the import succeeded. False if there is no 
     *  static member field with the given name.
     * 
     * @throws DuplicateIdentifierException
     *  If the name is the name of a field and another field has already 
     *  been imported with the same name using another single-static declaration.
     * 
     * @see #importSingleStatic(String)
     * @see StaticImporter#importSingleStatic(String)
     */
    public boolean importSingleStatic(Class<?> base, String name) 
    throws DuplicateIdentifierException {
        try {
            // Can throw a NoSuchFieldException 
            // (alse covers non-static fields!) 
            Field staticField = new Field(base, name);
            
            Field field = get(name);            
            if (field != null) {
                // importing the same field twice is no problem:
                if (field.equals(staticField)) return true;
                
                // importing two fields with the same name is. Of course,
                // the field could be a member (not statically imported)
                // in which case it will hide the newly imported field.
                // The import should succeed nonetheless!
                if (! field.isStaticallyImported())
                    return true;
                
                throw new DuplicateIdentifierException(
                    name + " is already defined in a single static import."
                );
                
            } else {                
                declareSafe(name, staticField);
                return true;
            }            
        } catch (NoSuchFieldException nsfe) {
            return false;
        }
    }
    
    public Field getField(String id) throws AmbiguousIdentifierException {
        Field result = super.get(id);
        
        if (result == null) {
            Field temp;
            for (Class<?> demand : getOnDemandImports()) {
                try {
                    // (also fails if the field is not static!) 
                    temp = new Field(demand, id);
                    if (result != null && !temp.equals(result))
                        throw new AmbiguousIdentifierException(
                            "%s could denote both %s and %s",
                            id, result, temp
                        );
                    result = temp;
                    
                } catch (NoSuchFieldException nsfe) {
                    // NOP
                }
            }
        }
        
        return result;
    }
    
    @Override
    public boolean isImportedOnDemand(String id) {
        // not very nice code here...
        for (Class<?> demand : getOnDemandImports())
            try { new Field(demand, id); return true; }
            catch (NoSuchFieldException nsfe) { /*NOP*/ }
        return false;
    }
    
    @Override
    public boolean isImported(String id) {
        return (isDeclaredId(id) && get(id).isStaticallyImported())
            || isImportedOnDemand(id);
    }
    
    /**
     * Checks whether or not a field with the given identifier
     * is known. This can be a either statically imported one, or
     * maybe an implicit member method (future). It might be
     * an ambiguous name as well, this does not matter for the
     * result of this inspector. 
     * 
     * @param id
     *  The name the field should have.
     *  
     * @return True iff a field with the given identifier is known.
     */
    public boolean isField(String id) {
        return isDeclaredId(id) 
            || isImportedOnDemand(id);
    }
}