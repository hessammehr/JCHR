package compiler.CHRIntermediateForm.constraints.ud.schedule;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.variables.FormalVariable;
import compiler.CHRIntermediateForm.variables.IActualVariable;

public interface IVariableInfo extends Comparable<IVariableInfo> {
    Occurrence getDeclaringOccurrence();
    FormalVariable getFormalVariable();
    int getDeclarationIndex();
    IActualVariable getActualVariable();
}