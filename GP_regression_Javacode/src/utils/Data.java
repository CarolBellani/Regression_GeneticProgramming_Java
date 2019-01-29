package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Data implements Serializable {
	private static final long serialVersionUID = 7L;
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Parameters
	//
	private double[][] trainingData, unseenData, allData;
	private double[][][] splittedData, splittedDataHalf;
	
	// -------------------------------------------------------------------


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// File I/O
	//
	public void loadAllData(String dataFilePath, boolean singleFile) {
		if (singleFile) 
			allData = readData(dataFilePath + ".txt");
		else {
			trainingData = readData(dataFilePath + "_training.txt");
			unseenData = readData(dataFilePath + "_unseen.txt");

			allData = new double[trainingData.length + unseenData.length][trainingData[0].length];
			int i = 0;

			for (; i < trainingData.length; i++)
				allData[i] = trainingData[i];

			for (; i < allData.length; i++)
				allData[i] = unseenData[i - trainingData.length];
			
			trainingData=unseenData=null;
		}
	}

	public static double[][] readData(String filename) {
		double[][] data = null;
		List<String> allLines = new ArrayList<String>();
		try {
			BufferedReader inputBuffer = new BufferedReader(new FileReader(filename));
			String line = inputBuffer.readLine();
			while (line != null) {
				allLines.add(line);
				line = inputBuffer.readLine();
			}
			inputBuffer.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		StringTokenizer tokens = new StringTokenizer(allLines.get(0).trim());
		int numberOfColumns = tokens.countTokens();
		data = new double[allLines.size()][numberOfColumns];
		for (int i = 0; i < data.length; i++) {
			tokens = new StringTokenizer(allLines.get(i).trim());
			for (int k = 0; k < numberOfColumns; k++)
				data[i][k] = Double.parseDouble(tokens.nextToken().trim());
		}
		return data;
	}
	// -------------------------------------------------------------------

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// XValidation
	//
	public void holdout(double proportionTrain) {
		List<Integer> instances = Utils.shuffleInstances(allData.length);
		double proportionUnseen = 1 - proportionTrain;
		int trainingInstances = (int) Math.ceil(proportionTrain * allData.length);
		int unseenInstances = (int) Math.floor(proportionUnseen * allData.length);
		trainingData = new double[trainingInstances][];
		unseenData = new double[unseenInstances][];

		for (int i = 0; i < trainingInstances; i++)
			trainingData[i] = allData[instances.get(i)];

		for (int i = 0; i < unseenInstances; i++)
			unseenData[i] = allData[instances.get(trainingInstances + i)];
	}
	public void splittedData() {
		List<Integer> instances = Utils.shuffleInstances(trainingData.length);
		int trainingInstances = (int) Math.ceil(trainingData.length/4);
//		System.out.println(trainingInstances+" "+trainingData.length);
		splittedData = new double[4][trainingInstances][];
//		System.out.println(trainingInstances+"  "+trainingData.length  );
		for(int i=0; i< trainingData.length-1; i++) {   //need to adapt depending on the proportion of the dataset
			if(i < trainingData.length/4)
				splittedData[0][i] = trainingData[instances.get(i)]; 
			else if(i < trainingData.length/4 * 2)
				splittedData[1][i -( trainingData.length/4)] = trainingData[instances.get(i)]; 
			else if(i < trainingData.length/4 * 3)
				splittedData[2][i-(trainingData.length/4*2)] = trainingData[instances.get(i)]; 
			else
				splittedData[3][i-(trainingData.length/4 *3)] = trainingData[instances.get(i)]; 
		}
	}
		public void splittedDataHalf() {
			List<Integer> instances = Utils.shuffleInstances(trainingData.length);
			int trainingInstances = (int) Math.ceil(trainingData.length/2);
//			System.out.println(trainingInstances+" "+trainingData.length);
			splittedDataHalf = new double[2][trainingInstances][];
//			System.out.println(trainingInstances+"  "+trainingData.length  );
			for(int i=0; i< trainingData.length-1; i++) {   //need to adapt depending on the proportion of the dataset
				if(i < trainingData.length/2) {
					splittedDataHalf[0][i] = trainingData[instances.get(i)]; 
					
				}
				else
					splittedDataHalf[1][i-(trainingData.length/2 )] = trainingData[instances.get(i)]; 
			}
	}
	// -------------------------------------------------------------------

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Getters
	//
	//training data splitted
	public double[][][] getSplittedData(){
		return splittedData;
	}
	public double[][] getSplittN(int n){
		return splittedData[n];
	}
	public double[][] getSplittedDataHalf(int n){
		return splittedDataHalf[n];
	}
	public double[][] getSplittHalf(int n){
		return splittedDataHalf[n];
	}
	public int getNumberInstancesSplitted(int nsplit) {
		return splittedData[nsplit].length;
	}
	public int getAllNumberInstances() {
		return allData.length;
	}

	public int getDimensionality() {
		return allData[0].length - 1;
	}

	public double[][] getTrainingData() {
		return trainingData;
	}

	public double[][] getUnseenData() {
		return unseenData;
	}
}