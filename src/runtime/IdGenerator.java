package runtime;

/**
 * Use this &quot;feature&quot; with care: identifiers should always
 * be generated lazily if possible!
 *  
 * @author Peter Van Weert
 */
public final class IdGenerator {
    private IdGenerator() {/* not instantiatable */}
    
    private static int ID_COUNTER = 0;
    public static String generateUniqueId() {
        return "$" + ID_COUNTER++;
    }
    public static String generateUniqueId(String base) {
        return "$" + ID_COUNTER++ + "$" + base; 
    }
}
