package programElements;

public class Negative extends Operator {
	
	private static final long serialVersionUID = 7L;

	public Negative() {
		super(1);
	}

	public double performOperation(double... arguments) {
		
		
			return -arguments[0];
		

	}

	public String toString() {
		return "%";
	}
	
}
