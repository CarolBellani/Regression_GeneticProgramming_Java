package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import programElements.Constant;
import programElements.InputVariable;
import programElements.Operator;
import programElements.ProgramElement;
import programElements.Terminal;
import utils.Data;
import utils.Parameters;
import utils.Utils;

public class Individual implements Serializable {
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Parameters
	//
	private static final long serialVersionUID = 7L;
	protected static long ID;
	protected long id;
	protected ArrayList<ProgramElement> program;
	protected int depth;
	protected double trainingError, unseenError;
	protected double[] trainingOutputs, unseenOutputs;
	protected int evaluateIndex;
	protected int maximumDepthAchieved;
	protected int depthCalculationIndex;
	protected int printIndex;
	protected double outputTargetAvg, outputTargetAvgIndividual;
	protected double parsimonyMeasure;
	
	protected boolean sizeOverride;
	protected int computedSize;
	
	
	// --------------------------------------------------------------------

	public Individual() {
		program = new ArrayList<ProgramElement>();
		id = ID++;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Evaluation
	//
	public void evaluate(Data data) {
		evaluateOnTrainingData(data);
		evaluateOnUnseenData(data);
	}
	public void evaluateSplitted(Data data, int n) {
//		evaluateOnTrainingDataSplitted(data, n);
		evaluateOnTrainingDataHalf(data, n);
		evaluateOnUnseenData(data);
	}
	public double[] evaluateOnTrainingData(Data data) {
		double[][] trainingData = data.getTrainingData();
		if (sizeOverride == false) {
			trainingOutputs = evaluate(trainingData);
		}
		trainingError = calculateRMSE(trainingData, trainingOutputs);
		return trainingOutputs;
	}
	public double[] evaluateOnTrainingDataSplitted(Data data, int nSplit) {
		double[][] trainingData = data.getSplittN(nSplit);
		if (sizeOverride == false) {
			trainingOutputs = evaluate(trainingData);
		}
		trainingError = calculateRMSE(trainingData, trainingOutputs);
		return trainingOutputs;
	}
	public double[] evaluateOnTrainingDataHalf(Data data, int nSplit) {
		double[][] trainingData = data.getSplittHalf(nSplit%2);
		if (sizeOverride == false) {
			trainingOutputs = evaluate(trainingData);
		}
		trainingError = calculateRMSE(trainingData, trainingOutputs);
		return trainingOutputs;
	}
	public double[] evaluateOnUnseenData(Data data) {
		double[][] unseenData = data.getUnseenData();
		if (sizeOverride == false) {
			unseenOutputs = evaluate(unseenData);
		}
		unseenError = calculateRMSE(unseenData, unseenOutputs);
		//for testing
		//outputTargetAvg(unseenData, unseenOutputs);
		return unseenOutputs;
	}


	public double[] evaluate(double[][] data) {
		double[] outputs = new double[data.length];
		for (int i = 0; i < outputs.length; i++) {
			evaluateIndex = 0;
			outputs[i] = evaluateInner(data[i]);
		}
		setOutputTargetAvgData(data, outputs);
		return outputs;
	}

	protected double evaluateInner(double[] dataInstance) {
		if (program.get(evaluateIndex) instanceof InputVariable) {
			InputVariable inputVariable = (InputVariable) program.get(evaluateIndex);
			return inputVariable.getValue(dataInstance);
		} else if (program.get(evaluateIndex) instanceof Constant) {
			Constant constant = (Constant) program.get(evaluateIndex);
			return constant.getValue();
		} else {
			Operator operator = (Operator) program.get(evaluateIndex);
			double[] arguments = new double[operator.getArity()];
			for (int i = 0; i < arguments.length; i++) {
				evaluateIndex++;
				arguments[i] = evaluateInner(dataInstance);
			}
			return operator.performOperation(arguments);
		}
	}

	protected double calculateRMSE(double[][] data, double[] outputs) {
		double errorSum = 0.0;
//		System.out.println(data.length);
		for (int i = 0; i < data.length; i++) {
			double target = data[i][data[0].length - 1];
			errorSum += Math.pow(outputs[i] - target, 2.0);
			//System.out.println("Output predicted "+outputs[i]+"  real output  "+target);
		}
		return Math.sqrt(errorSum / data.length);
	}
	// just experimenting
	protected void setOutputTargetAvgData(double[][] data, double[] outputs) {
		double avg = 0, targetavg = 0;
	
		for (int i = 0; i < data.length; i++) {
			double target = data[i][data[0].length - 1];
			avg += outputs[i] ;
			targetavg += target;
		}
		outputTargetAvg = avg / outputs.length;
		outputTargetAvgIndividual = targetavg / data.length;
//		System.out.println("targetavg "+targetavg/data.length);
//		System.out.println(" output avg "+ avg/outputs.length);
	}
	protected double outputTargetAvgIndividual(double[][] data, double[] outputs) {
		double avg = 0, targetavg = 0;
	
		for (int i = 0; i < data.length; i++) {
			double target = data[i][data[0].length - 1];
			avg += outputs[i] ;
			targetavg += target;
		}
//		System.out.println("targetavg "+targetavg/data[0].length+" l "+data.length);
//		System.out.println(" output avg "+ avg/outputs.length+ "l" + outputs.length );
		return avg/outputs.length;
	}
	//true if high, false if low    --> target avg output of indv compared to target
	public boolean highLow() {
		if(outputTargetAvgIndividual > outputTargetAvg  )
			return true;
		else
			return false;
	}
	
	public double getOutputTargetAvg() {
		return outputTargetAvg;
	}
	public double getOutputTargetAvgIndividual() {
		return outputTargetAvgIndividual;
	}
	// --------------------------------------------------------------------

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Copy
	//
	public Individual deepCopy() {
		Individual newIndividual = new Individual();
		for (int i = 0; i < program.size(); i++) {
			newIndividual.program.add(program.get(i));
		}
		newIndividual.setDepth(depth);
		return newIndividual;
	}

	public Individual selectiveDeepCopy(int exclusionZoneStartIndex, int exclusionZoneEndIndex) {
		Individual newIndividual = new Individual();
		for (int i = 0; i < exclusionZoneStartIndex; i++)
			newIndividual.program.add(program.get(i));

		for (int i = exclusionZoneEndIndex + 1; i < program.size(); i++)
			newIndividual.program.add(program.get(i));

		return newIndividual;
	}
	// --------------------------------------------------------------------

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Depth calculation
	//
	public void calculateDepth() {
		maximumDepthAchieved = 0;
		depthCalculationIndex = 0;
		calculateDepth(0);
		depth = maximumDepthAchieved;
	}

	protected void calculateDepth(int currentDepth) {
		if (program.get(depthCalculationIndex) instanceof Operator) {
			Operator currentOperator = (Operator) program.get(depthCalculationIndex);
			for (int i = 0; i < currentOperator.getArity(); i++) {
				depthCalculationIndex++;
				calculateDepth(currentDepth + 1);
			}
		} else {
			if (currentDepth > maximumDepthAchieved)
				maximumDepthAchieved = currentDepth;
		}
	}
	// --------------------------------------------------------------------

	public int countElementsToEnd(int startingIndex) {
		if (program.get(startingIndex) instanceof Terminal)
			return 1;
		else {
			Operator operator = (Operator) program.get(startingIndex);
			int numberOfElements = 1;
			for (int i = 0; i < operator.getArity(); i++)
				numberOfElements += countElementsToEnd(startingIndex + numberOfElements);

			return numberOfElements;
		}
	}

	public void writeToObjectFile(String fullPath) {
		Utils.writeObject(this, fullPath+".obj");
	}

	public void addProgramElement(ProgramElement programElement) {
		program.add(programElement);
	}

	public void addProgramElementAtIndex(ProgramElement programElement, int index) {
		program.add(index, programElement);
	}

	public void removeProgramElementAtIndex(int index) {
		program.remove(index);
	}

	public ProgramElement getProgramElementAtIndex(int index) {
		return program.get(index);
	}

	public void setProgramElementAtIndex(ProgramElement programElement, int index) {
		program.set(index, programElement);
	}

	public void print() {
		printIndex = 0;
		if(Parameters.GP_APPLY||Parameters.BUILD_INDIVIDUALS)
			printInner();
		System.out.printf("\nIndividual "+id+" training error:\t%.2f\n", trainingError);
		System.out.printf("Individual "+id+" unseen error:\t%.2f\n", unseenError);
		System.out.println("Individual "+id+" size:\t" + program.size());
		System.out.println("Individual "+id+" depth:\t" + depth);
	}

	protected void printInner() {
		if (program.get(printIndex) instanceof Terminal) {
			System.out.print(" " + program.get(printIndex));
		} else {
			System.out.print(" (");
			System.out.print(program.get(printIndex));
			Operator currentOperator = (Operator) program.get(printIndex);
			for (int i = 0; i < currentOperator.getArity(); i++) {
				printIndex++;
				printInner();
			}
			System.out.print(")");
		}
	}

	public double getTrainingError() {
		return trainingError;
	}

	public double getUnseenError() {
		return unseenError;
	}

	public double[] getTrainingOutputs() {
		return trainingOutputs;
	}

	public double[] getUnseenOutputs() {
		return unseenOutputs;
	}
	
	public void setTrainingOutputs(double[] outputs) {
		this.trainingOutputs=outputs;
	}

	public void setUnseenOutputs(double[] outputs) {
		this.unseenOutputs=outputs;
	}

	public long getId() {
		return id;
	}

	public int getSize() {
		if (sizeOverride) {
			return computedSize;
		} else {
			return program.size();
		}
	}

	public int getDepth() {
		return depth;
	}

	public ArrayList<ProgramElement> getProgram() {
		return program;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public void setSizeOverride(boolean sizeOverride) {
		this.sizeOverride = sizeOverride;
	}

	public void setComputedSize(int computedSize) {
		this.computedSize = computedSize;
	}
	//Count number of elements and operators
	public int getNumberOfVariables() {
		int count=0; 
		for(int i=0; i<getSize();i++) {
			if( getProgramElementAtIndex(i).toString().contains("X") ) {
				count+=1;
			} else {
				
			}
		}
		return count;
	}
	
	

	
	public double getParsimonyMeasure() {
//		parsimonyMeasure = trainingError + Math.sqrt(getDepth())  ;
//		System.out.println("erro "+trainingError);
//		System.out.println("pars "+parsimonyMeasure);
//		return parsimonyMeasure;
		
		
		
		if(Parameters.GP_APPLY) {
			if(getSize() < 500)
				parsimonyMeasure = trainingError;
			else if(getSize() < 550)
				parsimonyMeasure = trainingError + 0.03;
			else if(getSize() < 600)
				parsimonyMeasure = trainingError + 0.07;
			else if(getSize() < 650)
				parsimonyMeasure = trainingError + 0.1;
			else if(getSize() < 700)
				parsimonyMeasure = trainingError + 0.15;
			else if(getSize() < 750)
				parsimonyMeasure = trainingError + 0.2;
			else if(getSize() < 800)
				parsimonyMeasure = trainingError + 0.25;
			else if(getSize() < 850)
				parsimonyMeasure = trainingError + 0.3;
			else if(getSize() < 900)
				parsimonyMeasure = trainingError + 0.35;
			else if(getSize() < 950)
				parsimonyMeasure = trainingError + 0.4;
			else if(getSize() < 1000)
				parsimonyMeasure = trainingError + 0.45;
			else if(getSize() < 1100)
				parsimonyMeasure = trainingError + 0.5;
			else if(getSize() < 1200)
				parsimonyMeasure = trainingError + 0.55;
			else if(getSize() < 1300)
				parsimonyMeasure = trainingError + 0.6;
			else if(getSize() < 1400)
				parsimonyMeasure = trainingError + 0.65;
			else if(getSize() < 1500)
				parsimonyMeasure = trainingError + 0.7;
			else
				parsimonyMeasure = trainingError + 0.75;
			
			return parsimonyMeasure;
			
		} else {
//			if(getSize() < 2000)
//				parsimonyMeasure = trainingError;
//			else if(getSize() < 4000)
//				parsimonyMeasure = trainingError + 2;
//			else if(getSize() < 6000)
//				parsimonyMeasure = trainingError + 4;
//			else if(getSize() < 8000)
//				parsimonyMeasure = trainingError + 5;
//			else if(getSize() < 10000)
//				parsimonyMeasure = trainingError + 7;
//			else if(getSize() < 15000)
//				parsimonyMeasure = trainingError + 11;
//			else if(getSize() < 20000)
//				parsimonyMeasure = trainingError + 15;
//			else
//				parsimonyMeasure = trainingError + 20;
//			
			parsimonyMeasure = trainingError;
			return parsimonyMeasure;
		}
		
		
	}
	//incomplete
	/*
	public int getNumberOfDistinctVars() {
		String[] vars = new String[248];
		int count =0;
		for(int i=0; i<getSize();i++) {
			if( getProgramElementAtIndex(i).toString().contains("X") ) {
				for(int a=0; a<vars.length; a++) {
					if(getProgramElementAtIndex(i).toString() == vars[a])
						break;
				}
					vars[] = getProgramElementAtIndex(i).toString();
				}
				count+=1;
			} else {
				
			}
		}
		return count;
	}
	*/

	
	public int getNumberOfOperators() {
		int numberOperators = getSize() - getNumberOfVariables() ;
		return numberOperators;
	}
	public int getNumberOfConstants() {
		int numberOfContants=0;
		for(int i=0; i<getSize();i++) {
			if(getProgramElementAtIndex(i).toString().contains("C") ) {
				numberOfContants+=1;
			} else {
				
			}
		}
		return numberOfContants;
	}
	public boolean isVariable(int index) {
		if( getProgramElementAtIndex(index).toString().contains("X") ) {
			return true;
		} else
			return false;
	}
	public boolean isConstant(int index) {
		if( getProgramElementAtIndex(index).toString().contains("C") ) {
			return true;
		} else
			return false;
	}
	public boolean isLF(int index) {
		if( getProgramElementAtIndex(index).toString().contains("LF") ) {
			return true;
		} else
			return false;
	}
	public boolean isOperator(int index) {
		if(!(isLF(index) || isVariable(index) || isConstant(index))) {
			return true;
		} else
			return false;
	}
	//only for 4 basic operators
	public boolean isOperatorFaster(int index) {
		if(getProgramElementAtIndex(index).toString().contains("+") || getProgramElementAtIndex(index).toString().contains("-") ||
		getProgramElementAtIndex(index).toString().contains("*") || getProgramElementAtIndex(index).toString().contains("/")){
			return true;
		} else
			return false;
	}
	public boolean isConditional(int index) {
		if( getProgramElementAtIndex(index).toString().contains(">") || getProgramElementAtIndex(index).toString().contains("<")  ) {
			return true;
		} else
			return false;
	}
	
}