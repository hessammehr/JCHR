package compiler.CHRIntermediateForm.arg.visitor;

public interface ILeafArgumentVisitable {

    public void accept(ILeafArgumentVisitor visitor) throws Exception;
}
