package compiler.CHRIntermediateForm.constraints.ud.schedule;


public abstract class AbstractJoinOrder implements IJoinOrder {

    public void accept(IJoinOrderVisitor visitor) throws Exception {
        visitor.isVisiting();
        for (IJoinOrderElement element : this) element.accept(visitor);
    }
}