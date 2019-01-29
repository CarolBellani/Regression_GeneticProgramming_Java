package programElements;

public class Min extends Operator {
	
	private static final long serialVersionUID = 7L;

	public Min() {
		super(2);
	}

	public double performOperation(double... arguments) {
		if(arguments[0] < arguments[1])
			return arguments[0];
		else
			return arguments[1];
	}

	public String toString() {
		return "min";
	}
	
}
