package moea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Program {
	private ArrayList<Pixel> pixels;
	private ArrayList<Chromosome> population;
	private ArrayList<ArrayList<Pixel>> image;
	private HashMap<Integer, Pixel> pixelMap;
	private int pSize;
	private String imagePath;
	private ArrayList<Pixel> MST;
	private ArrayList<double[]> distances;
	
	
	
	
	public Program(String imagePath) {
		super();
		this.pSize = Variables.pSize;
		this.image = new ArrayList<ArrayList<Pixel>>();
		this.pixels = new ArrayList<Pixel>();
		this.imagePath = imagePath;

	} 
	
	public void setDistances() {
		ArrayList<double[]> distances = new ArrayList<double[]>();
		int eastBorder = image.get(0).size()-1;
		int southBorder = image.size()-1;
		System.out.println("Width: " + eastBorder);
		System.out.println("Height: " + southBorder);
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
					System.out.println("tar hjørne1");
					
				}
				else if((i == southBorder) && (j==0)){ //SOUTH WEST corner
					double[] dist = {-1,-1,-1,-1};
					dist[0] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i-1).get(j)); //NORTH
					dist[2] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i).get(j+1)); //EAST
					distances.add(dist);
					System.out.println("tar hjørne2");
				}
				
				else if((i == 0) && (j == eastBorder)){ //NORTH EAST Corner
					double[] dist = {-1,-1,-1,-1};
					dist[1] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i+1).get(j)); //SOUTH
					dist[3] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i).get(j-1));//WEST
					distances.add(dist);
					System.out.println("tar hjørne3");
				}
				else if((i == southBorder) && (j == eastBorder)){ //SOUTH EAST corner
					double[] dist = {-1,-1,-1,-1};
					dist[0] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i-1).get(j)); //NORTH
					dist[3] = Functions.pixelToPixelDeviation(image.get(i).get(j), image.get(i).get(j-1));//WEST
					distances.add(dist);
					System.out.println("tar hjørne4");
					
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
		this.pixelMap = HelpMethods.generatePixelMap(pixels);
		this.image = HelpMethods.generateImage(pixels1);

		setDistances();
		createMST();

//		for (int i = 0; i < pixelsMST.size(); i++) {
//			if (!pixelsMST.get(i).equals(pixelsMST1.get(i))){
//				throw new IllegalArgumentException("kukeri");
//			}
//		}
//		System.exit(0);
//		ArrayList<Pixel> pixelsMST = HelpMethods.minimumSpanningTree2(pixels);
//		FileAdministrator fa = new FileAdministrator("Test image1");
//		System.exit(0);
//		System.out.println(new Chromosome(pixelsMST, pixels, 0).getSegments().size());
//		int size = pixelsMST.size()-1;
//		int count = 0;
//		for (int i = 0; i < pixelsMST.size(); i++) {
//			Pixel p1 = pixelsMST.get(i);
//			Pixel p2 = pixels.get(i);
//			if (p1.getRed() != p2.getRed()){
//				count++;
//			}
//		}
//		System.out.println(count);
//		System.out.println("frst r: "+pixelsMST.get(0).getRed() + " g: "+pixelsMST.get(0).getGreen() + " b: " + pixelsMST.get(0).getBlue());
//		System.out.println("last r: "+pixelsMST.get(size).getRed() + " g: "+pixelsMST.get(size).getGreen() + " b: " + pixelsMST.get(size).getBlue());
		

		this.population = HelpMethods.createPopulation(this.MST, pSize, pixels, HelpMethods.createMapPixelToIndex(pixels));
	}
	
	public void run(){
		long startTime = System.nanoTime();
		int generations = 1;
		//do some mutations
		for (int i = 0; i < 200; i++) {
			for (Chromosome chromosome : population) {
				chromosome.mutate();
			}
		}
		for (int i = 0; i < Variables.numberOfGenerations; i++) {
			ArrayList<Chromosome> selectedPopulation = Nsga2Operations.selection(population);
			population = HelpMethods.crossover(selectedPopulation, pixels);
			HelpMethods.mutation(population);
			
			long endTime = System.nanoTime();
			long duration = endTime - startTime;
			System.out.println("Generation number: " + generations + ", current best chromosome fitness: " + HelpMethods.findBestChromosome(population).getFitnessValue() + " Duration: " + duration/Math.pow(10,9)+ " sec");
			if (generations % 50 == 0){
			}
			generations++;
		}
		HelpMethods.paintEdgesGreen(HelpMethods.findBestChromosome(population));
		HelpMethods.drawImage(image);
	}
	
	
	
	public static void main(String[] args) throws IOException {
		String imagePath = "Test Image/1/Test Image.jpg";
		Program p = new Program(imagePath);
		p.init();
		p.run();
	}

}
