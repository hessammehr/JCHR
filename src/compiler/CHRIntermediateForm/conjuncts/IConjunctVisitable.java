package compiler.CHRIntermediateForm.conjuncts;

public interface IConjunctVisitable {

    void accept(IConjunctVisitor visitor) throws Exception;
}
