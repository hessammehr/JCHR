package compiler.CHRIntermediateForm.builder.tables;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import compiler.CHRIntermediateForm.Handler;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.exceptions.DuplicateIdentifierException;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;


/**
 * @author Peter Van Weert
 */
public class UserDefinedConstraintTable extends ConstraintTable<UserDefinedConstraint> {
    
	public UserDefinedConstraintTable() {
		// NOP
	}
	public UserDefinedConstraintTable(Handler handler) {
		setHandler(handler);
	}
	
	private Handler handler;
	public Handler getHandler() {
		return handler;
	}
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
    public UserDefinedConstraint declareConstraint(String id, int modifiers) 
    throws NullPointerException, DuplicateIdentifierException, IllegalIdentifierException {
        return declare(id, new UserDefinedConstraint(getHandler(), id, modifiers), false);
    }
    
    public void declareInfixIdentifier(UserDefinedConstraint ud, String infix)
    throws NullPointerException, DuplicateIdentifierException {
        declare(infix, ud, true);
    }
    
    @Override
    public Collection<UserDefinedConstraint> getValues() {
        Set<UserDefinedConstraint> result = new HashSet<UserDefinedConstraint>();
        result.addAll(super.getValues());
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(100);
        result.append(getClass().getName());
        result.append('@');
        result.append(Integer.toHexString(hashCode()));
        result.append(" contains ");
        result.append(getSize());
        result.append(" symbols:\n");
        
        for (Map.Entry<String, UserDefinedConstraint> entry : getTable().entrySet()) {
            result.append('\t');
            result.append(entry.getKey());
            result.append(" ~~> ");
            result.append(entry.getValue());
            result.append('\n');
        }
        
        return result.toString();
    }
}