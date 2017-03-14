package moea;

import java.io.IOException;
import java.util.ArrayList;

public class Program {
	
	private ArrayList<ArrayList<Pixel>> population;
	
	public Program(){
		population = new ArrayList<ArrayList<Pixel>>();
		
	}
	
	public void init(){
		
	}
	
	
	
	public static void main(String[] args) throws IOException {
		Pixel[][] pixels = HelpMethods.createImagePixelByPixel("Test Image/1/Test image.jpg");
		System.out.println("h");
		HelpMethods.drawImage(pixels);
	}

}
