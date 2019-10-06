package compiler.CHRIntermediateForm.arg.visitor;

public interface IArgumentVisitable extends ILeafArgumentVisitable {

    public void accept(IArgumentVisitor visitor) throws Exception;
    
}
