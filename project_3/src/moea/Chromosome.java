package moea;

import java.util.ArrayList;
import java.util.HashMap;

public class Chromosome {
	
	private ArrayList<ArrayList<Pixel>> segments;
	private ArrayList<Pixel> representation;
	private ArrayList<ArrayList<Pixel>> segmentEdges;
	private double fintessValue;

	private double deviationFitness;
	private double edgeFitness;
	private double connectivityFitness;
 	
	//mapper segmentnummer til fitnessvalues {deviation, edges, connectivity}
	private ArrayList<double[]> segmentFitnessValues;
	
	public Chromosome(ArrayList<Pixel> representation, ArrayList<Pixel> pixels){
		this.representation = representation;
		this.segments = HelpMethods.decodeChromosome(representation, pixels);
		this.segmentEdges = HelpMethods.generateSegmentEdges(segments);
		segmentFitnessValues = new ArrayList<double[]>();
		updateFitnessValuesInitially();
		updateFitnessValue();
	}
	
	private void updateFitnessValuesInitially(){
		int segmentNr = 0;
		for (ArrayList<Pixel> segment : segments) {
			double deviation = Functions.segmentDeviation(segment);
			double edgeFitness = Functions.segmentEdgeValue(segmentEdges.get(segmentNr), segment);
			double connectivity = Functions.segmentConnectivity(segment);
			double[] fintessValues = {deviation, edgeFitness, connectivity};
			segmentFitnessValues.add(fintessValues);
			segmentNr++;
		}
	}
	
	private void updateFitnessValue(){
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
}
