package compiler.CHRIntermediateForm.id;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import util.StringUtils;

import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;

/**
 * A class grouping a collection of static methods used for
 * testing and manipulating identifiers.
 * 
 * @author Peter Van Weert
 */
public abstract class Identifier {
    private final static Set<String> RESERVED_WORDS;
    static {
        RESERVED_WORDS = new HashSet<String>(asList(
            // Java keywords / reserved words
            "abstract" ,  "default" ,  "if"         ,  "private"   ,  "this",
            "boolean"  ,  "do"      ,  "implements" ,  "protected" ,  "throw",
            "break"    ,  "double"  ,  "import"     ,  "public"    ,  "throws",
            "byte"     ,  "else"    ,  "instanceof" ,  "return"    ,  "transient",
            "case"     ,  "extends" ,  "int"        ,  "short"     ,  "try",
            "catch"    ,  "final"   ,  "interface"  ,  "static"    ,  "void",
            "char"     ,  "finally" ,  "long"       ,  "strictfp"  ,  "volatile",
            "class"    ,  "float"   ,  "native"     ,  "super"     ,  "while",
            "const"    ,  "for"     ,  "new"        ,  "switch"    ,  "assert",
            "continue" ,  "goto"    ,  "package"    ,  "synchronized",
            
            // Java literals
            "true"     ,  "false"   , "null",
            
            // JCHR keywords
            "handler", 
            "constraint",
            "solver",
            "option",
            "rules", 
            "variable", "local",
            "fail"
        ));
    }

    public static boolean isValidJavaIdentifier(String id) {
        if (id == null) return false;
        
        StringTokenizer st = new StringTokenizer(id, ".");
        
        while (st.hasMoreTokens()) {
            if (! isValidSimpleIdentifier(st.nextToken()))
                return false;
        }
        
        return true;
    }
    
    public static boolean isValidSimpleIdentifier(String id) {
        if (id == null) return false;
        
        if (RESERVED_WORDS.contains(id)) return false;

        if (id.length() == 0 || !Character.isJavaIdentifierStart(id.codePointAt(0)))
            return false;
        for (int i = 1; i < id.length(); i++) {
            if (!Character.isJavaIdentifierPart(id.codePointAt(i)))
                return false;
        }
        
        return true;
    }
    
    public static boolean isValidComposedIdentifier(String id) {
        return isValidJavaIdentifier(id) && !isSimple(id);
    }
    
    public static boolean isValidUdSimpleIdentifier(String id) {
        return isValidSimpleIdentifier(id) 
            && (id.charAt(0) != '$'); // used for generated identifiers
    }
    
    public static boolean isValidInfixIdentifier(String id) {
        if (id == null || id.length() == 0) return false;
        
        int index = -1;
        while (true) {
            index = id.indexOf('\'', index+1);
            if (index < 0) return true;
            int counter = 0;
            for (int i = index-1; i >= 0; i--)
                if (id.charAt(i) == '\\') counter++;
                else break;
            if (counter % 2 == 0) return false; 
        }
    }
    
    public static void testIdentifier(String id) throws IllegalIdentifierException {
        if (! isValidJavaIdentifier(id)) 
            throw new IllegalIdentifierException(id);
    }
    
    public static void testSimpleIdentifier(String id) throws IllegalIdentifierException {
        if (! isValidSimpleIdentifier(id)) 
            throw new IllegalIdentifierException(id);
    }
    
    public static void testInfixIdentifier(String id) throws IllegalIdentifierException {
        if (! isValidInfixIdentifier(id)) 
            throw new IllegalIdentifierException(id);
    }
    
    public static void testComposedIdentifier(String id) throws IllegalIdentifierException {
        if (! isValidComposedIdentifier(id)) 
            throw new IllegalIdentifierException(id);
    }
    
    public static void testUdSimpleIdentifier(String id, boolean upperCaseFirst) throws IllegalIdentifierException {
        if (!isValidUdSimpleIdentifier(id) 
            || (startsWithUpperCase(id) != upperCaseFirst))            
                throw new IllegalIdentifierException(id);
    }
    
    public static void testUdSimpleIdentifier(String id) throws IllegalIdentifierException {
        testUdSimpleIdentifier(id, false);
    }
    
    public static int getNbTokens(final String value) {        
        final int length = value.length();
        final char[] chars = new char[length];
        value.getChars(0, length, chars, 0);
        int result = 1;
        for (int i = 0; i < length; i++)
            if (chars[i] == '.') { result++; i++; }
        return result;
    }
    
    public static boolean startsWithUpperCase(String s) {
        final char first = s.charAt(0);
        return Character.isUpperCase(first) || first == '_';
    }
    
    public static boolean startsWithLowerCase(String s) {
        final char first = s.charAt(0);
        return Character.isLowerCase(first) || first == '_';
    }
    
    /**
     * <p>
     * Returns the &quot;<i>tail</i>&quot; of a given identifier.
     * The <i>tail</i> of an identifier is the part after the last
     * occurrence of a dot in the identifier. If there is no dot 
     * present (i.e. it is a simple identifier), the result will
     * the identifier itself.
     * </p>
     * <p>
     * Some examples:
     * </p>
     * <ul>
     *  <li><code>getTail("java.lang.String").equals("String")</code></li>
     *  <li><code>getTail("List").equals("List")</code></li>
     *  <li><code>getTail("int").equals("int")</code></li>
     * </ul>
     * <p>
     * This also means that for each identifier <i>id</i>: 
     * <pre>    id.equals(getBody(id).concat(getTail(id)))</pre>
     * Also, the tail is part of the torso of an identifier, i.e.
     * <pre>    getTail(id).equals(getTail(getTorso(id)))</pre>
     * </p>
     * 
     * @param s
     *  The identifier we wich to receive the tail of.
     * @return The &quot;<i>tail</i>&quot; of a given identifier.
     * @throws NullPointerException
     *  If the given identifier is a null-pointer.
     * 
     * @see #getBody(String)
     * @see #getTorso(String)
     */
    public static String getTail(String s) throws NullPointerException {
        final int index = s.lastIndexOf('.') + 1;
        
        if (index == 0)
            return s;
        else
            return s.substring(index);
    }
    
    /**
     * <p>
     * Returns the &quot;<i>torso</i>&quot; of a given identifier.
     * The <i>torso</i> of an identifier is the part after the first
     * occurrence of a dot in the identifier. If there is no dot 
     * present (i.e. it is a simple identifier), the result will
     * the identifier itself.
     * </p>
     * <p>
     * Some examples:
     * </p>
     * <ul>
     *  <li><code>getTorso("java.lang.String").equals("lang.String")</code></li>
     *  <li><code>getTorso("Map.Entry").equals("Entry")</code></li>
     *  <li><code>getTorso("List").equals("List")</code></li>
     *  <li><code>getTorso("int").equals("int")</code></li>
     * </ul>
     * <p>
     * This also means that for each identifier <i>id</i>:
     * <pre>    id.equals(getHead(id).concat(getTorso(id)))</pre>
     * </p>
     * 
     * @param s
     *  The identifier we wich to receive the torso of.
     * @return The &quot;<i>torso</i>&quot; of a given identifier.
     * @throws NullPointerException
     *  If the given identifier is a null-pointer.
     * 
     * @see #getHead(String)
     * @see #getTail(String)
     */
    public static String getTorso(String s) throws NullPointerException {
        final int index = s.indexOf('.') + 1;
            
        if (index == 0)
            return s;
        else
            return s.substring(index);
    }
    
    /**
     * <p>
     * Returns the &quot;<i>head</i>&quot; of a given identifier.
     * The <i>head</i> of an identifier is the part before the first
     * occurrence of a dot in the identifier. If there is no dot 
     * present (i.e. it is a simple identifier), the result will
     * the empty <code>String</code>.
     * </p>
     * <p>
     * Some examples:
     * </p>
     * <ul>
     *  <li><code>getHead("java.lang.String").equals("java")</code></li>
     *  <li><code>getHead("Map.Entry").equals("Map")</code></li>
     *  <li><code>getHead("List").equals("")</code></li>
     *  <li><code>getHead("int").equals("")</code></li>
     * </ul>
     * <p>
     * This also means that for each identifier <i>id</i>:
     * <pre>    id.equals(getHead(id).concat(getTorso(id)))</pre>
     * Also, the head is part of the body of an identifier, i.e.
     * <pre>    getHead(id).equals(getHead(getBody(id)))</pre>
     * </p>
     * 
     * @param s
     *  The identifier we wich to receive the head of.
     * @return The &quot;<i>head</i>&quot; of a given identifier.
     * @throws NullPointerException
     *  If the given identifier is a null-pointer.
     * 
     * @see #getTorso(String)
     * @see #getBody(String)
     */
    public static String getHead(String s) throws NullPointerException {
        final int index = s.indexOf('.');
        
        if (index == -1)
            return "";
        else
            return s.substring(0, index);
    }
    
    /**
     * <p>
     * Returns the &quot;<i>body</i>&quot; of a given identifier.
     * The <i>body</i> of an identifier is the part before the last
     * occurrence of a dot in the identifier. If there is no dot 
     * present (i.e. it is a simple identifier), the result will
     * the empty <code>String</code>.
     * </p>
     * <p>
     * Some examples:
     * </p>
     * <ul>
     *  <li><code>getBody("java.lang.String").equals("java.lang")</code></li>
     *  <li><code>getBody("Map.Entry").equals("Map")</code></li>
     *  <li><code>getBody("List").equals("")</code></li>
     *  <li><code>getBody("int").equals("")</code></li>
     * </ul>
     * <p>
     * This also means that for each identifier <i>id</i>: <br/>
     * <pre>    id.equals(getBody(id).concat(getTail(id)))</pre>
     * </p>
     * 
     * @param s
     *  The identifier we wich to receive the body of.
     * @return The &quot;<i>body</i>&quot; of a given identifier.
     * @throws NullPointerException
     *  If the given identifier is a null-pointer.
     * 
     * @see #getTail(String)
     * @see #getHead(String)
     */
    public static String getBody(String s) throws NullPointerException {
        final int index = s.lastIndexOf('.');
            
        if (index == -1)
            return "";
        else
            return s.substring(0, index);
    }
    
    public static boolean isComposed(final String value) {
        return !isSimple(value);
    }
    
    public static boolean isSimple(final String value) {
        return value.indexOf('.') < 0;
    }
    
    /**
     * Flattens a composed identifier, turning it into a valid 
     * simple identifier, by replacing all dots with underscores. 
     *  
     * @param value
     *  A valid, possibly composed identifier. 
     * @return A valid sinple identifier.
     */
    public static String flatten(String value) {
        return value.replace('.', '_');
    }
    
    public static String makeJavaLike(String value) {
    	StringTokenizer in = new StringTokenizer(value, "_");
    	StringBuilder out = new StringBuilder(value.length()); 
    	
    	while (in.hasMoreTokens())
    		out.append(StringUtils.capFirst(in.nextToken()));
    	
    	return out.toString();
    }
}