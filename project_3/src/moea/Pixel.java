package moea;

import java.util.ArrayList;

public class Pixel {
	
	private int red;
	private int green;
	private int blue;
	private ArrayList<Pixel> neighbours;
	
	public Pixel(int red, int green, int blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
		neighbours = new ArrayList<Pixel>();
	}
	
	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}

	public ArrayList<Pixel> getNeighbours() {
		return neighbours;
	}
	
	public void addPixel(Pixel p){
		neighbours.add(p);
	}
	
	public void removePixel(Pixel p){
		neighbours.remove(p);
	}

}
