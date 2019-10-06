package runtime.primitive;

import java.util.Iterator;

import runtime.Constraint;
import runtime.ConstraintSystem;
import runtime.DoublyLinkedConstraintList;
import runtime.FailureException;

public final class IntEqualitySolverImpl implements IntEqualitySolver {
	
	public IntEqualitySolverImpl() {
		this(ConstraintSystem.get());
	}
	public IntEqualitySolverImpl(ConstraintSystem constraintSystem) {
		this.constraintSystem = constraintSystem;
	}
	
	final ConstraintSystem constraintSystem;
	public ConstraintSystem getConstraintSystem() {
		return constraintSystem;
	}
	
    public void tellEqual(LogicalInt X, final int value) {
        final LogicalInt Xrepr = X.find();
        
        if (Xrepr.hasValue) {
            if (Xrepr.value != value)
                throw new FailureException("Cannot make equal: " + Xrepr.value + " != " + value);
        } else if (!constraintSystem.isQueuing()){
            Xrepr.value = value;
            Xrepr.hasValue = true;

            Xrepr.rehashAllAndDispose();
            
            final DoublyLinkedConstraintList<Constraint> observers = Xrepr.variableObservers;
            if (observers != null) {
                Xrepr.variableObservers = null;
                final Iterator<Constraint> iter = observers.iterator();
                while (iter.hasNext()) 
                    iter.next().reactivate();   /* notify */
            }
        } else {
        	constraintSystem.new QueuedBuiltInConstraint() {
				@Override
				public void run() { tellEqual(Xrepr, value); }
			};
        }
    }

    public void tellEqual(final int value, LogicalInt Y) {
        final LogicalInt Yrepr = Y.find();
        
        if (Yrepr.hasValue) {
            if (Yrepr.value != value)
                throw new FailureException("Cannot make equal: " + Yrepr.value + " != " + value);
        } else if (!constraintSystem.isQueuing()) {
            Yrepr.value = value;
            Yrepr.hasValue = true;

            Yrepr.rehashAllAndDispose();
            
            final DoublyLinkedConstraintList<Constraint> observers = Yrepr.variableObservers;
            if (observers != null) {
                Yrepr.variableObservers = null;
                final Iterator<Constraint> iter = observers.iterator();
                while (iter.hasNext()) 
                    iter.next().reactivate();   /* notify */
            }
        } else {
        	constraintSystem.new QueuedBuiltInConstraint() { 
        		@Override 
				public void run() { tellEqual(Yrepr, value); }
			};
        }
    }
    
    public void tellEqual(LogicalInt X, LogicalInt Y) {
        if (X == Y) return;
        
        final LogicalInt Xrepr = X.find();
        final LogicalInt Yrepr = Y.find();
        
        if (Xrepr != Yrepr) {
        	if (!constraintSystem.isQueuing()) {
	            final boolean 
	                XhasValue = Xrepr.hasValue, 
	                YhasValue = Yrepr.hasValue;
	            
	            final int Xrank = Xrepr.rank;
	            int Yrank = Yrepr.rank;
	            
	            if (Xrank >= Yrank) {
	                Yrepr.parent = Xrepr;
	                if (Xrank == Yrank) Xrepr.rank++;
	                
	                if (XhasValue) {
	                    if (YhasValue) {                       // (1) ground ground
	                        if (Xrepr.value != Yrepr.value)
	                            throw new FailureException("Cannot make equal: " 
	                                        + Xrepr.value + " != " + Yrepr.value);
	                    } else {                               // (2) ground var
	                        Yrepr.rehashAllAndDispose();
	                        
	                        final DoublyLinkedConstraintList<Constraint> observers = Yrepr.variableObservers;
	                        if (observers != null) {
	                            Yrepr.variableObservers = null;
	                            final Iterator<Constraint> iter = observers.iterator();
	                            while (iter.hasNext()) 
	                                iter.next().reactivate();   /* notify */
	                        }
	                    }
	                } else {
	                    if (YhasValue) {                       // (3) var ground
	                        Xrepr.value = Yrepr.value;
	                        Xrepr.hasValue = true;
	                        Xrepr.rehashAllAndDispose();
	                        
	                        final DoublyLinkedConstraintList<Constraint> observers = Xrepr.variableObservers;
	                        if (observers != null) {
	                            Xrepr.variableObservers = null;
	                            final Iterator<Constraint> iter = observers.iterator();
	                            while (iter.hasNext()) 
	                                iter.next().reactivate();   /* notify */
	                        }
	                    } else {                               // (4) var var
	                        if (Yrepr.hashObservers != null) {
	                            Xrepr.mergeHashObservers(Yrepr.hashObservers);
	                            Yrepr.hashObservers = null;
	                        }
	                        
	                        final DoublyLinkedConstraintList<Constraint> Xobs, Yobs;
	                        if ((Yobs = Yrepr.variableObservers) != null) {
	                            Yrepr.variableObservers = null;
	                            if ((Xobs = Xrepr.variableObservers) != null) {
	                            	final Iterator<Constraint> iter = Xobs.iterator();
	                            	Xobs.mergeWith(Yobs);
	                                while (iter.hasNext()) 
	                                    iter.next().reactivate();   /* notify */
	                            } else {
	                                Xrepr.variableObservers = Yobs;
	                            }
	                        }
	                    }
	                }
	            } else {
	                Xrepr.parent = Yrepr;
	                
	                if (YhasValue) {
	                    if (XhasValue) {                       // (1) ground ground
	                        if (Xrepr.value != Yrepr.value)
	                            throw new FailureException("Cannot make equal " 
	                                + Xrepr.value + " != " + Yrepr.value);
	                    } else {                               // (2) var ground 
	                        Xrepr.rehashAllAndDispose();
	                        
	                        final DoublyLinkedConstraintList<Constraint> observers = Xrepr.variableObservers;
	                        if (observers != null) {
	                            Xrepr.variableObservers = null;
	                            final Iterator<Constraint> iter = observers.iterator();
	                            while (iter.hasNext()) 
	                                iter.next().reactivate();   /* notify */
	                        }
	                    }
	                } else {
	                    if (XhasValue) {                       // (3) ground var
	                        Yrepr.value = Xrepr.value;
	                        Yrepr.hasValue = true;
	                        Yrepr.rehashAllAndDispose();
	                        
	                        final DoublyLinkedConstraintList<Constraint> observers = Yrepr.variableObservers;
	                        if (observers != null) {
	                            Yrepr.variableObservers = null;
	                            final Iterator<Constraint> iter = observers.iterator();
	                            while (iter.hasNext()) 
	                                iter.next().reactivate();   /* notify */
	                        }
	                    } else {                               // (2) var var
	                        if (Xrepr.hashObservers != null) {
	                            Yrepr.mergeHashObservers(Xrepr.hashObservers);
	                            Xrepr.hashObservers = null;
	                        }
	                        
	                        final DoublyLinkedConstraintList<Constraint> Xobs, Yobs;
	                        if ((Xobs = Xrepr.variableObservers) != null) {
	                            Xrepr.variableObservers = null;
	                            if ((Yobs = Yrepr.variableObservers) != null) {
	                            	final Iterator<Constraint> iter = Yobs.iterator();
	                            	Yobs.mergeWith(Xobs);
	                                while (iter.hasNext()) 
	                                    iter.next().reactivate();   /* notify */
	                            } else {
	                                Yrepr.variableObservers = Xobs;
	                            }
	                        }
	                    }
	                }
	            }
	        } else {
	        	constraintSystem.new QueuedBuiltInConstraint(){
					@Override
					public void run() { tellEqual(Xrepr, Yrepr); }
				};
	        }
        }
    }

    public boolean askEqual(LogicalInt X, int value) {
        final LogicalInt representative = X.find();
        return representative.hasValue && representative.value == value;
    }
    
    public boolean askEqual(int value, LogicalInt X) {
        final LogicalInt representative = X.find();
        return representative.hasValue && representative.value == value;
    }

    public boolean askEqual(LogicalInt X, LogicalInt Y) {
        if (X == Y) return true;
        
        final LogicalInt Xrepr = X.find();
        final LogicalInt Yrepr = Y.find();
        
        return (Xrepr == Yrepr) 
            || (Xrepr.hasValue && Yrepr.hasValue && Xrepr.value == Yrepr.value);
    }
}