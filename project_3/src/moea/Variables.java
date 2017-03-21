package moea;

public class Variables {
	
	
	public static int maxSegments = 10;
	public static int minPixelsInSegment = 10;
	public static double mRate = 0.5;
	public static int elitesToNextGen = 100;
	public static int pSize = 50;
	
	//{active deviation, active edgevalue, active connectivity}
	public static boolean[] activeObjectives = {true, true, false};
	
	//choose randomly between the two chromosomes if not
	public static double selectBestChromosomeRate = 0.8;
	public static double mutationRate = 1;
	public static double mutationMergeRate = 0.8;
	public static double mutationSplitRate = 0.2;
	
	public static int numberOfGenerations = 50;


	//Crossover variable
	public static double mixingRate = 0.5;
	
	public static int minimumSegmentSize = 150;
	public static int maxmimumSegmentSizeForMutationMerge = 1000;
}
