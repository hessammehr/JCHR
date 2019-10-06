package compiler.CHRIntermediateForm.constraints.ud.schedule;

public class LastScheduleElementGetter extends AbstractScheduleVisitor {

    private IScheduleElement result;
    
    @Override
    protected void visit(IScheduleElement element) throws Exception {
        setResult(element);
    }    
    
    protected void setResult(IScheduleElement result) {
        this.result = result;
    }
    public IScheduleElement getResult() {
        return result;
    }
    
    
    public static IScheduleElement getLastScheduleElementOf(IScheduleVisitable schedule) {
        try {
            LastScheduleElementGetter getter = new LastScheduleElementGetter();
            schedule.accept(getter);
            return getter.getResult();
        } catch (Exception e) {
            throw new InternalError();
        }
    }
}
