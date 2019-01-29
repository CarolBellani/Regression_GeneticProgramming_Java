package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import main.Main;

public class Utils {
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Prints
	//
	public static void printVecInt(int[] vec) {
		System.out.print("[ " + vec[0]);
		for (int i = 1; i < vec.length - 1; i++) {
			System.out.print("\t" + vec[i]);
		}
		System.out.println("\t" + vec[vec.length - 1] + " ]");
	}

	public static void printVecBool(boolean[] vec) {
		System.out.print("[ " + vec[0]);
		for (int i = 1; i < vec.length - 1; i++) {
			System.out.print("\t" + vec[i]);
		}
		System.out.println("\t" + vec[vec.length - 1] + " ]");
	}

	public static void printVecDouble(double[] vec) {
		System.out.print("[ " + vec[0]);
		for (int i = 1; i < vec.length - 1; i++) {
			System.out.print("\t" + vec[i]);
		}
		System.out.println("\t" + vec[vec.length - 1] + " ]");
	}

	public static void printMDouble(double[][] mat) {
		for (int i = 1; i < mat.length; i++) {
			printVecDouble(mat[i]);
		}
	}
	// --------------------------------------------------------------------

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Data manipulation
	//
	public static List<Integer> shuffleInstances(int end) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < end; i++) {
			list.add(i);
		}
		Collections.shuffle(list);
		return list;
	}

	public static void writeData(double[][] data, String fullPath) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fullPath + "_out.txt"));
			for (int i = 0; i < Main.output.length; i++) {
				for (int j = 0; j < Main.output[i].length; j++)
					bw.write(Main.output[i][j] + "\t");
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
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
			for (int k = 0; k < numberOfColumns; k++) {
				data[i][k] = Double.parseDouble(tokens.nextToken().trim());
			}
		}
		return data;
	}
	// --------------------------------------------------------------------

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Read/Write JAVA objects
	//
	public static Object readObject(String filename) {
		Object object = null;
		try {
			InputStream inputStream = new BufferedInputStream(new FileInputStream(filename));
			ObjectInput objectInput = new ObjectInputStream(inputStream);
			try {
				object = objectInput.readObject();
			} finally {
				objectInput.close();
			}
		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}
		return object;
	}

	public static void writeObject(Object object, String filename) {
		try {
			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filename));
			ObjectOutput objectOutput = new ObjectOutputStream(outputStream);
			try {
				objectOutput.writeObject(object);
			} finally {
				objectOutput.close();
			}
		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}
	}
	// --------------------------------------------------------------------

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Calc
	//	
	public static double averageVec(double[] values) {
		double sum = 0.0;
		for (int i = 0; i < values.length; i++)
			sum += values[i];
		return sum / values.length;
	}

	public static double logisticFunction(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}
}
