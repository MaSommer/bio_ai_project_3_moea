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
	
	public static double pixelDeviation(double[] centeroid, Pixel p2){
		double redDistance =centeroid[0]-p2.getRed();
		double greenDistance = centeroid[1] - p2.getGreen();
		double blueDistance = centeroid[2] - p2.getBlue();
		
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
	
	public static double[] segmentDeviation(ArrayList<Pixel> segment){
		double[] deviation = {0.0, 0.0, 0.0, 0.0};
		double red = 0;
		double green = 0;
		double blue = 0;
		for(Pixel p:segment){
			red+=p.getRed();
			blue+=p.getBlue();
			green+=p.getGreen();
		}
		deviation[1] = red/segment.size();
		deviation[2] = green/segment.size();
		deviation[3] = blue/segment.size();
		ArrayList<Double> rgbValues = new ArrayList<Double>();
		rgbValues.add(red/segment.size());
		rgbValues.add(green/segment.size());
		rgbValues.add(blue/segment.size());
		
		for(Pixel p:segment){
			deviation[0]+=pixelDeviation(rgbValues,p);
		}
		return deviation;
	}
	
	public static double[] averageRGB(ArrayList<Pixel> segment){
		double[] rgb = {0.0, 0.0, 0.0};
		for(Pixel p:segment){
			rgb[0]+=p.getRed();
			rgb[1]+=p.getGreen();
			rgb[2]+=p.getBlue();
		}
		rgb[0] = rgb[0]/segment.size();
		rgb[1] = rgb[1]/segment.size();
		rgb[1] = rgb[1]/segment.size();
		
		return rgb;
	}
	
	public static double rgbDistance(double[] rgb1, double[] rgb2){
		double r1 = rgb1[0];
		double r2 = rgb2[0];
		double g1 = rgb1[1];
		double g2 = rgb2[1];
		double b1 = rgb1[2];
		double b2 = rgb2[2];
		return Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
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
			if (pixelsToSegment.get(neighbour.getId()) != segmentNr){
				connectivityValue += (1/counter);
				counter++;				
			}
		}
		return connectivityValue;
	}
	
	public static ArrayList<Pixel> getEdge(ArrayList<Pixel> segment, int segmentNr, ArrayList<Integer> pixelToSegment){
		ArrayList<Pixel> edge = new ArrayList<Pixel>();
		for(Pixel p : segment){
			if (p.getNeighbours().size() <4){
				edge.add(p);
				continue;				
			}
			for(Pixel neighbour: p.getNeighbours()){
				if (pixelToSegment.get(neighbour.getId()) != segmentNr){
					edge.add(p);
					break;
				}
			}
		}
		return edge;
	}
	
	public static double segmentEdgeValue(ArrayList<Pixel> segmentEdges, ArrayList<Pixel> segment, int segmentNr, ArrayList<Integer> pixelsToSegment){
		double edgeValue = 0;
		for(Pixel p: segmentEdges){
			for(Pixel neighbour: p.getNeighbours()){
				if (pixelsToSegment.get(neighbour.getId()) == segmentNr){
					edgeValue += pixelToPixelDeviation(p, neighbour);					
				}
			}
		}
		return edgeValue;
	}
	
	
	

}
