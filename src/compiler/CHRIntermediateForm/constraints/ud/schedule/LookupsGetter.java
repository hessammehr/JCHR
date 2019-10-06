package compiler.CHRIntermediateForm.constraints.ud.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import compiler.CHRIntermediateForm.constraints.ud.lookup.Lookup;

public class LookupsGetter extends AbstractScheduleVisitor {

    private List<Lookup> result = new ArrayList<Lookup>();
    
    @Override
    public void visit(Lookup lookup) throws Exception {
        result.add(lookup);
    }
    
    protected void setResult(List<Lookup> result) {
        this.result = result;
    }
    public List<Lookup> getResult() {
        return Collections.unmodifiableList(result);
    }
    
    @Override
    public void reset() throws Exception {
    	super.reset();
    	result.clear();
    }
    
    public static void addLookupsOf(IScheduleVisitable schedule, List<Lookup> lookups) {
    	try {
        	LookupsGetter getter = new LookupsGetter();
        	getter.setResult(lookups);
            schedule.accept(getter);
        } catch (Exception e) {
            throw new InternalError();
        }
    }
    
    public static List<Lookup> getLookupsOf(IScheduleVisitable schedule) {
        try {
        	LookupsGetter getter = new LookupsGetter();
            schedule.accept(getter);
            return getter.getResult();
        } catch (Exception e) {
            throw new InternalError();
        }
    }
    
    public static List<Lookup> getNonSingletonLookupsOf(IScheduleVisitable schedule) {
    	try {
        	LookupsGetter getter = new LookupsGetter() {
				@Override
				public void visit(Lookup lookup) throws Exception {
					if (!lookup.isSingleton()) super.visit(lookup);
				}
			};
            schedule.accept(getter);
            return getter.getResult();
        } catch (Exception e) {
            throw new InternalError();
        }
    }
    
    public static Lookup getFirstLookupOf(IScheduleVisitable schedule) {
    	return getLookupsOf(schedule).get(0);
    }
    public static Lookup getLastLookupOf(IScheduleVisitable schedule) {
    	List<Lookup> lookups = getLookupsOf(schedule);
    	return lookups.get(lookups.size() - 1);
    }
}