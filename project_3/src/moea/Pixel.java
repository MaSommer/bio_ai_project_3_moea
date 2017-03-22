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
	private int redPaint;
	private int greenPaint;
	private int bluePaint;

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
	
	public int getRed() {
		if(redPaint == red)
			return red;
		else{
			return redPaint;
		}
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
		if(greenPaint == green)
			return green;
		else{
			return greenPaint;
		}
	}
	
	public void clearPaint() {
		redPaint = red;
		greenPaint = green;
		bluePaint = blue;
	}

	public int getBlue() {
		if(bluePaint == blue)
			return blue;
		else{
			return bluePaint;
		}
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
		this.redPaint = 21;
		this.greenPaint = 250;
		this.bluePaint = 4;
	}
	
	public void setColor(int[] rgb){
		this.redPaint = rgb[0];
		this.greenPaint = rgb[1];
		this.bluePaint = rgb[2];
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
