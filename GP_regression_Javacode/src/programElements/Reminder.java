package programElements;

public class Reminder extends Operator {
	
	private static final long serialVersionUID = 7L;
	public static final double MINIMUM_DENOMINATOR_VALUE = 0.00000000001;

	public Reminder() {
		super(2);
	}

	public double performOperation(double... arguments) {
		if(arguments[1] > arguments[0]) {
			if (Math.abs(arguments[0]) < MINIMUM_DENOMINATOR_VALUE) {
				return arguments[1];
			}
			else
				return arguments[1] % arguments[0];
		}
		else {
			if (Math.abs(arguments[1]) < MINIMUM_DENOMINATOR_VALUE) {
				return arguments[0];
			}else 
				return arguments[0] % arguments[1] ;
		}
	}

	public String toString() {
		return "%";
	}
	
}