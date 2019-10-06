package compiler.CHRIntermediateForm.arg.visitor;

import util.visitor.IVisitor;
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

public interface ILeafArgumentVisitor extends IVisitor {
    
    public boolean explicitVariablesOnly();
    
    public void visit(StringArgument arg) throws Exception;
    
    public void visit(CharArgument arg) throws Exception;
    
    public void visit(BooleanArgument arg) throws Exception;
    
    public void visit(DoubleArgument arg) throws Exception;
    public void visit(FloatArgument arg) throws Exception;
    public void visit(LongArgument arg) throws Exception;
    public void visit(IntArgument arg) throws Exception;
    public void visit(ShortArgument arg) throws Exception;
    public void visit(ByteArgument arg) throws Exception;
    
    public void visit(NullArgument arg) throws Exception;
    
    public void visit(ClassNameImplicitArgument arg) throws Exception;
    
    public void visit(NamelessVariable arg) throws Exception;
    public void visit(Variable arg) throws Exception;
    
    public void visit(Solver arg) throws Exception;
    
    public void visit(OneDummy arg) throws Exception;
    public void visit(OtherDummy arg) throws Exception;
}