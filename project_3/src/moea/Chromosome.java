package moea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Chromosome {
	
	private ArrayList<ArrayList<Pixel>> segments;
	private ArrayList<Pixel> representation;
	private ArrayList<ArrayList<Pixel>> segmentEdges; 
	private ArrayList<Integer> pixelToSegment;
	
	private ArrayList<double[]> segmentAvgRGBValues;

	private double fitnessValue;
	private int id;
	private ArrayList<Pixel> pixels;

	private double deviationFitness;
	private double edgeFitness;
	private double connectivityFitness;
	private ArrayList<double[]> distances;
 	
	//mapper segmentnummer til fitnessvalues {deviation, edges, connectivity}
	private ArrayList<double[]> segmentFitnessValues;
	
	public Chromosome(ArrayList<Pixel> representation, ArrayList<Pixel> pixels, int id, ArrayList<double[]> distances){
		System.out.println("CHROMOSOME NR " + id);
		this.representation = representation;
		this.distances = distances;
		segmentFitnessValues = new ArrayList<double[]>();
		this.id = id;
		this.pixels = pixels;
		
		decodeChromosome(pixels);
		this.segmentEdges = HelpMethods.generateSegmentEdges(segments, pixelToSegment);

		segmentFitnessValues = new ArrayList<double[]>();
		
		updateSegmentFitnessValues();

		updateFitnessParameters();
	}
	
	public ArrayList<Integer> getPixelToSegment() {
		return this.pixelToSegment;
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
			Chromosome chr1 = new Chromosome(repr, repr, i+1, new ArrayList<double[]>());
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
			Chromosome chr1 = new Chromosome(repr, repr, i+1, new ArrayList<double[]>());
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
		segmentAvgRGBValues = new ArrayList<double[]>();
		for (ArrayList<Pixel> segment : segments) {
			double[] deviation = Functions.segmentDeviation(segment);
			double edgeFitness = Functions.segmentEdgeValue(segmentEdges.get(segmentNr), segment, segmentNr, pixelToSegment);
			double connectivity = Functions.segmentConnectivity(segment, segmentNr, pixelToSegment);
			double[] fitnessValues = {deviation[0], edgeFitness, connectivity};
			segmentFitnessValues.add(fitnessValues);
			double[] avgRGB = {deviation[1], deviation[2], deviation[3]};
			segmentAvgRGBValues.add(avgRGB);
			segmentNr++;
		}
	}
	
	public ArrayList<double[]> getSegmentAvgRGBValues() {
		return segmentAvgRGBValues;
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
	public int getId(){
		return id;
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
		
		int chainNr = 0;
		while(remainingPixels > 0){
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
	
//	public void mergeSegments(ArrayList<Pixel> segment1, ArrayList<Pixel> segment2){
//		long startTime = System.nanoTime();
//		ArrayList<Pixel> newSegment = new ArrayList<Pixel>(segment1);
//		for(Pixel p: segment2){
//			newSegment.add(p);
//		}
//
//		int pixelsRemaining = newSegment.size()-1;
//		for(Pixel p:newSegment){
//			this.representation.set(p.getId(), null);
//		}
//		
//		Comparator<Edge> edgeComparator = new Comparator<Edge>() {
//			public int compare(Edge e1, Edge e2){
//				if (e1.getWeight() > e2.getWeight()){
//					return 1;
//				}
//				else if (e1.getWeight() < e2.getWeight()){
//					return -1;
//				}
//				else {
//					return 0;
//				}
//			}
//		};
//		PriorityQueue<Edge> edges = new PriorityQueue<Edge>(10, edgeComparator);
//		int indexInNewSeg = 0;
//		int currentPixel = newSegment.get(indexInNewSeg).getId();
//		representation.set(currentPixel, pixels.get(currentPixl));
//		
//		while(pixelsRemaining > 0) {
//			
//			ArrayList<Edge> currentPixelsEdges = new ArrayList<Edge>();
//			int toPixelIndex = currentPixel;
//			for(int j = 0 ; j < 4 ; j++){
//				double distance = distances.get(currentPixel)[j];
//				if(distance == -1){
//					continue;
//				}
//				else{
//					int fromPixelIndex = -1;
//					if(j == 0){
//						fromPixelIndex = currentPixel - image.get(0).size(); //NORTH pixel
//					}
//					else if(j == 1){
//						fromPixelIndex = currentPixel+ image.get(0).size(); //SOUTH pixel
//					}
//					else if(j == 2){
//						fromPixelIndex = currentPixel +1;//EAST pixel
//					}
//					else if(j == 3) {
//						fromPixelIndex = currentPixel- 1; //WEST
//					}
//					Pixel fromPixel = pixels.get(fromPixelIndex);
//					Pixel toPixel = pixels.get(currentPixel);
//					
//					if(MST.get(fromPixelIndex) == null){
//						currentPixelsEdges.add(new Edge(fromPixel, toPixel, distance));
//						currentPixelsEdges.add(new Edge(toPixel, fromPixel, distance));
//					}
//				}
//			}
//			for(Edge e : currentPixelsEdges){
//				edges.add(e);
//			}
//			while(true){
//				Edge e = edges.poll();
//				
//				if(MST.get(e.getFrom().getId()) == null){
////					System.out.println("Used edge: " + e);
//					Pixel fromPixel = e.getFrom();
//					Pixel toPixel = e.getTo();
//					MST.set(fromPixel.getId(), toPixel);
//					pixelsRemaining--;
//					currentPixel = fromPixel.getId();
//					break;
//				}
//
//			}
//		
//	}

	
	public String toString(){
		return "" + id;
	}

}
