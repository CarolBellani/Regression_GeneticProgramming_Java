package programElements;

public class Cos extends Operator {
	
	private static final long serialVersionUID = 7L;

	public Cos() {
		super(1);
	}

	public double performOperation(double... arguments) {
		return Math.cos(arguments[0]) ;
	}

	public String toString() {
		return "cos";
	}
	
}
