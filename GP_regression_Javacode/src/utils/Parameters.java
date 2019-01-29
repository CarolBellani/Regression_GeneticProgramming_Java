package utils;

public class Parameters {
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Evolutionary algorithm parameters
	//
	public static final int EA_NRUNS = 3;
	public static final int EA_PSIZE = 100;
	public static final int EA_NGEN = 500;
	// --------------------------------------------------------------------
	public static final boolean PararellPops = true;
	public static final int PararellPopsN = 8;
	public static final int PararellSwap = 50;

	//Initialization
	public static final boolean USE_VARS_SPLITS = true;
	public static boolean USE_IMPORTANT_X = false;  //use only preselected vars
	public static boolean USE_ALL_OPERATORS = false; //use all the operators implemented. Some may not be that good
	public static boolean USE_VAR_MEMORY = false;
	//public static final int VARS_SPLITS = 5;
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// GP algorithm parameters
	public static final boolean USE_OBSV_SPLIT = false;
	//
	public static final boolean USE_SSGP_CENTRALIZED = true; // Steady State GP as main pop
	public static final int SSGP_PSIZE = 200;
	public static final int SEND_TO_CENTRAL = 8; //number of bests to send to ssgp
	//
	public static final boolean XOMUT = false; //true--> use XO and mutation with individual probs
	public static final double XOMUT_PROB = 0.3;
	//					
	//
	//Selectio parameters
	public static int SELECTION_TYPE = 2;
	public static boolean USE_PARSIMONY = true;
	public static int BUCKETS_N = 50;
	//XO parameters
	//1-default, 2-hoist, 3-XOhoisted, 4-XOavgImproved, 5-XOsizeFair
	public static int XO_TYPE = 1;
	// 
	//Mutation parameters
	//1-default, 2-shrink, 3-subtree, 4-changeVar,5- changeOperator,6- changeConstant, 7-SwapAll, 8-Swap, 9-Create
	public static  int MUT_TYPE =1;
	public static int MUT_N = 1; //number of mutations to do, standard parameter for all the mutations

	//
	public static int N_ELITES = 100;

	// 
	public static final boolean GP_APPLY = true;
	public static final int IN_DEPTH_LIM = 6;
	public static double VAR_TOUR_PR = 20; //tournament pressure
	public static final boolean VAR_APPLY_DEPTH_LIM = false;
	public static final int VAR_DEPTH_LIM = 17;
	public static final double VAR_XOVER_PROB = 0.3;
	// --------------------------------------------------------------------

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// GSGP particular parameters
	//
	public static boolean BUILD_INDIVIDUALS = true;
	public static final boolean MGSGP_APPLY_BOUND = true;
	public static final double MGSGP_MS = 0.4;
	// --------------------------------------------------------------------

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// File I/O
	//
	public static final int PRINT_N = 10;
	//
	public static final double XV_PROP_TRAIN = .7;

	public static final boolean IO_APPLY_PRINT_GEN = true;
	public static final boolean IO_WRITE_BEST_INDIVIDUAL = true;
	public static final boolean IO_WRITE_INDIVIDUALS_POPULATION = true;

	public static final boolean IO_IN_SINGLEFILE = true;
	public static final String IO_DIR = "C:\\Users\\bella\\Downloads\\Group04_Project2\\";
	public static final String IO_DIR_OUT = IO_DIR + "output\\";
	public static final String IO_DIR_OUT_IND = IO_DIR_OUT + "Individuals\\";
	public static final String IO_IN_PROB = "students";
	public static final String IO_IN_FILEPATH = IO_DIR + "input\\"+IO_IN_PROB;
	public static final String FileNameWrite = "output\\wannabe_2BEST7.csv";
}