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
		double redDistance = p1.getRed()-p2.getRed();
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
			deviation+=pixelDeviation(rgbValues,p)/Math.abs(Variables.maximumDeviationValue-Variables.minimumDeviationValue);
		}
		
		return deviation;
	
	}
	
	public static double segmentConnectivity(ArrayList<Pixel> segment, int segmentNr, ArrayList<Integer> pixelsToSegment){
		double connectivity = 0;
		for (Pixel pixel : segment) {
			connectivity += segmentConnectivityValue(pixel, segmentNr, pixelsToSegment);
		}
		return connectivity;
	}
	
	private static double segmentConnectivityValue(Pixel p, int segmentNr, ArrayList<Integer> pixelsToSegment){
		int counter = 1;
		double connectivityValue = 0;
		for(Pixel neighbour: p.getNeighbours()){
			if (pixelsToSegment.get(neighbour.getId()) == segmentNr){
				connectivityValue += (1/counter)/Math.abs(Variables.maximumConnectivityValue-Variables.minimumConnectivityValue);
				counter++;				
			}
		}
		return connectivityValue;
	}
	
	public static ArrayList<Pixel> getEdge(ArrayList<Pixel> segment, int segmentNr, ArrayList<Integer> pixelToSegment){
		ArrayList<Pixel> edge = new ArrayList<Pixel>();
		for(Pixel p : segment){
			for(Pixel neighbour: p.getNeighbours()){
				if (pixelToSegment.get(neighbour.getId()) != segmentNr){
					edge.add(p);
					break;
				}
			}
		}
//		System.out.println("getEdge: " + duration/Math.pow(10, 9) + " sec");
		return edge;
	}
	
	public static double segmentEdgeValue(ArrayList<Pixel> segmentEdges, ArrayList<Pixel> segment, int segmentNr, ArrayList<Integer> pixelsToSegment){
		double edgeValue = 0;
		double normalizeConstant = Math.abs(Variables.maximumEdgeValue-Variables.minimumEdgeValue);
		for(Pixel p: segmentEdges){
			for(Pixel neighbour: p.getNeighbours()){
				if (pixelsToSegment.get(neighbour.getId()) == segmentNr){
					edgeValue-= pixelToPixelDeviation(p, neighbour)/normalizeConstant;					
				}
//				if(!segment.contains(neighbour)){
//					edgeValue-= pixelToPixelDeviation(p, neighbour);
//				}
			}
		}
		return edgeValue;
	}
	
	
	

}
