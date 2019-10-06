package compiler.CHRIntermediateForm.arg.argument;

import java.util.Set;

import compiler.CHRIntermediateForm.exceptions.AmbiguityException;
import compiler.CHRIntermediateForm.members.Field;
import compiler.CHRIntermediateForm.members.Method;

public abstract class ImplicitArgument extends Argument implements IImplicitArgument {

    public Field getField(String name) throws AmbiguityException, NoSuchFieldException {
        return getType().getField(name);
    }

    public Set<Method> getMethods(String id) {
        return getType().getMethods(id);
    }
}