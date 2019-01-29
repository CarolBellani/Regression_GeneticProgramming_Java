package programElements;

public class Iff extends Operator {

	private static final long serialVersionUID = 7L;

	public Iff() {
		super(4);
	}

	public double performOperation(double... arguments) {
		if(arguments[0] < arguments[1])
			return arguments[2];
		else
			return arguments[3];
	}

	public String toString() {
		return "if";
	}
}
