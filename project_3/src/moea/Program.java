package moea;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Program {
	private ArrayList<Pixel> pixels;
	private ArrayList<ArrayList<Pixel>> population;
	private ArrayList<ArrayList<Pixel>> image;
	private int maxSegments;
	private int minPixelsInSegment;
	private double mRate;
	private int elitesToNextGen;
	private int pSize;
	private String imagePath;
	
	
	
	public Program(String imagePath,int maxSegments, int minPixelsInSegment, double mRate, int elitesToNextGen, int pSize) {
		super();
		this.maxSegments = maxSegments;
		this.minPixelsInSegment = minPixelsInSegment;
		this.mRate = mRate;
		this.elitesToNextGen = elitesToNextGen;
		this.pSize = pSize;
		this.population = new ArrayList<ArrayList<Pixel>>();
		this.image = new ArrayList<ArrayList<Pixel>>();
		this.pixels = new ArrayList<Pixel>();
		this.imagePath = imagePath;
	}


	public void init() throws IOException{
		Pixel[][] pixels1 = HelpMethods.createImagePixelByPixel(imagePath);
		this.pixels = HelpMethods.generatePixelList(pixels1);
		this.image = HelpMethods.generateImage(pixels1);
	}
	
	
	
	public static void main(String[] args) throws IOException {
		String imagePath = "Test Image/1/Test image.jpg";
		int maxSegments = 10;
		int minPixelsInSegment = 10;
		double mRate = 0.5;
		int elitesToNextGen = 100;
		int pSize = 100;
		Program p = new Program(imagePath, maxSegments, minPixelsInSegment, mRate, elitesToNextGen, pSize);
		p.init();
	}

}
