package compiler.analysis;

import util.exceptions.Exception; 

/**
 * A generic exception indicating something has gone wrong
 * during an analysis.
 * 
 * @author Peter Van Weert
 * @see Analysis#analyse(CHRIntermediateForm, Options)
 */
public class AnalysisException extends Exception {
    private static final long serialVersionUID = 1L;

    public AnalysisException() {
        super();
    }
    public AnalysisException(String message) {
        super(message);
    }
    public AnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
    public AnalysisException(Throwable cause) {
        super(cause);        
    }
    
    public AnalysisException(String message, Object... arguments) {
        super(message, arguments);
    }
    public AnalysisException(String message, Throwable cause, Object... arguments) {
        super(message, cause, arguments);
    }
}
