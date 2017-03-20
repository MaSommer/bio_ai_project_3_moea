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
	
	public String toString(){
		return ""+id;
	}
	
//	public Pixel(Pixel another){
//		this.red = another.red;
//		this.green = another.green;
//		this.blue = another.blue;
//		this.alpha = another.alpha;
//		this.id = another.id;
//		this.hasAlphaChannel = another.hasAlphaChannel;
//		this.neighbours = new ArrayList<Pixel>();
//		for (Pixel neigh : another.neighbours) {
//			this.neighbours.add(new Pixel(neigh));
//		}
//		this.neighbourDistances = another.neighbourDistances;
//	}
	
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
		neighbourDistances.add(Functions.pixelToPixelDeviation(this, p));
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
	
//	public String toString(){
//		int number = this.id+1;
//		return ""+number;
//	}
//	
//	public static void main(String[] args) {
//		Pixel p = new Pixel(0,0,0,0,false,1);
//		Pixel p1 = new Pixel(0,0,0,0,false,2);
//		Pixel p2 = new Pixel(0,0,0,0,false,3);
//		Pixel p3 = new Pixel(0,0,0,0,false,4);
//		p.addNeighbour(p1);
//		p.addNeighbour(p2);
//		p.addNeighbour(p3);
//		p1.addNeighbour(p);
//		p1.addNeighbour(p2);
//		p1.addNeighbour(p3);
//		p2.addNeighbour(p);
//		p2.addNeighbour(p1);
//		p2.addNeighbour(p3);
//		p3.addNeighbour(p);
//		p3.addNeighbour(p1);
//		p3.addNeighbour(p2);
//		new Pixel(p1);
//		System.out.println(p.getId());
//	}
}
