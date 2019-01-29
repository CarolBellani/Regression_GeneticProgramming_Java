package algorithms;

import java.awt.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import main.Individual;
import main.Main;
import main.Population;
import programElements.Addition;
import programElements.InputVariable;
import programElements.Multiplication;
import programElements.Operator;
import programElements.ProtectedDivision;
import programElements.Subtraction;
//import project.Statistics;
import utils.Data;
import utils.Parameters;


public class GP implements Serializable {
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Parameters
	//
	private static final long serialVersionUID = 7L;
	protected int currentGen;
	protected Random r;
	protected static Data data;
	protected Initializer initializer;
	protected Individual currentBest, currentBestSSGP;
	protected Individual currentBestUnseen, currentBestUnseenSSGP;
	protected Population population;
	protected double bloat, avgFit0, avgSize0, overfitting, btp, tbtp, overfittingPaper, btpPaper, tbtpPaper;
	
	
	//Variables used for Pararel Populations
	//if PararellPopsN is set to 0 will save memory when not used
	Population[] pops = new Population[Parameters.PararellPopsN]; 
	Individual[] popsCurrentBest = new Individual[Parameters.PararellPopsN];
	Individual[] popsCurrentBestUnseen = new Individual[Parameters.PararellPopsN];
	double [] popsBloat = new double[Parameters.PararellPopsN];
	double [] popsOverfit = new double[Parameters.PararellPopsN];
	double [] popsAvgFit0 = new double[Parameters.PararellPopsN];
	double [] popsAvgSize0 = new double[Parameters.PararellPopsN];
	double [] popsBtp = new double[Parameters.PararellPopsN];
	double [] popsTbtp = new double[Parameters.PararellPopsN];

	Individual absolutBest = new Individual();
	
	protected Population steadyStateCentral;

	public GP(Data data) {
		this.data = data;
		r = new Random();
		
		
		
		
		if(Parameters.PararellPops) {
			//Declare variables and arrays used only in pararell to save memory when not used
			
			initializer = new Initializer(data.getDimensionality());
			
			//for(int i=0;i<Parameters.PararellPopsN;i++) {
			for(int i=0;i<Parameters.PararellPopsN;i++) {	
				if(Parameters.USE_VARS_SPLITS) {
//					pops[i] = initializer.rampedHalfAndHalfInitializationSplitted(Parameters.EA_PSIZE, Parameters.IN_DEPTH_LIM, i+1);
					pops[i] = initializer.ilyaInitializeSplitted(Parameters.EA_PSIZE, Parameters.IN_DEPTH_LIM, (i%4)+1);
//					pops[i] = initializer.growPops(Parameters.EA_PSIZE, Parameters.IN_DEPTH_LIM);
				} else {
//					pops[i] = initializer.rampedHalfAndHalfInitialization(Parameters.EA_PSIZE, Parameters.IN_DEPTH_LIM);
					pops[i] = initializer.ilyaInitialize(Parameters.EA_PSIZE, Parameters.IN_DEPTH_LIM);
					
				}
				
				if(Parameters.USE_OBSV_SPLIT) {
//					pops[i].evaluateSplitted(data, i%4);
					pops[i].evaluateSplitted(data, i);
				} else
					pops[i].evaluate(data);
				
				popsBloat[i] = popsOverfit[i] = 0;
				popsAvgFit0[i] = pops[i].getAVGFitness();
				popsAvgSize0[i] = pops[i].getAVGSize();
				updateCurrentBestPararel(i);
				updateCurrentBestPararelUnseen(i);
				popsBtp = popsCurrentBest[i].getUnseenOutputs();
				popsTbtp = popsCurrentBest[i].getTrainingOutputs();
				
				absolutBest = popsCurrentBest[i];
				
				
//				updateAbsolutBest();
				
				printStatePararell(i);
				currentGen = 1;
				//missing addValue() which writes results to txt file, not needed for now
			}
			
			
			
		} else {
			initializer = new Initializer(data.getDimensionality());
//			population = initializer.rampedHalfAndHalfInitialization(Parameters.EA_PSIZE, Parameters.IN_DEPTH_LIM);
			if(Parameters.USE_VARS_SPLITS) {
//				pops[i] = initializer.rampedHalfAndHalfInitializationSplitted(Parameters.EA_PSIZE, Parameters.IN_DEPTH_LIM, i+1);
				population = initializer.ilyaInitializeSplitted(Parameters.EA_PSIZE, Parameters.IN_DEPTH_LIM, r.nextInt(4));
//				pops[i] = initializer.growPops(Parameters.EA_PSIZE, Parameters.IN_DEPTH_LIM);
			} else {
//				pops[i] = initializer.rampedHalfAndHalfInitialization(Parameters.EA_PSIZE, Parameters.IN_DEPTH_LIM);
				population = initializer.ilyaInitialize(Parameters.EA_PSIZE, Parameters.IN_DEPTH_LIM);
				
			}
			population.evaluate(data);
			
			bloat = overfitting = 0;
			avgFit0 = population.getAVGFitness();
			avgSize0 = population.getAVGSize();
			updateCurrentBest();
			btp = currentBest.getUnseenError();
			tbtp = currentBest.getTrainingError();

			btpPaper = currentBest.getUnseenError();
			tbtpPaper = currentBest.getTrainingError();
			
			
			printState();
			currentGen = 1;
			addValue();
			absolutBest = currentBest;
			}
		
		
			if(Parameters.USE_SSGP_CENTRALIZED) {
				 steadyStateCentral = new Population();
				steadyStateCentral = initializer.ilyaInitialize(Parameters.EA_PSIZE, Parameters.IN_DEPTH_LIM);
				steadyStateCentral.evaluate(data);
				
				
				
				updateCurrentBestSSGP();
				updateCurrentBestUnseenSSGP();
			}
			
		
		}



	// --------------------------------------------------------------------

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Evolution
	//
	public void search(int numberOfGen) {	
		if(Parameters.PararellPops) {
			for (; currentGen <= numberOfGen; currentGen++) {
				Population[] offsprings = new Population[Parameters.PararellPopsN];
				
				
				
				
				
				//change during run parameters
//				Parameters.MUT_TYPE=1;
				
				/*
				if(currentGen%3==0) {
					Parameters.MUT_TYPE=7;
				}
				*/
				
				
				for(int a=0;a<Parameters.PararellPopsN;a++) {
					offsprings[a] = new Population(); 
					
					if(Parameters.GP_APPLY)
						parameterChangePops(a);
					
//					Parameters.XO_TYPE = 1;
//					if(a==0)
//						Parameters.XO_TYPE = 3;
//						System.out.println("pre  pop size "+ pops[a].getSize()+" offspring size "+offsprings[a].getSize()+"  "+a);
					while (offsprings[a].getSize() < pops[a].getSize()) {
//						System.out.println(pops[a].getSize()+" of "+offsprings[a].getSize());
						Individual p1, newIndividual;
						p1 = selectionMenu(Parameters.SELECTION_TYPE, pops[a]);
						newIndividual = p1;
						// apply crossover or mutation
						if(Parameters.XOMUT) {
							newIndividual = p1; // need to initialize, if not it may not be initialized due to probs
							if (r.nextDouble() < Parameters.VAR_XOVER_PROB) {
								Individual p2 = selectionMenu(Parameters.SELECTION_TYPE, pops[a]);
								newIndividual = crossoverMenu(p1, p2, Parameters.XO_TYPE, pops[a]);
							} 
							if(r.nextDouble() < Parameters.XOMUT_PROB) {
								newIndividual = mutationMenu(p1, Parameters.MUT_TYPE);
							}
							
						}
						else {
							if (r.nextDouble() < Parameters.VAR_XOVER_PROB) {
								Individual p2 = selectionMenu(Parameters.SELECTION_TYPE, pops[a]);
								newIndividual = crossoverMenu(p1, p2, Parameters.XO_TYPE, pops[a]);
							} else {
								newIndividual = mutationMenu(p1, Parameters.MUT_TYPE);
							}
						}
						if (Parameters.VAR_APPLY_DEPTH_LIM && newIndividual.getDepth() > Parameters.VAR_DEPTH_LIM)
							newIndividual = p1;
						else {
							if(Parameters.USE_OBSV_SPLIT)
								newIndividual.evaluateSplitted(data, a%4);
							else
								newIndividual.evaluate(data);
						}
						
//						System.out.println("1  pop size "+ pops[a].getSize()+" offspring size "+offsprings[a].getSize()+"  "+a);
						offsprings[a].addIndividual(newIndividual);
//						System.out.println("2  pop size "+ pops[a].getSize()+" offspring size "+offsprings[a].getSize()+"  "+a);
//						System.out.println("error "+ offsprings[a].getBest().getTrainingError());
						
						
					} 
					
					
//					System.out.println("error "+ offsprings[a].getBest().getTrainingError());
//					System.out.println("3  pop size "+ pops[a].getSize()+" offspring size "+offsprings[a].getSize()+"  "+a);
					pops[a] = replacementPararel(pops[a], offsprings[a]);
//					System.out.println("4  pop size "+ pops[a].getSize()+" offspring size "+offsprings[a].getSize()+"  "+a);
//					System.out.println(pops[a].getSize()+" of "+offsprings[a].getSize());
					pops[a] = Elitism(pops[a], offsprings[a], Parameters.N_ELITES);
					
					
					updateCurrentBestPararel(a);
					updateCurrentBestPararelUnseen(a);
					
					computeBloatPararel(a);
					computeOverfittingPararel(a);;
					printStatePararell(a);
					//addValue();
					setAbsoluteBestPararelSSGP();
					setAbsoluteBestPararelUnseen();
					System.out.println("5  pop size "+ pops[a].getSize()+" offspring size "+offsprings[a].getSize());
					
					if(Parameters.GP_APPLY && currentGen%50==0) {
						computeVarsFitness();
						exportVarsFitness();
					}
						
					
					
					
					
				}
				
				
				//every X generations swap best elements
				if(Parameters.PararellPops || currentGen%Parameters.PararellSwap==0 ) {
					//swapPararel(Parameters.TrainUnseen);
					if(Parameters.USE_SSGP_CENTRALIZED) {
						if(Parameters.GP_APPLY)
							parameterChangePops(Parameters.PararellPopsN + 1);
				
				int ss = 0;
				while ( ss < steadyStateCentral.getSize()) { // same 
					ss++;
					Individual p1, newIndividual;
					//p1 = tournamentSelection();
					p1 = selectionMenu(Parameters.SELECTION_TYPE, steadyStateCentral);
//					System.out.println("Start");
					// apply crossover or mutation
					if(Parameters.XOMUT) {
						newIndividual = p1;// need to initialize, if not it may not be initialized due to probs
						if (r.nextDouble() < Parameters.VAR_XOVER_PROB) {
							Individual p2 = selectionMenu(Parameters.SELECTION_TYPE, steadyStateCentral);
							newIndividual = crossoverMenu(p1, p2, Parameters.XO_TYPE, steadyStateCentral);
						} 
						if(r.nextDouble() < Parameters.XOMUT_PROB) {
							newIndividual = mutationMenu(p1, Parameters.MUT_TYPE);
						}
						
					}
					else {
						if (r.nextDouble() < Parameters.VAR_XOVER_PROB) {
							Individual p2 = selectionMenu(Parameters.SELECTION_TYPE, steadyStateCentral);
							newIndividual = crossoverMenu(p1, p2, Parameters.XO_TYPE, steadyStateCentral);
						} else {
							newIndividual = mutationMenu(p1, Parameters.MUT_TYPE);
						}
					}
					if (Parameters.VAR_APPLY_DEPTH_LIM && newIndividual.getDepth() > Parameters.VAR_DEPTH_LIM)
						newIndividual = p1;
					else {
//						System.out.println("before eval");
//						System.out.println(newIndividual.getDepth()+"  "+newIndividual.getSize());
						newIndividual.evaluate(data);
//						System.out.println("after eval");
					}

//					System.out.println("end");
					steadyStateCentral.steadyStateSwap(newIndividual);
				}
				sendToCentral();
				updateCurrentBestSSGP();
				updateCurrentBestUnseenSSGP();
				
				setAbsoluteBestPararelSSGP();
				setAbsoluteBestUnseenPararelSSGP();
				
				
				printStateSSGP();
				
				printState(); //global
				
					}else {
						swapPararel1();
					}
				}
				
				
				updateAbsolutBest();
				printStateAbsolut();
				
//				System.out.println();
//				System.out.println(currentBest.getOutputTargetAvg());
//				System.out.println(currentBest.getOutputTargetAvgIndividual());
				
				
				if(currentGen%Parameters.PRINT_N==0)
					Stats_Report();
				
				
//				System.out.println("test "+ (Math.pow(-5, 0.1)));
				
			} //end of gens
			
			
			
			
		} else {
			for (; currentGen <= numberOfGen; currentGen++) {
				Population offspring = new Population();
				while (offspring.getSize() < population.getSize()) {
					Individual p1, newIndividual;
					//p1 = tournamentSelection();
					p1 = selectionMenu(Parameters.SELECTION_TYPE, population);
					
					// apply crossover or mutation
					if(Parameters.XOMUT) {
						newIndividual = p1;// need to initialize, if not it may not be initialized due to probs
						if (r.nextDouble() < Parameters.VAR_XOVER_PROB) {
							Individual p2 = selectionMenu(Parameters.SELECTION_TYPE, population);
							newIndividual = crossoverMenu(p1, p2, Parameters.XO_TYPE, population);
						} 
						if(r.nextDouble() < Parameters.XOMUT_PROB) {
							newIndividual = mutationMenu(p1, Parameters.MUT_TYPE);
						}
						
					}
					else {
						if (r.nextDouble() < Parameters.VAR_XOVER_PROB) {
							Individual p2 = selectionMenu(Parameters.SELECTION_TYPE,  population);
							newIndividual = crossoverMenu(p1, p2, Parameters.XO_TYPE,population);
						} else {
							newIndividual = mutationMenu(p1, Parameters.MUT_TYPE);
						}
					}
					if (Parameters.VAR_APPLY_DEPTH_LIM && newIndividual.getDepth() > Parameters.VAR_DEPTH_LIM)
						newIndividual = p1;
					else
						newIndividual.evaluate(data);

					offspring.addIndividual(newIndividual);
				}
				population = replacement(offspring);
				population = Elitism(population, offspring, Parameters.N_ELITES);
				
				
				updateCurrentBest();
				updateCurrentBestUnseen();
				
				computeBloat();
				computeOverfitting();
				printState();
				addValue();

				
				if(currentBest.getUnseenError() < absolutBest.getUnseenError())
					absolutBest = currentBest;
				
				if(currentGen%Parameters.PRINT_N==0)
					Stats_Report();
				/******   TEST RANDOM STUFF HERE    **************/
				/*
				System.out.println("BEST TRAIN training err "+population.getBest().getTrainingError());
				System.out.println("unseen err "+population.getBest().getUnseenError());
				System.out.println("BEST UNSEEN training err "+population.getBestUnseen().getTrainingError());
				System.out.println("unseen err "+population.getBestUnseen().getUnseenError());
				*/
				

			}
			
		}
		

		
		
		
	}
	// --------------------------------------------------------------------
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Selection
	//
	protected Individual selectionMenu(int selection_N, Population pop) {
		Individual newIndividual= new Individual();
		if(Parameters.PararellPops) {
			
			switch(selection_N) {
			case 1:	newIndividual = tournamentSelectionPOP(pop);
				break;
			case 2: newIndividual = rouletteWhell(pop);
				break;
			case 3: newIndividual = rankingBucketSelection(pop);
				break;
			default: System.out.println("No selection type selected");
			}
			return newIndividual;
			
		} else {
			switch(selection_N) {
				case 1:	newIndividual = tournamentSelection();
					break;
				case 2: newIndividual = rouletteWheelSelection(population);
					break;
				default: System.out.println("No selection type selected");
			}
			return newIndividual;
			
		}
	}
	protected Individual tournamentSelection() {
		Population tournamentPopulation = new Population();
		//int tournamentSize = (int) (Parameters.VAR_TOUR_PR * population.getSize());//
		int tournamentSize = (int) (Parameters.VAR_TOUR_PR); //same but not multiplying by pop
		for (int i = 0; i < tournamentSize; i++)
			tournamentPopulation.addIndividual(population.getIndividual(r.nextInt(population.getSize())));
		if(Parameters.USE_PARSIMONY)
			return tournamentPopulation.getBestParsimony();
		else
			return tournamentPopulation.getBest();
	}
	protected Individual tournamentSelectionPararel(int popindex) {
		Population tournamentPopulation = new Population();
		//int tournamentSize = (int) (Parameters.VAR_TOUR_PR * pops[popindex].getSize());
		int tournamentSize = (int) (Parameters.VAR_TOUR_PR); //same but not multiplying by pop
		for (int i = 0; i < tournamentSize; i++)
			tournamentPopulation.addIndividual(pops[popindex].getIndividual(r.nextInt(pops[popindex].getSize())));
		if(Parameters.USE_PARSIMONY)
			return tournamentPopulation.getBestParsimony();
		else
			return tournamentPopulation.getBest();
	}
	//adapted for ssgp
	protected Individual tournamentSelectionSSGP() {
		Population tournamentPopulation = new Population();
		//int tournamentSize = (int) (Parameters.VAR_TOUR_PR * pops[popindex].getSize());
		int tournamentSize = (int) (Parameters.VAR_TOUR_PR); //same but not multiplying by pop
		for (int i = 0; i < tournamentSize; i++)
			tournamentPopulation.addIndividual(steadyStateCentral.getIndividual(r.nextInt(steadyStateCentral.getSize())));
		if(Parameters.USE_PARSIMONY)
			return tournamentPopulation.getBestParsimony();
		else
			return tournamentPopulation.getBest();
	}
	//multi pop tournament selection, should have done it at the start :/
	protected Individual tournamentSelectionPOP(Population pop) {
		Population tournamentPopulation = new Population();
		//int tournamentSize = (int) (Parameters.VAR_TOUR_PR * pops[popindex].getSize());
		int tournamentSize = (int) (Parameters.VAR_TOUR_PR); //same but not multiplying by pop
		for (int i = 0; i < tournamentSize; i++)
			tournamentPopulation.addIndividual(pop.getIndividual(r.nextInt(pop.getSize())));
		if(Parameters.USE_PARSIMONY)
			return tournamentPopulation.getBestParsimony();
		else
			return tournamentPopulation.getBest();
	}

	
	//hope not errors because of some functions' domain
	protected Individual rouletteWheelSelection(Population pop) {
		double rand = 0 + (totalParesomonyErrorInverted(pop)) * r.nextDouble(), acumulatedFit =0;
		
		int i;
		if(Parameters.USE_PARSIMONY) {
			
			
			
			for( i=0; i<pop.getSize();i++) {
				acumulatedFit += pop.getIndividual(pop.getWorstIndexParsimony()).getParsimonyMeasure() - pop.getIndividual(i).getParsimonyMeasure();
//				System.out.println(acumulatedFit);
			
			
		
//				acumulatedFit += pop.getIndividual(i).getParsimonyMeasure() / totalFit ;
				
//			System.out.println(population.getIndividual(i).getTrainingError() );
				if(acumulatedFit >= rand)
					break;
			}

		} else {
			double totalFit = totalTrainErrorInverted(pop);
			for(i=0; i<pop.getSize(); i++) {
				acumulatedFit += (pop.getIndividual(pop.getWorstIndex()).getTrainingError() - pop.getIndividual(i).getTrainingError() )/ totalFit ;
//			System.out.println(population.getIndividual(i).getTrainingError() );
//				System.out.println(acumulatedFit +"    "+i+ "   "+rand);
//				
//				System.out.println(pop.getIndividual(pop.getWorstIndex()).getTrainingError() +"   "+pop.getIndividual(i).getTrainingError());
				if(acumulatedFit >= rand)
					break;
			}
		}
//		System.out.println("acum "+acumulatedFit+" rand "+rand+" total "+totalFit);
//	System.out.println("index "+i+" size "+Parameters.EA_PSIZE);
//		System.out.println(acumulatedFit+"   "+i);
		return pop.getIndividual(i);
	}
	protected Individual rouletteWhell(Population pop) {

		double sumFitness=0, p=r.nextDouble(), sum = 0, invertSum = 0; 
		int i, parentIndex= 0;

		if(Parameters.USE_PARSIMONY) {
			for(int j=0;j<pop.getSize();j++)
				sumFitness+=pop.getIndividual(j).getParsimonyMeasure();

			for (i = 0; i < pop.getSize(); i++)
				invertSum +=  1-(pop.getIndividual(i).getParsimonyMeasure()/sumFitness);

			for (i = 0; i < pop.getSize(); i++) {
				sum += (1-(pop.getIndividual(i).getParsimonyMeasure()/sumFitness))/invertSum;

				if (sum >= p) {
					parentIndex=i;
					break;
				}
			}

			return pop.getIndividual(parentIndex);
		} else {
		for(int j=0;j<pop.getSize();j++)
			sumFitness+=pop.getIndividual(j).getTrainingError();

		for (i = 0; i < pop.getSize(); i++)
			invertSum +=  1-(pop.getIndividual(i).getTrainingError()/sumFitness);

		for (i = 0; i < pop.getSize(); i++) {
			sum += (1-(pop.getIndividual(i).getTrainingError()/sumFitness))/invertSum;

			if (sum >= p) {
				parentIndex=i;
				break;
			}
		}

		return pop.getIndividual(parentIndex);
		}
	}
	// places individuals in buckets by fitness. After in the best bucket we get the smallest one
	protected Individual rankingBucketSelection(Population pop) {
		pop.assignBuckets();
		Individual[] bestBucket = pop.getFirstBucket();
		Individual best = bestBucket[0];
		for(int i=0; i<bestBucket.length; i++) {
			if(best.getSize() > bestBucket[i].getSize())
				best = bestBucket[i];
//			System.out.println(" i  "+i +" size "+best.getSize());
//			System.out.println(bestBucket.length);
		}
		return best;
		
	}
	protected double totalTrainErrorInverted(Population pop) {
		double trainErr = 0;
		for(int i=0; i<pop.getSize();i++) {
			trainErr+= pop.getIndividual(pop.getWorstIndex()).getTrainingError() - pop.getIndividual(i).getTrainingError();
		} return trainErr;
	}
	protected double totalParesomonyErrorInverted(Population pop) {
		double trainErr = 0;
		for(int i=0; i<Parameters.EA_PSIZE;i++) {
			trainErr+=  pop.getIndividual(pop.getWorstIndex()).getParsimonyMeasure() - pop.getIndividual(i).getParsimonyMeasure();
		} return trainErr;
	}
	// --------------------------------------------------------------------

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Variation
	//
	//menu for XOs. Add new XO here and determine XO type in parameters
	protected Individual crossoverMenu(Individual p1, Individual p2, int XO_N, Population pop) {
		
		Individual newIndividual= new Individual();
		switch(XO_N) {
			case 1:	newIndividual = crossover(p1, p2);
				break;
			case 2: newIndividual = hoist(p1);
				break;
			case 3: newIndividual = crossoverHoisted(p1, p2);
				break;
			case 4: newIndividual = crossoverHighLow(p1, p2, pop);
				break;
			case 5: newIndividual = crossoverSizeFair(p1, p2);
				break;
			default: System.out.println("No XO type selected");
		}
		return newIndividual;
	}
	protected Individual crossover(Individual p1, Individual p2) {
		int p1CrossoverStart = r.nextInt(p1.getSize());
		int p1ElementsToEnd = p1.countElementsToEnd(p1CrossoverStart);
		int p2CrossoverStart = r.nextInt(p2.getSize());
		int p2ElementsToEnd = p2.countElementsToEnd(p2CrossoverStart);

		Individual offspring = p1.selectiveDeepCopy(p1CrossoverStart, p1CrossoverStart + p1ElementsToEnd - 1);

		for (int i = 0; i < p2ElementsToEnd; i++)
			offspring.addProgramElementAtIndex(p2.getProgramElementAtIndex(p2CrossoverStart + i), p1CrossoverStart + i);

		offspring.calculateDepth();
		return offspring;
	}
	//special type of XO according to theory
	protected Individual hoist(Individual p1) {
		int hoistPoint = r.nextInt(p1.getSize()) ;  //so it doesn't generate a 0 element subtree
		//int parentElementsToEnd = p.countElementsToEnd(mutationPoint);
		Individual offspring = new Individual();
		
		for(int i=hoistPoint; i<p1.getSize();i++) {
			offspring.addProgramElement(p1.getProgramElementAtIndex(i));
		}
		//checkStructure(offspring);
		offspring.calculateDepth();
		return offspring;
	}
	protected Individual crossoverHoisted(Individual p1, Individual p2) {
		p1 = hoist(p1);
		p2 = hoist(p2);
		Individual offspring = new Individual();
		/*    add a operator between them, may not be needed
		int operator =  r.nextInt(4)+1;
		
		switch(operator) {
			case 1:offspring.addProgramElement(new Addition());
				break;
			case 2:offspring.addProgramElement(new Subtraction());
				break;
			case 3:offspring.addProgramElement(new Multiplication());
				break;
			case 4:offspring.addProgramElement(new ProtectedDivision());
				break;
				default: System.out.println("operator not found");
		} */
		for(int i=0; i<p1.getSize();i++) {
			offspring.addProgramElement(p1.getProgramElementAtIndex(i));
		}
		for(int i=0; i<p2.getSize();i++) {
			offspring.addProgramElement(p2.getProgramElementAtIndex(i));
		}
		return offspring;
	}
	protected Individual crossoverHighLow(Individual p1, Individual p2, Population pop) {
		
		for(int i=0; i<100; i++) {
			
			if(p1.highLow() ^ p2.highLow() )
				break;
			p2 = tournamentSelectionPOP(pop);
		}
		
		int p1CrossoverStart = r.nextInt(p1.getSize());
		int p1ElementsToEnd = p1.countElementsToEnd(p1CrossoverStart);
		int p2CrossoverStart = r.nextInt(p2.getSize());
		int p2ElementsToEnd = p2.countElementsToEnd(p2CrossoverStart);

		Individual offspring = p1.selectiveDeepCopy(p1CrossoverStart, p1CrossoverStart + p1ElementsToEnd - 1);

		for (int i = 0; i < p2ElementsToEnd; i++)
			offspring.addProgramElementAtIndex(p2.getProgramElementAtIndex(p2CrossoverStart + i), p1CrossoverStart + i);

		offspring.calculateDepth();
		return offspring;
		
	}
	protected Individual crossoverSizeFair(Individual p1, Individual p2) {
		int p1CrossoverStart = r.nextInt(p1.getSize()) + p1.getSize()/10; //to stop infinity loop
		int p1ElementsToEnd = p1.countElementsToEnd(p1CrossoverStart);
		int p2CrossoverStart = r.nextInt(p2.getSize());
		int p2ElementsToEnd = p2.countElementsToEnd(p2CrossoverStart);
		
		Individual offspring = p1.selectiveDeepCopy(0, p1CrossoverStart);

		while (p2ElementsToEnd*2 +1 > offspring.getSize()) {
			p2CrossoverStart = r.nextInt(p2.getSize());
			p2ElementsToEnd = p2.countElementsToEnd(p2CrossoverStart);
		}
		for (int i = 0; i < p2ElementsToEnd; i++)
			offspring.addProgramElementAtIndex(p2.getProgramElementAtIndex(p2CrossoverStart + i), p1CrossoverStart + i);

		return offspring;
	}
	
	//menu for mutations. Add new mutation here and determine mutation type in parameters
	protected Individual mutationMenu(Individual p1, int mutation_N) {
			
			Individual newIndividual = new Individual();
			switch(mutation_N) {
				case 1:	newIndividual = mutation(p1);
					break;
				case 2: newIndividual = mutationShrink(p1);
					break;
				case 3: newIndividual = mutationChangeVar(p1, Parameters.MUT_N);
					break;
				case 4: newIndividual = mutationSwap(p1, Parameters.MUT_N);
					break;
				case 5: newIndividual = mutationSwapAll(p1, Parameters.MUT_N);
					break;
				case 6: newIndividual = mutationCreate(p1);
					break;
				case 7: newIndividual = mutationSubTree(p1);
					break;
				case 8: newIndividual = mutationChangeOperator(p1, Parameters.MUT_N);
					break;
				case 9: newIndividual = mutationChangeConstant(p1, Parameters.MUT_N);
					break;
				default: System.out.println("No mutation type selected");
			}
			return newIndividual;
		}
	protected Individual mutation(Individual p) {
		int mutationPoint = r.nextInt(p.getSize());
		int parentElementsToEnd = p.countElementsToEnd(mutationPoint);
		Individual offspring = p.selectiveDeepCopy(mutationPoint, mutationPoint + parentElementsToEnd - 1);
		Individual randomTree = initializer.grow(Parameters.IN_DEPTH_LIM);

		for (int i = 0; i < randomTree.getSize(); i++)
			offspring.addProgramElementAtIndex(randomTree.getProgramElementAtIndex(i), mutationPoint + i);
		
		
		offspring.calculateDepth();
		return offspring;
	}
	//generates a new individual from a parent’s subtree
	// still with ERROR, seems that when it gets a individual too small breaks the structure and can't calculate depth
	protected Individual mutationShrink(Individual p) {
		int mutationPoint = r.nextInt(p.getSize()) ;  //so it doesn't generate a 0 element subtree
		int parentElementsToEnd = p.countElementsToEnd(mutationPoint);
		Individual offspring = new Individual();
		
		for(int i=0; i<mutationPoint;i++) {
			offspring.addProgramElement(p.getProgramElementAtIndex(i));
		}
		Individual randomTree = initializer.grow(Parameters.IN_DEPTH_LIM);
		for(int i=0; i<randomTree.getSize();i++) {
			offspring.addProgramElement(randomTree.getProgramElementAtIndex(i));
		}
		//checkStructure(offspring);
		offspring.calculateDepth();
		return offspring;
	}
	protected Individual mutationSubTree(Individual p) {
		int mutationPoint = r.nextInt(p.getSize()) ;  //so it doesn't generate a 0 element subtree
		//int parentElementsToEnd = p.countElementsToEnd(mutationPoint);
		Individual offspring = new Individual();
		
		for(int i=mutationPoint; i<p.getSize();i++) {
			offspring.addProgramElement(p.getProgramElementAtIndex(i));
		}
		//checkStructure(offspring);
		offspring.calculateDepth();
		return offspring;
	}
	//changes var or contants by random var or constant to a maximum of N
	protected Individual mutationChangeVar(Individual i, int varsChangeN) {
		//Individual offspring = new Individual();
		while (varsChangeN!=0) {
			int pointToStart = r.nextInt(i.getSize());
			for(int a=pointToStart; a< i.getSize(); a++    ) {
				if(i.isVariable(a) || i.isConstant(a)) {
					i.setProgramElementAtIndex(initializer.terminalSet.get(r.nextInt(initializer.terminalSet.size()) ), a);
					varsChangeN-=1;
				} else {
					continue;
				}
			}
			
		}
		
		return i;
	}
	protected Individual mutationChangeOperator(Individual i, int varsChangeN) {
		while (varsChangeN!=0) {
			int pointToStart = r.nextInt(i.getSize());
			for(int a=pointToStart; a< i.getSize(); a++    ) {
				if(i.isOperator(a)) {
					i.setProgramElementAtIndex(initializer.functionSet.get(r.nextInt(initializer.functionSet.size()) ), a);
					varsChangeN-=1;
				} else {
					continue;
				}
			}
			
		}
		return i;
	}
	protected Individual mutationChangeConstant(Individual i, int varsChangeN){
		while (varsChangeN!=0) {
//			int pointToStart = r.nextInt(i.getSize());
			for(int a=0; a< i.getSize(); a++    ) {
				if(i.getNumberOfConstants() < varsChangeN)
					return i;
				if(i.isConstant(a) || r.nextBoolean()) {
					i.setProgramElementAtIndex(initializer.constantSet.get(r.nextInt(initializer.constantSet.size()) ), a);
					varsChangeN-=1;
				} else {
					continue;
				}
			}
			
		}
		return i;
	}
	//swaps every var with next one
	protected Individual mutationSwapAll(Individual i, int numberOfSwaps) {
		Individual offspring = new Individual();
		for(int a=0; a<(i.getSize()-1); a++) {
			if(numberOfSwaps==0)
				break;
			if(i.isVariable(a) && i.isVariable(a+1)) {
				offspring.addProgramElement(i.getProgramElementAtIndex(a));
				i.setProgramElementAtIndex(i.getProgramElementAtIndex(a+1), a);
				i.setProgramElementAtIndex(offspring.getProgramElementAtIndex(0), a+1);
				offspring.removeProgramElementAtIndex(0);
			} else{
				continue;
			}
		}
		return i;
	}
	//swaps only 2 vars of place
	protected Individual mutationSwap(Individual i, int numberOfSwaps) {
		Individual offspring = new Individual();
		for(int a=0; a<(i.getSize()-1); a++) {
			if(numberOfSwaps==0)
				break;
			if(i.isVariable(a) && i.isVariable(a+1)) {
				offspring.addProgramElement(i.getProgramElementAtIndex(a));
				i.setProgramElementAtIndex(i.getProgramElementAtIndex(a+1), a);
				i.setProgramElementAtIndex(offspring.getProgramElementAtIndex(0), a+1);
				offspring.removeProgramElementAtIndex(0);
				a++;
			} else{
				continue;
			}
		}
		return i;
	}
	protected Individual mutationCreate(Individual i) {
		Individual a = new Individual();
		if(r.nextBoolean()) {
			a = initializer.grow(Parameters.IN_DEPTH_LIM);
		} else {
			a =initializer.full(Parameters.IN_DEPTH_LIM);
		}
		return a;
	}
	// this replacement implements elitism
	protected Population replacement(Population offspring) {
		Population nextGeneration = new Population();
		Individual bestParent = population.getBest();
		Individual bestOffspring = offspring.getBest();
		Individual elit;
		
		
		if (bestParent.getTrainingError() < bestOffspring.getTrainingError())
			elit = bestParent;
		else
			elit = bestOffspring;

		nextGeneration.addIndividual(elit);
		for (int i = 0; i < offspring.getSize(); i++) {
			if (offspring.getIndividual(i).getId() != elit.getId())
				nextGeneration.addIndividual(offspring.getIndividual(i));
		}
		return nextGeneration;
	}
	//replacement adapted for pararelPops
	protected Population replacementPararel(Population populationT, Population offspringT) {
		Population nextGeneration = new Population();
//		Individual bestParent = populationT.getBest();
//		Individual bestOffspring = offspringT.getBest();
//		Individual elit;
		//got elitism implemented anyway
//		if(Parameters.TrainUnseen) {
//			if (bestParent.getTrainingError() < bestOffspring.getTrainingError())
//				elit = bestParent;
//			else
//				elit = bestOffspring;
//		} else {
//			if (bestParent.getUnseenError() < bestOffspring.getUnseenError())
//				elit = bestParent;
//			else
//				elit = bestOffspring;
//		}
//		
//		nextGeneration.addIndividual(elit);
		for (int i = 0; i < offspringT.getSize(); i++) {
			nextGeneration.addIndividual(offspringT.getIndividual(i));
		}
		return nextGeneration;
		
	}

	protected void swapPararel(boolean train) {
		int[] bests = new int[Parameters.PararellPopsN];
		int x;
		if(train) {
			for(x=0; x<Parameters.PararellPopsN; x++) {
				bests[x] = pops[x].getBestIndex();
			} 
			for(x=0; x<Parameters.PararellPopsN;x++) {
				pops[x].removeIndividual(pops[x].getWorstIndex());
				
				if(x==(Parameters.PararellPopsN-1))
					pops[x].addIndividual(pops[0].getIndividual(bests[0]));
				else
					pops[x].addIndividual(pops[x+1].getIndividual(bests[x+1]));
			}
		}else {
			for(x=0; x<Parameters.PararellPopsN; x++) {
				bests[x] = pops[x].getBestIndexUnseen();
			} 
			for(x=0; x<Parameters.PararellPopsN;x++) {
				pops[x].removeIndividual(pops[x].getWorstIndexUnseen());
				
				if(x==(Parameters.PararellPopsN-1))
					pops[x].addIndividual(pops[0].getIndividual(bests[0]));
				else
					pops[x].addIndividual(pops[x+1].getIndividual(bests[x+1]));
			}
		}
	}
	protected void swapPararel1() {
		int x; 
		Individual i = pops[0].getBest();
		for(x=1; x< Parameters.PararellPopsN; x++) {
			pops[x-1].removeIndividual(pops[x-1].getWorstIndex());
			pops[x-1].addIndividual(pops[x].getBest() );
		}
		pops[x-1].removeIndividual(pops[x-1].getWorstIndex());
		pops[x-1].addIndividual(i );
		//pops[x]
		
	}
	protected void parameterChangePops(int n) {
		switch(n) {
		case 0: Parameters.XO_TYPE =1 ;Parameters.MUT_TYPE=1; Parameters.SELECTION_TYPE =1; Parameters.VAR_TOUR_PR = 10; Parameters.N_ELITES = 10;  break;
		case 1: Parameters.XO_TYPE =1 ;Parameters.MUT_TYPE=7; Parameters.SELECTION_TYPE =1; Parameters.VAR_TOUR_PR = 30; Parameters.N_ELITES = 30;  break;
		case 2: Parameters.XO_TYPE =1 ;Parameters.MUT_TYPE=7; Parameters.SELECTION_TYPE =1; Parameters.VAR_TOUR_PR = 50; Parameters.N_ELITES = 50; break;
		case 3: Parameters.XO_TYPE =4 ;Parameters.MUT_TYPE=1; Parameters.SELECTION_TYPE =1; Parameters.VAR_TOUR_PR = 70; Parameters.N_ELITES = 70; break;
		case 4: Parameters.XO_TYPE =1 ;Parameters.MUT_TYPE=1; Parameters.SELECTION_TYPE =2; Parameters.N_ELITES = 10;  break;
		case 5: Parameters.XO_TYPE =1 ;Parameters.MUT_TYPE=1; Parameters.SELECTION_TYPE =2; Parameters.N_ELITES = 30;  break;
		case 6: Parameters.XO_TYPE =4 ;Parameters.MUT_TYPE=7; Parameters.SELECTION_TYPE =2; Parameters.N_ELITES = 50;  break;
		case 7: Parameters.XO_TYPE =4 ;Parameters.MUT_TYPE=1; Parameters.SELECTION_TYPE =1; Parameters.VAR_TOUR_PR = 50; Parameters.N_ELITES = 50; break;
		default:Parameters.XO_TYPE =1 ;Parameters.MUT_TYPE=7; Parameters.SELECTION_TYPE =1; Parameters.VAR_TOUR_PR = 50; Parameters.N_ELITES = 50; break;
		}
	}
	
	public void setAbsoluteBestPararelSSGP() {
		int best = 0; double bestTrain = 99999;
		for(int i=0; i<pops.length;i++) {
			if(popsCurrentBest[i].getTrainingError() < bestTrain) {
				best = i;
				bestTrain = popsCurrentBest[i].getTrainingError();
			}
		}
		if(Parameters.USE_SSGP_CENTRALIZED) {
			if(bestTrain < currentBestSSGP.getTrainingError())
				currentBest = popsCurrentBest[best];
			else
				currentBest = currentBestSSGP;
			
		} else
			currentBest = popsCurrentBest[best];
			
	}
	public void setAbsoluteBestPararelUnseen() {
		int best = 0; double bestTrain = 99999;
		for(int i=0; i<pops.length;i++) {
			if(popsCurrentBest[i].getUnseenError() < bestTrain) {
				best = i;
				bestTrain = popsCurrentBest[i].getUnseenError();
			}
		}
		currentBestUnseen = popsCurrentBest[best];
	}
	public void setAbsoluteBestUnseenPararelSSGP() {
		int best = 0; double bestTrain = 99999;
		for(int i=0; i<pops.length;i++) {
			if(popsCurrentBestUnseen[i].getUnseenError() < bestTrain) {
				best = i;
				bestTrain = popsCurrentBestUnseen[i].getUnseenError();
			}
		}
		if(Parameters.USE_SSGP_CENTRALIZED) {
			if(bestTrain < currentBestUnseenSSGP.getUnseenError())
				currentBestUnseen = popsCurrentBestUnseen[best];
			else
				currentBestUnseen = currentBestUnseenSSGP;
			
		}else
			currentBestUnseen = popsCurrentBestUnseen[best];
	}
	
	//elitism
	public Population Elitism(Population pop,Population offspring, int nElites) {
		int[] wors = pop.getWorsts(nElites);
		int[] bes = pop.getBests(nElites);
		for(int i=0; i<nElites; i++) {
//			System.out.println(wors[i]+"  "+bes[i]);
			offspring.removeIndividual(wors[i]);
			offspring.addIndividual(pop.getIndividual(bes[i]));
		}
		return offspring;
		
	}
	
	// --------------------------------------------------------------------

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// GP Measures
	//
	public void computeBloat() {
		double avgSizeG, avgFitG;
		avgSizeG = population.getAVGSize();
		avgFitG = population.getAVGFitness();
		bloat = ((avgSizeG - avgSize0) / avgSize0) / ((avgFit0 - avgFitG) / avgFit0);
	}
	//computes adapted to work with pararel
	public void computeBloatPararel(int pop) {
		double avgSizeG, avgFitG;
		avgSizeG = pops[pop].getAVGSize();
		avgFitG = pops[pop].getAVGFitness();
		popsBloat[pop] = ((avgSizeG - popsAvgSize0[pop]) / popsAvgSize0[pop]) / ((popsAvgFit0[pop] - avgFitG) / popsAvgFit0[pop]);
	}
	public void computeOverfittingPararel(int pop) {
		if (popsCurrentBest[pop].getUnseenError() < popsBtp[pop]) {
			popsOverfit[pop] = 0;
			popsBtp[pop] = popsCurrentBest[pop].getUnseenError();
			popsTbtp[pop] = popsCurrentBest[pop].getTrainingError();
		} else
			popsOverfit[pop] = Math.abs(popsCurrentBest[pop].getTrainingError() - popsCurrentBest[pop].getUnseenError())
					- Math.abs(popsTbtp[pop] - popsBtp[pop]);

	}
	public void computeOverfitting() {
		if (currentBest.getUnseenError() < btp) {
			overfitting = 0;
			btp = currentBest.getUnseenError();
			tbtp = currentBest.getTrainingError();
		} else
			overfitting = Math.abs(currentBest.getTrainingError() - currentBest.getUnseenError())
					- Math.abs(tbtp - btp);

	}
	public void computeOverfittingPaper() {
		if(currentBest.getTrainingError() > currentBest.getUnseenError() ) {
			overfittingPaper = 0;
		} else if( currentBest.getUnseenError() < btpPaper) {
			overfittingPaper = 0;
			btpPaper = currentBest.getUnseenError();
			tbtpPaper = currentBest.getTrainingError();
		} else {
			overfitting = Math.abs(currentBest.getTrainingError() - currentBest.getUnseenError())
					- Math.abs(tbtp - btp);
		}
	}
	public double avgTrainingError(Population pop) {
		double avg=0;
		for(int i=0; i<pop.getSize(); i++) {
			avg+= pop.getIndividual(i).getTrainingError() ;
		}
		return avg/ pop.getSize();
	}
	// --------------------------------------------------------------------

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Other
	//
	//checks the structure of the individual
	protected void checkStructure(Individual i) {
		 if ( (i.getNumberOfVariables()+1) == i.getNumberOfOperators() || (i.getNumberOfVariables() -1) == i.getNumberOfOperators() 
				 || (i.getNumberOfVariables()) == i.getNumberOfOperators()) {
			//correct the structure (maybe? it might screw the XO/mutation)
		}else {
			
			System.out.println("ERROR, the individual stucture is not correct");
			System.out.println("Number of operators --> "+i.getNumberOfOperators());
			System.out.println("Number of variables --> "+i.getNumberOfVariables());
		}
	}
	
	
	protected void addValue() {
		Main.output[Main.outputCount][0] = Main.currentRun;
		Main.output[Main.outputCount][1] = currentGen;
		Main.output[Main.outputCount][2] = currentBest.getTrainingError();
		Main.output[Main.outputCount][3] = currentBest.getUnseenError();
		Main.output[Main.outputCount][4] = currentBest.getSize();
		Main.output[Main.outputCount][5] = currentBest.getDepth();
		Main.output[Main.outputCount][6] = bloat;
		Main.output[Main.outputCount++][7] = overfitting;
	}
	//
	//MISSING ADDVALUE FOR PARAREL POPS
	//
	protected void updateCurrentBest() {
		currentBest = population.getBest();
	}
	protected void updateCurrentBestUnseen() {
		currentBestUnseen = population.getBestUnseen();
	}
	protected void updateCurrentBestPararel(int i) {
		popsCurrentBest[i] = pops[i].getBest();
	}
	protected void updateCurrentBestPararelUnseen(int i) {
		popsCurrentBestUnseen[i] = pops[i].getBestUnseen();
	}

	protected void printState() {
		if (Parameters.IO_APPLY_PRINT_GEN) {
			System.out.println("\nBest at generation:\t\t" + currentGen+"\tGlobal Best");
			System.out.printf("Training error:\t\t%.2f\nUnseen error:\t\t%.2f\nSize:\t\t\t%d\nDepth:\t\t\t%d",
					currentBest.getTrainingError(), currentBest.getUnseenError(), currentBest.getSize(),
					currentBest.getDepth());
		}
	}
	protected void printStateAbsolut() {
		if (Parameters.IO_APPLY_PRINT_GEN) {
			System.out.println("\nBest at generation:\t\t" + currentGen+"\tAbsolut Best");
			System.out.printf("Training error:\t\t%.2f\nUnseen error:\t\t%.2f\nSize:\t\t\t%d\nDepth:\t\t\t%d",
					absolutBest.getTrainingError(), absolutBest.getUnseenError(), absolutBest.getSize(),
					absolutBest.getDepth());
		}
	}
	protected void printStatePararell(int i) {
		if (Parameters.IO_APPLY_PRINT_GEN) {
			System.out.println("\nBest at generation:\t\t" + currentGen+" \t in Population "+i);
			System.out.printf("Training error:\t\t%.2f\nUnseen error:\t\t%.2f\nSize:\t\t\t%d\nDepth:\t\t\t%d\nBloat:\t\t\t%f\nOverfit\t\t\t%f\n",
					popsCurrentBest[i].getTrainingError(), popsCurrentBest[i].getUnseenError(), popsCurrentBest[i].getSize(),
					popsCurrentBest[i].getDepth(), popsBloat[i], popsOverfit[i]);
		
		}
	}
	protected void printStateSSGP() {
		if (Parameters.IO_APPLY_PRINT_GEN) {
			System.out.println("\nBest at generation:\t\t" + currentGen+"\tin Global Optimum Central");
			System.out.printf("Training error:\t\t%.2f\nUnseen error:\t\t%.2f\nSize:\t\t\t%d\nDepth:\t\t\t%d",
					currentBestSSGP.getTrainingError(), currentBestSSGP.getUnseenError(), currentBestSSGP.getSize(),
					currentBestSSGP.getDepth());
		}
	}

	public void updateAbsolutBest() {
//		System.out.println("1-absolut "+absolutBest.getTrainingError() +"  un "+absolutBest.getUnseenError());
		for(int i=0; i<popsCurrentBest.length;i++) {
			if(absolutBest.getUnseenError() > popsCurrentBest[i].getUnseenError())
				absolutBest = popsCurrentBest[i];
		}
//		System.out.println("2-absolut "+absolutBest.getTrainingError() +"  un "+absolutBest.getUnseenError());
		if(Parameters.USE_SSGP_CENTRALIZED && absolutBest.getUnseenError() > currentBestUnseenSSGP.getUnseenError())
			absolutBest = currentBestSSGP;
//		System.out.println("3-absolut "+absolutBest.getTrainingError() +"  un "+absolutBest.getUnseenError());
	}


	public Individual getCurrentBest() {
		return currentBest;
	}
	public Individual getAbsoluteBest() {
		return absolutBest;
	}
	// Compare current best of all populations and get overall best
	public Individual getCurrentBestPararel() {
		double bestTrainingErr=9999;
		int index=999;
		for(int i=0; i<Parameters.PararellPopsN;i++) {
			if(popsCurrentBest[i].getTrainingError() < bestTrainingErr) {
				bestTrainingErr = popsCurrentBest[i].getTrainingError();
				index = i;
			}
		}
		return popsCurrentBest[index];
	}
	public Individual getCurrentBestPararelUnseen() {
		double bestTrainingErr=9999;
		int index=999;
		for(int i=0; i<Parameters.PararellPopsN;i++) {
			if(popsCurrentBest[i].getUnseenError() < bestTrainingErr) {
				bestTrainingErr = popsCurrentBestUnseen[i].getUnseenError();
				index = i;
			}
		}
		return popsCurrentBestUnseen[index];
	}

	public Population getPopulation() {
		return population;
	}
	
	
	public void computeVarsFitness() {
		
		//
		if(currentBest.getTrainingError() <28 && currentBest.getUnseenError() < 35) {
			for(int i=0; i<currentBest.getSize(); i++) {
				/*
				if(currentBest.isOperatorFaster(i)) {
					if(currentBest.getProgramElementAtIndex(i).toString().contains("+"))
						initializer.varsFitness[278] += 1;
					else if(currentBest.getProgramElementAtIndex(i).toString().contains("-"))
						initializer.varsFitness[279] += 1;
					else if(currentBest.getProgramElementAtIndex(i).toString().contains("*"))
						initializer.varsFitness[280] += 1;
					else if(currentBest.getProgramElementAtIndex(i).toString().contains("/"))
						initializer.varsFitness[281] += 1;
					else
						System.out.println("cant get the right operator for computeVarsFitness");
					
				} else { */
					for(int x=0; x<278; x++) {
						if(currentBest.getProgramElementAtIndex(i).toString().contains(Integer.toString(x)))
							initializer.varsFitness[x] += 1;
					}
				
				
			}
			
		}
	}
	
	public void sendToCentral() {
		
		for(int i=0; i<Parameters.SEND_TO_CENTRAL*Parameters.PararellPopsN; i++) {
			steadyStateCentral.steadyStateSwap(pops[i%Parameters.PararellPopsN].getBest());
		}
		
	}
	protected void updateCurrentBestSSGP() {
		currentBestSSGP = steadyStateCentral.getBest();
	}
	protected void updateCurrentBestUnseenSSGP() {
		currentBestUnseenSSGP = steadyStateCentral.getBestUnseen();
	}
	
	/*  IO methods */



	public void exportVarsFitness() {
		BufferedWriter bw = null;
		try {
			bw =  new BufferedWriter(new FileWriter("output\\varsFitness.txt", false));
			
			String toWrite = "";
			toWrite+= initializer.varsFitness[0];
			
			
//			bw.write(varsFitness[0]);
			for(int i=1; i<initializer.varsFitness.length;i++) {
				toWrite += (","+initializer.varsFitness[i]);
//				if(i==varsFitness.length-1) {
//					bw.write(varsFitness[i]);
//				}
				
			}
			bw.write(toWrite);
			
			
		} catch(IOException e) {}
		finally {
			try {
			bw.close();
			} catch(IOException e2) {}
		}
	}
	
	/******    STATS REPORT     **//////
	
	public void Stats_Report() {
		BufferedWriter bw = null;
		try {
			bw =  new BufferedWriter(new FileWriter(Parameters.FileNameWrite, true));

			//	bw.newLine();
			//	bw.write("NEW RUN");
		/*		bw.write("Generation,"+currentGen+", TrainingError,"+currentBest.getTrainingError()+",UnseenError,"+currentBest.getUnseenError()+",size,"+currentBest.getSize()+
						",depth,"+currentBest.getDepth()+",bloat,"+bloat+",overfit,"+overfitting+",NVARS,"+currentBest.getNumberOfVariables()+
						",NOPERATORS,"+currentBest.getNumberOfOperators()+
						",PROGRAM,"+currentBest.getProgram().toString());
			*/
//			bw.append("Train,"+currentGen+","+currentBest.getTrainingError()+","+currentBest.getUnseenError()+","+currentBest.getSize()+
//					","+currentBest.getDepth()+","+bloat+","+overfitting+","+
//					","+currentBest.getProgram());
//			
			if(Parameters.GP_APPLY) {
				
				if(currentGen==Parameters.PRINT_N)
					bw.newLine();
				bw.newLine();
				bw.append("Train,"+currentGen+","+currentBest.getTrainingError()+","+currentBest.getUnseenError()+","+currentBest.getSize()+
						","+currentBest.getDepth()+","+bloat+","+overfitting+","+currentBest.getNumberOfVariables()+
						","+currentBest.getNumberOfOperators()+","+Parameters.XV_PROP_TRAIN+","+Parameters.VAR_XOVER_PROB+","+Parameters.VAR_TOUR_PR+
						","+Parameters.XO_TYPE+","+Parameters.MUT_TYPE+","+Parameters.N_ELITES+","+
						Parameters.USE_SSGP_CENTRALIZED+","+Parameters.USE_VARS_SPLITS+","+Parameters.USE_OBSV_SPLIT+
						","+currentBest.getProgram());
//				setAbsoluteBestUnseenPararelSSGP();
				bw.newLine();
//			bw.append("Unseen,"+currentGen+","+currentBestUnseen.getTrainingError()+","+currentBestUnseen.getUnseenError()+","+currentBestUnseen.getSize()+
//					","+currentBestUnseen.getDepth()+","+bloat+","+overfitting+","+
//					","+currentBestUnseen.getProgram());
//			
				bw.append("Unseen,"+currentGen+","+currentBestUnseen.getTrainingError()+","+currentBestUnseen.getUnseenError()+","+currentBestUnseen.getSize()+
						","+currentBestUnseen.getDepth()+","+bloat+","+overfitting+","+currentBestUnseen.getNumberOfVariables()+
						","+currentBestUnseen.getNumberOfOperators()+","+Parameters.XV_PROP_TRAIN+","+Parameters.VAR_XOVER_PROB+","+Parameters.VAR_TOUR_PR+
						","+Parameters.XO_TYPE+","+Parameters.MUT_TYPE+","+Parameters.N_ELITES+","+
						Parameters.USE_SSGP_CENTRALIZED+","+Parameters.USE_VARS_SPLITS+","+Parameters.USE_OBSV_SPLIT+
						","+currentBestUnseen.getProgram());   bw.newLine();
						
						bw.append("Absolut,"+currentGen+","+absolutBest.getTrainingError()+","+absolutBest.getUnseenError()+","+absolutBest.getSize()+
								","+absolutBest.getDepth()+","+bloat+","+overfitting+","+absolutBest.getNumberOfVariables()+
								","+absolutBest.getNumberOfOperators()+","+Parameters.XV_PROP_TRAIN+","+Parameters.VAR_XOVER_PROB+","+Parameters.VAR_TOUR_PR+
								","+Parameters.XO_TYPE+","+Parameters.MUT_TYPE+","+Parameters.N_ELITES+","+
								Parameters.USE_SSGP_CENTRALIZED+","+Parameters.USE_VARS_SPLITS+","+Parameters.USE_OBSV_SPLIT+
								","+absolutBest.getProgram());		
						
			} else {
				if(currentGen==Parameters.PRINT_N)
					bw.newLine();
				bw.newLine();
				bw.append("Train,"+currentGen+","+currentBest.getTrainingError()+","+currentBest.getUnseenError()+","+currentBest.getSize()+
						","+currentBest.getDepth()+","+bloat+","+overfitting);
				setAbsoluteBestPararelUnseen();bw.newLine();
//			bw.append("Unseen,"+currentGen+","+currentBestUnseen.getTrainingError()+","+currentBestUnseen.getUnseenError()+","+currentBestUnseen.getSize()+
//					","+currentBestUnseen.getDepth()+","+bloat+","+overfitting+","+
//					","+currentBestUnseen.getProgram());
//			
				bw.append("Unseen,"+currentGen+","+currentBestUnseen.getTrainingError()+","+currentBestUnseen.getUnseenError()+","+currentBestUnseen.getSize()+
						","+currentBestUnseen.getDepth()+","+bloat+","+overfitting);  bw.newLine();
				
				bw.append("Absolut,"+currentGen+","+absolutBest.getTrainingError()+","+absolutBest.getUnseenError()+","+absolutBest.getSize()+
						","+absolutBest.getDepth()+","+bloat+","+overfitting);
				
			}
			
				//bw.write("GENERATION, "+ currentGeneration + ", BEST FITNESS , "+getBest(population).getFitness());
			//bw.append();
			
				/*	bw.newLine();			
				bw.newLine();
			bw.write(getBest(population).getFitness()+","+avgFitness()
			+","+population[getWorstIndex(population)].getFitness());   */

			
		} catch(IOException e) {}
		finally {
			try {
			bw.close();
			} catch(IOException e2) {}
		}
	}
}
