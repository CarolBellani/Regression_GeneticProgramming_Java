package algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import main.Individual;
import main.Population;
import programElements.Absolut;
import programElements.Addition;
import programElements.Average;
import programElements.Conditional;
import programElements.ConditionalSmaller;
import programElements.Constant;
import programElements.Cos;
import programElements.Iff;
import programElements.InputVariable;
import programElements.Inverse;
import programElements.Log;
import programElements.Log10;
import programElements.LogisticFunction;
import programElements.Max;
import programElements.Min;
import programElements.Multiplication;
import programElements.Negative;
import programElements.Operator;
import programElements.PowerTwo;
import programElements.Powers;
import programElements.ProgramElement;
import programElements.ProtectedDivision;
import programElements.Reminder;
import programElements.Sin;
import programElements.Subtraction;
import programElements.Tan;
import utils.Parameters;

public class Initializer {

	private Random r;
	//change to protected so we can get sets on other code
	protected ArrayList<ProgramElement> functionSet, terminalSet, fullSet, importVarSet, constantSet, varSet;
	protected ArrayList<ProgramElement> splittedSets1,splittedSets2,splittedSets3,splittedSets4 ;
	
	public int[] varsFitness = new int[277];

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Constructor: create function, terminal and full set
	//
	public Initializer(int dimensionality) {
		r = new Random();

		importVarsFitness();
		
		
		// create function set
		functionSet = new ArrayList<ProgramElement>();
		functionSet.add(new Addition());
		functionSet.add(new Subtraction());
		functionSet.add(new Multiplication());
		functionSet.add(new ProtectedDivision());
		functionSet.add(new Iff());
		functionSet.add(new LogisticFunction());
//		functionSet.add(new Inverse());
		
		
		if(Parameters.USE_ALL_OPERATORS) {
			functionSet.add(new Reminder());
			functionSet.add(new Conditional());
			functionSet.add(new Average());
			functionSet.add(new Negative());
			functionSet.add(new Max());
			functionSet.add(new Min());
			functionSet.add(new Absolut());
//			functionSet.add(new Cos());
//			functionSet.add(new Sin());
//			functionSet.add(new Tan());
			functionSet.add(new Log());
			functionSet.add(new Log10());
			functionSet.add(new PowerTwo());
//			functionSet.add(new Powers());
//			functionSet.add(new ConditionalSmaller());
			
//			functionSet.addAll(new Max());
		}
		
		
		
		
		// create terminal set
		terminalSet = new ArrayList<ProgramElement>();
		constantSet = new ArrayList<ProgramElement>();
		varSet = new ArrayList<ProgramElement>();
//		double[] constants = {-10,-7.5,-5,-3, -2.0, -1.5 -1.0, -0.75, -0.5, -0.25, 0.0, 0.25, 0.5, 0.75, 1.0 ,1.5,2.0,3,4,5,7.5,10};
		double[] constants = { -1.0, -0.75, -0.5, -0.25, 0.0, 0.25, 0.5, 0.75, 1.0};

		for (int i = 0; i < constants.length; i++)
			constantSet.add(new Constant(constants[i]));
		
		for (int i = 0; i < constants.length; i++)
			terminalSet.add(new Constant(constants[i]));
		
		// add input features
	
//		System.out.println(terminalSet.size());
		for (int i = 0; i < dimensionality; i++) 
			varSet.add(new InputVariable(i));
		/*********TEST************/
		//preselection of important vars manually
		
		importVarSet = new ArrayList<ProgramElement>();
		//int[] importantVars = { 246, 253, 205, 240, 247, 252, 249, 112, 90, 241, 248, 61, 263, 242, 57, 243, 34, 258, 9,12,44,65,58,268,29,251,118,256,56,28,121,265,207,271,30,50,267,204,257,261,250,45,64,206,32,73,123,18,70,262,115,266,69,11,7,59,276,52,255,49,272,91,72,27,273,68,274,71,17,5,2,20,15,92,22,33,10,245,254,270,25,16,51,275,75,24,13,83,3,63,23,126,260,116,244,76,78,19,14,55,8,120,269,6,259,21,4,264,26,209,53,0,98,48,170,
			//	1,31,35,36,37,38,39,40,41,42,43,46,47,54,60,62,66,67,74,77,79,80,81,82,84,85,86,87,88,89,93,94,95,96,97,99,100,101,102,103,104,105,106,107,108,109,110,111,113,114,117,119,122,124,125      };
		
		//IMPORTANT VARS ACCORDING TO HP FOREST, TOTAL 79 SELECTED
		int[] importantVars = { 37, 245, 240,246,251,51,79,103,74,84,0,269,201,247,252,105,21,276,34,5,7,92,69,76,13,239,261,26,273,274,33,48,58,59,6,70,8,107,207,255,270,30,3,50,52,54,62,116,129,139,205,
								241,242,25,272,56,60,10,128,12,17,190,19,206,208,209,23,253,258,260,265,266,268,27,29,32,45,47,57,85};
		
//		System.out.println("import  "+importantVars.length);
		
		/*
		for(int i=0;i<importantVars.length;i++) {
			 importVarSet.add(new InputVariable(importantVars[i]));
		} */
		if(Parameters.USE_IMPORTANT_X) {
			for(int i=0; i<importantVars.length; i++) {
				terminalSet.add(new InputVariable(importantVars[i]));
			}
		} else {
			
				for (int i = 0; i < dimensionality; i++) {
					if(i==1 || i==42  || i==43  || i==66  || i==79  || i==80  || i==81  || i==89  || i==94  || i==95  || i==96  || i==97  || i==99 
							|| i==107 || i==122 || i==137 || i==149 || i==143 || i==147 || i==148 || i==149 || i==151 || i==153 || i==155 || i==158 
							|| i==163 || i==165 || i==166 || i==167 || i==171 || i==172 || i==174 || i==175 || i==178 || i==180 || i==181 || i==182
							|| i==184 || i==185 || i==186 || i==187 || i==188 || i==195 || i==196 || i==197 || i==201 || i==212 || i==213 || i==215
							|| i==224 || i==227 || i==230 || i==233 || i==234 ) {
						continue;
					}
					terminalSet.add(new InputVariable(i));
					
				}
		}

		
		//4 splited sets of vars
		if(Parameters.USE_VARS_SPLITS) { 
			splittedSets1 = new ArrayList<ProgramElement>();
			splittedSets2 = new ArrayList<ProgramElement>();
			splittedSets3 = new ArrayList<ProgramElement>();
			splittedSets4 = new ArrayList<ProgramElement>();
			/*
			for(int i=0; i<dimensionality/4; i+=4) {
				splittedSets1.add(new InputVariable(i));
				splittedSets2.add(new InputVariable(i+1));
				splittedSets3.add(new InputVariable(i+2));
				splittedSets4.add(new InputVariable(i+3));
			}*/
			for(int i=0; i<terminalSet.size(); i++) {
				if(i < terminalSet.size() /4)
					splittedSets1.add(terminalSet.get(i));
				else if(i < terminalSet.size()/4 *2)
					splittedSets2.add(terminalSet.get(i));
				else if(i < terminalSet.size()/4 * 3)
					splittedSets3.add(terminalSet.get(i));
				else
					splittedSets4.add(terminalSet.get(i));
			}
//			
//		}
		
//			for(int i=0; i<dimensionality; i++) {
//			if(i < dimensionality/4)
//				splittedSets1.add(new InputVariable(i));
//			else if(i < dimensionality/4 *2)
//				splittedSets2.add(new InputVariable(i));
//			else if(i < dimensionality/4 * 3)
//				splittedSets3.add(new InputVariable(i));
//			else
//				splittedSets4.add(new InputVariable(i));
//		}
		
	}
		

		
		/*
		for(int i=0; i<dimensionality;i++) {
			for(int a=0; a<importantVars.length; a++) {
				if(i==importantVars[a])
			}
		}
		
		System.out.println(importantVars.length);
		
		importVarSet.add(new InputVariable(246));
		importVarSet.add(new InputVariable(0));
		importVarSet.add(new InputVariable(253));
		importVarSet.add(new InputVariable(205));
		importVarSet.add(new InputVariable(240));
		importVarSet.add(new InputVariable(247));
		importVarSet.add(new InputVariable(249));
		importVarSet.add(new InputVariable(252));
		importVarSet.add(new InputVariable(112));
		importVarSet.add(new InputVariable(90));
		importVarSet.add(new InputVariable(241));
		importVarSet.add(new InputVariable(248));
		
		
		*/
		
		
		
		
		//test until here

		// merge
		fullSet = new ArrayList<ProgramElement>();
		for (ProgramElement programElement : functionSet)
			fullSet.add(programElement);
		for (ProgramElement programElement : terminalSet)
			fullSet.add(programElement);
	}
	// --------------------------------------------------------------------

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Initialization Algorithms:
	
	public Population ilyaInitialize(int populationSize, int maxDepth) {
		int individualsPerDepth = populationSize / maxDepth;
		int remainingIndividuals = populationSize % maxDepth;
		Population populationMain = new Population();
		int fullIndividuals, growIndividuals;
		
		while(populationMain.getSize() < populationSize) {
			Population population = new Population();
			for (int depth = 1; depth <= maxDepth; depth++) {
				if (depth == maxDepth) {
					fullIndividuals = (int) Math.floor((individualsPerDepth + remainingIndividuals) / 2.0);
					growIndividuals = (int) Math.ceil((individualsPerDepth + remainingIndividuals) / 2.0);
				} else {
					fullIndividuals = (int) Math.floor(individualsPerDepth / 2.0);
					growIndividuals = (int) Math.ceil(individualsPerDepth / 2.0);
				}
	
				for (int i = 0; i < fullIndividuals; i++)
					population.addIndividual(full(depth));
	
				for (int i = 0; i < growIndividuals; i++)
					population.addIndividual(grow(depth));
				
			}  population.evaluate(GP.data);
			populationMain.addIndividual(population.getBest());
			
			
		}
		return populationMain;
	}
	public Population ilyaInitializeSplitted(int populationSize, int maxDepth, int setToUse) {
		int individualsPerDepth = populationSize / maxDepth;
		int remainingIndividuals = populationSize % maxDepth;
		Population populationMain = new Population();
		int fullIndividuals, growIndividuals;
		
		while(populationMain.getSize() < populationSize) {
			Population population = new Population();
			for (int depth = 1; depth <= maxDepth; depth++) {
				if (depth == maxDepth) {
					fullIndividuals = (int) Math.floor((individualsPerDepth + remainingIndividuals) / 2.0);
					growIndividuals = (int) Math.ceil((individualsPerDepth + remainingIndividuals) / 2.0);
				} else {
					fullIndividuals = (int) Math.floor(individualsPerDepth / 2.0);
					growIndividuals = (int) Math.ceil(individualsPerDepth / 2.0);
				}
	
				for (int i = 0; i < fullIndividuals; i++)
					population.addIndividual(fullSplitted(depth, setToUse));
	
				for (int i = 0; i < growIndividuals; i++)
					population.addIndividual(growSplitted(depth, setToUse));
				
			}  
			if(Parameters.USE_OBSV_SPLIT) {
				population.evaluateSplitted(GP.data, setToUse);
				
			} else {
				population.evaluate(GP.data);
			}				
			populationMain.addIndividual(population.getBest());
			
			
		}
		return populationMain;
	}
	//
	//
	public Population rampedHalfAndHalfInitialization(int populationSize, int maxDepth) {
		int individualsPerDepth = populationSize / maxDepth;
		int remainingIndividuals = populationSize % maxDepth;
		Population population = new Population();
		int fullIndividuals, growIndividuals;

		for (int depth = 1; depth <= maxDepth; depth++) {
			if (depth == maxDepth) {
				fullIndividuals = (int) Math.floor((individualsPerDepth + remainingIndividuals) / 2.0);
				growIndividuals = (int) Math.ceil((individualsPerDepth + remainingIndividuals) / 2.0);
			} else {
				fullIndividuals = (int) Math.floor(individualsPerDepth / 2.0);
				growIndividuals = (int) Math.ceil(individualsPerDepth / 2.0);
			}
			
			for (int i = 0; i < fullIndividuals; i++)
				population.addIndividual(full(depth));

			for (int i = 0; i < growIndividuals; i++)
				population.addIndividual(grow(depth));
		}
		return population;
	}
	public Population rampedHalfAndHalfInitializationSplitted(int populationSize, int maxDepth, int setToUse) {
		int individualsPerDepth = populationSize / maxDepth;
		int remainingIndividuals = populationSize % maxDepth;
		Population population = new Population();
		int fullIndividuals, growIndividuals;

		for (int depth = 1; depth <= maxDepth; depth++) {
			if (depth == maxDepth) {
				fullIndividuals = (int) Math.floor((individualsPerDepth + remainingIndividuals) / 2.0);
				growIndividuals = (int) Math.ceil((individualsPerDepth + remainingIndividuals) / 2.0);
			} else {
				fullIndividuals = (int) Math.floor(individualsPerDepth / 2.0);
				growIndividuals = (int) Math.ceil(individualsPerDepth / 2.0);
			}

			for (int i = 0; i < fullIndividuals; i++)
				population.addIndividual(fullSplitted(depth, setToUse));

			for (int i = 0; i < growIndividuals; i++)
				population.addIndividual(growSplitted(depth, setToUse));
		}
		return population;
	}
	public Population growPops(int populationSize, int maxDepth) {
		int individualsPerDepth = populationSize / maxDepth;
		int remainingIndividuals = populationSize % maxDepth;
		Population population = new Population();
		int growIndividuals;

		for (int depth = 1; depth <= maxDepth; depth++) {
			if (depth == maxDepth) {
				growIndividuals = (int) Math.ceil((individualsPerDepth + remainingIndividuals) );
			} else {
				growIndividuals = (int) Math.ceil(individualsPerDepth );
			}


			for (int i = 0; i < growIndividuals; i++)
				population.addIndividual(grow(depth));
		}
		return population;
	}
	
	public Individual full(int maximumTreeDepth) {
		Individual individual = new Individual();
		if(Parameters.USE_VAR_MEMORY)
			fullInnerRouletted(individual, 0, maximumTreeDepth);
		else
			fullInner(individual, 0, maximumTreeDepth);
		individual.setDepth(maximumTreeDepth);
		return individual;
	}
	public Individual fullSplitted(int maximumTreeDepth, int setToUse) {
		Individual individual = new Individual();
		fullInnerSplitted(individual, 0, maximumTreeDepth, setToUse);
		individual.setDepth(maximumTreeDepth);
		return individual;
	}

	private void fullInner(Individual individual, int currentDepth, int maximumTreeDepth) {
		if (currentDepth == maximumTreeDepth) {
			
				ProgramElement randomTerminal = terminalSet.get(r.nextInt(terminalSet.size()));
				individual.addProgramElement(randomTerminal);
				
			
		} else {
			Operator randomOperator = (Operator) functionSet.get(r.nextInt(functionSet.size()));
			individual.addProgramElement(randomOperator);
			for (int i = 0; i < randomOperator.getArity(); i++) {
				fullInner(individual, currentDepth + 1, maximumTreeDepth);
			}
		}
	}
	private void fullInnerRouletted(Individual individual, int currentDepth, int maximumTreeDepth) {
		if (currentDepth == maximumTreeDepth) {
			if(Parameters.USE_IMPORTANT_X) {
				ProgramElement randomTerminal = importVarSet.get(r.nextInt(importVarSet.size()));
				individual.addProgramElement(randomTerminal);
			}else {
				ProgramElement randomTerminal = varSet.get(rouletteVarFitness());
				individual.addProgramElement(randomTerminal);
				
			}
		} else {
			Operator randomOperator = (Operator) functionSet.get(r.nextInt(functionSet.size()));
			individual.addProgramElement(randomOperator);
			for (int i = 0; i < randomOperator.getArity(); i++) {
				fullInner(individual, currentDepth + 1, maximumTreeDepth);
			}
		}
	}
	private void fullInnerSplitted(Individual individual, int currentDepth, int maximumTreeDepth, int setToUse) {
		ProgramElement randomTerminal;
		if (currentDepth == maximumTreeDepth) {
			switch(setToUse) {
				case 1:
						 randomTerminal = splittedSets1.get(r.nextInt(splittedSets1.size()));
						individual.addProgramElement(randomTerminal);
						break;
				case 2:
					 randomTerminal = splittedSets2.get(r.nextInt(splittedSets2.size()));
					individual.addProgramElement(randomTerminal);
					break;
				case 3:
					 randomTerminal = splittedSets3.get(r.nextInt(splittedSets3.size()));
					individual.addProgramElement(randomTerminal);
					break;
				case 4:
					 randomTerminal = splittedSets4.get(r.nextInt(splittedSets4.size()));
					individual.addProgramElement(randomTerminal);
					break;
				default:
					System.out.println("set doesnt exist");
		}

		} else {
			Operator randomOperator = (Operator) functionSet.get(r.nextInt(functionSet.size()));
			individual.addProgramElement(randomOperator);
			for (int i = 0; i < randomOperator.getArity(); i++) {
				fullInnerSplitted(individual, currentDepth + 1, maximumTreeDepth, setToUse);
			}
		}
	}

	public Individual grow(int maximumTreeDepth) {
		Individual individual = new Individual();
		if(Parameters.USE_VAR_MEMORY)
			growInnerRouletted(individual, 0, maximumTreeDepth);
		else
			growInner(individual, 0, maximumTreeDepth);
		individual.calculateDepth();
		return individual;
	}
	public Individual growSplitted(int maximumTreeDepth, int setToUse) {
		Individual individual = new Individual();
		growInnerSplitted(individual, 0, maximumTreeDepth, setToUse);
		individual.calculateDepth();
		return individual;
	}
	private void growInner(Individual individual, int currentDepth, int maximumTreeDepth) {
		if (currentDepth == maximumTreeDepth) {
			ProgramElement randomTerminal = terminalSet.get(r.nextInt(terminalSet.size()));
			individual.addProgramElement(randomTerminal);
		} else {
			if (r.nextBoolean()) {
				Operator randomOperator = (Operator) functionSet.get(r.nextInt(functionSet.size()));
				individual.addProgramElement(randomOperator);
				for (int i = 0; i < randomOperator.getArity(); i++) {
					growInner(individual, currentDepth + 1, maximumTreeDepth);
				}
			} else {
				
					ProgramElement randomTerminal = terminalSet.get(r.nextInt(terminalSet.size()));
					individual.addProgramElement(randomTerminal);
					
				
			}
		}
	}
	private void growInnerRouletted(Individual individual, int currentDepth, int maximumTreeDepth) {
		if (currentDepth == maximumTreeDepth) {
			ProgramElement randomTerminal = terminalSet.get(r.nextInt(terminalSet.size()));
			individual.addProgramElement(randomTerminal);
		} else {
			if (r.nextBoolean()) {
				Operator randomOperator = (Operator) functionSet.get(r.nextInt(functionSet.size()));
				individual.addProgramElement(randomOperator);
				for (int i = 0; i < randomOperator.getArity(); i++) {
					growInner(individual, currentDepth + 1, maximumTreeDepth);
				}
			} else {
				if(Parameters.USE_IMPORTANT_X) {
					ProgramElement randomTerminal = importVarSet.get(r.nextInt(importVarSet.size()));
					individual.addProgramElement(randomTerminal);
				} else {
					ProgramElement randomTerminal = varSet.get(rouletteVarFitness());
					individual.addProgramElement(randomTerminal);
					
				}
			}
		}
	}
	private void growInnerSplitted(Individual individual, int currentDepth, int maximumTreeDepth, int setToUse) {
		ProgramElement randomTerminal;
		if (currentDepth == maximumTreeDepth) {
			switch(setToUse) {
				case 1:
						 randomTerminal = splittedSets1.get(r.nextInt(splittedSets1.size()));
						individual.addProgramElement(randomTerminal);
						break;
				case 2:
					 randomTerminal = splittedSets2.get(r.nextInt(splittedSets2.size()));
					individual.addProgramElement(randomTerminal);
					break;
				case 3:
					 randomTerminal = splittedSets3.get(r.nextInt(splittedSets3.size()));
					individual.addProgramElement(randomTerminal);
					break;
				case 4:
					 randomTerminal = splittedSets4.get(r.nextInt(splittedSets4.size()));
					individual.addProgramElement(randomTerminal);
					break;
				default:
					System.out.println("set doesnt exist");
			}
		} else {
			if (r.nextBoolean()) {
				Operator randomOperator = (Operator) functionSet.get(r.nextInt(functionSet.size()));
				individual.addProgramElement(randomOperator);
				for (int i = 0; i < randomOperator.getArity(); i++) {
					growInner(individual, currentDepth + 1, maximumTreeDepth);
				}
			} else {
				switch(setToUse) {
					case 1:
							randomTerminal = splittedSets1.get(r.nextInt(splittedSets1.size()));
							individual.addProgramElement(randomTerminal);
							break;
					case 2:
						 randomTerminal = splittedSets2.get(r.nextInt(splittedSets2.size()));
						individual.addProgramElement(randomTerminal);
						break;
					case 3:
						 randomTerminal = splittedSets3.get(r.nextInt(splittedSets3.size()));
						individual.addProgramElement(randomTerminal);
						break;
					case 4:
						 randomTerminal = splittedSets4.get(r.nextInt(splittedSets4.size()));
						individual.addProgramElement(randomTerminal);
						break;
					default:
						System.out.println("set doesnt exist");
					}
			}
		}
	}
	//if true is for var, if false is for operator
	protected int rouletteVarFitness() {
		double rand = r.nextDouble();
		float acumulatedFit=0;
		int i; long totala = totalVarFitness();
		for (i=0; i<varsFitness.length; i++) {
			double a = varsFitness[i] / totala;
//			System.out.println(varsFitness[i]+"  "+varsFitness[i] / totala+"   "+totala+" "+a);
			acumulatedFit += (varsFitness[i]* 100 / totala* 100) * 100;
			if(acumulatedFit >= rand)
				break;
		} 
//		System.out.println("acum "+acumulatedFit+" r "+rand+" last "+varsFitness[varsFitness.length-1]);
		return i;
	}
	
	protected long totalVarFitness() {
		long total = 0;

			for(int i=0; i< varsFitness.length-4; i++) {
				total+=varsFitness[i];
			}

		
		return total;
	}
	
	public void importVarsFitness() {
	//	Scanner inFile1 = new Scanner(new File("output\\varsFitness.txt"));
        String fileName= "output\\varsFitness.txt";
        File file= new File(fileName);

        // this gives you a 2-dimensional array of strings
        Scanner inputStream;
       String[] values = new String[282];
        try{
            inputStream = new Scanner(file);

            while(inputStream.hasNext()){
                String line= inputStream.next();
                 values = line.split(",");
                // this adds the currently parsed line to the 2-dimensional string array
                
            }
            inputStream.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        
        for(int i=0; i<values.length; i++) {
        	varsFitness[i] = Integer.parseInt(values[i]) ;
        			
        }
//        System.out.println(Arrays.toString(varsFitness));
	}
}
