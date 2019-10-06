package runtime.debug;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import runtime.Constraint;

/**
 * A basic tracer implementation that simply prints all events as
 * plain {@link String}s. By default, the format used is controlled 
 * by the <code>runtime/debug/Tracer.properties</code> file, though 
 * subclasses are allowed to override the (protected)
 * {@link #println(String, Object[])} method and change this.
 *
 * @see SysoutTracer
 * @see MessageFormat
 * @see ResourceBundle
 * 
 * @author Peter Van Weert
 */
public abstract class PlainTextTracer implements Tracer {
    
    protected final static String LINE_SEPARATOR = 
        System.getProperty("line.separator", "\n");
    
    protected static ResourceBundle bundle
        = ResourceBundle.getBundle("runtime/debug/Tracer");
    
    public void activated(Constraint constraint) {
        println("activated", constraint.toDebugString());
    }
    
    public void reactivated(Constraint constraint) {
        println("reactivated", constraint.toDebugString());
    }
    
    public void stored(Constraint constraint) {
        println("stored", constraint.toDebugString());
    }
    
    public void suspended(Constraint constraint) {
    	println("suspended", constraint.toDebugString());
    }
    
    public void removed(Constraint constraint) {
        println("removed", constraint.toDebugString());
    }
    
    public void terminated(Constraint constraint) {
        println("terminated", constraint.toDebugString());
    }
    
    public void fires(String ruleId, int activeIndex, Constraint... constraints) {
    	fire('s', ruleId, activeIndex, constraints);
    }
    
    public void fired(String ruleId, int activeIndex, Constraint... constraints) {
    	fire('d', ruleId, activeIndex, constraints);
    }
    
    private void fire(char X, String ruleId, int activeIndex, Constraint... constraints) {
        String match;
        
        if (constraints.length == 0) 
            match = "[]";
        else {
            StringBuilder result = new StringBuilder()
                .append('[')
                .append(constraints[0].toDebugString());
            
            for (int i = 1; i < constraints.length; i++)
                result.append(", ").append(constraints[i].toDebugString());
            
            result.append(']');
            match = result.toString();
        }
        
        println("fire" + X, ruleId, activeIndex, match);
    }
    
    protected void println(String name, Object... arguments) {
        println(MessageFormat.format(bundle.getString(name), arguments));
    }
    
    protected abstract void println(String value);
}