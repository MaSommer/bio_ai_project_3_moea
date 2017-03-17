package moea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Program {
	private ArrayList<Pixel> pixels;
	private ArrayList<Chromosome> population;
	private ArrayList<ArrayList<Pixel>> image;
	private HashMap<Integer, Pixel> pixelMap;
	private int maxSegments;
	private int minPixelsInSegment;
	private double mRate;
	private int elitesToNextGen;
	private int pSize;
	private String imagePath;
	
	
	
	
	public Program(String imagePath) {
		super();
		this.maxSegments = Variables.maxSegments;
		this.minPixelsInSegment = Variables.minPixelsInSegment;
		this.mRate = Variables.mRate;
		this.elitesToNextGen = Variables.elitesToNextGen;
		this.pSize = Variables.pSize;
		this.image = new ArrayList<ArrayList<Pixel>>();
		this.pixels = new ArrayList<Pixel>();
		this.imagePath = imagePath;
	}


	public void init() throws IOException{
		Pixel[][] pixels1 = HelpMethods.createImagePixelByPixel(imagePath);
		this.pixels = HelpMethods.generatePixelList(pixels1);
		//Refers to the pixel with id as same as the key
		this.pixelMap = HelpMethods.generatePixelMap(pixels);
		this.image = HelpMethods.generateImage(pixels1);
//
//		
//		BufferedReader br = new BufferedReader(new FileReader("palme.txt"));	
//		ArrayList<Pixel> pixelsMST1 = new ArrayList<Pixel>();
//		String line = "";
//		line = br.readLine();
//		System.out.println(line);
//		while((line = br.readLine()) != null){
//			pixelsMST1.add(pixels.get(Integer.parseInt(line)));
//			System.out.println("cock");
//		}
//		br.close();
		
//		ArrayList<Pixel> pixelsMST1 = fa.readMST(pixels, "palme.txt");
		ArrayList<Pixel> pixelsMST = HelpMethods.minimumSpanningTree2(pixels);
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
		
		
		this.population = HelpMethods.createPopulation(pixelsMST, pSize, pixels, HelpMethods.createMapPixelToIndex(pixels));
	}
	
	public void run(){
		long startTime = System.nanoTime();
		int generations = 1;
		//do some mutations
		for (int i = 0; i < 500; i++) {
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
		String imagePath = "Test Image/1/Test image.jpg";
		Program p = new Program(imagePath);
		p.init();
		p.run();
	}

}
