package compiler.CHRIntermediateForm.variables;

import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.id.AbstractIdentified;
import compiler.CHRIntermediateForm.id.Identifier;

/**
 * <p>
 * An abstract classed that can be used by {@link IVariable}
 * implementations.
 * </p>
 * <p>
 * The fact that each variable has got an identifier 
 * (unique within a certain scope), is more or less all that 
 * variables have in common. A variable identifier can never
 * be a composed identifier.
 * </p>
 * 
 * @see Identifier#isValidSimpleIdentifier(String)
 */
public class AbstractVariable extends AbstractIdentified implements IVariable {

    public AbstractVariable(String identifier) throws IllegalIdentifierException {
        super(identifier);
    }
    
    /**
     * A variable identifier always has to be a valid, simple identifier, 
     * as specified by {@link Identifier#isValidSimpleIdentifier(String)},
     * and start with an upper-case letter or underscore.
     * 
     * @see #isValidVariableIdentifier(String)
     */ 
    @Override
    public boolean canHaveAsIdentifier(String identifier) {
        return isValidVariableIdentifier(identifier);
    }
    
   /**
    * A variable identifier always has to be a valid, simple identifier, 
    * as specified by {@link Identifier#isValidSimpleIdentifier(String)},
    * and start with an upper-case letter, underscore or dollar sign
    * (the latter is for generated names only). In other words: it must
    * <em>not</em> start with a lower case!
    * 
    * @see Identifier#isValidSimpleIdentifier(String)
    * @see Identifier#startsWithUpperCase(String)
    */
    public final static boolean isValidVariableIdentifier(String identifier) {
        return Identifier.isValidSimpleIdentifier(identifier)
            && ( 
                Identifier.startsWithUpperCase(identifier) 
                    || identifier.startsWith("$") 
            );
    }
    
    @Override
    public final boolean equals(Object other) {
        return this == other;
    }
}