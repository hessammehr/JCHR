package compiler.analysis;

import java.util.Arrays;

import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;

/**
 * <p>
 * A functional dependency (FD) is a constraint between two sets 
 * of attributes of a constraint.
 * </p><p>
 * Given a constraint {@code C}, a set of attributes {@code X} of 
 * {@code C} is said to <em>functionally determine</em> an attribute 
 * {@code Y} of {@code C}, (written {@code X --> Y}) 
 * iff each {@code X} value is associated with precisely one {@code Y} 
 * value. 
 * We call {@code X} the <em>determinant set</em> and {@code Y} the 
 * <em>dependent attribute</em>. 
 * Thus, given the values of the attributes in {@code X}, 
 * one can determine the corresponding value of {@code Y}.
 * </p><p>
 * A {@link FunctionalDependency} consists of two sets of attributes
 * of a constraint, a <em>determinant set</em> {@code X} and a 
 * <em>dependent set</em> {@code Y}. Each attribute in {@code Y} is
 * functionally determined by {@code X}. 
 * </p>
 * 
 * @author Peter Van Weert
 */
public abstract class FunctionalDependency {

	private int arity;
	
	public FunctionalDependency(UserDefinedConstraint constraint) {
		this(constraint.getArity());
	}
	
	public FunctionalDependency(int arity) {
		setArity(arity); 
	}
	
	protected void setArity(int arity) {
		this.arity = arity;
	}
	public int getArity() {
		return arity;
	}
	
	/**
	 * The indices of the attributes of the determinant set:
	 * each index occurs only once, 
	 * and the indices are sorted from small to large. 
	 * 
	 * @return The indices of the attributes of the determinant set.
	 */
	public abstract int[] getDeterminantSet();
	
	public int getNbDeterminants() {
		return getDeterminantSet().length;
	}
	
	/**
	 * The indices of the attributes of the dependent set:
	 * each index occurs only once, 
	 * and the indices are sorted from small to large. 
	 * 
	 * @return The indices of the attributes of the determinant set.
	 */
	public abstract int[] getDependentSet();
	
	public int getNbDependents() {
		return getDependentSet().length;
	}
	
	public boolean isDependent(int i) {
		return isComplete() 
			|| Arrays.binarySearch(getDependentSet(), i) >= 0;
	}
	
	/**
	 * Tests whether the attributes of the determinant set functinally determine
	 * <em>all</em> the attributes of the constraint or not?
	 * 
	 * @return <code>true</code> if the attributes of the determinant set 
	 * 	functinally determine <em>all</em> the attributes of the constraint; 
	 * 	<code>false</code> otherwise. 
	 */
	public boolean isComplete() {
		return getNbDeterminants() + getNbDependents() == getArity();
	}
	
	@Override
	public String toString() {
		int det[] = getDeterminantSet(), l = det.length;
		int dep[] = getDependentSet(), m = dep.length;
		StringBuilder result = new StringBuilder((l+m)<<2);
		result.append('{');
		if (l > 0) {
			result.append(det[0]);
			for (int i = 1; i < l; i++)
				result.append(", ").append(det[i]);
		}
		result.append("} --> {");
		if (m > 0) {
			result.append(dep[0]);
			for (int i = 1; i < m; i++)
				result.append(", ").append(dep[i]);
		}
		return result.append('}').toString();
	}
	
	public boolean isTrivial() {
		return getNbDependents() == 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof FunctionalDependency)
			&& equals((FunctionalDependency)obj);
	}
	public boolean equals(FunctionalDependency other) {
		return this.getNbDependents() == other.getNbDependents()
			&& this.getNbDeterminants() == other.getNbDeterminants()
			&& Arrays.equals(this.getDependentSet(), other.getDependentSet())
			&& Arrays.equals(this.getDeterminantSet(), other.getDeterminantSet());
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(getDeterminantSet())
			 ^ Arrays.hashCode(getDependentSet());
	}
	
	public int[] getSuperfluousIndices(int[] indexes) {
		return new int[0];
	}
	
	int propagateInto(boolean[] fixed) {
		int[] determinants = getDeterminantSet();
		for (int i = 0; i < determinants.length; i++)
			if (!fixed[determinants[i]]) return 0;
		int result = 0;
		for (int i = 0; i < fixed.length; i++) {
			if (!fixed[i] && isDependent(i)) {
				fixed[i] = true;
				result++;
			}
		}
		return result;
	}
}