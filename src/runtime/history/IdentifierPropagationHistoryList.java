package runtime.history;

/**
 * <p>
 * A simple array-list implementation of a list of
 * {@link IdentifierPropagationHistory}'s. It can be used
 * for maintaining backpointers, thus avoiding the possible
 * memory-leak of the (distributed) propagation histories.
 * </p><p>
 * For the <code>leq</code>-benchmark this resulted in a
 * 50% slowdown, but this seems unavoidable if you want to
 * eliminate the memory leak.
 * </p>
 * 
 * @author Peter Van Weert
 */
public class IdentifierPropagationHistoryList {
	private IdentifierPropagationHistory[] array
		= new IdentifierPropagationHistory[16];
	private int size;
	
	public IdentifierPropagationHistoryList() {
		// NOP
	}
	public IdentifierPropagationHistoryList(IdentifierPropagationHistory init) {
		array[0] = init;
		size = 1;
	}
	
	public void add(IdentifierPropagationHistory hist) {
		if (hist == null) throw new NullPointerException();
		final int size = this.size++;
		if (size == array.length) {
			IdentifierPropagationHistory[] array
				= new IdentifierPropagationHistory[size << 1];
			System.arraycopy(this.array, 0, array, 0, size);
			this.array = array;
		}
		array[size] = hist;
	}
	
	public void removeFromAll(int ID) {
		final int size = this.size;
		for (int i = 0; i < size; i++)
			array[i].remove(ID);
	}
}
