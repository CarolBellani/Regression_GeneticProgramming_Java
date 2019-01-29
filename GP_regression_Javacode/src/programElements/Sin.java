package programElements;

public class Sin extends Operator {
	
	private static final long serialVersionUID = 7L;

	public Sin() {
		super(1);
	}

	public double performOperation(double... arguments) {
		return Math.sin(arguments[0]) ;
	}

	public String toString() {
		return "sin";
	}
	
}
