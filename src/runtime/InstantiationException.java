package runtime;

public class InstantiationException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public InstantiationException() {
		super();
	}
	public InstantiationException(String variableName) {
		super("Variable " + variableName + " is not ground");
	}
}