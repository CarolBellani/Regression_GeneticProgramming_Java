package programElements;

public class Sqr extends Operator {
	
	private static final long serialVersionUID = 7L;

	public Sqr() {
		super(1);
	}

	public double performOperation(double... arguments) {
		if(arguments[0] == 0)
			return 0;
		
		
		return Math.sqrt(Math.abs(arguments[0])) ;
	}

	public String toString() {
		return "sqrt";
	}
	
}
