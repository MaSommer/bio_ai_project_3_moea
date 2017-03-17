package moea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Nsga2Operations {
	
	public static ArrayList<Chromosome> selection(ArrayList<Chromosome> population){
		ArrayList<Chromosome> selectedChromosome = new ArrayList<Chromosome>();
		HashMap<Integer, ArrayList<Chromosome>> frontierMap = fastNonDominatedSort(population);
		crowdingDistanceAssignment(frontierMap);
		Collections.shuffle(population);
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
	
	
	public static void crowdingDistanceAssignment(HashMap<Integer, ArrayList<Chromosome>> frontierMap){
		Iterator it = frontierMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			ArrayList<Chromosome> chromosomes = (ArrayList<Chromosome>) pair.getValue();
			ArrayList<Double> distances = new ArrayList<Double>();
			for (int i = 0; i < chromosomes.size(); i++) {
				distances.add(0.0);				
			} 
			//for each active objective {deviation, edgevalue, connectivity}, this is specified in variables
			for (int i = 0; i < Variables.activeObjectives.length; i++) {
				if (Variables.activeObjectives[i]){
					sortArrayListsInFrontierMap(i, frontierMap);
					distances.set(0, Double.MAX_VALUE);
					distances.set(distances.size()-1, Double.MAX_VALUE);
					for (int j = 1; j < distances.size()-1; j++) {
						double nominator = getActiveObjective(i, chromosomes.get(j+1))-getActiveObjective(i, chromosomes.get(j+1));
						double fMin = getActiveObjective(i, chromosomes.get(0));
						double fMax = getActiveObjective(i, chromosomes.get(chromosomes.size()-1));
//						double fMin = getActiveObjective(i, frontierMap.get(1).get(0));
//						double fMax = getActiveObjective(i, frontierMap.get(frontierMap.size()-1).get(frontierMap.get(frontierMap.size()-1).size()-1));
						double denominator = fMax - fMin;
						double insertValue = distances.get(j) + nominator/denominator;
						distances.set(j, insertValue);
					}	    							
				}
			}
			int index = 0;
			for (Chromosome chr : chromosomes) {
				chr.setFitnessValue(distances.get(index));
				index++;
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
				Chromosome dominator = getDominator(p ,q);
				if(dominator.equals(p)){
					pDominates.add(q);
				}
				if(dominator.equals(q)){
					pDominationCount++;
				}
			}
			dominates.put(p, pDominates);
			dominationCount.put(p,pDominationCount);
			
			if(pDominationCount==0){
				if(!frontiers.containsKey(1)){
					ArrayList<Chromosome> frontier1 = new ArrayList<Chromosome>();
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
					int domCount = dominationCount.get(q);
					domCount-=1;
					dominationCount.put(q, domCount);
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

		for(int i = 0 ; i < fitnesses1.length ; i++){
			if((fitnesses1[i] > fitnesses2[i]) && Variables.activeObjectives[i]){
				fitness1IsBigger++;
			}
		}

		if(fitness1IsBigger == counter){
			return c2;
		}
		if(fitness1IsBigger == 0){
			return c1;
		}

		return dominator;

	}
	public static void main(String[] args) {
		ArrayList<Pixel> repr = new ArrayList<Pixel>();
		ArrayList<Chromosome> pop = Chromosome.testCrowdChromosomes();
		ArrayList<Chromosome> empty = new ArrayList<Chromosome>();
		HashMap<Integer,ArrayList<Chromosome>> map = new HashMap<Integer, ArrayList<Chromosome>>();
		map.put(1, pop);
		map.put(2, empty);
		Nsga2Operations.crowdingDistanceAssignment(map);
		ArrayList<Chromosome> toPrint = map.get(1);
		System.out.println(toPrint);
		
	}


}
