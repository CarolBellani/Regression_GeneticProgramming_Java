package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import utils.Data;
import utils.Parameters;

public class Population implements Serializable {

	private static final long serialVersionUID = 7L;

	protected ArrayList<Individual> individuals;
	protected int[] bests ;
	protected Individual buckets[][];
	
	
	public Population() {
		individuals = new ArrayList<Individual>();
		bests = new int[Parameters.EA_PSIZE];

	}
	
	public void evaluate(Data data) {
		for (int i = 0; i < individuals.size(); i++) 
			individuals.get(i).evaluate(data);
	}
	public void evaluateSplitted(Data data, int split) {
		for(int i=0; i<individuals.size();i++) {
			individuals.get(i).evaluateSplitted(data, split);
		}
	}

	public double getAVGSize() {
		double size=0;
		for (Individual i:individuals)
			size+=i.getSize();
		return size/individuals.size();
	}
	
	public double getAVGFitness() {
		double fit=0;
		for (Individual i:individuals) 
			fit+=i.getTrainingError();
		return fit/individuals.size();
	}
	
	public Individual getBest() {
		return individuals.get(getBestIndex());
	}
	public Individual getBestUnseen() {
		return individuals.get(getBestIndexUnseen());
	}
	public Individual getBestParsimony() {
		return individuals.get(getBestIndexParsimony());
	}
	
	public int getSize() {
		return individuals.size();
	}

	public Individual getIndividual(int index) {
		return individuals.get(index);
	}

	public ArrayList<Individual> getIndividuals() {
		return individuals;
	}

	public int getBestIndex() {
		int bestIndex = 0;
		double bestTrainingError = individuals.get(bestIndex).getTrainingError();
		for (int i = 1; i < individuals.size(); i++) {
			if (individuals.get(i).getTrainingError() < bestTrainingError) {
				bestTrainingError = individuals.get(i).getTrainingError();
				bestIndex = i;
			}
		}
		return bestIndex;
	}
	public int getBestIndexParsimony() {
		int bestIndex = 0;
		double bestTrainingError = individuals.get(bestIndex).getParsimonyMeasure();
		for (int i = 1; i < individuals.size(); i++) {
			if (individuals.get(i).getParsimonyMeasure() < bestTrainingError) {
				bestTrainingError = individuals.get(i).getParsimonyMeasure();
				bestIndex = i;
			}
		}
		return bestIndex;
	}
	
	//not allowed, but to ckeck
	public int getBestIndexUnseen() {
		int bestIndex = 0;
		double bestUnseenError = individuals.get(bestIndex).getUnseenError();
		for (int i = 1; i < individuals.size(); i++) {
			if (individuals.get(i).getUnseenError() < bestUnseenError) {
				bestUnseenError = individuals.get(i).getUnseenError();
				bestIndex = i;
			}
		}
		return bestIndex;
	}
	
	public int getWorstIndex() {
		int bestIndex = 0;
		double bestTrainingError = individuals.get(bestIndex).getTrainingError();
		for (int i = 1; i < individuals.size(); i++) {
			if (individuals.get(i).getTrainingError() > bestTrainingError) {
				bestTrainingError = individuals.get(i).getTrainingError();
				bestIndex = i;
			}
		}
		return bestIndex;
	}
	public int getWorstIndexUnseen() {
		int bestIndex = 0;
		double bestTrainingError = individuals.get(bestIndex).getUnseenError();
		for (int i = 1; i < individuals.size(); i++) {
			if (individuals.get(i).getUnseenError() > bestTrainingError) {
				bestTrainingError = individuals.get(i).getUnseenError();
				bestIndex = i;
			}
		}
		return bestIndex;
	}
	public int getWorstIndexParsimony() {
		int bestIndex = 0;
		double bestTrainingError = individuals.get(bestIndex).getParsimonyMeasure();
		for (int i = 1; i < individuals.size(); i++) {
			if (individuals.get(i).getParsimonyMeasure() > bestTrainingError) {
				bestTrainingError = individuals.get(i).getParsimonyMeasure();
				bestIndex = i;
			}
		}
		return bestIndex;
	}
	
	
	public int[] getBests(int number) {
		if(Parameters.USE_PARSIMONY) 
			sortIndividualsParsimony();
		else
			sortIndividuals();
		
		int[] bes = new int[number];
		for(int i=0; i<number;i++) {
			bes[i] = bests[i];
//			System.out.println("index "+i+" train "+individuals.get(i).getTrainingError());
		}
		return bes;
	}
	public int[] getWorsts(int number) {
		if(Parameters.USE_PARSIMONY) 
			sortIndividualsParsimony();
		else
			sortIndividuals();
		
		int[] wors = new int[number];
		for(int i=0; i<number;i++) {
			wors[i] = bests[bests.length-1-i];
//			System.out.println(bests[bests.length-1-i]);
//			System.out.println("index "+i+" train "+individuals.get(i).getTrainingError());
		}
		return wors;
	}
	
	//smallest error, BETTER, is index 0
	public void sortIndividuals() {
		int j, temp;
		for(int i=0; i<Parameters.EA_PSIZE; i++) {
			bests[i] = i;
		}
		boolean flag=true;
		
		while(flag) {
			flag=false;
			for(j=0; j<bests.length-1; j++) {
				if(getIndividual(bests[j]).trainingError > getIndividual(bests[j+1]).trainingError) {
					temp = bests[j]; 
					bests[j] = bests[j+1];
					bests[j+1] = temp;
					flag=true;
//					System.out.println(individuals.get(0).getTrainingError() +"  and "+ 0);
//					System.out.println(individuals.get(1).getTrainingError() +"  and "+ 1);
//					System.out.println(individuals.get(2).getTrainingError() +"  and "+ 2);
//					System.out.println(Arrays.toString(bests));
					
				}
			}
		}
	}
	public void sortIndividualsParsimony() {
		int j, temp;
		for(int i=0; i<Parameters.EA_PSIZE; i++) {
			bests[i] = i;
		}
		boolean flag=true;
		
		while(flag) {
			flag=false;
			for(j=0; j<bests.length-1; j++) {
				if(getIndividual(bests[j]).getParsimonyMeasure() > getIndividual(bests[j+1]).getParsimonyMeasure()) {
					temp = bests[j]; 
					bests[j] = bests[j+1];
					bests[j+1] = temp;
					flag=true;
//					System.out.println(individuals.get(0).getTrainingError() +"  and "+ 0);
//					System.out.println(individuals.get(1).getTrainingError() +"  and "+ 1);
//					System.out.println(individuals.get(2).getTrainingError() +"  and "+ 2);
//					System.out.println(Arrays.toString(bests));
					
				}
			}
		}
	}
	public void assignBuckets() {
		buckets = new Individual[Parameters.BUCKETS_N][individuals.size()/Parameters.BUCKETS_N];
		sortIndividuals();
		for(int b=0; b<Parameters.BUCKETS_N;b++) {
			for(int i=0; i<individuals.size()/Parameters.BUCKETS_N; i++) {
				buckets[b][i] = getIndividual(b+(i*Parameters.BUCKETS_N));
			}
		}
	}
	public Individual[][] getBuckets(){
		return buckets;
	}
	public Individual[] getFirstBucket() {
		return buckets[0];
	}
	
	
	//for steady state gp
	public void steadyStateSwap(Individual in) {
//		System.out.println("time  A" +getSize());
		individuals.remove(getWorstIndex());
//		System.out.println("time         B");
		addIndividual(in);
//		System.out.println("time                C");
	}
	
	public void addIndividual(Individual individual) {
		individuals.add(individual);
	}

	public void removeIndividual(int index) {
		individuals.remove(index);
	}
}