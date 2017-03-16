package moea;

import java.io.IOException;
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
		long startTime = System.nanoTime();
		Pixel[][] pixels1 = HelpMethods.createImagePixelByPixel(imagePath);
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		System.out.println(duration/Math.pow(10, 6) + " milli seconds");
		this.pixels = HelpMethods.generatePixelList(pixels1);
		//Refers to the pixel with id as same as the key
		this.pixelMap = HelpMethods.generatePixelMap(pixels);
		this.image = HelpMethods.generateImage(pixels1);
		ArrayList<Pixel> pixelsMST = HelpMethods.minimumSpanningTree2(pixels);
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
		
		
		this.population = HelpMethods.createPopulation(pixelsMST, pSize, pixels);
		HelpMethods.paintEdgesGreen(population.get(0));
		HelpMethods.drawImage(image);
	}
	
	
	
	public static void main(String[] args) throws IOException {
		String imagePath = "Test Image/pi.png";
		Program p = new Program(imagePath);
		p.init();
	}

}
