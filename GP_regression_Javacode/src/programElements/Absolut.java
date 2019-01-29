package programElements;

public class Absolut extends Operator {
	
	private static final long serialVersionUID = 7L;

	public Absolut() {
		super(1);
	}

	public double performOperation(double... arguments) {
		return Math.abs(arguments[0]) ;
	}

	public String toString() {
		return "abs";
	}
	
}
