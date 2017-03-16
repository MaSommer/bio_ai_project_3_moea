package moea;

public class Variables {
	
	public static int maxSegments = 10;
	public static int minPixelsInSegment = 10;
	public static double mRate = 0.5;
	public static int elitesToNextGen = 100;
	public static int pSize = 20;
	
	public static boolean optimizeDeviation = true;
	public static boolean optimizeEdgeFitness = true;
	public static boolean optimizeConnectivity = true;
	
	public static double deviationWeight = 0.4;
	public static double edgeFitnessWeight = 0.3;	
	public static double connectivityWeight = 0.3;
	
	//choose randomly between the two chromosomes if not
	public static double selectBestChromosomeRate = 0.8;
	
	public static double maximumDeviationValue = Math.sqrt(Math.pow(255, 2)+Math.pow(255, 2)+Math.pow(255, 2));
	public static double minimumDeviationValue = 0;
	public static double maximumEdgeValue;
	public static double minimumEdgeValue;
	public static double maximumConnectivityValue;
	public static double minimumConnectivityValue;

}
