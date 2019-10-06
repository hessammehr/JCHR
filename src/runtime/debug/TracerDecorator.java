package runtime.debug;

import runtime.Constraint;

public class TracerDecorator implements Tracer {

    private Tracer decorated;

    public TracerDecorator(Tracer tracer) {
        setDecorated(tracer);
    }
    
    protected Tracer getDecorated() {
        return decorated;
    }
    protected void setDecorated(Tracer decorated) {
        this.decorated = decorated;
    }

    public void activated(Constraint constraint) {
        getDecorated().activated(constraint);
    }

    public void fires(String ruleId, int activeIndex, Constraint... constraints) {
        getDecorated().fires(ruleId, activeIndex, constraints);
    }
    
    public void fired(String ruleId, int activeIndex, Constraint... constraints) {
        getDecorated().fired(ruleId, activeIndex, constraints);
    }

    public void reactivated(Constraint constraint) {
        getDecorated().reactivated(constraint);
    }
    
    public void suspended(Constraint constraint) {
    	getDecorated().suspended(constraint);
    }

    public void removed(Constraint constraint) {
        getDecorated().removed(constraint);
    }
    
    public void terminated(Constraint constraint) {
        getDecorated().terminated(constraint);
    }

    public void stored(Constraint constraint) {
        getDecorated().stored(constraint);
    }
}
