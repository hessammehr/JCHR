package runtime.debug;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * A basic tracer implementation that simply prints all events to a
 * given output stream or <code>PrintStream</code>. By default,
 * the format used is controlled by the 
 * <code>runtime/debug/Tracer.properties</code> file, though subclasses
 * are allowed to override the (protected) 
 * {@link #println(String, Object[])}
 * method and change this.
 *
 * @see SysoutTracer
 * @see MessageFormat
 * @see ResourceBundle
 * 
 * @author Peter Van Weert
 */
public class OutputStreamTracer extends PlainTextTracer {
    
    private PrintStream out;
    
    public OutputStreamTracer(OutputStream out) {
        this(new PrintStream(out));
    }
    
    public OutputStreamTracer(PrintStream out) {
        setOut(out);
    }
    
    public PrintStream getOut() {
        return out;
    }
    public void setOut(PrintStream out) {
        this.out = out;
    }

    @Override
    protected void println(String value) {
        getOut().println(value);
    }
}