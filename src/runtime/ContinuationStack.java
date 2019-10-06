package runtime;

import java.util.Arrays;
import java.util.EmptyStackException;

import runtime.Handler.Continuation;

/**
 * An unrolled linked list implementation of a stack.
 * Most public operations run in constant time. 
 * Obviously the {@link #push(Continuation[])} operation is 
 * linear in the length of the provided list.
 * The {@link #replace(int, Continuation)} is worst-case linear 
 * in the distance between the top of the stack,
 * and the position to be swapped.
 * The constant factor (equal to {@link #BLOCK_SIZE}<sup>-1</sup>) is 
 * small enough though to have pseudo-constant time swapping
 * in all but degenerate cases.
 *
 * @author Peter Van Weert
 */
public final class ContinuationStack {
	final static int BLOCK_SIZE = 256;
	
	private Block currentBlock = new Block();
	private Continuation[] stack = currentBlock.continuations;
	private int size;
	private int c;

	protected final static Continuation DROP = new Continuation() {
		@Override
		protected Continuation call() {
			return null;
		}
		@Override
		public String toString() {
			return "BOTTOM";
		}
	};
	
	/**
	 * Pushes a special bottom continuation on the stack.
	 */
	public void pushDrop() {
		// the bottom DROP is never really pushed (or popped)
		if (++size == 1) return;
		if (c != BLOCK_SIZE) 
			stack[c++] = DROP;
		else {
			stack = (currentBlock = currentBlock.nextBlock()).continuations;
			stack[0] = DROP;
			c = 1;
		}
	}
	
	/**
	 * Pushes the provided continuation on the stack.  
	 */
	public void push(Continuation continuation) {
		size++;
		if (c != BLOCK_SIZE) 
			stack[c++] = continuation;
		else {
			stack = (currentBlock = currentBlock.nextBlock()).continuations;
			stack[0] = continuation;
			c = 1;
		}
	}
	
	/**
	 * Pushes the provided continuations on the stack, in reverse order
	 * (that is: the right-most argument is pushed first, the left-most
	 * argument is pushed last).  
	 */
	public void push(Continuation c1, Continuation c2) {
		size += 2;
		switch (c) {
			default:
				stack[c++] = c2;
				stack[c++] = c1;
			break;
			
			case BLOCK_SIZE:
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c2;
				stack[1] = c1;
				c = 2;
			break;

			case BLOCK_SIZE-1:
				stack[BLOCK_SIZE-1] = c2;
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c1;
				c = 1;
			break;
		}
	}
	
	/**
	 * Pushes the provided continuations on the stack, in reverse order
	 * (that is: the right-most argument is pushed first, the left-most
	 * argument is pushed last).  
	 */
	public void push(Continuation c1, Continuation c2, Continuation c3) {
		size += 3;
		switch (c) {
			default:
				int c = this.c;
				stack[c++] = c3;
				stack[c++] = c2;
				stack[c++] = c1;
				this.c = c;
			break;

			case BLOCK_SIZE:
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c3;
				stack[1] = c2;
				stack[2] = c1;
				c = 3;
			break;

			case BLOCK_SIZE-1:
				stack[BLOCK_SIZE-1] = c3;
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c2;
				stack[1] = c1;
				c = 2;
			break;
			
			case BLOCK_SIZE-2:
				stack[BLOCK_SIZE-2] = c3;
				stack[BLOCK_SIZE-1] = c2;
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c1;
				c = 1;
			break;
		}
	}
	
	/**
	 * Pushes the provided continuations on the stack, in reverse order
	 * (that is: the right-most argument is pushed first, the left-most
	 * argument is pushed last).  
	 */
	public void push(Continuation c1, Continuation c2, Continuation c3, Continuation c4) {
		size += 4;
		switch (c) {
			default:
				int c = this.c;
				stack[c++] = c4;
				stack[c++] = c3;
				stack[c++] = c2;
				stack[c++] = c1;
				this.c = c;
			break;
			
			case BLOCK_SIZE:
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c4;
				stack[1] = c3;
				stack[2] = c2;
				stack[3] = c1;
				c = 4;
			break;

			case BLOCK_SIZE-1:
				stack[BLOCK_SIZE-1] = c4;
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c3;
				stack[1] = c2;
				stack[2] = c1;
				c = 3;
			break;
			
			case BLOCK_SIZE-2:
				stack[BLOCK_SIZE-2] = c4;
				stack[BLOCK_SIZE-1] = c3;
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c2;
				stack[1] = c1;
				c = 2;
			break;
			
			case BLOCK_SIZE-3:
				stack[BLOCK_SIZE-3] = c4;
				stack[BLOCK_SIZE-2] = c3;
				stack[BLOCK_SIZE-1] = c2;
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c1;
				c = 1;
			break;
		}
	}
	
	/**
	 * Pushes the provided continuations on the stack, in reverse order
	 * (that is: the right-most argument is pushed first, the left-most
	 * argument is pushed last).  
	 */
	public void push(Continuation c1, Continuation c2, Continuation c3, Continuation c4, Continuation c5) {
		size += 5;
		switch (c) {
			default:
				stack[c++] = c5;
				stack[c++] = c4;
				stack[c++] = c3;
				stack[c++] = c2;
				stack[c++] = c1;
			break;
			
			case BLOCK_SIZE:
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c5;
				stack[1] = c4;
				stack[2] = c3;
				stack[3] = c2;
				stack[4] = c1;
				c = 5;
			break;

			case BLOCK_SIZE-1:
				stack[BLOCK_SIZE-1] = c5;
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c4;
				stack[1] = c3;
				stack[2] = c2;
				stack[3] = c1;
				c = 4;
			break;
			
			case BLOCK_SIZE-2:
				stack[BLOCK_SIZE-2] = c5;
				stack[BLOCK_SIZE-1] = c4;
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c3;
				stack[1] = c2;
				stack[2] = c1;
				c = 3;
			break;
			
			case BLOCK_SIZE-3:
				stack[BLOCK_SIZE-3] = c5;
				stack[BLOCK_SIZE-2] = c4;
				stack[BLOCK_SIZE-1] = c3;
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c2;
				stack[1] = c1;
				c = 2;
			break;
			
			case BLOCK_SIZE-4:
				stack[BLOCK_SIZE-4] = c5;
				stack[BLOCK_SIZE-3] = c4;
				stack[BLOCK_SIZE-2] = c3;
				stack[BLOCK_SIZE-1] = c2;
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c1;
				c = 1;
			break;
		}
	}
	
	/**
	 * Pushes the provided continuations on the stack, in reverse order
	 * (that is: the right-most argument is pushed first, the left-most
	 * argument is pushed last).  
	 */
	public void push(Continuation c1, Continuation c2, Continuation c3, Continuation c4, Continuation c5, Continuation c6) {
		size += 6;
		switch (c) {
			default:
				stack[c++] = c6;
				stack[c++] = c5;
				stack[c++] = c4;
				stack[c++] = c3;
				stack[c++] = c2;
				stack[c++] = c1;
			break;
			
			case BLOCK_SIZE:
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c6;
				stack[1] = c5;
				stack[2] = c4;
				stack[3] = c3;
				stack[4] = c2;
				stack[5] = c1;
				c = 6;
			break;

			case BLOCK_SIZE-1:
				stack[BLOCK_SIZE-1] = c6;
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c5;
				stack[1] = c4;
				stack[2] = c3;
				stack[3] = c2;
				stack[4] = c1;
				c = 5;
			break;
			
			case BLOCK_SIZE-2:
				stack[BLOCK_SIZE-2] = c6;
				stack[BLOCK_SIZE-1] = c5;
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c4;
				stack[1] = c3;
				stack[2] = c2;
				stack[3] = c1;
				c = 4;
			break;
			
			case BLOCK_SIZE-3:
				stack[BLOCK_SIZE-3] = c6;
				stack[BLOCK_SIZE-2] = c5;
				stack[BLOCK_SIZE-1] = c4;
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c3;
				stack[1] = c2;
				stack[2] = c1;
				c = 3;
			break;
			
			case BLOCK_SIZE-4:
				stack[BLOCK_SIZE-4] = c6;
				stack[BLOCK_SIZE-3] = c5;
				stack[BLOCK_SIZE-2] = c4;
				stack[BLOCK_SIZE-1] = c3;
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c2;
				stack[1] = c1;
				c = 2;
			break;
			
			case BLOCK_SIZE-5:
				stack[BLOCK_SIZE-5] = c6;
				stack[BLOCK_SIZE-4] = c5;
				stack[BLOCK_SIZE-3] = c4;
				stack[BLOCK_SIZE-2] = c3;
				stack[BLOCK_SIZE-1] = c2;
				stack = (currentBlock = currentBlock.nextBlock()).continuations;
				stack[0] = c1;
				c = 1;
			break;
		}
	}
	
	/**
	 * Pushes the provided continuations on the stack, in reverse order
	 * (that is: the right-most argument is pushed first, the left-most
	 * argument is pushed last).
	 * 
	 * @param A <em>non-empty</em> array of continuations.
	 */
	public void push(final Continuation... continuations) {
		Continuation[] stack = this.stack;
		int c = this.c;
		int j = continuations.length;
		this.size += continuations.length;

		while (true) {
			do {
				stack[c++] = continuations[--j];
				if (j == 0) { this.stack = stack; this.c = c; return; }
			} while (c < BLOCK_SIZE);
			
			stack = (currentBlock = currentBlock.nextBlock()).continuations;
			c = 0;
		}
	}
	
	/**
	 * Pushes the provided continuations on the stack, in reverse order
	 * (that is: the right-most argument is pushed first, the left-most
	 * argument is pushed last).
	 * 
	 * @param A <em>non-empty</em> continuation queue (not equal to <code>null</code>).
	 * @param A first {@link Continuation} to push.
	 */
	void push(int num, Continuation continuation, ContinuationQueue.Block otherBlock, int otherC) {
		size += num + 1;
		
		Block thisBlock = this.currentBlock;
		Continuation[] thisStack = this.stack;
		int thisC = this.c;

		int thisR;
		if (thisC != BLOCK_SIZE) { 
			thisStack[thisC++] = continuation;
			thisR = BLOCK_SIZE-thisC;
		} else {
			thisStack = (thisBlock = thisBlock.nextBlock()).continuations;
			thisStack[0] = continuation;
			thisC = 1;
			thisR = BLOCK_SIZE-1;
		}
		
		int otherR = BLOCK_SIZE-otherC;
		
		if (thisR > otherR) {
			System.arraycopy(otherBlock.continuations, otherC, thisStack, thisC, otherR);
			int a = thisR - otherR, b = BLOCK_SIZE - a;
			while ((otherBlock = otherBlock.previous) != null) {
				Continuation[] queue = otherBlock.continuations;
				System.arraycopy(queue, 0, thisStack, b, a);
				thisStack = (thisBlock = thisBlock.nextBlock()).continuations;
				System.arraycopy(queue, a, thisStack, 0, b);
			}
			this.c = b;
		} else if (thisR < otherR) {
			Continuation[] queue = otherBlock.continuations;
			System.arraycopy(queue, otherC, thisStack, thisC, thisR);
			int b = otherR - thisR, a = BLOCK_SIZE - b;
			thisStack = (thisBlock = thisBlock.nextBlock()).continuations;
			System.arraycopy(queue, a, thisStack, 0, b);
			while ((otherBlock = otherBlock.previous) != null) {
				queue = otherBlock.continuations;
				System.arraycopy(queue, 0, thisStack, b, a);
				thisStack = (thisBlock = thisBlock.nextBlock()).continuations;
				System.arraycopy(queue, a, thisStack, 0, b);
			}
			this.c = b;
		} else /*if (thisR == otherR)*/ {
			System.arraycopy(otherBlock.continuations, otherC, thisStack, thisC, thisR);
			while ((otherBlock = otherBlock.previous) != null)
				System.arraycopy(otherBlock.continuations, 0, 
					(thisBlock = thisBlock.nextBlock()).continuations, 0, BLOCK_SIZE);
			this.c = BLOCK_SIZE;
		}
		
		this.currentBlock = thisBlock;
		this.stack = thisStack;
	}
	
	void push(int num, ContinuationQueue.Block otherBlock, int otherC) {
		size += num;
		
		Block thisBlock = this.currentBlock;
		Continuation[] thisStack = this.stack;
		int thisC = this.c;

		int thisR = BLOCK_SIZE-thisC, otherR = BLOCK_SIZE-otherC;
		
		if (thisR > otherR) {
			System.arraycopy(otherBlock.continuations, otherC, thisStack, thisC, otherR);
			int a = thisR - otherR, b = BLOCK_SIZE - a;
			while ((otherBlock = otherBlock.previous) != null) {
				Continuation[] queue = otherBlock.continuations;
				System.arraycopy(queue, 0, thisStack, b, a);
				thisStack = (thisBlock = thisBlock.nextBlock()).continuations;
				System.arraycopy(queue, a, thisStack, 0, b);
			}
			this.c = b;
		} else if (thisR < otherR) {
			Continuation[] queue = otherBlock.continuations;
			System.arraycopy(queue, otherC, thisStack, thisC, thisR);
			int b = otherR - thisR, a = BLOCK_SIZE - b;
			thisStack = (thisBlock = thisBlock.nextBlock()).continuations;
			System.arraycopy(queue, a, thisStack, 0, b);
			while ((otherBlock = otherBlock.previous) != null) {
				queue = otherBlock.continuations;
				System.arraycopy(queue, 0, thisStack, b, a);
				thisStack = (thisBlock = thisBlock.nextBlock()).continuations;
				System.arraycopy(queue, a, thisStack, 0, b);
			}
			this.c = b;
		} else /*if (thisR == otherR)*/ {
			System.arraycopy(otherBlock.continuations, otherC, thisStack, thisC, thisR);
			while ((otherBlock = otherBlock.previous) != null)
				System.arraycopy(otherBlock.continuations, 0, 
					(thisBlock = thisBlock.nextBlock()).continuations, 0, BLOCK_SIZE);
			this.c = BLOCK_SIZE;
		}
		
		this.currentBlock = thisBlock;
		this.stack = thisStack;
	}
	
	/**
	 * Replaces the {@link Continuation} at a given index with
	 * the given one, and returns the former {@link Continuation}.
	 * @param index
	 * @param continuation
	 * 
	 * @return The {@link Continuation} previously at the given index. 
	 */
	public Continuation replace(int index, Continuation continuation) {
		Continuation[] stack;
		int c = this.c - (size - index);
		
		if (c >= 0) {
			stack = this.stack;
		} else {
			Block current = currentBlock;
			do {
				current = current.previous;
				c += BLOCK_SIZE;
			} while (c < 0);
			stack = current.continuations;
		}

		Continuation result = stack[c];
		stack[c] = continuation;
		return result;
	}
	
	/**
	 * Replaces the {@link Continuation} at the top with
	 * the given one, and returns the former {@link Continuation}.
	 * In other words &quot;<code>d = stack.swap(c);</code>&quot;
	 * is equivalent to (but slightly more efficient than) 
	 * &quot;<code>d = stack.pop(); stack.push(c);</code>&quot;.
	 * 
	 * @param continuation
	 * 
	 * @return The {@link Continuation} previously at the top. 
	 */
	public Continuation replace(Continuation continuation) {
		Continuation[] stack;
		int c = this.c - 1;
		
		if (c >= 0) {
			stack = this.stack;
		} else try {
			stack = (currentBlock = currentBlock.previous).continuations;
			c = BLOCK_SIZE - 1;
		} catch (NullPointerException npe) {
			throw new EmptyStackException();
		}
		
		Continuation result = stack[c];
		stack[c] = continuation;
		return result;
	}
	
	public Continuation pop() throws EmptyStackException {
		// the bottom DROP is not really pushed (or popped)
		if (--size == 0) return DROP;
		
		Continuation[] stack = this.stack;
		
		if (c == BLOCK_SIZE) {
			currentBlock.clearFirstNextBlock();	// let gc do its thing 
			return stack[--c];
		}

		stack[c] = null;
		if (c > 0) return stack[--c];	// let gc do its thing 
		
		try {
			this.stack = stack = (currentBlock = currentBlock.previousBlock()).continuations;
			c = BLOCK_SIZE - 1;
			return stack[BLOCK_SIZE - 1];
		} catch (NullPointerException npe) {
			size = 0;
			throw new EmptyStackException();
		}
	}
	
	/**
	 * Undo the last {@link #pop()} operation.
	 * Only the very last {@link #pop()} can be undone, 
	 * and no other stack operations are allowed between the 
	 * {@link #pop()} and the {@link #undoPop()}.
	 * The {@link #undoPop()}-operation is not allowed if the stack is empty.
	 * If called when one of these conditions is valiolated,
	 * the stack will become inconsistent. 
	 */
	public void undoPop() {
		c++;
		size++;
	}
	
	public Continuation peek() throws EmptyStackException {
		if (c == 0) {
			if (size == 1)
				return DROP; 
			else try {
				return currentBlock.previous.continuations[BLOCK_SIZE - 1];
			} catch (NullPointerException npe) {
				throw new EmptyStackException();
			}
		} else {
			return stack[c-1];
		}
	}
	
	public int getSize() {
		return size;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public void reset() {
		stack = (currentBlock = new Block()).continuations;
		size = c = 0;
	}
	
	private final static class Block {
		public final Block previous;
		public Block next;
		public final Continuation[] continuations = new Continuation[BLOCK_SIZE];
		public Block() {
			this.previous = null;
		}
		public Block(Block previous) {
			this.previous = previous;
		}
		public void clearFirstNextBlock() {
			if (next != null) next.continuations[0] = null;
		}
		public Block nextBlock() {
			return next == null? next = new Block(this) : next;
		}
		public Block previousBlock() {
			// keep one (and only one) extra block 
			if (next != null) next.next = null;
			return previous;
		}
		@Override
		public String toString() {
			return Arrays.toString(continuations);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		Block current = currentBlock;
		while (current.previous != null)
			current = current.previous;
		result.append(current);
		while (current != currentBlock)
			result.append('-').append(current = current.next);
		return result.toString();
	}
}