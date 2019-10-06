package compiler.CHRIntermediateForm.constraints.ud.schedule;

public interface IScheduleVisitable {
    void accept(IScheduleVisitor visitor) throws Exception;
}