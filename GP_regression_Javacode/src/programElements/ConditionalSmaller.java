package programElements;

public class ConditionalSmaller extends Operator {

	private static final long serialVersionUID = 7L;

	public ConditionalSmaller() {
		super(2);
	}

	public double performOperation(double... arguments) {
		if(arguments[0] < arguments[1])
			return 1;
		else
			return 0;
		
	}

	public String toString() {
		return "<";
	}
}
