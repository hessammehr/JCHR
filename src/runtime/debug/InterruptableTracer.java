package runtime.debug;

import runtime.Constraint;

public class InterruptableTracer extends TracerDecorator {

    private boolean debugging;
    
    public InterruptableTracer(Tracer tracer) {
    	this(new SleepingDecoratingTracer(tracer));
    }
    
    public InterruptableTracer(InterruptingTracer tracer) {
        super(tracer);
        tracer.linkIteruptor(this);
    }
    
    protected synchronized void resume() {
        debugging = false;
        notifyAll();
    }
    
    protected synchronized void interrupt(Runnable doFirst) {
        try {
            debugging = true;
            new Thread(doFirst).start();
            while (debugging) wait();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void activated(final Constraint constraint) {
        if (getDecorated().warrantsInterruptIfActivated(constraint)) {
            interrupt(new Runnable() {
                @SuppressWarnings("synthetic-access")
                public void run() {
                    InterruptableTracer.super.activated(constraint);
                }
            });
        } else {
            super.activated(constraint);   
        }
    }

    @Override
    public void fires(final String ruleId, final int activeIndex, final Constraint... constraints) {
        if (getDecorated().warrantsInterruptIfFires(ruleId, activeIndex, constraints)) {
            interrupt(new Runnable() {
                @SuppressWarnings("synthetic-access")
                public void run() {
                    InterruptableTracer.super.fires(ruleId, activeIndex, constraints);
                }
            });
        } else {
            super.fires(ruleId, activeIndex, constraints);
        }
    }
    
    @Override
    public void fired(final String ruleId, final int activeIndex, final Constraint... constraints) {
        if (getDecorated().warrantsInterruptIfFires(ruleId, activeIndex, constraints)) {
            interrupt(new Runnable() {
                @SuppressWarnings("synthetic-access")
                public void run() {
                    InterruptableTracer.super.fired(ruleId, activeIndex, constraints);
                }
            });
        } else {
            super.fired(ruleId, activeIndex, constraints);
        }
    }
    
    @Override
    public void suspended(final Constraint constraint) {
    	if (getDecorated().warrantsInterruptIfSuspended(constraint)) {
            interrupt(new Runnable() {
                @SuppressWarnings("synthetic-access")
                public void run() {
                    InterruptableTracer.super.suspended(constraint);
                }
            });
        } else {
            super.suspended(constraint);
        }
    }

    @Override
    public void reactivated(final Constraint constraint) {
        if (getDecorated().warrantsInterruptIfReactivated(constraint)) {
            interrupt(new Runnable() {
                @SuppressWarnings("synthetic-access")
                public void run() {
                    InterruptableTracer.super.reactivated(constraint);
                }
            });
        } else {
            super.reactivated(constraint);
        }
    }
    
    

    @Override
    public void removed(final Constraint constraint) {
        if (getDecorated().warrantsInterruptIfRemoved(constraint)) {
            interrupt(new Runnable() {
                @SuppressWarnings("synthetic-access")
                public void run() {
                    InterruptableTracer.super.removed(constraint);
                }
            });
        } else {
            super.removed(constraint);
        }
    }

    @Override
    public void stored(final Constraint constraint) {
        if (getDecorated().warrantsInterruptIfStored(constraint)) {
            interrupt(new Runnable() {
                @SuppressWarnings("synthetic-access")
                public void run() {
                    InterruptableTracer.super.stored(constraint);
                }
            });
        } else {
            super.stored(constraint);
        }
    }
    
    @Override
    protected InterruptingTracer getDecorated() {
        return (InterruptingTracer)super.getDecorated();
    }
    
    public static abstract class InterruptingTracer implements Tracer {
        private InterruptableTracer interruptor;
        
        void linkIteruptor(InterruptableTracer interruptor) {
            if (this.interruptor != null)
                throw new IllegalStateException();
            this.interruptor = interruptor;
        }
        
        protected void resume() {
            if (interruptor != null) interruptor.resume();
        }
        
        protected boolean warrantsInterruptIfActivated(Constraint constraint) {
            return true;
        }
        
        protected boolean warrantsInterruptIfReactivated(Constraint constraint) {
            return true;
        }
        
        protected boolean warrantsInterruptIfStored(Constraint constraint) {
            return true;
        }
        
        protected boolean warrantsInterruptIfSuspended(Constraint constraint) {
            return true;
        }
        
        protected boolean warrantsInterruptIfRemoved(Constraint constraint) {
            return true;
        }
        
        protected boolean warrantsInterruptIfTerminated(Constraint constraint) {
            return true;
        }
        
        protected boolean warrantsInterruptIfFires(String ruleId, int activeIndex, Constraint... constraints) {
            return true;
        }
        
        protected boolean warrantsInterruptIfFired(String ruleId, int activeIndex, Constraint... constraints) {
            return true;
        }
    }
    
    public abstract static class DecoratingInterruptingTracer extends InterruptingTracer {
    	private Tracer decorated;

        public DecoratingInterruptingTracer(Tracer tracer) {
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
    
    public static class SleepingDecoratingTracer extends DecoratingInterruptingTracer {
    	public final static long DEFAULT_MILLIS = 666;
    	
    	private long millis;
    	
    	public SleepingDecoratingTracer(Tracer tracer) {
    		this(tracer, DEFAULT_MILLIS);
    	}
    	
    	public SleepingDecoratingTracer(Tracer tracer, long millis) {
    		super(tracer);
			setMillis(millis);
		}
    	
    	public void setMillis(long millis) {
			this.millis = millis;
		}
    	public long getMillis() {
			return millis;
		}
    	
    	protected void sleep() {
    		try { Thread.sleep(getMillis()); }
    		catch (InterruptedException ie) {/* NOP */}
    	}

		@Override
		public void activated(Constraint constraint) {
			super.activated(constraint);
			sleep(); resume();
		}

		@Override
		public void fires(String ruleId, int activeIndex, Constraint... constraints) {
			super.fires(ruleId, activeIndex, constraints);
			sleep(); resume();
		}
		
		@Override
		public void fired(String ruleId, int activeIndex, Constraint... constraints) {
			super.fired(ruleId, activeIndex, constraints);
			sleep(); resume();
		}

		@Override
		public void reactivated(Constraint constraint) {
			super.reactivated(constraint);
			sleep(); resume();
		}

		@Override
		public void removed(Constraint constraint) {
			super.removed(constraint);
			sleep(); resume();
		}

		@Override
		public void stored(Constraint constraint) {
			super.stored(constraint);
			sleep(); resume();
		}

		@Override
		public void suspended(Constraint constraint) {
			super.suspended(constraint);
			sleep(); resume();
		}

		@Override
		public void terminated(Constraint constraint) {
			super.terminated(constraint);
			sleep(); resume();
		}
    }
}