package runtime.debug;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import runtime.Constraint;

public class StatisticsTracer implements Tracer {
    
    private Map<String, ConstraintStatistics> constraintStatistics
        = new HashMap<String, ConstraintStatistics>();
    protected static class ConstraintStatistics {
        public int activated, stored, suspended, reactivated, removed, terminated;
        
        @Override
        public String toString() {
            return new Formatter().format(
                "activated:%d, stored:%d, suspended:%d, reactivated:%d, removed:%d, terminated:%d",
                activated, stored, suspended, reactivated, removed, terminated
            ).toString();
        }
    }
    
    public ConstraintStatistics getConstraintStatistics(Constraint constraint) {
        ConstraintStatistics result = constraintStatistics.get(constraint.getIdentifier());
        if (result == null) {
            result = new ConstraintStatistics();
            constraintStatistics.put(constraint.getIdentifier(), result);
        }
        return result;
    }
    
    private Map<String, Map<Integer, Integer>> ruleStatistics = 
            new HashMap<String, Map<Integer, Integer>>();
    
    public void activated(Constraint constraint) {
        getConstraintStatistics(constraint).activated++;
    }

    public void fires(String ruleId, int activeIndex, Constraint... constraints) {
        Map<Integer, Integer> map = ruleStatistics.get(ruleId);
        if (map == null) {
            map = new HashMap<Integer, Integer>(4);
            map.put(activeIndex, 1);
            ruleStatistics.put(ruleId, map);
        } else {
            Integer num = map.get(activeIndex);
            if (num == 0)
                num = 1;
            else
                num = num.intValue() + 1;
            map.put(activeIndex, num);
        }
    }
    
    public void fired(String ruleId, int activeIndex, Constraint... constraints) {
    	// NOP (cf fires)
    }
    
    public void suspended(Constraint constraint) {
    	getConstraintStatistics(constraint).suspended++;
    }

    public void reactivated(Constraint constraint) {
        getConstraintStatistics(constraint).reactivated++;
    }

    public void removed(Constraint constraint) {
        getConstraintStatistics(constraint).removed++;
    }

    public void stored(Constraint constraint) {
        getConstraintStatistics(constraint).stored++;
    }

    public void terminated(Constraint constraint) {
        getConstraintStatistics(constraint).terminated++;
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Entry<String, ConstraintStatistics> entry : constraintStatistics.entrySet())
            result.append("Constraint ").append(entry.getKey())
                .append(" {").append(entry.getValue()).append("}\n");
        for (Entry<String, Map<Integer, Integer>> entry : ruleStatistics.entrySet()) {
            int sum = 0;
            for (int value : entry.getValue().values()) sum += value;
            result.append("Rule ").append(entry.getKey())
                .append(" fired ").append(sum).append(sum == 1? " time " : " times ")
                .append(entry.getValue())
                .append('\n');
        }
        return result.toString();
    }
}