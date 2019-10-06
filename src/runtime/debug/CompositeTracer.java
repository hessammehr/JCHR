package runtime.debug;

import java.util.Arrays;

import runtime.Constraint;

/**
 * A helper class that allows for more then one tracer to be added
 * to a handler.
 * 
 * @author Peter Van Weert
 */
public class CompositeTracer implements Tracer {
    
    private Iterable<Tracer> tracers;

    /**
     * Creates a new <code>CompositeTracer</code> that delegates all calls
     * to the series of given tracers (the tracers are called in the order
     * they are given here).  
     * 
     * @param tracers
     *  The series of tracers to delegate all tracer calls to.
     * @throws NullPointerException
     *  If <code>tracers == null</code>.
     */
    public CompositeTracer(Tracer... tracers) throws NullPointerException {
        this(Arrays.asList(tracers));
    }
    
    /**
     * Creates a new <code>CompositeTracer</code> that delegates all calls
     * to the series of given tracers (the tracers are called in the order
     * they are returned by the iterator returned by the iterable).  
     * 
     * @param tracers
     *  The series of tracers to delegate all tracer calls to.
     * @throws NullPointerException
     *  If <code>tracers == null</code>.
     */
    public CompositeTracer(Iterable<Tracer> tracers) throws NullPointerException {
        if (tracers == null) throw new NullPointerException();
        setTracers(tracers);
    } 

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * <p>
     * All calls are delegated to all tracers that are registered with
     * this composite tracer.
     * </p>
     */
    public void activated(Constraint constraint) {
        for (Tracer tracer : getTracers()) tracer.activated(constraint);
    }
    
    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * <p>
     * All calls are delegated to all tracers that are registered with
     * this composite tracer.
     * </p>
     */
    public void suspended(Constraint constraint) {
        for (Tracer tracer : getTracers()) tracer.suspended(constraint);
    }

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * <p>
     * All calls are delegated to all tracers that are registered with
     * this composite tracer.
     * </p>
     */
    public void reactivated(Constraint constraint) {
        for (Tracer tracer : getTracers()) tracer.reactivated(constraint);
    }

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * <p>
     * All calls are delegated to all tracers that are registered with
     * this composite tracer.
     * </p>
     */
    public void stored(Constraint constraint) {
        for (Tracer tracer : getTracers()) tracer.stored(constraint);
    }

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * <p>
     * All calls are delegated to all tracers that are registered with
     * this composite tracer.
     * </p>
     */
    public void removed(Constraint constraint) {
        for (Tracer tracer : getTracers()) tracer.removed(constraint);
    }
    
    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * <p>
     * All calls are delegated to all tracers that are registered with
     * this composite tracer.
     * </p>
     */
    public void terminated(Constraint constraint) {
        for (Tracer tracer : getTracers()) tracer.terminated(constraint);
    }

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * <p>
     * All calls are delegated to all tracers that are registered with
     * this composite tracer.
     * </p>
     */
    public void fires(String ruleId, int activeIndex, Constraint... constraints) {
        for (Tracer tracer : getTracers()) tracer.fires(ruleId, activeIndex, constraints);
    }
    
    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * <p>
     * All calls are delegated to all tracers that are registered with
     * this composite tracer.
     * </p>
     */
    public void fired(String ruleId, int activeIndex, Constraint... constraints) {
        for (Tracer tracer : getTracers()) tracer.fired(ruleId, activeIndex, constraints);
    }

    /**
     * Sets the tracers this <code>CompositeTracer</code> delegates all
     * tracer calls to.
     * 
     * @param tracers
     *  The tracers to delegate all tracer calls to.
     */
    public void setTracers(Iterable<Tracer> tracers) {
        this.tracers = tracers;
    }
    
    /**
     * Returns the tracers this <code>CompositeTracer</code> delegates all
     * tracer calls to.
     * 
     * @return The tracers this <code>CompositeTracer</code> delegates all
     *  tracer calls to.
     */
    public Iterable<Tracer> getTracers() {
        return tracers;
    }
}
