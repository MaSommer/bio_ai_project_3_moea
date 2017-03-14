package moea;

import java.util.ArrayList;

public class Pixel {
	
	private int red;
	private int green;
	private int blue;
	private int alpha;
	private boolean hasAlphaChannel;
	private ArrayList<Pixel> neighbours;
	private ArrayList<Double> neighbourDistances;
	
	public Pixel(int red, int green, int blue, int alpha, boolean hasAlphaChannel){
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		this.hasAlphaChannel = hasAlphaChannel;
		neighbours = new ArrayList<Pixel>();
		neighbourDistances = new ArrayList<Double>();
		for(Pixel neighbour: neighbours){
			neighbourDistances.add(Functions.pixelToPixelDeviation(this, neighbour));
		}
	}
	
	public int getRed() {
		return red;
	}
	
	public boolean hasAlphaChannel(){
		return hasAlphaChannel;
	}
	
	public int getAlpha() {
		return alpha;
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
	
	public void addNeighbour(Pixel p){
		neighbours.add(p);
	}
	
	public void removeNeighbour(Pixel p){
		neighbours.remove(p);
	}
	
	public double getDistance(Pixel neighbour){
		int index = neighbours.indexOf(neighbour);
		return neighbourDistances.get(index);
	}

}
