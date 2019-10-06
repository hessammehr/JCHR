package util.collections;

import java.util.Iterator;
import java.util.SortedSet;

public final class CollectionUtils {

	private CollectionUtils() { /* not instantiatable */ }
	
	public static <E extends Comparable<? super E>> boolean disjoint(SortedSet<E> one, SortedSet<E> other) {
		if (one.isEmpty() || other.isEmpty()) return true;
		
		Iterator<E> oneIter = one.iterator(), otherIter = other.iterator();
		E oneCurrent = oneIter.next(), otherCurrent = otherIter.next();
		
		int comp = oneCurrent.compareTo(otherCurrent); 
		
		do {
			while (comp < 0 && oneIter.hasNext())
				comp = (oneCurrent = oneIter.next()).compareTo(otherCurrent);
			while (comp > 0 && otherIter.hasNext()) {
				if (!oneIter.hasNext()) return true;
				comp = oneCurrent.compareTo(otherCurrent = otherIter.next());
			}
			if (comp == 0) return false;
		} while (oneIter.hasNext() || otherIter.hasNext());
		
		return true;
	}
}
