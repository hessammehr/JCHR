package runtime.list;

import runtime.Constraint;
import runtime.SinglyLinkedConstraintList;
import runtime.Handler.RehashableKey;

public abstract class RehashableSinglyLinkedConstraintList<T extends Constraint> 
	extends SinglyLinkedConstraintList<T>
	implements RehashableKey {
	
	private static int $idCounter;
	private final int ID = --$idCounter;
	public final int getRehashableKeyId() {
		return ID;
	}
}