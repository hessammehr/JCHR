package compiler.options;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.OptionHandlerRegistry;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public abstract class BooleanOptionHandler extends OptionHandler<Boolean> {
    
    /**
     * Registers the default handlers for booleans (the default in the 
     * sense that it does the same as args4j would do by default).
     */
    public static void registerDefault() {
        register(True.class);
    }
    
    /**
     * Registers a given handler class as the class that handles boolean
     * and Boolean options.
     * 
     * @param handler
     *  The class to handle boolean and Boolean options.
     */
    public static void register(Class<? extends BooleanOptionHandler> handler) {
        OptionHandlerRegistry.getRegistry().registerHandler(Boolean.class, handler);
        OptionHandlerRegistry.getRegistry().registerHandler(Boolean.TYPE, handler);
    }
    
    protected BooleanOptionHandler(CmdLineParser parser, OptionDef option, Setter<Boolean> setter) {
        super(parser, option, setter);
    }
    
    @Override
    public String getDefaultMetaVariable() {
        return null;
    }
    
    public static class True extends BooleanOptionHandler {
        public True(CmdLineParser parser, OptionDef option, Setter<Boolean> setter) {
            super(parser, option, setter);
        }
        
        @Override
        public int parseArguments(Parameters params) throws CmdLineException {
            setter.addValue(TRUE);
            return 0;
        }
    }
    
    public static class False extends BooleanOptionHandler {
        public False(CmdLineParser parser, OptionDef option, Setter<Boolean> setter) {
            super(parser, option, setter);
        }
        
        @Override
        public int parseArguments(Parameters params) throws CmdLineException {
            setter.addValue(FALSE);
            return 0;
        }
    }
    
    private abstract static class AbstractBooleanOptionHandler extends BooleanOptionHandler {
        public AbstractBooleanOptionHandler(CmdLineParser parser, OptionDef option, Setter<Boolean> setter) {
            super(parser, option, setter);
        }
        
        @Override
        public final int parseArguments(Parameters params) throws CmdLineException {
            String param = params.getParameter(0); 
            if (isTrue(param))
                setter.addValue(TRUE);
            else if (isFalse(param))
                setter.addValue(FALSE);
            else 
                throw new CmdLineException(owner, new IllegalArgumentException("Illegal value: " + param));
            
            return 1;
        }
        
        protected abstract boolean isTrue(String s);
        protected abstract boolean isFalse(String s);
        
        @Override
        public String getDefaultMetaVariable() {
        	return "<boolean>";
        }
    }
    
    public static class YesNo extends AbstractBooleanOptionHandler {
        public YesNo(CmdLineParser parser, OptionDef option, Setter<Boolean> setter) {
            super(parser, option, setter);
        }
        
        @Override
        protected boolean isTrue(String s) {
            return "yes".equalsIgnoreCase(s);
        }
        @Override
        protected boolean isFalse(String s) {
            return "no".equalsIgnoreCase(s);
        }
    }
    
    public static class TrueFalse extends AbstractBooleanOptionHandler {
        public TrueFalse(CmdLineParser parser, OptionDef option, Setter<Boolean> setter) {
            super(parser, option, setter);
        }
        
        @Override
        protected boolean isTrue(String s) {
            return "true".equalsIgnoreCase(s);
        }
        @Override
        protected boolean isFalse(String s) {
            return "false".equalsIgnoreCase(s);
        }
    }
    
    public static class OnOff extends AbstractBooleanOptionHandler {
        public OnOff(CmdLineParser parser, OptionDef option, Setter<Boolean> setter) {
            super(parser, option, setter);
        }
        
        @Override
        protected boolean isTrue(String s) {
            return "on".equalsIgnoreCase(s);
        }
        @Override
        protected boolean isFalse(String s) {
            return "off".equalsIgnoreCase(s);
        }
    }
    
    public static class Generic extends AbstractBooleanOptionHandler {
        public final static String VALUES = "yes/no; on/off; true/false";
        
        public Generic(CmdLineParser parser, OptionDef option, Setter<Boolean> setter) {
            super(parser, option, setter);
        }
        
        @Override
        protected boolean isTrue(String s) {
            return "true".equalsIgnoreCase(s) 
                || "on".equalsIgnoreCase(s)
                || "yes".equalsIgnoreCase(s);
        }
        @Override
        protected boolean isFalse(String s) {
            return "false".equalsIgnoreCase(s)
                || "off".equalsIgnoreCase(s)
                || "no".equalsIgnoreCase(s);
        }
    }
}
