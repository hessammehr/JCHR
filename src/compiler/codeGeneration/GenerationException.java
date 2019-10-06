package compiler.codeGeneration;

/**
 * @author Peter Van Weert
 */
public class GenerationException extends Exception {
    private static final long serialVersionUID = 1L;

    public GenerationException() {
        super();
    }
    
    public GenerationException(String message) {
        super(message);
    }
    
    public GenerationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public GenerationException(Throwable cause) {
        super(cause);
    }
}
