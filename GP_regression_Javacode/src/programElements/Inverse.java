package programElements;

public class Inverse extends Operator {
	
	private static final long serialVersionUID = 7L;

	public Inverse() {
		super(1);
	}

	public double performOperation(double... arguments) {
		return Math.pow(arguments[0], -1);
	}

	public String toString() {
		return "inverse";
	}
	
}
