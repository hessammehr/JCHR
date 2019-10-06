package compiler.CHRIntermediateForm.constraints.ud.schedule;

public interface IJoinOrderVisitable {

    void accept(IJoinOrderVisitor visitor) throws Exception;
}