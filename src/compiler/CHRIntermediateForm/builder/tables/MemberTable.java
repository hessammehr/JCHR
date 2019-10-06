package compiler.CHRIntermediateForm.builder.tables;

import static compiler.CHRIntermediateForm.builder.tables.ClassTable.getStaticImportBase;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;

public abstract class MemberTable<T> extends SymbolTable<T> {
    
    private Set<Class<?>> onDemandImports;
    
    public MemberTable() {
        setOnDemandImports(new HashSet<Class<?>>(4));
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
     * declaration to import several fields [...] with the same 
     * name, or several methods with the same name and signature.
     * </p>
     * <p>
     * [...]
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
     *  @see StaticImporter#importStaticOnDemand(String)
     */
    public void importStaticOnDemand(String id)
    throws IllegalIdentifierException, ClassNotFoundException {
        importStaticOnDemand(getStaticImportBase(id));
    }
    
    /**
     * @see #importStaticOnDemand(String)
     * @see StaticImporter#importStaticOnDemand(String) 
     */
    void importStaticOnDemand(Class<?> base) {
        getOnDemandImports().add(base);
    }
    
    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }
    
    protected Set<Class<?>> getOnDemandImports() {
        return onDemandImports;
    }
    protected void setOnDemandImports(Set<Class<?>> onDemandImports) {
        this.onDemandImports = onDemandImports;
    }
    
    /**
     * Checks whether the given id is an imported member through
     * an import-on-demand declaration. It will also return 
     * <code>true</code> if it is an ambiguously imported 
     * identifier.  
     * 
     * @param id
     *  The identifier (a simple name).
     * @return True if and only if the member refered to by the 
     *  given identifier (if any) is imported on demand (might
     *  still be ambiguous!)
     */
    public abstract boolean isImportedOnDemand(String id);
    
    
    /**
     * Checks whether the given id is an imported member through
     * an import-on-demand or a single-import declaration. It will also 
     * return <code>true</code> if it is an ambiguously imported 
     * identifier.  
     * 
     * @param id
     *  The identifier (a simple name).
     * @return True if and only if the member refered to by the 
     *  given identifier (if any) is imported (might
     *  still be ambiguous!)
     */
    public abstract boolean isImported(String id);
}