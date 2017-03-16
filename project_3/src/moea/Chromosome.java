package moea;

import java.util.ArrayList;
import java.util.HashMap;

public class Chromosome {
	
	private ArrayList<ArrayList<Pixel>> segments;
	private ArrayList<Pixel> representation;
	private ArrayList<ArrayList<Pixel>> segmentEdges;
	private HashMap<Pixel, ArrayList<Integer>> mapPixelToIndex;
	private ArrayList<Integer> pixelToSegment;
	private double fintessValue;
	private int id;

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
		
		decodeChromosome(pixels);
		long startTime = System.nanoTime();
		this.segmentEdges = HelpMethods.generateSegmentEdges(segments, pixelToSegment);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("Segment edges: "+ duration/Math.pow(10, 9) + " sec");
		
		mapPixelToIndex = HelpMethods.createMapPixelToIndex(representation);
		
		updateFitnessValue();
		System.out.println();
	}
	
	private void updateFitnessValuesForEachSegment(){
		int segmentNr = 0;
		for (ArrayList<Pixel> segment : segments) {
			double deviation = Functions.segmentDeviation(segment);
			double edgeFitness = Functions.segmentEdgeValue(segmentEdges.get(segmentNr), segment, segmentNr, pixelToSegment);
			double connectivity = Functions.segmentConnectivity(segment, segmentNr, pixelToSegment);
			double[] fintessValues = {deviation, edgeFitness, connectivity};
			segmentFitnessValues.add(fintessValues);
			segmentNr++;
		}
	}
	
	private void updateFitnessValue(){
		updateFitnessValuesForEachSegment();
		this.deviationFitness = 0;
		this.edgeFitness = 0;
		this.connectivityFitness = 0;
		this.fintessValue = 0;
		for (double[] segmentFitness : segmentFitnessValues) {
			deviationFitness += segmentFitness[0];
			edgeFitness += segmentFitness[1];
			connectivityFitness += segmentFitness[2];
		}
		this.fintessValue = Variables.deviationWeight * deviationFitness + Variables.edgeFitnessWeight * edgeFitness + Variables.connectivityWeight * connectivityFitness;
	}
	
	public void updateChromosome(){
		
	}
	
	public ArrayList<ArrayList<Pixel>> getSegments() {
		return segments;
	}

	public ArrayList<Pixel> getRepresentation() {
		return representation;
	}

	public double getFintessValue() {
		return fintessValue;
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
		System.out.println("decodeChromosome: "+ duration/Math.pow(10, 9) + " sec");
		this.segments = decodedChromosome;
	}
}
