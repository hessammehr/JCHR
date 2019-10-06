package compiler.CHRIntermediateForm.builder.tables;

import static compiler.CHRIntermediateForm.builder.tables.ClassTable.getStaticImportBase;

import java.util.HashSet;
import java.util.Set;

import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.id.Identifier;
import compiler.CHRIntermediateForm.members.Method;

public class MethodTable extends MemberTable<HashSet<Method>> {

    /**
     * <p>
     * Performs a single-static-import. Quoting the Java Language
     * Specification:
     * </p>
     * <p>
     * A single-static-import declaration imports all [...] 
     * static [member methods] with a given simple name from a type. 
     * This makes these static [member methods] available under their 
     * simple name [...].
     * </p>
     * <p>
     * The [<code>id</code>] must be start with the canonical name 
     * of a class or interface type; [an exception is thrown] if 
     * the named type does not exist. [...] 
     * [<code>id</code>] must name at least one static [member methods] of 
     * the named type; [The import does not succeed] if there is 
     * no [member method] of that name [...].
     * </p>
     * [...]
     * </p>
     * <p>
     * A single-static-import declaration [...] that imports a method 
     * named <i>n</i> with signature <i>s</i> shadows the declaration 
     * of any static method named <i>n</i> with signature <i>s</i> 
     * imported by a static-import-on-demand declaration [...].
     * </p>
     * <p>
     * [...]
     * </p>
     * <p>
     * Note that it is permissable for one single-static-import declaration 
     * to import [...] several methods with the same name and signature.
     * </p>
     * <p>
     * [...]
     * </p>
     * 
     * @param id
     *  The fully qualified name of at least one static member method.
     *  
     * @return True if the import succeeded. False otherwise. This means that
     *  if the identifier does name a valid type (i.e. does no
     *  <code>ClassNotFoundException</code> or <code>DuplicateIdentifierException</code>
     *  is thrown), but there is no member method with the given name, <code>false</code>
     *  is returned (no exception!).
     * 
     * @throws ClassNotFoundException
     *  If no class is found with the &quot;body&quot; of the given 
     *  identifier.
     * @throws IllegalIdentifierException
     *  If the given identifier starts with the identifier of a 
     *  primitive type or a type from the nameless package (cannot
     *  be imported).
     *  
     * @see StaticImporter#importSingleStatic(String)
     */    
    public boolean importSingleStatic(String id) 
    throws ClassNotFoundException, IllegalIdentifierException {
        return importSingleStatic(
            getStaticImportBase(Identifier.getBody(id)), 
            Identifier.getTail(id)
        );
    }
    
    /**
     * Statically imports all static methods with the given name from a given
     * base class. For more information on single static imports, we 
     * refer to the specification of {@link #importSingleStatic(String)}.
     * 
     * @param name
     *  The name of at least one static member method.
     *  
     * @return True if the import succeeded. False if there is 
     *  no static member method with the given name.
     * 
     * @see #importSingleStatic(String)
     * @see StaticImporter#importSingleStatic(String)
     */
    public boolean importSingleStatic(Class<?> base, String name) { 
        HashSet<Method> methods = new HashSet<Method>();
        Method.addStaticMethods(methods, base, name, true);
        if (methods.isEmpty()) return false;
        
        if (isDeclaredId(name))
            get(name).addAll(methods);
        else
            declareSafe(name, methods);
        
        return true;
    }

    @SuppressWarnings("unchecked")
    public Set<Method> getMethods(String id) {
        HashSet<Method> result = get(id);
        if (result == null) {
            result = new HashSet<Method>(); 
        } else {
            result = (HashSet<Method>)result.clone();
        }
        for (Class<?> demand : getOnDemandImports())
            Method.addStaticMethods(result, demand, id, true);
        return result;
    }
    
    @Override
    public boolean isImported(String id) {
        if (! isDeclaredId(id)) return false;
        for (Method method : get(id))
            if (method.isStaticallyImported()) return true;
        return false;
    }
    
    @Override
    public boolean isImportedOnDemand(String id) {
        for (Class<?> demand : getOnDemandImports())
            if (Method.hasStaticMethod(demand, id))
                return true;
        return false;
    }
    
    /**
     * Checks whether or not a method with the given identifier
     * is known. This can be a either statically imported one, or
     * maybe an implicit member method (future). 
     * 
     * @param id
     *  The name the method should have.
     *  
     * @return True iff a method with the given identifier is known.
     */
    public boolean isMethod(String id) {
        return isDeclaredId(id) || isImportedOnDemand(id);
    }
}