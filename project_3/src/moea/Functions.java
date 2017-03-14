package moea;

import java.util.ArrayList;

public class Functions {
	
	public static double pixelDeviation(ArrayList<Double> centeroid, Pixel p2){
		double redDistance =centeroid.get(0)-p2.getRed();
		double greenDistance = centeroid.get(1) - p2.getGreen();
		double blueDistance = centeroid.get(2) - p2.getBlue();
		
		double notSquaredDev = Math.pow(redDistance, 2);
		notSquaredDev += Math.pow(greenDistance, 2);
		notSquaredDev += Math.pow(blueDistance, 2);
		return Math.pow(notSquaredDev, 0.5);
	}
	
	public static double pixelToPixelDeviation(Pixel p1, Pixel p2){
		double redDistance =p1.getRed()-p2.getRed();
		double greenDistance = p1.getGreen() - p2.getGreen();
		double blueDistance = p1.getBlue() - p2.getBlue();
		
		double notSquaredDev = Math.pow(redDistance, 2);
		notSquaredDev += Math.pow(greenDistance, 2);
		notSquaredDev += Math.pow(blueDistance, 2);
		return Math.pow(notSquaredDev, 0.5);
	}
	
	public static double segmentDeviation(ArrayList<Pixel> segment){
		double deviation = 0;
		double red = 0;
		double green = 0;
		double blue = 0;
		for(Pixel p:segment){
			red+=p.getRed();
			blue+=p.getBlue();
			green+=p.getGreen();
		}
		ArrayList<Double> rgbValues = new ArrayList<Double>();
		rgbValues.add(red/segment.size());
		rgbValues.add(green/segment.size());
		rgbValues.add(blue/segment.size());
		
		for(Pixel p:segment){
			deviation+=pixelDeviation(rgbValues,p);
		}
		
		return deviation;
	
	}
	
	public static double segmentConnectivityValue(ArrayList<Pixel> segment, Pixel p){
		int counter = 1;
		double connectivityValue = 0;
		for(Pixel neighbour: p.getNeighbours()){
			if(!segment.contains(neighbour)){
				connectivityValue+=1/counter;
				counter++;
			}
		}
		return connectivityValue;
	}
	
	public static ArrayList<Pixel> getEdge(ArrayList<Pixel> segment){
		ArrayList<Pixel> edge = new ArrayList<Pixel>();
		for(Pixel p : segment){
			for(Pixel neighbour: p.getNeighbours()){
				if(!segment.contains(neighbour)){
					edge.add(p);
					break;
				}
			}
		}
		return edge;
	}
	
	public static double segmentEdgeValue(ArrayList<Pixel> segment){
		double edgeValue = 0;
		ArrayList<Pixel> edges = getEdge(segment);
		for(Pixel p: edges){
			for(Pixel neighbour: p.getNeighbours()){
				if(!segment.contains(neighbour)){
					edgeValue-= pixelToPixelDeviation(p, neighbour);
				}
			}
		}
		
		
		return edgeValue;
	}
	
	
	

}
