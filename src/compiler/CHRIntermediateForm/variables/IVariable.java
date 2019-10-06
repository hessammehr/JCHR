package compiler.CHRIntermediateForm.variables;

import compiler.CHRIntermediateForm.id.Identified;
import compiler.CHRIntermediateForm.id.Identifier;

/**
 * <p>
 * The interface implemented by all variables.
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
public interface IVariable extends Identified {
    /* indicator interface */
}