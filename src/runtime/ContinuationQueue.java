package runtime;

import static runtime.ContinuationStack.BLOCK_SIZE;

import java.util.Arrays;

import runtime.Handler.Continuation;

/**
 * An unrolled linked list implementation of a queue.
 * 
 * @author Peter Van Weert
 */
public final class ContinuationQueue {
	Block currentBlock = new Block();
	Continuation[] queue = currentBlock.continuations;
	int size;
	int c = BLOCK_SIZE;

	/**
	 * Enqueues the provided continuation on the queue.  
	 */
	public void enqueue(Continuation continuation) {
		size++;
		if (c > 0) 
			queue[--c] = continuation;
		else {
			queue = (currentBlock = new Block(currentBlock)).continuations;
			queue[BLOCK_SIZE-1] = continuation;
			c = BLOCK_SIZE-1;
		}
	}
	
	/**
	 * Returns the first continuation in the queue,
	 * and pushes the rest of the continuations 
	 * in reversed order on the given stack.
	 * If empty, the result will simply be zero.
	 */
	public Continuation pollAndPush(ContinuationStack stack) {
		int size = this.size;
		if (size == 0) return null;
		this.size = 0;
		int c = this.c;
		this.c = BLOCK_SIZE;
		Continuation result = queue[c];
		if (size == 1) {
			queue[c] = null;
			return result;
		} else {	// size > 1
			if (c == BLOCK_SIZE-1)
				stack.push(size, currentBlock.previous, 0);
			else
				stack.push(size, currentBlock, c+1);
			queue = (currentBlock = new Block()).continuations;
			return result;
		} 
	}
	
	/**
	 * Returns the first continuation in the queue 
	 * (or the provided continuation if the queue is empty),
	 * and pushes the rest of the continuations 
	 * (including the provided continuation) 
	 * in reversed order on the given stack.
	 */
	public Continuation pollAndPush(ContinuationStack stack, Continuation continuation) {
		int size = this.size;
		if (size == 0) return continuation;
		this.size = 0;
		int c = this.c;
		this.c = BLOCK_SIZE;
		Continuation result = queue[c];
		if (size == 1) {
			stack.push(continuation);
			queue[c] = null;
			return result;
		} else {	// size > 1
			if (c == BLOCK_SIZE-1)
				stack.push(size, continuation, currentBlock.previous, 0);
			else
				stack.push(size, continuation, currentBlock, c+1);
			queue = (currentBlock = new Block()).continuations;
			return result;
		}
	}
	
	public int getSize() {
		return size;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public void reset() {
		queue = (currentBlock = new Block()).continuations;
		size = 0;
		c = BLOCK_SIZE;
	}
	
	final static class Block {
		public final Block previous;
		public final Continuation[] continuations = new Continuation[BLOCK_SIZE];
		public Block() {
			this.previous = null;
		}
		public Block(Block previous) {
			this.previous = previous;
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
		result.append(current);
		while ((current = current.previous) != null) 
			result.append('-').append(current);
		return result.toString();
	}
}
