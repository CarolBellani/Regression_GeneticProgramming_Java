package programElements;

public class PowerTwo extends Operator {
	
	private static final long serialVersionUID = 7L;
	public static final double MINIMUM_DENOMINATOR_VALUE = 0.00000000001;


	public PowerTwo() {
		super(1);
	}

	public double performOperation(double... arguments) {
		if (Math.abs(arguments[0]) < MINIMUM_DENOMINATOR_VALUE) {
			return 0;
		}
		return Math.pow(arguments[0], 2) ;
	}

	public String toString() {
		return "^2";
	}
	
}
