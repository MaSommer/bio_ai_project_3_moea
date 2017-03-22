package moea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ParetoPlot {
	ArrayList<Chromosome> paretoFront;
	String pythonString="";
	
	public ParetoPlot(ArrayList<Chromosome> frontier){
		this.paretoFront = frontier;
		pythonString();
		System.out.println("  Teststreng: " +pythonString);
		int counter = 0;
		for(boolean v : Variables.activeObjectives){
			if(v){
				counter++;
			}
		}
		String path = "python /Users/Sjur/Downloads/plot"+counter+"D.py  "+ pythonString;
		
		String[] cmd = {path};
		
        String line;
        Process p;
        try {
			p = Runtime.getRuntime().exec(path);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			System.out.println(input.readLine());
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void pythonString(){
		String connectivity = "";
		String Edge = "";
		String Deviation = "";
		int counter = 1;
		for(Chromosome chr : paretoFront){
			String comma = "";
			if(counter < paretoFront.size()){
				comma = ",";
			}
			connectivity+= ""+chr.getConnectivityFitness() +comma;
			Edge+= "" + chr.getEdgeFitness() + comma;
			Deviation+= "" + chr.getDeviationFitness() + comma;
			counter++;
		}
		
		String useDev = "";
		String useEdge = "";
		String useCon = "";
		
		if(Variables.activeObjectives[0]){
			this.pythonString+=Deviation+" ";
			useDev = "Deviation ";
		}
		if(Variables.activeObjectives[1]){
			this.pythonString+=Edge+" ";
			useEdge = "Edge ";
		}
		if(Variables.activeObjectives[2]){
			this.pythonString+=connectivity+" ";
			useCon = "Connectivity ";
		}
		String labels = useDev + useEdge + useCon;
		labels.trim();
		pythonString.trim();
		this.pythonString +=" " + labels;
	}

}
