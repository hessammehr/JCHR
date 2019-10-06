package compiler.CHRIntermediateForm.conjuncts;

public interface IGuardConjunctVisitable {

    void accept(IGuardConjunctVisitor visitor) throws Exception;
}
