package programElements;

public class Average extends Operator {
	
	private static final long serialVersionUID = 7L;

	public Average() {
		super(2);
	}

	public double performOperation(double... arguments) {
		return (arguments[0] + arguments[1])/2;
	}

	public String toString() {
		return "avg";
	}
	
}
