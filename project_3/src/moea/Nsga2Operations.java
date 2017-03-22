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


	public static ArrayList<Chromosome> crossover(ArrayList<Chromosome> selectedPopulation, ArrayList<Pixel> pixels){
		ArrayList<Chromosome> population = new ArrayList<Chromosome>();
		while(population.size() < Variables.pSize){
			for(int i = 0 ; i < selectedPopulation.size()-1; i+=2){
				Chromosome chr1 = selectedPopulation.get(i);
				Chromosome chr2 = selectedPopulation.get(i+1);
				double random = Math.random();
				if (random < Variables.crossoverRate){
					ArrayList<Chromosome> children = generateOffsprings(chr1, chr2, Variables.mixingRate, pixels);
					population.addAll(children);					
				}
				else{
					population.add(chr1);					
					population.add(chr2);					
				}
				
			}
			Collections.shuffle(selectedPopulation);
		}
//		selectedPopulation = population;
		return population;
	}
	
	private static ArrayList<Chromosome> generateOffsprings(Chromosome chr1, Chromosome chr2, double mixingRate, ArrayList<Pixel> pixels){
		ArrayList<Chromosome> offSprings = new ArrayList<Chromosome>();
		ArrayList<Pixel> representation1 = chr1.getRepresentation();
		ArrayList<Pixel> representation2 = chr2.getRepresentation();
		for(int i = 0 ; i < representation1.size() ; i++){
			if(Math.random() < mixingRate){
				int swap1index = representation1.get(i).getId();
				int swap2index = representation2.get(i).getId();
				
				representation2.set(i, pixels.get(swap1index));
				representation1.set(i, pixels.get(swap2index));
			}
			
		}
		chr1.updateChromosome();
		chr2.updateChromosome();
		for (ArrayList<Pixel> segment : chr1.getSegments()) {
			if (segment.size() < Variables.minimumSegmentSize){
				mutationMergeSmallest(chr1, pixels);
			}
		}
		for (ArrayList<Pixel> segment : chr2.getSegments()) {
			if (segment.size() < Variables.minimumSegmentSize){
				mutationMergeSmallest(chr2, pixels);
			}
		}
		chr1.updateChromosome();
		chr2.updateChromosome();		
		offSprings.add(chr1);
		offSprings.add(chr2);
		
		return offSprings;
	}
	


	public static void mutation(ArrayList<Chromosome> population, ArrayList<Pixel> pixels){
		double random = Math.random();
		for (Chromosome chr : population) {
			if (chr.getSegments().size() <= Variables.optimalNumberOfSegments || random > Variables.mutationRate){
				continue;
			}
			random = Math.random();
			if (random < Variables.mutationMergeAllCombinationsRate){
				mutationMergeTestAllCombinations(chr, pixels);
			}
			else{
				mutationMergeSmallest(chr, pixels);
			}
		}
	}
	
	public static void mutationMergeTestAllCombinations(Chromosome chromosome, ArrayList<Pixel> pixels){
		Comparator<double[]> rgbDistanceComperator = new Comparator<double[]>() {
			public int compare(double[] d1, double[] d2){
				if (d1[2] > d2[2]){
					return 1;
				}
				else if (d1[2] < d2[2]){
					return -1;
				}
				else {
					return 0;
				}
			}
		};
		PriorityQueue<double[]> segmentEvaluation = new PriorityQueue<double[]>(10, rgbDistanceComperator);
		ArrayList<ArrayList<Pixel>> segments = chromosome.getSegments();
		HashMap<Integer, ArrayList<Integer>> segmentDiscoveredMap = new HashMap<Integer, ArrayList<Integer>>();
		for (int i = 0; i < segments.size(); i++) {
			ArrayList<Pixel> segmentEdges = chromosome.getSegmentEdges().get(i);
			ArrayList<Integer> pixelsToSegment = chromosome.getPixelToSegment();
			for (Pixel pixel : segmentEdges) {
				for (Pixel neighbour : pixel.getNeighbours()) {
					if (segmentDiscoveredMap.get(i) != null && segmentDiscoveredMap.get(i).contains(pixelsToSegment.get(neighbour.getId()))){
						continue;
					}
					int segmentIndex1 = pixelsToSegment.get(pixel.getId());
					int segmentIndex2 = pixelsToSegment.get(neighbour.getId());
					if (segmentIndex1 != segmentIndex2){
						//{segmentNr1, segmentNr2, rgbDistance}
						double[] segmentsToMerge = new double[3];
						
						
						double[] segmentRGB1 = chromosome.getSegmentAvgRGBValues().get(segmentIndex1);
						double[] segmentRGB2 = chromosome.getSegmentAvgRGBValues().get(segmentIndex2);
						segmentsToMerge[0] = segmentIndex1;
						segmentsToMerge[1] = segmentIndex2;
						double deviationDistance = Functions.rgbDistance(segmentRGB1, segmentRGB2);
						double edgeValue1 = Functions.segmentEdgeValue(segmentEdges, segments.get(segmentIndex1), segmentIndex1, pixelsToSegment)/segmentEdges.size()*segments.get(segmentIndex1).size();
						double edgeValue2 = Functions.segmentEdgeValue(chromosome.getSegmentEdges().get(segmentIndex2), segments.get(segmentIndex2), segmentIndex2, pixelsToSegment)/chromosome.getSegmentEdges().get(segmentIndex2).size()*segments.get(segmentIndex2).size();
						
						double conectivity1 = Functions.segmentConnectivity(segments.get(segmentIndex1), segmentIndex1, pixelsToSegment);
						double conectivity2 = Functions.segmentConnectivity(segments.get(segmentIndex2), segmentIndex2, pixelsToSegment);
						
						segmentsToMerge[2] = deviationDistance ;
						
						segmentEvaluation.add(segmentsToMerge);
						if (segmentDiscoveredMap.get(i) == null){
							ArrayList<Integer> segmentIndexes = new ArrayList<Integer>();
							segmentIndexes.add(segmentIndex2);
							segmentDiscoveredMap.put(i, segmentIndexes);
						}
						else{
							segmentDiscoveredMap.get(i).add(segmentIndex2);
						}
					}
				}
			}
		}
		double[] segmentsToMerge = segmentEvaluation.poll();
		ArrayList<Pixel> segment1 = segments.get((int)(segmentsToMerge[0]));
		ArrayList<Pixel> segment2 = segments.get((int)(segmentsToMerge[1]));
		chromosome.mergeSegments(segment1, segment2);		
	}

	public static void mutationMergeSmallest(Chromosome chromosome, ArrayList<Pixel> pixels){
		//		System.out.println("was mutated: " + chromosome.getId());
		ArrayList<ArrayList<Pixel>> segments = chromosome.getSegments();
		double prob = Math.random();
		int choice = -1;
		if (prob < 0.5){
			int minSegmentSize = Integer.MAX_VALUE;
			choice = -1;
			for (int i = 0; i < segments.size(); i++) {
				if(segments.get(i).size() < minSegmentSize){
					minSegmentSize = segments.get(i).size();
					choice = i;
				}	
			}	
		}
		else{
			choice = (int)(Math.random()*segments.size());
			while (segments.get(choice).size() < Variables.maxmimumSegmentSizeForMutationMerge){
				choice = (int)(Math.random()*segments.size());			
			}
		}
		ArrayList<Pixel> segmentEdges = chromosome.getSegmentEdges().get(choice);
		ArrayList<Pixel> representation = chromosome.getRepresentation();

		double[] segmentRGB = chromosome.getSegmentAvgRGBValues().get(choice);
		double bestRGBDistace = Double.POSITIVE_INFINITY;
//		int pixelToMerge = -1;
		int bestSegmentToMerge = -1;
		//finds the lowest deviation distance
		for (Pixel pixel : segmentEdges) {
			//tests if pixel points to itself
			for (Pixel neighbour : pixel.getNeighbours()) {
				int neighbourSegment = chromosome.getPixelToSegment().get(neighbour.getId());
				if (choice != neighbourSegment){
					double[] neighbourSegmentRGB = chromosome.getSegmentAvgRGBValues().get(neighbourSegment);
					double rgbDistance = Functions.rgbDistance(segmentRGB, neighbourSegmentRGB);
					if (rgbDistance < bestRGBDistace){
						bestRGBDistace = rgbDistance;
//						pixelToMerge = pixel.getId();
						bestSegmentToMerge = neighbourSegment;
					}
				}
			}
			if (representation.get(pixel.getId()) == pixel){
			}
		}
		ArrayList<Pixel> segment1 = segments.get(choice);
		ArrayList<Pixel> segment2 = segments.get(bestSegmentToMerge);
		chromosome.mergeSegments(segment1, segment2);
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
