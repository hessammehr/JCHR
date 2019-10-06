package compiler.CHRIntermediateForm.builder.tables;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import compiler.CHRIntermediateForm.exceptions.AmbiguousIdentifierException;
import compiler.CHRIntermediateForm.exceptions.DuplicateIdentifierException;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.id.Identifier;
import compiler.CHRIntermediateForm.types.PrimitiveType;

/**
 * @author Peter Van Weert
 */
public class ClassTable extends SymbolTable<Class<?>> {
    
    public ClassTable() {
        setOnDemandImports(new HashMap<String, OnDemandImport>(4));
        importOnDemand("java.lang");
    }
    
    private Map<String, OnDemandImport> onDemandImports;
    
    static Class<?> getStaticImportBase(String id) 
    throws ClassNotFoundException, IllegalIdentifierException, NullPointerException {
        return forName(getStaticImportBaseId(id));
    }
    
    private static String getStaticImportBaseId(String id) 
    throws IllegalIdentifierException, NullPointerException {
        if (id.length() == 0)
            throw new IllegalIdentifierException("Static import %s cannot be resolved", id);
        testStaticImportBaseId(id);
        return id;
    }
    
    private static void testStaticImportBaseId(String id) 
    throws IllegalIdentifierException, NullPointerException {
        if (Identifier.isSimple(id)) {
            if (PrimitiveType.isPrimitiveType(id))
                throw new IllegalIdentifierException(
                    "Static import only from classes and interfaces (%s is a primitive type)", id
                );
            
            throw new IllegalIdentifierException(
                "Cannot import classes from the nameless package (%s)", id
            );
        }
    }
    
   /**
    * <p>
    * Returns the <code>Class</code> object associated with the class or
    * interface with the given string name.<br/>
    * Given the fully qualified name for a class or interface this method 
    * attempts to locate, load, and link the class or interface.
    * The class loader returned by 
    * <pre>ClassTable.class.getClassLoader()</pre>
    * is used to load the class. The class is not initialized.
    * </p>
    * <p><em>
    * Unlike the <code>Class.forName</code> methods, the format used
    * to represent an inner member class or interface does not <em>have</em>
    * to be the binary name, but the fully qualified name (also known as the
    * cannonical name) can <em>also</em> be used.
    * To import e.g. {@link Map.Entry} interface, you can use:
    *   <pre>   ClassTable.forName("java.util.Map.Entry")</pre>
    * as well as:
    *   <pre>   ClassTable.forName("java.util.Map$Entry")</pre>
    * The former will <em>not</em> work with <code>Class.forName</code>.
    * </em></p>
    * <p>
    * This method cannot be used to obtain any of the 
    * <code>Class</code> objects representing primitive types or void.
    * </p>
    * <p>
    * If <code>id</code> denotes an array class, the component type of
    * the array class is loaded but not initialized.
    * </p>
    * 
    * @param id
    *   The fully qualified name (or binary name) of the desired 
    *   (reference) class. 
    * 
    * @return The <code>Class</code> object for the class with the
    *   specified name.
    * 
    * @exception LinkageError if the linkage fails
    * @exception ExceptionInInitializerError 
    *   If the initialization provoked by this method fails
    * @exception ClassNotFoundException 
    *   If the class cannot be located
    * @exception NullPointerException
    *   If the given identifier is <code>null</code>.
    */ 
    public static Class<?> forName(String id) 
    throws ClassNotFoundException, NullPointerException {
        try {
            return forName0(id);
        } catch (ClassNotFoundException cnfe) {
            // It might still be the identifier of an inner class:
            Class<?> result = forNameRec(id);
            
            // If not, we should re-throw the caught exception!
            if (result == null) throw cnfe;
            
            return result;
        }
    }
    protected static Class<?> forNameOrNull(String id) {
        try {
            return forName0(id);
        } catch (ClassNotFoundException cnfe) {
            // It might still be the identifier of an inner class:
            return forNameRec(id);
        }
    }
    protected static boolean testForName(String id) {
        return forNameOrNull(id) != null;
    }
    protected static Class<?> forName0(String id) throws ClassNotFoundException {
        return Class.forName(id, false, ClassTable.class.getClassLoader());
    }
    
    /**
     * <p>
     * This method (recursively) determines whether the given 
     * identifier denotes an inner class. If not, the result
     * will be <code>null</code>.
     * </p>
     * <p>
     * In retrospect, a non-recursive algorithm might be slightly
     * efficient, but the difference is marginal.
     * </p>
     * 
     * @param id
     *  The identifier we need to test whether it is an
     *  identifier of an inner class or not.
     * @return The class object reflecting the inner class
     *  identified by the given identifier or <code>null</code>
     *  if no such class exists or can be found. 
     */
    private static Class<?> forNameRec(String id) {
        String body = Identifier.getBody(id);
        
        // If the given identifier is composed:
        if (body.length() != 0) {
            Class<?> outer;
            
            try {
                // Base case: 
                //  The body of the identifier is an outer type.
                //  Note that a type identifier and a package
                //  identifier are not allowed to collide, meaning
                //  that if this succeeds this *has* to be an
                //  outer class and not some inner class of a
                //  class identified by a prefix of body!
                outer = forName0(body); 
            } catch (ClassNotFoundException cnfe) {
                // Recursive case:
                //  The <body> might itself be an identifier of an
                //  inner class.
                outer = forNameRec(body);
            }

            // If an outer class is found we still need to check
            // the full identifier is really an inner class of <outer>
            // (note that it is no problem if <outer> is an 
            // inner class!)
            if (outer != null) try {
                return forName0(outer.getName() + '$' + Identifier.getTail(id)); 
            } catch (ClassNotFoundException cnfe) {
                // If not: <id> cannot be a class identifier...
                return null;
            } else {
                // If no <outer> is found, <id> cannot be 
                // an inner class identifier!
                return null;
            }
        } else {
            // The identifier is not composed: since we cannot
            // import classes from the unnamed package, the result
            // has to be <null>.
            return null;
        }
    }
    
    public Class<?> getClass(String id) 
    throws ClassNotFoundException, AmbiguousIdentifierException {
        // If it is a known class (fqn or not), things are easy:
        if (isDeclaredId(id)) return super.get(id);
        
        // Cache the result for future use (it is safe to do this
        // here, since the result is always unambiguous):

        // First we see whether it is part of the current package:
        Class<?> result = getClassOrNull0(id);
        if (result != null) return ensureDeclared(id, result); 
        
        // Else we have to try imports on demand, inner classes, etc:
        result = getClassOrNull(id);
        if (result != null) return ensureDeclared(id, result);
        
        throw new ClassNotFoundException(id);
    }
    
    private Class<?> getClassOrNull0(String id) {
        if (getCurrentPackage().equals(UNNAMED_PACKAGE_ID))
            return getClassOrNull2(id);
        else
            return getClassOrNull2(getCurrentPackage() + '.' + id);
    }
    
    public final static String UNNAMED_PACKAGE_ID = "";
    
    private String currentPackage;
    
    public void setCurrentPackage(String packageName) 
    throws IllegalIdentifierException, NullPointerException {
        if (! packageName.equals(UNNAMED_PACKAGE_ID))
            Identifier.testIdentifier(packageName);
        this.currentPackage = packageName;
    }
    
    public String getCurrentPackage() {
        return currentPackage;
    }

    /**
     * This method deals with import-on-demands, inner class identifiers,
     * fully qualified names, ...
     * 
     * @pre The given identifier is not declared (i.e. the class has not
     *  been imported using its fully qualified name).
     * 
     * @param id
     *  The identifier we are investigating the class-known-problem for.
     * @return A class reflecting a type with the given identifier,
     *  using the current database of import-on-demand prefixes.
     * @throws AmbiguousIdentifierException
     *  An example will clarify this: suppose <code>id</code> is 
     *  <code>&quot;List&quot;</code> and: 
     *  <pre>
     *      import java.awt.*;
     *      import java.util.*;
     *  </pre>
     */
    private Class<?> getClassOrNull(String id) throws AmbiguousIdentifierException {
        // If it is a simple identified class (not imported, cf precondition)
        // we need to check all import-on-demands:
        if (Identifier.isSimple(id)) {
            return getClassOrNull1(id);
        } else {
            // If it is a composed identifier it might be a fully qualified name
            // (inner type or not):
            Class<?> result = getClassOrNull2(id);
            if (result != null) return result;
            
            // It might also be an identifier of an inner type (nested arbitrary
            // deep), prefixed at some point by a simple identifier (i.e.
            // not fully qualified):
            Class<?> outer = getClassOrNull1(Identifier.getHead(id));
            if (outer == null) return null;
            
            // If the head of the id is an identifier of an outer class, we
            // should check whether the entire id is really an inner class: 
            try {
                return forName(outer.getName() + Identifier.getTorso(id));
            } catch (ClassNotFoundException cnfe) {
                return null;
            }
        }
    }
    
    /*
     * @pre simpleId is a simple identifier
     */
    private Class<?> getClassOrNull1(String simpleId) throws AmbiguousIdentifierException {
        Class<?> temp, result = null;
        
        for (OnDemandImport onDemandImport : getOnDemandImports().values()) {
            if ((temp = getClassOrNull2(onDemandImport + simpleId)) != null) {
                if (onDemandImport.isStatic() && ! isStatic(temp))
                    continue;
                if (result != null && !temp.equals(result))
                    throw new AmbiguousIdentifierException(
                        simpleId + " could be both " + result + " or " + temp
                    );
                result = temp;
            }
        }
        
        return result;
    }
    
    public static boolean isStatic(Class<?> clazz) {
        return Modifier.isStatic(clazz.getModifiers());
    }
    
    /*
     * @pre composedId is een composed identifier
     */
    private Class<?> getClassOrNull2(String composedId) {
        try {
            // Cache the result for future use (since this is a
            // composed identifier this can never pose a problem)
            return ensureDeclared(composedId, forName(composedId));
        } catch (ClassNotFoundException cnfe) {
            return null;
        }
    }
    
    /**
     * Deals with a import-on-demand declaration. A type-import-on-demand 
     * declaration allows all accessible types declared in the type 
     * or package named by a canonical name to be imported as needed.
     * Two or more type-import-on-demand declarations may name the same 
     * type or package.
     * A type-import-on-demand declaration never causes any other
     * declaration to be shadowed.
     *  
     * @param id
     *  The canonical name of a package or a type.
     */
    /*
     * TODO
     *  check whether the package or the type is "accessible"...
     */
    public void importOnDemand(String id) {
        testImportOnDemand(id);
        getOnDemandImports().put(id, new OnDemandImport(id, false));
    }
    
    public void testImportOnDemand(String id) {
        Class<?> clazz = forNameOrNull(id);
        if (clazz == null) return;
        
        if (clazz.getClasses().length == 0) 
            System.err.printf(
                "Warning: import %s has no member classes. " +
                "Maybe you intended a static import?%n", clazz.getCanonicalName()
            );
    }
    
    
    /**
     * <p>
     * Performs a static-import-on-demand. Quoting the Java Language
     * Specification:
     * </p>
     * <p>
     * A static-import-on-demand declaration allows all [...] [static 
     * member types] declared in the type named by a canonical name 
     * to be imported as needed.
     * </p>
     * <p>
     * [An exception is thrown if] a static-import-on-demand declaration 
     * [names] a type that does not exist [...]. Two or more 
     * static-import-on-demand declarations [...] may name the 
     * same type; the effect is as if there was exactly 
     * one such declaration. Two or more static-import-on-demand 
     * declarations [...] may name the same [type]; the effect is 
     * as if the member was imported exactly once.
     * </p>
     * <p>
     * Note that it is permissable for one static-import-on-demand 
     * declaration to import several [...] types with the same 
     * name, [...].
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
     * @see StaticImporter#importStaticOnDemand(String)
     */
    public void importStaticOnDemand(String id) 
    throws IllegalIdentifierException, ClassNotFoundException {
        // test whether this is a valid id to be as a static import
        getStaticImportBase(id);
        _importStaticOnDemand(id);
    }
    
    /**
     * Statically imports, on demand, all static inner types of a
     * given base class. The base class should not represent
     * a primitive type, an array type, or any other type that does
     * not make sense.
     * 
     * @param base
     *  The base class of which we are importing the static 
     *  inner types of (on demand).
     */
    public void importStaticOnDemand(Class<?> base) {
        _importStaticOnDemand(base.getCanonicalName());
    }    
    
    void _importStaticOnDemand(String id) { 
        OnDemandImport staticImport = new OnDemandImport(id, true);
        OnDemandImport previous = getOnDemandImports().put(id, staticImport);
        
        // If a non-static import on demand was already made with the 
        // same identifier ...
        if (previous != null && ! previous.isStatic())
            staticImport.setStatic(false);
    }

    /**
     * <p>
     * Deals with a single-type-import declaration. Quoting the 
     * Java Language Specification:
     * </p>
     * <p> 
     * <i>
     *  A single-type-import declaration <em>d</em> 
     *  in a compilation unit <em>c</em> of package <em>p</em> 
     *  that imports a type named <em>n</em> shadows the 
     *  declarations of:
     *   <ul>
     *      <li>
     *          any top level type named <em>n</em> declared in another 
     *          compilation unit of <em>p</em>.
     *      </li>
     *      <li>
     *          any type named <em>n</em> imported by a type-import-on-demand 
     *          declaration in <em>c</em>.
     *      </li>
     *      <li>          
     *          any type named <em>n</em> imported by a static-import-on-demand 
     *          declaration in <em>c</em>.
     *      </li>
     *  </ul>
     * </i>
     * </p>
     * <p>
     * Note that in the current version only the second bullet matters,
     * but in future versions both other will also be respected.  
     * </p>
     * 
     * @param id
     *  The canonical name of the class or interface to be imported.
     *  Does <em>not</em> have to be a top level type.
     *
     * @throws ClassNotFoundException
     *  If the class is not found by this class's classloader.
     * @throws NoClassDefFoundError
     *  If the Java Virtual Machine or a <code>ClassLoader</code> instance
     *  tries to load in the definition of a class and no definition of 
     *  the class could be found.
     * @throws IllegalIdentifierException
     *  If the given identifier is a simple name: no classes from the
     *  nameless package can be imported.
     * @throws DuplicateIdentifierException
     *  If another type with the same simple name has already been 
     *  imported. Note that it is not a problem to import the
     *  same type more then once using a single-type-import.
     */
    /*
     * TODO
     *   - make sure the bullets are ok when introducing packages
     *      and/or static imports
     *   - make sure that non-top-level types are accessible
     *      (maybe wait until package is introduced)
     */
    public void importSingleType(String id) 
    throws ClassNotFoundException, NoClassDefFoundError, 
        IllegalIdentifierException, DuplicateIdentifierException {
        
        // If the class was already declared, simply return
        if (isDeclaredId(id)) return;

        // Test whether this is a valid identifier
        testStaticImportBaseId(id);
        
        // Lookup the class (can throw exception/error)
        Class<?> theClass = forName(id);
        
        // We know it is safe to declare the class 
        //  (from test two lines earlier).
        declareSafe(id, theClass);
        
        // Declaring the simple name on the other hand could fail, e.g.:
        //      import java.util.List;
        //      import java.awt.List;
        // Note that it is (and this is correct) not a problem to do e.g.
        //      import java.util.*;
        //      import java.awt.List;
        singleImport(Identifier.getTail(id), theClass);
    }
    
    /**
     * <p>
     * Performs a single-static-import. Quoting the Java Language
     * Specification:
     * </p>
     * <p>
     * A single-static-import declaration imports [the] [...] 
     * static [member type] with a given simple name from a type.
     * This makes [this] static [member type] available under [its] 
     * simple name [...].
     * </p>
     * <p>
     * The [<code>id</code>] must start with the canonical name 
     * of a class or interface type; [an exception is thrown] if 
     * the named type does not exist. [...] 
     * [<code>id</code>] must name [a static member type];
     * [the import does not succeed] if there is no member [type] 
     * of that name [...].
     * </p>
     * <p>
     * [...]
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
     * to import several [...] types with the same name [...].
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
     *  The fully qualified name of a static member type.
     *  
     * @return True if the import succeeded. False otherwise. This means that
     *  if the identifier does name a valid type (i.e. does no
     *  <code>ClassNotFoundException</code> or <code>DuplicateIdentifierException</code>
     *  is thrown), but there is no member type with the given name, <code>false</code>
     *  is returned (no exception!).
     * 
     * @throws ClassNotFoundException
     *  If no class is found with the &quot;body&quot; of the given 
     *  identifier.
     * @throws DuplicateIdentifierException
     *  If the identifier names a type and another type has already 
     *  been imported with  the same simple name using a 
     *  single-type-declaration or another single-static declaration.
     * @throws IllegalIdentifierException
     *  If the given identifier starts with the identifier of a 
     *  primitive type or a type from the nameless package (cannot
     *  be imported).
     *  
     * @see StaticImporter#importSingleStatic(String)
     */
    public boolean importSingleStatic(String id) 
    throws ClassNotFoundException, IllegalIdentifierException, DuplicateIdentifierException {
        return isDeclaredId(id) // If the class was already declared, simply return true
            || _importSingleStatic(
                    id,
                    getStaticImportBase(Identifier.getBody(id)),
                    Identifier.getTail(id)
                );
    }
    
    /**
     * Statically imports a static type with the given name from a given
     * base class. For more information on single static imports, we 
     * refer to the specification of {@link #importSingleStatic(String)}.
     * 
     * @param name
     *  The of a static inner type.
     * @param base
     *  The class we are importing a static inner type from.
     * 
     * @return True if the import succeeded. False if there is no 
     *  static inner type with the given name.
     * 
     * @throws DuplicateIdentifierException
     *  If the name is the name of a static inner type, but another type 
     *  has already been imported with the same name using another 
     *  single-static declaration, or using a single-type import.
     * 
     * @see #importSingleStatic(String)
     * @see StaticImporter#importSingleStatic(String)
     */
    public boolean importSingleStatic(Class<?> base, String name) 
    throws DuplicateIdentifierException {
        return importSingleStatic(base.getCanonicalName() + name, base, name);
    }
    
    /**
     * @see #importSingleStatic(Class, String)
     * @see StaticImporter#importSingleStatic(String)
     */
    boolean importSingleStatic(String id, Class<?> base, String name) 
    throws DuplicateIdentifierException {
        return isDeclaredId(id) // If the class was already declared, simply return true
            || _importSingleStatic(id, base, name);
    }
    
    /**
     * @pre ! isDeclaredId(id)
     * 
     * @see #importSingleStatic(String)
     * @see #importSingleStatic(String, Class)
     */
    private boolean _importSingleStatic(String id, Class<?> base, String name)
    throws DuplicateIdentifierException {
        try {            
            // Lookup the class (may throw exception/error, 
            // should be caught though)
            Class<?> clazz = forName0(base.getName() + '$' + name);
            
            // It has to be a static class
            if (! isStatic(clazz)) return false;
            
            // We know it is safe to declare the class 
            //  (precondition).
            declareSafe(id, clazz);
            
            // Declaring the simple name on the other hand could fail,
            // when colliding with another single type import
            // (static or not) --> this exception must not be caught!
            singleImport(name, clazz);
            
            return true;
            
        } catch (ClassNotFoundException cnfe) {
            // static import was not a class...
            return false;
        }
    }
    
    
    
    /**
     * Performs the actual importing of a class with the given
     * simple name (we could ask the class object its name as well
     * of course, but who cares). Since it is possible to import
     * the same type more then once using different fully qualified
     * names (can e.g. happen with static types that are imported
     * using a subclasses' fully qualified name) we need an extra
     * check here to see whether a <em>different</em> class has 
     * already been imported with the given simple name. 
     * 
     * @param name
     *  The simple name of the class (will be the same as
     *  <code>theClass.getSimpleName()</code>).
     * @param theClass
     *  The class that has to be imported.
     *  
     * @throws DuplicateIdentifierException
     *  If there is already <em>another</em> class imported with
     *  the given simple name (using a single import).
     */
    protected void singleImport(String name, Class<?> theClass) 
    throws DuplicateIdentifierException {
        Class<?> someClass = get(name);
        if (someClass != null && ! someClass.equals(theClass))
            throw new DuplicateIdentifierException(
                "Cannot import %s as %s is already used to import %s",
                theClass.getName(), name, someClass.getName()
            );
        declareSafe(name, theClass);
    }

    /**
     * Checks whether the given id is an imported class, be it using
     * a simple-type-import or a import-on-demand declaration. It will
     * also return <code>true</code> if it is an ambiguously imported
     * type identifier. In other words: it checks whether the class
     * refered to by the given identifier (if any) can be referenced
     * (ambiguously or not) using its simple name.  
     * 
     * @param id
     *  The identifier.
     * @return True if and only if the class
     *  refered to by the given identifier (if any) can be referenced
     *  (ambiguously or not) using its simple name. False otherwise.
     */
    /*
     * cross-reference: this method is used to test whether a
     *  variable identifier hides a type identifier.
     */
    public boolean isImported(String id) {
        return checkImportedness(id, true); 
    }
    
    /**
     * Checks whether the class refered to by the given identifier 
     * (if any) can be referenced unambiguously using its simple name.  
     * 
     * @param id
     *  The identifier.
     * @return True if and only if the class refered to by the 
     *  given identifier (if any) can be referenced unambiguously
     *  using its simple name. False otherwise.
     */
    public boolean isUnambiguouslyImported(String id) {
        return checkImportedness(id, false); 
    }
    
    private boolean checkImportedness(String id, boolean whatIfAmbiguous) {
        try {
            id = Identifier.getTail(id);
            return isDeclaredId(id)
                || (getClassOrNull1(id) != null);
            
        } catch (AmbiguousIdentifierException e) {
            return whatIfAmbiguous;
        }
    }

    public Map<String, OnDemandImport> getOnDemandImports() {
        return onDemandImports;
    }
    protected void setOnDemandImports(Map<String, OnDemandImport> importedPackages) {
        this.onDemandImports = importedPackages;
    }
    
    final static class OnDemandImport {
        private boolean _static;
        private String id;
        
        public OnDemandImport(String id, boolean _static) {
            setId(id);
            setStatic(_static);
        }
        
        public boolean isStatic() {
            return _static;
        }
        public void setStatic(boolean _static) {
            this._static = _static; 
        }
        
        public String getId() {
            return id;
        }
        private void setId(String id) {
            this.id = id;
        }
        
        @Override
        public String toString() {
            return getId() + '.';
        }
        
        @Override
        public int hashCode() {
            return getId().hashCode();
        }
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof OnDemandImport)
                && getId().equals(((OnDemandImport)obj).getId());
        }
    }
}