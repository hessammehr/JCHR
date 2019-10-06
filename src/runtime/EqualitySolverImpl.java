package runtime;

import java.util.Iterator;


public final class EqualitySolverImpl<Type> implements EqualitySolver<Type> {
	
	public EqualitySolverImpl() {
		this(ConstraintSystem.get());
	}
	public EqualitySolverImpl(ConstraintSystem constraintSystem) {
		this.constraintSystem = constraintSystem;
	}
	
	final ConstraintSystem constraintSystem;
	public ConstraintSystem getConstraintSystem() {
		return constraintSystem;
	}

    public void tellEqual(Logical<Type> X, final Type value) {
        final Logical<Type> Xrepr = X.find();
        final Type oldValue = Xrepr.value; 

        if (oldValue != null) {
            if (!oldValue.equals(value))
                throw new FailureException("Cannot make equal: " + oldValue + " != " + value);
        } else if (!constraintSystem.isQueuing()) {
            Xrepr.value = value;

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

    public void tellEqual(final Type value, Logical<Type> Y) {
        final Logical<Type> Yrepr = Y.find();
        final Type oldValue = Yrepr.value; 

        if (oldValue != null) {
            if (!oldValue.equals(value))
                throw new FailureException("Cannot make equal: " + oldValue + " != " + value);
        } else if (!constraintSystem.isQueuing()) {
            Yrepr.value = value;

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
				public void run() { tellEqual(value, Yrepr); }
			};
        }
    }

    public void tellEqual(Logical<Type> X, Logical<Type> Y) {
        if (X == Y) return;
        
        final Logical<Type> Xrepr = X.find();
        final Logical<Type> Yrepr = Y.find();

        if (Xrepr != Yrepr) {
        	if (!constraintSystem.isQueuing()) {
	            final Type Xvalue = Xrepr.value, Yvalue = Yrepr.value;
	
	            final int Xrank = Xrepr.rank;
	            int Yrank = Yrepr.rank;
	
	            if (Xrank >= Yrank) {
	                Yrepr.parent = Xrepr;
	                if (Xrank == Yrank) Xrepr.rank++;
	
	                if (Xvalue == null) {
	                    if (Yvalue != null) {                  // (1) var ground
	                        Xrepr.value = Yvalue;
	                        Xrepr.rehashAllAndDispose();
	                        
	                        final DoublyLinkedConstraintList<Constraint> observers = Xrepr.variableObservers;
	                        if (observers != null) {
	                            Xrepr.variableObservers = null;
	                            final Iterator<Constraint> iter = observers.iterator();
	                            while (iter.hasNext()) 
	                                iter.next().reactivate();   /* notify */
	                        }
	                    } else {                               // (2) var var
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
	                } else {
	                    if (Yvalue == null) {                  // (3) ground var
	                        Yrepr.rehashAllAndDispose();
	                        
	                        final DoublyLinkedConstraintList<Constraint> observers = Yrepr.variableObservers;
	                        if (observers != null) {
	                            Yrepr.variableObservers = null;
	                            final Iterator<Constraint> iter = observers.iterator();
	                            while (iter.hasNext()) 
	                                iter.next().reactivate();   /* notify */
	                        }
	                    } else {                               // (4) ground ground
	                        if (!Xvalue.equals(Yvalue))
	                            throw new FailureException("Cannot make equal: " + Xvalue + " != " + Yvalue);
	                    }
	                }
	            }
	            else {
	                Xrepr.parent = Yrepr;
	
	                if (Yvalue == null) {
	                    if (Xvalue != null) {                  // (1) ground var 
	                        Yrepr.value = Xvalue;
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
	                } else {
	                    if (Xvalue == null) {                  // (3) var ground
	                        Xrepr.rehashAllAndDispose();
	                        
	                        final DoublyLinkedConstraintList<Constraint> observers = Xrepr.variableObservers;
	                        if (observers != null) {
	                            Xrepr.variableObservers = null;
	                            final Iterator<Constraint> iter = observers.iterator();
	                            while (iter.hasNext()) 
	                                iter.next().reactivate();   /* notify */
	                        }
	                    } else {                               // (4) ground ground 
	                        if (!Xvalue.equals(Yvalue))
	                            throw new FailureException("Cannot make equal " + Xvalue + " != " + Yvalue);
	                    }
	                }
	            }
        	} else {
        		constraintSystem.new QueuedBuiltInConstraint() {
    				@Override
    				public void run() { tellEqual(Xrepr, Yrepr); }
    			};
        	}
        }
    }

    public boolean askEqual(Logical<Type> X, Type value) {
        return value.equals(X.find().value);
    }

    public boolean askEqual(Type value, Logical<Type> X) {
        return value.equals(X.find().value);
    }

    public boolean askEqual(Logical<Type> X, Logical<Type> Y) {
        if (X == Y) return true;
        
        final Logical<Type> Xrepr = X.find();
        final Logical<Type> Yrepr = Y.find();

        return (Xrepr == Yrepr)
                || (Xrepr.value != null && Xrepr.value.equals(Yrepr.value));
    }
}