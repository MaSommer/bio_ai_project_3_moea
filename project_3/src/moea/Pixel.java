package moea;

import java.util.ArrayList;

public class Pixel {
	
	private int red;
	private int green;
	private int blue;
	private int alpha;
	private int id;
	private boolean hasAlphaChannel;
	
	//{west, north, east, south}
	private Pixel[] neighbours;
	
	public Pixel(int red, int green, int blue, int alpha, boolean hasAlphaChannel, int id){
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		this.id = id;
		this.hasAlphaChannel = hasAlphaChannel;
		neighbours = new Pixel[4];
	}
	
	public int getRed() {
		return red;
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

	public Pixel[] getNeighbours() {
		return neighbours;
	}
	
//	public void addNeighbour(Pixel p){
//		neighbours.add(p);
//	}
//	
//	public void removeNeighbour(Pixel p){
//		neighbours.remove(p);
//	}

}
