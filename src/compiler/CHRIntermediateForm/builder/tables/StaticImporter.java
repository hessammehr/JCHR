package compiler.CHRIntermediateForm.builder.tables;

import static compiler.CHRIntermediateForm.builder.tables.ClassTable.getStaticImportBase;
import compiler.CHRIntermediateForm.exceptions.DuplicateIdentifierException;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.id.Identifier;

/**
 * A helper class that helps with static imports. 
 *  
 * @author Peter Van Weert
 * 
 * @see compiler.CHRIntermediateForm.builder.tables.ClassTable
 * @see compiler.CHRIntermediateForm.builder.tables.FieldTable
 * @see compiler.CHRIntermediateForm.builder.tables.MethodTable
 */
public class StaticImporter {
    
    private ClassTable classTable;
    
    private FieldTable fieldTable;
    
    private MethodTable methodTable;
    
    public StaticImporter(ClassTable classTable, FieldTable fieldTable, MethodTable methodTable) {
        setClassTable(classTable);
        setFieldTable(fieldTable);
        setMethodTable(methodTable);
    }

    /**
     * <p>
     * Performs a single-static-import. Quoting the Java Language
     * Specification:
     * </p>
     * <p>
     * A single-static-import declaration imports all [...] 
     * static members with a given simple name from a type. 
     * This makes these static members available under their 
     * simple name [...].
     * </p>
     * <p>
     * The [<code>id</code>] must be start with the canonical name 
     * of a class or interface type; [an exception is thrown] if 
     * the named type does not exist. [...] 
     * [<code>id</code>] must name at least one static member of 
     * the named type; [an exception is thrown] if there is 
     * no member of that name [...].
     * </p>
     * <p>
     * A single-static-import declaration [...] that imports a field 
     * named <i>n</i> shadows the declaration of any static field 
     * named <i>n</i> imported by a static-import-on-demand declaration 
     * [...].
     * </p>
     * <p>
     * A single-static-import declaration [...] that imports a method 
     * named <i>n</i> with signature <i>s</i> shadows the declaration 
     * of any static method named <i>n</i> with signature <i>s</i> 
     * imported by a static-import-on-demand declaration [...].
     * </p>
     * <p>
     * A single-static-import declaration [...] that imports a type named 
     * <i>n</i> shadows the declarations of:
     * </p>
     * <ul>
     * <li>
     *  any static type named <i>n</i> imported by a static-import-on-demand 
     *  declaration [...].
     * </li>
     * <li>
     *  [...]
     * </li>
     * <li>
     *  any type named <i>n</i> imported by a type-import-on-demand declaration 
     *  [...].
     * </li>
     * </ul>
     * <p>
     * [...].
     * </p>
     * <p>
     * Note that it is permissable for one single-static-import declaration 
     * to import several fields or types with the same name, or several 
     * methods with the same name and signature.
     * </p>
     * <p>
     * If [...] both a single-static-import declaration 
     * that imports a type whose simple name is <i>n</i>, and a single-type-import 
     * declaration that imports a type whose simple name is <i>n</i> [are made], 
     * [an exception is thrown].
     * </p>
     * <p>
     * [...]
     * </p>
     * 
     * @param id
     *  An identifier that has to start with the fully
     *  qualified name of a type, followed by a dot and the name of
     *  at least one static member or type if this type. 
     * 
     * @throws ClassNotFoundException
     *  If no class is found with the &quot;body&quot; of the given 
     *  identifier.
     * @throws DuplicateIdentifierException
     *  If the identifier names a field and another field has already
     *  been imported with the same simple name, or if the identifier
     *  names a type and another type has already been imported with
     *  the same simple name using a single-type-declaration or another
     *  single-static declaration.
     * @throws IllegalIdentifierException
     *  If the given identifier starts with the identifier of a 
     *  primitive type or a type from the nameless package (cannot
     *  be imported). An <code>IllegalIdentifierException</code>
     *  is also thrown if the given identifier does start with the fully
     *  qualified name of a type, followed by a dot and a simple name, 
     *  but this simple name does not denote a static member or type.
     *  
     * @see ClassTable#importSingleStatic(String)
     * @see FieldTable#importSingleStatic(String)
     * @see MethodTable#importSingleStatic(String)
     */
    public void importSingleStatic(String id) 
    throws ClassNotFoundException, IllegalIdentifierException,
    DuplicateIdentifierException {
        
        String name = Identifier.getTail(id);
        Class<?> base = getStaticImportBase(Identifier.getBody(id));
        
        boolean imported = false;
        
        imported |= getClassTable().importSingleStatic(id, base, name);
        imported |= getFieldTable().importSingleStatic(base, name);
        imported |= getMethodTable().importSingleStatic(base, name);
        
        if (! imported)
            throw new IllegalIdentifierException("Cannot resolve static import: " + id);
    }
    
    /**
     * <p>
     * Performs a static-import-on-demand. Quoting the Java Language
     * Specification:
     * </p>
     * <p>
     * A static-import-on-demand declaration allows all [...] static members 
     * declared in the type named by a canonical name to be imported 
     * as needed.
     * </p>
     * <p>
     * [An exception is thrown if] a static-import-on-demand declaration 
     * [names] a type that does not exist [...]. Two or more 
     * static-import-on-demand declarations [...] may name the 
     * same type or package; the effect is as if there was exactly 
     * one such declaration. Two or more static-import-on-demand 
     * declarations [...] may name the same member; the effect is 
     * as if the member was imported exactly once.
     * </p>
     * <p>
     * Note that it is permissable for one static-import-on-demand 
     * declaration to import several fields or types with the same 
     * name, or several methods with the same name and signature.
     * </p>
     * <p>
     * If [...] both a static-import-on-demand declaration and a 
     * type-import-on-demand declaration that name the same type [are
     * made], the effect is as if the static member types of that 
     * type were imported only once.
     * </p>
     * <p>
     * A static-import-on-demand declaration never causes any 
     * other declaration to be shadowed.
     * </p>
     * 
     * @param id
     *  The fully qualified name of a type.
     *  
     * @throws ClassNotFoundException
     *  If the given identifier is not the fully qualified name name of a type.
     * @throws IllegalIdentifierException 
     *  If the given identifier is the fully qualified name name of a primitive
     *  type, or from a type that cannot be imported (e.g. a type
     *  from the nameless package).
     *  
     * @see ClassTable#importStaticOnDemand(String)
     * @see MemberTable#importStaticOnDemand(String)
     */
    public void importStaticOnDemand(String id)
    throws ClassNotFoundException, IllegalIdentifierException {
        
        Class<?> base = getStaticImportBase(id);
        
        getClassTable()._importStaticOnDemand(id);
        getFieldTable().importStaticOnDemand(base);
        getMethodTable().importStaticOnDemand(base);
    }

    protected ClassTable getClassTable() {
        return classTable;
    }
    protected void setClassTable(ClassTable classTable) {
        this.classTable = classTable;
    }

    protected FieldTable getFieldTable() {
        return fieldTable;
    }
    protected void setFieldTable(FieldTable fieldTable) {
        this.fieldTable = fieldTable;
    }

    protected MethodTable getMethodTable() {
        return methodTable;
    }
    protected void setMethodTable(MethodTable methodTable) {
        this.methodTable = methodTable;
    }
}