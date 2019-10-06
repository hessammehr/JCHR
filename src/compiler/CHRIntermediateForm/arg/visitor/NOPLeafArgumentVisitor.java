package compiler.CHRIntermediateForm.arg.visitor;

import compiler.CHRIntermediateForm.arg.argument.ClassNameImplicitArgument;
import compiler.CHRIntermediateForm.arg.argument.FormalArgument.OneDummy;
import compiler.CHRIntermediateForm.arg.argument.FormalArgument.OtherDummy;
import compiler.CHRIntermediateForm.arg.argument.constant.BooleanArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.ByteArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.CharArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.DoubleArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.FloatArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.IntArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.LongArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.NullArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.ShortArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.StringArgument;
import compiler.CHRIntermediateForm.solver.Solver;
import compiler.CHRIntermediateForm.variables.NamelessVariable;
import compiler.CHRIntermediateForm.variables.Variable;

public abstract class NOPLeafArgumentVisitor 
    extends AbstractLeafArgumentVisitor     // not strictly necessary, but makes type hierarchy easier
    implements ILeafArgumentVisitor {
    
    public NOPLeafArgumentVisitor() {
        super();
    }
    
    public NOPLeafArgumentVisitor(boolean explicitOnly) {
        super(explicitOnly);
    }
    
    public void visit(BooleanArgument arg) throws Exception {
        // NOP
    }

    public void visit(ByteArgument arg) throws Exception {
        // NOP
    }

    public void visit(CharArgument arg) throws Exception {
        // NOP
    }

    public void visit(DoubleArgument arg) throws Exception {
        // NOP
    }

    public void visit(FloatArgument arg) throws Exception {
        // NOP
    }

    public void visit(IntArgument arg) throws Exception {
        // NOP
    }

    public void visit(LongArgument arg) throws Exception {
        // NOP
    }

    public void visit(ShortArgument arg) throws Exception {
        // NOP
    }

    public void visit(StringArgument arg) throws Exception {
        // NOP
    }
    
    public void visit(ClassNameImplicitArgument arg) throws Exception {
        // NOP
    }
    
    public void visit(NullArgument arg) throws Exception {
        // NOP
    }
    
    public void visit(NamelessVariable arg) throws Exception {
        // NOP
    }
    
    public void visit(Variable arg) throws Exception {
        // NOP
    }
    
    public void visit(Solver arg) throws Exception {
        // NOP
    }
    
    public void visit(OneDummy arg) throws Exception {
        // NOP
    }
    
    public void visit(OtherDummy arg) throws Exception {
        // NOP
    }
}
