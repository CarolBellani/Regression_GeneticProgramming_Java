package programElements;

public class Log10 extends Operator {
	
	private static final long serialVersionUID = 7L;

	public Log10() {
		super(1);
	}

	public double performOperation(double... arguments) {
		if(arguments[0] == 0)
			return 0;
		
		
		return Math.log10(Math.abs(arguments[0])) ;
	}

	public String toString() {
		return "log10";
	}
	
}
