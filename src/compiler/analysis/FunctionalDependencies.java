package compiler.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.Resettable;
import util.iterator.UnmodifiableIterator;

public class FunctionalDependencies implements Resettable, Iterable<FunctionalDependency> {
	
	private final List<FunctionalDependency> dependencies 
				= new ArrayList<FunctionalDependency>();
	
	public boolean add(FunctionalDependency dependency) {
		int n = getNbDependencies();
		if (n > 0) {
			if (dependency.getArity() != getArity())
				throw new IllegalArgumentException("wrong arity");
			
			for (int i = 0; i < n; i++)
				if (dependency.equals(getDependencyAt(i)))
					return false;
		}
		
		dependencies.add(dependency);
		return true;
	}
	
	public FunctionalDependency getDependencyAt(int index) {
		return dependencies.get(index);
	}
	
	public int getNbDependencies() {
		return dependencies.size();
	}
	
	public void reset() throws Exception {
		dependencies.clear();
	}
	
	public Iterator<FunctionalDependency> iterator() {
		return new UnmodifiableIterator<FunctionalDependency>(dependencies.iterator());
	}
	
	public int[] getSuperfluousIndices(int[] indexes) {
		return new int[0];
	}
	
	protected int getArity() {
		return getDependencyAt(0).getArity();
	}
	
	public boolean isEmpty() {
		return getNbDependencies() == 0;
	}
	
	public int[] getDependents(int[] fixedIndexes) {
		if (isEmpty()) return new int[0];
		boolean[] fixed = new boolean[getArity()];
		for (int i = 0; i < fixedIndexes.length; i++)
			fixed[fixedIndexes[i]] =  true;
		return getDependents(fixed);
	}
	
	/**
	 * Returns a list of indexes of previously unfixed arguments 
	 * that are functionally dependent on the arguments already fixed. 
	 * Also sets the corresponding booleans on <code>true</code> in the provided
	 * list of booleans.
	 * 
	 * @param fixed
	 * 	A boolean array indicating which arguments are already fixed
	 * (<code>true</code> is fixed; <code>false</code> is unfixed).
	 * 	The length is equal to the constraint's arity.
	 * @return A list of indexes of previously unfixed arguments 
	 * 	that are functionally dependent on the arguments already fixed.
	 */
	public int[] getDependents(boolean[] fixed) {
		boolean[] clone = fixed.clone();
		
		int n, t = 0; 
		do {
			n = 0;
			for (FunctionalDependency dependency : this)
				n += dependency.propagateInto(fixed);
			t += n;
		} while (n > 0);
		
		if (t == 0) return new int[0];
		
		int[] result = new int[t];
		for (int i = 0, j = 0; i < fixed.length; i++)
			if (fixed[i] && !clone[i]) 
				result[j++] = i;
		return result;
	}
}