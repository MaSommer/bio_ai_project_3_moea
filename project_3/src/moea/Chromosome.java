package moea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Chromosome {
	
	private ArrayList<ArrayList<Pixel>> segments;
	private ArrayList<Pixel> representation;
	private ArrayList<ArrayList<Pixel>> segmentEdges;
	private ArrayList<Integer> pixelToSegment;
	private double fitnessValue;
	private int id;
	private ArrayList<Pixel> pixels;

	private double deviationFitness;
	private double edgeFitness;
	private double connectivityFitness;
 	
	//mapper segmentnummer til fitnessvalues {deviation, edges, connectivity}
	private ArrayList<double[]> segmentFitnessValues;
	
	public Chromosome(ArrayList<Pixel> representation, ArrayList<Pixel> pixels, int id){
		System.out.println("CHROMOSOME NR " + id);
		this.representation = representation;
		segmentFitnessValues = new ArrayList<double[]>();
		this.id = id;
		this.pixels = pixels;

		decodeChromosome(pixels);
		long startTime = System.nanoTime();
		this.segmentEdges = HelpMethods.generateSegmentEdges(segments, pixelToSegment);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
//		System.out.println("Segment edges: "+ duration/Math.pow(10, 9) + " sec");
		segmentFitnessValues = new ArrayList<double[]>();
		long startTime1 = System.nanoTime();
		updateSegmentFitnessValues();
		long endTime1 = System.nanoTime();
		long duration1 = (endTime1 - startTime1);
//		System.out.println("Update fitness values: " + duration1/Math.pow(10, 9) + " sec");
		updateFitnessParameters();
	}
	
	public void addObjectiveDistance(double distance){
		this.fitnessValue += distance;
	}
	
	public void updateSegmentBorder() {
		this.segmentEdges = HelpMethods.generateSegmentEdges(segments, pixelToSegment);
	}
	
	public void clearFitness(){
		this.fitnessValue = 0;
	}
	
	public Chromosome(Chromosome copy){
		this.representation = (ArrayList<Pixel>) copy.representation.clone();
		this.deviationFitness = copy.deviationFitness;
		this.edgeFitness = copy.edgeFitness;
		this.connectivityFitness = copy.connectivityFitness;
		this.pixels = copy.pixels;
	}
	
	public static ArrayList<Chromosome> testFrontierChromosomes(){
		ArrayList<Pixel> repr = new ArrayList<Pixel>();
		ArrayList<Chromosome> pop = new ArrayList<Chromosome>();
		double[][] fitnessValues = {{3,3,3}, {5,2,6}, {0,3,4},{1,4,4}, {1,4,5}, {3,3,4}, {5,6,3}, {2,5,6}, {1,7,9}, {3,6,7}};
		for(int i = 0 ; i < fitnessValues.length ; i++){
			Chromosome chr1 = new Chromosome(repr, repr, i+1);
			double[] fitnesses = fitnessValues[i];
			chr1.setConnectivityFitness(fitnesses[0]);
			chr1.setDeviationFitness(fitnesses[1]);
			chr1.setEdgeFitness(fitnesses[2]);
			System.out.println(Arrays.toString(fitnesses));
			pop.add(chr1);
		}
		return pop;
	}
	public static ArrayList<Chromosome> testCrowdChromosomes(){
		ArrayList<Pixel> repr = new ArrayList<Pixel>();
		ArrayList<Chromosome> pop = new ArrayList<Chromosome>();
		double[][] fitnessValues = {{35,7,1},{80,37,4},{1,2,9}, {27,4,15}, {10,16,22}, {6,11,30}, {45,1,39}, {15,22,49}, {21,29,60}, {3,50,73}};
		for(int i = 0 ; i < fitnessValues.length ; i++){
			Chromosome chr1 = new Chromosome(repr, repr, i+1);
			double[] fitnesses = fitnessValues[i];
			chr1.setConnectivityFitness(fitnesses[0]);
			chr1.setDeviationFitness(fitnesses[1]);
			chr1.setEdgeFitness(fitnesses[2]);
			pop.add(chr1);
		}
		return pop;
	}

	public void setDeviationFitness(double deviationFitness) {
		this.deviationFitness = deviationFitness;
	}

	public void setEdgeFitness(double edgeFitness) {
		this.edgeFitness = edgeFitness;
	}

	public void setConnectivityFitness(double connectivityFitness) {
		this.connectivityFitness = connectivityFitness;
	}

	private void updateSegmentFitnessValues(){
		int segmentNr = 0;
		segmentFitnessValues = new ArrayList<double[]>();
		for (ArrayList<Pixel> segment : segments) {
			double deviation = Functions.segmentDeviation(segment);
			double edgeFitness = Functions.segmentEdgeValue(segmentEdges.get(segmentNr), segment, segmentNr, pixelToSegment);
			double connectivity = Functions.segmentConnectivity(segment, segmentNr, pixelToSegment);
			double[] fitnessValues = {deviation, edgeFitness, connectivity};
			segmentFitnessValues.add(fitnessValues);
			segmentNr++;
		}
	}
	
	public void mutate(){
		int fromPixelIndex = (int) (representation.size() * Math.random());
		Pixel fromPixel = pixels.get(fromPixelIndex);
		
		ArrayList<Pixel> neighbours = fromPixel.getNeighbours();
		
		int toSwapInIndex = (int) (Math.random()*(neighbours.size()-1));
		Pixel toSwapIn = neighbours.get(toSwapInIndex);
		
		representation.set(fromPixelIndex, toSwapIn);
	}
	
	private void updateFitnessParameters(){
		updateSegmentFitnessValues();
		this.deviationFitness = 0;
		this.edgeFitness = 0;
		this.connectivityFitness = 0;
		for (double[] segmentFitness : segmentFitnessValues) {
			deviationFitness += segmentFitness[0];
			edgeFitness += segmentFitness[1];
			connectivityFitness += segmentFitness[2];
		}
	}
	
	
	public void updateChromosome(){
		decodeChromosome(pixels); //Assume the representation has changed. Now we decode the chromosome
		this.segmentEdges = HelpMethods.generateSegmentEdges(segments, pixelToSegment);
		updateFitnessParameters();
	}
	
	
	public double getDeviationFitness() {
		return deviationFitness;
	}

	public double getEdgeFitness() {
		return edgeFitness;
	}

	public double getConnectivityFitness() {
		return connectivityFitness;
	}

	public ArrayList<ArrayList<Pixel>> getSegments() {
		return segments;
	}

	public ArrayList<Pixel> getRepresentation() {
		return representation;
	}

	public double getFitnessValue() {
		return fitnessValue;
	}
	
	public ArrayList<ArrayList<Pixel>> getSegmentEdges(){
		return segmentEdges;
	}
	
	public void decodeChromosome(ArrayList<Pixel> pixels){
		long startTime = System.nanoTime();
		ArrayList<ArrayList<Pixel>> decodedChromosome = new ArrayList<ArrayList<Pixel>>();
		ArrayList<Boolean> visited = new ArrayList<Boolean>();
		pixelToSegment = new ArrayList<Integer>();
		int remainingPixels = pixels.size();
		for(int i = 0 ; i < representation.size(); i++){
			visited.add(false);
			pixelToSegment.add(null);
		}
		int index = -1;
		int lastStartIndex = 0;
		Pixel newPixel=null;
		
		long startTime3 = System.nanoTime();
		int chainNr = 0;
		while(remainingPixels > 0){
			long startTime4 = System.nanoTime();
			ArrayList<Pixel> chain = new ArrayList<Pixel>(); //Ny kjede som foelges til en ende
			for(int i = lastStartIndex ; i < visited.size(); i++){    //finner foerste ledige sted aa starte fra
				if(!visited.get(i)){
					index = i;
					lastStartIndex = i;
					chain.add(pixels.get(index));
					break;
				}
			}
			while(true){
				Boolean tempFix = false;
				if(visited.get(index)){   //Sjekker at vi ikke har vaert innom foer. Hvis vi har det, saa skal vi avslutte kjedesoeket.
					if(!tempFix){
						newPixel = representation.get(index);						
					}
					if (pixelToSegment.get(index) != null){
						HelpMethods.mergeArrayList(decodedChromosome.get(pixelToSegment.get(index)), chain);
						for (int i = 0; i < chain.size(); i++) {
							int ind = chain.get(i).getId();
							pixelToSegment.set(ind, pixelToSegment.get(index));
						}
						remainingPixels -= chain.size();
					}
					else{
						for (int i = 0; i < chain.size(); i++) {
							int ind = chain.get(i).getId();
							pixelToSegment.set(ind, chainNr);
						}
						decodedChromosome.add(chain);
						chainNr++;
						remainingPixels -= chain.size();		
					}
					break;
				}

				visited.set(index, true);
				newPixel = representation.get(index);									//Hvis vi ikke har besoekt den nye kjeden, saa finner vi hvilket pixel som ligger paa den nye indexen.

				index = newPixel.getId();
				if(!visited.get(index)){
					chain.add(newPixel);
					tempFix=true;												//Legger til ubesoekt node i segmentet. 
				}
			}
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
//		System.out.println("decodeChromosome: "+ duration/Math.pow(10, 9) + " sec");
		
		this.segments = decodedChromosome;
	}
	
	public void removeSmallSegments( int minSize){
		for(ArrayList<Pixel> segment: this.segments){
			if(segment.size() < minSize){
				for(Pixel p:segment){
					ArrayList<Pixel> borderPixels = new ArrayList<Pixel>();
					for(Pixel n:p.getNeighbours()){
						if(!segment.contains(n)){
							borderPixels.add(n);
						}
					}
					if(borderPixels.size() == 0){
						continue;
					}
					else{
						double minDistance = 10000;
						Pixel minPointer = null;
						for(Pixel b:borderPixels){
							double dist = p.getDistance(b);
							if(dist<minDistance){
								minDistance = dist;
								minPointer = b;
							}
						}
						this.representation.set(p.getId(), minPointer);
						break;
					}	
				}
			}
		}
//		System.out.println("Number of segments: " + segments.size());
		decodeChromosome(pixels);
		
	}


	
	public String toString(){
		return "" + id;
	}

}
