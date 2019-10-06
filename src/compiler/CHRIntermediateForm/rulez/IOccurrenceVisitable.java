package compiler.CHRIntermediateForm.rulez;

public interface IOccurrenceVisitable {

    public void accept(IOccurrenceVisitor visitor) throws Exception;

}
