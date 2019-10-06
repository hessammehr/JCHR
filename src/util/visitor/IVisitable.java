package util.visitor;

/**
 * There is a major disadvantage to using this interface: 
 * each class can only implement it at most once 
 * (i.e.: not with different type parameters).
 *
 * @author Peter Van Weert
 */
public interface IVisitable<T extends IVisitor> {
	public void accept(T visitor) throws Exception;
}
