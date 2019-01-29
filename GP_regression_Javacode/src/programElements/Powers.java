package programElements;

public class Powers extends Operator {
	
	private static final long serialVersionUID = 7L;

	public Powers() {
		super(2);
	}

	public double performOperation(double... arguments) {
		return Math.pow(arguments[0], arguments[1]) ;
	}

	public String toString() {
		return "^";
	}
	
}
