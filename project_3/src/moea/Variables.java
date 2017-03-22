package moea;

public class Variables {
	
	
	public static int maxSegments = 10;
	public static int minPixelsInSegment = 10;
	public static int pSize = 30;
	public static int numberOfGenerations = 40;
	
	//{active deviation, active edgevalue, active connectivity}
	public static boolean[] activeObjectives = {true, false, true};

	
	//choose randomly between the two chromosomes if not
	public static double selectBestChromosomeRate = 0.8;
	public static double mutationRate = 1;
	public static double mutationMergeSmallesRate = 0.05;
	public static double mutationMergeAllCombinationsRate = 0.95;
	public static double mutationSplitRate = 0;
	public static double crossoverRate = 0.01;
	


	//Crossover variable
	public static double mixingRate = 0.5;
	
	public static int minimumSegmentSize = 750;

	public static int maxmimumSegmentSizeForMutationMerge = 1000;
	
	public static int numberOfFirstInitializingCut = 40;

}
