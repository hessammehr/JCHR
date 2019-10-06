package runtime.list;

import runtime.Constraint;
import runtime.DoublyLinkedConstraintList;
import runtime.Handler.RehashableKey;

public abstract class RehashableDoublyLinkedConstraintList<T extends Constraint> 
	extends DoublyLinkedConstraintList<T>
	implements RehashableKey {
	
	private static int $idCounter;
	private final int ID = --$idCounter;
	public final int getRehashableKeyId() {
		return ID;
	}
}