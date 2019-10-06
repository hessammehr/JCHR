package runtime;

import runtime.Handler.Continuation;

public final class ConstraintSystem {
	private final static ThreadLocal<ConstraintSystem> 
		TL = new ThreadLocal<ConstraintSystem>() {
			@Override
			protected ConstraintSystem initialValue() {
				return new ConstraintSystem();
			}
		};
	public static void reset() {
		TL.remove();
	}
	public static ConstraintSystem get() {
		return TL.get();
	}
	
	public final ContinuationStack STACK = new ContinuationStack();
	final ContinuationQueue QUEUE = new ContinuationQueue();
	
	private boolean queuing;
	
	public final boolean inDefaultHostLanguageMode() {
		return hostLanguageMode && !queuing;
	}
	public final boolean inHostLanguageMode() {
		return hostLanguageMode;
	}
	public final boolean isQueuing() { 
		return queuing && hostLanguageMode;
	}
	public void setQueuing(boolean queuing) {
		this.queuing = queuing;
	}
	public boolean hasQueued() {
		return !QUEUE.isEmpty();
	}
	final Continuation dequeue() {
		hostLanguageMode = false;
		return QUEUE.pollAndPush(STACK);
	}
	final Continuation dequeue(Continuation continuation) {
		hostLanguageMode = false;
		return QUEUE.pollAndPush(STACK, continuation);
	}
	
	boolean hostLanguageMode = true;
	
	public abstract class QueuedBuiltInConstraint extends Continuation {
		public QueuedBuiltInConstraint() {
			QUEUE.enqueue(this);
		}
		
		@Override
		protected final Continuation call() {
			run();
			return STACK.pop(); 
		}
		
		protected abstract void run();
	}
	public abstract class QueuedHostLanguageCode extends Continuation {
		public QueuedHostLanguageCode() {
			QUEUE.enqueue(this);
		}
		
		@Override
		protected final Continuation call() {
			hostLanguageMode = true;
			run();
			Continuation queued = dequeue();
			hostLanguageMode = false;
			return (queued != null)? queued : STACK.pop(); 
		}
		
		protected abstract void run();
	}
	
	@Override
	public String toString() {
		return STACK.toString() + " ++ " + QUEUE.toString();
	}
}