package runtime.debug;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import runtime.Constraint;
import runtime.debug.InterruptableTracer.InterruptingTracer;

public abstract class BasicInterruptingTracer extends InterruptingTracer {
    
    private final List<Class<? extends Constraint>> constraintClasses;
    private final Set<Class<? extends Constraint>> 
        ifActivated, ifSuspended, ifReactivated, ifStored, ifRemoved, ifTerminated;
    
    public BasicInterruptingTracer(Class<? extends Constraint>... classes) {
        this(true, true, true, true, true, true, classes);
    }
    
    public BasicInterruptingTracer(
		boolean interruptAllIfActivated,
		boolean interruptAllIfSuspended,
		boolean interruptAllIfReactivated,
		boolean interruptAllIfStored,
		boolean interruptAllIfTerminated,
		boolean interruptAllIfRemoved, 
		Class<? extends Constraint>... classes
	) {
    	constraintClasses = Arrays.asList(classes);
        
        ifActivated = new HashSet<Class<? extends Constraint>>(classes.length);
        ifSuspended = new HashSet<Class<? extends Constraint>>(classes.length);
        ifReactivated = new HashSet<Class<? extends Constraint>>(classes.length);
        ifStored = new HashSet<Class<? extends Constraint>>(classes.length);
        ifRemoved = new HashSet<Class<? extends Constraint>>(classes.length);
        ifTerminated = new HashSet<Class<? extends Constraint>>(classes.length);
        
        interruptAllIfActivated(interruptAllIfActivated);
        interruptAllIfSuspended(interruptAllIfSuspended);
        interruptAllIfReactivated(interruptAllIfReactivated);
        interruptAllIfRemoved(interruptAllIfRemoved);
        interruptAllIfStored(interruptAllIfStored);
        interruptAllIfTerminated(interruptAllIfTerminated);
    }
    
    protected void interruptAll(Set<Class<? extends Constraint>> set, boolean b) {
        if (b)
            set.addAll(constraintClasses);
        else
            set.clear();
    }
    protected void interrupt(Set<Class<? extends Constraint>> set, Class<? extends Constraint> clazz, boolean b) {
        if (b)
            set.add(clazz);
        else
            set.remove(clazz);
    }
    
    
    @Override
    public boolean warrantsInterruptIfActivated(Constraint constraint) {
        return warrantsInterruptIfActivated(constraint.getClass());
    }
    public boolean warrantsInterruptIfActivated(Class<? extends Constraint> constraint) {
        return ifActivated.contains(constraint);
    }
    public void interruptAllIfActivated(boolean b) {
        interruptAll(ifActivated, b);
    }
    public void interuptIfActivated(Class<? extends Constraint> clazz, boolean b) {
        interrupt(ifActivated, clazz, b);
    }
    
    @Override
    public boolean warrantsInterruptIfSuspended(Constraint constraint) {
        return warrantsInterruptIfSuspended(constraint.getClass());
    }
    public boolean warrantsInterruptIfSuspended(Class<? extends Constraint> constraint) {
        return ifSuspended.contains(constraint);
    }
    public void interruptAllIfSuspended(boolean b) {
        interruptAll(ifSuspended, b);
    }
    public void interuptIfSuspended(Class<? extends Constraint> clazz, boolean b) {
        interrupt(ifSuspended, clazz, b);
    }

    @Override
    public boolean warrantsInterruptIfReactivated(Constraint constraint) {
        return warrantsInterruptIfReactivated(constraint.getClass());
    }
    public boolean warrantsInterruptIfReactivated(Class<? extends Constraint> constraint) {
        return ifReactivated.contains(constraint);
    }
    public void interruptAllIfReactivated(boolean b) {
        interruptAll(ifReactivated, b);
    }
    public void interuptIfReactivated(Class<? extends Constraint> clazz, boolean b) {
        interrupt(ifReactivated, clazz, b);
    }

    @Override
    public boolean warrantsInterruptIfRemoved(Constraint constraint) {
        return warrantsInterruptIfRemoved(constraint.getClass());
    }
    public boolean warrantsInterruptIfRemoved(Class<? extends Constraint> constraint) {
        return ifRemoved.contains(constraint);
    }
    public void interruptAllIfRemoved(boolean b) {
        interruptAll(ifRemoved, b);
    }
    public void interuptIfRemoved(Class<? extends Constraint> clazz, boolean b) {
        interrupt(ifRemoved, clazz, b);
    }

    @Override
    public boolean warrantsInterruptIfStored(Constraint constraint) {
        return warrantsInterruptIfStored(constraint.getClass());
    }
    public boolean warrantsInterruptIfStored(Class<? extends Constraint> constraint) {
        return ifStored.contains(constraint);
    }
    public void interruptAllIfStored(boolean b) {
        interruptAll(ifStored, b);
    }
    public void interuptIfStored(Class<? extends Constraint> clazz, boolean b) {
        interrupt(ifStored, clazz, b);
    }

    @Override
    public boolean warrantsInterruptIfTerminated(Constraint constraint) {
        return warrantsInterruptIfTerminated(constraint.getClass());
    }
    public boolean warrantsInterruptIfTerminated(Class<? extends Constraint> constraint) {
        return ifTerminated.contains(constraint);
    }
    public void interruptAllIfTerminated(boolean b) {
        interruptAll(ifTerminated, b);
    }
    public void interuptIfTerminated(Class<? extends Constraint> clazz, boolean b) {
        interrupt(ifTerminated, clazz, b);
    }
}