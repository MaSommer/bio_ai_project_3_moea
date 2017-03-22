package moea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Program {
	private ArrayList<Pixel> pixels;
	private ArrayList<Chromosome> population;
	private ArrayList<ArrayList<Pixel>> image;
	
	private long totalStartTime;
	
	//Key: pixel id, Value: the pixels that point
	private int pSize;
	private String imagePath;
	private ArrayList<Pixel> MST;
	private ArrayList<double[]> distances;
	public static int[][] colors = {	{246, 14, 14, 180},
										{246, 169, 14, 180},
										{246, 246, 14, 180},
										{114, 246, 14, 180},
										{14, 246, 184, 180},
										{14, 200, 246, 180},
										{14, 107, 246, 180},
										{14, 107, 246, 180},
										{14, 21, 246, 180},
										{200, 14, 246, 180},
										{246, 14, 184, 180},
										{246, 14, 99, 180},
										{246, 14, 14, 180},
										{0, 0, 0, 180},
										{131, 119, 119, 180},
										{220, 129, 129, 180},
										{229, 204, 255, 180},
										{0, 153, 0, 180},
										{153, 0, 76, 180}       };
	
	
	
	
	
	
	public Program(String imagePath) {
		super();
		this.totalStartTime = System.nanoTime();
		this.pSize = Variables.pSize;
		this.image = new ArrayList<ArrayList<Pixel>>();
		this.pixels = new ArrayList<Pixel>();
		this.imagePath = imagePath;

	}
	
	public static void paintSegments(Chromosome c){
		int counter = 0;
		for(ArrayList<Pixel> segment:c.getSegments()){
			for(Pixel p : segment){
				p.setColor(colors[counter%colors.length]);
			}
			counter++;
		}
	}
	
	public double calculateNeuronDistance(double[] values, Pixel p){
		double redDist = Math.pow(p.getRed()-values[0], 2);
		double greenDist = Math.pow(p.getGreen()-values[1], 2);
		double blueDist = Math.pow(p.getBlue()-values[2], 2);
		return Math.pow(redDist+blueDist+greenDist, 0.5);
	}
	
	public ArrayList<ArrayList<Pixel>> paintWithKmeans(int segments){
		double[][] neurons = new double[segments][3];
		double red = 0;
		double blue = 0;
		double green = 0;
		for(Pixel p: pixels){
			red+= p.getRed();
			blue+=p.getBlue();
			green+=p.getGreen();
		}
		red=red/pixels.size();
		green = green/pixels.size();
		blue = blue/pixels.size();
		
		for(double[] values:neurons){
			values[0] = red;
			values[1] = green;
			values[2] =blue;
		}
		
		for(Pixel p: pixels){
			double minDist = 1000000;
			int index = -1;
			for(int i = 0; i < neurons.length ; i++){
				if(calculateNeuronDistance(neurons[i] , p) <= minDist){
					index = i;
					minDist = calculateNeuronDistance(neurons[i] , p);
					
				}
			}
			neurons[index][0] += 0.5*(p.getRed() - neurons[index][0]);
			neurons[index][1] += 0.5*(p.getGreen() - neurons[index][1]);
			neurons[index][2] += 0.5*(p.getBlue() - neurons[index][2]);
			
		}
		ArrayList<ArrayList<Pixel>> segmentedPixels = new ArrayList<ArrayList<Pixel>>();
		for(int i = 0 ; i < neurons.length ; i++){
			segmentedPixels.add(new ArrayList<Pixel>());
		}
		for(Pixel p:pixels){
			double minDist = 100000;
			int index = -1;
			for(int i = 0 ; i < neurons.length ; i++){
				if(calculateNeuronDistance(neurons[i], p) <= minDist){
					index = i;
					minDist = calculateNeuronDistance(neurons[i], p);
				}
			}
			segmentedPixels.get(index).add(p);
		}
//		for(double[] neuron:neurons){
//			System.out.println(Arrays.toString(neuron));
//		}
		return segmentedPixels;
			
	}
	
	public Chromosome encode(ArrayList<ArrayList<Pixel>> segmentedPixels, int id){
		ArrayList<Pixel> workingCopy = (ArrayList<Pixel>) MST.clone();
		
		
		for(ArrayList<Pixel> segment: segmentedPixels){
			for(Pixel p: segment){
				Pixel pointsTo = MST.get(p.getId());
				if(!segment.contains(pointsTo)){
					workingCopy.set(p.getId(), p);
				}
			}
		}
		
		if(workingCopy.contains(null)){
			System.out.println("SKADA");
		}
		
		Chromosome individual = new Chromosome(workingCopy, pixels, id, distances, this.image);
		if(individual.getRepresentation().contains(null)){
			System.out.println("Bullshiot");
		}
		int counter = 0;
//		while(individual.getSegments().size() > Variables.optimalNumberOfSegments){
//			Nsga2Operations.mutation(individual, pixels);
//		}
		individual.updateChromosome();
		return individual;
	}

	
	public void setDistances() {
		ArrayList<double[]> distances = new ArrayList<double[]>();
		int eastBorder = image.get(0).size()-1;
		int southBorder = image.size()-1;
		int north = 0;
		int south = 0;
		int east = 0 ;
		int west = 0;
		for(int i = 0 ; i < southBorder+1; i++){
			for(int j = 0 ; j < eastBorder+1 ; j++){
				int index = i*southBorder + j;
				if((i!=0) && (i!=southBorder) && (j!=0) && (j!=eastBorder)){
					double[] dist = {-1,-1,-1,-1};
					dist[0] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i-1).get(j)); //NORTH
					dist[1] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i+1).get(j)); //SOUTH
					dist[2] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i).get(j+1)); //EAST
					dist[3] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i).get(j-1));//WEST
					distances.add(dist);
				}
				
				else if((i == 0) && (j==0)){ //NORTH-WEST corner
					double[] dist = {-1,-1,-1,-1};
					dist[1] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i+1).get(j)); //SOUTH
					dist[2] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i).get(j+1)); //EAST
					distances.add(dist);
					
				}
				else if((i == southBorder) && (j==0)){ //SOUTH WEST corner
					double[] dist = {-1,-1,-1,-1};
					dist[0] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i-1).get(j)); //NORTH
					dist[2] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i).get(j+1)); //EAST
					distances.add(dist);
				}
				
				else if((i == 0) && (j == eastBorder)){ //NORTH EAST Corner
					double[] dist = {-1,-1,-1,-1};
					dist[1] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i+1).get(j)); //SOUTH
					dist[3] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i).get(j-1));//WEST
					distances.add(dist);
				}
				else if((i == southBorder) && (j == eastBorder)){ //SOUTH EAST corner
					double[] dist = {-1,-1,-1,-1};
					dist[0] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i-1).get(j)); //NORTH
					dist[3] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i).get(j-1));//WEST
					distances.add(dist);
					
				}
				else if(i == 0){ //NORTH BORDER
					double[] dist = {-1,-1,-1,-1};
					dist[1] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i+1).get(j)); //SOUTH
					dist[2] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i).get(j+1)); //EAST
					dist[3] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i).get(j-1));//WEST
					distances.add(dist);
					north++;
					
				}
				else if(i == southBorder){ //SOUTH BORDER
					double[] dist = {-1,-1,-1,-1};
					dist[0] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i-1).get(j)); //NORTH
					dist[2] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i).get(j+1)); //EAST
					dist[3] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i).get(j-1));//WEST
					distances.add(dist);
					south++;
				}
				
				else if(j == 0){ //WEST BORDER
					double[] dist = {-1,-1,-1,-1};
					dist[0] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i-1).get(j)); //NORTH
					dist[1] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i+1).get(j)); //SOUTH
					dist[2] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i).get(j+1)); //EAST
					distances.add(dist);
					west++;
				}
				
				else if(j == eastBorder){ //EastBorder
					double[] dist = {-1,-1,-1,-1};
					dist[0] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i-1).get(j)); //NORTH
					dist[1] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i+1).get(j)); //SOUTH
					dist[3] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i).get(j-1));//WEST
					distances.add(dist);
					east++;
				}
					
			}
		}
		this.distances = distances;
	}

	
	public void createMST(){
		long startTime = System.nanoTime();
		ArrayList<Pixel> MST = new ArrayList<Pixel>();
		for(int i = 0 ; i < pixels.size(); i++){
			MST.add(null);
		}
		int pixelsRemaining = pixels.size()-1;
		
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
		PriorityQueue<Edge> edges = new PriorityQueue<Edge>(10, edgeComparator);
		int currentPixel = 0;
		MST.set(0, pixels.get(0));
		
		while(pixelsRemaining > 0) {
			
			ArrayList<Edge> currentPixelsEdges = new ArrayList<Edge>();
			int toPixelIndex = currentPixel;
			for(int j = 0 ; j < 4 ; j++){
				double distance = distances.get(currentPixel)[j];
				if(distance == -1){
					continue;
				}
				else{
					int fromPixelIndex = -1;
					if(j == 0){
						fromPixelIndex = currentPixel - image.get(0).size(); //NORTH pixel
					}
					else if(j == 1){
						fromPixelIndex = currentPixel+ image.get(0).size(); //SOUTH pixel
					}
					else if(j == 2){
						fromPixelIndex = currentPixel +1;//EAST pixel
					}
					else if(j == 3) {
						fromPixelIndex = currentPixel- 1; //WEST
					}
					Pixel fromPixel = pixels.get(fromPixelIndex);
					Pixel toPixel = pixels.get(currentPixel);
					
					if(MST.get(fromPixelIndex) == null){
						currentPixelsEdges.add(new Edge(fromPixel, toPixel, distance));
						currentPixelsEdges.add(new Edge(toPixel, fromPixel, distance));
					}
				}
			}
			for(Edge e : currentPixelsEdges){
				edges.add(e);
			}
			while(true){
				Edge e = edges.poll();
				
				if(MST.get(e.getFrom().getId()) == null){
//					System.out.println("Used edge: " + e);
					Pixel fromPixel = e.getFrom();
					Pixel toPixel = e.getTo();
					MST.set(fromPixel.getId(), toPixel);
					pixelsRemaining--;
					currentPixel = fromPixel.getId();
					break;
				}

			}
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("create MST: "+ duration/Math.pow(10, 9) + " sec");
		this.MST = MST;
	}


	public void init() throws IOException{
		Pixel[][] pixels1 = HelpMethods.createImagePixelByPixel(imagePath);
		this.pixels = HelpMethods.generatePixelList(pixels1);
		//Refers to the pixel with id as same as the key
		this.image = HelpMethods.generateImage(pixels1);
		setDistances();
		createMST();
//		ArrayList<Pixel> mstPixels = HelpMethods.minimumSpanningTree2(pixels);
		
//		this.population = HelpMethods.createPopulation(MST, pixels, image, distances);
//		for (int i = 0; i < Variables.pSize/2; i++) {
//			long startTime = System.nanoTime();
//			ArrayList<ArrayList<Pixel>> segmentedPixels = paintWithKmeans(10);
//			population.add(encode(segmentedPixels, 30, i*10));
//			long endTime = System.nanoTime();
//			long duration = endTime - startTime;
//			System.out.println("Duration: " + duration/Math.pow(10, 9) + " sec");
//		}
		this.population = HelpMethods.createPopulation(MST, pixels, image, distances);
		
//		for (int i = 0; i < Variables.pSize/2; i++) {
//			long startTime = System.nanoTime();
//			ArrayList<ArrayList<Pixel>> segmentedPixels = paintWithKmeans(10);
//			population.add(encode(segmentedPixels, 30));
//			long endTime = System.nanoTime();
//			long duration = endTime - startTime;
//			System.out.println("Duration: " + duration/Math.pow(10, 9) + " sec");
//		}
	}
	
	public void run(){
		long startTime = System.nanoTime();
		int generations = 1;
		for (int i = 0; i < Variables.numberOfGenerations; i++) {
			population = Nsga2Operations.selection(population);
//			population = HelpMethods.crossover(selectedPopulation, pixels);
			Nsga2Operations.mutation(population, pixels);
			
			long endTime = System.nanoTime();
			long duration = endTime - startTime;
			System.out.println("Generation number: " + generations + ", current best chromosome fitness: " + HelpMethods.findBestChromosome(population).getFitnessValue() + " Duration: " + duration/Math.pow(10,9)+ " sec");
			generations++;
		}
		Chromosome best = HelpMethods.findBestChromosome(population);
		
		long totalEndtime = System.nanoTime();
		long duration = (long) ((totalEndtime - totalStartTime)/Math.pow(10, 9));
		HelpMethods.paintEdgesGreen(best);
		HelpMethods.drawImage(image, best.getSegments(), duration);
		best.paintGroundTruth();
		HelpMethods.drawImage(image, best.getSegments(), duration);
	}
	
	public ArrayList<ArrayList<Pixel>> getImage() {
		return this.image;
	}
	
	public ArrayList<Chromosome> getPopulation(){
		return this.population;
	}
	
	public ArrayList<Chromosome> generateTestImage() {
		ArrayList<Chromosome> pop = new ArrayList<Chromosome>();
		ArrayList<Pixel> representation = this.MST;
		ArrayList<Pixel> segment = pixels;
		HashMap<Pixel, ArrayList<Integer>> mapPixelsToIndex = HelpMethods.createMapPixelToIndex(representation);
		pop.add(new Chromosome((ArrayList<Pixel>) this.MST.clone() , pixels, 1, distances, image));
		
		ArrayList<Edge> edges = HelpMethods.generateEdgesWithMap(representation, segment, pixels, mapPixelsToIndex);
				
		int pixelToCut = HelpMethods.cutIntoTwoSegments(representation, pixels, mapPixelsToIndex, segment, 1000);
		ArrayList<Pixel> copy = (ArrayList<Pixel>) representation.clone();
		
		representation.set(pixelToCut, pixels.get(pixelToCut));
		Chromosome c = new Chromosome(representation, pixels, 1, distances, image);
		pop.add(c);
		pop.add(new Chromosome(copy, pixels, 1, distances, image));
		return pop;
	}
	
	
	
	public static void main(String[] args) throws IOException {
		String imagePath = "Test Image/pi.jpg";
		Program p = new Program(imagePath);
		p.init();
		p.run();
//		Chromosome chr1 = HelpMethods.initializeChromosome(80, p.MST, p.pixels, p.image, p.distances);
//		while (chr1.getSegments().size() > Variables.optimalNumberOfSegments){
//			Nsga2Operations.mutationMergeTestAllCombinations(chr1, p.pixels);
//		}
		
//		ArrayList<ArrayList<Pixel>> decoded = p.paintWithKmeans(8);
//		Chromosome chr = p.encode(decoded, 1);
//		int counter = 0;
//		while(chr.getSegments().size() > 400 && counter < 100){
//			System.out.println("Number of segments: " +chr.getSegments().size());
//			chr.removeSmallSegments(2000);
//			counter++;
//		}
//		while(chr.getSegments().size() > Variables.optimalNumberOfSegments){
//			Nsga2Operations.mutationMergeTestAllCombinations(chr, p.pixels);
//			System.out.println("SHIT" +chr.getSegments().size());
//		}
//		HelpMethods.paintEdgesGreen(chr1);
//		HelpMethods.drawImage(p.image);
//		p.init();
//		p.run();
//		HelpMethods.c
//		ArrayList<Chromosome> pop = p.generateTestImage();
//		Chromosome c = pop.get(1);
//		c.mergeSegments(c.getSegments().get(0), c.getSegments().get(1));
//		HelpMethods.paintEdgesGreen(chr1);
//		HelpMethods.drawImage(p.getImage());
//		p.run();

//		p.getPopulation().get(0).updateSegmentBorder();
//		HelpMethods.paintEdgesGreen(p.getPopulation().get(0));
//		HelpMethods.drawImage(p.getImage());

//		HelpMethods.paintEdgesGreen(p.getPopulation().get(0));
//		HelpMethods.drawImage(p.getImage());
//		p.paintWithKmeans(10);
//		HelpMethods.drawImage(p.getImage());
//		p.createMST();
		
//		
	}

}
