package moea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Nsga2Operations {
	
	
	public static void corwdingDistanceAssignment(HashMap<Integer, ArrayList<Chromosome>> frontierMap){
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
						double denominator = getActiveObjective(i, chromosomes.get(chromosomes.size()-1)) - getActiveObjective(i, chromosomes.get(0));
						double insertValue = distances.get(j) + nominator/denominator;
						distances.set(j, insertValue);
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
				});;
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

}
