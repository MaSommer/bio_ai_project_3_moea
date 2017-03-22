package moea;

public class Variables {
	
	
	public static int maxSegments = 10;
	public static int minPixelsInSegment = 10;
	public static int pSize = 30;
	public static int numberOfGenerations = 30;
	
	//{active deviation, active edgevalue, active connectivity}
	public static boolean[] activeObjectives = {true, true, true};
	
	//choose randomly between the two chromosomes if not
	public static double selectBestChromosomeRate = 0.8;
	public static double mutationRate = 1;
	public static double mutationMergeSmallesRate = 0.1;
	public static double mutationMergeAllCombinationsRate = 0.9;
	public static double mutationSplitRate = 0;
	public static double crossoverRate = 0;
	


	//Crossover variable
	public static double mixingRate = 0.5;
	
<<<<<<< HEAD
	public static int minimumSegmentSize = 300;

	public static int maxmimumSegmentSizeForMutationMerge = 1000;
	
	public static int numberOfFirstInitializingCut = 20;
=======
	public static int minimumSegmentSize = 40;
	public static int maxmimumSegmentSizeForMutationMerge = 1000;
	
	public static int numberOfFirstInitializingCut = 50;
>>>>>>> parent of aa8869a... Merge branch 'master' into Marty
	
	public static int optimalNumberOfSegments = 40;
}
