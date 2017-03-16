package moea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Nsga2Operations {




	public HashMap<Integer,ArrayList<Chromosome>> fastNonDominatedSort(ArrayList<Chromosome> population){
		HashMap<Integer,ArrayList<Chromosome>> frontiers = new HashMap<Integer,ArrayList<Chromosome>>();
		HashMap<Chromosome,Integer> dominationCount= new HashMap<Chromosome,Integer>();
		HashMap<Chromosome,ArrayList<Chromosome>> dominates = new HashMap<Chromosome, ArrayList<Chromosome>>();
		HashMap<Chromosome,Integer> rank = new HashMap<Chromosome,Integer>();
		

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
				rank.put(p, 1);
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
						rank.put(q, i+1);
					}
				}
			}
			i++;
			frontiers.put(i, nextFront);
		}
		return frontiers;

	}


	private Chromosome getDominator(Chromosome c1, Chromosome c2){
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


}
