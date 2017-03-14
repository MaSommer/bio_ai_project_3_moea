package moea;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Program {
	
	private ArrayList<ArrayList<Pixel>> population;
	private ArrayList<ArrayList<Pixel>> image;
	private HashMap<Double,Double> pixelDistances;
	private int maxSegments;
	private int minPixelsInSegment;
	private double mRate;
	private int elitesToNextGen;
	private int pSize;
	
	
	
	public Program(int maxSegments, int minPixelsInSegment, double mRate, int elitesToNextGen, int pSize) {
		super();
		this.maxSegments = maxSegments;
		this.minPixelsInSegment = minPixelsInSegment;
		this.mRate = mRate;
		this.elitesToNextGen = elitesToNextGen;
		this.pSize = pSize;
		this.population = new ArrayList<ArrayList<Pixel>>();
		this.image = new ArrayList<ArrayList<Pixel>>();
		this.pixelDistances = new HashMap<Double,Double>();
	}



	public void init(){
		
		
	}
	
	
	
	public static void main(String[] args) throws IOException {
		Pixel[][] pixels = HelpMethods.createImagePixelByPixel("Test Image/1/Test image.jpg");
		System.out.println("h");
		HelpMethods.drawImage(pixels);
	}

}
