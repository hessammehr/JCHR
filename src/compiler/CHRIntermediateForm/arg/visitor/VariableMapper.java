package compiler.CHRIntermediateForm.arg.visitor;

import java.util.Map;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argument.ILeafArgument;
import compiler.CHRIntermediateForm.arg.argumentable.IArgumentable;
import compiler.CHRIntermediateForm.arg.argumented.IArgumented;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.arguments.EmptyArguments;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.variables.IActualVariable;
import compiler.CHRIntermediateForm.variables.Variable;

public class VariableMapper extends UpCastingArgumentVisitor {

    private IArguments result;
    
    private Map<? extends IActualVariable, ? extends IArgument> map;
    
    protected VariableMapper(Map<? extends IActualVariable, ? extends IArgument> map, int arity) {
        super(false);
        setMap(map);
        initResult(arity);
    }
    
    @Override
    public final boolean recurse() {
        return false;
    }
    
    @Override
    public void visit(Variable arg) {
        getResult().addArgument(getMap().get(arg));
    }
    @Override
    public void visit(ILeafArgument arg) {
        getResult().addArgument(arg);
    }
    
    
    @Override
    public <T extends IArgumented<? extends IArgumentable<?>> & IArgument> 
                    void visitArgumented(T arg) throws Exception {
     
        getResult().addArgument(map(arg, map));
    }
    
    
    public void setMap(Map<? extends IActualVariable, ? extends IArgument> map) {
        this.map = map;
    }
    public Map<? extends IActualVariable, ? extends IArgument> getMap() {
        return map;
    }
    
    
    public IArguments getResult() {
        return result;
    }
    protected void initResult(int arity) {
        if (arity == 0)
            result = EmptyArguments.getInstance();
        else
            result = new Arguments(arity);
    }
    
    public static <T extends IArgumented<? extends IArgumentable<?>> & IArgument> 
        IArgument map(T argumented, Map<? extends IActualVariable, ? extends IArgument> map) {
        
        return (IArgument)argumented.getArgumentable().createInstance(mapArguments(argumented, map));
    }
    
    public static IArguments mapArguments(
        IArgumented<? extends IArgumentable<?>> args, Map<? extends IActualVariable, ? extends IArgument> map
    ) {
        try {
            VariableMapper mapper = new VariableMapper(map, args.getArity());
            args.accept(mapper);
            return mapper.getResult();
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }
}