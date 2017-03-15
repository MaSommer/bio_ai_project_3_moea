package moea;

import java.awt.Color;
import java.util.ArrayList;

public class Pixel {
	
	private int red;
	private int green;
	private int blue;
	private int alpha;
	private int id;
	private boolean hasAlphaChannel;

	private ArrayList<Pixel> neighbours;

	private ArrayList<Double> neighbourDistances;
	
	//{west, north, east, south}
	
	public Pixel(int red, int green, int blue, int alpha, boolean hasAlphaChannel, int id){
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		this.id = id;
		this.hasAlphaChannel = hasAlphaChannel;
		neighbours = new ArrayList<Pixel>();
		neighbourDistances = new ArrayList<Double>();
	}
	
	public int getRed() {
		return red;
	}
	
	public ArrayList<Pixel> getNeighbours() {
		return neighbours;
	}
	public int getId(){
		return id;
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

	
	public void addNeighbour(Pixel p){
		neighbours.add(p);
	}
	
	public void removeNeighbour(Pixel p){
		neighbours.remove(p);
	}
	
	public ArrayList<Double> getNeighbourDistances(){
		return neighbourDistances;
	}
	
	public double getDistance(Pixel neighbour){
		int index = neighbours.indexOf(neighbour);
		if (neighbour.equals(this)){
			return 0;
		}
		return neighbourDistances.get(index);
	}
	
	public void paintGreen(){
		this.red = 21;
		this.green = 250;
		this.blue = 4;
	}
}
