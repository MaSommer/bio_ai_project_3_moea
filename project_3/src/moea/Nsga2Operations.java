package moea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;


public class Nsga2Operations {
	
	public static ArrayList<Chromosome> selection(ArrayList<Chromosome> population){
		ArrayList<Chromosome> selectedChromosome = new ArrayList<Chromosome>();
		HashMap<Integer, ArrayList<Chromosome>> frontierMap = fastNonDominatedSort(population);
		crowdingDistanceAssignment(frontierMap);
		Iterator it = frontierMap.entrySet().iterator();
		while(it.hasNext() && selectedChromosome.size() < population.size()){
			Map.Entry pair = (Map.Entry)it.next();
			//sorts after highest distance
			Collections.sort((ArrayList<Chromosome>) pair.getValue(), new Comparator<Chromosome>() {
				public int compare(Chromosome chr1, Chromosome chr2) {
					return -Double.compare(chr1.getFitnessValue(), chr2.getFitnessValue());
				}
			});
			ArrayList<Chromosome> chromosomesFromFronteie = (ArrayList<Chromosome>) pair.getValue();
			int i = 0;
			while (i < chromosomesFromFronteie.size() && selectedChromosome.size() < population.size()) {
				selectedChromosome.add(chromosomesFromFronteie.get(i));
				i++;
			}
		}
		return selectedChromosome;
	}
	
	
	public static ArrayList<Chromosome> crossover(ArrayList<Chromosome> population){
		
		return population;
	}
	

	public ArrayList<Chromosome> singleCrossover(Chromosome chr1, Chromosome chr2){
		Chromosome child1 = new Chromosome(chr1);
		Chromosome child2 = new Chromosome(chr2);
		ArrayList<Chromosome> children = new ArrayList<Chromosome>();
		
		children.add(child1);
		children.add(child2);
		
		//select pixel to look at
		int pixelIndex = (int) Math.random() * chr1.getRepresentation().size();
		
		//Find segment ids of the selected pixel in each chromosome
		int segment1Index = child1.getPixelToSegment().get(pixelIndex);
		int segment2Index = child2.getPixelToSegment().get(pixelIndex);
		
		//Find the segments
		ArrayList<Pixel> segment1 = child1.getSegments().get(segment1Index);
		ArrayList<Pixel> segment2 = child2.getSegments().get(segment2Index);
		
		ArrayList<Pixel> toBeInsertedAgain = new ArrayList<Pixel>();
		//Find Pixels that are in both segments
		ArrayList<double[]> c1RGB = child1.getSegmentAvgRGBValues();
		ArrayList<double[]> c2RGB = child2.getSegmentAvgRGBValues();
		
		//take RGB to totals instead of averages
		for(int i = 0 ; i < 3 ; i++){
			c1RGB.get(segment1Index)[i] = c1RGB.get(segment1Index)[i] * segment1.size();
			c2RGB.get(segment2Index)[i] = c2RGB.get(segment2Index)[i] * segment2.size();
		}
		
		//Find common pixels in both segments, remove RGB contribution of selected pixels
		for(Pixel p: segment1){
			if(segment2.contains(p)){
				toBeInsertedAgain.add(p);
				c1RGB.get(segment1Index)[0] -= p.getRed();
				c1RGB.get(segment1Index)[1] -= p.getGreen();
				c1RGB.get(segment1Index)[2] -= p.getBlue();
				c2RGB.get(segment2Index)[0] -= p.getRed();
				c2RGB.get(segment2Index)[1] -= p.getGreen();
				c2RGB.get(segment2Index)[2] -= p.getBlue();
				
			}
		}
		
		//update average RGB with the new size of the segments
		for(int i = 0 ; i < 3 ; i++){
			c1RGB.get(segment1Index)[i] = c1RGB.get(segment1Index)[i] / (segment1.size()-toBeInsertedAgain.size());
			c2RGB.get(segment2Index)[i] = c2RGB.get(segment2Index)[i] / (segment2.size() - toBeInsertedAgain.size());
		}
		
		ArrayList<Pixel> child1Repr = child1.getRepresentation();
		ArrayList<Pixel> child2Repr = child2.getRepresentation();
		//Set all pointers from the selected pixels equal to null and 
		for(Pixel p: toBeInsertedAgain){
			int id = p.getId();
			child1Repr.set(p.getId(), null);
			child2Repr.set(p.getId(), null);
		}
		//Find those pixels that are on the edge and add these edges to a priorityQueue
		Comparator<Edge> edgeComparator = new Comparator<Edge>() {
			public int compare(Edge e1, Edge e2){
				if (e1.getWeight() > e2.getWeight()){
					return 1;
				}
				else if (e1.getWeight() < e2.getWeight()){
					return -1;
				}
				else {
					return 0;
				}
			}
		};
		PriorityQueue<Edge> edgesFrom1 = new PriorityQueue<Edge>(10, edgeComparator);
		PriorityQueue<Edge> edgesFrom2 = new PriorityQueue<Edge>(10, edgeComparator);
		
		for(Pixel p: toBeInsertedAgain){
			ArrayList<Pixel> neighbours = p.getNeighbours();
			ArrayList<Double> neighbourDistances = p.getNeighbourDistances();
			for(int i = 0 ; i < neighbours.size() ; i++){
				if(!toBeInsertedAgain.contains(neighbours.get(i))){
					int neighbour1Segment = child1.getPixelToSegment().get(neighbours.get(i).getId());
					int neighbour2Segment = child2.getPixelToSegment().get(neighbours.get(i).getId());
					double neighbour1Dev = Functions.pixelDeviation(c1RGB.get(neighbour1Segment), p);
					double neighbour2Dev = Functions.pixelDeviation(c2RGB.get(neighbour2Segment), p);
					neighbour1Dev += neighbourDistances.get(i);
					neighbour2Dev += neighbourDistances.get(i);
					Edge newEdge1 = new Edge(p, neighbours.get(i), neighbour1Dev);
					
				}
			}
		}
		
	
		
	}
	
	public static void mutation(Chromosome chromosome, ArrayList<Pixel> pixels){
		double random = Math.random();
		for (ArrayList<Pixel> segment : chromosome.getSegments()) {
			boolean point = false;
			for (Pixel pixel : segment) {
				if (chromosome.getRepresentation().get(pixel.getId()).getId() == pixel.getId()){
					point = true;
					break;
				}
			}
			if (point == false){
				throw new IllegalArgumentException("Mutation, Chr: " + chromosome.getId() + " Segmentsize: " + segment.size());
			}
		}
		if (random < Variables.mutationMergeRate){
			mutationMerge(chromosome, pixels);
		}
		else{
			mutationSplit(chromosome, pixels);
		}
	}
	
	public static void mutationMerge(Chromosome chromosome, ArrayList<Pixel> pixels){
//		System.out.println("was mutated: " + chromosome.getId());
		ArrayList<ArrayList<Pixel>> segments = chromosome.getSegments();
		int randomSegment = (int)(Math.random()*segments.size());
		while (segments.get(randomSegment).size() < Variables.maxmimumSegmentSizeForMutationMerge){
			randomSegment = (int)(Math.random()*segments.size());			
		}
		ArrayList<Pixel> segmentEdges = chromosome.getSegmentEdges().get(randomSegment);
		ArrayList<Pixel> representation = chromosome.getRepresentation();
		
		double[] segmentRGB = chromosome.getSegmentAvgRGBValues().get(randomSegment);
		double bestRGBDistace = Double.POSITIVE_INFINITY;
		int pixelToMerge = -1;
		int neighbourPixelToMerge = -1;
		int bestSegmentToMerge = -1;
		//finds the lowest deviation distance
		for (Pixel pixel : segmentEdges) {
			//tests if pixel points to itself
			if (representation.get(pixel.getId()) == pixel){
				for (Pixel neighbour : pixel.getNeighbours()) {
					int neighbourSegment = chromosome.getPixelToSegment().get(neighbour.getId());
					if (randomSegment != neighbourSegment){
						double[] neighbourSegmentRGB = chromosome.getSegmentAvgRGBValues().get(neighbourSegment);
						double rgbDistance = Functions.rgbDistance(segmentRGB, neighbourSegmentRGB);
						if (rgbDistance < bestRGBDistace){
							bestRGBDistace = rgbDistance;
							pixelToMerge = pixel.getId();
							neighbourPixelToMerge = neighbour.getId();
							bestSegmentToMerge = neighbourSegment;
						}
					}
				}
			}
		}
		boolean point = false;
		for (Pixel p : segments.get(randomSegment)) {
			if (representation.get(p.getId()).getId() == p.getId()){
				point = true;
//				System.out.println(point);
			}
		}
		if (pixelToMerge == -1){
			return;
		}
		int bestNeighbourToMerge = -1;
		double bestEdgeValue = Double.POSITIVE_INFINITY;
		
		//choose to merge with the lowest edge value 
		for (Pixel neighbour : pixels.get(pixelToMerge).getNeighbours()) {
			int neighbourSegment = chromosome.getPixelToSegment().get(neighbour.getId());
			double edgeValue = Functions.pixelToPixelDeviation(pixels.get(pixelToMerge), neighbour);
			if (edgeValue < bestEdgeValue && neighbourSegment != chromosome.getPixelToSegment().get(pixelToMerge) && bestSegmentToMerge == neighbourSegment){
				bestEdgeValue = edgeValue;
				bestNeighbourToMerge = neighbour.getId();
			}
		}
		if (pixelToMerge != -1 && neighbourPixelToMerge != -1 && bestRGBDistace < 50){
			chromosome.getRepresentation().set(pixelToMerge, pixels.get(bestNeighbourToMerge));
			chromosome.updateChromosome();
		}
		
	}
	
	public static void mutationSplit(Chromosome chromosome, ArrayList<Pixel> pixels){
		HashMap<Pixel, ArrayList<Integer>> mapPixelsThatPointsOnPixel = HelpMethods.createMapPixelToIndex(chromosome.getRepresentation());
		ArrayList<ArrayList<Pixel>> segments = chromosome.getSegments();
		//Choosing the biggest segment to do a split
		int randomSegment = (int)(Math.random()*segments.size());
		ArrayList<Pixel> segment = segments.get(randomSegment);
		int minSegmentSize = 10;
		int pixelIdToCut = HelpMethods.cutIntoTwoSegments(chromosome.getRepresentation(), pixels, mapPixelsThatPointsOnPixel, segment, minSegmentSize);
		if (pixelIdToCut != -1){
			chromosome.getRepresentation().set(pixelIdToCut, pixels.get(pixelIdToCut));			
		}
		chromosome.updateChromosome();
	}
	
	
	public static void crowdingDistanceAssignment(HashMap<Integer, ArrayList<Chromosome>> frontierMap){
		Iterator it = frontierMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			ArrayList<Chromosome> chromosomes = (ArrayList<Chromosome>) pair.getValue();
			if (chromosomes.size() == 0){
				break;
			}
			for (int i = 0; i < chromosomes.size(); i++) {
				chromosomes.get(i).clearFitness();
			} 
			//for each active objective {deviation, edgevalue, connectivity}, this is specified in variables
			for (int i = 0; i < Variables.activeObjectives.length; i++) {
				if (Variables.activeObjectives[i]){
					sortArrayListsInFrontierMap(i, frontierMap);
					chromosomes.get(0).addObjectiveDistance(Double.MAX_VALUE-2);
					chromosomes.get(chromosomes.size()-1).addObjectiveDistance(Double.MAX_VALUE-2);
					double fMin = getActiveObjective(i, chromosomes.get(0));
					double fMax = getActiveObjective(i, chromosomes.get(chromosomes.size()-1));
					double denominator = fMax - fMin;
					for (int j = 1; j < chromosomes.size()-1; j++) {
						double nominator = getActiveObjective(i, chromosomes.get(j+1))-getActiveObjective(i, chromosomes.get(j-1));
						double insertValue = nominator/denominator;
						chromosomes.get(j).addObjectiveDistance(insertValue);
					}	    							
				}
			}
		}
	}
	
	public static double getActiveObjective(int i, Chromosome chr){
		if (i == 0){
			return chr.getDeviationFitness();
		}
		else if (i == 1){
			return chr.getEdgeFitness();
		}
		else {
			return chr.getConnectivityFitness();
		}
	}
	
	public static void sortArrayListsInFrontierMap(int i, HashMap<Integer, ArrayList<Chromosome>> frontierMap){
		Iterator it = frontierMap.entrySet().iterator();
		if (i == 0){
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				Collections.sort((ArrayList<Chromosome>) pair.getValue(), new Comparator<Chromosome>() {
					public int compare(Chromosome chr1, Chromosome chr2) {
						
						return Double.compare(chr1.getDeviationFitness(), chr2.getDeviationFitness());
					}
				});
			}
		}
		else if ( i == 1){
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				Collections.sort((ArrayList<Chromosome>) pair.getValue(), new Comparator<Chromosome>() {
					public int compare(Chromosome chr1, Chromosome chr2) {
						
						return Double.compare(chr1.getEdgeFitness(), chr2.getEdgeFitness());
					}
				});;
			}
		}
		else {
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				Collections.sort((ArrayList<Chromosome>) pair.getValue(), new Comparator<Chromosome>() {
					public int compare(Chromosome chr1, Chromosome chr2) {
						
						return Double.compare(chr1.getConnectivityFitness(), chr2.getConnectivityFitness());
					}
				});;
			}
		}
	}




	public static HashMap<Integer,ArrayList<Chromosome>> fastNonDominatedSort(ArrayList<Chromosome> population){
		HashMap<Integer,ArrayList<Chromosome>> frontiers = new HashMap<Integer,ArrayList<Chromosome>>();
		HashMap<Chromosome,Integer> dominationCount= new HashMap<Chromosome,Integer>();
		HashMap<Chromosome,ArrayList<Chromosome>> dominates = new HashMap<Chromosome, ArrayList<Chromosome>>();
		

		for(Chromosome p:population){
			ArrayList<Chromosome> pDominates = new ArrayList<Chromosome>();
			Integer pDominationCount = 0;
			for(Chromosome q : population){
				if(p.equals(q)){
					continue;
				}
				Chromosome dominator = getDominator(p, q);
				if(dominator != null && dominator.equals(p)){
					pDominates.add(q);
				}
				if(dominator != null && dominator.equals(q)){
					pDominationCount++;
				}
			}
			dominates.put(p, pDominates);
			dominationCount.put(p,pDominationCount);
			if(pDominationCount==0){
				if(!frontiers.containsKey(1)){
					ArrayList<Chromosome> frontier1 = new ArrayList<Chromosome>();
					frontiers.put(1,frontier1);
				}
				frontiers.get(1).add(p);
			}
		}
		int i = 1;
		while(frontiers.get(i).size() !=0){
			ArrayList<Chromosome> nextFront = new ArrayList<Chromosome>();
			ArrayList<Chromosome> currentFrontier = frontiers.get(i);
			for(Chromosome p:currentFrontier){
				ArrayList<Chromosome> Q = dominates.get(p);
				for(Chromosome q:Q){
					int domCount = 0;
					if(getDominator(p,q).equals(p)){
						domCount = dominationCount.get(q);
						domCount-=1;
						dominationCount.put(q, domCount);
					}
					if(domCount ==0){
						nextFront.add(q);
					}
				}
			}
			i++;
			frontiers.put(i, nextFront);
		}
		return frontiers;

	}


	private static Chromosome getDominator(Chromosome c1, Chromosome c2){
		Chromosome dominator = null;
		double[] fitnesses1 = {c1.getDeviationFitness(), c1.getEdgeFitness(), c1.getConnectivityFitness()};
		double[] fitnesses2 = {c2.getDeviationFitness(), c2.getEdgeFitness(), c2.getConnectivityFitness()};
		int counter = 0;
		for(int i = 0 ; i < 3 ; i++){
			if(Variables.activeObjectives[i]){
				counter++;
			}
		}
		
		int fitness1IsBigger = 0;
		int fitness2IsBigger = 0;

		for(int i = 0 ; i < fitnesses1.length ; i++){
			if((fitnesses1[i] >= fitnesses2[i]) && Variables.activeObjectives[i]){
				fitness1IsBigger++;
			}
		}
		for(int i = 0 ; i < fitnesses1.length ; i++){
			if((fitnesses1[i] <= fitnesses2[i]) && Variables.activeObjectives[i]){
				fitness2IsBigger++;
			}
		}

		if(fitness1IsBigger == counter){
			dominator = c2;
		}
		if(fitness2IsBigger == counter){
			dominator = c1;
		}
		
		return dominator;

	}
//	public static void main(String[] args) {
//		ArrayList<Chromosome> pop = Chromosome.testCrowdChromosomes();
//		ArrayList<Chromosome> empty = new ArrayList<Chromosome>();
//		HashMap<Integer,ArrayList<Chromosome>> map = new HashMap<Integer,ArrayList<Chromosome>>();
//		map.put(1, pop);
//		map.put(2, empty);
//		crowdingDistanceAssignment(map);
//		System.out.println(map.get(1));
//		Collections.sort(pop, new Comparator<Chromosome>() {
//			public int compare(Chromosome chr1, Chromosome chr2) {
//				return -Double.compare(chr1.getFitnessValue(), chr2.getFitnessValue());
//			}
//		});
//		System.out.println(map.get(1));
//		for(int i = 1 ; i < map.size()+1; i++){
//		}
		
//		Nsga2Operations.crowdingDistanceAssignment(map);
//		ArrayList<Chromosome> toPrint = map.get(1);
//		System.out.println(toPrint);
//		
//	}


}
