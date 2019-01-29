package main;

import algorithms.GP;
import algorithms.GSGP;
import utils.Data;
import utils.Parameters;
import utils.Utils;

public class Main {

	public static int outputCount = 0;
	public static double[][] output;
	public static int currentRun = 0;

	public static void main(String[] args) {

		output = new double[Parameters.EA_NRUNS * Parameters.EA_NGEN + Parameters.EA_NRUNS][8];
		double[][] summary = new double[4][Parameters.EA_NRUNS];
		Individual best = null;

		Data data = new Data();
		data.loadAllData(Parameters.IO_IN_FILEPATH, Parameters.IO_IN_SINGLEFILE);
		
		for (int r = 0; r < Parameters.EA_NRUNS; r++) {
			System.out.printf("\n\t\t##### Run %d #####\n", r);
			data.holdout(Parameters.XV_PROP_TRAIN);
			if(Parameters.USE_OBSV_SPLIT) {
				data.splittedData();
				data.splittedDataHalf();
			}
			
			GP gp;
			if (Parameters.GP_APPLY)
				gp = new GP(data);
			else
				gp = new GSGP(data);

			gp.search(Parameters.EA_NGEN);
//			Individual bestFound = gp.getCurrentBest();
			Individual bestFound = gp.getAbsoluteBest();
			if (best == null)
				best = bestFound;
			else if (best.getUnseenError() > bestFound.getUnseenError())
				best = bestFound;

			summary[0][r] = bestFound.getTrainingError();
			summary[1][r] = bestFound.getUnseenError();
			summary[2][r] = bestFound.getSize();
			summary[3][r] = bestFound.getDepth();

			System.out.print("\nBest at run " + r + ":\t");
			bestFound.print();
			System.out.println();

			Utils.writeData(output, Parameters.IO_DIR_OUT + Parameters.IO_IN_PROB);
			currentRun++;
		}

		// print average results
		System.out.printf("\n\t\t##### Results after " + Parameters.EA_NRUNS + " runs #####\n\n");
		System.out.printf("Average training error:\t\t%.2f\n", Utils.averageVec(summary[0]));
		System.out.printf("Average unseen error:\t\t%.2f\n", Utils.averageVec(summary[1]));
		System.out.printf("Average size:\t\t\t%.2f\n", Utils.averageVec(summary[2]));
		System.out.printf("Average depth:\t\t\t%.2f\n", Utils.averageVec(summary[3]));

		System.out.print("\nBest at all runs:\t");
		best.print();
		System.out.println();

		if(Parameters.GP_APPLY||Parameters.BUILD_INDIVIDUALS)
			best.writeToObjectFile(Parameters.IO_DIR_OUT_IND + "best_TE"+best.trainingError+"_UE"+best.unseenError);
	}
}