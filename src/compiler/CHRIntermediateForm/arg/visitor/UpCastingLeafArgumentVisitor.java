package compiler.CHRIntermediateForm.arg.visitor;

import compiler.CHRIntermediateForm.arg.argument.ClassNameImplicitArgument;
import compiler.CHRIntermediateForm.arg.argument.FormalArgument;
import compiler.CHRIntermediateForm.arg.argument.ILeafArgument;
import compiler.CHRIntermediateForm.arg.argument.FormalArgument.OneDummy;
import compiler.CHRIntermediateForm.arg.argument.FormalArgument.OtherDummy;
import compiler.CHRIntermediateForm.arg.argument.constant.BooleanArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.ByteArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.CharArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.DoubleArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.FloatArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.IntArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.LiteralArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.LongArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.NullArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.ShortArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.StringArgument;
import compiler.CHRIntermediateForm.solver.Solver;
import compiler.CHRIntermediateForm.variables.IActualVariable;
import compiler.CHRIntermediateForm.variables.NamelessVariable;
import compiler.CHRIntermediateForm.variables.Variable;

public abstract class UpCastingLeafArgumentVisitor 
    extends AbstractLeafArgumentVisitor
    implements ILeafArgumentVisitor {
    
    public UpCastingLeafArgumentVisitor() {
        super();
    }
    public UpCastingLeafArgumentVisitor(boolean explicitOnly) {
        super(explicitOnly);
    }

    public void visit(ILeafArgument arg) {
        // NOP (default implementation)
    }
    
    public void visit(LiteralArgument<?> arg) {
        visit((ILeafArgument)arg);
    }
    
    public void visit(BooleanArgument arg) {
        visit((LiteralArgument<?>)arg);
    }

    public void visit(ByteArgument arg) {
        visit((LiteralArgument<?>)arg);
    }

    public void visit(CharArgument arg) {
        visit((LiteralArgument<?>)arg);
    }

    public void visit(DoubleArgument arg) {
        visit((LiteralArgument<?>)arg);
    }

    public void visit(FloatArgument arg) {
        visit((LiteralArgument<?>)arg);
    }

    public void visit(IntArgument arg) {
        visit((LiteralArgument<?>)arg);
    }

    public void visit(LongArgument arg) {
        visit((LiteralArgument<?>)arg);
    }

    public void visit(ShortArgument arg) {
        visit((LiteralArgument<?>)arg);
    }

    public void visit(StringArgument arg) {
        visit((LiteralArgument<?>)arg);
    }
    
    public void visit(ClassNameImplicitArgument arg) {
        visit((ILeafArgument)arg);
    }
    
    public void visit(NullArgument arg) {
        visit((ILeafArgument)arg);
    }
    
    public void visit(IActualVariable arg) {
        visit((ILeafArgument)arg);
    }
    
    public void visit(NamelessVariable arg) {
        visit((IActualVariable)arg);
    }
    
    public void visit(Variable arg) throws Exception {
        visit((IActualVariable)arg);
    }
    
    public void visit(Solver arg) {
        visit((ILeafArgument)arg);
    }
    
    public void visit(OneDummy arg) throws Exception {
        visit((FormalArgument)arg);
    }
    
    public void visit(OtherDummy arg) throws Exception {
        visit((FormalArgument)arg);
    }
    
    public void visit(FormalArgument arg) throws Exception {
        visit((ILeafArgument)arg);
    }
}