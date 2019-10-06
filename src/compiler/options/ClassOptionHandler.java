package compiler.options;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import compiler.CHRIntermediateForm.builder.tables.ClassTable;
import compiler.CHRIntermediateForm.exceptions.AmbiguousIdentifierException;

public class ClassOptionHandler extends OptionHandler<Class<?>> {

    private static ClassTable $classTable; 
    
    public ClassOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super Class<?>> setter) {
        super(parser, option, setter);
    }

    /**
     * Registers this class as the class to handle {@link Class}
     * options.
     * 
     * @param handler
     *  The class to handle {@link Class} options.
     */
    public static void register() {
        CmdLineParser.registerHandler(Class.class, ClassOptionHandler.class);
    }
    
    @Override
    public int parseArguments(Parameters params) throws CmdLineException {
        try {
            String param = params.getParameter(0);
            if ($classTable == null)
                setter.addValue(ClassTable.forName(param));
            else
                setter.addValue($classTable.getClass(param));
            return 1;
            
        } catch (ClassNotFoundException cnfe) {
            throw new CmdLineException(cnfe);
        } catch (AmbiguousIdentifierException aie) {
            throw new CmdLineException(aie);
        }
    }
    
    @Override
    public String getDefaultMetaVariable() {
        return "<fqn>";
    }
    
     
    // Can only be dealt with in a static statical manner, 
    // because args4j deals with option handlers statically 
    public static void useClassTable(ClassTable classTable) {
        $classTable = classTable;
    }
}
